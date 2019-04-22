/*
 * Copyright 2019 Chocohead
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.chocohead.clm.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;

import com.jamieswhiteshirt.clotheslinefabric.common.block.ClotheslineAnchorBlock;

import com.chocohead.clm.blocks.MotorBlock;

@Mixin(value = ClotheslineAnchorBlock.class, remap = false)
public abstract class AnchorBlockMixin extends WallMountedBlock {
	private AnchorBlockMixin() {
		super(null);
	}

	@Inject(method = "canPlaceAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;isSolidSmallSquare(Lnet/minecraft/world/ViewableWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z", remap = true), remap = true, cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void canPlaceOnMotor(BlockState state, ViewableWorld world, BlockPos pos, CallbackInfoReturnable<Boolean> callback, Direction direction, BlockPos neighbourPosition) {
		if (world.getBlockState(neighbourPosition).getBlock() instanceof MotorBlock) callback.setReturnValue(true);
	}
}