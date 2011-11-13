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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sk89q.reic.util.BestResultsAggregator;
import com.sk89q.reic.util.StringUtil;

public class ICConfigurationManager {
    
    private final Map<String, ICConfiguration> configurations;
    
    public ICConfigurationManager() {
        configurations = new HashMap<String, ICConfiguration>();
    }
    
    public void register(String id, ICFactory factory, Family family, String ... aliases) {
        ICConfiguration configuration = new ICConfiguration(id, factory, family);
        configurations.put(id.toLowerCase(), configuration);
        
        for (String alias : aliases) {
            configurations.put(alias.toLowerCase(), configuration);
        }
    }
    
    public ICConfiguration get(String id) {
        return configurations.get(id.toLowerCase());
    }
    
    public List<String> findSimiliarIDs(String id) {
        id = id.toLowerCase();
        
        BestResultsAggregator<String> results =
                new BestResultsAggregator<String>(5, BestResultsAggregator.Order.WEIGHT_LOWER);
        
        for (Map.Entry<String, ICConfiguration> entry : configurations.entrySet()) {
            String testLower = entry.getKey().toLowerCase();
            if (id.charAt(0) != testLower.charAt(0)) continue;
            int dist = StringUtil.getLevenshteinDistance(id, testLower.toLowerCase());
            if (dist > 10) continue;
            results.add(entry.getKey(), dist);
        }
        
        return results.getResults();
    }
    
    public Collection<ICConfiguration> getUniqueICs() {
        Map<String, ICConfiguration> unique = new HashMap<String, ICConfiguration>();

        for (Map.Entry<String, ICConfiguration> entry : configurations.entrySet()) {
            unique.put(entry.getValue().getId(), entry.getValue());
        }
        
        return unique.values();
    }

    public List<String> getAliases(ICConfiguration configuration) {
        List<String> aliases = new ArrayList<String>();

        for (Map.Entry<String, ICConfiguration> entry : configurations.entrySet()) {
            if (entry.getValue() == configuration) {
                aliases.add(entry.getKey().toUpperCase());
            }
        }
        
        return aliases;
    }

}
