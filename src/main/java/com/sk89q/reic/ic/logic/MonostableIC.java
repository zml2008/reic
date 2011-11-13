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
import com.sk89q.reic.Family;
import com.sk89q.reic.IC;
import com.sk89q.reic.ICDocumentation;
import com.sk89q.reic.ICException;
import com.sk89q.reic.ReIC;
import com.sk89q.reic.State;

public class MonostableIC extends AbstractIC {
    
    private int delay;
    private int duration;
    private boolean isHigh = false;
    
    public MonostableIC(Block block, State state, int delay, int duration) {
        super(block, state);
        this.delay = delay;
        this.duration = duration;
    }
    
    public int getTriggerDelay() {
        return 1;
    }

    public void trigger() {
        if (getState().triggered(0) && getState().in(0)) {
            getState().out(0, false);
            isHigh = false;
            
            if (delay == 1) {
                tick();
            } else {
                getState().setNextTick(delay - 1);
            }
        }
    }

    public void tick() {
        if (!isHigh) {
            getState().out(0, true);
            getState().setNextTick(duration);
            isHigh = true;
        } else {
            getState().out(0, false);
        }
    }

    public void unload() {
    }
    
    public String getSummary() {
        return "Will go high after " + delay + " ticks for " + duration + " ticks.";
    }

    public static class MonostableICFactory extends AbstractICFactory {
        public IC create(ReIC reic, Family family, Block sign, String[] lines) throws ICException {
            int delay = 0;
            int duration = 0;
            
            try {
                delay = Integer.parseInt(lines[1]);
                if (delay < 1) {
                    throw new ICException("The minimum delay is 1.");
                }
            } catch (NumberFormatException e) {
                throw new ICException("The delay (line 2) should be an integer.");
            }
            
            try {
                duration = Integer.parseInt(lines[2]);
                if (delay < 1) {
                    throw new ICException("The minimum duration is 1.");
                }
            } catch (NumberFormatException e) {
                throw new ICException("The duration (line 3) should be an integer.");
            }
            
            expectNoArg(lines, 3);
            
            return new MonostableIC(sign, family.createState(sign), delay, duration);
        }

        public boolean canCreate(Player player) {
            return true;
        }

        public String getDescription() {
            return "Goes high after a specified delay and stays high for a specified duration before going low again.";
        }

        public ICDocumentation getDocumentation() {
            return new ICDocumentation()
                    .summary("When triggered with a high, the output goes low immediately, followed by a high after a specified delay, and then stays high for a specified duration before going low again. " +
                            "If the IC is triggered before the delay fires or the duration completes, " +
                            "then the output goes low and the delay is reset.")
                    .param("Delay (in ticks)")
                    .param("Duration (in ticks)")
                    .input("Trigger")
                    .output("Output signal");
        }
    }

}
