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

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public interface State {
    
    Block getBlock();

    Sign getSign();

    Block getAttachedTo();

    boolean update();

    void clearTriggered();
    
    boolean in(int pin);
    
    boolean out(int pin);
    
    void out(int pin, boolean val);
    
    boolean passthrough(int pinIn);
    
    boolean passthrough(int pinIn, int pinOut);
    
    boolean triggered(int pin);

    int numIn();
    
    int numOut();
    
    boolean hasIn(int pin);
    
    boolean hasOut(int pin);
    
    void setNextTick(int ticks);
    
    void clearTick();
    
    int getNextTick();
    
    boolean tickCleared();
    
    void reset();
    
}
