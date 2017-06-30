package samples;
/*   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="421597677" > 
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
 * The sample demonstrates how PCF commands can be used to display active local queues.
 * <p>
 * The sample can bind to a local queue manager using local bindings by using the following
 * parameters:-<br>
 * PCF_DisplayActiveLocalQueues QueueManager<br>
 * <br>
 * e.g. PCF_DisplayActiveLocalQueues QM1<br>
 * <br>
 * Or the sample can bind to a remote queue manager using client bindings by using the following
 * parameters:-<br>
 * PCF_DisplayActiveLocalQueues QueueManager Host Port<br>
 * <br>
 * e.g. PCF_DisplayActiveLocalQueues QM1 localhost 1414<br>
 * <br>
 * <b> N.B. When binding to another machine it is assumed that any intervening firewall has been
 * prepared to allow this connection.</b><br>
 * <br>
 * A typical output when running this sample would be:-<br>
 * <br>
 * <code><font face="courier, monospaced">
 * +-----+------------------------------------------------+-----+<br>
 * |Index|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Queue Name&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Depth|<br>
 * +-----+------------------------------------------------+-----+<br>
 * |0&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.ADMIN.QMGR.EVENT&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;|1&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |1&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.AUTH.DATA.QUEUE&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;|79&nbsp;&nbsp;&nbsp;|<br>
 * |2&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.CLUSTER.REPOSITORY.QUEUE&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|1&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |3&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.DURABLE.SUBSCRIBER.QUEUE&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|2&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |4&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.HIERARCHY.STATE&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;|2&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |5&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.RETAINED.PUB.QUEUE&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|2
 * &nbsp;&nbsp;&nbsp;|<br>
 * +-----+------------------------------------------------+-----+<br></font></code>
 */

public class PCF_DisplayActiveLocalQueues {

  // @COPYRIGHT_START@
  /** Comment for copyright_notice */
  static final String copyright_notice = "Licensed Materials - Property of IBM "
      + "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
      + "(c) Copyright IBM Corp. 2008, 2009 All Rights Reserved. "
      + "US Government Users Restricted Rights - Use, duplication or "
      + "disclosure restricted by GSA ADP Schedule Contract with " + "IBM Corp.";
  // @COPYRIGHT_END@

  /** The SCCSID which is expanded when the file is extracted from CMVC */
  public static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/pcf/PCF_DisplayActiveLocalQueues.java"; //$NON-NLS-1$

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

        DisplayActiveLocalQueues(pcfCM);

        pcfCM.DestroyAgent();
      }
    }
    catch (Exception e) {
      pcfCM.DisplayException(e);
    }
    return;
  }

  /**
   * DisplayActiveLocalQueues uses the PCF command 'MQCMD_INQUIRE_Q' to gather information about all
   * local Queues (using the '*' wildcard to denote 'all queues') contained within the given Queue
   * Manager. The search is refined using a filter property to return just those queues that contain
   * one or more messages. The information is displayed in a tabular form on the console.<br>
   * For more information on the Inquire Queue command, please read the "Programmable Command
   * Formats and Administration Interface" section within the Websphere MQ documentation.
   * 
   * @param pcfCM Object used to hold common objects used by the PCF samples.
   * @throws IOException
   * @throws MQDataException
   * @throws PCFException
   */
  public static void DisplayActiveLocalQueues(PCF_CommonMethods pcfCM) throws PCFException,
      MQDataException, IOException {
    // Create the PCF message type for the inquire.
    PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q);

    // Add the inquire rules.
    // Queue name = wildcard.
    pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, "*");

    // Queue type = LOCAL.
    pcfCmd.addParameter(MQConstants.MQIA_Q_TYPE, MQConstants.MQQT_LOCAL);

    // Queue depth filter = "WHERE depth > 0".
    pcfCmd.addFilterParameter(MQConstants.MQIA_CURRENT_Q_DEPTH, MQConstants.MQCFOP_GREATER, 0);

    // Execute the command. The returned object is an array of PCF messages.
    PCFMessage[] pcfResponse = pcfCM.agent.send(pcfCmd);

    // For each returned message, extract the message from the array and display the
    // required information.
    System.out.println("+-----+------------------------------------------------+-----+");
    System.out.println("|Index|                    Queue Name                  |Depth|");
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
