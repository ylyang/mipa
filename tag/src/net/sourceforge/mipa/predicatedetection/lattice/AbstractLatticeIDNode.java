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
package net.sourceforge.mipa.predicatedetection.lattice;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Yiling Yang <csylyang@gmail.com>
 *
 */
public class AbstractLatticeIDNode implements Serializable {

    private static final long serialVersionUID = -2194410751685873911L;

    protected String[] ID;

    protected LocalState[] globalState;

    public AbstractLatticeIDNode(LocalState[] gs, String[] s) {
        ID = s;
        globalState = new LocalState[gs.length];
        for (int i = 0; i < gs.length; i++) {
            globalState[i] = gs[i];
        }
    }

    public String[] getID() {
        return ID;
    }

    public LocalState[] getGlobalState() {
        return globalState;
    }
    
    public int hashCode() {
        return (StringUtils.join(ID)).hashCode();
    }
    
    public boolean equals(AbstractLatticeIDNode node) {
        return hashCode() == node.hashCode();
    }
}