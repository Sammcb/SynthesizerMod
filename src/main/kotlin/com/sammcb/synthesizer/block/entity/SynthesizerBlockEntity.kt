package com.sammcb.synthesizer.block.entity

import com.sammcb.synthesizer.block.SynthesizerBlock
import com.sammcb.synthesizer.block.entity.SynthesizerClientData
import com.sammcb.synthesizer.block.enums.Tier
import com.sammcb.synthesizer.config.Config
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.Vec3
import net.minecraft.world.ticks.ContainerSingleItem

class SynthesizerBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(BlockEntities.SYNTHESIZER, pos, state), ContainerSingleItem.BlockContainerSingleItem {
	private var ticked = 0
	private var item = ItemStack.EMPTY
	val clientData = SynthesizerClientData()

	companion object {
		private const val ITEM_TAG_ID = "Item"

		fun clientTick(entity: SynthesizerBlockEntity) {
			entity.clientData.updateDisplayEntity()
		}

		fun tick(level: Level, pos: BlockPos, state: BlockState, entity: SynthesizerBlockEntity) {
			val below = pos.below()
			if (entity.getTheItem().isEmpty() || !level.getBlockState(below).isAir()) return

			if (entity.ticked < Config.delay()) {
				entity.ticked++
				return
			}

			entity.ticked = 0
			val synthesizedItemStack = entity.getTheItem().copy()
			val synthesizerTier: Tier = state.getValue(SynthesizerBlock.TIER)
			val stackSize = synthesizerTier.stackSize()
			synthesizedItemStack.setCount(stackSize)

			val itemPos = Vec3.atCenterOf(below)
			val itemVelocity = Vec3.ZERO
			val itemEntity = ItemEntity(level, itemPos.x(), itemPos.y(), itemPos.z(), synthesizedItemStack, itemVelocity.x(), itemVelocity.y(), itemVelocity.z())
			itemEntity.setDefaultPickUpDelay()
			level.addFreshEntity(itemEntity)

			if (level.getBlockState(pos.above()).`is`(BlockTags.WOOL_CARPETS)) return
			level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5f, 1f)
		}
	}

	fun popOutTheItem() {
		val entityLevel = getLevel()
		if (entityLevel == null || entityLevel.isClientSide()) return
		val itemStack = getTheItem().copy()
		if (itemStack.isEmpty()) return
		val pos = getBlockPos()
		removeTheItem()
		val itemPosition = Vec3.upFromBottomCenterOf(pos, 1.01).offsetRandom(entityLevel.random, 0.7f)
		val itemEntity = ItemEntity(entityLevel, itemPosition.x(), itemPosition.y(), itemPosition.z(), itemStack)
		itemEntity.setDefaultPickUpDelay()
		entityLevel.addFreshEntity(itemEntity)
	}

	// Save block data
	override fun saveAdditional(valueOutput: ValueOutput) {
		super.saveAdditional(valueOutput)
		if (item.isEmpty()) return
		valueOutput.store(ITEM_TAG_ID, ItemStack.CODEC, item)
	}

	// Load block data
	override fun loadAdditional(valueInput: ValueInput) {
		super.loadAdditional(valueInput)
		item = valueInput.read(ITEM_TAG_ID, ItemStack.CODEC).orElse(ItemStack.EMPTY)
	}

	// Create an update tag to sync data with client
	override fun getUpdateTag(provider: HolderLookup.Provider) = saveCustomOnly(provider)

	// Send update tag to client
	override fun getUpdatePacket() = ClientboundBlockEntityDataPacket.create(this)

	override fun getContainerBlockEntity() = this

	override fun getMaxStackSize() = 1

	override fun getTheItem() = item

	override fun splitTheItem(index: Int): ItemStack {
		val itemStack = getTheItem()
		setTheItem(ItemStack.EMPTY)
		return itemStack
	}

	override fun setTheItem(itemStack: ItemStack) {
		val itemStackSingleItem = itemStack
		itemStackSingleItem.limitSize(getMaxStackSize())
		item = itemStackSingleItem
		// Mark block entity chunk dirty so it is saved
		setChanged()
		val entityLevel = getLevel()
		if (entityLevel == null) return
		// Send update to neighbors and client
		val updateFlags = Block.UPDATE_NEIGHBORS or Block.UPDATE_CLIENTS
		entityLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), updateFlags)
	}
}
