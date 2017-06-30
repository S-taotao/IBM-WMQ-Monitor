package samples;
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="1995,2012" 
 *   crc="3730308103" > 
 *  Licensed Materials - Property of IBM  
 *   
 *  5724-H72,5655-R36,5655-L82,5724-L26, 
 *   
 *  (C) Copyright IBM Corp. 1995, 2012 All Rights Reserved.  
 *   
 *  US Government Users Restricted Rights - Use, duplication or  
 *  disclosure restricted by GSA ADP Schedule Contract with  
 *  IBM Corp.  
 *   </copyright> 
 */

import java.util.Iterator;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQHeader;
import com.ibm.mq.headers.MQHeaderList;
import com.ibm.mq.headers.MQIIH;
import com.ibm.mq.headers.MQRFH2;

/**
 * Simple program to demonstrate using the IMS Bridge with the MQ Classes for Java<p>
 * 
 * <strong>NOTE:</strong> unlike most of the samples, this cannot be run "as is", but is still 
 * a useful exemplar of how the IMS Bridge can be invoked from Java.<p>
 * 
 * In our example IMS system, the "NNNSR002" transaction takes no data, and returns 2 segments of data as a reply
 * <ul>
 * <li>in COMMIT_THEN_SEND mode, this will be in 2 separate reply messages</li>
 * <Li>in SEND_THEN_COMMIT mode, this will be in a single reply message, as 2 segments</li>
 * </ul>
 * 
 * In the sample, we pass some arbitrary data in an MQRFH2 header, simply to illustrate how this could be done -
 * for example to maintain some state data.<P>
 *  
 * Exception handling in this sample is limited. 
 */
public class IMSBridgeSample {

  // @COPYRIGHT_START@
  /** Comment for copyright_notice */
  static final String copyright_notice = "Licensed Materials - Property of IBM "
      + "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
      + "(c) Copyright IBM Corp. 2002 All Rights Reserved. "
      + "US Government Users Restricted Rights - Use, duplication or "
      + "disclosure restricted by GSA ADP Schedule Contract with " + "IBM Corp.";
  // @COPYRIGHT_END@
  static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/wmqjava/IMSBridgeSample.java";

  /** Queue Manager enabled for the IMS Bridge */
  private static String queueManagerName = System.getProperty("queueManager");

  /** IMS "request" queue */
  private static String requestQueueName = System.getProperty("requestQueue");

  /** IMS "reply" queue */
  private static String replyQueueName = System.getProperty("replyQueue");

  /** host for the queue manager */
  private static String hostName = System.getProperty("host");

  /** port for the chinit */
  private static int port = Integer.getInteger("port").intValue();

  /** channel to use */
  private static String channelName = System.getProperty("channelName");

  /** How long to wait for a response - in seconds 
   * (I've taken a totally arbitrary default here - a realistic figure depends on the behaviour of the IMS system) 
   */
  private static int pauseTime = Integer.getInteger("pauseTime", 5);

  private MQQueueManager queueManager;
  private MQQueue requestQueue;
  private MQQueue replyQueue;
  private MQGetMessageOptions gmo; // To set get wait

  /**
   * Constructor - connects to the QM, opens queues, etc...
   * @throws MQException
   */
  @SuppressWarnings("unchecked")
  // Silence a warning about unchecked collection MQEnvironment.properties
  public IMSBridgeSample() throws MQException {
    MQEnvironment.hostname = hostName;
    MQEnvironment.channel = channelName;
    MQEnvironment.port = port;
    MQEnvironment.properties.put(MQConstants.TRANSPORT_PROPERTY, MQConstants.TRANSPORT_MQSERIES_CLIENT);

    queueManager = new MQQueueManager(queueManagerName);

    requestQueue = queueManager.accessQueue(requestQueueName, MQConstants.MQOO_OUTPUT);

    replyQueue = queueManager.accessQueue(replyQueueName, MQConstants.MQOO_INPUT_SHARED);

    gmo = new MQGetMessageOptions();
    gmo.waitInterval = pauseTime * 1000;
    gmo.options = MQConstants.MQGMO_WAIT;
  }

  private void tearDown() {
    try {
      requestQueue.close();
      replyQueue.close();
      queueManager.close();
    }
    catch (MQException mqe) {
      // In this case, I'll simply ignore any exceptions
    }
  }

