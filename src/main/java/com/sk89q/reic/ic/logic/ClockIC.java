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

package com.sk89q.reic.ic.logic;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.sk89q.reic.AbstractIC;
import com.sk89q.reic.AbstractICFactory;
import com.sk89q.reic.CreatedOnChunkLoad;
import com.sk89q.reic.Family;
import com.sk89q.reic.IC;
import com.sk89q.reic.ICDocumentation;
import com.sk89q.reic.ICException;
import com.sk89q.reic.ReIC;
import com.sk89q.reic.State;

public class ClockIC extends AbstractIC {
    
    private int delay;
    
    public ClockIC(Block block, State state, int delay) {
        super(block, state);
        this.delay = delay;
    }
    
    public int getTriggerDelay() {
        return delay;
    }

    public void initialize() {
        trigger();
    }

    public void trigger() {
        State state = getState();
        
        if (state.in(0)) {
            state.setNextTick(delay);
        } else {
            state.clearTick();
        }
    }
    
    public void tick() {
        State state = getState();
        state.out(0, !state.out(0));
        state.setNextTick(delay);
    }

    public void unload() {
    }
    
    public String getSummary() {
        return "Will toggle every " + delay + " ticks.";
    }

    public static class ClockICFactory extends AbstractICFactory implements CreatedOnChunkLoad {
        public IC create(ReIC reic, Family family, Block sign, String[] lines) throws ICException {
            int delay = 0;
            
            try {
                delay = Integer.parseInt(lines[1]);
                if (delay < 2) {
                    throw new ICException("The minimum delay is 2.");
                }
            } catch (NumberFormatException e) {
                throw new ICException("The delay (line 2) should be an integer.");
            }
            
            expectNoArg(lines, 2);
            expectNoArg(lines, 3);
            
            return new ClockIC(sign, family.createState(sign), delay);
        }

        public boolean canCreate(Player player) {
            return true;
        }

        public String getDescription() {
            return "Toggles the output state at a certain specified interval.";
        }

        public ICDocumentation getDocumentation() {
            return new ICDocumentation()
                    .summary("Toggles the output state at a certain specified interval.")
                    .param("Interval (in ticks)")
                    .input("HIGH to enable")
                    .output("Clock signal");
        }
    }

}
