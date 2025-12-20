package com.sammcb.synthesizer.item

import com.sammcb.synthesizer.Constants
import com.sammcb.synthesizer.block.Blocks
import com.sammcb.synthesizer.config.Config
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.ItemLore
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents

object Items {
	private fun resourceKey(itemId: String) = ResourceKey.create(Registries.ITEM, Constants.id(itemId))

	private val tunerLore = ItemLore(listOf(
		Component.translatable("item.${Constants.MOD_ID}.tuner.tooltip_1"),
		Component.translatable("item.${Constants.MOD_ID}.tuner.tooltip_2")
	))
	private val tunerProperties = Item.Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant().component(DataComponents.LORE, tunerLore)
	val TUNER = Items.registerItem(resourceKey("tuner"), ::TunerItem, tunerProperties)

	private val echoFlowerLore = ItemLore(listOf(
		Component.translatable("item.${Constants.MOD_ID}.echo_flower.tooltip_1"),
		Component.translatable("item.${Constants.MOD_ID}.echo_flower.tooltip_2").withStyle(ChatFormatting.GRAY)
	))
	private val echoFlowerProperties = Item.Properties().rarity(Rarity.UNCOMMON).component(DataComponents.LORE, echoFlowerLore)
	val ECHO_FLOWER = Items.registerItem(resourceKey("echo_flower"), ::Item, echoFlowerProperties)

	private val echoStarLore = ItemLore(listOf(
		Component.translatable("item.${Constants.MOD_ID}.echo_star.tooltip")
	))
	private val echoStarProperties = Item.Properties().rarity(Rarity.RARE).component(DataComponents.LORE, echoStarLore)
	val ECHO_STAR = Items.registerItem(resourceKey("echo_star"), ::Item, echoStarProperties)

	private val echoIngotLore = ItemLore(listOf(
		Component.translatable("item.${Constants.MOD_ID}.echo_ingot.tooltip")
	))
	private val echoIngotProperties = Item.Properties().rarity(Rarity.UNCOMMON).fireResistant().component(DataComponents.LORE, echoIngotLore)
	val ECHO_INGOT = Items.registerItem(resourceKey("echo_ingot"), ::Item, echoIngotProperties)

	private var synthesizerLore = ItemLore(listOf(
		Component.translatable("block.${Constants.MOD_ID}.synthesizer.tooltip_1"),
		Component.translatable("block.${Constants.MOD_ID}.synthesizer.tooltip_2"),
		Component.translatable("block.${Constants.MOD_ID}.synthesizer.tooltip_3"),
		Component.translatable("block.${Constants.MOD_ID}.synthesizer.tooltip_4", Config.delay())
	))
	private val synthesizerProperties = Item.Properties().rarity(Rarity.EPIC).fireResistant().component(DataComponents.LORE, synthesizerLore)
	val SYNTHESIZER = Items.registerBlock(Blocks.SYNTHESIZER, synthesizerProperties)

	private val echoBlockLore = ItemLore(listOf(
		Component.translatable("block.${Constants.MOD_ID}.echo_block.tooltip")
	))
	private val echoBlockProperties = Item.Properties().rarity(Rarity.RARE).fireResistant().component(DataComponents.LORE, echoBlockLore)
	val ECHO_BLOCK = Items.registerBlock(Blocks.ECHO_BLOCK, echoBlockProperties)

	private val itemGroupResourceKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Constants.id("group"))
	val ITEM_GROUP = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, itemGroupResourceKey, FabricItemGroup.builder().title(Component.translatable("itemGroup.${Constants.MOD_ID}.group")).icon({ ItemStack(SYNTHESIZER) }).build())

	init {
		ItemGroupEvents.modifyEntriesEvent(itemGroupResourceKey).register({ itemGroup ->
			itemGroup.prepend(ECHO_FLOWER)
			itemGroup.prepend(ECHO_STAR)
			itemGroup.prepend(ECHO_INGOT)
			itemGroup.prepend(ECHO_BLOCK)
			itemGroup.prepend(TUNER)
			itemGroup.prepend(SYNTHESIZER)
		})
	}

	fun init() {}
}
