package com.sammcb.synthesizer.item

import com.sammcb.synthesizer.Constants
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry
import org.quiltmc.qsl.item.group.api.QuiltItemGroup

object Items {
	val ITEM_GROUP = QuiltItemGroup.builder(Constants.id("${Constants.MOD_ID}_group")).icon { ItemStack(TUNER) }.build()

	val TUNER = TunerItem(settings().maxCount(1).rarity(Rarity.EPIC).fireproof())
	val ECHO_FLOWER = EchoFlowerItem(settings().rarity(Rarity.UNCOMMON))
	val ECHO_STAR = EchoStarItem(settings().rarity(Rarity.RARE))
	val ECHO_INGOT = EchoIngotItem(settings().rarity(Rarity.UNCOMMON).fireproof())

	fun init() {
		Registry.register(Registry.ITEM, Constants.id("tuner"), TUNER)
		Registry.register(Registry.ITEM, Constants.id("echo_flower"), ECHO_FLOWER)
		Registry.register(Registry.ITEM, Constants.id("echo_star"), ECHO_STAR)
		Registry.register(Registry.ITEM, Constants.id("echo_ingot"), ECHO_INGOT)
	}

	fun settings(): Item.Settings = Item.Settings().group(ITEM_GROUP)
}
