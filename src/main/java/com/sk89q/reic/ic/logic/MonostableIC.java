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
    
    public MonostableIC(Block block, State state, int delay) {
        super(block, state);
        this.delay = delay;
    }
    
    public int getTriggerDelay() {
        return delay;
    }

    public void trigger() {
        getState().passthrough(0);
    }

    public void unload() {
    }
    
    public String getSummary() {
        return "Will delay the input to the output " + delay + " ticks.";
    }

    public static class MonostableICFactory extends AbstractICFactory {
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
            
            return new MonostableIC(sign, family.createState(sign), delay);
        }

        public boolean canCreate(Player player) {
            return true;
        }

        public String getDescription() {
            return "Outputs the input state after the specified delay time.";
        }

        public ICDocumentation getDocumentation() {
            return new ICDocumentation()
                    .summary("Outputs the input state after the specified delay time. " +
                            "If the IC is triggered before the delay fires, then the delay is reset.")
            		.param("Delay (in ticks)")
            		.input("Input signal")
            		.output("Delayed signal");
        }
    }

}
