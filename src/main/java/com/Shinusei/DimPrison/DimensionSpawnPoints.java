package com.Shinusei.DimPrison;

import java.io.File;

import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public final class DimensionSpawnPoints {

    private static Configuration configuration;
    private static int respawnDimensionId;

    private DimensionSpawnPoints() {}

    public static void initialize(File configFile) {
        configuration = new Configuration(configFile);
        configuration.load();
        respawnDimensionId = configuration.getInt(
            "respawnDimension",
            Configuration.CATEGORY_GENERAL,
            0,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Dimension players respawn in.");
        configuration.save();
    }

    public static int getRespawnDimensionId() {
        if (!DimensionManager.isDimensionRegistered(respawnDimensionId)) {
            throw new IllegalStateException(
                "DimPrison respawn dimension " + respawnDimensionId + " is not registered.");
        }
        return respawnDimensionId;
    }

    public static void setRespawnDimensionId(int dimensionId) {
        if (configuration == null) {
            throw new IllegalStateException("DimPrison configuration has not been initialized.");
        }
        if (!DimensionManager.isDimensionRegistered(dimensionId)) {
            throw new IllegalArgumentException("Dimension " + dimensionId + " is not registered.");
        }

        respawnDimensionId = dimensionId;
        configuration.get(Configuration.CATEGORY_GENERAL, "respawnDimension", 0, "Dimension players respawn in.")
            .set(dimensionId);
        configuration.save();
    }

    public static ChunkCoordinates get(int dimensionId) {
        if (configuration == null) {
            return null;
        }

        Property property = configuration.get("dimensionSpawnPoints", Integer.toString(dimensionId), "");
        String value = property.getString();
        if (value.isEmpty()) {
            return null;
        }

        String[] coordinates = value.split(",", -1);
        if (coordinates.length != 3) {
            throw new IllegalStateException(
                "Invalid DimPrison spawn point for dimension " + dimensionId + ": " + value);
        }

        try {
            return new ChunkCoordinates(
                Integer.parseInt(coordinates[0]),
                Integer.parseInt(coordinates[1]),
                Integer.parseInt(coordinates[2]));
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                "Invalid DimPrison spawn point for dimension " + dimensionId + ": " + value,
                exception);
        }
    }

    public static void set(int dimensionId, ChunkCoordinates spawnPoint) {
        if (configuration == null) {
            throw new IllegalStateException("DimPrison configuration has not been initialized.");
        }

        configuration.get("dimensionSpawnPoints", Integer.toString(dimensionId), "")
            .set(spawnPoint.posX + "," + spawnPoint.posY + "," + spawnPoint.posZ);
        configuration.save();
    }
}
