package samples;
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="2635134740" > 
 *  Licensed Materials - Property of IBM  
 *   
 *  5724-H72,5655-R36,5655-L82,5724-L26, 
 *   
 *  (C) Copyright IBM Corp. 2008, 2012 All Rights Reserved.  
 *   
 *  US Government Users Restricted Rights - Use, duplication or  
 *  disclosure restricted by GSA ADP Schedule Contract with  
 *  IBM Corp.  
 *   </copyright> 
 */

import java.io.IOException;
import java.util.Arrays;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFException;
import com.ibm.mq.headers.pcf.PCFMessageAgent;

/**
 * <u>How to use this sample</u><br>
 * The PCF samples can be run independently or in conjunction with each other. An example of how
 * they can be linked together is shown below:-<br>
 * <br>
 * <u>MQ Queue Manager Control</u>
 * <ul>
 * WalkThroughQueueManagerAttributes
 * </ul>
 * <p>
 * <u>MQ Queue Control</u>
 * <ul>
 * DisplayActiveLocalQueues<br>
 * ListQueueNames<br>
 * CreateQueue<br>
 * ClearQueue<br>
 * DeleteQueue
 * </ul>
 * <p>
 * <u>MQ Channel Control</u>
 * <ul>
 * DisplayActiveLocalChannels<br>
 * DisplayConnections<br>
 * CreateChannel<br>
 * StopChannel<br>
 * StartChannel<br>
 * DeleteChannel<br>
 * </ul>
 * <br>
 * <b>Note that this sample cannot be run independently (there is no main method), but this class
 * must be included by all other PCF samples in this suite.</b>
 * <p>
 * The sample can bind to a local queue manager using local bindings by using the following
 * parameters:-<br>
 * PCF_Sample QueueManager<br>
 * <br>
 * e.g. PCF_Sample QM1<br>
 * <br>
 * Or the sample can bind to a remote queue manager using client bindings by using the following
 * parameters:-<br>
 * PCF_Sample QueueManager Host Port<br>
 * <br>
 * e.g. PCF_Sample QM1 localhost 1414<br>
 * <br>
 * <b> N.B. When binding to another machine it is assumed that any intervening firewall has been
 * prepared to allow this connection.</b>
 */

public class PCF_CommonMethods {

  // @COPYRIGHT_START@
  /** Comment for copyright_notice */
  static final String copyright_notice = "Licensed Materials - Property of IBM "
      + "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
      + "(c) Copyright IBM Corp. 2008, 2009 All Rights Reserved. "
      + "US Government Users Restricted Rights - Use, duplication or "
      + "disclosure restricted by GSA ADP Schedule Contract with " + "IBM Corp.";
  // @COPYRIGHT_END@

  /** The SCCSID which is expanded when the file is extracted from CMVC */
  public static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/pcf/PCF_CommonMethods.java"; //$NON-NLS-1$

  /**
   * Constant used to identify the PCF queue name that will be generated/used by some of the PCF
   * samples.
   */
  public static final String pcfQueue = "WCB.QUEUE";

  /**
   * Constant used to identify the PCF channel name that will be generated/used by some of the PCF
   * samples.
   */
  public static final String pcfChannel = "SYSTEM.ADMIN.SVRCONN";

  /**
   * If used, will contain the hostname of the machine that is running the queue manager. For a
   * queue manager running on the local machine this would be localhost or 127.0.0.1.
   */
  public String host = "22.188.59.35";

  /**
   * If used, will contain the port of the queue listener. The default is usually 1414.
   */
  public int port = 1411;

  /**
   * Name of the queue manager.
   */
  public String queueManager = "WCB";

  /**
   * Object containing the instance of the PCFMessageAgent.
   */
  public PCFMessageAgent agent = null;

  /**
   * Boolean used to indicate whether the connection is using client or server bindings.
   */
  public boolean client = false;

  /**
   * Name of the default channel. This is set to "SYSTEM.DEF.SVRCONN".
   */
  public String channel = "WCB.CONN";

  /**
   * Used to pad out formatting of information in the samples.
   */
  public String padding = null;

  /**
   * Default constructor. The constructor is used to initialise the variables.<br>
   * <b>Note that this sample cannot be run independently, but this class must be included by all
   * other PCF samples in this suite.</b><br>
   * The PCF samples can be run independently or in conjunction. An example of how they can be
   * linked together is shown below:-<br>
   * <u>MQ Queue Manager Control</u><br>
   * <ul>
   * WalkThroughQueueManagerAttributes
   * </ul>
   * <p>
   * <u>MQ Queue Control</u><br>
   * <ul>
   * DisplayActiveLocalQueues<br>
   * ListQueueNames<br>
   * CreateQueue<br>
   * ClearQueue<br>
   * DeleteQueue
   * </ul>
   * <p>
   * <u>MQ Channel Control</u><br>
   * <ul>
   * DisplayActiveLocalChannels<br>
   * DisplayConnections<br>
   * CreateChannel<br>
   * StopChannel<br>
   * StartChannel<br>
   * DeleteChannel<br>
   * </ul>
   */
  public PCF_CommonMethods() {
    char[] space = new char[64];

    Arrays.fill(space, 0, space.length, ' ');

    padding = new String(space);
    return;
  }

