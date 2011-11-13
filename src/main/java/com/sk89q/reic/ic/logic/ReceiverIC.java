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

public class ReceiverIC extends AbstractIC {
    
    private String network;
    
    public ReceiverIC(Block block, State state, String network) {
        super(block, state);
        this.network = network;
        
        TransmitterIC.register(this);
    }

    public void trigger() {
    }

    public void receive(boolean val) {
        getState().out(0, val);
    }

    public void unload() {
        TransmitterIC.unregister(this);
    }
    
    public String getNetwork() {
        return network;
    }
    
    public String getSummary() {
        return "Will receive a signal on the '" + network + "' network.";
    }
    
    public String getDebugInfo() {
        return "Currently aware of " + TransmitterIC.getListenersSize(network)
                + " total listener(s) (including me) on network '" + network + "'";
    }

    public static class ReceiverICFactory extends AbstractICFactory implements CreatedOnChunkLoad {
        public IC create(ReIC reic, Family family, Block sign, String[] lines) throws ICException {
            String network = lines[1].trim();
            
            if (network.length() == 0) {
                throw new ICException("The second line should be a network name.");
            }
            
            expectNoArg(lines, 2);
            expectNoArg(lines, 3);
            
            return new ReceiverIC(sign, family.createState(sign), network);
        }

        public boolean canCreate(Player player) {
            return true;
        }

        public String getDescription() {
            return "Transmits a signal on the given network name.";
        }

        public ICDocumentation getDocumentation() {
            return new ICDocumentation()
                    .summary("Receives a signal on the given network name. Network names are case-sensitive. " +
                            "When a chunk with a receiver IC is loaded, the receiver will not try to sync.")
                    .param("Network name")
                    .output("Value transmitted");
        }
    }

}
