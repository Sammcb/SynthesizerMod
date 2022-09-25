package com.sammcb.synthesizer.block

import com.sammcb.synthesizer.Constants
import net.minecraft.block.Block
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.BlockView

class EchoBlock(settings: Settings): Block(settings)  {
	override fun appendTooltip(itemStack: ItemStack, world: BlockView?, tooltip: MutableList<Text>, context: TooltipContext) {
		tooltip.add(Text.translatable("block.${Constants.MOD_ID}.echo_block.tooltip").formatted(Formatting.DARK_PURPLE))
	}
}
