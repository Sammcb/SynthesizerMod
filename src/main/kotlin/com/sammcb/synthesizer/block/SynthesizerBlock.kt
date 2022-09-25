package com.sammcb.synthesizer.block

import com.sammcb.synthesizer.Constants
import com.sammcb.synthesizer.block.entity.BlockEntities
import com.sammcb.synthesizer.block.entity.SynthesizerBlockEntity
import com.sammcb.synthesizer.block.enums.Tier
import com.sammcb.synthesizer.config.Config
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockRenderType
import net.minecraft.block.Blocks
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.random.RandomGenerator
import net.minecraft.world.BlockView
import net.minecraft.world.World

class SynthesizerBlock(settings: Settings): BlockWithEntity(settings)  {
	companion object {
		val ENABLED = Properties.ENABLED
		val TIER = EnumProperty.of("tier", Tier::class.java)
	}

	init {
		setDefaultState(getDefaultState().with(ENABLED, true).with(TIER, Tier.WOOD))
	}

	override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = SynthesizerBlockEntity(pos, state)

	override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
		if (world.isClient) return ActionResult.SUCCESS

		val itemStack = player.getStackInHand(hand)
		val inList = Config.inList(itemStack)
		val itemName = itemStack.getItem().getName()
		if ((Config.allow() && !inList) || (!Config.allow() && inList)) {
			player.sendMessage(Text.translatable("block.${Constants.MOD_ID}.synthesizer.deny", itemName).formatted(Formatting.RED), true)
			return ActionResult.SUCCESS
		}

		if (itemStack.isEmpty()) {
			player.sendMessage(Text.translatable("block.${Constants.MOD_ID}.synthesizer.stop").formatted(Formatting.GREEN), true)
		} else {
			player.sendMessage(Text.translatable("block.${Constants.MOD_ID}.synthesizer.accept", itemName).formatted(Formatting.GREEN), true)
		}

		val synthesizer = world.getBlockEntity(pos) as SynthesizerBlockEntity
		synthesizer.setItemStack(itemStack.copy())
		world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS)

		return ActionResult.SUCCESS
	}

	override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL

	override fun <T: BlockEntity> getTicker(world: World, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
		return if (!world.isClient) BlockWithEntity.checkType(type, BlockEntities.SYNTHESIZER, SynthesizerBlockEntity::tick) else null
	}

	override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
		builder.add(ENABLED, TIER)
	}

	override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos, notify: Boolean) {
		if (world.isClient) return

		val enabled = state.get(ENABLED)
		val shouldEnable = !world.isReceivingRedstonePower(pos)
		if (enabled != shouldEnable) {
			world.setBlockState(pos, state.with(ENABLED, shouldEnable))
		}
	}

	override fun appendTooltip(itemStack: ItemStack, world: BlockView?, tooltip: MutableList<Text>, context: TooltipContext) {
		tooltip.add(Text.translatable("block.${Constants.MOD_ID}.synthesizer.tooltip_1").formatted(Formatting.DARK_PURPLE))
		tooltip.add(Text.translatable("block.${Constants.MOD_ID}.synthesizer.tooltip_2").formatted(Formatting.DARK_PURPLE))
		tooltip.add(Text.translatable("block.${Constants.MOD_ID}.synthesizer.tooltip_3").formatted(Formatting.DARK_PURPLE))
	}

	override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: RandomGenerator) {
		super.randomDisplayTick(state, world, pos, random)

		val synthesizer = world.getBlockEntity(pos) as SynthesizerBlockEntity
		if (synthesizer.getItemStack().isOf(Items.AIR) || world.getBlockState(pos.down()).getBlock() != Blocks.AIR || !state.get(ENABLED)) return

		val x = pos.getX().toDouble() + 0.5
		val y = pos.getY().toDouble() + 1
		val z = pos.getZ().toDouble() + 0.5
		val velocityX = random.nextGaussian()
		val velocityY = random.nextGaussian() + 1
		val velocityZ = random.nextGaussian()

		world.addParticle(ParticleTypes.PORTAL, x, y, z, velocityX, velocityY, velocityZ)
	}
}
