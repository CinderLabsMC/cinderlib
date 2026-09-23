package net.cinderlabsmc.cinderlib.armor;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.util.GeckoLibUtil;
import com.google.common.base.Suppliers;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;

/**
 * Armor piece of a {@link CinderArmor} rendered by GeckoLib. Extend it and
 * return it from
 * {@link CinderArmor#createItem} to add your own controllers or behaviour.
 */
public class CinderArmorItem extends Item implements GeoItem {

  private final CinderArmor armor;
  private final ArmorType type;
  private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

  public CinderArmorItem(@NonNull CinderArmor armor, @NonNull ArmorType type, @NonNull Properties properties) {
    super(properties.humanoidArmor(armor.armorMaterial(), type));
    this.armor = armor;
    this.type = type;
  }

  public @NonNull CinderArmor armor() {
    return armor;
  }

  public @NonNull EquipmentSlot slot() {
    return type.getSlot();
  }

  /**
   * Default: loops the idle animation of this piece's slot if set. Override to
   * add your own controllers.
   */
  @Override
  public void registerControllers(@NonNull ControllerRegistrar controllers) {
    var idleName = armor.idleAnimation(slot());
    if (idleName != null) {
      var idle = RawAnimation.begin().thenLoop(idleName);
      controllers.add(new AnimationController<CinderArmorItem>("idle", test -> test.setAndContinue(idle)));
    }
  }

  @Override
  public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
    return geoCache;
  }

  @Override
  public void createGeoRenderer(@NonNull Consumer<GeoRenderProvider> consumer) {
    consumer.accept(new GeoRenderProvider() {
      private final Supplier<CinderArmorRenderer<?>> renderer = Suppliers
          .memoize(() -> new CinderArmorRenderer<>(CinderArmorItem.this));

      @Override
      public @Nullable GeoArmorRenderer<?, ?> getGeoArmorRenderer(@Nullable ItemStack itemStack,
          @NonNull EquipmentSlot equipmentSlot) {
        return renderer.get();
      }
    });
  }
}
