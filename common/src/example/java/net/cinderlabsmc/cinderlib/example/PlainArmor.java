package net.cinderlabsmc.cinderlib.example;

import org.jspecify.annotations.NonNull;

import net.cinderlabsmc.cinderlib.armor.CinderArmor;
import net.cinderlabsmc.cinderlib.armor.CinderArmorMaterial;
import net.minecraft.sounds.SoundEvents;

public final class PlainArmor extends CinderArmor {

  private static final CinderArmorMaterial MATERIAL = CinderArmorMaterial.create().durability(25).defense(2, 5, 6, 2)
      .enchantability(12).equipSound(SoundEvents.ARMOR_EQUIP_IRON);

  public PlainArmor() {
    super("plain");
  }

  @Override
  public @NonNull CinderArmorMaterial material() {
    return MATERIAL;
  }
}
