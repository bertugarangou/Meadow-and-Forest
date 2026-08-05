package net.carqui.meadowandforest.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Renders the up to 4 items currently drying on a Drying Tray directly in
 * the world, at their 2x2 quadrant positions, matching the slot targeting in
 * {@link MAFDryingTrayBlock}. No progress bar/tint - items simply visually
 * swap to their result the moment drying completes (server sync driven).
 * <p>
 * NOTE: This is the file most likely to need small adjustments once compiled -
 * 26.2's block entity rendering uses a newer submit/render-state pipeline that
 * isn't fully documented locally. If `ItemStackRenderState#submit` or
 * `ItemModelResolver#updateForTopItem` don't match exactly, your IDE's
 * autocomplete against the actual NeoForge/Minecraft jars will show the
 * correct method names/signatures quickly - the overall structure (extract
 * 4 ItemStacks into the render state, then submit each with a PoseStack
 * offset per quadrant) will not need to change.
 */
public class MAFDryingTrayRenderer implements BlockEntityRenderer<MAFDryingTrayBlockEntity, MAFDryingTrayRenderer.RenderState> {

    // Local-space (0-1) centers of the 2x2 grid quadrants, matching MAFDryingTrayBlock#resolveSlot.
    private static final double[] SLOT_X = {0.3, 0.7, 0.3, 0.7};
    private static final double[] SLOT_Z = {0.3, 0.3, 0.7, 0.7};
    private static final double SLOT_Y = 2.0 / 16.0;

    public MAFDryingTrayRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class RenderState extends BlockEntityRenderState {
        final ItemStack[] items = {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
    }

    @Override
    public @NonNull RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(@NonNull MAFDryingTrayBlockEntity blockEntity, @NonNull RenderState renderState, float partialTick, @NonNull Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);
        for (int i = 0; i < MAFDryingTrayBlockEntity.SLOT_COUNT; i++) {
            renderState.items[i] = blockEntity.getSlot(i).stack();
        }
    }

    @Override
    public void submit(@NonNull RenderState renderState, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState cameraState) {
        for (int i = 0; i < MAFDryingTrayBlockEntity.SLOT_COUNT; i++) {
            ItemStack stack = renderState.items[i];
            if (stack.isEmpty()) continue;

            poseStack.pushPose();
            poseStack.translate(SLOT_X[i], SLOT_Y, SLOT_Z[i]);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));
            poseStack.scale(0.4f, 0.4f, 0.4f);

            ItemStackRenderState itemRenderState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(
                    itemRenderState, stack, ItemDisplayContext.FIXED, null, null, 0
            );
            itemRenderState.submit(poseStack, collector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);

            poseStack.popPose();
        }
    }
}
