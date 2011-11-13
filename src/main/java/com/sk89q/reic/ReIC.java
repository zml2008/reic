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

package com.sk89q.reic;

import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.sk89q.rebar.AbstractComponent;
import com.sk89q.rebar.BukkitEvent;
import com.sk89q.rebar.Rebar;
import com.sk89q.reic.families.SingleInputSingleOutput;
import com.sk89q.reic.families.TripleInputSingleOutput;
import com.sk89q.reic.ic.logic.*;
import com.sk89q.reic.ic.world.*;
import com.sk89q.reic.util.BlockMaterialUtil;
import com.sk89q.reic.util.BlockUtil;
import com.sk89q.reic.util.ChatUtil;

public class ReIC extends AbstractComponent {

    private ReIC reIC = this;
    private Tracker tracker = new Tracker();
    private ICConfigurationManager configurations = new ICConfigurationManager();
    
    public void initialize() {
        Rebar.getInstance().registerEvents(new BlockListener());
        Rebar.getInstance().registerEvents(new WorldListener());
        Rebar.getInstance().registerEvents(new PlayerListener());
        Rebar.getInstance().registerInterval(tracker, 1, 1);
        Rebar.getInstance().registerCommands(Commands.class, this);

        registerICs();

        for (World world : Rebar.server().getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                reIC.loadICs(chunk);
            }
        }
    }

    public void shutdown() {
    }
    
    private void registerICs() {
        SingleInputSingleOutput siso = new SingleInputSingleOutput();
        TripleInputSingleOutput _3iso = new TripleInputSingleOutput();

        configurations.register("Repeater", new RepeaterIC.RepeaterICFactory(), siso, "Repeat", "Diode");
        configurations.register("Clock", new ClockIC.ClockICFactory(), siso);
        configurations.register("Monostable", new MonostableIC.MonostableICFactory(), siso, "Delay");
        configurations.register("Midi", new MidiIC.MidiICFactory(), siso);
        configurations.register("TimeOfDay", new TimeTriggerIC.TimeTriggerICFactory(), siso, "Time");

        configurations.register("And-3", new AndIC.AndICFactory(), _3iso, "And", "And3");
        configurations.register("MidiPlayer", new MidiPlayerIC.MidiPlayerICFactory(), _3iso);
        configurations.register("Transmitter", new TransmitterIC.TransmitterICFactory(), _3iso, "Xmit", "Transmit");
        configurations.register("Receiver", new ReceiverIC. ReceiverICFactory(), _3iso, "Receive");
    }

    public ICConfigurationManager getConfigurationsManager() {
        return configurations;
    }
    
    public void sendHelp(CommandSender sender, ICConfiguration configuration) {
        sender.sendMessage(ChatColor.GOLD + 
                "-- Help for IC " + configuration.getId() + " --");
        for (String line : configuration.getFactory().getDocumentation().toString().split("\n")) {
            sender.sendMessage(ChatColor.WHITE + line);
        }

        // Show aliases
        StringBuilder str = new StringBuilder();
        str.append(ChatColor.GRAY);
        str.append("Aliases: ");
        
        boolean first = true;
        for (String alias : reIC.getConfigurationsManager().getAliases(configuration)) {
            if (!first) {
                str.append(", ");
            }
            str.append(alias);
            first = false;
        }

        ChatUtil.msg(sender, str.toString());
    }
    
    private void loadICs(Chunk chunk) {
        for (BlockState blockState : chunk.getTileEntities()) {
            Block block = blockState.getBlock();
            
            if (block.getType() == Material.WALL_SIGN && blockState instanceof Sign) {
                Sign sign = (Sign) blockState;
                
                try {
                    if (!Tracker.isICHeader(sign.getLine(0))) {
                        continue;
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    continue;
                }
                
                String id = Tracker.parseICHeader(sign.getLine(0));
                ICConfiguration configuration = configurations.get(id);
                
                if (configuration.getFactory() instanceof CreatedOnChunkLoad) {
                    loadICUnattended(configuration, id, block, sign);
                }
            }
        }
    }
    
    private void unloadICs(Chunk chunk) {
        tracker.removeChunk(chunk);
    }
    
    private IC loadICUnattended(ICConfiguration configuration, String id, Block block, Sign sign) {
        // Deleted IC?
        if (configuration == null) {
            sign.setLine(0, ChatColor.RED + "{" + id + "}");
            sign.update();
            return null;
        }
        
        try {
            IC ic = configuration.create(reIC, block, sign.getLines());
            tracker.register(block, ic);
            return ic;
        } catch (Throwable e) {
            sign.setLine(0, ChatColor.RED + "{" + id + "}");
            sign.update();
            return null;
        }
        
    }
    
    private void suggestICs(CommandSender sender, String id) {
        // Suggest some potential ICs
        List<String> matches = configurations.findSimiliarIDs(id);
        if (matches.size() > 0) {
            StringBuilder str = new StringBuilder();
            str.append(ChatColor.GRAY);
            str.append("Did you mean: ");
            
            boolean first = true;
            for (String match : matches) {
                if (!first) {
                    str.append(ChatColor.GRAY + ", ");
                }
                str.append(ChatColor.AQUA);
                str.append(match);
                first = false;
            }
            
            ChatUtil.msg(sender, str.toString());
        }
    }
    
    private class PlayerListener extends org.bukkit.event.player.PlayerListener {
        @Override
        @BukkitEvent(type = Type.PLAYER_INTERACT, priority = Priority.Normal)
        public void onPlayerInteract(PlayerInteractEvent event) {
            Action action = event.getAction();
            if (action != Action.RIGHT_CLICK_BLOCK) return;
            Block block = event.getClickedBlock();
            if (block == null || block.getType() != Material.WALL_SIGN) return;
            Sign sign = BlockUtil.getState(block, Sign.class);
            if (!Tracker.isICHeader(sign.getLine(0))) return;
            Player player = event.getPlayer();
            String id = Tracker.parseICHeader(sign.getLine(0));
            
            if (player.isSneaking()) {
                player.sendMessage(ChatColor.GOLD + 
                        "-- Debug for IC " + id + " --");
                
                IC ic = tracker.get(block);
                if (ic == null) {
                    ChatUtil.error(player, "IC has not yet been initialized! Power it to initialize it.");
                } else {
                    String debug = ic.getDebugInfo();
                    
                    // Summary
                    String summary = ic.getSummary();
                    if (summary != null) {
                        player.sendMessage(ChatColor.GRAY + "At-a-glance: " + summary);
                    }
                    
                    // Debugging
                    if (debug != null) {
                        ChatUtil.msg(player, ChatColor.GRAY, "Debug info: ", ChatColor.WHITE, debug);
                    }
                }
            } else {
                ICConfiguration configuration = configurations.get(id);
                if (configuration == null) {
                    ChatUtil.error(player, "The given IC does not seem to exist (anymore?).");
                } else {
                    sendHelp(player, configuration);
                }
            }
            
            event.setCancelled(true);
        }
    }
    
    private class WorldListener extends org.bukkit.event.world.WorldListener {
        @Override
        @BukkitEvent(type = Type.CHUNK_UNLOAD, priority = Priority.Monitor)
        public void onChunkUnload(ChunkUnloadEvent event) {
            if (event.isCancelled()) return;
            unloadICs(event.getChunk());
        }

        @Override
        @BukkitEvent(type = Type.CHUNK_LOAD, priority = Priority.Monitor)
        public void onChunkLoad(ChunkLoadEvent event) {
            loadICs(event.getChunk());
        }

        @Override
        @BukkitEvent(type = Type.WORLD_LOAD, priority = Priority.Monitor)
        public void onWorldLoad(WorldLoadEvent event) {
            for (Chunk chunk : event.getWorld().getLoadedChunks()) {
                reIC.loadICs(chunk);
            }
        }

        @Override
        @BukkitEvent(type = Type.WORLD_UNLOAD, priority = Priority.Monitor)
        public void onWorldUnload(WorldUnloadEvent event) {
            for (Chunk chunk : event.getWorld().getLoadedChunks()) {
                reIC.unloadICs(chunk);
            }
        }
    }
    
    private class BlockListener extends org.bukkit.event.block.BlockListener {
        @Override
        @BukkitEvent(type = Type.BLOCK_PLACE, priority = Priority.Monitor)
        public void onBlockPlace(BlockPlaceEvent event) {
            if (event.isCancelled()) return;
            
            Block block = event.getBlock();
            
            if (block.getType() == Material.SIGN_POST
                    || block.getType() == Material.WALL_SIGN) {
                tracker.remove(block);
            }
        }

        @Override
        @BukkitEvent(type = Type.BLOCK_BREAK, priority = Priority.Monitor)
        public void onBlockBreak(BlockBreakEvent event) {
            if (event.isCancelled()) return;
            
            Block block = event.getBlock();
            
            if (block.getType() == Material.WALL_SIGN) {
                tracker.remove(block);
            }
        }

        @Override
        @BukkitEvent(type = Type.BLOCK_PHYSICS_BREAK, priority = Priority.Monitor)
        public void onBlockPhysicsBreak(BlockPhysicsBreakEvent event) {
            if (event.isCancelled()) return;
            
            Block block = event.getBlock();
            
            if (block.getType() == Material.WALL_SIGN) {
                tracker.remove(block);
            }
        }
        
        @Override
        @BukkitEvent(type = Type.BLOCK_PHYSICS, priority = Priority.Monitor)
        public void onBlockPhysics(BlockPhysicsEvent event) {
            Block block = event.getBlock();

            if (block.getType() != Material.WALL_SIGN) return;
            if (!BlockMaterialUtil.isRedstoneSourceBlock(event.getChangedType())) return;
            
            IC ic = tracker.get(event.getBlock());

            // Auto load IC
            if (ic == null) {
                Sign sign = BlockUtil.getState(block, Sign.class);
                
                if (!Tracker.isICHeader(sign.getLine(0))) {
                    return;
                }
            
                String id = Tracker.parseICHeader(sign.getLine(0));
                ICConfiguration configuration = configurations.get(id);
                
                ic = loadICUnattended(configuration, id, block, sign);
                
                if (ic == null) {
                    return;
                }
            }
            
            tracker.physicsUpdate(block, ic);
        }
        
        @Override
        @BukkitEvent(type = Type.SIGN_CHANGE)
        public void onSignChange(SignChangeEvent event) {
            if (event.isCancelled()) return;
            if (!event.getLine(0).startsWith(" =") || event.getLine(0).length() <= 2) return;
            
            String msg = processICSign(event);
            if (msg != null) {
                Block block = event.getBlock();
                Player player = event.getPlayer();
                
                BlockUtil.drop(block);
                ChatUtil.error(player, msg);
                event.setCancelled(true);
            }
        }
        
        private String processICSign(SignChangeEvent event) {
            Block block = event.getBlock();
            Player player = event.getPlayer();
            
            if (event.getBlock().getType() != Material.WALL_SIGN) {
                return "IC signs must be a wall sign.";
            }
            
            String id = event.getLine(0).substring(2);
            ICConfiguration configuration = configurations.get(id);
            
            if (configuration == null) {
                suggestICs(player, id);
                return "No IC named '" + id.toUpperCase() + "' exists.";
            }
            
            if (!Rebar.getInstance().hasPermission(player, "reic.ic." + id)
                    && (configuration.getFactory() instanceof RestrictedIC ||
                            !Rebar.getInstance().hasPermission(player, "reic.ic.safe." + id))) {
                return "You don't have permission to create this IC.";
            }
            
            if (!configuration.getFactory().canCreate(player)) {
                return "You are not allowed to create this IC.";
            }
            
            IC ic;
            
            try {
                ic = configuration.create(reIC, block, event.getLines(), player);
            } catch (ICException e) {
                return "IC error: " + e.getMessage();
            } catch (Throwable e) {
                BlockUtil.drop(block);
                return "Error occurred when creating the IC: " + e.getMessage();
            }
            
            event.setLine(0, Tracker.getICHeader(configuration.getId()));
            
            tracker.register(block, ic);
            
            player.sendMessage(ChatColor.GOLD +
                    "'" + configuration.getId() + "' IC created! How to use: "
                    + ChatColor.AQUA + "/reic doc "
                    + configuration.getId().toLowerCase());
            
            String summary = ic.getSummary();
            if (summary != null) {
                player.sendMessage(ChatColor.GRAY + "At-a-glance: " + summary);
            }
            
            return null;
        }
    }

    public static class Commands {
        public Commands(ReIC reIC) {
        }
        
        @Command(aliases = { "reic" }, desc = "ReIC commands")
        @NestedCommand(ReICCommands.class)
        public void reic(CommandContext context, CommandSender sender) {
        }
        
    }

    public static class ReICCommands {
        private ReIC reIC;
        
        public ReICCommands(ReIC reIC) {
            this.reIC = reIC;
        }
        
        @Command(aliases = { "reinit" }, desc = "Re-initialize all ICs", min = 0, max = 0)
        @CommandPermissions({ "reic.reinit" })
        public void reinit(CommandContext context, CommandSender sender) {
            Server server = Rebar.server();
            server.broadcastMessage(ChatColor.GRAY + "(ReIC integrated circuits are being re-initialized... please wait.)");
            
            for (World world : server.getWorlds()) {
                int chunkCount = 0;
                
                for (Chunk chunk : world.getLoadedChunks()) {
                    reIC.unloadICs(chunk);
                    reIC.loadICs(chunk);
                    
                    chunkCount++;
                }
                
                ChatUtil.msg(sender, ChatColor.YELLOW, "ReIC: Reloaded " + chunkCount
                        + " chunks on '" + world.getName() + "'...");
            }

            server.broadcastMessage(ChatColor.GRAY + "(ReIC ICs have been re-initialized.)");
        }
        
        @Command(aliases = { "doc" }, usage = "<id>", desc = "Read the documentation for an IC", min = 1, max = 1)
        @CommandPermissions({ "reic.doc" })
        public void doc(CommandContext context, CommandSender sender) throws CommandException {
            String id = context.getString(0);
            ICConfiguration configuration = reIC.getConfigurationsManager().get(id);
            
            if (configuration == null) {
                reIC.suggestICs(sender, id);
                throw new CommandException("No IC named '" + id.toUpperCase() + "' exists.");
            }

            reIC.sendHelp(sender, configuration);
        }
        
        @Command(aliases = { "list" }, desc = "List all the available ICs.", min = 0, max = 0)
        @CommandPermissions({ "reic.list" })
        public void list(CommandContext context, CommandSender sender) throws CommandException {
            Collection<ICConfiguration> list = reIC.getConfigurationsManager().getUniqueICs();
            
            StringBuilder str = new StringBuilder();
            str.append(ChatColor.YELLOW);
            str.append("Available ICs: ");
            
            boolean first = true;
            for (ICConfiguration configuration : list) {
                if (!first) {
                    str.append(ChatColor.YELLOW + ", ");
                }
                str.append(ChatColor.AQUA);
                str.append(configuration.getId());
                first = false;
            }

            ChatUtil.msg(sender, str.toString());
            ChatUtil.msg(sender, ChatColor.YELLOW, "Use /reic doc <id> to lookup an IC.");
        }
    }

}
