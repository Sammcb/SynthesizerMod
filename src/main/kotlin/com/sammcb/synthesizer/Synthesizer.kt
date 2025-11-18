package com.sammcb.synthesizer

import com.sammcb.synthesizer.Constants
import com.sammcb.synthesizer.Log.LOGGER
import com.sammcb.synthesizer.block.Blocks
import com.sammcb.synthesizer.block.entity.BlockEntities
import com.sammcb.synthesizer.config.Config
import com.sammcb.synthesizer.item.Items
import net.fabricmc.api.ModInitializer

@Suppress("unused")
object Synthesizer: ModInitializer {
	override fun onInitialize() {
		Config.validateFileExists()
		Blocks.init()
		BlockEntities.init()
		Items.init()
		LOGGER.info("${Constants.MOD_ID} has been initialized.")
	}
}
