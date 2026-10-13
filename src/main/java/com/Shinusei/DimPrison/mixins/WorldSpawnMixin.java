package com.Shinusei.DimPrison.mixins;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.Shinusei.DimPrison.DimensionSpawnPoints;

@Mixin(ServerConfigurationManager.class)
public class WorldSpawnMixin {

    @ModifyVariable(
        method = "respawnPlayer(Lnet/minecraft/entity/player/EntityPlayerMP;IZ)Lnet/minecraft/entity/player/EntityPlayerMP;",
        at = @At("HEAD"),
        argsOnly = true,
        name = "dimension")
    private int dimprison$respawnInConfiguredDimension(int dimension) {
        return DimensionSpawnPoints.getRespawnDimensionId();
    }

    @Redirect(
        method = "respawnPlayer(Lnet/minecraft/entity/player/EntityPlayerMP;IZ)Lnet/minecraft/entity/player/EntityPlayerMP;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/WorldProvider;getRespawnDimension(Lnet/minecraft/entity/player/EntityPlayerMP;)I"),
        remap = false)
    private int dimprison$keepRespawnInConfiguredDimension(WorldProvider provider, EntityPlayerMP player) {
        return DimensionSpawnPoints.getRespawnDimensionId();
    }
}
