# DimPrison

A Minecraft 1.7.10 mod for controlling dimension travel and world spawn points.

## Commands

- `/toggledimlock true|false` — enable or disable the dimension travel lock.
- `/setworldspawn <dimensionId> [x y z]` — set the spawn point for a dimension. Omit coordinates to use your current position; from the console, it keeps that dimension's current spawn coordinates.

DimPrison stores its respawn dimension and dimension-specific spawn points per world. When ServerUtilities is installed, `/spawn` uses the dimension and spawn point configured with `/setworldspawn` as well.
