/*
 * Copyright 2019 Chocohead
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.chocohead.clm.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.VersionedChunkStorage;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import com.chocohead.clm.callbacks.ChunkLoadCallback;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin extends VersionedChunkStorage {
	@Shadow
	private @Final ServerWorld world;

	private ThreadedAnvilChunkStorageMixin() {
		super(null, null);
	}

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/chunk/WorldChunk;setLoadedToWorld(Z)V",
					shift = At.Shift.AFTER
					),
			method = "method_17227(Lnet/minecraft/server/world/ChunkHolder;Lnet/minecraft/world/chunk/Chunk;)Lnet/minecraft/world/chunk/Chunk;", //Double nested lambda in convertToFullChunk
			locals = LocalCapture.CAPTURE_FAILHARD
			)
	private void method_17227(ChunkHolder holder, Chunk rawChunk, CallbackInfoReturnable<Chunk> callback, ChunkPos pos, WorldChunk chunk) {
		assert world == chunk.getWorld();
		assert holder.getPos() == pos;
		ChunkLoadCallback.EVENT.invoker().accept(world, chunk, pos);
	}
}