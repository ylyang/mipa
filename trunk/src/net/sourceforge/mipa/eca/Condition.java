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
package net.sourceforge.mipa.eca;

/**
 * Condition of ECA mechanism.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class Condition {

    /** reference to action of ECA */
    private Listener action;

    public Condition(Listener action) {
        this.action = action;
    }

    /**
     * called by DataSource for notifying event change.
     * 
     * @param eventName
     *            event name
     * @param value
     *            event value
     * @see DataSource
     */
    public void update(String eventName, String value) {

    }

    /**
     * notify the action of ECA mechanism.
     * 
     * @param eventName
     *            event name
     */
    public void notifyListener(String eventName) {

    }
}