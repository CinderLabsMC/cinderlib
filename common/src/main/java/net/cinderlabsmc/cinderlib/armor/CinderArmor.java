package net.cinderlabsmc.cinderlib.armor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import dev.architectury.registry.registries.RegistrySupplier;
import net.cinderlabsmc.cinderlib.block.CinderRegistrar;
import net.cinderlabsmc.cinderlib.block.geo.CinderBones;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

/**
 * A GeckoLib armor set (helmet, chestplate, leggings, boots): one subclass per
 * set, every setting is an overridable
 * method. Contains no GeckoLib types, so it is safe without GeckoLib at
 * runtime.
 *
 * <pre>{@code
 * public final class RunicArmor extends CinderArmor {
 *   public RunicArmor() {
 *     super("runic");
 *   }
 *
 *   public CinderArmorMaterial material() {
 *     return MATERIAL;
 *   }
 * 
 *   public String idleAnimation(EquipmentSlot slot) {
 *     return "animation.runic.idle";
 *   }
 * 
 *   public boolean translucent() {
 *     return true;
 *   }
 * }
 *
 * public static final RunicArmor RUNIC = CinderArmor.register(REGISTRAR, new RunicArmor());
 * }</pre>
 *
 * <p>
 * Default asset paths for set {@code mod:name}:
 * <ul>
 * <li>model: {@code assets/mod/geckolib/models/armor/name.geo.json}</li>
 * <li>animation:
 * {@code assets/mod/geckolib/animations/armor/name.animation.json}</li>
 * <li>texture: {@code assets/mod/textures/armor/name.png}</li>
 * </ul>
 * Default bone names are {@code armorHead}, {@code armorBody},
 * {@code armorLeftArm}, {@code armorRightArm},
 * {@code armorLeftLeg}, {@code armorRightLeg}, {@code armorLeftBoot} and
 * {@code armorRightBoot}.
 * The rendering hooks are called on the client every frame, so keep them cheap.
 */
public abstract class CinderArmor {

  /** Body parts of an armor model; each one maps to a bone in the geo model. */
  public enum Part {
    HEAD("armorHead"),
    BODY("armorBody"),
    LEFT_ARM("armorLeftArm"),
    RIGHT_ARM("armorRightArm"),
    LEFT_LEG("armorLeftLeg"),
    RIGHT_LEG("armorRightLeg"),
    LEFT_FOOT("armorLeftBoot"),
    RIGHT_FOOT("armorRightBoot");

    private final String defaultBone;

    Part(String defaultBone) {
      this.defaultBone = defaultBone;
    }

    public @NonNull String defaultBone() {
      return defaultBone;
    }
  }

