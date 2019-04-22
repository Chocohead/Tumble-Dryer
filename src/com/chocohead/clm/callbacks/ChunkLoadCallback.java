/*
 * Copyright 2019 Chocohead
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.chocohead.clm.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

@FunctionalInterface
public interface ChunkLoadCallback {
	Event<ChunkLoadCallback> EVENT = EventFactory.createArrayBacked(ChunkLoadCallback.class, (listeners) -> (world, chunk, pos) -> {
		for (ChunkLoadCallback callback : listeners) {
			callback.accept(world, chunk, pos);
		}
	});

	void accept(World world, WorldChunk chunk, ChunkPos chunkPos);
}