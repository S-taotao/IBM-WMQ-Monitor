/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="1995,2012" 
 *   crc="2889061024" > 
 *  Licensed Materials - Property of IBM  
 *   
 *  5724-H72,5655-R36,5655-L82,5724-L26, 
 *   
 *  (C) Copyright IBM Corp. 1995, 2012 All Rights Reserved.  
 *   
 *  US Government Users Restricted Rights - Use, duplication or  
 *  disclosure restricted by GSA ADP Schedule Contract with  
 *  IBM Corp.  
 *   </copyright> 
 */

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;

/**
 * Simple example program
 */
public class MQSample {

  // code identifier
  static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/wmqjava/MQSample.java";

  // define the name of the QueueManager
  private static final String qManager = "WCB";
  // and define the name of the Queue
  private static final String qName = "WCB.QUEUE";

  /**
   * Main entry point
   * 
   * @param args - command line arguments (ignored)
   */
  public static void main(String args[]) {
	  MQEnvironment.hostname = "22.188.59.35"; 
	  MQEnvironment.channel = "WCB.CONN";
	  MQEnvironment.port = 1411;
    try {
      // Create a connection to the QueueManager
      System.out.println("Connecting to queue manager: " + qManager);
      MQQueueManager qMgr = new MQQueueManager(qManager);

      // Set up the options on the queue we wish to open
      int openOptions = MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_OUTPUT;

      // Now specify the queue that we wish to open and the open options
      System.out.println("Accessing queue: " + qName);
      MQQueue queue = qMgr.accessQueue(qName, openOptions);

      // Define a simple WebSphere MQ Message ...
      MQMessage msg = new MQMessage();
      // ... and write some text in UTF8 format
      msg.writeUTF("Hello, World!");

      // Specify the default put message options
      MQPutMessageOptions pmo = new MQPutMessageOptions();

      // Put the message to the queue
      System.out.println("Sending a message...");
      queue.put(msg, pmo);

      // Now get the message back again. First define a WebSphere MQ
      // message
      // to receive the data
      MQMessage rcvMessage = new MQMessage();

      // Specify default get message options
      MQGetMessageOptions gmo = new MQGetMessageOptions();

      // Get the message off the queue.
      System.out.println("...and getting the message back again");
      queue.get(rcvMessage, gmo);

      // And display the message text...
      String msgText = rcvMessage.readUTF();
      System.out.println("The message is: " + msgText);

      // Close the queue
      System.out.println("Closing the queue");
      queue.close();

      // Disconnect from the QueueManager
      System.out.println("Disconnecting from the Queue Manager");
      qMgr.disconnect();
      System.out.println("Done!");
    }
    catch (MQException ex) {
      System.out.println("A WebSphere MQ Error occured : Completion Code " + ex.completionCode
          + " Reason Code " + ex.reasonCode);
      ex.printStackTrace();
      for (Throwable t = ex.getCause(); t != null; t = t.getCause()) {
        System.out.println("... Caused by ");
        t.printStackTrace();
      }

    }
    catch (java.io.IOException ex) {
      System.out.println("An IOException occured whilst writing to the message buffer: " + ex);
    }
    return;
  }
}
