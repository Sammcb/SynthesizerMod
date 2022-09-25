package com.sammcb.synthesizer.item

import com.sammcb.synthesizer.Constants
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World

class EchoFlowerItem(settings: Settings): Item(settings) {
	override fun appendTooltip(itemStack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
		tooltip.add(Text.translatable("item.${Constants.MOD_ID}.echo_flower.tooltip_1").formatted(Formatting.DARK_PURPLE))
		tooltip.add(Text.translatable("item.${Constants.MOD_ID}.echo_flower.tooltip_2").formatted(Formatting.GRAY, Formatting.ITALIC))
	}
}
