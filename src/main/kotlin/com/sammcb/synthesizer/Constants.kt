package com.sammcb.synthesizer

import net.minecraft.util.Identifier

object Constants {
	const val MOD_ID = "synthesizer"

	fun id(path: String) = Identifier(MOD_ID, path)
}
