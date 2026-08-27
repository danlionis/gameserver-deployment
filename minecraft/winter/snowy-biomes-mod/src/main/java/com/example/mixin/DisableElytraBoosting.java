package com.example.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public abstract class DisableElytraBoosting {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void disableBoost(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (user.isGliding() && !user.getGameMode().isCreative()) {
            // Cancel firework usage if gliding -> prevents FireworkRocketEntity from spawning
            user.sendMessage(Text.literal("¯\\_(ツ)_/¯"), true);
            cir.setReturnValue(ActionResult.PASS); // act like we didn't use it
        }
    }
}

