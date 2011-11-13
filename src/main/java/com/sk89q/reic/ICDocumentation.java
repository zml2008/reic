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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

public class ICDocumentation {

    private String summary = "";
    private String allInputs;
    private final Map<Integer, String> inputs = new HashMap<Integer, String>();
    private final Map<Integer, String> outputs = new HashMap<Integer, String>();
    private final Map<Integer, String> parameters = new HashMap<Integer, String>();
    
    public ICDocumentation summary(String summary) {
        this.summary = summary;
        return this;
    }
    
    private ICDocumentation param(int num, String message) {
        parameters.put(num, message);
        return this;
    }
    
    public ICDocumentation param(String message) {
        param(inputs.size(), message);
        return this;
    }
    
    public ICDocumentation params(String message) {
        for (int i = parameters.size(); i < 4; i++) {
            param(i, message);
        }
        return this;
    }
    
    private ICDocumentation input(int num, String message) {
        inputs.put(num, message);
        return this;
    }
    
    public ICDocumentation input(String message) {
        input(inputs.size(), message);
        return this;
    }

    public ICDocumentation inputs(String message) {
        this.allInputs = message;
        return this;
    }
    
    private ICDocumentation output(int num, String message) {
        outputs.put(num, message);
        return this;
    }
    
    public ICDocumentation output(String message) {
        output(outputs.size(), message);
        return this;
    }
    
    private void appendFields(StringBuilder str, Map<Integer, String> map, String name, ChatColor color) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            str.append("\n");
            str.append(color);
            str.append(name);
            str.append(" #");
            str.append(entry.getKey());
            str.append(": ");
            str.append(ChatColor.WHITE);
            str.append(entry.getValue());
        }
    }

    public String toString() {
        StringBuilder str = new StringBuilder(summary);
        appendFields(str, parameters, "Param", ChatColor.BLUE);
        appendFields(str, inputs, "Input", ChatColor.GREEN);
        if (allInputs != null) {
            appendFields(str, inputs, "Input", ChatColor.GREEN);
            str.append("Inputs: ");
            str.append(ChatColor.WHITE);
            str.append(allInputs);
        }
        appendFields(str, outputs, "Output", ChatColor.LIGHT_PURPLE);
        return str.toString();
    }
    
}
