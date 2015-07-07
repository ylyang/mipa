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

import net.sourceforge.mipa.application.ResultCallback;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.components.Message;
import net.sourceforge.mipa.components.TimedMessageContent;
import net.sourceforge.mipa.predicatedetection.AbstractFIFOChecker;
import net.sourceforge.mipa.predicatedetection.Structure;
import net.sourceforge.mipa.predicatedetection.TimedModality;

/**
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class TSCPChecker extends AbstractFIFOChecker {

    private static final long serialVersionUID = -4933006939390647117L;

    private ArrayList<ArrayList<TimedMessageContent>> queues;
    
    private PrintWriter out = null;
    
    private PrintWriter outTime = null;
    
    int number = 0;
    
    private Structure specification;
    
    private long epsilon;
    
	public long responseTime = 0;
    
    /**
     * @param application
     * @param checkerName
     * @param normalProcesses
     */
    public TSCPChecker(ResultCallback application, String predicateID, String checkerName,
                      String[] normalProcesses, Structure specification) {
        super(application, predicateID, checkerName, normalProcesses);
        this.specification = specification;
        epsilon = MIPAResource.getEpsilon();
        queues = new ArrayList<ArrayList<TimedMessageContent>>();
        for (int i = 0; i < normalProcesses.length; i++) {
            queues.add(new ArrayList<TimedMessageContent>());
        }
        
//        if(ENABLE_PHYSICAL_CLOCK) {
            try {
                //out = new PrintWriter(LOG_DIRECTORY + "/found_interval.log");
                outTime = new PrintWriter(LOG_DIRECTORY
       				 + "/TSCP_Time.log");
            } catch (Exception e) {
                e.printStackTrace();
            }
//        }
    }
    
    // FIX bug of issue 8 at http://mipa.googlecode.com
    private void addOnce(ArrayList<Integer> list, Integer num) {
        boolean already = false;
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).intValue() == num.intValue()) {
                already = true;
                break;
            }
        }
        if(already == false) list.add(num);
    }
    
    protected void handle(ArrayList<Message> messages) {
    	long time_s = System.nanoTime();
    	String normalProcess = messages.get(0).getSenderID();
        int id = nameToID.get(normalProcess).intValue();
        
        ArrayList<TimedMessageContent> queue = queues.get(id);
        
        ArrayList<TimedMessageContent> contents = new ArrayList<TimedMessageContent> ();
        for(int i = 0; i < messages.size(); i++) {
            contents.add((TimedMessageContent) messages.get(i).getMessageContent());
        }
        for(int i = 0; i < contents.size(); i++) {
        	queue.add(contents.get(i));
        }
        if (queue.size() == contents.size()) {
    		check(id);
    	}
        long time_e = System.nanoTime();
        //for(int i = 0; i<messages.size();i++) {
        //outTime.println((time_e - time_s)*1.0/messages.size());
	        outTime.println((time_e - time_s)*1.0);
	        outTime.flush();
        //}
    }
    
    private void check(int id) {
		// TODO Auto-generated method stub
    	ArrayList<Integer> changed = new ArrayList<Integer>();
        changed.add(new Integer(id));
        while (true) {
            while (changed.size() != 0) {
                ArrayList<Integer> newchanged = new ArrayList<Integer>();
                for (int i = 0; i < changed.size(); i++) {
                    int elem = changed.get(i).intValue();
                    for (int j = 0; j < children.length; j++) {
                        if (elem == j)
                            continue;
                        ArrayList<TimedMessageContent> qi = queues.get(elem);
                        ArrayList<TimedMessageContent> qj = queues.get(j);
                        if (qi.size() != 0 && qj.size() != 0) {
                        	TimedMessageContent qiHead = qi.get(0);
                        	TimedMessageContent qjHead = qj.get(0);
                            if (qjHead.getStartTime() + epsilon > qiHead.getEndTime() - epsilon){
                                addOnce(newchanged, new Integer(elem));
                            }
                            if (qiHead.getStartTime() + epsilon > qjHead.getEndTime() - epsilon){
                                addOnce(newchanged, new Integer(j));
                            }
                        }
                    } // end for j
                }// end for i
                changed = newchanged;
                for (int i = 0; i < changed.size(); i++) {
                    int elem = changed.get(i).intValue();
                    queues.get(elem).remove(0);
                }
            }// end while
            boolean found = true; 
            for (int i = 0; i < children.length; i++) {
                if (queues.get(i).size() == 0) {
                    found = false;
                    break;
                }
            }
            if (found == true) {
            	boolean flag = false;
            	long activityStartTime = 0;
            	long activityEndTime = Long.MAX_VALUE;
            	int toBeDelete = -1;
            	for (int i = 0; i < children.length; i++) {
            		TimedMessageContent qiHead = queues.get(i).get(0);
            		long startTime = qiHead.getStartTime() + epsilon;
            		activityStartTime = Math.max(activityStartTime, startTime);
            		long endTime = qiHead.getEndTime() - epsilon;
            		if(endTime < activityEndTime) {
            			toBeDelete = i;
            		}
            		activityEndTime = Math.min(activityEndTime, endTime);
            	}
            	long duration = activityEndTime - activityStartTime;
            	String operator = ((TimedModality)specification.getChildren().get(1)).getOp();
            	String bound = ((TimedModality)specification.getChildren().get(1)).getBound();
            	switch (operator) {
					case "greater-than":
						if(duration > Long.valueOf(bound)) {
							flag = true;
						}
						break;
					case "greater-or-equal":
						if(duration >= Long.valueOf(bound)) {
							flag = true;
						}
						break;
					case "less-than":
						if(duration < Long.valueOf(bound)) {
							flag = true;
						}
						break;
					case "less-or-equal":
						if(duration <= Long.valueOf(bound)) {
							flag = true;
						}
						break;
					default:
						break;
				}
            	if(flag == true) {
//            		try {
//                    	if(number == 0) {
//                    		System.out.println("The predicate "+ predicateID + " is satisfied.");
//                    		application.callback(String.valueOf(true));
//                    		number++;
//                    	}
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
            		// detection found
                    for (int i = 0; i < children.length; i++) {
                    	TimedMessageContent foundContent = queues.get(i).remove(0);
                        
//                        if(ENABLE_PHYSICAL_CLOCK) {
//                            long lo = foundContent.getStartTime();
//                            long hi = foundContent.getEndTime();
//                            try {
//                                String end = i + 1 != children.length ? " " : "\n";
//                                out.print(lo + " " + hi + end);
//                                out.flush();
//                            } catch(Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
                        changed.add(new Integer(i));
                    }
            	}
            	else {
            		queues.get(toBeDelete).remove(0);
            		changed.add(new Integer(toBeDelete));
            	}
            } else
                break;
        }
	}
}
