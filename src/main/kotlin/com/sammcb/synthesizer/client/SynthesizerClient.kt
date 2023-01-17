package com.sammcb.synthesizer.client

import com.sammcb.synthesizer.block.entity.BlockEntities
import com.sammcb.synthesizer.client.render.block.entity.SynthesizerBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer

@Suppress("unused")
class SynthesizerClient: ClientModInitializer {
	override fun onInitializeClient(mod: ModContainer) {
		BlockEntityRendererFactories.register(BlockEntities.SYNTHESIZER, ::SynthesizerBlockEntityRenderer)
	}
}
