package com.Shinusei.DimPrison;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = DimPrison.MODID, version = DimPrison.VERSION, acceptableRemoteVersions = "*")
public class DimPrison {

    public static final String MODID = "dimprison";
    public static final String VERSION = "0.0.1";
    public static boolean dimensionLockEnabled = true;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        DimensionSpawnPoints.initialize(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandToggleDimLock());
    }
}
