package samples;
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="3916320166" > 
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
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class for message handling in the MQ base java samples
 */
public class MQSampleMessageManager {

  // @COPYRIGHT_START@
  /** Comment for copyright_notice */
  static final String copyright_notice = "Licensed Materials - Property of IBM "
      + "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
      + "(c) Copyright IBM Corp. 1999, 2009 All Rights Reserved. "
      + "US Government Users Restricted Rights - Use, duplication or "
      + "disclosure restricted by GSA ADP Schedule Contract with " + "IBM Corp.";
  // @COPYRIGHT_END@

  /** The SCCSID which is expanded when the file is extracted from CMVC */
  public static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/wmqjava/MQSampleMessageManager.java";

  private ResourceBundle messages = null;

  private boolean loadedOK = false;

  MQSampleMessageManager(String bundleName) {
    try {
      messages = ResourceBundle.getBundle(bundleName);
      loadedOK = true;
    }
    catch (MissingResourceException e) {
      System.out.println(e);
      System.out.println("Unable to load the message catalogue mqjcivp.properties");
      System.out.println("Please ensure that the samples directory is in your CLASSPATH");
      System.out.println("and try again.");
      loadedOK = false;
    }
    return;
  }

  /**
   * Gets a message identified by an integer
   * 
   * @param msgId the identifier
   * @return the message
   */
  public String getMessage(int msgId) {
    String msg = messages.getString(Integer.toString(msgId));
    return msg;
  }

  /**
   * Gets a message identified by an integer, interpolating inserted text from an array
   * 
   * @param msgId the identifier
   * @param inserts the array of inserted text strings
   * @return the message
   */
  public String getMessage(int msgId, Object[] inserts) {
    String msg = messages.getString(Integer.toString(msgId));
    return MessageFormat.format(msg, inserts);
  }

  /**
   * Indicates that the message resource bundle was (or was not) loaded successfully
   * 
   * @return Whether or not the resource bundle was loaded successfully
   */
  public boolean loadedOK() {
    return loadedOK;
  }

}
