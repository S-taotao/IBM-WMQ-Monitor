package samples;
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="1650483073" > 
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
import java.util.Hashtable;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFException;
import com.ibm.mq.headers.pcf.PCFMessage;

/**
 * <u>How to use this sample</u><br>
 * The sample demonstrates how PCF commands can be used to clear messages from a queue.
 * <p>
 * The sample can bind to a local queue manager using local bindings by using the following
 * parameters:-<br>
 * PCF_ClearQueue QueueManager<br>
 * <br>
 * e.g. PCF_ClearQueue QM1<br>
 * <br>
 * Or the sample can bind to a remote queue manager using client bindings by using the following
 * parameters:-<br>
 * PCF_ClearQueue QueueManager Host Port<br>
 * <br>
 * e.g. PCF_ClearQueue QM1 localhost 1414<br>
 * <br>
 * <b> N.B. When binding to another machine it is assumed that any intervening firewall has been
 * prepared to allow this connection.</b><br>
 * <br>
 * A typical output when running this sample when a valid queue is present would be:- <br>
 * <br>
 * <code><font face="courier, monospaced">
 * +-----+------------------------------------------------+-----+<br>
 * |Index|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * Queue Name (before)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Depth|<br>
 * +-----+------------------------------------------------+-----+<br>
 * |0&nbsp;&nbsp;&nbsp;&nbsp;|PCFQUEUE&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;|4&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * +-----+------------------------------------------------+-----+<br>
 * +-----+------------------------------------------------+-----+<br>
 * |Index|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * Queue Name (after)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Depth|<br>
 * +-----+------------------------------------------------+-----+<br>
 * |0&nbsp;&nbsp;&nbsp;&nbsp;|PCFQUEUE&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;|0&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * +-----+------------------------------------------------+-----+<br></font></code>
 * 
 */

public class PCF_ClearQueue {

  // @COPYRIGHT_START@
  /** Comment for copyright_notice */
  static final String copyright_notice = "Licensed Materials - Property of IBM "
      + "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
      + "(c) Copyright IBM Corp. 2008, 2009 All Rights Reserved. "
      + "US Government Users Restricted Rights - Use, duplication or "
      + "disclosure restricted by GSA ADP Schedule Contract with " + "IBM Corp.";
  // @COPYRIGHT_END@

  /** The SCCSID which is expanded when the file is extracted from CMVC */
  public static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/pcf/PCF_ClearQueue.java"; //$NON-NLS-1$

  /**
   * PCF sample entry function. When calling this sample, use either of the following formats:-
   * <p>
   * <table border="1">
   * <tr>
   * <td>Format</td>
   * <td>Example</td>
   * <td>Information</td>
   * </tr>
   * <tr>
   * <td>PCF_Sample QueueManager</td>
   * <td>PCF_Sample QM</td>
   * <td>Use this prototype when connecting to a local queue manager.</td>
   * </tr>
   * <tr>
   * <td>PCF_Sample QueueManager Host Port</td>
   * <td>PCF_Sample QM localhost 1414</td>
   * <td>Use this prototype when connecting to a queue manager using client bindings.</td>
   * </tr>
   * </table>
   * 
   * @param args Input parameters.
   */
  public static void main(String[] args) {
    PCF_CommonMethods pcfCM = new PCF_CommonMethods();

    try {
      if (pcfCM.ParseParameters(args)) {
        pcfCM.CreateAgent(args.length);

        ClearQueue(pcfCM);

        pcfCM.DestroyAgent();
      }
    }
    catch (Exception e) {
      pcfCM.DisplayException(e);
    }
    return;
  }

