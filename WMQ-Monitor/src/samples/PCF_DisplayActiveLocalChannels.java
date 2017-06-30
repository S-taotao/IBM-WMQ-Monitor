package samples;
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="1069674258" > 
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
 * The sample demonstrates how PCF commands can be used to display active local channels.
 * <p>
 * The sample can bind to a local queue manager using local bindings by using the following
 * parameters:-<br>
 * PCF_DisplayActiveLocalChannels QueueManager<br>
 * <br>
 * e.g. PCF_DisplayActiveLocalChannels QM1<br>
 * <br>
 * Or the sample can bind to a remote queue manager using client bindings by using the following
 * parameters:-<br>
 * PCF_DisplayActiveLocalChannels QueueManager Host Port<br>
 * <br>
 * e.g. PCF_DisplayActiveLocalChannels QM1 localhost 1414<br>
 * <br>
 * <b> N.B. When binding to another machine it is assumed that any intervening firewall has been
 * prepared to allow this connection.</b><br>
 * <br>
 * A typical output when running this sample would be:-<br>
 * <br>
 * <code><font face="courier, monospaced">
 * +-----+------------------------------------------------+----------+<br>
 * |Index|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;Channel Name&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;Type&nbsp;
 * &nbsp;|<br>
 * +-----+------------------------------------------------+----------+<br>
 * |0&nbsp;&nbsp;&nbsp;&nbsp;|PCFCHANL&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;|SDR&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |1&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.AUTO.RECEIVER&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|RCVR&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |2&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.AUTO.SVRCONN&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|SVRCN&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |3&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.DEF.CLUSRCVR&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|CLUSRCVR&nbsp;&nbsp;|<br>
 * |4&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.DEF.CLUSSDR&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|CLUSSDR&nbsp;&nbsp;&nbsp;|<br>
 * |5&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.DEF.RECEIVER&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|RCVR&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |6&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.DEF.REQUESTER&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|RQSTR&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * |7&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.DEF.SENDER&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|SDR&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;|<br>
 * |8&nbsp;&nbsp;&nbsp;&nbsp;|SYSTEM.DEF.SERVER&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|SVR&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;|<br>
 * |9 &nbsp;&nbsp;&nbsp;|SYSTEM.DEF.SVRCONN&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|SVRCN&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
 * +-----+------------------------------------------------+----------+<br></font></code>
 */

public class PCF_DisplayActiveLocalChannels {

  // @COPYRIGHT_START@
  /** Comment for copyright_notice */
  static final String copyright_notice = "Licensed Materials - Property of IBM "
      + "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
      + "(c) Copyright IBM Corp. 2008, 2009 All Rights Reserved. "
      + "US Government Users Restricted Rights - Use, duplication or "
      + "disclosure restricted by GSA ADP Schedule Contract with " + "IBM Corp.";
  // @COPYRIGHT_END@

  /** The SCCSID which is expanded when the file is extracted from CMVC */
  public static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/pcf/PCF_DisplayActiveLocalChannels.java"; //$NON-NLS-1$

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

        DisplayActiveLocalChannels(pcfCM);

        pcfCM.DestroyAgent();
      }
    }
    catch (Exception e) {
      pcfCM.DisplayException(e);
    }
    return;
  }

  /**
   * DisplayActiveLocalChannels uses the PCF command 'MQCMD_INQUIRE_CHANNEL_NAMES' to find all
   * Channels (using the '*' wildcard) of all types (using the 'MQCHT_ALL' type) on the Queue
   * Manager. Mandatory parameters must appear first. Failure to define these parameters first will
   * result in a 3015 (MQRCCF_PARM_SEQUENCE_ERROR) error.<br>
   * For more information on the Delete Queue command, please read the "Programmable Command Formats
   * and Administration Interface" section within the Websphere MQ documentation.
   * 
   * @param pcfCM Object used to hold common objects used by the PCF samples.
   * @throws IOException
   * @throws MQDataException
   * @throws PCFException
   */
  public static void DisplayActiveLocalChannels(PCF_CommonMethods pcfCM) throws PCFException,
      MQDataException, IOException {
    // Create the PCF message type for the channel names inquire.
    PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_CHANNEL_NAMES);

    // Add the inquire rules.
    // Queue name = wildcard.
    pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, "*");

    // Channel type = ALL.
    pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_TYPE, MQConstants.MQCHT_ALL);

    // Execute the command. The returned object is an array of PCF messages.
    PCFMessage[] pcfResponse = pcfCM.agent.send(pcfCmd);

    // For each returned message, extract the message from the array and display the
    // required information.
    System.out.println("+-----+------------------------------------------------+----------+");
    System.out.println("|Index|                  Channel Name                  |   Type   |");
    System.out.println("+-----+------------------------------------------------+----------+");

    // The Channel information is held in some array element of the response object (the
    // contents of the response object is defined in the documentation).
    for (int responseNumber = 0; responseNumber < pcfResponse.length; responseNumber++) {
      String[] names = (String[]) pcfResponse[responseNumber]
          .getParameterValue(MQConstants.MQCACH_CHANNEL_NAMES);

      // There might not be any names, so test this first before attempting to parse the object.
      if (names != null) {
        int[] types = (int[]) pcfResponse[responseNumber]
            .getParameterValue(MQConstants.MQIACH_CHANNEL_TYPES);
        String[] channelTypes = {"", "SDR", "SVR", "RCVR", "RQSTR", "", "CLTCN", "SVRCN",
            "CLUSRCVR", "CLUSSDR", ""};
        for (int index = 0; index < names.length; index++) {
          System.out.println("|" + (index + pcfCM.padding).substring(0, 5) + "|"
              + (names[index] + pcfCM.padding).substring(0, 48) + "|"
              + (channelTypes[types[index]] + pcfCM.padding).substring(0, 10) + "|");
        }

        System.out.println("+-----+------------------------------------------------+----------+");
        break;
      }
    }
    return;
  }
}
