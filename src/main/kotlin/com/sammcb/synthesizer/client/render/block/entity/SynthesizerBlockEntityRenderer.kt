package com.sammcb.synthesizer.client.render.block.entity

import com.sammcb.synthesizer.block.entity.SynthesizerBlockEntity
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.Items
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.BlockTags
import net.minecraft.util.math.Axis
import net.minecraft.util.math.MathHelper

class SynthesizerBlockEntityRenderer(context: Context): BlockEntityRenderer<SynthesizerBlockEntity> {
	override fun render(synthesizer: SynthesizerBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
		val carpetAbove = synthesizer.getWorld()?.getBlockState(synthesizer.getPos().up())?.isIn(BlockTags.WOOL_CARPETS) ?: false
		val airAbove = synthesizer.getWorld()?.getBlockState(synthesizer.getPos().up())?.isOf(Blocks.AIR) ?: false
		if (synthesizer.getItemStack().isOf(Items.AIR) || !(airAbove || carpetAbove)) return
		matrices.push()

		val time = (synthesizer.getWorld()?.getTime() ?: 0) + tickDelta
		val offset = MathHelper.sin(time / 8) / 8
		matrices.translate(0.5, 1.25 + offset, 0.5)
		matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(time * 4))

		val itemStack = synthesizer.getItemStack()
		val lightAbove = WorldRenderer.getLightmapCoordinates(synthesizer.getWorld(), synthesizer.getPos().up())
		val itemRenderer = MinecraftClient.getInstance().getItemRenderer()
		val model = itemRenderer.getHeldItemModel(itemStack, null, null, 0)
		itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers, lightAbove, overlay, model)

		matrices.pop()
	}
}
