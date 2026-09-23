package net.cinderlabsmc.cinderlib.armor;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

import net.cinderlabsmc.cinderlib.block.geo.CinderBones;
import net.cinderlabsmc.cinderlib.block.geo.client.CinderGeoClient;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/**
 * Renderer for {@link CinderArmorItem}: everything is driven by the item's
 * {@link CinderArmor}.
 */
public class CinderArmorRenderer<R extends HumanoidRenderState & GeoRenderState>
    extends GeoArmorRenderer<CinderArmorItem, R> {

  private static final DataTicket<ItemStack> STACK = DataTicket.create("cinderlib_armor_stack", ItemStack.class);

  private final CinderArmor armor;

  public CinderArmorRenderer(@NonNull CinderArmorItem item) {
    super(new Model(item.armor()));
    this.armor = item.armor();
    this.scaleWidth = armor.scaleWidth();
    this.scaleHeight = armor.scaleHeight();
  }

  @Override
  public void captureDefaultRenderState(@NonNull CinderArmorItem animatable, @NonNull RenderData renderData,
      @NonNull R renderState, float partialTick) {
    super.captureDefaultRenderState(animatable, renderData, renderState, partialTick);
    renderState.addGeckolibData(STACK, renderData.itemStack());
  }

  @Override
  public @NonNull List<ArmorSegment> getSegmentsForSlot(@NonNull R renderState, @NonNull EquipmentSlot slot) {
    return armor.parts(slot).stream().map(CinderArmorRenderer::segment).toList();
  }

  @Override
  public @NonNull String getBoneNameForSegment(@NonNull R renderState, @NonNull ArmorSegment segment) {
    return armor.bone(part(segment));
  }

  @Override
  public int getRenderColor(@NonNull CinderArmorItem animatable, @NonNull RenderData data, float partialTick) {
    var tint = armor.tint(data.itemStack());
    return tint.isPresent() ? tint.getAsInt() : super.getRenderColor(animatable, data, partialTick);
  }

  @Override
  public @Nullable RenderType getRenderType(@NonNull R renderState, @NonNull Identifier texture) {
    return armor.translucent() ? RenderTypes.entityTranslucent(texture) : super.getRenderType(renderState, texture);
  }

  @Override
  public void adjustModelBonesForRender(@NonNull RenderPassInfo<R> renderPassInfo, @NonNull BoneSnapshots snapshots) {
    super.adjustModelBonesForRender(renderPassInfo, snapshots);

    var stack = renderPassInfo.getGeckolibData(STACK);
    var slot = renderPassInfo.getGeckolibData(CURRENT_SLOT);
    if (stack != null && slot != null) {
      var bones = new CinderBones();
      armor.adjustBones(stack, slot, bones);
      CinderGeoClient.applyBones(bones, snapshots);
    }
  }

  private static ArmorSegment segment(CinderArmor.Part part) {
    return switch (part) {
      case HEAD -> ArmorSegment.HEAD;
      case BODY -> ArmorSegment.CHEST;
      case LEFT_ARM -> ArmorSegment.LEFT_ARM;
      case RIGHT_ARM -> ArmorSegment.RIGHT_ARM;
      case LEFT_LEG -> ArmorSegment.LEFT_LEG;
      case RIGHT_LEG -> ArmorSegment.RIGHT_LEG;
      case LEFT_FOOT -> ArmorSegment.LEFT_FOOT;
      case RIGHT_FOOT -> ArmorSegment.RIGHT_FOOT;
    };
  }

  private static CinderArmor.Part part(ArmorSegment segment) {
    return switch (segment) {
      case HEAD -> CinderArmor.Part.HEAD;
      case CHEST -> CinderArmor.Part.BODY;
      case LEFT_ARM -> CinderArmor.Part.LEFT_ARM;
      case RIGHT_ARM -> CinderArmor.Part.RIGHT_ARM;
      case LEFT_LEG -> CinderArmor.Part.LEFT_LEG;
      case RIGHT_LEG -> CinderArmor.Part.RIGHT_LEG;
      case LEFT_FOOT -> CinderArmor.Part.LEFT_FOOT;
      case RIGHT_FOOT -> CinderArmor.Part.RIGHT_FOOT;
    };
  }

  /** Picks model and texture of the slot currently rendered. */
  private static final class Model extends GeoModel<CinderArmorItem> {

    private final CinderArmor armor;

    private Model(CinderArmor armor) {
      this.armor = armor;
    }

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
      return armor.model(slot(renderState));
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
      return armor.texture(slot(renderState));
    }

    @Override
    public @NonNull Identifier getAnimationResource(@NonNull CinderArmorItem animatable) {
      return armor.animation();
    }

    private static EquipmentSlot slot(GeoRenderState renderState) {
      var slot = renderState.getGeckolibData(CURRENT_SLOT);
      return slot != null ? slot : EquipmentSlot.CHEST;
    }
  }
}
