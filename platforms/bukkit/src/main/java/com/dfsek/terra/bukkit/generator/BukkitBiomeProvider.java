package com.dfsek.terra.bukkit.generator;

import com.dfsek.terra.api.world.biome.Biome;

import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.dfsek.terra.api.Handle;


public class BukkitBiomeProvider extends BiomeProvider implements Handle {
    private final com.dfsek.terra.api.world.biome.generation.BiomeProvider delegate;
    
    public BukkitBiomeProvider(com.dfsek.terra.api.world.biome.generation.BiomeProvider delegate) { this.delegate = delegate; }
    
    @Override
    public @NotNull org.bukkit.block.Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
        Biome biome = delegate.getBiome(x, z, worldInfo.getSeed());
        return (org.bukkit.block.Biome) biome.getVanillaBiomes().get(biome.getGenerator().getBiomeNoise(), x, y, z).getHandle();
    }
    
    @Override
    public @NotNull List<org.bukkit.block.Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        return StreamSupport.stream(delegate.getBiomes().spliterator(), false)
                            .flatMap(terraBiome -> terraBiome.getVanillaBiomes()
                                                             .getContents()
                                                             .stream()
                                                             .map(biome -> (org.bukkit.block.Biome) biome.getHandle()))
                            .collect(Collectors.toList());
    }
    
    @Override
    public Object getHandle() {
        return delegate;
    }
}
