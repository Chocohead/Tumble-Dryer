package com.chocohead.clm.blocks;

import java.util.Locale;
import java.util.Random;

import com.jamieswhiteshirt.clotheslinefabric.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkNode;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkState;
import com.jamieswhiteshirt.clotheslinefabric.common.item.ClotheslineItems;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tag.FabricItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class MotorBlock extends Block implements Waterloggable {
	public enum Status implements StringRepresentable {
		OFF, ON, BUST, FLOODED;

		@Override
		public String asString() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.UP, Direction.DOWN);
	public static final EnumProperty<Status> STATUS = EnumProperty.create("status", Status.class);
	private static final VoxelShape UP_SHAPE = VoxelShapes.union(Block.createCuboidShape(4, 0, 4, 12, 13, 12), Block.createCuboidShape(3, 1, 3, 13, 10, 13), Block.createCuboidShape(7, 13, 7, 9, 16, 9));
	private static final VoxelShape DOWN_SHAPE = VoxelShapes.union(Block.createCuboidShape(4, 3, 4, 12, 16, 12), Block.createCuboidShape(3, 5, 3, 13, 15, 13), Block.createCuboidShape(7, 0, 7, 9, 3, 9));

	public MotorBlock() {
		super(FabricBlockSettings.copy(Blocks.IRON_BLOCK).breakByTool(FabricItemTags.PICKAXES, 1).build());

		setDefaultState(stateFactory.getDefaultState().with(FACING, Direction.UP).with(STATUS, Status.OFF));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> states) {
		states.with(FACING, STATUS);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, VerticalEntityPosition entity) {
		switch (state.get(FACING)) {
		case UP:
			return UP_SHAPE;

		case DOWN:
			return DOWN_SHAPE;

		default:
			throw new IllegalStateException("Unexpected facing: " + state.get(FACING));
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, VerticalEntityPosition entity) {
		return getCollisionShape(state, world, pos, entity);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayer().pitch > 0 ? Direction.UP : Direction.DOWN);
	}

	@Override
	public int getTickRate(ViewableWorld world) {
		return 2;
	}

	@Override
	public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (state.get(STATUS) == Status.ON) {
			BlockPos connected = pos.offset(state.get(FACING));
			NetworkNode node = ((NetworkManagerProvider) world).getNetworkManager().getNetworks().getNodes().get(connected);

			int nextUpdate = getTickRate(world);
			if (node != null) {
				NetworkState networkState = node.getNetwork().getState();
				int momentum = networkState.getMomentum();

				if (momentum < 15) {//Add on some momentum so long as it's not going to be faster than 50
					networkState.setMomentum(momentum + nextUpdate);

					if (!world.isClient && world.random.nextBoolean()) {
						switch (state.get(FACING)) {
						case UP:
							((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE, pos.getX() + 8D/16, pos.getY() + 6D/16, pos.getZ() + 4D/16, 1, 0, 0D, -2D/16, 0);
							break;

						case DOWN:
							((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE, pos.getX() + 8D/16, pos.getY() + 10D/16, pos.getZ() + 12D/16, 1, 0, 0D, +2D/16, 0);
							break;

						default:
							throw new IllegalStateException("Unexpected facing: " + state.get(FACING));
						}
					}
					if (momentum + nextUpdate < 14) nextUpdate = 1;
				}
			}

			world.getBlockTickScheduler().schedule(pos, this, nextUpdate);
		}
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (player.getStackInHand(hand).getItem() == ClotheslineItems.CLOTHESLINE_ANCHOR) return false;
		if (world.isClient) return true;

		switch (state.get(STATUS)) {
		case OFF:
			world.setBlockState(pos, state.with(STATUS, Status.ON));
			world.getBlockTickScheduler().schedule(pos, this, getTickRate(world));
			player.sendMessage(new TranslatableTextComponent(getTranslationKey() + ".on"));
			return true;

		case ON:
			world.setBlockState(pos, state.with(STATUS, Status.OFF));
			player.sendMessage(new TranslatableTextComponent(getTranslationKey() + ".off"));
			return true;

		case BUST:
			//world.playLevelEvent(1031, pos, 0); //Float params are volume and pitch
			world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.BLOCKS, 1F, 0.8F + 0.4F * world.random.nextFloat());
			player.sendMessage(new TranslatableTextComponent(getTranslationKey() + ".ruined"));
			return true;

		case FLOODED:
			if (player.getStackInHand(hand).getItem() == Items.BUCKET) return false;
			world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 1F, 0.8F + 0.4F * world.random.nextFloat());
			player.sendMessage(new TranslatableTextComponent(getTranslationKey() + ".flooded"));
			return true;

		default:
			throw new IllegalStateException("Unexpected status " + state.get(STATUS));
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(STATUS) == Status.FLOODED ? Fluids.WATER.getStill(false) : Fluids.EMPTY.getDefaultState();
	}

	@Override
	public boolean canFillWithFluid(BlockView view, BlockPos pos, BlockState state, Fluid fluid) {
		return state.get(STATUS) != Status.FLOODED && fluid == Fluids.WATER;
	}

	@Override
	public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
		if (state.get(STATUS) != Status.FLOODED && fluidState.getFluid() == Fluids.WATER) {
			if (!world.isClient()) {
				if (state.get(STATUS) == Status.ON) world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1F, 0.8F + 0.4F * world.getRandom().nextFloat());
				world.setBlockState(pos, state.with(STATUS, Status.FLOODED), 3);
				world.getFluidTickScheduler().schedule(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
		if (state.get(STATUS) == Status.FLOODED) {
			world.setBlockState(pos, state.with(STATUS, Status.BUST), 3);
			return Fluids.WATER;
		} else {
			return Fluids.EMPTY;
		}
	}
}