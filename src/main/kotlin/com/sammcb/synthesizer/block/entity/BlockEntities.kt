package com.sammcb.synthesizer.block.entity

import com.sammcb.synthesizer.Constants
import com.sammcb.synthesizer.block.Blocks
import net.minecraft.util.registry.Registry
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder

object BlockEntities {
	val SYNTHESIZER = QuiltBlockEntityTypeBuilder.create(::SynthesizerBlockEntity, Blocks.SYNTHESIZER).build()

	fun init() {
		Registry.register(Registry.BLOCK_ENTITY_TYPE, Constants.id("synthesizer"), SYNTHESIZER)
	}
}
