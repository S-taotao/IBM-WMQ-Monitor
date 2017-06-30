package datacollect;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.mq.MQException;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFMessageAgent;

public class IntelligentAgent {
	
	private PCFMessageAgent pcfMessageAgent = null;
	private PCFMessage request = null;	
	private static final String[] chStatusText = { "", "MQCHS_BINDING", "MQCHS_STARTING", "MQCHS_RUNNING",
			"MQCHS_STOPPING", "MQCHS_RETRYING", "MQCHS_STOPPED", "MQCHS_REQUESTING", "MQCHS_PAUSED", "", "", "", "",
			"MQCHS_INITIALIZING" };
	
	
	public IntelligentAgent(String host, int port, String svrConnChl) {		
		try {			
			pcfMessageAgent = new PCFMessageAgent(host, port, svrConnChl);
		} catch (MQException e) {			
			e.printStackTrace();
		}
	}
	
	public int CurrDepth(String qName) {
		//May be optimized, "request object could be defined and initialized out of the function
		//However, defining and initializing the request object within function has some advantages:
		//1. Convenient to specify qName as a parameter of the function;
		//2. Avoid to assign a request object for each type of query
		request = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q);
		request.addParameter(CMQC.MQCA_Q_NAME, qName);
		request.addParameter(CMQC.MQIA_Q_TYPE, CMQC.MQQT_LOCAL);
		request.addParameter(MQConstants.MQIACF_Q_ATTRS, new int[] { CMQC.MQCA_Q_NAME, CMQC.MQIA_CURRENT_Q_DEPTH });
		PCFMessage[] pcfResponse;
		try {
			pcfResponse = pcfMessageAgent.send(request);
			return pcfResponse[0].getIntParameterValue(CMQC.MQIA_CURRENT_Q_DEPTH);
		} catch (MQException | IOException e) {			
			e.printStackTrace();
			return -1;
		}	

	}
	
	public void recordDepth(String fileName, int count, String qName) {
		File file = new File(fileName);
		recordDepth(file, count, qName);
	}
	
	public void recordDepth(File fileName, int count, String qName) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss"); //这里的MM一定要大写，不然显示出来的是分钟
		try {
			Writer out = new FileWriter(fileName, true);
			for (int i = 0; i < count; i++) {
				out.write(dateFormat.format(new Date()) + ", " + CurrDepth(qName) + "\r\n");
				System.out.println(dateFormat.format(new Date()) + ", " + CurrDepth(qName) + "\r\n");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {					
					System.out.println(this.toString()+" is stoped!");
					out.flush();
					out.close();
					release();
					return;
				}
				out.flush();
			}
			out.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	public Map<String, Integer> CurrDepths(List<String> qNames) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		request = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q);
		request.addParameter(CMQC.MQCA_Q_NAME, "*");
		request.addParameter(CMQC.MQIA_Q_TYPE, CMQC.MQQT_LOCAL);
		request.addParameter(MQConstants.MQIACF_Q_ATTRS, new int[] { CMQC.MQCA_Q_NAME, CMQC.MQIA_CURRENT_Q_DEPTH });
		PCFMessage[] pcfResponse;		
		try {
			pcfResponse = pcfMessageAgent.send(request);
			for (int i = 0; i < pcfResponse.length; i++) {
				//trim() is necessary, otherwise, the subsequent condition qNames.contains(name) will not be valid 
				String name = pcfResponse[i].getStringParameterValue(CMQC.MQCA_Q_NAME).trim();
				int depth = pcfResponse[i].getIntParameterValue(CMQC.MQIA_CURRENT_Q_DEPTH);				
				if (qNames.contains(name)) result.put(name, depth);				
				//System.out.println("The depth of " + name + " is: " + depth);
			}
			return result;
		} catch (MQException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void recordDepths(String fileName, int count, List<String> qNames) {
		File file = new File(fileName);
		recordDepths(file, count, qNames);
	}
	
	public void recordDepths(File fileName, int count, List<String> qNames) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		try {
			Writer out = new FileWriter(fileName, true);
			out.write("Time, ");
			for (String s : qNames) {
				out.write(s + ", ");
			}
			System.out.println("Start to record the Q Depths.....");
			out.write("\r\n");
			for (int i = 0; i < count; i++) {
				Map<String, Integer> result = CurrDepths(qNames);
				out.write(dateFormat.format(new Date()) + ", ");
				for (String s : qNames) {
					out.write(result.get(s) + ", ");
				}
				out.write("\r\n");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					System.out.println(this.toString()+" is stoped!");
					out.flush();
					out.close();
					release();
					return;
				}
				out.flush();     //flush() to refresh the log file more frequently
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int runningChannelCount() {
		request = new PCFMessage(MQConstants.MQCMD_INQUIRE_CHANNEL_STATUS);
		request.addParameter(MQConstants.MQCACH_CHANNEL_NAME, "*");
		int count = 0;
		PCFMessage[] pcfResponse;
		try {
			pcfResponse = pcfMessageAgent.send(request);
			System.out.println("Number of Channels: " + pcfResponse.length);
			for (int index = 0; index < pcfResponse.length; index++) {
				PCFMessage response = pcfResponse[index];
				String names = (String) response.getParameterValue(MQConstants.MQCACH_CHANNEL_NAME);
				System.out.println(names);
				int chStatus = ((Integer) (pcfResponse[0].getParameterValue(MQConstants.MQIACH_CHANNEL_STATUS)))
						.intValue();
				System.out.println("Channel status is " + chStatusText[chStatus]);
				if (3==chStatus) count++;
			}
		} catch (MQException | IOException e) {			
			e.printStackTrace();
		}
		return count;
	}
	
	public void recordChlCnt(String fileName, int count) {
		File file = new File(fileName);
		recordChlCnt(file, count);
	}
	
	public void recordChlCnt(File fileName, int count) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		try {
			Writer out = new FileWriter(fileName, true);
			for (int i = 0; i < count; i++) {
				out.write(dateFormat.format(new Date()) + ", " + runningChannelCount() + "\r\n");
				System.out.println(dateFormat.format(new Date()) + ", " + runningChannelCount() + "\r\n");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {					
					System.out.println(this.toString()+" is stoped!");
					out.flush();
					out.close();
					release();
					return;
				}
				out.flush();
			}
			out.close();
		} catch (IOException e) {			
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
		IntelligentAgent agent = new IntelligentAgent("22.188.59.35", 1411, "WCB.CONN");
		List<String> qnames= new ArrayList<String>();
		qnames.add("WCB.QUEUE");
		qnames.add("QL1");
		agent.recordDepths("E:\\test.txt", 100, qnames);
		agent.release();
		/*List<String> c = new ArrayList<String>();
		c.add("a");
		c.add("bbb");
		System.out.println(c.contains("a"));*/		
	}
}
