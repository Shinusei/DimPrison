package com.Shinusei.DimPrison.mixins;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.Teleporter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerConfigurationManager.class)
public abstract class DimensionTransferMixin {

    @Inject(
        method = "transferPlayerToDimension(Lnet/minecraft/entity/player/EntityPlayerMP;I)V",
        at = @At("HEAD"),
        cancellable = true)
    private void preventPlayerDimensionTransfer(EntityPlayerMP player, int targetDimension, CallbackInfo ci) {
        dimprison$preventDimensionChange(player, targetDimension, ci);
    }

    @Inject(
        method = "transferPlayerToDimension(Lnet/minecraft/entity/player/EntityPlayerMP;ILnet/minecraft/world/Teleporter;)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false)
    private void preventPlayerDimensionTransferWithTeleporter(EntityPlayerMP player, int targetDimension,
        Teleporter teleporter, CallbackInfo ci) {
        dimprison$preventDimensionChange(player, targetDimension, ci);
    }

    @Unique
    private static void dimprison$preventDimensionChange(EntityPlayerMP player, int targetDimension, CallbackInfo ci) {
        if (targetDimension != player.dimension) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Dimension travel is disabled."));
            ci.cancel();
        }
    }
}
