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
import org.bukkit.entity.Player;

public final class ICConfiguration {

    private String id;
    private ICFactory factory;
    private Family family;
    
    ICConfiguration(String id, ICFactory factory, Family family) {
        this.id = id;
        this.factory = factory;
        this.family = family;
    }

    public String getId() {
        return id;
    }

    public ICFactory getFactory() {
        return factory;
    }
    
    public Family getFamily() {
        return family;
    }
    
    public IC create(ReIC reIC, Block block, String[] lines) throws ICException {
        return getFactory().create(reIC, getFamily(), block, lines);
    }
    
    public IC create(ReIC reIC, Block block, String[] lines, Player player) throws ICException {
        return getFactory().create(reIC, getFamily(), block, lines, player);
    }
    
}
