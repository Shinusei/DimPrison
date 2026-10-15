package com.Shinusei.DimPrison.mixins;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.Shinusei.DimPrison.DimPrison;

@Mixin(EntityPlayerMP.class)
public abstract class DimensionPrisonMixin {

    @Inject(method = "travelToDimension(I)V", at = @At("HEAD"), cancellable = true)
    private void onTravelToDimension(int targetDimId, CallbackInfo ci) {
        if (DimPrison.dimensionLockEnabled) {
            EntityPlayerMP player = (EntityPlayerMP) (Object) this;

            if (targetDimId != player.dimension || (player.dimension == 1 && targetDimId == 1)) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Dimension travel is disabled."));
                ci.cancel();
            }
        }
    }
}
