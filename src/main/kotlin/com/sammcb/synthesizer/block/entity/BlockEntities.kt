package com.sammcb.synthesizer.block.entity

import com.sammcb.synthesizer.Constants
import com.sammcb.synthesizer.block.Blocks
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registry
import net.minecraft.registry.Registries
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder

object BlockEntities {
	val SYNTHESIZER = QuiltBlockEntityTypeBuilder.create(::SynthesizerBlockEntity, Blocks.SYNTHESIZER).build()

	private fun register(blockEntityId: String, blockEntityType: BlockEntityType<*>) {
		Registry.register(Registries.BLOCK_ENTITY_TYPE, Constants.id(blockEntityId), blockEntityType)
	}

	fun init() {
		register("synthesizer", SYNTHESIZER)
	}
}
