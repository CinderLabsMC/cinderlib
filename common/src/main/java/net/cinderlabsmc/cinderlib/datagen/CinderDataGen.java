package net.cinderlabsmc.cinderlib.datagen;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.cinderlabsmc.cinderlib.block.CinderBlock;
import net.cinderlabsmc.cinderlib.block.CinderBlockType;
import net.cinderlabsmc.cinderlib.block.CinderRegistrar;
import net.minecraft.resources.Identifier;

/**
 * Loader independent data generator: collects assets and data in memory and
 * writes them as JSON, so it needs no
 * Fabric or NeoForge datagen API. Run it from a {@code main} method (see
 * {@link #run(Path)}).
 *
 * <pre>{@code
 * public static void main(String[] args) {
 *   CinderDataGen.create("mymod")
 *       .blocks(ModMod.REGISTRAR)
 *       .lang("itemGroup.mymod.main", "My Mod")
 *       .item("ruby")
 *       .shaped("ruby_block", "mymod:ruby_block", 1, new String[] { "RRR", "RRR", "RRR" }, Map.of('R', "mymod:ruby"))
 *       .run(Path.of("common/src/generated/resources"));
 * }
 * }</pre>
 * 
 * Output layout follows the vanilla resource pack format
 * ({@code assets/<mod>/...}, {@code data/<mod>/...}); the
 * {@code common} module already includes {@code src/generated/resources}.
 */
public final class CinderDataGen {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  private final String modId;
  private final Map<String, JsonElement> files = new TreeMap<>();
  private final Map<String, String> lang = new TreeMap<>();

  private CinderDataGen(String modId) {
    this.modId = modId;
  }

  public static @NonNull CinderDataGen create(@NonNull String modId) {
    return new CinderDataGen(modId);
  }

  public @NonNull String modId() {
    return modId;
  }

  public @NonNull Identifier id(@NonNull String path) {
    return Identifier.fromNamespaceAndPath(modId, path);
  }

  /** Lets a helper such as {@code CinderOre} add its files. */
  public @NonNull CinderDataGen apply(@NonNull Consumer<CinderDataGen> generator) {
    generator.accept(this);
    return this;
  }

  // ---- raw files ----

  /** Adds {@code assets/<mod>/<path>.json}. */
  public @NonNull CinderDataGen asset(@NonNull String path, @NonNull JsonElement json) {
    files.put("assets/" + modId + "/" + path + ".json", json);
    return this;
  }

  /** Adds {@code data/<mod>/<path>.json}. */
  public @NonNull CinderDataGen data(@NonNull String path, @NonNull JsonElement json) {
    files.put("data/" + modId + "/" + path + ".json", json);
    return this;
  }

  // ---- lang ----

  public @NonNull CinderDataGen lang(@NonNull String key, @NonNull String value) {
    lang.put(key, value);
    return this;
  }

  // ---- items ----

  /**
   * Flat generated item model from {@code textures/item/<name>.png} plus item
   * definition and English name.
   */
  public @NonNull CinderDataGen item(@NonNull String name) {
    return item(name, "item/" + name);
  }

  /**
   * Like {@link #item(String)} with an explicit texture path (relative to
   * {@code textures/}, no extension).
   */
  public @NonNull CinderDataGen item(@NonNull String name, @NonNull String texture) {
    var model = json();
    model.addProperty("parent", "minecraft:item/generated");
    model.add("textures", json("layer0", modId + ":" + texture));
    asset("models/item/" + name, model);
    itemDefinition(name, modId + ":item/" + name);
    return lang("item." + modId + "." + name, title(name));
  }

  // ---- blocks ----

  /**
   * Cube block with one texture ({@code textures/block/<name>.png}): block state,
   * block model, item definition,
   * self-drop loot table and English name.
   */
  public @NonNull CinderDataGen cubeBlock(@NonNull String name) {
    var model = json();
    model.addProperty("parent", "minecraft:block/cube_all");
    model.add("textures", json("all", modId + ":block/" + name));
    asset("models/block/" + name, model);

    var variants = json();
    variants.add("", json("model", modId + ":block/" + name));
    asset("blockstates/" + name, json("variants", variants));

    itemDefinition(name, modId + ":block/" + name);
    selfDrop(name);
    return lang("block." + modId + "." + name, title(name));
  }

  /** Drops the block itself. */
  public @NonNull CinderDataGen selfDrop(@NonNull String block) {
    var entry = json();
    entry.addProperty("type", "minecraft:item");
    entry.addProperty("name", modId + ":" + block);

    var pool = json();
    pool.addProperty("rolls", 1);
    pool.add("entries", array(entry));
    pool.add("conditions", array(json("condition", "minecraft:survives_explosion")));

    var table = json();
    table.addProperty("type", "minecraft:block");
    table.add("pools", array(pool));
    table.addProperty("random_sequence", modId + ":blocks/" + block);
    return data("loot_table/blocks/" + block, table);
  }

