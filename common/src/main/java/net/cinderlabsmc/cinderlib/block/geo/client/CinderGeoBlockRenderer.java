package net.cinderlabsmc.cinderlib.block.geo.client;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import net.cinderlabsmc.cinderlib.block.geo.CinderBones;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeoBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Generic renderer for every {@link CinderGeoBlockEntity}: per-instance model/texture and {@link CinderBones}
 * adjustments come from the block entity. Facing blocks are rotated by GeckoLib automatically.
 */
public class CinderGeoBlockRenderer<T extends CinderGeoBlockEntity, R extends BlockEntityRenderState & GeoRenderState> extends GeoBlockRenderer<T, R> {

    public static final DataTicket<CinderBones> BONES = DataTicket.create("cinderlib_geo_bones", CinderBones.class);

    public CinderGeoBlockRenderer(BlockEntityRendererProvider.@NonNull Context context, @NonNull GeoModel<T> model) {
        super(context, model);
    }

    @Override
    public void addRenderData(@NonNull T blockEntity, @Nullable Void relatedObject, @NonNull R renderState, float partialTick) {
        renderState.addGeckolibData(CinderGeoModel.MODEL, blockEntity.getGeoModel());
        renderState.addGeckolibData(CinderGeoModel.TEXTURE, blockEntity.getGeoTexture());

        var bones = new CinderBones();
        blockEntity.adjustBones(bones);
        if (!bones.isEmpty()) {
            renderState.addGeckolibData(BONES, bones);
        }
    }

    @Override
    public void adjustModelBonesForRender(@NonNull RenderPassInfo<R> renderPassInfo, @NonNull BoneSnapshots snapshots) {
        var bones = renderPassInfo.getGeckolibData(BONES);
        if (bones != null) {
            CinderGeoClient.applyBones(bones, snapshots);
        }
    }
}
