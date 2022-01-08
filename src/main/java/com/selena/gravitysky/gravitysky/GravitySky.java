package com.selena.gravitysky.gravitysky;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_18_R1.legacy.CraftLegacy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.comments.CommentType;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class GravitySky extends JavaPlugin {


    private static GravitySky instance;

    private List<Material> itemsList;
    private List<Material> blocksList;

    @Override
    public void onEnable() {
        setInstance(this);

        setUp();
        credits();
        new GameTimer(new Random());
        getServer().getPluginManager().registerEvents(new Events(), this);
    }

    private void setUp() {
        File fileConfig = new File(getDataFolder(), "Config.yml");


        YamlFile yamlConfig = new YamlFile(fileConfig);

        if (!fileConfig.exists()) {
            try {

                yamlConfig.set("SpawnRadius", 50);
                yamlConfig.setComment("SpawnRadius", "from Player location - (radius / 2) to Player location + (radius / 2)", CommentType.SIDE);
                yamlConfig.set("PeriodMax", 10);
                yamlConfig.setComment("PeriodMax", "The max time it can take for an item to spawn", CommentType.SIDE);
                yamlConfig.set("PeriodMin", 3);
                yamlConfig.setComment("PeriodMin", "The min time it can take for an item to spawn", CommentType.SIDE);
                yamlConfig.set("MaxItems", 100);
                yamlConfig.setComment("MaxItems", "The max amount of items that can spawn in a chest", CommentType.SIDE);
                yamlConfig.set("YSpawn", 320);
                yamlConfig.setComment("YSpawn", "The Y level that items spawn at", CommentType.SIDE);
                List<String> blacklistedBlocks = new ArrayList<>();
                blacklistedBlocks.add(Material.AIR.name());
                blacklistedBlocks.add(Material.CAVE_AIR.name());
                yamlConfig.set("BlacklistedBlocks", blacklistedBlocks);
                List<String> blacklistedItems = new ArrayList<>();
                blacklistedItems.add(Material.POTION.name());
                blacklistedItems.add(Material.SPLASH_POTION.name());
                blacklistedItems.add(Material.LINGERING_POTION.name());
                blacklistedItems.add(Material.ENCHANTED_BOOK.name());
                blacklistedItems.add(Material.WRITTEN_BOOK.name());
                blacklistedItems.add(Material.AIR.name());
                blacklistedItems.add(Material.CAVE_AIR.name());
                yamlConfig.set("BlacklistedItems", blacklistedItems);

                yamlConfig.set("GameMethodType", 1);
                yamlConfig.setComment("GameMethodType", "Use 1 for best option, there is also 0 for old mechanics", CommentType.SIDE);
                yamlConfig.set("ItemSpawnAttempts", 5);
                yamlConfig.set("LegacyItems", false);

                yamlConfig.set("Messages.ChestSpawn", "${GREEN}A chest has spawned with items at ${X} ${Y} ${Z}");
                yamlConfig.set("Messages.BlockSpawn", "${GREEN}A ${BLOCK} has spawned at ${X} ${Y} ${Z}");
                yamlConfig.set("Messages.ChestEmpty", "${RED}Removed chest at ${X} ${Y} ${Z} because it is empty");

                yamlConfig.save();

            } catch (Exception e) {
                System.out.println("Can't create the Config.yml. [" + e.getMessage() + "]");
            }
        }


        try {
            yamlConfig.load();
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
        Options.spawnRadius = yamlConfig.getInt("SpawnRadius");
        Options.periodMax = yamlConfig.getInt("PeriodMax");
        Options.periodMin = yamlConfig.getInt("PeriodMin");
        Options.maxItems = yamlConfig.getInt("MaxItems");
        Options.ySpawn = yamlConfig.getInt("YSpawn");
        for (String blockName : yamlConfig.getStringList("BlacklistedBlocks")) {
            Options.blacklistedBlocks.add(Material.matchMaterial(blockName));
        }
        for (String itemName : yamlConfig.getStringList("BlacklistedItems")) {
            Options.blacklistedItems.add(Material.matchMaterial(itemName));
        }
        Options.maxAttempts = yamlConfig.getInt("ItemSpawnAttempts");
        Options.legacyItems = yamlConfig.getBoolean("LegacyItems");
        Options.methodType = yamlConfig.getInt("GameMethodType");
        // Un tested but should work maybe hopefully
        if (Options.legacyItems) CraftLegacy.init();
        loadMaterials();
        Options.messages.put("ChestSpawn", Utils.color(yamlConfig.getString("Messages.ChestSpawn")));
        Options.messages.put("BlockSpawn", Utils.color(yamlConfig.getString("Messages.BlockSpawn")));
        Options.messages.put("ChestEmpty", Utils.color(yamlConfig.getString("Messages.ChestEmpty")));


    }


    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {

        ChunkGenerator generator = null;

        try {
            Class<?> c = Class.forName("com.selena.gravitysky.gravitysky.worldgen.WorldGenerator");
            Constructor<?> m = c.getConstructor();
            Object i = m.newInstance();
            generator = (ChunkGenerator) i;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return generator;
    }


    public static GravitySky getInstance() {
        return instance;
    }

    private static void setInstance(GravitySky instance) {
        GravitySky.instance = instance;
    }


    private void loadMaterials() {
        Material[] mats = Material.class.getEnumConstants();
        this.itemsList = new ArrayList<>();
        this.blocksList = new ArrayList<>();
        for (Material mat : mats) {
            if (!Options.legacyItems && mat.isLegacy())
                continue;
            if (!Options.blacklistedItems.contains(mat) && !mat.isBlock()) {
                itemsList.add(mat);
                System.out.println(Utils.color("&aAdded " + mat.name() + " to the list of item materials that are allowed to spawn"));
            }
            if (!Options.blacklistedBlocks.contains(mat) && mat.isBlock()) {
                blocksList.add(mat);
                System.out.println(Utils.color("&aAdded " + mat.name() + " to the list of blocks that are allowed to spawn"));

            }
        }
        System.out.println(Utils.color("&aThere are " + blocksList.size() + " Blocks, and " + itemsList.size() + " Items that can spawn!"));

    }

    public List<Material> getItemList() {
        return itemsList;
    }

    public List<Material> getBlockList() {
        return blocksList;
    }

    public static class Events implements Listener {


        @EventHandler
        public void itemSpawn(ItemSpawnEvent event) {


            List<Entity> entities = event.getEntity().getNearbyEntities(2, 2, 2);
            if (entities.isEmpty()) return;

            for (Entity entity : entities) {
                if (!(entity.getType() == EntityType.FALLING_BLOCK)) {
                    continue;
                }
                Location location = event.getEntity().getLocation();

                if (location.getBlockY() <= Objects.requireNonNull(location.getWorld()).getMinHeight()) {
                    event.getEntity().remove();
                    entity.remove();
                    FallingBlock fallingBlock = (FallingBlock) entity;

                    location.setY(location.getWorld().getMinHeight());
                    Block block = location.getBlock();

                    block.setType(fallingBlock.getBlockData().getMaterial(), false);
                    block.getState().update(false, false);

                }
            }

        }

        @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
        private void onSandFall(EntityChangeBlockEvent event) {
            Location loc = event.getEntity().getLocation();
            if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getTo() == Material.AIR && loc.getBlockY() <= Objects.requireNonNull(loc.getWorld()).getMinHeight()) {
                if (event.getBlock().getType().hasGravity()) {
                    event.setCancelled(true);
                    //Update the block to fix a visual client bug, but don't apply physics
                    event.getBlock().getState().update(false, false);
                }
            }
        }


        @EventHandler
        public void chestClose(InventoryCloseEvent event) {
            if (!event.getInventory().getType().equals(InventoryType.CHEST)) return;
            int x = Objects.requireNonNull(event.getInventory().getLocation()).getBlockX();
            int y = event.getInventory().getLocation().getBlockY();
            int z = event.getInventory().getLocation().getBlockZ();
            if (event.getInventory().isEmpty()) {
                event.getInventory().getLocation().getBlock().breakNaturally();
                event.getPlayer().getServer().getOnlinePlayers().forEach(player -> player.sendMessage(Options.messages.get("ChestEmpty").replace("${X}", x + "").replace("${Y}", y + "").replace("${Z}", z + "")));
            }


        }

    }


    public void credits() {
        System.out.println(Utils.color("${BOLD}The project was made by the following teams/people"));
        System.out.println(Utils.color("${AQUA}${BOLD} * ${PINK}Selena (RedW0lfStone) - Lead Developer"));
        System.out.println(Utils.color("${AQUA}${BOLD} * ${PINK}Carleslc (https://github.com/Carleslc/) - Provided open source YAML API ${TILT}(Mainly used for comments)"));
    }


}