  /**
   * ClearQueue assumes that a queue has been created (by previously calling the CreateQueue
   * sample). The queue is then filled with a random number of random length messages. The PCF
   * command 'MQCMD_INQUIRE_Q' is then called to find how many messages are in the queue. Then the
   * PCF command 'MQCMD_CLEAR_Q' is used to clear all messages from the Queue. Finally a second
   * 'MQCMD_INQUIRE_Q' command is used to show that the queue has been cleared. <br>
   * For more information on the Create Queue command, please read the "Programmable Command Formats
   * and Administration Interface" section within the Websphere MQ documentation.
   * 
   * @param pcfCM Object used to hold common objects used by the PCF samples.
   * @throws MQException
   * @throws IOException
   * @throws MQDataException
   * @throws PCFException
   */
  public static void ClearQueue(PCF_CommonMethods pcfCM) throws MQException, PCFException,
      MQDataException, IOException {
    // Connect to the Queue Manager (handling either client or local bindings).
    MQQueueManager mqQueueManager;

    if (pcfCM.client) {
      mqQueueManager = new MQQueueManager(pcfCM.queueManager);
    }
    else {
      Hashtable parms = new Hashtable(8);

      parms.put(MQConstants.PORT_PROPERTY, new Integer(pcfCM.port));
      parms.put(MQConstants.CCSID_PROPERTY, new Integer(1208));
      parms.put(MQConstants.CHANNEL_PROPERTY, pcfCM.channel);
      parms.put(MQConstants.TRANSPORT_PROPERTY, MQConstants.TRANSPORT_MQSERIES_CLIENT);
      parms.put(MQConstants.HOST_NAME_PROPERTY, pcfCM.host);
      parms.put(MQConstants.CONNECT_OPTIONS_PROPERTY, new Integer(
          MQConstants.MQCNO_STANDARD_BINDING));

      mqQueueManager = new MQQueueManager(pcfCM.queueManager, parms);
    }

    // Connect to the PCF Queue on the QueueManager
    MQQueue mqQueue = mqQueueManager.accessQueue(PCF_CommonMethods.pcfQueue,
        MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_OUTPUT);

    // Put several messages (between 2 and 9) of different lengths (between 10 and 99
    // characters) onto the Queue.
    MQMessage mqTxMsg = new MQMessage();
    MQPutMessageOptions mqPMO = new MQPutMessageOptions();
    String msg = "Hello!";

    mqTxMsg.format = MQConstants.MQFMT_STRING;

    for (int msgCount = 0; msgCount < 2 + (int) (Math.random() * 8.0); msgCount++) {
      String txMsg = "";
      int msgLength = 10 + (int) (Math.random() * 90.0);

      if (msgLength > msg.length()) {
        for (int i = 0; i < msgLength / msg.length(); i++) {
          txMsg += msg;
        }

        txMsg += msg.substring(0, msgLength % msg.length());
      }
      else {
        txMsg = msg.substring(msgLength);
      }

      mqTxMsg.writeBytes(txMsg);

      mqQueue.put(mqTxMsg, mqPMO);
    }

    // Disconnect from the Queue and Queue Manager.
    mqQueue.close();
    mqQueueManager.close();
    mqQueueManager.disconnect();

    // Now have several message on a Queue. Display Queue information.
    PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q);

    // Add the inquire rules.
    pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, PCF_CommonMethods.pcfQueue);

    // Execute the command. The returned object is an array of PCF messages. There will
    // only be one set of queue status information, but this shows how it could be extended.
    PCFMessage[] pcfResponse = pcfCM.agent.send(pcfCmd);

    System.out.println("+-----+------------------------------------------------+-----+");
    System.out.println("|Index|             Queue Name (before)                |Depth|");
    System.out.println("+-----+------------------------------------------------+-----+");

    for (int index = 0; index < pcfResponse.length; index++) {
      PCFMessage response = pcfResponse[index];

      System.out.println("|"
          + (index + pcfCM.padding).substring(0, 5)
          + "|"
          + (response.getParameterValue(MQConstants.MQCA_Q_NAME) + pcfCM.padding).substring(0, 48)
          + "|"
          + (response.getParameterValue(MQConstants.MQIA_CURRENT_Q_DEPTH) + pcfCM.padding)
              .substring(0, 5) + "|");
    }

    System.out.println("+-----+------------------------------------------------+-----+");

    // The sample has so far connected to the Queue and filled it with a random number of
    // random length messages. The sample has demonstrated that these messages are on the
    // Queue. Now clear the queue and then demonstrate that the queue is empty.
    // Clear the messages. Note that rather than create another PCFMessage, the existing
    // object can be reinitialised with another command.
    pcfCmd.initialize(MQConstants.MQCMD_CLEAR_Q);

    // Add the inquire rules.
    pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, PCF_CommonMethods.pcfQueue);

    // Execute the command. The returned object is an array of PCF messages.
    pcfResponse = pcfCM.agent.send(pcfCmd);

    // Display Queue information.
    pcfCmd.initialize(MQConstants.MQCMD_INQUIRE_Q);

    // Add the inquire rules.
    pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, PCF_CommonMethods.pcfQueue);

    // Execute the command. The returned object is an array of PCF messages. There will
    // only be one set of queue status information, but this shows how it could be extended.
    pcfResponse = pcfCM.agent.send(pcfCmd);

    System.out.println("+-----+------------------------------------------------+-----+");
    System.out.println("|Index|             Queue Name (after)                 |Depth|");
    System.out.println("+-----+------------------------------------------------+-----+");

    for (int index = 0; index < pcfResponse.length; index++) {
      PCFMessage response = pcfResponse[index];

      System.out.println("|"
          + (index + pcfCM.padding).substring(0, 5)
          + "|"
          + (response.getParameterValue(MQConstants.MQCA_Q_NAME) + pcfCM.padding).substring(0, 48)
          + "|"
          + (response.getParameterValue(MQConstants.MQIA_CURRENT_Q_DEPTH) + pcfCM.padding)
              .substring(0, 5) + "|");
    }

    System.out.println("+-----+------------------------------------------------+-----+");
    return;
  }
}
