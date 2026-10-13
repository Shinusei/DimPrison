package com.Shinusei.DimPrison;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
public class CommandToggleDimLock extends CommandBase {
    @Override
    public String getCommandName() {
        return "toggledimlock";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/toggledimlock <true|false>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: " + getCommandUsage(sender)));
            return;
        }

        boolean choice = Boolean.parseBoolean(args[0]);

        if (choice) {
            sender.addChatMessage(new ChatComponentText("Dimensional travel disabled"));
            DimPrison.dimensionLockEnabled = true;
        } else {
            sender.addChatMessage(new ChatComponentText("Dimensional travel enabled"));
            DimPrison.dimensionLockEnabled = false;
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
}