  /**
   * @param args
   * @throws Exception 
    */
  public static void main(String[] args) throws Exception {
    IMSBridgeSample firstTest = new IMSBridgeSample();

    firstTest.runTest("NNNSR002", CMQC.MQICM_COMMIT_THEN_SEND);
    firstTest.runTest("NNNSR002", CMQC.MQICM_SEND_THEN_COMMIT);

    firstTest.tearDown();
  }

  private void runTest(String transaction, char commitMode) throws Exception {

    System.out.println("Testing transaction {" + transaction + "} commitMode {" + commitMode + "}");

    // This message will be used to send the IMS transaction
    MQMessage putMessage = new MQMessage();

    // I'm just putting some arbitrary data in the RFH2
    // I'm not setting charset or encoding as they should simply be inherited from the MQMD
    MQRFH2 rfh2 = new MQRFH2();
    rfh2.setFolderStrings(new String[]{"<d1>First</d1>", "<d2>Second</d2>", "<d3>Third</d3>"});

    // I'm only using the IIH to set the commitMode
    // I'm not setting charset or encoding as they should simply be inherited from the MQMD
    MQIIH iih = new MQIIH();
    iih.setCommitMode(commitMode);

    // Build the list of headers - this lets us handle the format chaining cheaply and easily
    // Note that the RFH2 should be first - or perhaps more accurately, the IIH must be last - in the chain
    // as the LLZZ data is expected to follow the IIH
    MQHeaderList putList = new MQHeaderList();
    putList.add(rfh2);
    putList.add(iih);

    // Set the data format to be MQFMT_IMS_VAR_STRING and put the appropriate (first header's) format into the MD 
    putMessage.format = putList.updateHeaderChaining(CMQC.MQFMT_IMS_VAR_STRING);

    // Note that charset and encoding do not need to be set. MQ knows how to convert the data as necessary.

    // Set the replyTo queue details, as this is how the IMS bridge responses are delivered
    putMessage.replyToQueueName = replyQueueName;
    putMessage.replyToQueueManagerName = queueManagerName;

    // write the header chain to the message, this will convert the data as appropriate
    // and handle the chaining of ccsids and encodings (in this case, all defaulted)
    putList.write(putMessage);

    // Now write the LLZZ data for IMS. In this case, it's simply the LL (length),  ZZ (zeroes) and the transaction code (as bytes)
    putMessage.writeShort(transaction.length() + 4); // LL includes the LL & ZZ bytes
    putMessage.writeShort(0); // ZZ
    putMessage.writeString(transaction);

    System.out.println("Putting the message");
    requestQueue.put(putMessage);

    // Now process the responses - remember the response segments may (or may not) be split over multiple messages
    boolean keepTrying = true;
    while (keepTrying) {
      try {
        MQMessage gotMessage = new MQMessage();

        System.out.println("Getting the message");
        replyQueue.get(gotMessage, gmo);

        System.out.println("Got message - format " + gotMessage.format + " ccsid " + gotMessage.characterSet + " encoding " + gotMessage.encoding);

        // Take the headers apart, by loading them into a List and iterating over it
        MQHeaderList gotList = new MQHeaderList(gotMessage);
        Iterator<?> gotIterator = gotList.listIterator();
        while (gotIterator.hasNext()) {
          MQHeader header = (MQHeader) gotIterator.next();
          System.out.println("Got Header - {" + header.toString() + "}");

          // If it's an RFH2, dump out the folder strings 
          if (header.type() == "MQRFH2") {
            String[] folderStrings = ((MQRFH2) header).getFolderStrings();
            for (String s : folderStrings) {
              System.out.println("\t{" + s + "}");
            }
          }
        }

        // Now pick apart the segment data 
        // the MQHeaderList constructor will have left the data pointer at the first LL
        for (int segment = 0; gotMessage.getDataLength() > 0; segment++) {
          short LL = gotMessage.readShort();
          gotMessage.readShort(); // skip over the ZZ
          String transactionData = gotMessage.readStringOfByteLength(LL - 4);// LL includes the LL & ZZ bytes
          System.out.println("Segment(" + segment + ") data = {" + transactionData + "}");
        }
      }
      catch (MQException mqe) {
        if (mqe.getReason() == MQConstants.MQRC_NO_MSG_AVAILABLE) { // We expect this
          keepTrying = false;
        }
        else { // We don't expect this...
          throw mqe;
        }
      }
    }
  }

}
