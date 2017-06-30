package samples;
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="3337777131" > 
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
import java.util.Hashtable;

import com.ibm.mq.MQDestination;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

/**
 * Demonstrates using the publish/subscribe API introduced into WebSphere MQ v7 Use system
 * properties to define the parameters :-
 * <ul>
 * <li>com.ibm.mq.pubSubSample.queueManagerName
 * <li>com.ibm.mq.pubSubSample.topicObject (default is "myTopicObject")
 * <li>com.ibm.mq.pubSubSample.subscriberCount (default is 3)
 * <ul>
 */
public class MQPubSubApiSample {

  // @COPYRIGHT_START@
  /** Comment for copyright_notice */
  static final String copyright_notice = "Licensed Materials - Property of IBM "
      + "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
      + "(c) Copyright IBM Corp. 2008, 2009 All Rights Reserved. "
      + "US Government Users Restricted Rights - Use, duplication or "
      + "disclosure restricted by GSA ADP Schedule Contract with " + "IBM Corp.";
  // @COPYRIGHT_END@

  /** The SCCSID which is expanded when the file is extracted from CMVC */
  public static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/wmqjava/MQPubSubApiSample.java";

  /** The name of the Queue Manager to use in these tests */
  private String queueManagerName;

  /*
   * used for Wait/Notify between subscribers and publisher.
   */
  private static Object syncPoint = new Object();
  private static volatile int readySubscribers = 0;
  private Hashtable properties;
  private String topicString;
  private String topicObject;
  private Thread[] subscribers;
  private static int subscriberCount;

  MQPubSubApiSample() {
    topicString = null;
    topicObject = System.getProperty("com.ibm.mq.pubSubSample.topicObject", "myTopicObject");
    queueManagerName = System.getProperty("com.ibm.mq.pubSubSample.queueManagerName");
    subscriberCount = Integer.getInteger("com.ibm.mq.pubSubSample.subscriberCount", 3).intValue();
    properties = new Hashtable();
    properties.put("hostname", "localhost");
    properties.put("port", new Integer(1414));
    properties.put("channel", "SYSTEM.DEF.SVRCONN");
    return;
  }

  /**
   * Main entry point
   * 
   * @param arg command line arguments (ignored)
   */
  public static void main(String[] arg) {

    MQPubSubApiSample sample = new MQPubSubApiSample();

    sample.launchSubscribers();

    // Wait till the subscribers have initialised and notified us...
    synchronized (syncPoint) {
      while (readySubscribers < subscriberCount) {
        try {
          syncPoint.wait();
        }
        catch (InterruptedException e) {
          // We'll silently ignore it
        }
      }
    }

    try {
      sample.doPublish();
    }
    catch (MQException e) {
      e.printStackTrace();
      for (Throwable t = e.getCause(); t != null; t = t.getCause()) {
        System.out.println("... Caused by ");
        t.printStackTrace();
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return;
  }

  private void launchSubscribers() {
    System.out.println("Launching the subscribers");
    subscribers = new Thread[subscriberCount];
    for (int threadNo = 0; threadNo < subscribers.length; threadNo++) {
      subscribers[threadNo] = new Subscriber("Subscriber" + threadNo);
      subscribers[threadNo].start();
    }
    return;
  }

  private void doPublish() throws MQException, IOException {
    int destinationType = CMQC.MQOT_TOPIC;

    /* Do the publish */
    MQQueueManager queueManager = new MQQueueManager(queueManagerName, properties);
    MQMessage messageForPut = new MQMessage();
    System.out.println("***Publishing ***");
    messageForPut.writeString("Hello world!");
    queueManager.put(destinationType, topicObject, messageForPut);
    return;
  }

  class Subscriber extends Thread {

    private String myName;
    final int openOptionsForGet = CMQC.MQSO_CREATE | CMQC.MQSO_FAIL_IF_QUIESCING
        | CMQC.MQSO_MANAGED | CMQC.MQSO_NON_DURABLE;

    Subscriber(String subscriberName) {
      super(subscriberName);
      myName = subscriberName;
      return;
    }

    /**
     * run method for a subscriber - waits for data
     */
    public void run() {
      try {
        System.out.println(myName + " - ***Subscribing***");
        // Must use our own queue manager or we will block...
        MQQueueManager queueManager = new MQQueueManager(queueManagerName, properties);
        MQDestination destinationForGet = queueManager.accessTopic(topicString, topicObject,
            CMQC.MQTOPIC_OPEN_AS_SUBSCRIPTION, openOptionsForGet);

        // Let the controlling thread know we've started ...
        synchronized (syncPoint) {
          readySubscribers++;
          syncPoint.notify();
        }
        MQGetMessageOptions mgmo = new MQGetMessageOptions();
        mgmo.options = CMQC.MQGMO_WAIT;
        mgmo.waitInterval = 30000;
        System.out.println(myName + " - ***Retrieving***");
        MQMessage messageForGet = new MQMessage();
        synchronized (getClass()) {
          destinationForGet.get(messageForGet, mgmo);
        }
        String messageDataFromGet = messageForGet.readLine();
        System.out.println(myName + " - Got [" + messageDataFromGet + "]");
      }
      catch (Exception e) {
        System.err.println(myName + " " + e);
        e.printStackTrace();
        for (Throwable t = e.getCause(); t != null; t = t.getCause()) {
          System.out.println("... Caused by ");
          t.printStackTrace();
        }
        return;
      }
      readySubscribers--;
      return;
    }
  }

}
