import java.io.IOException;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.pcf.CMQCFC;
import com.ibm.mq.pcf.PCFException;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFMessageAgent;


public class GetCurrDepByPcf {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MQException 
	 * @throws PCFException 
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws PCFException, MQException, IOException {
		PCFMessageAgent pcfMessageAgent = null;
		try {
			pcfMessageAgent = new PCFMessageAgent("22.188.59.35", 1411,
					"WCB.CONN");
		} catch (MQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//PCFMessage request = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q_NAMES);
		PCFMessage request = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q);
		//PCFMessage request = new PCFMessage (CMQCFC.MQCMD_INQUIRE_Q);
		
	    // Add the inquire rules.
	    // Queue name = wildcard.
		//request = new PCFMessage (CMQCFC.MQCMD_INQUIRE_Q);
		request = new PCFMessage (MQConstants.MQCMD_INQUIRE_Q);
		request.addParameter (CMQC.MQCA_Q_NAME, "*");
		//request.addParameter (CMQC.MQCA_Q_NAME, "WCB.QUEUE");
		//request.addParameter (CMQC.MQCA_Q_NAME, new String[]{"QL1","WCB.QUEUE"});
		request.addParameter (CMQC.MQIA_Q_TYPE, CMQC.MQQT_LOCAL);
		request.addParameter (MQConstants.MQIACF_Q_ATTRS, new int [] { CMQC.MQCA_Q_NAME, CMQC.MQIA_CURRENT_Q_DEPTH });


	    // Execute the command. The returned object is an array of PCF messages.
	    //PCFMessage[] pcfResponse = pcfMessageAgent.send(request);
	    PCFMessage[] pcfResponse = pcfMessageAgent.send(request);

	    for (int i = 0; i < pcfResponse.length; i++)
	    {
	      String name = pcfResponse [i].getStringParameterValue (CMQC.MQCA_Q_NAME);
	      int depth = pcfResponse [i].getIntParameterValue (CMQC.MQIA_CURRENT_Q_DEPTH);
	      
	      System.out.println("The depth of " + name +" is: " + depth);
	    }
	    // Disconnect
	    pcfMessageAgent.disconnect ();
	    
	}

}



