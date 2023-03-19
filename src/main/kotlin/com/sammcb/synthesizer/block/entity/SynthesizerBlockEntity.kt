package com.sammcb.synthesizer.block.entity

import com.sammcb.synthesizer.block.SynthesizerBlock
import com.sammcb.synthesizer.block.enums.Tier
import com.sammcb.synthesizer.config.Config
import net.minecraft.block.Blocks
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.Packet
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.registry.tag.BlockTags
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class SynthesizerBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(BlockEntities.SYNTHESIZER, pos, state) {
	private var ticked = 0
	private var itemStack = ItemStack(Items.AIR)

	companion object {
		private const val ITEM_STACK_KEY = "itemStack"

		fun tick(world: World, pos: BlockPos, state: BlockState, entity: SynthesizerBlockEntity) {
			val below = pos.down()
			if (world.isClient || entity.itemStack.isOf(Items.AIR) || world.getBlockState(below).getBlock() != Blocks.AIR || !state.get(SynthesizerBlock.ENABLED)) return

			if (entity.ticked < Config.delay()) {
				entity.ticked++
				return
			}

			entity.ticked = 0
			val synthesizedItemStack = entity.getItemStack()
			val stackSize = state.get(SynthesizerBlock.TIER).stackSize()
			synthesizedItemStack.setCount(stackSize)

			val itemEntity = ItemEntity(world, below.x.toDouble() + 0.5, below.y.toDouble() + 0.5, below.z.toDouble() + 0.5, synthesizedItemStack, 0.0, 0.0, 0.0)
			itemEntity.setToDefaultPickupDelay()
			world.spawnEntity(itemEntity)

			if (world.getBlockState(pos.up()).isIn(BlockTags.WOOL_CARPETS)) return
			world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 1f)
		}
	}

	override fun writeNbt(nbt: NbtCompound) {
		val itemStackNbt = itemStack.writeNbt(NbtCompound())
		nbt.put(ITEM_STACK_KEY, itemStackNbt)
		super.writeNbt(nbt)
	}

	override fun readNbt(nbt: NbtCompound) {
		super.readNbt(nbt)
		itemStack = ItemStack.fromNbt(nbt.getCompound(ITEM_STACK_KEY))
	}

	override fun toSyncedNbt(): NbtCompound {
		return NbtCompound().also(::writeNbt)
	}

	override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
		return BlockEntityUpdateS2CPacket.of(this)
	}

	fun setItemStack(newItemStack: ItemStack) {
		val itemStackSingleItem = newItemStack
		itemStackSingleItem.setCount(1)
		itemStack = itemStackSingleItem
		markDirty()
	}

	fun getItemStack(): ItemStack = itemStack.copy()
}
