package com.sammcb.synthesizer

import net.minecraft.resources.Identifier

object Constants {
	const val MOD_ID = "synthesizer"

	fun id(path: String) = Identifier.fromNamespaceAndPath(MOD_ID, path)
}
