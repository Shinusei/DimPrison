package com.Shinusei.DimPrison.mixins;

import com.Shinusei.DimPrison.DimPrison;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "micdoodle8.mods.galacticraft.core.util.WorldUtil", remap = false)
public abstract class GalacticraftTravelMixin {

    @Inject(method = "toCelestialSelection", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void preventGalacticraftCelestialSelection(EntityPlayerMP player, @Coerce Object playerStats,
        int rocketTier, @Coerce Object mapMode, CallbackInfo ci) {
        if (DimPrison.dimensionLockEnabled) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Dimension travel is disabled."));
            ci.cancel();
        }
    }

    @Inject(method = "teleportEntity", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preventGalacticraftDimensionTravel(@Coerce Object targetWorld, Entity entity,
        int targetDimension, @Coerce Object teleportType, boolean transferInventory, @Coerce Object ridingRocket,
        CallbackInfoReturnable<Entity> cir) {
        if (DimPrison.dimensionLockEnabled) {
            if (entity instanceof EntityPlayerMP) {
                ((EntityPlayerMP) entity)
                    .addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Dimension travel is disabled."));
                cir.setReturnValue(null);
            }
        }
    }
}
