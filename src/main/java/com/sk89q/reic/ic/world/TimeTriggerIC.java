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

package com.sk89q.reic.ic.world;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.reic.AbstractIC;
import com.sk89q.reic.AbstractICFactory;
import com.sk89q.reic.CreatedOnChunkLoad;
import com.sk89q.reic.Family;
import com.sk89q.reic.IC;
import com.sk89q.reic.ICDocumentation;
import com.sk89q.reic.ICException;
import com.sk89q.reic.ReIC;
import com.sk89q.reic.State;
import com.sk89q.reic.util.CommandUtil;
import com.sk89q.reic.util.TimeUtil;

public class TimeTriggerIC extends AbstractIC {
    
    private int time;
    private long nextTime = 0;
    private boolean wasHigh = false;
    
    public TimeTriggerIC(Block block, State state, int time) {
        super(block, state);
        if (time < 0) {
            time += 24000;
        }
        this.time = time;
    }
    
    private void resetNext() {
        long now = getBlock().getWorld().getFullTime();
        long nowRel = now % 24000;
        if (nowRel < 0) nowRel += 24000;
        if (time > nowRel) {
            nextTime = now - nowRel + time;
        } else {
            nextTime = now - nowRel + 24000 + time;
        }
        setNext();
    }
    
    private void setNext() {
        long now = getBlock().getWorld().getFullTime();
        int delay = 5;
        if (nextTime - now > 20 * 30) {
            delay = 19 * 30;
        } else if (nextTime - now > 20 * 10) {
            delay = 19 * 9;
        }
        getState().setNextTick(delay);
    }

    public void initialize() {
        resetNext();
    }

    public void trigger() {
    }

    public void tick() {
        long now = getBlock().getWorld().getFullTime();
        if (wasHigh) {
            getState().out(0, false);
            wasHigh = false;
            resetNext();
        } else if (now >= nextTime) {
            wasHigh = true;
            getState().out(0, true);
            getState().setNextTick(4);
        } else {
            setNext();
        }
    }

    public void unload() {
    }
    
    public String getSummary() {
        return "Will trigger at around " + TimeUtil.getTimeString(time) + " for 4 ticks.";
    }

    public String getDebugInfo() {
        long now = getBlock().getWorld().getFullTime();
        return "Next trigger in " + (nextTime - now) + " ticks";
    }

    public static class TimeTriggerICFactory extends AbstractICFactory implements CreatedOnChunkLoad {
        public IC create(ReIC reic, Family family, Block sign, String[] lines) throws ICException {
            int time;
            try {
                time = CommandUtil.matchTime(lines[1].trim());
            } catch (CommandException e) {
                throw new ICException(e.getMessage());
            }
            
            expectNoArg(lines, 2);
            expectNoArg(lines, 3);
            
            return new TimeTriggerIC(sign, family.createState(sign), time);
        }

        public boolean canCreate(Player player) {
            return true;
        }

        public String getDescription() {
            return "Outputs a high for 4 ticks when the specified time of day is reached.";
        }

        public ICDocumentation getDocumentation() {
            return new ICDocumentation()
                    .summary("Outputs a high for 4 ticks when the specified time of day is reached.")
                    .param("Time string (3pm, 15:30, etc.)")
                    .output("HIGH when the time of day is reached");
        }
    }

}
