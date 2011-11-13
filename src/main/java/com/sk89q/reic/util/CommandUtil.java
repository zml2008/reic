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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.rebar.Rebar;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.blocks.ClothColor;
import com.sk89q.worldedit.blocks.ItemType;

public final class CommandUtil {

    private static final Pattern twelveHourTime
            = Pattern.compile("^([0-9]+(?::[0-9]+)?)([apmAPM\\.]+)$");
    
    private CommandUtil() {
    }
    
    private static Server getServer() {
        return Rebar.getInstance().getServer();
    }
    
    protected static Iterable<Player> checkPlayerMatch(List<Player> players)
            throws CommandException {
        // Check to see if there were any matches
        if (players.size() == 0) {
            throw new CommandException("No players matched query.");
        }
        
        return players;
    }
    
    public static Player checkPlayer(CommandSender sender)
            throws CommandException {
        if (sender instanceof Player) {
            return (Player) sender;
        } else {
            throw new CommandException("A player context is required. (Specify a world or player if the command supports it.)");
        }
    }

    public static List<Player> matchPlayerNames(String filter) {
        Player[] players = getServer().getOnlinePlayers();

        filter = filter.toLowerCase();
        
        // Allow exact name matching
        if (filter.charAt(0) == '@' && filter.length() >= 2) {
            filter = filter.substring(1);
            
            for (Player player : players) {
                if (player.getName().equalsIgnoreCase(filter)) {
                    List<Player> list = new ArrayList<Player>();
                    list.add(player);
                    return list;
                }
            }
            
            return new ArrayList<Player>();
        // Allow partial name matching
        } else if (filter.charAt(0) == '*' && filter.length() >= 2) {
            filter = filter.substring(1);
            
            List<Player> list = new ArrayList<Player>();
            
            for (Player player : players) {
                if (player.getName().toLowerCase().contains(filter)) {
                    list.add(player);
                }
            }
            
            return list;
        
        // Start with name matching
        } else {
            List<Player> list = new ArrayList<Player>();
            
            for (Player player : players) {
                if (player.getName().toLowerCase().startsWith(filter)) {
                    list.add(player);
                }
            }
            
            return list;
        }
    }

    public static Iterable<Player> matchPlayers(CommandSender source, String filter)
            throws CommandException {
        
        if (getServer().getOnlinePlayers().length == 0) {
            throw new CommandException("No players matched query.");
        }
        
        if (filter.equals("*")) {
            return checkPlayerMatch(Arrays.asList(getServer().getOnlinePlayers()));
        }

        // Handle special hash tag groups
        if (filter.charAt(0) == '#') {
            // Handle #world, which matches player of the same world as the
            // calling source
            if (filter.equalsIgnoreCase("#world")) {
                List<Player> players = new ArrayList<Player>();
                Player sourcePlayer = checkPlayer(source);
                World sourceWorld = sourcePlayer.getWorld();
                
                for (Player player : getServer().getOnlinePlayers()) {
                    if (player.getWorld().equals(sourceWorld)) {
                        players.add(player);
                    }
                }

                return checkPlayerMatch(players);
            
            // Handle #near, which is for nearby players.
            } else if (filter.equalsIgnoreCase("#near")) {
                List<Player> players = new ArrayList<Player>();
                Player sourcePlayer = checkPlayer(source);
                World sourceWorld = sourcePlayer.getWorld();
                org.bukkit.util.Vector sourceVector
                        = sourcePlayer.getLocation().toVector();
                
                for (Player player : getServer().getOnlinePlayers()) {
                    if (player.getWorld().equals(sourceWorld)
                            && player.getLocation().toVector().distanceSquared(
                                    sourceVector) < 900) {
                        players.add(player);
                    }
                }

                return checkPlayerMatch(players);
            
            } else {
                throw new CommandException("Invalid group '" + filter + "'.");
            }
        }
        
        List<Player> players = matchPlayerNames(filter);
        
        return checkPlayerMatch(players);
    }
    
