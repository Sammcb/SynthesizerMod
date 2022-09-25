package com.sammcb.synthesizer

import com.sammcb.synthesizer.Log.LOGGER
import com.sammcb.synthesizer.block.Blocks
import com.sammcb.synthesizer.block.entity.BlockEntities
import com.sammcb.synthesizer.config.Config
import com.sammcb.synthesizer.item.Items
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer

@Suppress("unused")
class Synthesizer: ModInitializer {
	override fun onInitialize(mod: ModContainer) {
		Config.init()
		Blocks.init()
		BlockEntities.init()
		Items.init()
		LOGGER.info("${mod.metadata().name()} has been initialized.")
	}
}
