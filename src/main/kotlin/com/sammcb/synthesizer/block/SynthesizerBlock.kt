package com.sammcb.synthesizer.block

import com.sammcb.synthesizer.Constants
import com.sammcb.synthesizer.block.entity.BlockEntities
import com.sammcb.synthesizer.block.entity.SynthesizerBlockEntity
import com.sammcb.synthesizer.block.enums.Tier
import com.sammcb.synthesizer.config.Config
import com.mojang.serialization.MapCodec
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.tags.BlockTags
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.redstone.Orientation
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

class SynthesizerBlock(properties: BlockBehaviour.Properties): BaseEntityBlock(properties)  {
	companion object {
		val CODEC: MapCodec<SynthesizerBlock> = simpleCodec(::SynthesizerBlock)
		val ENABLED = BlockStateProperties.ENABLED
		val TIER = EnumProperty.create("tier", Tier::class.java)
	}

	init {
		registerDefaultState(stateDefinition.any().setValue(ENABLED, true).setValue(TIER, Tier.WOOD))
	}

	override fun codec() = CODEC

	override fun newBlockEntity(pos: BlockPos, state: BlockState) = SynthesizerBlockEntity(pos, state)

	private fun setSynthesizedItem(level: Level, pos: BlockPos, itemStack: ItemStack, player: Player) {
		if (level.isClientSide()) return
		val synthesizerBlockEntity = level.getBlockEntity(pos)
		if (synthesizerBlockEntity !is SynthesizerBlockEntity) return

		synthesizerBlockEntity.setTheItem(itemStack.consumeAndReturn(1, player))
		// Emit event of block change
		level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)
	}

	override fun useItemOn(itemStack: ItemStack, state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult): InteractionResult {
		if (level.isClientSide()) return InteractionResult.SUCCESS
		val synthesizerBlockEntity = level.getBlockEntity(pos)
		if (synthesizerBlockEntity !is SynthesizerBlockEntity) return InteractionResult.SUCCESS
		if (itemStack.isEmpty() || !synthesizerBlockEntity.getTheItem().isEmpty()) return InteractionResult.TRY_WITH_EMPTY_HAND

		val inList = Config.inList(itemStack)
		val itemName = itemStack.getStyledHoverName()
		if ((Config.allow() && !inList) || (!Config.allow() && inList)) {
			player.displayClientMessage(Component.translatable("block.${Constants.MOD_ID}.synthesizer.deny", itemName).withStyle(ChatFormatting.RED), true)
			return InteractionResult.SUCCESS
		}

		player.displayClientMessage(Component.translatable("block.${Constants.MOD_ID}.synthesizer.accept", itemName).withStyle(ChatFormatting.GREEN), true)

		setSynthesizedItem(level, pos, itemStack, player)
		return InteractionResult.SUCCESS
	}

	override fun useWithoutItem(blockState: BlockState, level: Level, pos: BlockPos, player: Player, hit: BlockHitResult): InteractionResult {
		if (level.isClientSide()) return InteractionResult.PASS
		val synthesizerBlockEntity = level.getBlockEntity(pos)
		if (synthesizerBlockEntity !is SynthesizerBlockEntity) return InteractionResult.PASS

		synthesizerBlockEntity.popOutTheItem()

		player.displayClientMessage(Component.translatable("block.${Constants.MOD_ID}.synthesizer.stop").withStyle(ChatFormatting.GREEN), true)

		setSynthesizedItem(level, pos, ItemStack.EMPTY, player)
		return InteractionResult.SUCCESS
	}

	override fun <T: BlockEntity> getTicker(level: Level, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
		val synthesizerEnabled: Boolean = state.getValue(ENABLED)
		if (!synthesizerEnabled) return null

		val clientTick = { _: Level, _: BlockPos, _: BlockState, synthesizerBlockEntity: SynthesizerBlockEntity -> SynthesizerBlockEntity.clientTick(synthesizerBlockEntity) }
		if (level.isClientSide()) return createTickerHelper(type, BlockEntities.SYNTHESIZER, clientTick)
		return createTickerHelper(type, BlockEntities.SYNTHESIZER, SynthesizerBlockEntity::tick)
	}

	// Check to see if powered when placed
	override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState = defaultBlockState().setValue(ENABLED, !blockPlaceContext.getLevel().hasNeighborSignal(blockPlaceContext.getClickedPos()))

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(ENABLED, TIER)
	}

	override fun neighborChanged(state: BlockState, level: Level, pos: BlockPos, block: Block, orientation: Orientation?, bl: Boolean) {
		if (level.isClientSide()) return

		val enabled: Boolean = state.getValue(ENABLED)
		val shouldEnable = !level.hasNeighborSignal(pos)
		if (enabled == shouldEnable) return
		val updateFlags = Block.UPDATE_NEIGHBORS or Block.UPDATE_CLIENTS
		level.setBlock(pos, state.setValue(ENABLED, shouldEnable), updateFlags)
	}

	override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
		super.animateTick(state, level, pos, random)

		val synthesizer = level.getBlockEntity(pos)
		if (synthesizer !is SynthesizerBlockEntity) return
		val synthesizerEnabled: Boolean = state.getValue(ENABLED)
		val airBelow = level.getBlockState(pos.below()).isAir()
		val carpetAbove = level.getBlockState(pos.above()).`is`(BlockTags.WOOL_CARPETS)
		if (synthesizer.getTheItem().isEmpty() || !airBelow || carpetAbove || !synthesizerEnabled) return

		val particlePos = Vec3.atBottomCenterOf(pos.above())
		val particleVelocity = Vec3(random.nextGaussian(), random.nextGaussian() + 1, random.nextGaussian())

		level.addParticle(ParticleTypes.PORTAL, particlePos.x(), particlePos.y(), particlePos.z(), particleVelocity.x(), particleVelocity.y(), particleVelocity.z())
	}
}
