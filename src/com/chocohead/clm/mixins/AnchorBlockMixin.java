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
import net.minecraft.world.WorldView;

import com.jamieswhiteshirt.clothesline.common.block.ClotheslineAnchorBlock;

import com.chocohead.clm.blocks.MotorBlock;

@Mixin(value = ClotheslineAnchorBlock.class, remap = false)
public abstract class AnchorBlockMixin extends WallMountedBlock {
	private AnchorBlockMixin() {
		super(null);
	}

	@Inject(method = "canPlaceAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;sideCoversSmallSquare(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z", remap = true), remap = true, cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void canPlaceOnMotor(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> callback, Direction direction, BlockPos neighbourPosition) {
		//Block#isSolidSmallSquare needs a 4x10x4 shape from the given side, but our motor only has 2x16x2 thus we lie
		if (world.getBlockState(neighbourPosition).getBlock() instanceof MotorBlock) callback.setReturnValue(true);
	}
}