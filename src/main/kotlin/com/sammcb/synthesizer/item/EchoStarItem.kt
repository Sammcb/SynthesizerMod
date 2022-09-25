package com.sammcb.synthesizer.item

import com.sammcb.synthesizer.Constants
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World

class EchoStarItem(settings: Settings): Item(settings) {
	override fun appendTooltip(itemStack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
		tooltip.add(Text.translatable("item.${Constants.MOD_ID}.echo_star.tooltip").formatted(Formatting.DARK_PURPLE))
	}

	override fun hasGlint(stack: ItemStack): Boolean = true
}