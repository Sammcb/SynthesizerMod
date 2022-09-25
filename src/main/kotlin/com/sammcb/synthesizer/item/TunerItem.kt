package com.sammcb.synthesizer.item

import com.sammcb.synthesizer.Constants
import com.sammcb.synthesizer.block.Blocks
import com.sammcb.synthesizer.block.SynthesizerBlock
import com.sammcb.synthesizer.block.enums.Tier
import com.sammcb.synthesizer.config.Config
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.world.World

class TunerItem(settings: Settings): Item(settings) {
	override fun useOnBlock(context: ItemUsageContext): ActionResult {
		val world = context.getWorld()
		if (world.isClient) return ActionResult.SUCCESS

		val pos = context.getBlockPos()
		val state = world.getBlockState(pos)
		if (!state.isOf(Blocks.SYNTHESIZER)) return ActionResult.FAIL

		val currentTier = state.get(SynthesizerBlock.TIER)
		val nextTier = Tier.values().firstOrNull { it.ordinal > currentTier.ordinal && it.stackSize() > 0 } ?: Tier.WOOD
		world.setBlockState(pos, state.with(SynthesizerBlock.TIER, nextTier))

		val player = context.getPlayer()
		if (player != null) {
			val ticks = if (Config.delay() < 0) 0 else Config.delay()
			player.sendMessage(Text.translatable("item.${Constants.MOD_ID}.tuner.tune", nextTier.stackSize(), ticks), true)
		}

		world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_PLACE, SoundCategory.PLAYERS, 1f, 1f)

		return ActionResult.SUCCESS
	}

	override fun appendTooltip(itemStack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
		val ticks = if (Config.delay() < 0) 0 else Config.delay()
		tooltip.add(Text.translatable("item.${Constants.MOD_ID}.tuner.tooltip_1").formatted(Formatting.DARK_PURPLE))
		tooltip.add(Text.translatable("item.${Constants.MOD_ID}.tuner.tooltip_2").formatted(Formatting.DARK_PURPLE))
		tooltip.add(Text.translatable("item.${Constants.MOD_ID}.tuner.tooltip_3", ticks).formatted(Formatting.DARK_PURPLE))
	}
}
