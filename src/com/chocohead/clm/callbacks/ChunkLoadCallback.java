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