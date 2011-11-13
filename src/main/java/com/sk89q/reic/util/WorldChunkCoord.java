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

public class WorldChunkCoord extends ChunkCoord {
    
    private World world;

    public WorldChunkCoord(World world) {
        this.world = world;
    }

    public WorldChunkCoord(World world, int x, int z) {
        super(x, z);
        this.world = world;
    }

    public WorldChunkCoord(Chunk chunk) {
        super(chunk.getX(), chunk.getZ());
        this.world = chunk.getWorld();
    }
    
    public World getWorld() {
        return world;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WorldChunkCoord)) {
            return false;
        }

        WorldChunkCoord other = (WorldChunkCoord) obj;
        return this.world.equals(other.world) && getX() == other.getX() && getZ() == other.getZ();
    }

    public int hashCode() {
        return getX() + getZ() << 16 + this.world.hashCode() << 8;
    }
}
