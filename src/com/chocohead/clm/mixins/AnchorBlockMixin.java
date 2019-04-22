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
	//private static final VoxelShape tinyStick = createCuboidShape(7D, 0D, 7D, 9D, 3D, 9D);

	private AnchorBlockMixin() {
		super(null);
	}

	/*@Redirect(method = "canPlaceAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;isSolidSmallSquare(Lnet/minecraft/world/ViewableWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"))
	private boolean canReallyPlace(ViewableWorld world, BlockPos pos, Direction side) {
		BlockState blockState_1 = world.getBlockState(pos);
		System.out.println("Tinkering on " + side + ", would've normally returned " + Block.isSolidSmallSquare(world, pos, side));
		return !blockState_1.matches(BlockTags.LEAVES) && !VoxelShapes.matchesAnywhere(blockState_1.getCollisionShape(world, pos).getFace(side), tinyStick, BooleanBiFunction.ONLY_SECOND);
	}*/

	@Inject(method = "canPlaceAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;isSolidSmallSquare(Lnet/minecraft/world/ViewableWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z", remap = true), remap = true, cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void canPlaceOnMotor(BlockState state, ViewableWorld world, BlockPos pos, CallbackInfoReturnable<Boolean> callback, Direction direction, BlockPos neighbourPosition) {
		if (world.getBlockState(neighbourPosition).getBlock() instanceof MotorBlock) callback.setReturnValue(true);
	}
}