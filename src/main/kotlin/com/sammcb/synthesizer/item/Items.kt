package com.sammcb.synthesizer.item

import com.sammcb.synthesizer.Constants
import com.sammcb.synthesizer.block.Blocks
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Rarity

object Items {
	val TUNER = TunerItem(settings().maxCount(1).rarity(Rarity.EPIC).fireproof())
	val ECHO_FLOWER = EchoFlowerItem(settings().rarity(Rarity.UNCOMMON))
	val ECHO_STAR = EchoStarItem(settings().rarity(Rarity.RARE))
	val ECHO_INGOT = EchoIngotItem(settings().rarity(Rarity.UNCOMMON).fireproof())
	val SYNTHESIZER = BlockItem(Blocks.SYNTHESIZER, Items.settings().rarity(Rarity.EPIC).fireproof())
	val ECHO_BLOCK = BlockItem(Blocks.ECHO_BLOCK, Items.settings().rarity(Rarity.RARE).fireproof())

	val ITEM_GROUP = FabricItemGroup.builder(Constants.id("${Constants.MOD_ID}_group")).icon({ ItemStack(TUNER) }).build()

	private fun register(itemId: String, item: Item) {
		Registry.register(Registries.ITEM, Constants.id(itemId), item)
		ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register({ entries -> entries.addItem(item) })
	}

	fun init() {
		register("synthesizer", SYNTHESIZER)
		register("tuner", TUNER)
		register("echo_flower", ECHO_FLOWER)
		register("echo_star", ECHO_STAR)
		register("echo_ingot", ECHO_INGOT)
		register("echo_block", ECHO_BLOCK)
	}

	fun settings(): Item.Settings = Item.Settings()
}
