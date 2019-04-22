package com.chocohead.clm.mixins;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.loot.context.LootContextType;
import net.minecraft.world.loot.context.LootContextType.Builder;
import net.minecraft.world.loot.context.LootContextTypes;

@Mixin(LootContextTypes.class)
public interface LootContextTypesAccessor {
	@Invoker
	static LootContextType getRegister(String name, Consumer<Builder> type) {
		return null; //Mixin is very lazy for resolving @Invokers essentially like @Accessors, hence it needs a get :|
	}
}