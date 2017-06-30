package samples;
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="2014960302" > 
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
 * The sample demonstrates how PCF commands can be used to start a channel.
 * <p>
 * The sample can bind to a local queue manager using local bindings by using the following
 * parameters:-<br>
 * PCF_StartChannel QueueManager<br>
 * <br>
 * e.g. PCF_StartChannel QM1<br>
 * <br>
 * Or the sample can bind to a remote queue manager using client bindings by using the following
 * parameters:-<br>
 * PCF_StartChannel QueueManager Host Port<br>
 * <br>
 * e.g. PCF_StartChannel QM1 localhost 1414<br>
 * <br>
 * <b> N.B. When binding to another machine it is assumed that any intervening firewall has been
 * prepared to allow this connection.</b><br>
 */

public class PCF_StartChannel {

  // @COPYRIGHT_START@
  /** Comment for copyright_notice */
  static final String copyright_notice = "Licensed Materials - Property of IBM "
      + "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
      + "(c) Copyright IBM Corp. 2008, 2009 All Rights Reserved. "
      + "US Government Users Restricted Rights - Use, duplication or "
      + "disclosure restricted by GSA ADP Schedule Contract with " + "IBM Corp.";
  // @COPYRIGHT_END@

  /** The SCCSID which is expanded when the file is extracted from CMVC */
  public static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/pcf/PCF_StartChannel.java"; //$NON-NLS-1$

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

        StartChannel(pcfCM);

        pcfCM.DestroyAgent();
      }
    }
    catch (Exception e) {
      pcfCM.DisplayException(e);
    }
    return;
  }

  /**
   * StartChannel uses the PCF command 'MQCMD_START_CHANNEL' to start a channel on the Queue
   * Manager. Mandatory PCF parameters must appear first. Failure to define these parameters first
   * will result in a 3015 (MQRCCF_PARM_SEQUENCE_ERROR) error.<br>
   * For more information on the Start Channel command, please read the "Programmable Command
   * Formats and Administration Interface" section within the Websphere MQ documentation.
   * 
   * @param pcfCM Object used to hold common objects used by the PCF samples.
   * @throws IOException
   * @throws MQDataException
   * @throws PCFException
   */
  public static void StartChannel(PCF_CommonMethods pcfCM) throws PCFException, MQDataException,
      IOException {
    // Create the PCF message type for the start channel.
    PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_START_CHANNEL);

    // Add the start channel mandatory parameters.
    // Channel name.
    pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, PCF_CommonMethods.pcfChannel);

    // Execute the command. The returned object is an array of PCF messages.
    /* PCFMessage[] pcfResponse = */// We ignore the returned result
    pcfCM.agent.send(pcfCmd);

    // Check the channel status.
    PCF_ChannelStatus.ChannelStatus(pcfCM);
    return;
  }
}
