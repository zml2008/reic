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

public class AndIC extends AbstractIC {
    
    public AndIC(Block block, State state) {
        super(block, state);
    }

    public void trigger() {
        State state = getState();
        boolean val = true;
        for (int i = 0; i < state.numIn(); i++) {
            if (!state.in(i)) {
                val = false;
                break;
            }
        }
        state.out(0, val);
    }

    public void unload() {
    }

    public static class AndICFactory extends AbstractICFactory {
        public IC create(ReIC reic, Family family, Block sign, String[] lines) throws ICException {
            expectNoArgs(lines);
            return new AndIC(sign, family.createState(sign));
        }

        public boolean canCreate(Player player) {
            return true;
        }

        public String getDescription() {
            return "Outputs the AND result of all inputs.";
        }

        public ICDocumentation getDocumentation() {
            return new ICDocumentation()
                    .summary("Outputs the AND result of all inputs.")
                    .inputs("ANDed values")
                    .output("Result of AND");
        }
    }

}
