package datacollect;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFMessageAgent;

public class GetCurrDepByPcf {

	private PCFMessageAgent pcfMessageAgent = null;
	private PCFMessage request = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q);

	public GetCurrDepByPcf(String host, int port, String svrConnChl) {
						
		try {
			// pcfMessageAgent = new PCFMessageAgent("22.188.59.35", 1411, "WCB.CONN");
			pcfMessageAgent = new PCFMessageAgent(host, port, svrConnChl);
		} catch (MQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		request.addParameter(CMQC.MQCA_Q_NAME, "WCB.QUEUE");
		request.addParameter(CMQC.MQIA_Q_TYPE, CMQC.MQQT_LOCAL);
		request.addParameter(MQConstants.MQIACF_Q_ATTRS, new int[] { CMQC.MQCA_Q_NAME, CMQC.MQIA_CURRENT_Q_DEPTH });

	}

	public int CurrDepth() {
		PCFMessage[] pcfResponse;
		try {
			pcfResponse = pcfMessageAgent.send(request);
			return pcfResponse[0].getIntParameterValue(CMQC.MQIA_CURRENT_Q_DEPTH);
		} catch (MQException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}

	}
	
	public void record(String fileName, int count){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
		try {
			Writer out = new FileWriter(new File(fileName), true);
			for (int i=0;i<count; i++){				
				out.write(dateFormat.format(new Date())+", " + CurrDepth()+"\r\n");
				System.out.println(dateFormat.format(new Date())+", " + CurrDepth()+"\r\n");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
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

		GetCurrDepByPcf gcd = new GetCurrDepByPcf("22.188.59.35", 1411, "WCB.CONN");

		gcd.record("E:\\record.txt", 10000);
		gcd.release();

	}

}
