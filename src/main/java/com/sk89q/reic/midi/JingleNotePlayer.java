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

package com.sk89q.reic.midi;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class JingleNotePlayer implements Runnable {
    private Block block;
    private JingleSequencer sequencer;
    private final int playDistance = 4096;
    
    public JingleNotePlayer(Block block, JingleSequencer seq) {
        this.block = block;
        this.sequencer = seq;
    }
    
    public void run() {
        try {
            sequencer.run(this);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            sequencer.stop();
            sequencer = null;
        }
    }

    public void stop() {
        if (sequencer != null) {
            sequencer.stop();
        }
    }
    
    public void play(byte instrument, byte note) {
        for (Player player : block.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(block.getLocation()) <= playDistance) {
                player.playNote(block.getLocation(), instrument, note);
            }
        }
    }
}
