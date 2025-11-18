package com.sammcb.synthesizer.block.enums

import com.sammcb.synthesizer.config.Config
import net.minecraft.util.StringRepresentable

enum class Tier(val level: String): StringRepresentable {
	WOOD("wood"),
	COPPER("copper"),
	IRON("iron"),
	GOLD("gold"),
	DIAMOND("diamond"),
	NETHERITE("netherite");

	override fun getSerializedName() = level

	fun stackSize() = Config.stackSize(this)
}
