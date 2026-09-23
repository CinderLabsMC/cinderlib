package net.cinderlabsmc.cinderlib.worldgen;

import static net.cinderlabsmc.cinderlib.datagen.CinderDataGen.array;
import static net.cinderlabsmc.cinderlib.datagen.CinderDataGen.json;

import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.cinderlabsmc.cinderlib.datagen.CinderDataGen;

/**
 * Ore vein definition that generates the worldgen data files: configured
 * feature, placed feature and a NeoForge
 * biome modifier. Fabric has no data driven biome modifiers, so add the placed
 * feature there in code with
 * {@code BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.UNDERGROUND_ORES, key)}.
 *
 * <pre>{@code
 * CinderDataGen.create("mymod")
 *     .apply(CinderOre.of("ruby_ore").block("mymod:ruby_ore").deepslate("mymod:deepslate_ruby_ore")
 *         .size(6).count(4).height(-64, 16));
 * }</pre>
 */
public final class CinderOre implements Consumer<CinderDataGen> {

  private final String name;
  private String block;
  private String deepslateBlock;
  private int size = 9;
  private int count = 8;
  private int minY = -64;
  private int maxY = 64;
  private float discardChanceOnAirExposure;
  private String biomes = "#minecraft:is_overworld";

  private CinderOre(String name) {
    this.name = name;
  }

  /** @param name file name of the feature, usually the ore block name */
  public static @NonNull CinderOre of(@NonNull String name) {
    return new CinderOre(name);
  }

  /** Ore block placed in stone, as {@code mod:block}. */
  public @NonNull CinderOre block(@NonNull String block) {
    this.block = block;
    return this;
  }

  /** Variant placed in deepslate, as {@code mod:block}. */
  public @NonNull CinderOre deepslate(@NonNull String block) {
    this.deepslateBlock = block;
    return this;
  }

  /** Maximum vein size. */
  public @NonNull CinderOre size(int size) {
    this.size = size;
    return this;
  }

  /** Veins per chunk. */
  public @NonNull CinderOre count(int count) {
    this.count = count;
    return this;
  }

  /** Uniform absolute height range. */
  public @NonNull CinderOre height(int minY, int maxY) {
    this.minY = minY;
    this.maxY = maxY;
    return this;
  }

  /**
   * Chance (0 to 1) that an ore block touching air is skipped, like vanilla
   * diamonds.
   */
  public @NonNull CinderOre hideFromAir(float chance) {
    this.discardChanceOnAirExposure = chance;
    return this;
  }

  /**
   * Biome id or {@code #tag} that gets the ore, default
   * {@code #minecraft:is_overworld}.
   */
  public @NonNull CinderOre biomes(@NonNull String biomes) {
    this.biomes = biomes;
    return this;
  }

  @Override
  public void accept(@NonNull CinderDataGen data) {
    if (block == null) {
      throw new IllegalStateException("CinderOre " + name + " has no block");
    }
    var feature = data.modId() + ":" + name;

    var targets = new JsonArray();
    targets.add(target("minecraft:stone_ore_replaceables", block));
    if (deepslateBlock != null) {
      targets.add(target("minecraft:deepslate_ore_replaceables", deepslateBlock));
    }

    var config = json();
    config.addProperty("size", size);
    config.addProperty("discard_chance_on_air_exposure", discardChanceOnAirExposure);
    config.add("targets", targets);

    var configured = json();
    configured.addProperty("type", "minecraft:ore");
    configured.add("config", config);
    data.data("worldgen/configured_feature/" + name, configured);

    var height = json();
    height.addProperty("type", "minecraft:uniform");
    height.add("min_inclusive", absolute(minY));
    height.add("max_inclusive", absolute(maxY));

    var placed = json();
    placed.addProperty("feature", feature);
    placed.add("placement", array(
        placement("minecraft:count", "count", count),
        json("type", "minecraft:in_square"),
        heightRange(height),
        json("type", "minecraft:biome")));
    data.data("worldgen/placed_feature/" + name, placed);

    var modifier = json();
    modifier.addProperty("type", "neoforge:add_features");
    modifier.addProperty("biomes", biomes);
    modifier.addProperty("features", feature);
    modifier.addProperty("step", "underground_ores");
    data.data("neoforge/biome_modifier/" + name, modifier);
  }

  private static JsonObject target(String tag, String state) {
    var predicate = json();
    predicate.addProperty("predicate_type", "minecraft:tag_match");
    predicate.addProperty("tag", tag);

    var target = json();
    target.add("target", predicate);
    target.add("state", json("Name", state));
    return target;
  }

  private static JsonObject placement(String type, String key, int value) {
    var json = json();
    json.addProperty("type", type);
    json.addProperty(key, value);
    return json;
  }

  private static JsonObject heightRange(JsonObject height) {
    var json = json();
    json.addProperty("type", "minecraft:height_range");
    json.add("height", height);
    return json;
  }

  private static JsonObject absolute(int y) {
    var json = json();
    json.addProperty("absolute", y);
    return json;
  }
}
