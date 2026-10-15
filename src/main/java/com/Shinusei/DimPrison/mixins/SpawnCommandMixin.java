package com.Shinusei.DimPrison.mixins;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.Shinusei.DimPrison.DimPrison;
import com.Shinusei.DimPrison.DimensionSpawnPoints;

import serverutils.lib.math.BlockDimPos;
import serverutils.lib.math.TeleporterDimPos;

@Pseudo
@Mixin(targets = "serverutils.command.tp.CmdSpawn", remap = false)
public abstract class SpawnCommandMixin {

    @Inject(
        method = { "processCommand", "func_71515_b" },
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 0)
    private void dimprison$blockCrossDimensionSpawn(ICommandSender sender, String[] args, CallbackInfo ci) {
        if (!DimPrison.dimensionLockEnabled || !(sender instanceof EntityPlayerMP player)) {
            return;
        }

        if (player.dimension != DimensionSpawnPoints.getRespawnDimensionId()) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Dimension travel is disabled."));
            ci.cancel();
        }
    }

    @Inject(method = "getSpawnTeleporter", at = @At("HEAD"), cancellable = true, remap = false)
    private void dimprison$useConfiguredSpawn(CallbackInfoReturnable<TeleporterDimPos> cir) {
        int dimensionId = DimensionSpawnPoints.getRespawnDimensionId();
        WorldServer world = MinecraftServer.getServer()
            .worldServerForDimension(dimensionId);
        ChunkCoordinates spawn = world.getSpawnPoint();
        while (world.getBlock(spawn.posX, spawn.posY, spawn.posZ)
            .isNormalCube()) {
            spawn.posY += 2;
        }

        cir.setReturnValue(new BlockDimPos(spawn, dimensionId).teleporter());
    }
}
