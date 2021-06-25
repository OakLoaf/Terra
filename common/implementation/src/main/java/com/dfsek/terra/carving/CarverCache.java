package com.dfsek.terra.carving;

import com.dfsek.terra.api.TerraPlugin;
import com.dfsek.terra.api.util.FastRandom;
import com.dfsek.terra.api.util.GlueList;
import com.dfsek.terra.api.util.MathUtil;
import com.dfsek.terra.api.util.PopulationUtil;
import com.dfsek.terra.api.world.World;
import com.dfsek.terra.api.world.biome.TerraBiome;
import com.dfsek.terra.api.world.biome.UserDefinedBiome;
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;
import com.dfsek.terra.api.world.carving.Worm;
import com.dfsek.terra.vector.Vector3Impl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CarverCache {

    private final LoadingCache<Long, List<Worm.WormPoint>> cache;
    private final UserDefinedCarver carver;

    public CarverCache(World w, TerraPlugin main, UserDefinedCarver carver) {
        this.carver = carver;
        cache = CacheBuilder.newBuilder().maximumSize(main.getTerraConfig().getCarverCacheSize())
                .build(new CacheLoader<Long, List<Worm.WormPoint>>() {
                    @Override
                    public List<Worm.WormPoint> load(@NotNull Long key) {
                        int chunkX = (int) (key >> 32);
                        int chunkZ = (int) key.longValue();
                        BiomeProvider provider = main.getWorld(w).getBiomeProvider();
                        if(CarverCache.this.carver.isChunkCarved(w, chunkX, chunkZ, new FastRandom(PopulationUtil.getCarverChunkSeed(chunkX, chunkZ, w.getSeed() + CarverCache.this.carver.hashCode())))) {
                            long seed = PopulationUtil.getCarverChunkSeed(chunkX, chunkZ, w.getSeed());
                            Random r = new FastRandom(seed);
                            Worm carving = CarverCache.this.carver.getWorm(seed, new Vector3Impl((chunkX << 4) + r.nextInt(16), CarverCache.this.carver.getConfig().getHeight().get(r), (chunkZ << 4) + r.nextInt(16)));
                            List<Worm.WormPoint> points = new GlueList<>();
                            for(int i = 0; i < carving.getLength(); i++) {
                                carving.step();
                                TerraBiome biome = provider.getBiome(carving.getRunning());
                                if(!((UserDefinedBiome) biome).getConfig().getCarvers().containsKey(CarverCache.this.carver)) { // Stop if we enter a biome this carver is not present in
                                    return Collections.emptyList();
                                }
                                points.add(carving.getPoint());
                            }
                            return points;
                        }
                        return Collections.emptyList();
                    }
                });
    }

    public List<Worm.WormPoint> getPoints(int chunkX, int chunkZ) {
        return cache.getUnchecked(MathUtil.squash(chunkX, chunkZ));
    }
}
