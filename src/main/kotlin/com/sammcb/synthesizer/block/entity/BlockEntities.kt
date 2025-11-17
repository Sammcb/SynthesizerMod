package com.sammcb.synthesizer.block.entity

import com.sammcb.synthesizer.Constants
import com.sammcb.synthesizer.block.Blocks
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder

object BlockEntities {
	private fun <T: BlockEntity> register(blockEntityId: String, entityFactory: FabricBlockEntityTypeBuilder.Factory<out T>, vararg blocks: Block): BlockEntityType<T> {
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.id(blockEntityId), FabricBlockEntityTypeBuilder.create(entityFactory, *blocks).build())
	}

	val SYNTHESIZER = register("synthesizer", ::SynthesizerBlockEntity, Blocks.SYNTHESIZER)

	fun init() {}
}
