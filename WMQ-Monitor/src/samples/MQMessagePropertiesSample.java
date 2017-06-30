package samples;
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="393373229" > 
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
import java.util.Enumeration;

import com.ibm.mq.MQDestination;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

/**
 * Demonstrates the use of the Message Properties API introduced into WebSphere MQ v7
 * 
 * To Run it, use "java MQMessagePropertiesSample <queue_manager_name>"
 * 
 */
public class MQMessagePropertiesSample {

  // @COPYRIGHT_START@
  /** Comment for copyright_notice */
  static final String copyright_notice = "Licensed Materials - Property of IBM "
      + "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
      + "(c) Copyright IBM Corp. 2008, 2009 All Rights Reserved. "
      + "US Government Users Restricted Rights - Use, duplication or "
      + "disclosure restricted by GSA ADP Schedule Contract with " + "IBM Corp.";
  // @COPYRIGHT_END@

  /** The SCCSID which is expanded when the file is extracted from CMVC */
  public static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/wmqjava/MQMessagePropertiesSample.java"; //$NON-NLS-1$

  /** The name of the default queue for putting message */
  protected static final String DEFAULT_QUEUE = "SYSTEM.DEFAULT.LOCAL.QUEUE"; //$NON-NLS-1$

  /** The Queue Manager to use in these tests */
  protected MQQueueManager queueManager;

  MQMessagePropertiesSample(String queueManagerName) throws MQException {
    /* Create the queue manager */
    queueManager = new MQQueueManager(queueManagerName);
    return;
  }

  /**
   * Main entry point
   * 
   * @param arg - the queue manager name
   */
  public static void main(String[] arg) {
    MQMessagePropertiesSample sample = null;
    try {
      sample = new MQMessagePropertiesSample(arg[0]);
      System.out.println("Using basic properties :-");
      sample.useBasicProperties();
      System.out.println();
      System.out.println("Using property folders :-");
      sample.usePropertyFolders();
    }
    catch (Exception e) {
      e.printStackTrace();
      for (Throwable t = e.getCause(); t != null; t = t.getCause()) {
        System.out.println("... Caused by ");
        t.printStackTrace();
      }
      System.exit(-1);
    }
    return;
  }

