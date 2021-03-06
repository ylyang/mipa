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
package net.sourceforge.mipa.predicatedetection.lattice.scp;

import net.sourceforge.mipa.application.ResultCallback;
import net.sourceforge.mipa.predicatedetection.lattice.AbstractLatticeNode;
import net.sourceforge.mipa.predicatedetection.lattice.LatticeChecker;
import net.sourceforge.mipa.predicatedetection.lattice.LocalState;
import static config.Config.LOG_DIRECTORY;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * SCP check based on lattice
 * 
 * @author Tingting Hua<huatingting0820@gmail.com>
 * 
 */
public class SCPLatticeChecker extends LatticeChecker {

	private static final long serialVersionUID = -1172728258002666284L;

	/** store the node when last time check the SCP */
	//private AbstractLatticeNode lastnode;

	private SCPLatticeNode previousnode = null;;

	private PrintWriter out = null;

	private int count;

	public SCPLatticeChecker(ResultCallback application, String predicateID, String checkerName,
			String[] normalProcesses) {
		super(application, predicateID, checkerName, normalProcesses);
		// TODO Auto-generated constructor stub
		try {
			out = new PrintWriter(LOG_DIRECTORY + "/SCP.log");
			out.println("ready!");
		} catch (Exception e) {
			e.printStackTrace();
		}

		count = 0;
	}

	@Override
	public void check(AbstractLatticeNode startNode,
			AbstractLatticeNode currentNode) {
		// TODO Auto-generated method stub

		((SCPLatticeNode) currentNode).settailflag(true);
		detectSCP((SCPLatticeNode) currentNode);
		previousnode = (SCPLatticeNode) currentNode;
		((SCPLatticeNode) currentNode).setvisited(true);
	}

	private void detectSCP(SCPLatticeNode node) {

		ArrayList<SCPLatticeNode> pred = new ArrayList<SCPLatticeNode>();
		for (int i = 0; i < node.getprevious().size(); i++) {
           pred.add((SCPLatticeNode) node.getprevious().get(i));
        }
		
		// if the node is the initial node, pred is null.
		if (pred.size() == 0) {
			previousnode = node;
			//lastnode = previousnode;
			return;
		}

		Iterator<SCPLatticeNode> itor = pred.iterator();
		while (itor.hasNext()) {
			SCPLatticeNode prednode = itor.next();
			if (prednode.getvisited() == false) {
				detectSCP(prednode);
				prednode.setvisited(true);
			}
		}

		boolean flag = true;
		Iterator<SCPLatticeNode> iterator = pred.iterator();
		while (iterator.hasNext()) {
			SCPLatticeNode prenode = iterator.next();
			if (prenode.getpathflag() == false) {
				flag = false;
				break;
			}
		}

		if (node.cgs()) {
			node.setinsideflag(true);
		}

		node.setpathflag(flag || node.cgs());
		
		//set old lattice part false
		/*for(int i=0;i<dimension;i++){
			if(node.getID()[i].compareTo(lastnode.getID()[i])<0){
				node.getSCPNode().setpathflag(false);
			}
		}*/

		// node's pathflag is true
		if (node.getpathflag() == true) {
			// node is the tail of the lattice
			if (node.gettailflag() == true) {
				// the previous node is inside the true frame,the current node
				// is outside
				if ((previousnode.getinsideflag() == true)
						&& (node.getinsideflag() == false)) {
					// detect SCP,out put the information
					try {
						application.callback(String.valueOf(true));
					} catch (Exception e) {
						e.printStackTrace();
					}
					count++;
					out.print(count + " ");
					String[] s = node.getID();
					for (int i = 0; i < s.length; i++) {
						out.print(s[i] + " ");
					}
					LocalState[] gs = node.getglobalState();
					for (int i = 0; i < gs.length; i++) {
						// String end = i + 1 != children.length ? " " : "\r\n";
						out.print("[" + gs[i].getvc().toString() + "] ");
						out.flush();
					}
					out.print("( ");
					for (int i = 0; i < gs.length; i++) {
						String end = i + 1 != children.length ? " " : " )\r\n";
						out.print(gs[i].getintervalID() + end);
						out.flush();
					}
					
					//clear all the previous flag and LP
					node.setpathflag(false);
					
					//lastnode=previousnode;

				}
			}
		}

		/*if (flag && node.getSCPNode().cgs()) {
			// detect SCP
			try {
				application.callback(String.valueOf(true));
			} catch (Exception e) {
				e.printStackTrace();
			}
			count++;
			out.print(count + " ");
			String[] s = node.getID();
			for (int i = 0; i < s.length; i++) {
				out.print(s[i] + " ");
			}
			LocalState[] gs = node.getglobalState();
			for (int i = 0; i < gs.length; i++) {
				// String end = i + 1 != children.length ? " " : "\r\n";
				out.print("[" + gs[i].getvc().toString() + "] ");
				out.flush();
			}
			out.print("( ");
			for (int i = 0; i < gs.length; i++) {
				String end = i + 1 != children.length ? " " : " )\r\n";
				out.print(gs[i].getintervalID() + end);
				out.flush();
			}

			node.getSCPNode().setpathflag(false);

			Iterator<AbstractLatticeNode> iter = pred.iterator();
			String[] position = node.getID();
			while (iter.hasNext()) {
				AbstractLatticeNode prenode = iterator.next();
				// if (prenode.getID()[]) {

				// }
			}

		} else {
			node.getSCPNode().setpathflag(flag || node.getSCPNode().cgs());
		}*/

	}

	@Override
	public SCPLatticeNode createNode(LocalState[] globalState, String[] s) {
		// TODO Auto-generated method stub
		SCPLatticeNode node = new SCPLatticeNode(globalState, s);
		return node;
	}

}
