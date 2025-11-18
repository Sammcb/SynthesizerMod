package com.sammcb.synthesizer.block.entity

import net.minecraft.util.Mth

class SynthesizerClientData {
	companion object {
		private val ROTATION_SPEED = 5f
	}
	var previousSpin = 0f; private set
	var currentSpin = 0f; private set

	fun updateDisplayEntity() {
		previousSpin = currentSpin
		currentSpin = Mth.wrapDegrees(currentSpin + ROTATION_SPEED)
	}
}
