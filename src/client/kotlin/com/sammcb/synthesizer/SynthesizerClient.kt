package com.sammcb.synthesizer

import com.sammcb.synthesizer.block.entity.BlockEntities
import com.sammcb.synthesizer.renderer.blockentity.SynthesizerRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.fabricmc.api.ClientModInitializer

@Suppress("unused")
object SynthesizerClient: ClientModInitializer {
	override fun onInitializeClient() {
		BlockEntityRenderers.register(BlockEntities.SYNTHESIZER, ::SynthesizerRenderer)
	}
}
