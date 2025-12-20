package com.sammcb.synthesizer.renderer.blockentity

import com.sammcb.synthesizer.block.SynthesizerBlock
import com.sammcb.synthesizer.block.entity.SynthesizerBlockEntity
import com.sammcb.synthesizer.renderer.blockentity.state.SynthesizerRenderState
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.ItemEntityRenderer
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.item.ItemModelResolver
import net.minecraft.client.renderer.state.CameraRenderState
import net.minecraft.tags.BlockTags
import net.minecraft.util.Ease
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.phys.Vec3
import kotlinx.io.discardingSink

class SynthesizerRenderer(context: BlockEntityRendererProvider.Context): BlockEntityRenderer<SynthesizerBlockEntity, SynthesizerRenderState> {
	private val itemModelResolver: ItemModelResolver
	private val random = RandomSource.create()

	init {
		itemModelResolver = context.itemModelResolver()
	}

	override fun createRenderState() = SynthesizerRenderState()

	override fun extractRenderState(synthesizerBlockEntity: SynthesizerBlockEntity, synthesizerRenderState: SynthesizerRenderState, tickProgress: Float, cameraPos: Vec3, crumblingOverlay: ModelFeatureRenderer.CrumblingOverlay?) {
		super<BlockEntityRenderer>.extractRenderState(synthesizerBlockEntity, synthesizerRenderState, tickProgress, cameraPos, crumblingOverlay)
		val level = synthesizerBlockEntity.getLevel()
		if (level == null) return
		val itemStack = synthesizerBlockEntity.getTheItem().copy()
		val above = synthesizerBlockEntity.getBlockPos().above()
		val carpetAbove = level.getBlockState(above).`is`(BlockTags.WOOL_CARPETS)
		val airAbove = level.getBlockState(above).isAir()
		if (itemStack.isEmpty() || !(airAbove || carpetAbove)) return
		val displayItem = ItemClusterRenderState()
		itemModelResolver.updateForTopItem(displayItem.item, itemStack, ItemDisplayContext.GROUND, level, null, 0)
		displayItem.count = ItemClusterRenderState.getRenderedAmount(itemStack.getCount())
		displayItem.seed = ItemClusterRenderState.getSeedForItemStack(itemStack)
		synthesizerRenderState.displayItem = displayItem
		val synthesizerEnabled: Boolean = synthesizerBlockEntity.getBlockState().getValue(SynthesizerBlock.ENABLED)
		val tickDelta = if (synthesizerEnabled) tickProgress else 0f
		val clientData = synthesizerBlockEntity.clientData
		synthesizerRenderState.spin = Mth.rotLerp(tickDelta, clientData.previousSpin, clientData.currentSpin)
		val lightColor = LevelRenderer.getLightColor(level, above)
		synthesizerRenderState.lightColor = lightColor
	}

	override fun submit(synthesizerRenderState: SynthesizerRenderState, poseStack: PoseStack, submitNodeCollector: SubmitNodeCollector, cameraRenderState: CameraRenderState) {
		val displayItem = synthesizerRenderState.displayItem
		if (displayItem == null) return
		poseStack.pushPose()
		val offset = Ease.inOutSine(Mth.abs(synthesizerRenderState.spin) / 180) / 8
		poseStack.translate(0.5, 1.25 + offset, 0.5)
		poseStack.mulPose(Axis.YP.rotationDegrees(synthesizerRenderState.spin))
		ItemEntityRenderer.renderMultipleFromCount(poseStack, submitNodeCollector, synthesizerRenderState.lightColor, displayItem, random)
		poseStack.popPose()
	}
}
