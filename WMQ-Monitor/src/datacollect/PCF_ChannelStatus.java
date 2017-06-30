package datacollect;

import java.io.IOException;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFMessageAgent;

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

	private static final String[] chStatusText = { "", "MQCHS_BINDING", "MQCHS_STARTING", "MQCHS_RUNNING",
			"MQCHS_STOPPING", "MQCHS_RETRYING", "MQCHS_STOPPED", "MQCHS_REQUESTING", "MQCHS_PAUSED", "", "", "", "",
			"MQCHS_INITIALIZING" };

	private PCFMessageAgent pcfMessageAgent = null;
	private PCFMessage request = null;

	public PCF_ChannelStatus(String host, int port, String queueName) {
		try {
			pcfMessageAgent = new PCFMessageAgent(host, port, queueName);
		} catch (MQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request = new PCFMessage(MQConstants.MQCMD_INQUIRE_CHANNEL_STATUS);
		request.addParameter(MQConstants.MQCACH_CHANNEL_NAME, "*");
	}

	public int runningChannelCount() {
		int count = 0;
		PCFMessage[] pcfResponse;
		try {
			pcfResponse = pcfMessageAgent.send(request);
			System.out.println("Number of Channels: " + pcfResponse.length);
			for (int index = 0; index < pcfResponse.length; index++) {
				com.ibm.mq.pcf.PCFMessage response = pcfResponse[index];
				String names = (String) response.getParameterValue(MQConstants.MQCACH_CHANNEL_NAME);
				System.out.println(names);
				int chStatus = ((Integer) (pcfResponse[0].getParameterValue(MQConstants.MQIACH_CHANNEL_STATUS)))
						.intValue();
				System.out.println("Channel status is " + chStatusText[chStatus]);
				if (3==chStatus) count++;
			}
		} catch (MQException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return count;
	}
	
	public void release() {

		if (null != pcfMessageAgent)
			try {
				pcfMessageAgent.disconnect();
			} catch (MQException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public static void main(String[] args) {
		PCF_ChannelStatus chsstat = new PCF_ChannelStatus("22.188.59.35", 1411, "WCB.CONN");
		System.out.println("Current number of running channel is: " + chsstat.runningChannelCount());
		
	}
	
}
