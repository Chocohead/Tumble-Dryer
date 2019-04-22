package com.chocohead.clm;

import java.util.Collections;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;

public class NotFloodedLootCondition implements LootCondition {
	private static final NotFloodedLootCondition INSTANCE = new NotFloodedLootCondition();

	public static class Factory extends LootCondition.Factory<NotFloodedLootCondition> {
		public Factory() {
			super(ClothesLineMotor.NOT_FLOODED.getIdentifier(), NotFloodedLootCondition.class);
		}

		@Override
		public void toJson(JsonObject json, NotFloodedLootCondition condition, JsonSerializationContext context) {
		}

		@Override
		public NotFloodedLootCondition fromJson(JsonObject json, JsonDeserializationContext context) {
			return NotFloodedLootCondition.INSTANCE;
		}
	}

	@Override
	public boolean test(LootContext context) {
		return context.hasParameter(ClothesLineMotor.NOT_FLOODED);
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return Collections.singleton(ClothesLineMotor.NOT_FLOODED);
	}
}
