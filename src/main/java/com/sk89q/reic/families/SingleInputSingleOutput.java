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

public class SingleInputSingleOutput implements Family {

    public State createState(Block sign) {
        return new StateSISO(sign);
    }
    
    public static class StateSISO extends AbstractState {
        
        private boolean input = false;
        private boolean output = false;
        private boolean changed = false;

        public StateSISO(Block sign) {
            super(sign);
        }

        public boolean update() {
            boolean lastInput = input;
            
            input = isPowered(getBlock(), getFace0()) 
                    || isPowered(getBlock(), getFace1())
                    || isPowered(getBlock(), getFace2());
            
            output = isPoweredOutput(getSingleOutput());
            
            return changed = (changed || lastInput != input);
        }
        
        public void clearTriggered() {
            changed = false;
        }

        public boolean in(int pin) {
            return pin == 0 ? input : false;
        }

        public boolean out(int pin) {
            return pin == 0 ? output : false;
        }

        public void out(int pin, boolean val) {
            setOutput(getSingleOutput(), val);
        }

        public boolean triggered(int pin) {
            return pin == 0 ? changed : false;
        }

        public int numIn() {
            return 1;
        }

        public int numOut() {
            return 1;
        }
        
    }

}
