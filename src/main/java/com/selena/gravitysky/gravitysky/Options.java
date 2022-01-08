package com.selena.gravitysky.gravitysky;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class Options {

    public static int spawnRadius;
    public static int periodMax;
    public static int periodMin;
    public static int ySpawn;
    public static int maxItems;
    public static List<Material> blacklistedBlocks = new ArrayList<>();
    public static List<Material> blacklistedItems = new ArrayList<>();
    public static Map<String, String> messages = new TreeMap<>();
    public static int methodType;
    public static boolean legacyItems;

    public static int maxAttempts;
}
