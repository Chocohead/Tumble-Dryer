/*
 * Copyright 2019 Chocohead
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.chocohead.clm;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Settings;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

import com.jamieswhiteshirt.clotheslinefabric.common.item.ClotheslineItemGroups;

import com.chocohead.clm.blocks.MotorBlock;
import com.chocohead.clm.blocks.MotorBlock.Status;
import com.chocohead.clm.callbacks.ChunkLoadCallback;

public class ClothesLineMotor implements ModInitializer {
	public static final Block MOTOR = new MotorBlock();

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("clothesline_motor", "motor"), MOTOR);
		Registry.register(Registry.ITEM, new Identifier("clothesline_motor", "motor"), new BlockItem(MOTOR, new Settings().itemGroup(ClotheslineItemGroups.ITEMS)));
		Registry.register(Registry.ITEM, new Identifier("clothesline_motor", "broken_motor"), new Item(new Settings().itemGroup(ClotheslineItemGroups.ITEMS) ) {
			@Override
			@Environment(EnvType.CLIENT)
			public void buildTooltip(ItemStack stack, World world, List<TextComponent> tooltip, TooltipContext options) {
				tooltip.add(new TranslatableTextComponent("item.clothesline_motor.broken_motor.disclaimer").applyFormat(TextFormat.GRAY));
			}
		});

		Registry.register(Registry.ITEM, new Identifier("clothesline_motor", "motor_casing"), new Item(new Settings().itemGroup(ClotheslineItemGroups.ITEMS)));
		Registry.register(Registry.ITEM, new Identifier("clothesline_motor", "coil"), new Item(new Settings().itemGroup(ClotheslineItemGroups.ITEMS)) {
			@Override
			@Environment(EnvType.CLIENT)
			public void buildTooltip(ItemStack stack, World world, List<TextComponent> tooltip, TooltipContext options) {
				tooltip.add(new TranslatableTextComponent("item.clothesline_motor.coil.disclaimer").applyFormat(TextFormat.GRAY));
			}
		});

		ChunkLoadCallback.EVENT.register((world, chunk, pos) -> {
			if (world.isClient) return;

			BlockState activeUpMotor = MOTOR.getDefaultState().with(MotorBlock.STATUS, Status.ON).with(MotorBlock.FACING, Direction.UP);
			BlockState activeDownMotor = MOTOR.getDefaultState().with(MotorBlock.STATUS, Status.ON).with(MotorBlock.FACING, Direction.DOWN);
			Mutable relativePos = new Mutable(pos.getStartX(), 0, pos.getStartZ());

			for (ChunkSection section : chunk.getSectionArray()) {
				if (!ChunkSection.isEmpty(section) && (section.method_19523(activeUpMotor) || section.method_19523(activeDownMotor))) {
					for (int x = 0; x < 16; x++) {
						for (int y = 0; y < 16; y++) {
							for (int z = 0; z < 16; z++) {
								BlockState state = section.getBlockState(x, y, z);

								if (state.getBlock() == MOTOR && state.get(MotorBlock.STATUS) == Status.ON) {
									BlockPos realPos = relativePos.add(x, y, z);

									world.getBlockTickScheduler().schedule(realPos, MOTOR, MOTOR.getTickRate(world));
								}
							}
						}
					}
				}

				relativePos.setY(relativePos.getY() + 16);
			}
		});
	}
}