package com.sammcb.synthesizer.block

import com.sammcb.synthesizer.Constants
import com.sammcb.synthesizer.item.Items
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.MapColor
import net.minecraft.block.Material
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.math.BlockPos
import net.minecraft.util.Rarity
import net.minecraft.world.BlockView

object Blocks {
	val SYNTHESIZER = SynthesizerBlock(Settings.of(Material.METAL, MapColor.IRON_GRAY).requiresTool().strength(5f, 6f).sounds(BlockSoundGroup.METAL))
	val ECHO_BLOCK = EchoBlock(Settings.of(Material.METAL, MapColor.BLACK).requiresTool().strength(5f, 6f).sounds(BlockSoundGroup.METAL))

	private fun register(blockId: String, block: Block) {
		Registry.register(Registries.BLOCK, Constants.id(blockId), block)
	}

	fun init() {
		register("synthesizer", SYNTHESIZER)
		register("echo_block", ECHO_BLOCK)
	}
}
