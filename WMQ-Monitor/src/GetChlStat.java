import java.io.IOException;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.pcf.PCFException;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFMessageAgent;

public class GetChlStat {
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
			
			
			for (int index = 0; index < pcfResponse.length; index++) {
			PCFMessage response = pcfResponse[index]; 
			String names = (String)	response.getParameterValue(MQConstants.MQCACH_CHANNEL_NAME);
			System.out.println(names); }
			
			for (int index = 0; index < pcfResponse.length; index++) {
				PCFMessage response = pcfResponse[index];
				String name = (String) response
						.getParameterValue(MQConstants.MQCACH_CHANNEL_NAME);
				int type = ((Integer) response
						.getParameterValue(MQConstants.MQIACH_CHANNEL_TYPE))
						.intValue();
				System.out.println(name);

				//System.out.println("|" + index + "|" + name  + "|");
			}

			System.out
					.println("+-----+------------------------------------------------+----------+------------------------------------------------+");
		} catch (MQException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



}
