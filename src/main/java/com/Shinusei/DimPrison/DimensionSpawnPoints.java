package com.Shinusei.DimPrison;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public final class DimensionSpawnPoints {

    private static final String DATA_NAME = DimPrison.MODID + "_spawn_points";
    private static final Map<Integer, ChunkCoordinates> legacySpawnPoints = new HashMap<>();

    private static Configuration legacyConfiguration;
    private static int legacyRespawnDimensionId;
    private static boolean legacyMigrationComplete;

    private DimensionSpawnPoints() {}

    public static void initialize(File configFile) {
        legacyConfiguration = new Configuration(configFile);
        legacyConfiguration.load();
        legacyMigrationComplete = legacyConfiguration
            .get(Configuration.CATEGORY_GENERAL, "worldDataMigrationComplete", false)
            .getBoolean(false);

        if (!legacyMigrationComplete) {
            legacyRespawnDimensionId = legacyConfiguration.getInt(
                "respawnDimension",
                Configuration.CATEGORY_GENERAL,
                0,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                "Legacy respawn dimension, imported into the first world opened.");
            for (String dimensionKey : legacyConfiguration.getCategory("dimensionSpawnPoints")
                .keySet()) {
                Property property = legacyConfiguration.get("dimensionSpawnPoints", dimensionKey, "");
                String value = property.getString();
                if (!value.isEmpty()) {
                    legacySpawnPoints.put(Integer.parseInt(dimensionKey), parseSpawnPoint(dimensionKey, value));
                }
            }
        }
    }

    public static int getRespawnDimensionId() {
        SpawnData data = getData();
        int dimensionId = data == null ? 0 : data.respawnDimensionId;
        if (!DimensionManager.isDimensionRegistered(dimensionId)) {
            throw new IllegalStateException("DimPrison respawn dimension " + dimensionId + " is not registered.");
        }
        return dimensionId;
    }

    public static void setRespawnDimensionId(int dimensionId) {
        if (!DimensionManager.isDimensionRegistered(dimensionId)) {
            throw new IllegalArgumentException("Dimension " + dimensionId + " is not registered.");
        }

        SpawnData data = getRequiredData();
        data.respawnDimensionId = dimensionId;
        data.markDirty();
    }

    public static ChunkCoordinates get(int dimensionId) {
        SpawnData data = getData();
        return data == null ? null : data.spawnPoints.get(dimensionId);
    }

    public static void set(int dimensionId, ChunkCoordinates spawnPoint) {
        getRequiredData().setSpawnPoint(dimensionId, spawnPoint);
    }

    private static SpawnData getRequiredData() {
        SpawnData data = getData();
        if (data == null) {
            throw new IllegalStateException("DimPrison world data is not available.");
        }
        return data;
    }

    private static SpawnData getData() {
        WorldServer overworld = DimensionManager.getWorld(0);
        if (overworld == null) {
            return null;
        }

        MapStorage storage = overworld.mapStorage;
        SpawnData data = (SpawnData) storage.loadData(SpawnData.class, DATA_NAME);
        if (data != null) {
            return data;
        }

        data = new SpawnData(DATA_NAME);
        if (!legacyMigrationComplete) {
            data.respawnDimensionId = legacyRespawnDimensionId;
            data.spawnPoints.putAll(legacySpawnPoints);
            data.markDirty();

            legacyMigrationComplete = true;
            legacyConfiguration.get(Configuration.CATEGORY_GENERAL, "worldDataMigrationComplete", false)
                .set(true);
            legacyConfiguration.save();
        }
        storage.setData(DATA_NAME, data);
        return data;
    }

    private static ChunkCoordinates parseSpawnPoint(String dimensionId, String value) {
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

    public static class SpawnData extends WorldSavedData {

        private static final String RESPAWN_DIMENSION_TAG = "respawnDimension";
        private static final String SPAWN_POINTS_TAG = "spawnPoints";

        private final Map<Integer, ChunkCoordinates> spawnPoints = new HashMap<>();
        private int respawnDimensionId;

        public SpawnData(String name) {
            super(name);
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            respawnDimensionId = compound.getInteger(RESPAWN_DIMENSION_TAG);
            NBTTagList points = compound.getTagList(SPAWN_POINTS_TAG, 10);
            for (int index = 0; index < points.tagCount(); index++) {
                NBTTagCompound point = points.getCompoundTagAt(index);
                spawnPoints.put(
                    point.getInteger("dimension"),
                    new ChunkCoordinates(point.getInteger("x"), point.getInteger("y"), point.getInteger("z")));
            }
        }

        @Override
        public void writeToNBT(NBTTagCompound compound) {
            compound.setInteger(RESPAWN_DIMENSION_TAG, respawnDimensionId);
            NBTTagList points = new NBTTagList();
            for (Map.Entry<Integer, ChunkCoordinates> entry : spawnPoints.entrySet()) {
                NBTTagCompound point = new NBTTagCompound();
                point.setInteger("dimension", entry.getKey());
                point.setInteger("x", entry.getValue().posX);
                point.setInteger("y", entry.getValue().posY);
                point.setInteger("z", entry.getValue().posZ);
                points.appendTag(point);
            }
            compound.setTag(SPAWN_POINTS_TAG, points);
        }

        private void setSpawnPoint(int dimensionId, ChunkCoordinates spawnPoint) {
            spawnPoints.put(dimensionId, spawnPoint);
            markDirty();
        }
    }
}
