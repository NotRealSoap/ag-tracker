/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.BlockPos
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.MathHelper
 */
package org.polyfrost.example.module.modules;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public enum PitMapType {
    ABYSS("Abyss", 114, 20, 82, 9.9),
    GENESIS("Genesis", 86, 30, 43, 10.9),
    SEASONS("Seasons", 114, 20, 82, 9.9),
    ELEMENTS("Elements", 114, 20, 82, 9.9),
    CASTLE("Castle", 95, 20, 71, 9.9);

    private final String name;
    private final int spawnY;
    private final int spawnRadius;
    private final int floorY;
    private final double floorRadius;
    private final Set<BlockPos> circle;

    private PitMapType(String name, int spawnY, int spawnRadius, int floorY, double floorRadius) {
        this.name = name;
        this.spawnY = spawnY;
        this.spawnRadius = spawnRadius;
        this.floorY = floorY;
        this.floorRadius = floorRadius;
        this.circle = PitMapType.getCircle(floorY, floorRadius);
    }

    public static PitMapType fromName(String name) {
        for (PitMapType type : PitMapType.values()) {
            if (!type.getName().equalsIgnoreCase(name)) continue;
            return type;
        }
        return null;
    }

    private static Set<BlockPos> getCircle(int floorY, double radius) {
        HashSet<BlockPos> blocks = new HashSet<BlockPos>();
        for (int x = -MathHelper.func_76143_f((double)(radius + 1.0)); x <= MathHelper.func_76143_f((double)(radius + 1.0)); ++x) {
            for (int z = -MathHelper.func_76143_f((double)(radius + 1.0)); z <= MathHelper.func_76143_f((double)(radius + 1.0)); ++z) {
                double cx = (double)x - 0.5;
                double cz = (double)z - 0.5;
                if (!(cx * cx + cz * cz <= radius * radius)) continue;
                blocks.add(new BlockPos(x, floorY, z));
            }
        }
        HashSet<BlockPos> filtered = new HashSet<BlockPos>();
        block2: for (BlockPos pos : blocks) {
            for (EnumFacing facing : EnumFacing.field_176754_o) {
                BlockPos offset = pos.func_177972_a(facing);
                if (blocks.contains(offset)) continue;
                filtered.add(pos);
                continue block2;
            }
        }
        return filtered;
    }

    public String getName() {
        return this.name;
    }

    public int getSpawnY() {
        return this.spawnY;
    }

    public int getSpawnRadius() {
        return this.spawnRadius;
    }

    public int getFloorY() {
        return this.floorY;
    }

    public double getFloorRadius() {
        return this.floorRadius;
    }

    public Set<BlockPos> getCircle() {
        return this.circle;
    }
}

