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

package com.sk89q.reic.families;

import org.bukkit.block.Block;

import com.sk89q.reic.AbstractState;
import com.sk89q.reic.Family;
import com.sk89q.reic.State;

public class TripleInputSingleOutput implements Family {

    public State createState(Block sign) {
        return new State3ISO(sign);
    }
    
    public static class State3ISO extends AbstractState {
        
        private boolean[] input = new boolean[3];
        private boolean output = false;
        private boolean[] changed = new boolean[3];

        public State3ISO(Block sign) {
            super(sign);
        }

        public boolean update() {
            boolean[] newInput = new boolean[3];
            
            newInput[0] = isPowered(getBlock(), getFace0());
            newInput[1] = isPowered(getBlock(), getFace1());
            newInput[2] = isPowered(getBlock(), getFace2());

            output = isPoweredOutput(getSingleOutput());

            changed[0] = changed[0] || newInput[0] != input[0];
            changed[1] = changed[1] || newInput[1] != input[1];
            changed[2] = changed[2] || newInput[2] != input[2];
            
            input = newInput;
            
            return changed[0] || changed[1] || changed[2];
        }
        
        public void clearTriggered() {
            changed = new boolean[3];
        }

        public boolean in(int pin) {
            if (pin < 0 || pin >= numIn()) return false;
            return input[pin];
        }

        public boolean out(int pin) {
            return pin == 0 ? output : false;
        }

        public void out(int pin, boolean val) {
            setOutput(getSingleOutput(), val);
        }

        public boolean triggered(int pin) {
            if (pin < 0 || pin >= numIn()) return false;
            return changed[pin];
        }

        public int numIn() {
            return 3;
        }

        public int numOut() {
            return 1;
        }
        
    }

}
