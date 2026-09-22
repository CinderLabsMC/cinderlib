package net.cinderlabsmc.cinderlib.block.geo.client;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeoBlockEntity;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

/**
 * Generic GeckoLib model for all CinderLib blocks and items. Model and texture can be overridden per instance
 * through the render state ({@link #MODEL}, {@link #TEXTURE}).
 */
public class CinderGeoModel<T extends GeoAnimatable> extends GeoModel<T> {

    public static final DataTicket<Identifier> MODEL = DataTicket.create("cinderlib_geo_model", Identifier.class);
    public static final DataTicket<Identifier> TEXTURE = DataTicket.create("cinderlib_geo_texture", Identifier.class);

    private final Identifier model;
    private final Identifier texture;
    private final Identifier animation;

    public CinderGeoModel(@NonNull Identifier model, @NonNull Identifier texture, @NonNull Identifier animation) {
        this.model = model;
        this.texture = texture;
        this.animation = animation;
    }

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
        return renderState.getOrDefaultGeckolibData(MODEL, model);
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
        return renderState.getOrDefaultGeckolibData(TEXTURE, texture);
    }

    @Override
    public @NonNull Identifier getAnimationResource(@NonNull T animatable) {
        return animatable instanceof CinderGeoBlockEntity blockEntity ? blockEntity.getGeoAnimation() : animation;
    }
}
