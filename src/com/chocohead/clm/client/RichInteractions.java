package com.chocohead.clm.client;

import com.chocohead.clm.ClothesLineMotor;
import com.chocohead.clm.blocks.MotorBlock;
import com.jamieswhiteshirt.clothesline.api.client.RichInteractionRegistry;
import com.jamieswhiteshirt.clothesline.api.client.RichInteractionType;

public class RichInteractions implements RichInteractionRegistry.Consumer {
    @Override
    public void accept(RichInteractionRegistry registry) {
        registry.addBlock(ClothesLineMotor.MOTOR, (state, world, pos, player, hitResult) -> {
            MotorBlock.Status status = state.get(MotorBlock.STATUS);
            if (status == MotorBlock.Status.ON || status == MotorBlock.Status.OFF) {
                MotorBlock.Rotation interactionRotation = MotorBlock.getInteractionRotation(pos, hitResult.getPos().x, hitResult.getPos().z, player);
                return interactionRotation == MotorBlock.Rotation.CLOCKWISE ? RichInteractionType.ROTATE_CLOCKWISE : RichInteractionType.ROTATE_COUNTER_CLOCKWISE;
            }
            return RichInteractionType.NONE;
        });
    }
}
