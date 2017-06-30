package samples;
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="3532621157" > 
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
 * The sample demonstrates how PCF commands can be used to display channel information.
 * <p>
 * The sample can bind to a local queue manager using local bindings by using the following
 * parameters:-<br>
 * PCF_DisplayConnections QueueManager<br>
 * <br>
 * e.g. PCF_DisplayConnections QM1<br>
 * <br>
 * Or the sample can bind to a remote queue manager using client bindings by using the following
 * parameters:-<br>
 * PCF_DisplayConnections QueueManager Host Port<br>
 * <br>
 * e.g. PCF_DisplayConnections QM1 localhost 1414<br>
 * <br>
 * <b> N.B. When binding to another machine it is assumed that any intervening firewall has been
 * prepared to allow this connection.</b><br>
 * <br>
 * A typical output when running this sample would be:-<br>
 * <br>
 * <code><font face="courier, monospaced">
 * +-----+------------------------------------------------+----------+------------------------------------------------+<br>
 * |Index|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * Channel Name&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * |&nbsp;&nbsp;&nbsp;Type&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Exit Information&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;|<br>
 * +-----+------------------------------------------------+----------+------------------------------------------------+<br>
 * |0&nbsp;&nbsp;&nbsp;&nbsp;|PCFCHANL&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;|SDR&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |1&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.AUTO.RECEIVER&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|RCVR&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |2&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.AUTO.SVRCONN&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|SVRCN&nbsp;&nbsp;&nbsp;
 * &nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |3&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.DEF.CLUSRCVR&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|CLUSRCVR&nbsp;&nbsp;|
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |4&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.DEF.CLUSSDR&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|CLUSSDR&nbsp;
 * &nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |5&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.DEF.RECEIVER&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|RCVR&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |6&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.DEF.REQUESTER&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|RQSTR&nbsp;&nbsp;
 * &nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |7&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.DEF.SENDER&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * |SDR&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |8&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.DEF.SERVER&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|SVR
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |9&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.DEF.SVRCONN&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|SVRCN
 * &nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |10&nbsp;&nbsp;&nbsp;|Channel&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|CLTCN&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|com.ibm.mq.ExternalV6ReceiveExit&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|com.ibm.mq.ExternalV6SendExit&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|com.ibm.mq.ExternalV6SecurityExit&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |11&nbsp;&nbsp;&nbsp;|SYSTEM.DEF.CLNTCONN&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|CLTCN
 * &nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * +-----+------------------------------------------------+----------+------------------------------------------------+<br></font></code>
 */

public class PCF_DisplayConnections {

  // @COPYRIGHT_START@
  /** Comment for copyright_notice */
  static final String copyright_notice = "Licensed Materials - Property of IBM "
      + "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
      + "(c) Copyright IBM Corp. 2008, 2009 All Rights Reserved. "
      + "US Government Users Restricted Rights - Use, duplication or "
      + "disclosure restricted by GSA ADP Schedule Contract with " + "IBM Corp.";
  // @COPYRIGHT_END@

  /** The SCCSID which is expanded when the file is extracted from CMVC */
  public static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/pcf/PCF_DisplayConnections.java"; //$NON-NLS-1$

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

        DisplayConnections(pcfCM);

