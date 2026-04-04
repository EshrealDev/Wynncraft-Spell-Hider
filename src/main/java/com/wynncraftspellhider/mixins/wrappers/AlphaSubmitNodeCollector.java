package com.wynncraftspellhider.mixins.wrappers;

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.ParticleFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class AlphaSubmitNodeCollector implements SubmitNodeCollector {
    private final SubmitNodeCollector delegate;
    private final float alpha;

    public AlphaSubmitNodeCollector(SubmitNodeCollector delegate, float alpha) {
        this.delegate = delegate;
        this.alpha = alpha;
    }

    @Override
    public void submitCustomGeometry(PoseStack poseStack, RenderType renderType,
                                     SubmitNodeCollector.CustomGeometryRenderer renderer) {
        delegate.submitCustomGeometry(poseStack, renderType,
                (pose, vertexConsumer) -> renderer.render(pose, new AlphaVertexConsumer(vertexConsumer, alpha)));
    }

    @Override
    public void submitItem(PoseStack poseStack, ItemDisplayContext itemDisplayContext,
                           int i, int j, int k, int[] tints, List<BakedQuad> quads,
                           RenderType renderType, ItemStackRenderState.FoilType foilType) {
        int[] modifiedTints = new int[tints.length];
        for (int t = 0; t < tints.length; t++) {
            int argb = tints[t];
            int a = (int)(ARGB.alpha(argb) * alpha);
            modifiedTints[t] = ARGB.color(a, ARGB.red(argb), ARGB.green(argb), ARGB.blue(argb));
        }
        delegate.submitItem(poseStack, itemDisplayContext, i, j, k, modifiedTints, quads, renderType, foilType);
    }

    @Override
    public OrderedSubmitNodeCollector order(int i) {
        return delegate.order(i);
    }

    // --- Everything else delegates straight through ---
    @Override public void submitShadow(PoseStack p, float f, List<EntityRenderState.ShadowPiece> l) { delegate.submitShadow(p, f, l); }
    @Override public void submitNameTag(PoseStack p, @Nullable Vec3 v, int i, Component c, boolean bl, int j, double d, CameraRenderState cs) { delegate.submitNameTag(p, v, i, c, bl, j, d, cs); }
    @Override public void submitText(PoseStack p, float f, float g, FormattedCharSequence fcs, boolean bl, Font.DisplayMode dm, int i, int j, int k, int l) { delegate.submitText(p, f, g, fcs, bl, dm, i, j, k, l); }
    @Override public void submitFlame(PoseStack p, EntityRenderState s, Quaternionf q) { delegate.submitFlame(p, s, q); }
    @Override public void submitLeash(PoseStack p, EntityRenderState.LeashState ls) { delegate.submitLeash(p, ls); }
    @Override public <S> void submitModel(Model<? super S> model, S s, PoseStack p, RenderType rt, int i, int j, int k, @Nullable TextureAtlasSprite tas, int l, ModelFeatureRenderer.@Nullable CrumblingOverlay co) { delegate.submitModel(model, s, p, rt, i, j, k, tas, l, co); }
    @Override public void submitModelPart(ModelPart mp, PoseStack p, RenderType rt, int i, int j, @Nullable TextureAtlasSprite tas, boolean bl, boolean bl2, int k, ModelFeatureRenderer.@Nullable CrumblingOverlay co, int l) { delegate.submitModelPart(mp, p, rt, i, j, tas, bl, bl2, k, co, l); }
    @Override public void submitBlock(PoseStack p, BlockState bs, int i, int j, int k) { delegate.submitBlock(p, bs, i, j, k); }
    @Override public void submitMovingBlock(PoseStack p, MovingBlockRenderState mbrs) { delegate.submitMovingBlock(p, mbrs); }
    @Override public void submitBlockModel(PoseStack p, RenderType rt, BlockStateModel bsm, float f, float g, float h, int i, int j, int k) { delegate.submitBlockModel(p, rt, bsm, f, g, h, i, j, k); }
    @Override public void submitParticleGroup(SubmitNodeCollector.ParticleGroupRenderer pgr) { delegate.submitParticleGroup(pgr); }
}