  /**
   * Names and, for blocks without GeckoLib, generates state, model, item and loot
   * of every block in the registrar.
   * GeckoLib blocks bring their own model and blockstate; they only get name and
   * loot.
   */
  public @NonNull CinderDataGen blocks(@NonNull CinderRegistrar registrar) {
    for (CinderBlockType<?> type : registrar.types()) {
      var name = type.id().getPath();
      if (type.geo() == null && type.properties().isEmpty() && type.facing() == CinderBlock.Facing.NONE) {
        cubeBlock(name);
      } else {
        selfDrop(name);
        lang("block." + modId + "." + name, title(name));
      }
    }
    return this;
  }

  // ---- recipes ----

  public @NonNull CinderDataGen shaped(@NonNull String name, @NonNull String result, int count,
      @NonNull String[] pattern, @NonNull Map<Character, String> keys) {
    var key = json();
    keys.forEach((symbol, item) -> key.addProperty(String.valueOf(symbol), item));

    var recipe = json();
    recipe.addProperty("type", "minecraft:crafting_shaped");
    recipe.add("pattern", array(Arrays.stream(pattern).map(JsonPrimitive::new).toArray(JsonElement[]::new)));
    recipe.add("key", key);
    recipe.add("result", result(result, count));
    return data("recipe/" + name, recipe);
  }

  public @NonNull CinderDataGen shapeless(@NonNull String name, @NonNull String result, int count,
      @NonNull String... ingredients) {
    var list = new JsonArray();
    for (var ingredient : ingredients) {
      list.add(ingredient);
    }

    var recipe = json();
    recipe.addProperty("type", "minecraft:crafting_shapeless");
    recipe.add("ingredients", list);
    recipe.add("result", result(result, count));
    return data("recipe/" + name, recipe);
  }

  // ---- tags ----

  /** Item tag {@code <mod>:<name>}; values may be item ids or {@code #tags}. */
  public @NonNull CinderDataGen itemTag(@NonNull String name, @NonNull String... values) {
    return tag("item", name, values);
  }

  public @NonNull CinderDataGen blockTag(@NonNull String name, @NonNull String... values) {
    return tag("block", name, values);
  }

  private CinderDataGen tag(String kind, String name, String[] values) {
    var list = new JsonArray();
    for (var value : values) {
      list.add(value);
    }
    return data("tags/" + kind + "/" + name, json("values", list));
  }

  // ---- output ----

  /**
   * Writes everything below {@code output}, replacing existing generated files.
   */
  public void run(@NonNull Path output) {
    if (!lang.isEmpty()) {
      var json = json();
      lang.forEach(json::addProperty);
      asset("lang/en_us", json);
    }

    try {
      for (var file : files.entrySet()) {
        var path = output.resolve(file.getKey());
        Files.createDirectories(path.getParent());
        Files.writeString(path, GSON.toJson(file.getValue()) + "\n");
      }
    } catch (IOException e) {
      throw new UncheckedIOException("Data generation failed", e);
    }
  }

  // ---- helpers ----

  private void itemDefinition(String name, String model) {
    var inner = json();
    inner.addProperty("type", "minecraft:model");
    inner.addProperty("model", model);
    asset("items/" + name, json("model", inner));
  }

  private static JsonObject result(String item, int count) {
    var result = json();
    result.addProperty("id", item);
    result.addProperty("count", count);
    return result;
  }

  /** {@code "ruby_block"} to {@code "Ruby Block"}. */
  public static @NonNull String title(@NonNull String path) {
    var builder = new StringBuilder();
    for (var word : path.split("_")) {
      if (word.isEmpty())
        continue;
      if (!builder.isEmpty())
        builder.append(' ');
      builder.append(word.substring(0, 1).toUpperCase(Locale.ROOT)).append(word.substring(1));
    }
    return builder.toString();
  }

  public static @NonNull JsonObject json() {
    return new JsonObject();
  }

  public static @NonNull JsonObject json(@NonNull String key, @NonNull String value) {
    var json = new JsonObject();
    json.addProperty(key, value);
    return json;
  }

  public static @NonNull JsonObject json(@NonNull String key, @NonNull JsonElement value) {
    var json = new JsonObject();
    json.add(key, value);
    return json;
  }

  public static @NonNull JsonArray array(@NonNull JsonElement... elements) {
    var array = new JsonArray();
    for (var element : elements) {
      array.add(element);
    }
    return array;
  }
}
