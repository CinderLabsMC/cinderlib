package net.cinderlabsmc.cinderlib.block.geo.client;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

/** Item renderer for GeckoLib block items; centers the model like a block. */
public class CinderGeoItemRenderer<T extends Item & GeoAnimatable> extends GeoItemRenderer<T> {

    public CinderGeoItemRenderer(@NonNull GeoModel<T> model) {
        super(model);
    }

    @Override
    public void adjustRenderPose(@NonNull RenderPassInfo<GeoRenderState> renderPassInfo) {
        renderPassInfo.poseStack().translate(0.5f, 0, 0.5f);
    }
}
