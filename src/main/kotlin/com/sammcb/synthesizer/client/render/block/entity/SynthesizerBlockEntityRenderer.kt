package com.sammcb.synthesizer.client.render.block.entity

import com.sammcb.synthesizer.block.entity.SynthesizerBlockEntity
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.Items
import net.minecraft.item.ItemStack
import net.minecraft.tag.BlockTags
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3f

class SynthesizerBlockEntityRenderer(context: Context): BlockEntityRenderer<SynthesizerBlockEntity> {
	override fun render(synthesizer: SynthesizerBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
		val carpetAbove = synthesizer.getWorld()?.getBlockState(synthesizer.getPos().up())?.isIn(BlockTags.WOOL_CARPETS) ?: false
		val airAbove = synthesizer.getWorld()?.getBlockState(synthesizer.getPos().up())?.isOf(Blocks.AIR) ?: false
		if (synthesizer.getItemStack().isOf(Items.AIR) || !(airAbove || carpetAbove)) return
		matrices.push()

		val time = (synthesizer.getWorld()?.getTime() ?: 0) + tickDelta
		val offset = MathHelper.sin(time / 8) / 8
		matrices.translate(0.5, 1.25 + offset, 0.5)
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(time * 4))

		val itemStack = synthesizer.getItemStack()
		val lightAbove = WorldRenderer.getLightmapCoordinates(synthesizer.getWorld(), synthesizer.getPos().up())
		MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers, 0)

		matrices.pop()
	}
}
