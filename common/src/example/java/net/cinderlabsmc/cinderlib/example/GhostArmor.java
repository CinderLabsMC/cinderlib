package net.cinderlabsmc.cinderlib.example;

import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

import org.jspecify.annotations.NonNull;

import net.cinderlabsmc.cinderlib.armor.CinderArmor;
import net.cinderlabsmc.cinderlib.armor.CinderArmorMaterial;
import net.cinderlabsmc.cinderlib.block.geo.CinderBones;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public final class GhostArmor extends CinderArmor {

  private static final CinderArmorMaterial MATERIAL = CinderArmorMaterial.create().durability(15).defense(1, 4, 3, 1)
      .enchantability(20)
      .repairedBy(ItemTags.REPAIRS_LEATHER_ARMOR);

  public GhostArmor() {
    super("ghost");
  }

  @Override
  public @NonNull CinderArmorMaterial material() {
    return MATERIAL;
  }

  @Override
  public @NonNull Set<EquipmentSlot> slots() {
    return Set.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST);
  }

  @Override
  public @NonNull List<Part> parts(@NonNull EquipmentSlot slot) {
    return slot == EquipmentSlot.CHEST ? List.of(Part.BODY) : super.parts(slot);
  }

  @Override
  public @NonNull String bone(@NonNull Part part) {
    return switch (part) {
      case HEAD -> "hood";
      case BODY -> "robe";
      default -> super.bone(part);
    };
  }

  @Override
  public boolean translucent() {
    return true;
  }

  @Override
  public @NonNull OptionalInt tint(@NonNull ItemStack stack) {
    return OptionalInt.of(stack.isDamaged() ? 0x99FFAAAA : 0xCCFFFFFF);
  }

  @Override
  public void adjustBones(@NonNull ItemStack stack, @NonNull EquipmentSlot slot, @NonNull CinderBones bones) {
    if (stack.isDamaged())
      bones.hide("halo");
    if (slot == EquipmentSlot.HEAD)
      bones.scale("hood", 1.1f, 1.1f, 1.1f);
  }
}
