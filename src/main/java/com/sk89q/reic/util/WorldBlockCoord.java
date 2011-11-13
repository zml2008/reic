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

import org.bukkit.World;
import org.bukkit.block.Block;

public class WorldBlockCoord extends BlockCoord {
    
    private World world;

    public WorldBlockCoord(World world) {
        this.world = world;
    }

    public WorldBlockCoord(World world, int x, int y, int z) {
        super(x, y, z);
        this.world = world;
    }

    public WorldBlockCoord(Block block) {
        super(block.getX(), block.getY(), block.getZ());
        this.world = block.getWorld();
    }
    
    public World getWorld() {
        return world;
    }

    public Block getBlock() {
        return world.getBlockAt(getX(), getY(), getZ());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WorldBlockCoord)) {
            return false;
        }

        WorldBlockCoord other = (WorldBlockCoord) obj;
        return this.world.equals(other.world) && getX() == other.getX()
                && getY() == other.getY() && getZ() == other.getZ();
    }

    public int hashCode() {
        return getX() + getY() << 8 + getZ() << 16 + this.world.hashCode() << 7;
    }
}