  private static final EquipmentSlot[] SLOTS = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
      EquipmentSlot.FEET };

  private final String name;
  private @Nullable Identifier id;
  private @Nullable ArmorMaterial armorMaterial;
  private final Map<EquipmentSlot, RegistrySupplier<Item>> items = new EnumMap<>(EquipmentSlot.class);

  /**
   * @param name registry name of the set; pieces are {@code name_helmet},
   *             {@code name_chestplate}, {@code name_leggings},
   *             {@code name_boots}
   */
  protected CinderArmor(@NonNull String name) {
    this.name = name;
  }

  /**
   * Registers all pieces of the set. Call before
   * {@link CinderRegistrar#register()}.
   */
  public static <A extends CinderArmor> @NonNull A register(@NonNull CinderRegistrar registrar, @NonNull A armor) {
    ((CinderArmor) armor).registerPieces(registrar);
    return armor;
  }

  private void registerPieces(CinderRegistrar registrar) {
    if (id != null) {
      throw new IllegalStateException("CinderArmor " + id + " is already registered");
    }
    var setId = registrar.id(name);
    id = setId;

    for (var slot : SLOTS) {
      if (!slots().contains(slot))
        continue;

      var type = typeOf(slot);
      var itemId = setId.withSuffix("_" + type.getName());
      items.put(slot, registrar.items().register(itemId, () -> createItem(type,
          properties(slot, new Item.Properties()).setId(ResourceKey.create(Registries.ITEM, itemId)))));
    }
  }

  // ---- settings ----

  /** The armor material (defense, durability, ...). */
  public abstract @NonNull CinderArmorMaterial material();

  /** The pieces this set consists of. Default: all four. */
  public @NonNull Set<EquipmentSlot> slots() {
    return Set.of(SLOTS);
  }

  /**
   * Asset suffix of a slot: {@code "legs"} makes {@code name_legs} the model and
   * texture of that slot. {@code null}
   * (default) uses the shared {@code name} assets. Override {@link #model} or
   * {@link #texture} for full control.
   */
  public @Nullable String variant(@NonNull EquipmentSlot slot) {
    return null;
  }

  /**
   * Model id, e.g. {@code mod:armor/runic} for
   * {@code geckolib/models/armor/runic.geo.json}.
   */
  public @NonNull Identifier model(@NonNull EquipmentSlot slot) {
    return id().withPrefix("armor/").withSuffix(suffix(slot));
  }

  /** Full texture path, e.g. {@code mod:textures/armor/runic.png}. */
  public @NonNull Identifier texture(@NonNull EquipmentSlot slot) {
    return id().withPath("textures/armor/" + name + suffix(slot) + ".png");
  }

  /**
   * Animation file id, e.g. {@code mod:armor/runic} for
   * {@code geckolib/animations/armor/runic.animation.json}.
   */
  public @NonNull Identifier animation() {
    return id().withPrefix("armor/");
  }

  /**
   * Animation (by name inside the animation file) looped on the piece of this
   * slot, or {@code null} for none.
   */
  public @Nullable String idleAnimation(@NonNull EquipmentSlot slot) {
    return null;
  }

  /** Parts rendered for the slot; vanilla layout by default. */
  public @NonNull List<Part> parts(@NonNull EquipmentSlot slot) {
    return switch (slot) {
      case HEAD -> List.of(Part.HEAD);
      case CHEST -> List.of(Part.BODY, Part.LEFT_ARM, Part.RIGHT_ARM);
      case LEGS -> List.of(Part.LEFT_LEG, Part.RIGHT_LEG);
      case FEET -> List.of(Part.LEFT_FOOT, Part.RIGHT_FOOT);
      default -> List.of();
    };
  }

  /** Bone name of a part. */
  public @NonNull String bone(@NonNull Part part) {
    return part.defaultBone();
  }

  public float scaleWidth() {
    return 1;
  }

  public float scaleHeight() {
    return 1;
  }

  /** Renders with an alpha-blended render type, for translucent textures. */
  public boolean translucent() {
    return false;
  }

  /**
   * ARGB tint of a stack; empty for the vanilla default (white, multiplied by
   * dye).
   */
  public @NonNull OptionalInt tint(@NonNull ItemStack stack) {
    return OptionalInt.empty();
  }

  /**
   * Hides, shows or scales bones per wearer on the client; the stack lets you
   * read data components.
   */
  public void adjustBones(@NonNull ItemStack stack, @NonNull EquipmentSlot slot, @NonNull CinderBones bones) {
  }

  /** Adjusts the item properties of one piece. */
  public Item.@NonNull Properties properties(@NonNull EquipmentSlot slot, Item.@NonNull Properties properties) {
    return properties;
  }

  /**
   * Creates the item of one piece; return a {@link CinderArmorItem} subclass to
   * add controllers or behaviour.
   */
  public @NonNull Item createItem(@NonNull ArmorType type, Item.@NonNull Properties properties) {
    return new CinderArmorItem(this, type, properties);
  }

  // ---- access ----

  public final @NonNull Identifier id() {
    if (id == null) {
      throw new IllegalStateException("CinderArmor " + name + " is not registered, use CinderArmor.register(...)");
    }
    return id;
  }

  /** The vanilla material built from {@link #material()}, created once. */
  public final @NonNull ArmorMaterial armorMaterial() {
    if (armorMaterial == null) {
      armorMaterial = material().build(id());
    }
    return armorMaterial;
  }

  /**
   * The registered item of a slot, or {@code null} if the piece is not part of
   * the set.
   */
  public final @Nullable RegistrySupplier<Item> item(@NonNull EquipmentSlot slot) {
    return items.get(slot);
  }

  public final @NonNull Item helmet() {
    return require(EquipmentSlot.HEAD);
  }

  public final @NonNull Item chestplate() {
    return require(EquipmentSlot.CHEST);
  }

  public final @NonNull Item leggings() {
    return require(EquipmentSlot.LEGS);
  }

  public final @NonNull Item boots() {
    return require(EquipmentSlot.FEET);
  }

  private String suffix(EquipmentSlot slot) {
    var variant = variant(slot);
    return variant != null ? "_" + variant : "";
  }

  private Item require(EquipmentSlot slot) {
    var item = items.get(slot);
    if (item == null) {
      throw new IllegalStateException("CinderArmor " + name + " has no piece for " + slot);
    }
    return item.get();
  }

  private static ArmorType typeOf(EquipmentSlot slot) {
    return switch (slot) {
      case HEAD -> ArmorType.HELMET;
      case CHEST -> ArmorType.CHESTPLATE;
      case LEGS -> ArmorType.LEGGINGS;
      default -> ArmorType.BOOTS;
    };
  }
}
