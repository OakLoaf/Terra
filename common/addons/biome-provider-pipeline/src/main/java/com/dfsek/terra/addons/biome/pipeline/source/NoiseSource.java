/*
 * Copyright (c) 2020-2021 Polyhedral Development
 *
 * The Terra Core Addons are licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in this module's root directory.
 */

package com.dfsek.terra.addons.biome.pipeline.source;

import com.dfsek.terra.api.noise.NoiseSampler;
import com.dfsek.terra.api.util.collection.ProbabilityCollection;
import com.dfsek.terra.api.world.biome.Biome;


public class NoiseSource implements BiomeSource {
    private final ProbabilityCollection<Biome> biomes;
    private final NoiseSampler sampler;
    
    public NoiseSource(ProbabilityCollection<Biome> biomes, NoiseSampler sampler) {
        this.biomes = biomes;
        this.sampler = sampler;
    }
    
    @Override
    public Biome getBiome(double x, double z, long seed) {
        return biomes.get(sampler, x, z, seed);
    }
    
    @Override
    public Iterable<Biome> getBiomes() {
        return biomes.getContents();
    }
}
