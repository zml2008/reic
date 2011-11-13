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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

public class TransmitterIC extends AbstractIC {
    
    private static Map<String, List<ReceiverIC>> listeners = new HashMap<String, List<ReceiverIC>>();
    
    private String network;
    
    public static void register(ReceiverIC ic) {
        List<ReceiverIC> list = listeners.get(ic.getNetwork());
        if (list == null) {
            list = new LinkedList<ReceiverIC>();
            listeners.put(ic.getNetwork(), list);
        }
        list.add(ic);
    }
    
    public static void unregister(ReceiverIC ic) {
        List<ReceiverIC> list = listeners.get(ic.getNetwork());
        if (list != null) {
            list.remove(ic);
            if (list.size() == 0) {
                listeners.remove(ic.getNetwork());
            }
        }
    }
    
    public static void dispatch(String network, boolean val) {
        List<ReceiverIC> list = listeners.get(network);
        if (list != null) {
            for (ReceiverIC ic : list) { 
                ic.receive(val);
            }
        }
    }
    
    public static int getListenersSize(String network) {
        List<ReceiverIC> list = listeners.get(network);
        return list == null ? 0 : list.size();
    }
    
    public TransmitterIC(Block block, State state, String network) {
        super(block, state);
        this.network = network;
    }

    public void trigger() {
        State state = getState();
        
        if (state.triggered(0) || (state.triggered(1) && state.in(1))) {
            dispatch(network, state.in(0));
        }
    }

    public void unload() {
    }
    
    public String getNetwork() {
        return network;
    }
    
    public String getSummary() {
        return "Will transmit a signal on the '" + network + "' network.";
    }
    
    public String getDebugInfo() {
        return "Currently aware of " + getListenersSize(network) + " listener(s) on network '" + network + "'";
    }

    public static class TransmitterICFactory extends AbstractICFactory {
        public IC create(ReIC reic, Family family, Block sign, String[] lines) throws ICException {
            String network = lines[1].trim();
            
            if (network.length() == 0) {
                throw new ICException("The second line should be a network name.");
            }
            
            expectNoArg(lines, 2);
            expectNoArg(lines, 3);
            
            return new TransmitterIC(sign, family.createState(sign), network);
        }

        public boolean canCreate(Player player) {
            return true;
        }

        public String getDescription() {
            return "Transmits a signal on the given network name.";
        }

        public ICDocumentation getDocumentation() {
            return new ICDocumentation()
                    .summary("Transmits a signal on the given network name. Network names are case-sensitive. " +
                    		"The receiver receives the signal within the same tick. " +
                            "Receivers must be in a loaded chunk for the signal to be received. " +
                    		"If a chunk with a receiver is later loaded, the receiver will not try to sync with the transmitter until the transmitter transmits again.")
                    .param("Network name")
                    .input("Value to transmit")
                    .input("HIGH to force a transmit, LOW to do nothing");
        }
    }

}