        pcfCM.DestroyAgent();
      }
    }
    catch (Exception e) {
      pcfCM.DisplayException(e);
    }
    return;
  }

  /**
   * DisplayConnections uses the PCF command 'MQCMD_INQUIRE_CHANNEL' to find all Channels (using the
   * '*' wildcard) of all types (using the 'MQCHT_ALL' type) on the Queue Manager. This is different
   * from 'MQCMD_INQUIRE_CHANNEL_NAMES' because it also displays the "Client Connection" channels.
   * In this sample, the output also includes any Exits that might have been defined on the channel.
   * Mandatory parameters must appear first. Failure to define these parameters first will result in
   * a 3015 (MQRCCF_PARM_SEQUENCE_ERROR) error.<br>
   * For more information on the Delete Queue command, please read the "Programmable Command Formats
   * and Administration Interface" section within the Websphere MQ documentation.
   * 
   * @param pcfCM Object used to hold common objects used by the PCF samples.
   * @throws IOException
   * @throws MQDataException
   * @throws PCFException
   */
  public static void DisplayConnections(PCF_CommonMethods pcfCM) throws PCFException,
      MQDataException, IOException {
    // Create the PCF message type for the inquire.
    PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_CHANNEL);

    // Add the inquire rules.
    // Queue name = wildcard.
    pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, "*");
    pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_TYPE, MQConstants.MQCHT_ALL);

    // Execute the command. The returned object is an array of PCF messages.
    PCFMessage[] pcfResponse = pcfCM.agent.send(pcfCmd);

    System.out
        .println("+-----+------------------------------------------------+----------+------------------------------------------------+");
    System.out
        .println("|Index|                  Channel Name                  |   Type   |                      Exit Information          |");
    System.out
        .println("+-----+------------------------------------------------+----------+------------------------------------------------+");

    // For each channel, the name, type and exit information is extracted. For a full list
    // of what fields are available, please read the MQ documentation.
    for (int index = 0; index < pcfResponse.length; index++) {
      PCFMessage response = pcfResponse[index];
      String name = (String) response.getParameterValue(MQConstants.MQCACH_CHANNEL_NAME);
      int type = ((Integer) response.getParameterValue(MQConstants.MQIACH_CHANNEL_TYPE)).intValue();
      String msgExit = (String) response.getParameterValue(MQConstants.MQCACH_MSG_EXIT_NAME);
      String rcvExit = (String) response.getParameterValue(MQConstants.MQCACH_RCV_EXIT_NAME);
      String sndExit = (String) response.getParameterValue(MQConstants.MQCACH_SEND_EXIT_NAME);
      String scyExit = (String) response.getParameterValue(MQConstants.MQCACH_SEC_EXIT_NAME);
      String[] channelTypes = {"", "SDR", "SVR", "RCVR", "RQSTR", "", "CLTCN", "SVRCN", "CLUSRCVR",
          "CLUSSDR", ""};
      String exitInfo = "";

      if ((msgExit != null) && (msgExit.trim().length() > 0)) {
        exitInfo = (msgExit + pcfCM.padding).substring(0, 48) + "|";
      }

      if ((rcvExit != null) && (rcvExit.trim().length() > 0)) {
        if (exitInfo.length() > 0) {
          exitInfo += "\r\n|" + pcfCM.padding.substring(0, 5) + "|"
              + pcfCM.padding.substring(0, 48) + "|" + pcfCM.padding.substring(0, 10) + "|";
        }

        exitInfo += (rcvExit + pcfCM.padding).substring(0, 48) + "|";
      }

      if ((sndExit != null) && (sndExit.trim().length() > 0)) {
        if (exitInfo.length() > 0) {
          exitInfo += "\r\n|" + pcfCM.padding.substring(0, 5) + "|"
              + pcfCM.padding.substring(0, 48) + "|" + pcfCM.padding.substring(0, 10) + "|";
        }

        exitInfo += (sndExit + pcfCM.padding).substring(0, 48) + "|";
      }

      if ((scyExit != null) && (scyExit.trim().length() > 0)) {
        if (exitInfo.length() > 0) {
          exitInfo += "\r\n|" + pcfCM.padding.substring(0, 5) + "|"
              + pcfCM.padding.substring(0, 48) + "|" + pcfCM.padding.substring(0, 10) + "|";
        }

        exitInfo += (scyExit + pcfCM.padding).substring(0, 48) + "|";
      }

      if (exitInfo.length() == 0) {
        exitInfo = pcfCM.padding.substring(0, 48) + "|";
      }

      System.out.println("|" + (index + pcfCM.padding).substring(0, 5) + "|"
          + (name + pcfCM.padding).substring(0, 48) + "|"
          + (channelTypes[type] + pcfCM.padding).substring(0, 10) + "|" + exitInfo);
    }

    System.out
        .println("+-----+------------------------------------------------+----------+------------------------------------------------+");
    return;
  }
}
