package com.selena.gravitysky.gravitysky;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Random;

public class GameTimer {

    private final Random blockRandom;
    private final Random locationRandom;
    private final GravitySky sky;

    public GameTimer(Random random) {

        blockRandom = new Random(random.nextLong());
        locationRandom = new Random(random.nextLong());
        Random timerRandom = new Random(random.nextLong());
        sky = GravitySky.getInstance();

        new BukkitRunnable() {
            @Override
            public void run() {


                Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();

                for (Player player : players) {
                    if (Options.methodType == 0) {
                        methodIteration0(player);
                    } else if (Options.methodType == 1) {
                        methodIteration1(player);
                    }
                }

            }

        }.runTaskTimer(GravitySky.getInstance(), 30, (timerRandom.nextInt(Options.periodMax - Options.periodMin) + Options.periodMin) * 20L);

    }

    // Iteration 0 does not use independent lists and could be inefficient
    // Also if the block is blacklisted it never tries again and needs to wait Options.
    private void methodIteration0(Player player) {
        World world = player.getWorld();
        Material material = Utils.randomEnum(Material.class, blockRandom);
        ItemStack itemStack = new ItemStack(material, blockRandom.nextInt(Options.maxItems));

        Location location = player.getLocation();

        int radius = Options.spawnRadius / 2;
        int xMin = location.getBlockX() - radius;
        int xMax = location.getBlockX() + radius;
        int x = blockRandom.nextInt(xMax - xMin) + xMin;

        int y = player.getWorld().getName().equals("world") ? Options.ySpawn : 250;

        int zMin = location.getBlockZ() - radius;
        int zMax = location.getBlockZ() + radius;
        int z = blockRandom.nextInt(zMax - zMin) + zMin;


        Location blockLocation = new Location(world, x, y, z);

        if (!material.isBlock() || blockRandom.nextInt(100) <= 10) {
            if (Options.blacklistedItems.contains(material))
                return;
            y = world.getHighestBlockAt(x, z).getY() + 1;
            if (y == -64) {
                int yMin = location.getBlockY() - radius;
                int yMax = location.getBlockY() + radius;
                y = blockRandom.nextInt(yMax - yMin) + yMin;
            }
            y = Math.max(Math.min(y, 319), -64);
            material = Material.CHEST;
            Block block = setBlock(world, x, y, z, material);
            Chest chest = (Chest) block.getState();
            chest.getInventory().addItem(itemStack);
            player.sendMessage(Options.messages.get("ChestSpawn").replace("${X}", x + "").replace("${Y}", y + "").replace("${Z}", z + ""));
        } else {
            if (!Options.blacklistedBlocks.contains(material)) {
                world.spawnFallingBlock(blockLocation, new MaterialData(material));

                player.sendMessage(Options.messages.get("BlockSpawn").replace("${X}", x + "").replace("${Y}", y + "").replace("${Z}", z + "").replace("${BLOCK}", material.name()));

            }
        }
    }

    private void methodIteration1(Player player) {
        boolean isBlock = blockRandom.nextInt(100) <= 75;

        World world = player.getWorld();

        Location location = player.getLocation();

        int radius = Options.spawnRadius / 2;
        int xMin = location.getBlockX() - radius;
        int xMax = location.getBlockX() + radius;
        int x = locationRandom.nextInt(xMax - xMin) + xMin;

        int y = Math.min(Options.ySpawn, world.getMaxHeight());

        int zMin = location.getBlockZ() - radius;
        int zMax = location.getBlockZ() + radius;
        int z = locationRandom.nextInt(zMax - zMin) + zMin;

        Location blockLocation = new Location(world, x - 0.5, y, z - 0.5);

        if (isBlock) {
            // Block
            Material material = sky.getBlockList().get(blockRandom.nextInt(sky.getBlockList().size()));
            int chance = blockRandom.nextInt(100);
            if (chance <= 10) {
                spawnItem(world, x, z, radius, player, material);
                return;
            }
            world.spawnFallingBlock(blockLocation, new MaterialData(material));

            player.sendMessage(Options.messages.get("BlockSpawn").replace("${X}", x + "").replace("${Y}", y + "").replace("${Z}", z + "").replace("${BLOCK}", material.name()));
        } else {
            spawnItem(world, x, z, radius, player, sky.getItemList().get(blockRandom.nextInt(sky.getItemList().size())));
        }
    }

    private void spawnItem(World world, int x, int z, int radius, Player player, Material material) {
        // Item
        if (Options.blacklistedItems.contains(material)) {
            for (int tries = 0; tries < Options.maxAttempts; tries++) {
                material = sky.getItemList().get(blockRandom.nextInt(sky.getItemList().size()));
                if (!Options.blacklistedItems.contains(material)) {
                    break;
                }
            }
        }
        if (Options.blacklistedItems.contains(material)) {
            return;
        }

        ItemStack itemStack = new ItemStack(material, blockRandom.nextInt(Options.maxItems));
        int y = world.getHighestBlockAt(x, z).getY() + 1;
        if (y == -64) {
            int yMin = player.getLocation().getBlockY() - radius;
            int yMax = player.getLocation().getBlockY() + radius;
            y = locationRandom.nextInt(yMax - yMin) + yMin;
        }
        y = Math.max(Math.min(y, 319), -64);
        material = Material.CHEST;
        Block block = setBlock(world, x, y, z, material);
        Chest chest = (Chest) block.getState();
        chest.getInventory().addItem(itemStack);
        player.sendMessage(Options.messages.get("ChestSpawn").replace("${X}", x + "").replace("${Y}", y + "").replace("${Z}", z + ""));
    }


    private Block setBlock(World world, int x, int y, int z, Material material) {
        Block block = world.getBlockAt(x, y, z);
        block.setType(material, false);

        if (material.hasGravity())
            block.getState().update(false, false);

        return block;
    }

//    private int getMaxNear(Collection<? extends Player> players) {
//        return Options.maxNear * players.size();
//    }
}
