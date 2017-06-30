package samples;
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="3244251170" > 
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

import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFException;
import com.ibm.mq.headers.pcf.PCFMessage;

/**
 * <u>How to use this sample</u><br>
 * The sample demonstrates how PCF commands can be used to create a queue.
 * <p>
 * The queue will not be a transmission queue (required by the create channel sample), so care must
 * be taken when combining the create channel and create queue samples as PCF_CreateChannel will
 * create a transmit type channel and PCF_CreateQueue will not, but the queue will have the same
 * name (as given by PCF_CommonMethods.pcfQueue i.e. "PCFQUEUE").<br>
 * <br>
 * The sample can bind to a local queue manager using local bindings by using the following
 * parameters:-<br>
 * PCF_CreateQueue QueueManager<br>
 * <br>
 * e.g. PCF_CreateQueue QM1<br>
 * <br>
 * Or the sample can bind to a remote queue manager using client bindings by using the following
 * parameters:-<br>
 * PCF_CreateQueue QueueManager Host Port<br>
 * <br>
 * e.g. PCF_CreateQueue QM1 localhost 1414<br>
 * <br>
 * <b> N.B. When binding to another machine it is assumed that any intervening firewall has been
 * prepared to allow this connection.</b><br>
 */

public class PCF_CreateQueue {

  // @COPYRIGHT_START@
  /** Comment for copyright_notice */
  static final String copyright_notice = "Licensed Materials - Property of IBM "
      + "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
      + "(c) Copyright IBM Corp. 2008, 2009 All Rights Reserved. "
      + "US Government Users Restricted Rights - Use, duplication or "
      + "disclosure restricted by GSA ADP Schedule Contract with " + "IBM Corp.";
  // @COPYRIGHT_END@

  /** The SCCSID which is expanded when the file is extracted from CMVC */
  public static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/pcf/PCF_CreateQueue.java"; //$NON-NLS-1$

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

        CreateQueue(pcfCM, false);

        pcfCM.DestroyAgent();
      }
    }
    catch (Exception e) {
      pcfCM.DisplayException(e);
    }
    return;
  }

  /**
   * CreateQueue uses the PCF command 'MQCMD_CREATE_Q' to create a new Queue on the Queue Manager.
   * When creating a Queue, the mandatory parameters must appear first. Failure to define these
   * parameters first will result in a 3015 (MQRCCF_PARM_SEQUENCE_ERROR) error. The sample catches
   * the "Queue already exists" exception only to show how this can be done.<br>
   * For more information on the Create Queue command, please read the "Programmable Command Formats
   * and Administration Interface" section within the Websphere MQ documentation.
   * 
   * @param pcfCM Object used to hold common objects used by the PCF samples.
   * @param transmitQueue Boolean used to determine if the created queue should be a transmit queue.
   * @throws IOException
   * @throws MQDataException
   */
  public static void CreateQueue(PCF_CommonMethods pcfCM, boolean transmitQueue)
      throws MQDataException, IOException {
    int queueType = MQConstants.MQQT_LOCAL;

    // Create the PCF message type for the create queue.
    // NB: The parameters must be added in a specific order or an exception (3015)
    // will be thrown.
    PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_CREATE_Q);

    // Queue name - Mandatory.
    pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, PCF_CommonMethods.pcfQueue);

    // Queue Type - Optional.
    pcfCmd.addParameter(MQConstants.MQIA_Q_TYPE, queueType);

    if (transmitQueue) {
      // Queue Type - This must be the second parameter!
      pcfCmd.addParameter(MQConstants.MQIA_USAGE, MQConstants.MQUS_TRANSMISSION);
    }

    // Add description.
    pcfCmd.addParameter(MQConstants.MQCA_Q_DESC, "Queue created by PCF samples");

    try {
      // Execute the command. The returned object is an array of PCF messages.
      // If the Queue already exists, then catch the exception, otherwise rethrow.
      /* PCFMessage[] pcfResponse = */// We ignore the returned result
      pcfCM.agent.send(pcfCmd);
    }
    catch (PCFException pcfe) {
      if (pcfe.reasonCode == MQConstants.MQRCCF_OBJECT_ALREADY_EXISTS) {
        System.out.println("The queue \"" + PCF_CommonMethods.pcfQueue
            + "\" already exists on the queue manager.");
      }
      else {
        throw pcfe;
      }
    }
    return;
  }
}
