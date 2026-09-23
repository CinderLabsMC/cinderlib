package net.cinderlabsmc.cinderlib.block.geo;

import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;

import net.cinderlabsmc.cinderlib.block.geo.client.CinderGeoClient;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

/**
 * Block item rendered with the block's GeckoLib model. Needs
 * {@code assets/<mod>/items/<name>.json}:
 * 
 * <pre>{@code
 * {"model": {"type": "minecraft:special", "base": "<mod>:block/<name>", "model": {"type": "geckolib:geckolib"}}}
 * }</pre>
 */
public class CinderGeoBlockItem extends BlockItem implements GeoItem {

  private final CinderGeo geo;
  private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

  public CinderGeoBlockItem(@NonNull Block block, @NonNull Properties properties, @NonNull CinderGeo geo) {
    super(block, properties);
    this.geo = geo;
  }

  public @NonNull CinderGeo geo() {
    return geo;
  }

  @Override
  public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar controllers) {
    if (geo.idleAnimation() != null) {
      var idle = RawAnimation.begin().thenLoop(geo.idleAnimation());
      controllers.add(new AnimationController<CinderGeoBlockItem>("idle", test -> test.setAndContinue(idle)));
    }
  }

  @Override
  public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
    return geoCache;
  }

  @Override
  public void createGeoRenderer(@NonNull Consumer<GeoRenderProvider> consumer) {
    consumer.accept(new GeoRenderProvider() {
      private @Nullable GeoItemRenderer<?> renderer;

      @Override
      public @NonNull GeoItemRenderer<?> getGeoItemRenderer() {
        if (renderer == null) {
          renderer = CinderGeoClient.createItemRenderer(CinderGeoBlockItem.this);
        }
        return renderer;
      }
    });
  }
}
