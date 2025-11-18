package com.sammcb.synthesizer.config

import com.sammcb.synthesizer.Constants
import com.sammcb.synthesizer.block.enums.Tier
import com.sammcb.synthesizer.Log.LOGGER
import java.io.File
import kotlin.math.max
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.ItemStack
import net.fabricmc.loader.api.FabricLoader

@Serializable
private data class ConfigData(val allow: Boolean, val items: List<String>, val tiers: Map<String, Int>, val delay: Int)

object Config {
	private const val MIN_SIZE = 1
	private const val MIN_DELAY = 0
	private const val CONFIG_FILE_NAME = "${Constants.MOD_ID}.json"
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

	init {
		val path = FabricLoader.getInstance().getConfigDir();
		val file = File("${path}/${CONFIG_FILE_NAME}")

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

	fun validateFileExists() {
		val configFile = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME).toFile()
		if (configFile.exists()) return
		LOGGER.warning("Config file ${CONFIG_FILE_NAME} does not exist!")
	}

	fun allow() = configData?.allow ?: false
	fun delay() = max(configData?.delay ?: 20, MIN_DELAY)

	fun inList(itemStack: ItemStack): Boolean {
		val tagPrefix = "#"
		val tags = configData?.items?.filter { it.startsWith(tagPrefix) } ?: listOf<String>()
		for (tag in tags) {
			val tagId = ResourceLocation.withDefaultNamespace(tag.removePrefix(tagPrefix))
			val tagKey = TagKey.create(Registries.ITEM, tagId)
			if (itemStack.`is`(tagKey)) return true
		}

		val itemId = itemStack.getItem().toString()
		val configItems: List<String> = configData?.items ?: listOf<String>()
		return configItems.contains(itemId)
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
		val stackSize = tiers.getOrDefault(tier.level, defaultAmount)
		return max(stackSize, MIN_SIZE)
	}
}
