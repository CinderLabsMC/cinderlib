package net.cinderlabsmc.cinderlib.block.geo.client;

import org.jspecify.annotations.NonNull;

import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.BoneSnapshots;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.cinderlabsmc.cinderlib.block.geo.CinderBones;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeo;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeoBlockEntity;
import net.cinderlabsmc.cinderlib.block.geo.CinderGeoBlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;

/** Client-only GeckoLib glue. Never load this class on a dedicated server. */
public final class CinderGeoClient {

  private CinderGeoClient() {
  }

  @SuppressWarnings("unchecked")
  public static void registerBlockRenderer(@NonNull BlockEntityType<?> type, @NonNull CinderGeo geo) {
    var geoType = (BlockEntityType<CinderGeoBlockEntity>) type;
    var model = new CinderGeoModel<CinderGeoBlockEntity>(geo.model(), geo.texture(), geo.animation());
    BlockEntityRendererRegistry.register(geoType, context -> new CinderGeoBlockRenderer<>(context, model));
  }

  public static @NonNull GeoItemRenderer<CinderGeoBlockItem> createItemRenderer(@NonNull CinderGeoBlockItem item) {
    var geo = item.geo();
    return new CinderGeoItemRenderer<>(new CinderGeoModel<>(geo.itemModel(), geo.texture(), geo.animation()));
  }

  public static void applyBones(CinderBones bones, BoneSnapshots snapshots) {
    bones.forEach((name, bone) -> snapshots.ifPresent(name, snapshot -> {
      if (bone.hidden()) {
        snapshot.skipRender(true);
      }
      if (bone.scaled()) {
        snapshot.setScale(snapshot.getScaleX() * bone.scaleX(), snapshot.getScaleY() * bone.scaleY(),
            snapshot.getScaleZ() * bone.scaleZ());
      }
    }));
  }
}
