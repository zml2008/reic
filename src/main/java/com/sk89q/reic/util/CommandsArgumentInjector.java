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

package com.sk89q.reic.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.sk89q.minecraft.util.commands.Injector;

public class CommandsArgumentInjector implements Injector {

    private Object[] args;
    private Class<?>[] signature;
    
    public CommandsArgumentInjector(Object ... args) {
        this.args = args;
        signature = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            signature[i] = args[i].getClass();
        }
    }

    public Object getInstance(Class<?> cls) throws InvocationTargetException,
            IllegalAccessException, InstantiationException {
        Constructor<?> constr;
        try {
            constr = cls.getConstructor(signature);
        } catch (SecurityException e) {
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
        return constr.newInstance(args);
    }
    

}
