package net.cinderlabsmc.cinderlib.block.geo;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * GeckoLib settings of a block. Contains no GeckoLib types, so it is safe without GeckoLib at runtime.
 *
 * <p>Default asset paths for block {@code mod:name}:
 * <ul>
 *     <li>model: {@code assets/mod/geckolib/models/block/name.geo.json} (also used for the item unless {@link #itemModel(Identifier)} is set)</li>
 *     <li>animation: {@code assets/mod/geckolib/animations/block/name.animation.json} (only needed with controllers)</li>
 *     <li>texture: {@code assets/mod/textures/block/name.png}</li>
 * </ul>
 */
public final class CinderGeo {

    private Identifier model;
    private @Nullable Identifier itemModel;
    private Identifier texture;
    private Identifier animation;
    private @Nullable String idleAnimation;
    private boolean geoItem = true;

    private CinderGeo(Identifier model, Identifier texture, Identifier animation) {
        this.model = model;
        this.texture = texture;
        this.animation = animation;
    }

    public static @NonNull CinderGeo defaults(@NonNull Identifier blockId) {
        var path = blockId.withPrefix("block/");
        return new CinderGeo(path, blockId.withPath("textures/block/" + blockId.getPath() + ".png"), path);
    }

    /** Model id, e.g. {@code mod:block/rack_shelf} for {@code geckolib/models/block/rack_shelf.geo.json}. */
    public @NonNull CinderGeo model(@NonNull Identifier model) {
        this.model = model;
        return this;
    }

    /** Separate model for the block item; defaults to {@link #model(Identifier)}. */
    public @NonNull CinderGeo itemModel(@NonNull Identifier itemModel) {
        this.itemModel = itemModel;
        return this;
    }

    /** Full texture path, e.g. {@code mod:textures/block/rack_shelf.png}. */
    public @NonNull CinderGeo texture(@NonNull Identifier texture) {
        this.texture = texture;
        return this;
    }

    /** Animation file id, e.g. {@code mod:block/rack_shelf} for {@code geckolib/animations/block/rack_shelf.animation.json}. */
    public @NonNull CinderGeo animation(@NonNull Identifier animation) {
        this.animation = animation;
        return this;
    }

    /** Loops this animation (by name inside the animation file) without writing a controller. */
    public @NonNull CinderGeo idle(@NonNull String animationName) {
        this.idleAnimation = animationName;
        return this;
    }

    /** Uses a plain {@code BlockItem} instead of a GeckoLib-rendered item. */
    public @NonNull CinderGeo plainItem() {
        this.geoItem = false;
        return this;
    }

    public @NonNull Identifier model() {
        return model;
    }

    public @NonNull Identifier itemModel() {
        return itemModel != null ? itemModel : model;
    }

    public @NonNull Identifier texture() {
        return texture;
    }

    public @NonNull Identifier animation() {
        return animation;
    }

    public @Nullable String idleAnimation() {
        return idleAnimation;
    }

    public boolean hasGeoItem() {
        return geoItem;
    }
}
