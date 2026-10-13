package com.leclowndu93150.heavier_weapons.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityInvoker {

    @Invoker("blockedByShield")
    void heavier_weapons$blockedByShield(LivingEntity defender);
}
