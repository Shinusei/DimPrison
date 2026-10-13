package com.Shinusei.DimPrison.mixins;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.Shinusei.DimPrison.DimensionSpawnPoints;

@Mixin(WorldProvider.class)
public abstract class WorldSpawnPointMixin {

    @Inject(method = "getSpawnPoint", at = @At("HEAD"), cancellable = true, remap = false)
    private void dimprison$getDimensionSpecificSpawnPoint(CallbackInfoReturnable<ChunkCoordinates> cir) {
        WorldProvider provider = (WorldProvider) (Object) this;
        ChunkCoordinates spawn = DimensionSpawnPoints.get(provider.dimensionId);
        if (spawn != null) {
            cir.setReturnValue(spawn);
        }
    }
}
