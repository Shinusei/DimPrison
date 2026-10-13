package com.Shinusei.DimPrison.mixins;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandSetDefaultSpawnpoint;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.Shinusei.DimPrison.DimensionSpawnPoints;

@Mixin(CommandSetDefaultSpawnpoint.class)
public abstract class SetWorldSpawnDimensionMixin {

    @Inject(method = "getCommandUsage", at = @At("HEAD"), cancellable = true)
    private void dimprison$showDimensionUsage(ICommandSender sender, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("/setworldspawn [x y z] | /setworldspawn <dimensionId> [x y z]");
    }

    @Inject(method = "processCommand", at = @At("HEAD"), cancellable = true)
    private void dimprison$setSpawnInDimension(ICommandSender sender, String[] args, CallbackInfo ci) {
        if (args.length != 1 && args.length != 4) {
            return;
        }

        int dimensionId = CommandBase.parseInt(sender, args[0]);
        if (!DimensionManager.isDimensionRegistered(dimensionId)) {
            throw new CommandException("Dimension " + dimensionId + " is not registered.");
        }

        WorldServer world = MinecraftServer.getServer()
            .worldServerForDimension(dimensionId);
        ChunkCoordinates spawn;
        if (args.length == 4) {
            int x = CommandBase.parseIntBounded(sender, args[1], -30000000, 30000000);
            int y = CommandBase.parseIntBounded(sender, args[2], 0, 255);
            int z = CommandBase.parseIntBounded(sender, args[3], -30000000, 30000000);
            spawn = new ChunkCoordinates(x, y, z);
        } else if (sender instanceof EntityPlayerMP) {
            spawn = sender.getPlayerCoordinates();
        } else {
            spawn = world.getSpawnPoint();
        }

        DimensionSpawnPoints.set(dimensionId, spawn);
        DimensionSpawnPoints.setRespawnDimensionId(dimensionId);
        sender.addChatMessage(
            new ChatComponentText(
                "World spawn set for dimension " + dimensionId
                    + " at "
                    + spawn.posX
                    + ", "
                    + spawn.posY
                    + ", "
                    + spawn.posZ
                    + "."));
        ci.cancel();
    }

    @Redirect(
        method = "processCommand",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setSpawnLocation(III)V"))
    private void dimprison$setSpawnForCurrentDimension(World world, int x, int y, int z) {
        int dimensionId = world.provider.dimensionId;
        DimensionSpawnPoints.set(dimensionId, new ChunkCoordinates(x, y, z));
        DimensionSpawnPoints.setRespawnDimensionId(dimensionId);
    }
}
