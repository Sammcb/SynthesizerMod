package com.sammcb.synthesizer

import net.minecraft.resources.ResourceLocation

object Constants {
	const val MOD_ID = "synthesizer"

	fun id(path: String) = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
}
