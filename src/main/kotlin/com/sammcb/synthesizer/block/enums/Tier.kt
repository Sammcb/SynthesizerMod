package com.sammcb.synthesizer.block.enums

import com.sammcb.synthesizer.config.Config
import net.minecraft.util.StringIdentifiable

enum class Tier(val level: String): StringIdentifiable {
	WOOD("wood"),
	COPPER("copper"),
	IRON("iron"),
	GOLD("gold"),
	DIAMOND("diamond"),
	NETHERITE("netherite");

	override fun asString(): String = level

	fun stackSize(): Int = Config.stackSize(this)
}
