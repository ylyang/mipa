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

/**
 * The <code>Catalog</code> class represents catalog of MIPA components which
 * use Naming server.
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public enum Catalog {
    DataSource,
    ECAManager,
    Predicate,
    NormalProcess,
    Checker,
    Application,
    AbstractGroup,
    Group,
    Numerical,
    Worker,
    FireMan
}
