package net.cinderlabsmc.cinderlib.armor;

import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

/**
 * Fluent replacement for the {@link ArmorMaterial} constructor, returned from
 * {@link CinderArmor#material()}.
 * The equipment asset defaults to the id of the armor set
 * ({@code assets/mod/equipment/name.json}).
 *
 * <pre>{@code
 * CinderArmorMaterial.create().durability(37).defense(3, 6, 8, 3).enchantability(15).toughness(1)
 * }</pre>
 */
public final class CinderArmorMaterial {

  private int durability = 15;
  private int helmet;
  private int chestplate;
  private int leggings;
  private int boots;
  private int enchantability = 9;
  private Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_GENERIC;
  private float toughness;
  private float knockbackResistance;
  private TagKey<Item> repairTag = ItemTags.REPAIRS_IRON_ARMOR;
  private @Nullable Identifier assetId;

  private CinderArmorMaterial() {
  }

  public static @NonNull CinderArmorMaterial create() {
    return new CinderArmorMaterial();
  }

  /**
   * Base durability; multiplied per piece by vanilla (helmet 11, chestplate 16,
   * leggings 15, boots 13).
   */
  public @NonNull CinderArmorMaterial durability(int durability) {
    this.durability = durability;
    return this;
  }

  public @NonNull CinderArmorMaterial defense(int helmet, int chestplate, int leggings, int boots) {
    this.helmet = helmet;
    this.chestplate = chestplate;
    this.leggings = leggings;
    this.boots = boots;
    return this;
  }

  public @NonNull CinderArmorMaterial enchantability(int enchantability) {
    this.enchantability = enchantability;
    return this;
  }

  public @NonNull CinderArmorMaterial equipSound(@NonNull Holder<SoundEvent> sound) {
    this.equipSound = sound;
    return this;
  }

  public @NonNull CinderArmorMaterial toughness(float toughness) {
    this.toughness = toughness;
    return this;
  }

  public @NonNull CinderArmorMaterial knockbackResistance(float knockbackResistance) {
    this.knockbackResistance = knockbackResistance;
    return this;
  }

  /** Items tag that repairs the armor in an anvil. */
  public @NonNull CinderArmorMaterial repairedBy(@NonNull TagKey<Item> tag) {
    this.repairTag = tag;
    return this;
  }

  /**
   * Equipment asset id, e.g. {@code mod:ember} for
   * {@code assets/mod/equipment/ember.json}. Defaults to the set id.
   */
  public @NonNull CinderArmorMaterial asset(@NonNull Identifier assetId) {
    this.assetId = assetId;
    return this;
  }

  @NonNull
  ArmorMaterial build(@NonNull Identifier setId) {
    ResourceKey<EquipmentAsset> asset = ResourceKey.create(EquipmentAssets.ROOT_ID, assetId != null ? assetId : setId);
    return new ArmorMaterial(durability,
        Map.of(ArmorType.HELMET, helmet, ArmorType.CHESTPLATE, chestplate, ArmorType.LEGGINGS, leggings,
            ArmorType.BOOTS, boots),
        enchantability, equipSound, toughness, knockbackResistance, repairTag, asset);
  }
}
