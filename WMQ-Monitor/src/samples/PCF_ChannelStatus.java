package samples;

/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="2105200464" > 
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
 * The sample demonstrates how PCF commands can be used to collect channel
 * status.
 * <p>
 * The sample can bind to a local queue manager using local bindings by using
 * the following parameters:-<br>
 * PCF_ChannelStatus QueueManager<br>
 * <br>
 * e.g. PCF_ChannelStatus QM1<br>
 * <br>
 * Or the sample can bind to a remote queue manager using client bindings by
 * using the following parameters:-<br>
 * PCF_ChannelStatus QueueManager Host Port<br>
 * <br>
 * e.g. PCF_ChannelStatus QM1 localhost 1414<br>
 * <br>
 * <b> N.B. When binding to another machine it is assumed that any intervening
 * firewall has been prepared to allow this connection.</b><br>
 * <br>
 * A typical output when running this sample when a valid channel is present
 * would be:-<br>
 * <br>
 * Channel status is MQCHS_RETRYING
 */

public class PCF_ChannelStatus {

	// @COPYRIGHT_START@
	/** Comment for copyright_notice */
	static final String copyright_notice = "Licensed Materials - Property of IBM "
			+ "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
			+ "(c) Copyright IBM Corp. 2008, 2009 All Rights Reserved. "
			+ "US Government Users Restricted Rights - Use, duplication or "
			+ "disclosure restricted by GSA ADP Schedule Contract with "
			+ "IBM Corp.";
	// @COPYRIGHT_END@

	/** The SCCSID which is expanded when the file is extracted from CMVC */
	public static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/pcf/PCF_ChannelStatus.java"; //$NON-NLS-1$

	/**
	 * PCF sample entry function. When calling this sample, use either of the
	 * following formats:-
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
	 * <td>Use this prototype when connecting to a queue manager using client
	 * bindings.</td>
	 * </tr>
	 * </table>
	 * 
	 * @param args
	 *            Input parameters.
	 */
	public static void main(String[] args) {
		PCF_CommonMethods pcfCM = new PCF_CommonMethods();

		try {
			if (pcfCM.ParseParameters(args)) {
				pcfCM.CreateAgent(args.length);

				ChannelStatus(pcfCM);

				pcfCM.DestroyAgent();
			}
		} catch (Exception e) {
			pcfCM.DisplayException(e);
		}
		return;
	}

	/**
	 * ChannelStatus uses the PCF command 'MQCMD_INQUIRE_CHANNEL_STATUS' to read
	 * a channel's status on the Queue Manager. Mandatory PCF parameters must
	 * appear first. Failure to define these parameters first will result in a
	 * 3015 (MQRCCF_PARM_SEQUENCE_ERROR) error. <br>
	 * For more information on the Inquire Channel Status command, please read
	 * the "Programmable Command Formats and Administration Interface" section
	 * within the Websphere MQ documentation.
	 * 
	 * @param pcfCM
	 *            Object used to hold common objects used by the PCF samples.
	 * @throws IOException
	 * @throws MQDataException
	 */
	public static void ChannelStatus(PCF_CommonMethods pcfCM)
			throws MQDataException, IOException {
		// Create the PCF message type for the inquire channel.
		PCFMessage pcfCmd = new PCFMessage(
				MQConstants.MQCMD_INQUIRE_CHANNEL_STATUS);

		// Add the start channel mandatory parameters.
		// Channel name.
		pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, "*");

		String[] chStatusText = { "", "MQCHS_BINDING", "MQCHS_STARTING",
				"MQCHS_RUNNING", "MQCHS_STOPPING", "MQCHS_RETRYING",
				"MQCHS_STOPPED", "MQCHS_REQUESTING", "MQCHS_PAUSED", "", "",
				"", "", "MQCHS_INITIALIZING" };

		// Execute the command. The returned object is an array of PCF messages.
		// If no object
		// can be returned, then catch the exception as this may not be an
		// error.
		try {
			PCFMessage[] pcfResponse = pcfCM.agent.send(pcfCmd);
			System.out.println("Number of Channels: " + pcfResponse.length);
			for (int index = 0; index < pcfResponse.length; index++) {
				PCFMessage response = pcfResponse[index];
				String names = (String) response
						.getParameterValue(MQConstants.MQCACH_CHANNEL_NAME);
				System.out.println(names);
				int chStatus = ((Integer) (pcfResponse[0]
						.getParameterValue(MQConstants.MQIACH_CHANNEL_STATUS)))
						.intValue();
				System.out.println("Channel status is "
						+ chStatusText[chStatus]);
			}

		} catch (PCFException pcfe) {
			// If the channel type is MQCHT_RECEIVER, MQCHT_SVRCONN or
			// MQCHT_CLUSRCVR, then the
			// only action is to enable the channel, not start it.
			if (pcfe.reasonCode != MQConstants.MQRCCF_CHL_STATUS_NOT_FOUND) {
				throw pcfe;
			}

			if (pcfCM.client) {
				System.out.println("Either the queue manager \""
						+ pcfCM.queueManager
						+ "\" does not exist or is not listening on port \""
						+ pcfCM.port + "\" or the channel \""
						+ PCF_CommonMethods.pcfChannel
						+ "\" does not exist on the queue manager.");
			} else {
				System.out.println("Either the queue manager \""
						+ pcfCM.queueManager
						+ "\" does not exist or the channel \""
						+ PCF_CommonMethods.pcfChannel
						+ "\" does not exist on the queue manager.");
			}
		}
		return;
	}
}
