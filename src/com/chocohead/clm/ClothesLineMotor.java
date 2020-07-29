/*
 * Copyright 2019 Chocohead
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.chocohead.clm;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Settings;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

import com.jamieswhiteshirt.clothesline.common.item.ClotheslineItemGroups;

import com.chocohead.clm.blocks.MotorBlock;
import com.chocohead.clm.blocks.MotorBlock.Status;

public class ClothesLineMotor implements ModInitializer {
	public static final MotorBlock MOTOR = new MotorBlock();

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("tumble_dryer", "motor"), MOTOR);
		Registry.register(Registry.ITEM, new Identifier("tumble_dryer", "motor"), new BlockItem(MOTOR, new Settings().group(ClotheslineItemGroups.ITEMS)));
		Registry.register(Registry.ITEM, new Identifier("tumble_dryer", "broken_motor"), new Item(new Settings().group(ClotheslineItemGroups.ITEMS) ) {
			@Override
			@Environment(EnvType.CLIENT)
			public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext options) {
				tooltip.add(new TranslatableText("item.tumble_dryer.broken_motor.disclaimer").styled(style -> style.withColor(Formatting.GRAY)));
			}
		});

		Registry.register(Registry.ITEM, new Identifier("tumble_dryer", "motor_casing"), new Item(new Settings().group(ClotheslineItemGroups.ITEMS)));
		Registry.register(Registry.ITEM, new Identifier("tumble_dryer", "coil"), new Item(new Settings().group(ClotheslineItemGroups.ITEMS)) {
			@Override
			@Environment(EnvType.CLIENT)
			public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext options) {
				tooltip.add(new TranslatableText("item.tumble_dryer.coil.disclaimer").styled(style -> style.withColor(Formatting.GRAY)));
			}
		});

		ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
			Mutable relativePos = new Mutable(chunk.getPos().getStartX(), 0, chunk.getPos().getStartZ());

			for (ChunkSection section : chunk.getSectionArray()) {
				if (!ChunkSection.isEmpty(section) && section.method_19523(state -> state.isOf(MOTOR) && state.get(MotorBlock.STATUS) == Status.ON)) {
					for (int x = 0; x < 16; x++) {
						for (int y = 0; y < 16; y++) {
							for (int z = 0; z < 16; z++) {
								BlockState state = section.getBlockState(x, y, z);

								if (state.isOf(MOTOR) && state.get(MotorBlock.STATUS) == Status.ON) {
									BlockPos realPos = relativePos.add(x, y, z);

									world.getBlockTickScheduler().schedule(realPos, MOTOR, MotorBlock.getTickRate(world));
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