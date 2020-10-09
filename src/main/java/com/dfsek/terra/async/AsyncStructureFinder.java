package com.dfsek.terra.async;

import com.dfsek.terra.Terra;
import com.dfsek.terra.TerraProfiler;
import com.dfsek.terra.TerraWorld;
import com.dfsek.terra.biome.TerraBiomeGrid;
import com.dfsek.terra.biome.UserDefinedBiome;
import com.dfsek.terra.config.genconfig.StructureConfig;
import com.dfsek.terra.structure.GaeaStructure;
import com.dfsek.terra.structure.StructureSpawnRequirement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.polydev.gaea.generation.GenerationPhase;
import org.polydev.gaea.profiler.ProfileFuture;

import java.util.Random;

public class AsyncStructureFinder implements Runnable {
    private final TerraBiomeGrid grid;
    private final StructureConfig target;
    private final Player p;
    private final int startRadius;
    private final int maxRadius;
    private final boolean tp;
    private final int centerX;
    private final int centerZ;
    private final long seed;
    private final World world;

    public AsyncStructureFinder(TerraBiomeGrid grid, StructureConfig target, Player p, int startRadius, int maxRadius, boolean tp) {
        this.grid = grid;
        this.target = target;
        this.p = p;
        this.startRadius = startRadius;
        this.maxRadius = maxRadius;
        this.tp = tp;
        this.centerX = p.getLocation().getBlockX();
        this.centerZ = p.getLocation().getBlockZ();
        this.seed = p.getWorld().getSeed();
        this.world = p.getWorld();
    }

    @Override
    public void run() {
        int x = centerX;
        int z = centerZ;

        int wid = target.getSpawn().getWidth() + 2*target.getSpawn().getSeparation();
        x/=wid;
        z/=wid;

        int run = 1;
        boolean toggle = true;
        boolean found = false;
        Vector spawn = null;
        main: for(int i = startRadius; i < maxRadius; i++) {
            for(int j = 0; j < run; j++) {
                spawn = target.getSpawn().getChunkSpawn(x, z, seed);
                if(isValidSpawn(spawn.getBlockX(), spawn.getBlockZ())) {
                    found = true;
                    break main;
                }
                if(toggle) x += 1;
                else x -= 1;
            }
            for(int j = 0; j < run; j++) {
                spawn = target.getSpawn().getChunkSpawn(x, z, seed);
                if(isValidSpawn(spawn.getBlockX(), spawn.getBlockZ())) {
                    found = true;
                    break main;
                }
                if(toggle) z += 1;
                else z -= 1;
            }
            run++;
            toggle = !toggle;
        }
        if(found) {
            p.sendMessage("Located structure at (" + spawn.getBlockX() + ", " + spawn.getBlockZ() + ").");
            if(tp) {
                int finalX = spawn.getBlockX();
                int finalZ = spawn.getBlockZ();
                Bukkit.getScheduler().runTask(Terra.getInstance(), () -> p.teleport(new Location(p.getWorld(), finalX, p.getLocation().getY(), finalZ)));
            }
        } else if(p.isOnline()) p.sendMessage("Unable to locate structure. " + spawn);
    }
    private boolean isValidSpawn(int x, int z) {
        Location spawn = target.getSpawn().getNearestSpawn(x, z, world.getSeed()).toLocation(world);
        if(! TerraWorld.getWorld(world).getConfig().getBiome((UserDefinedBiome) grid.getBiome(spawn)).getStructures().contains(target)) return false;
        Random r2 = new Random(spawn.hashCode());
        GaeaStructure struc = target.getStructure(r2);
        GaeaStructure.Rotation rotation = GaeaStructure.Rotation.fromDegrees(r2.nextInt(4) * 90);
        for(int y = target.getSearchStart().get(r2); y > 0; y--) {
            if(!target.getBound().isInRange(y)) return false;
            spawn.setY(y);
            if(! struc.checkSpawns(spawn, rotation)) continue;
            return true;
        }
        return false;
    }
}