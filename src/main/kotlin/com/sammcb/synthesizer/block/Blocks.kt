package com.sammcb.synthesizer.block

import com.sammcb.synthesizer.Constants
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor

object Blocks {
	private fun resourceKey(blockId: String) = ResourceKey.create(Registries.BLOCK, Constants.id(blockId))

	private val synthesizerProperties = BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)
	val SYNTHESIZER = Blocks.register(resourceKey("synthesizer"), ::SynthesizerBlock, synthesizerProperties)
	private val echoBlockProperties = BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)
	val ECHO_BLOCK = Blocks.register(resourceKey("echo_block"), echoBlockProperties)

	fun init() {}
}
