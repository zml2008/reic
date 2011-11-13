// $Id$
/*
 * ReIC integrated circuits
 * Copyright (C) 2010, 2011 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.sk89q.reic.util;

import org.bukkit.Chunk;
import org.bukkit.World;

public class ChunkUtil {
    
    private ChunkUtil() {
    }
    
    public static Chunk getRelative(Chunk chunk, int deltaX, int deltaZ) {
        return chunk.getWorld().getChunkAt(chunk.getX() + deltaX, chunk.getZ() + deltaZ);
    }
    
    public static Chunk getPotentialChunk(World world, int x, int z) {
        if (world.isChunkLoaded(x, z)) {
            return world.getChunkAt(x, z);
        }
        
        return null;
    }
    
    public static Chunk getPotentialChunk(WorldChunkCoord coord) {
        World world = coord.getWorld();
        int x = coord.getX();
        int z = coord.getZ();
        if (world.isChunkLoaded(x, z)) {
            return world.getChunkAt(x, z);
        }
        
        return null;
    }

}
