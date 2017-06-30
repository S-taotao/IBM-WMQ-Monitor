import java.io.IOException;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.pcf.PCFException;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFMessageAgent;

public class GetChannels {

	/**
	 * @param args
	 * @throws Exceptions
	 */
	public static void main(String[] args) throws PCFException {

		PCFMessageAgent pcfMessageAgent = null;
		try {
			pcfMessageAgent = new PCFMessageAgent("22.188.59.35", 1411,
					"WCB.CONN");
		} catch (MQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PCFMessage request = new PCFMessage(MQConstants.MQCMD_INQUIRE_CHANNEL);
		request.addParameter(MQConstants.MQCACH_CHANNEL_NAME, "*");
		request.addParameter(MQConstants.MQIACH_CHANNEL_TYPE,
				MQConstants.MQCHT_ALL);
		try {
			PCFMessage[] pcfResponse = pcfMessageAgent.send(request);
			
			/*
			 * for (int index = 0; index < pcfResponse.length; index++) {
			 * PCFMessage response = pcfResponse[index]; String names = (String)
			 * response.getParameterValue(MQConstants.MQCACH_CHANNEL_NAME);
			 * System.out.println(names); }
			 */

			System.out
					.println("+-----+------------------------------------------------+----------+------------------------------------------------+");
			System.out
					.println("|Index|                  Channel Name                  |   Type   |                      Exit Information          |");
			System.out
					.println("+-----+------------------------------------------------+----------+------------------------------------------------+");

			// For each channel, the name, type and exit information is
			// extracted. For a full list
			// of what fields are available, please read the MQ documentation.
			for (int index = 0; index < pcfResponse.length; index++) {
				PCFMessage response = pcfResponse[index];
				String name = (String) response
						.getParameterValue(MQConstants.MQCACH_CHANNEL_NAME);
				int type = ((Integer) response
						.getParameterValue(MQConstants.MQIACH_CHANNEL_TYPE))
						.intValue();
				String msgExit = (String) response
						.getParameterValue(MQConstants.MQCACH_MSG_EXIT_NAME);
				String rcvExit = (String) response
						.getParameterValue(MQConstants.MQCACH_RCV_EXIT_NAME);
				String sndExit = (String) response
						.getParameterValue(MQConstants.MQCACH_SEND_EXIT_NAME);
				String scyExit = (String) response
						.getParameterValue(MQConstants.MQCACH_SEC_EXIT_NAME);
				String[] channelTypes = { "", "SDR", "SVR", "RCVR", "RQSTR",
						"", "CLTCN", "SVRCN", "CLUSRCVR", "CLUSSDR", "" };
				String exitInfo = "";

				if ((msgExit != null) && (msgExit.trim().length() > 0)) {
					exitInfo = msgExit + "|";
				}

				if ((rcvExit != null) && (rcvExit.trim().length() > 0)) {
					if (exitInfo.length() > 0) {
						exitInfo += "\r\n|" + "|" + "|" + "|";
					}

					exitInfo += rcvExit + "|";
				}

				if ((sndExit != null) && (sndExit.trim().length() > 0)) {
					if (exitInfo.length() > 0) {
						exitInfo += "\r\n|" + "|" + "|" + "|";
					}

					exitInfo += sndExit + "|";
				}

				if ((scyExit != null) && (scyExit.trim().length() > 0)) {
					if (exitInfo.length() > 0) {
						exitInfo += "\r\n|" + "|" + "|" + "|";
					}

					exitInfo += scyExit + "|";
				}

				if (exitInfo.length() == 0) {
					exitInfo = "|";
				}

				System.out.println("|"
						+ index + "|"
						+ name  + "|"
						+ channelTypes[type]
						+ "|" + exitInfo);
			}

			System.out
					.println("+-----+------------------------------------------------+----------+------------------------------------------------+");
		} catch (MQException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