    public static Player matchPlayerExactly(CommandSender sender, String filter)
            throws CommandException {
        Player[] players = getServer().getOnlinePlayers();
        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(filter)) {
                return player;
            }
        }
    
        throw new CommandException("No player found!");
    }
    
    public static Player matchSinglePlayer(CommandSender sender, String filter)
            throws CommandException {
        // This will throw an exception if there are no matches
        Iterator<Player> players = matchPlayers(sender, filter).iterator();
        
        Player match = players.next();
        
        // We don't want to match the wrong person, so fail if if multiple
        // players were found (we don't want to just pick off the first one,
        // as that may be the wrong player)
        if (players.hasNext()) {
            throw new CommandException("More than one player found! " +
                    "Use @<name> for exact matching.");
        }
        
        return match;
    }
    
    public static ItemStack matchItem(CommandSender source, String name)
            throws CommandException {

        int id = 0;
        int dmg = 0;
        String dataName = null;

        if (name.contains(":")) {
            String[] parts = name.split(":");
            dataName = parts[1];
            name = parts[0];
        }
        
        try {
            id = Integer.parseInt(name);
        } catch (NumberFormatException e) {
            // Then check WorldEdit
            ItemType type = ItemType.lookup(name);
            
            if (type == null) {
                throw new CommandException("No item type known by '" + name + "'");
            }
            
            id = type.getID();
        }
        
        // If the user specified an item data or damage value, let's try
        // to parse it!
        if (dataName != null) {            
            dmg = matchItemData(id, dataName);
        }
        
        return new ItemStack(id, 1, (short)dmg, (byte)dmg);
    }
    
    public static int matchItemData(int id, String filter) throws CommandException {
        try {
            // First let's try the filter as if it was a number
            return Integer.parseInt(filter);
        } catch (NumberFormatException e) {
        }

        // So the value isn't a number, but it may be an alias!
        switch (id) {
            case BlockID.WOOD:
                if (filter.equalsIgnoreCase("redwood")) {
                    return 1;
                } else if (filter.equalsIgnoreCase("birch")) {
                    return 2;
                }
                
                throw new CommandException("Unknown wood type name of '" + filter + "'.");
            case BlockID.STEP:
            case BlockID.DOUBLE_STEP:
                BlockType dataType = BlockType.lookup(filter);
                
                if (dataType != null) {
                    if (dataType == BlockType.STONE) {
                        return 0;
                    } else if (dataType == BlockType.SANDSTONE) {
                        return 1;
                    } else if (dataType == BlockType.WOOD) {
                        return 2;
                    } else if (dataType == BlockType.COBBLESTONE) {
                        return 3;
                    } else {
                        throw new CommandException("Invalid slab material of '" + filter + "'.");
                    }
                } else {
                    throw new CommandException("Unknown slab material of '" + filter + "'.");
                }
            case BlockID.CLOTH:
                ClothColor col = ClothColor.lookup(filter);
                if (col != null) {
                    return col.getID();
                }
                
                throw new CommandException("Unknown wool color name of '" + filter + "'.");
            case 351: // Dye
                ClothColor dyeCol = ClothColor.lookup(filter);
                if (dyeCol != null) {
                    return 15 - dyeCol.getID();
                }
                
                throw new CommandException("Unknown dye color name of '" + filter + "'.");
            default: 
                throw new CommandException("Invalid data value of '" + filter + "'.");
        }
    }

    public static int matchTime(String timeStr) throws CommandException {
        Matcher matcher;
        
        try {
            int time = Integer.parseInt(timeStr);
            
            // People tend to enter just a number of the hour
            if (time <= 24) {
                return ((time - 8) % 24) * 1000;
            }
            
            return time;
        } catch (NumberFormatException e) {
            // Not an integer!
        }
        
        // Tick time
        if (timeStr.matches("^*[0-9]+$")) {
            return Integer.parseInt(timeStr.substring(1));
        
        // Allow 24-hour time
        } else if (timeStr.matches("^[0-9]+:[0-9]+$")) {
            String[] parts = timeStr.split(":");
            int hours = Integer.parseInt(parts[0]);
            int mins = Integer.parseInt(parts[1]);
            int n = (int) (((hours - 8) % 24) * 1000
                + Math.round((mins % 60) / 60.0 * 1000));
            return n;
        
        // Or perhaps 12-hour time
        } else if ((matcher = twelveHourTime.matcher(timeStr)).matches()) {
            String time = matcher.group(1);
            String period = matcher.group(2);
            int shift = 0;
            
            if (period.equalsIgnoreCase("am")
                    || period.equalsIgnoreCase("a.m.")) {
                shift = 0;
            } else if (period.equalsIgnoreCase("pm")
                    || period.equalsIgnoreCase("p.m.")) {
                shift = 12;
            } else {
                throw new CommandException("'am' or 'pm' expected, got '"
                        + period + "'.");
            }
            
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int mins = parts.length >= 2 ? Integer.parseInt(parts[1]) : 0;
            int n = (int) ((((hours % 12) + shift - 8) % 24) * 1000
                + (mins % 60) / 60.0 * 1000);
            return n;
        
        // Or some shortcuts
        } else if (timeStr.equalsIgnoreCase("dawn")) {
            return (6 - 8 + 24) * 1000;
        } else if (timeStr.equalsIgnoreCase("sunrise")) {
            return (7 - 8 + 24) * 1000;
        } else if (timeStr.equalsIgnoreCase("morning")) {
            return (8 - 8 + 24) * 1000;
        } else if (timeStr.equalsIgnoreCase("day")) {
            return (8 - 8 + 24) * 1000;
        } else if (timeStr.equalsIgnoreCase("midday")
                || timeStr.equalsIgnoreCase("noon")) {
            return (12 - 8 + 24) * 1000;
        } else if (timeStr.equalsIgnoreCase("afternoon")) {
            return (14 - 8 + 24) * 1000;
        } else if (timeStr.equalsIgnoreCase("evening")) {
            return (16 - 8 + 24) * 1000;
        } else if (timeStr.equalsIgnoreCase("sunset")) {
            return (21 - 8 + 24) * 1000;
        } else if (timeStr.equalsIgnoreCase("dusk")) {
            return (21 - 8 + 24) * 1000 + (int) (30 / 60.0 * 1000);
        } else if (timeStr.equalsIgnoreCase("night")) {
            return (22 - 8 + 24) * 1000;
        } else if (timeStr.equalsIgnoreCase("midnight")) {
            return (0 - 8 + 24) * 1000;
        }
        
        throw new CommandException("Time input format unknown.");
    }
}
