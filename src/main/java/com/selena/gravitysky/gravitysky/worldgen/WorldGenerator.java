package com.selena.gravitysky.gravitysky.worldgen;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class WorldGenerator extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {

        ChunkData chunk = createChunkData(world);
        if (x == 0 && z == 0)
            if (!world.isChunkGenerated(0,0)) {
                chunk.setBlock(0,99,0, Material.BEDROCK);
                world.setSpawnLocation(0, 100, 0);
            }

        return chunk;
    }
}
