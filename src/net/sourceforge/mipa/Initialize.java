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
package net.sourceforge.mipa;

import static config.Debug.DEBUG;

import java.io.File;
import java.rmi.server.UnicastRemoteObject;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sourceforge.mipa.components.ContextMapping;
import net.sourceforge.mipa.components.ContextRegister;
import net.sourceforge.mipa.components.ContextRegisterImp;
import net.sourceforge.mipa.components.Coordinator;
import net.sourceforge.mipa.components.CoordinatorImp;
import net.sourceforge.mipa.components.MIPAResource;
import net.sourceforge.mipa.components.MessageDispatcher;
import net.sourceforge.mipa.components.NoDelayMessageDispatcher;
import net.sourceforge.mipa.naming.IDManager;
import net.sourceforge.mipa.naming.IDManagerImp;
import net.sourceforge.mipa.naming.Naming;
import net.sourceforge.mipa.predicatedetection.PredicateParser;
import net.sourceforge.mipa.predicatedetection.PredicateParserMethod;

import org.w3c.dom.Document;

/**
 * MIPA platform initialization.
 * 
 * @author Jianping Yu <jianp.yue@gmail.com>
 */
public class Initialize {

    /**
     * initialize MIPA Infrastructure.
     * 
     * @return true if successful, false otherwise
     */
    public boolean initialize() {

        parseConfig("config.xml");

        try {
            // binds predicate parser
            Naming server = MIPAResource.getNamingServer();

            if (DEBUG) {
                System.out.println("Creating IDManager...");
            }

            IDManagerImp idManager = new IDManagerImp();
            IDManager managerStub = (IDManager) UnicastRemoteObject
                                                                   .exportObject(
                                                                                 idManager,
                                                                                 0);
            server.bind("IDManager", managerStub);

            if (DEBUG) {
                System.out.println("Creating ContextRegister...");
            }

            ContextMapping contextMapping = new ContextMapping();

            ContextRegisterImp contextRegister = new ContextRegisterImp(
                                                                        contextMapping);
            ContextRegister contextRegisterStub = (ContextRegister) UnicastRemoteObject
                                                                                       .exportObject(
                                                                                                     contextRegister,
                                                                                                     0);
            server.bind("ContextRegister", contextRegisterStub);

            //RandomDelayMessageDispatcher messageDispatcher = new RandomDelayMessageDispatcher();
            NoDelayMessageDispatcher messageDispatcher = new NoDelayMessageDispatcher();
            MessageDispatcher messageDispatcherStub = (MessageDispatcher) UnicastRemoteObject
                                                                                             .exportObject(
                                                                                                           messageDispatcher,
                                                                                                           0);
            server.bind("MessageDispatcher", messageDispatcherStub);
            
            Thread t = new Thread(messageDispatcher);
            t.start();

            if (DEBUG) {
                System.out.println("Creating PredicateParser...");
            }
            PredicateParser predicateParser = new PredicateParser(
                                                                  contextMapping);
            PredicateParserMethod predicateParserStub = (PredicateParserMethod) UnicastRemoteObject
                                                                                                   .exportObject(
                                                                                                                 predicateParser,
                                                                                                                 0);

            server.bind("PredicateParser", predicateParserStub);
            
            CoordinatorImp coordinator = new CoordinatorImp();
            Coordinator coordinatorStub = (Coordinator) UnicastRemoteObject.exportObject(coordinator, 0);
            server.bind("Coordinator", coordinatorStub);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void parseConfig(String fileName) {
        // TODO parses config file and sets MIPAResource
        try {
            File f = new File(fileName);

            DocumentBuilderFactory factory = DocumentBuilderFactory
                                                                   .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(f);

            String address = doc.getElementsByTagName("address").item(0)
                                .getFirstChild().getNodeValue();

            String port = doc.getElementsByTagName("port").item(0)
                             .getFirstChild().getNodeValue();

            MIPAResource
                        .setNamingAddress("rmi://" + address + ":" + port + "/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        boolean result = new Initialize().initialize();
        if (result == true) {
            System.out.println("Initialization finished.");
        } else {
            System.out.println("Error occurs when initializing");
        }

        if (DEBUG) {
            System.out.println(MIPAResource.getNamingAddress());
        }
    }
}