  /**
   * Demonstrates sending properties using various data types and retrieving them as primitives, as
   * objects and via the enumeration returned by getPropertyNames() .
   * 
   * @throws MQException
   * @throws IOException
   */
  public void useBasicProperties() throws MQException, IOException {
    /* Property names and values to test */
    String booleanPropertyName = "testBooleanProperty";
    boolean booleanPropertyValueForPut = true;

    String bytePropertyName = "testByteProperty";
    byte bytePropertyValueForPut = 55;

    String intPropertyName = "testIntProperty";
    int intPropertyValueForPut = 555555555;

    String longPropertyName = "testLongProperty";
    long longPropertyValueForPut = -5555555555555555555L;

    String doublePropertyName = "testDoubleProperty";
    double doublePropertyValueForPut = 55555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555.55555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555;

    String stringPropertyName = "testStringProperty";
    String stringPropertyValueForPut = "hello";

    String objectPropertyName = "testObjectProperty";
    Object objectPropertyValueForPut = new Integer(444444444);

    String nullPropertyName = "testNullProperty";
    Object nullPropertyValueForPut = null;

    /* Create the message */
    MQMessage messageForPut = createDefaultMQMessage();
    messageForPut.setBooleanProperty(booleanPropertyName, booleanPropertyValueForPut);
    messageForPut.setByteProperty(bytePropertyName, bytePropertyValueForPut);
    messageForPut.setIntProperty(intPropertyName, intPropertyValueForPut);
    messageForPut.setLongProperty(longPropertyName, longPropertyValueForPut);
    messageForPut.setDoubleProperty(doublePropertyName, doublePropertyValueForPut);
    messageForPut.setStringProperty(stringPropertyName, stringPropertyValueForPut);
    messageForPut.setObjectProperty(objectPropertyName, objectPropertyValueForPut);
    messageForPut.setObjectProperty(nullPropertyName, nullPropertyValueForPut);

    /* Do the put/get */
    MQMessage messageForGet = doPutGet(messageForPut);

    System.out.println("\tGetting properties as defined types :-");
    showValues(booleanPropertyName, booleanPropertyValueForPut, messageForGet
        .getBooleanProperty(booleanPropertyName));
    showValues(bytePropertyName, bytePropertyValueForPut, messageForGet
        .getByteProperty(bytePropertyName));
    showValues(intPropertyName, intPropertyValueForPut, messageForGet
        .getIntProperty(intPropertyName));
    showValues(longPropertyName, longPropertyValueForPut, messageForGet
        .getLongProperty(longPropertyName));
    showValues(doublePropertyName, doublePropertyValueForPut, messageForGet
        .getDoubleProperty(doublePropertyName));
    showValues(stringPropertyName, stringPropertyValueForPut, messageForGet
        .getStringProperty(stringPropertyName));
    showValues(objectPropertyName, objectPropertyValueForPut, messageForGet
        .getObjectProperty(objectPropertyName));
    showValues(nullPropertyName, nullPropertyValueForPut, messageForGet
        .getObjectProperty(nullPropertyName));

    System.out.println();
    System.out.println("\tGetting properties as objects :-");
    showValues(booleanPropertyName, booleanPropertyValueForPut, messageForGet
        .getObjectProperty(booleanPropertyName));
    showValues(bytePropertyName, bytePropertyValueForPut, messageForGet
        .getObjectProperty(bytePropertyName));
    showValues(intPropertyName, intPropertyValueForPut, messageForGet
        .getObjectProperty(intPropertyName));
    showValues(longPropertyName, longPropertyValueForPut, messageForGet
        .getObjectProperty(longPropertyName));
    showValues(doublePropertyName, doublePropertyValueForPut, messageForGet
        .getObjectProperty(doublePropertyName));
    showValues(stringPropertyName, stringPropertyValueForPut, messageForGet
        .getObjectProperty(stringPropertyName));

    /*
     * Wildcard property names requests - those whose name starts with "testB"
     */
    System.out.println();
    System.out.println("\tGetting properties starting with \"testB\" via the enumeration :-");

    Enumeration propertyNames = messageForGet.getPropertyNames("testB%");
    while (propertyNames.hasMoreElements()) {
      String name = (String) propertyNames.nextElement();
      Object value = messageForGet.getObjectProperty(name);
      System.out.println("\t\tProperty Name: " + name + " value: [" + objectClassName(value) + ":"
          + value + "]");
    }

    return;
  }

  private void showValues(String propertyName, double propertyValueForPut,
      Object propertyValueFromGet) {
    System.out.println("\t\tProperty Name: " + propertyName + " put: [" + propertyValueForPut
        + "] got [" + objectClassName(propertyValueFromGet) + ":" + propertyValueFromGet + "]");
    return;
  }

  private void showValues(String propertyName, long propertyValueForPut, Object propertyValueFromGet) {
    System.out.println("\t\tProperty Name: " + propertyName + " put: [" + propertyValueForPut
        + "] got [" + objectClassName(propertyValueFromGet) + ":" + propertyValueFromGet + "]");
    return;
  }

  private void showValues(String propertyName, boolean propertyValueForPut,
      Object propertyValueFromGet) {
    System.out.println("\t\tProperty Name: " + propertyName + " put: [" + propertyValueForPut
        + "] got [" + objectClassName(propertyValueFromGet) + ":" + propertyValueFromGet + "]");
    return;
  }

  private void showValues(String propertyName, double propertyValueForPut,
      double propertyValueFromGet) {
    System.out.println("\t\tProperty Name: " + propertyName + " put: [" + propertyValueForPut
        + "] got [" + propertyValueFromGet + "]");
    return;
  }

  private void showValues(String propertyName, Object propertyValueForPut,
      Object propertyValueFromGet) {
    System.out.println("\t\tProperty Name: " + propertyName + " put: ["
        + objectClassName(propertyValueForPut) + ":" + propertyValueForPut + "] got ["
        + objectClassName(propertyValueFromGet) + ":" + propertyValueFromGet + "]");
    return;
  }

  private String objectClassName(Object o) {
    return (o == null) ? "nullClass" : o.getClass().getName();
  }

  private void showValues(String propertyName, String propertyValueForPut,
      String propertyValueFromGet) {
    System.out.println("\t\tProperty Name: " + propertyName + " put: [" + propertyValueForPut
        + "] got [" + propertyValueFromGet + "]");
    return;
  }

