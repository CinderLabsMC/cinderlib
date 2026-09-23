package net.cinderlabsmc.cinderlib.example;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import net.cinderlabsmc.cinderlib.armor.CinderArmor;
import net.cinderlabsmc.cinderlib.armor.CinderArmorMaterial;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

public final class EmberArmor extends CinderArmor {

  private static final CinderArmorMaterial MATERIAL = CinderArmorMaterial.create()
      .durability(37)
      .defense(3, 6, 8, 3)
      .enchantability(15)
      .toughness(1f)
      .equipSound(SoundEvents.ARMOR_EQUIP_NETHERITE);

  public EmberArmor() {
    super("ember");
  }

  @Override
  public @NonNull CinderArmorMaterial material() {
    return MATERIAL;
  }

  @Override
  public @Nullable String variant(@NonNull EquipmentSlot slot) {
    return slot == EquipmentSlot.LEGS ? "legs" : null;
  }

  @Override
  public @Nullable String idleAnimation(@NonNull EquipmentSlot slot) {
    return slot == EquipmentSlot.HEAD ? "animation.ember.helmet_glow" : "animation.ember.idle";
  }

  @Override
  public float scaleWidth() {
    return 1.05f;
  }

  @Override
  public float scaleHeight() {
    return 1.05f;
  }

  @Override
  public Item.@NonNull Properties properties(@NonNull EquipmentSlot slot, Item.@NonNull Properties properties) {
    return properties.fireResistant();
  }
}
