package com.sammcb.synthesizer.item

import com.sammcb.synthesizer.Constants
import com.sammcb.synthesizer.block.Blocks
import com.sammcb.synthesizer.block.SynthesizerBlock
import com.sammcb.synthesizer.block.enums.Tier
import com.sammcb.synthesizer.config.Config
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.gameevent.GameEvent

class TunerItem(properties: Item.Properties): Item(properties) {
	override fun useOn(useOnContext: UseOnContext): InteractionResult {
		val level = useOnContext.getLevel()
		if (level.isClientSide()) return InteractionResult.SUCCESS

		val pos = useOnContext.getClickedPos()
		val state = level.getBlockState(pos)
		if (!state.`is`(Blocks.SYNTHESIZER)) return InteractionResult.FAIL

		val currentTier = state.getValue(SynthesizerBlock.TIER)
		val nextTier = Tier.values().firstOrNull { it.ordinal > currentTier.ordinal } ?: Tier.WOOD
		val updateFlags = Block.UPDATE_NEIGHBORS or Block.UPDATE_CLIENTS or Block.UPDATE_IMMEDIATE
		level.setBlock(pos, state.setValue(SynthesizerBlock.TIER, nextTier), updateFlags)
		val player = useOnContext.getPlayer()
		level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)

		if (player != null) {
			player.displayClientMessage(Component.translatable("item.${Constants.MOD_ID}.tuner.tune", nextTier.stackSize(), Config.delay()), true)
		}

		level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.PLAYERS, 1f, 1f)

		return InteractionResult.SUCCESS
	}
}