  private void showValues(String propertyName, long propertyValueForPut, long propertyValueFromGet) {
    System.out.println("\t\tProperty Name: " + propertyName + " put: [" + propertyValueForPut
        + "] got [" + propertyValueFromGet + "]");
    return;
  }

  private void showValues(String propertyName, boolean propertyValueForPut,
      boolean propertyValueFromGet) {
    System.out.println("\t\tProperty Name: " + propertyName + " put: [" + propertyValueForPut
        + "] got [" + propertyValueFromGet + "]");
    return;
  }

  /**
   * Demonstrates storing and retrieving properties in a variety of folders/groups within the RFH2.
   * 
   * @throws MQException
   * @throws IOException
   */
  public void usePropertyFolders() throws MQException, IOException {
    /* Property names and values to test with folders */
    // This has no explicit folder, so is in the "usr." folder
    String normalPropertyName = "testStringProperty";
    String normalPropertyValueForPut = "hello";

    // This is explicitly in the "usr." folder
    String explicitUsrPropertyName = "usr.testStringProperty2";
    String explicitUsrPropertyValueForPut = "hello2";

    // This is in a non-default folder - "test."
    String otherGroupPropertyName = "test.testStringProperty3";
    String otherGroupPropertyValueForPut = "hello3";

    // This is in a subfolder ...
    String subGroupPropertyName = "test.test2.testStringProperty4";
    String subGroupPropertyValueForPut = "hello4";

    /* Create the message */
    MQMessage messageForPut = createDefaultMQMessage();
    messageForPut.setStringProperty(normalPropertyName, normalPropertyValueForPut);
    messageForPut.setStringProperty(explicitUsrPropertyName, explicitUsrPropertyValueForPut);
    messageForPut.setStringProperty(otherGroupPropertyName, otherGroupPropertyValueForPut);
    messageForPut.setStringProperty(subGroupPropertyName, subGroupPropertyValueForPut);

    /* Do the put/get */
    MQMessage messageForGet = doPutGet(messageForPut);

    /* Retrieve and display the properties */
    String normalPropertyValueFromGet = messageForGet
        .getStringProperty("usr." + normalPropertyName);
    showValues(normalPropertyName, normalPropertyValueForPut, normalPropertyValueFromGet);
    String explicitUsrPropertyValueFromGet = messageForGet
        .getStringProperty(explicitUsrPropertyName.substring(4));
    showValues(explicitUsrPropertyName.substring(4), explicitUsrPropertyValueForPut,
        explicitUsrPropertyValueFromGet);
    String otherGroupPropertyValueFromGet = messageForGet.getStringProperty(otherGroupPropertyName);
    showValues(otherGroupPropertyName, otherGroupPropertyValueForPut,
        otherGroupPropertyValueFromGet);
    String subGroupPropertyValueFromGet = messageForGet.getStringProperty(subGroupPropertyName);
    showValues(subGroupPropertyName, subGroupPropertyValueForPut, subGroupPropertyValueFromGet);
    return;
  }

  /**
   * Creates an MQMessage with a default string as the message body.
   * 
   * @return the new MQMessage
   * @throws IOException
   */
  private MQMessage createDefaultMQMessage() throws IOException {
    MQMessage message = new MQMessage();
    message.writeString("hello world!");
    message.format = CMQC.MQFMT_STRING;
    return message;
  }

  /**
   * Performs a put and a get for the specified MQMessage
   * 
   * @param messageForPut the MQMessage to put
   * @return the message received on the get
   * @throws MQException
   * @throws IOException
   */
  private MQMessage doPutGet(MQMessage messageForPut) throws MQException {
    MQDestination destinationForGet = queueManager.accessQueue(DEFAULT_QUEUE,
        CMQC.MQOO_INPUT_EXCLUSIVE | CMQC.MQOO_FAIL_IF_QUIESCING);

    MQDestination destinationForPut = queueManager.accessQueue(DEFAULT_QUEUE, CMQC.MQOO_OUTPUT
        | CMQC.MQOO_FAIL_IF_QUIESCING);
    destinationForPut.put(messageForPut);

    /* Do the get */
    MQMessage messageForGet = new MQMessage();
    destinationForGet.get(messageForGet);

    /* Close the destinations */
    destinationForGet.close();
    destinationForPut.close();

    return messageForGet;
  }
}