  /**
   * Input parameter parser. This method looks at the input parameter list. The PCF sample can be
   * called with either just a queue manager name or with queue manager, IP address of the machine
   * running the queue manager and the port on which it is listening. If just the queue manager is
   * defined, then the sample will use local bindings. If the queue manager name, host and port are
   * defined, then the sample will use client bindings.
   * 
   * @param args Command line arguments.
   * @return boolean, set to true if the parameters where successfully read.
   */
  public boolean ParseParameters(String[] args) {
    int flags = 0;

    for (int argIndex = 0; argIndex < args.length; argIndex++) {
      switch (argIndex) {
        case 0 : // Queue Mananger's name.
        {
          queueManager = args[argIndex];

          flags |= 1;
          break;
        }

        case 1 : // Host (machine name).
        {
          host = args[argIndex];
          flags |= 2;
          break;
        }

        case 2 : // Port.
        {
          port = new Integer(args[argIndex]).intValue();
          flags |= 4;
          break;
        }
      }
    }

    if (((args.length == 1) && (flags == 1)) || ((args.length > 2) && (flags == 7))) {
      return true;
    }

    System.out.println("The PCF samples must be used with the following parameters:-");
    System.out.println("Either:-");
    System.out.println("PCF_Sample QueueManager");
    System.out.println("e.g. PCF_Sample QM1");
    System.out.println("or");
    System.out.println("PCF_Sample QueueManager Host Port");
    System.out.println("e.g. PCF_Sample QM1 localhost 1414");

    return false;
  }

  /**
   * Create an agent. This method is used by all PCF samples to create the PCFAgent object using the
   * queue manager name (local server bindings) or the host and port (client bindings). Note, when
   * connecting using client bindings, the queue manager name is not used (it assumes the queue
   * manager required will be the default).
   * 
   * @param numOfArgs Used to determine if the agent should be bound to a local server or to a
   *          client.
   * @throws MQDataException
   */
  public void CreateAgent(int numOfArgs) throws MQDataException {
    try {
      if (numOfArgs == 1) {
        client = false;

        // Connect to the local queue manager.
        agent = new PCFMessageAgent(queueManager);
      }
      else {
        client = true;

        // Connect to the client and define the queue manager host, port and channel.
        // Notice that the method does not take a queue manager name. It is assuming that the
        // default QM will be used.
        agent = new PCFMessageAgent(host, port, channel);
      }
    }
    catch (MQDataException mqde) {
      if (mqde.reasonCode == CMQC.MQRC_Q_MGR_NAME_ERROR) {
        System.out.print("Either could not find the ");
        if (client) {
          System.out.print("default queue manager at \"" + host + "\", port \"" + port + "\"");
        }
        else {
          System.out.print("queue manager \"" + queueManager + "\"");
        }

        System.out.println(" or could not find the default channel \"" + channel
            + "\" on the queue manager.");
      }

      throw mqde;
    }
    return;
  }

  /**
   * Destroy the agent. This method is used by all PCF samples to destroy the PCFAgent.
   * 
   * @throws MQDataException
   */
  public void DestroyAgent() throws MQDataException {
    // Disconnect the agent.
    agent.disconnect();
    return;
  }

  /**
   * Common method for displaying the error messages of a caught exception.
   * 
   * @param exception Caught exception object.
   */
  public void DisplayException(Exception exception) {
    if (exception.getClass().equals(PCFException.class)) {
      // PCFException exception handling.
      PCFException pcfe = (PCFException) exception;

      if (pcfe.reasonCode == MQConstants.MQRCCF_CHANNEL_NOT_FOUND) {
        if (client) {
          System.out.print("Either the queue manager \"" + queueManager + "\"");
        }
        else {
          System.out.print("Either the default queue manager");
        }

        System.out.println(" or the channel \"" + pcfChannel
            + "\" on the queue manager could not be found.");
      }
      else {
        System.err.println(pcfe + ": " + MQConstants.lookupReasonCode(pcfe.reasonCode));
      }
    }
    else if (exception.getClass().equals(IOException.class)) {
      // IOException exception handling.
      IOException ioe = (IOException) exception;

      System.err.println(ioe);
    }
    else if (exception.getClass().equals(MQDataException.class)) {
      // MQDataException exception handling.
      MQDataException de = (MQDataException) exception;

      System.err.println(de + ": " + MQConstants.lookupReasonCode(de.reasonCode));
    }
    else if (exception.getClass().equals(MQException.class)) {
      // MQException exception handling.
      MQException mqe = (MQException) exception;

      if (mqe.reasonCode == CMQC.MQRC_UNKNOWN_OBJECT_NAME) {
        System.out.println("Cound not find the queue \"" + pcfQueue + "\" on the queue manager \""
            + queueManager + "\".");
      }
      else {
        System.err.println(mqe + ": " + MQConstants.lookupReasonCode(mqe.reasonCode));
      }
    }
    return;
  }
}
