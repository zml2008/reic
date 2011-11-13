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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.blocks.BlockType;

public class BlockUtil {
    
    @SuppressWarnings("unchecked")
    public static <T extends BlockState> T getState(Block block, Class<T> expected) {
        BlockState state = block.getState();
        
        if (state != null && expected.isAssignableFrom(state.getClass())) {
            return (T) state;
        }
        
        throw new RuntimeException("Expected block state of " + expected.getCanonicalName()
                + " but instead got " + String.valueOf(state));
    }
    
    public static void drop(Block block) {
        BaseItemStack stack = BlockType.getBlockDrop(block.getTypeId(), block.getData());
        block.setTypeId(0);
        if (stack != null) {
            block.getWorld().dropItemNaturally(block.getLocation(),
                    new ItemStack(stack.getType(), stack.getAmount(), stack.getDamage()));
        }
    }

    public static boolean isSign(Block block) {
        return block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN;
    }
    
    public static BlockFace getLeftOf(BlockFace face) {
        switch (face) {
            case EAST: return BlockFace.NORTH;
            case NORTH: return BlockFace.WEST;
            case WEST: return BlockFace.SOUTH;
            case SOUTH: return BlockFace.EAST;
            default: return null;
        }
    }
    
    public static BlockFace getRightOf(BlockFace face) {
        switch (face) {
            case EAST: return BlockFace.SOUTH;
            case NORTH: return BlockFace.EAST;
            case WEST: return BlockFace.NORTH;
            case SOUTH: return BlockFace.WEST;
            default: return null;
        }
    }
    
}
