/*
 * Copyright (c) 2020-2021 Polyhedral Development
 *
 * The Terra Core Addons are licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in this module's root directory.
 */

package com.dfsek.terra.addons.biome.pipeline.stages;

import com.dfsek.terra.addons.biome.pipeline.api.BiomeExpander;
import com.dfsek.terra.addons.biome.pipeline.api.BiomeHolder;
import com.dfsek.terra.addons.biome.pipeline.api.Stage;
import com.dfsek.terra.api.world.biome.Biome;


public class ExpanderStage implements Stage {
    private final BiomeExpander expander;
    
    public ExpanderStage(BiomeExpander expander) {
        this.expander = expander;
    }
    
    @Override
    public BiomeHolder apply(BiomeHolder in, long seed) {
        return in.expand(expander, seed);
    }
    
    @Override
    public boolean isExpansion() {
        return true;
    }
    
    @Override
    public Iterable<Biome> getBiomes(Iterable<Biome> biomes) {
        return biomes;
    }
}
