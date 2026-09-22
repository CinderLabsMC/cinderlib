package net.cinderlabsmc.cinderlib.block.geo;

import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import net.cinderlabsmc.cinderlib.block.CinderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

/**
 * {@link CinderBlockEntity} rendered by GeckoLib. Blocks registered with {@code .geo()} need a block entity extending
 * this class (or get this class itself if they declare no block entity).
 *
 * <p>Client-side hooks: {@link #registerControllers}, {@link #adjustBones}, {@link #getGeoModel()},
 * {@link #getGeoTexture()}, {@link #getGeoAnimation()}. Data they read must be synced ({@link #sync()}).
 */
public class CinderGeoBlockEntity extends CinderBlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public CinderGeoBlockEntity(@NonNull BlockEntityType<?> type, @NonNull BlockPos pos, @NonNull BlockState state) {
        super(type, pos, state);
    }

    /** Default: loops {@link CinderGeo#idle(String)} if set. Override to add your own controllers. */
    @Override
    public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar controllers) {
        var geo = geo();
        if (geo != null && geo.idleAnimation() != null) {
            var idle = RawAnimation.begin().thenLoop(geo.idleAnimation());
            controllers.add(new AnimationController<CinderGeoBlockEntity>("idle", test -> test.setAndContinue(idle)));
        }
    }

    /** Called on the client each frame before rendering; hide or scale bones here. */
    public void adjustBones(@NonNull CinderBones bones) {
    }

    public @NonNull Identifier getGeoModel() {
        return requireGeo().model();
    }

    public @NonNull Identifier getGeoTexture() {
        return requireGeo().texture();
    }

    public @NonNull Identifier getGeoAnimation() {
        return requireGeo().animation();
    }

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    private CinderGeo requireGeo() {
        var geo = geo();
        if (geo == null) {
            throw new IllegalStateException("Block " + blockType().id() + " uses a CinderGeoBlockEntity but was not registered with .geo()");
        }
        return geo;
    }
}
