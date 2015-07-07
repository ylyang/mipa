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
package net.sourceforge.mipa.predicatedetection.normal.tscp;

import static config.Config.ENABLE_PHYSICAL_CLOCK;
import static config.Config.LOG_DIRECTORY;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.components.MessageType;
import net.sourceforge.mipa.components.TimedMessageContent;
import net.sourceforge.mipa.naming.Catalog;
import net.sourceforge.mipa.naming.IDManager;
import net.sourceforge.mipa.predicatedetection.AbstractNormalProcess;
import net.sourceforge.mipa.predicatedetection.VectorClock;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class TSCPNormalProcess extends AbstractNormalProcess {

	private static final long serialVersionUID = -2040352661249255553L;

	private long pTimeLo;

	private PrintWriter out;

	private Map<String, Long> currentMessageCount;

	private boolean localPredicate;

	private long lastTime;

	private long currentTime;

	/**
	 * @param name
	 */
	public TSCPNormalProcess(String name, String[] checkers,
			String[] normalProcesses) {
		super(name, checkers, normalProcesses);

		// TODO Auto-generated constructor stub
		currentMessageCount = new HashMap<String, Long>();
		for (int i = 0; i < checkers.length; i++) {
			currentMessageCount.put(checkers[i], new Long(0));
		}

		localPredicate = false;

		if (ENABLE_PHYSICAL_CLOCK) {
			try {
				out = new PrintWriter(LOG_DIRECTORY + "/" + name + ".log");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void receiveMsg(Message message) {

	}

	@Override
	public void action(String value) {
		// TODO Auto-generated method stub
		String[] values = value.split("\\s+");
		boolean newValue = Boolean.parseBoolean(values[0]);
		currentTime = Long.parseLong(values[1]);
		if (localPredicate == false && newValue == true) {
			localPredicate = newValue;
			lastTime = currentTime;
		} else if (newValue == false && localPredicate == true) {
			localPredicate = newValue;
			TimedMessageContent content = new TimedMessageContent(true,
					lastTime, currentTime);
			for (int i = 0; i < checkers.length; i++) {
				String checker = checkers[i];
				send(MessageType.Detection, checker, content);
			}
		}
	}

	private void send(MessageType type, String receiverName,
			TimedMessageContent content) {
		Message m = new Message();
		m.setType(type);
		m.setSenderID(name);
		m.setReceiverID(receiverName);
		m.setMessageContent(content);
		
		if (currentMessageCount.containsKey(receiverName) == true) {
			long currentCount = currentMessageCount.get(receiverName);
			m.setMessageID(currentCount);
			currentMessageCount.put(receiverName, new Long(currentCount + 1));
		} else {
			assert (false);
		}
		sender.send(m);
	}

	@Override
	public void application() {
		// TODO Auto-generated method stub

	}
}
