package com.Shinusei.DimPrison.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.Shinusei.DimPrison.DimPrison;

@Pseudo
@Mixin(targets = "serverutils.lib.math.TeleporterDimPos", remap = false)
public abstract class ServerUtilitiesTeleportMixin {

    @Shadow
    @Final
    public int dim;

    @Inject(method = "teleport", at = @At("HEAD"), cancellable = true, remap = false)
    private void dimprison$blockCrossDimensionTeleport(Entity entity, CallbackInfoReturnable<Entity> cir) {
        if (DimPrison.dimensionLockEnabled && entity instanceof EntityPlayerMP player && dim != player.dimension) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Dimension travel is disabled."));
            cir.setReturnValue(player);
        }
    }
}
