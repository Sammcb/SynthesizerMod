package com.sammcb.synthesizer.config

import com.sammcb.synthesizer.Constants
import com.sammcb.synthesizer.block.enums.Tier
import java.io.File
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import org.quiltmc.loader.api.QuiltLoader

@Serializable
private data class ConfigData(val allow: Boolean, val items: List<String>, val tiers: Map<String, Int>, val delay: Int)

object Config {
	const val WOOD_MIN_SIZE = 1
	private var configData: ConfigData? = null
	private const val defaultConfig = """{
	"allow": false,
	"items": [],
	"tiers": {
		"wood": 1,
		"copper": 4,
		"iron": 8,
		"gold": 16,
		"diamond": 32,
		"netherite": 64
	},
	"delay": 20
}
"""

	fun init() {
		val path = QuiltLoader.getConfigDir();
		val file = File("${path}/${Constants.MOD_ID}.json")

		val isNewFile = file.createNewFile()
		val jsonString: String
		if (isNewFile) {
			file.writeText(defaultConfig)
			jsonString = defaultConfig
		} else {
			jsonString = file.readText()
		}

		configData = Json.decodeFromString<ConfigData>(jsonString)
	}

	fun allow(): Boolean = configData?.allow ?: false
	fun items(): List<String> = configData?.items ?: listOf<String>()
	fun delay(): Int = configData?.delay ?: 20

	fun inList(itemStack: ItemStack): Boolean {
		val tags = configData?.items?.filter { it.first() == '#' } ?: listOf<String>()
		for (tag in tags) {
			val tagKey = TagKey.of(RegistryKeys.ITEM, Identifier(tag.drop(1)))
			if (itemStack.isIn(tagKey)) return true
		}
		val itemId = Registries.ITEM.getId(itemStack.getItem()).toString()
		return Config.items().contains(itemId)
	}

	fun stackSize(tier: Tier): Int {
		val defaultAmount = when(tier) {
			Tier.WOOD -> 1
			Tier.COPPER -> 4
			Tier.IRON -> 8
			Tier.GOLD -> 16
			Tier.DIAMOND -> 32
			Tier.NETHERITE -> 64
		}

		val tiers = configData?.tiers ?: mapOf<String, Int>()
		val stackSize = tiers.getOrDefault(tier.asString(), defaultAmount)
		return if (tier == Tier.WOOD && stackSize < 1) WOOD_MIN_SIZE else stackSize
	}
}
