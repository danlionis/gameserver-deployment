package com.example.mixin;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCityGenerator.Piece.class)
public abstract class DisableElytra {

    @Inject(
            method = "handleMetadata",
            at = @At("HEAD"),
            cancellable = true
    )
    private void removeElytra(
            String metadata,
            BlockPos pos,
            ServerWorldAccess world,
            Random random,
            BlockBox boundingBox,
            CallbackInfo ci
    ) {
        if (metadata.startsWith("Elytra")) {
            // Instead of spawning Elytra, you could spawn a stick item frame or just skip entirely.
            ItemFrameEntity itemFrameEntity = new ItemFrameEntity(world.toServerWorld(), pos, Direction.SOUTH);
            itemFrameEntity.setHeldItemStack(new ItemStack(Items.STICK), false);
            world.spawnEntity(itemFrameEntity);

            // Cancel so vanilla Elytra placement never runs.
            ci.cancel();
        }
    }
}
