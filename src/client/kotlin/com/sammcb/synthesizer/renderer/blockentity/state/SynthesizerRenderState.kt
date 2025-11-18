package com.sammcb.synthesizer.renderer.blockentity.state

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState

class SynthesizerRenderState: BlockEntityRenderState() {
	var displayItem: ItemClusterRenderState? = null
	var spin = 0f
	var height = 0f
	var lightColor = 0
}
