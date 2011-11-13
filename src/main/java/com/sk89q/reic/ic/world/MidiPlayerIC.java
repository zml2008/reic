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

import java.io.File;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import com.sk89q.reic.Family;
import com.sk89q.reic.IC;
import com.sk89q.reic.ICDocumentation;
import com.sk89q.reic.ICException;
import com.sk89q.reic.ReIC;
import com.sk89q.reic.RestrictedIC;
import com.sk89q.reic.State;

public class MidiPlayerIC extends MidiIC {
    
    public MidiPlayerIC(Block block, State state, File file) {
        super(block, state, file);
    }

    public void trigger() {
        State state = getState();
        
        if (state.triggered(0)) {
            if (state.in(0)) {
                stop();
                state.out(0, play());
            }
        } else if (state.triggered(1)) {
            if (state.in(1)) {
                stop();
                state.out(0, false);
            }
        }
    }

    public void unload() {
        stop();
    }

    public static class MidiPlayerICFactory extends MidiICFactory implements RestrictedIC {
        public IC create(ReIC reic, Family family, Block sign, String[] lines) throws ICException {
            File file = validateFile(sign, lines[1]);
            
            expectNoArg(lines, 2);
            expectNoArg(lines, 3);
            
            return new MidiPlayerIC(sign, family.createState(sign), file);
        }

        public boolean canCreate(Player player) {
            return true;
        }

        public String getDescription() {
            return "Plays a specified MIDI file, with separate start and stop inputs.";
        }
        
        public ICDocumentation getDocumentation() {
            return new ICDocumentation()
                    .summary("Plays a specified MIDI file in <ROOT>/midi/*.mid. " +
                            "If the MIDI file doesn't exist, the IC will do nothing.")
                    .param("Filename (without .mid)")
                    .input("Play if HIGH, do nothing if LOW")
                    .input("Stop if HIGH, do nothing if LOW")
                    .output("HIGH if playing, LOW if not playing");
        }
    }

}
