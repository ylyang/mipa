/* 
 * MIPA - Middleware Infrastructure for Predicate detection in Asynchronous 
 * environments
 * 
 * Copyright (C) 2009 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the term of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.mipa.naming;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class IDManagerImp implements IDManager {
    /** ID count map */
    private Map<Catalog, Integer> map;
    
    private int i = 0;
    
    public IDManagerImp() {
        map = new HashMap<Catalog, Integer>();
        for(Catalog catalog : Catalog.values()) {
            map.put(catalog, new Integer(0));
        }
    }

    @Override
    public synchronized String getID(Catalog prefix) throws RemoteException {
        if(prefix == Catalog.Numerical) {
            i++;
            return String.valueOf(i);
        }
        
        Integer count = map.get(prefix);
        String ID = prefix.toString() + count;
        map.put(prefix, new Integer(count.intValue() + 1));
        
        //System.err.println("Prefix: "+prefix+" "+ID);
        return ID;
    }

}
