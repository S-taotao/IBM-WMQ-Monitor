package samples;
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2002,2012" 
 *   crc="916466477" > 
 *  Licensed Materials - Property of IBM  
 *   
 *  5724-H72,5655-R36,5655-L82,5724-L26, 
 *   
 *  (C) Copyright IBM Corp. 2002, 2012 All Rights Reserved.  
 *   
 *  US Government Users Restricted Rights - Use, duplication or  
 *  disclosure restricted by GSA ADP Schedule Contract with  
 *  IBM Corp.  
 *   </copyright> 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.Locale;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;

/**
 * Websphere MQ for Java Installation Verification Program
 * 
 * Runs "standalone"
 * 
 * @author IBM Corporation
 * 
 */
public class MQIVP {

  // @COPYRIGHT_START@
  /** Comment for copyright_notice */
  static final String copyright_notice = "Licensed Materials - Property of IBM "
                                         + "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
                                         + "(c) Copyright IBM Corp. 2002, 2009 All Rights Reserved. "
                                         + "US Government Users Restricted Rights - Use, duplication or "
                                         + "disclosure restricted by GSA ADP Schedule Contract with " + "IBM Corp.";
  // @COPYRIGHT_END@
  static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/wmqjava/MQIVP.java";

  private MQSampleMessageManager messageMgr;
  private String hostname;
  private String channel = null;
  private String queueMgr;
  private MQQueueManager queueManager;
  private String port = "-1"; // @L1C
  private Hashtable properties = null; // @L1A
  // An output stream writer to convert the characters to
  // bytes using a suitable converter.
  private OutputStreamWriter os = null;

  private BufferedReader stdin = null;

  /**
   * Entry point - runs the basic test
   * 
   * @param args optionally "-trace" to turn tracing on
   */
  public static void main(String args[]) {
    // Turn tracing on?
    if ((args.length >= 1) && (args[0] != null)) {
      if (args[0].toLowerCase().equals("-trace")) {
        MQEnvironment.enableTracing(1);
      }
    }

    MQIVP ivp = new MQIVP();

    try {
      ivp.getParameters();
      ivp.simpleTest();
      ivp.finish();
    }
    catch (IOException e) {
      System.out.println(ivp.messageMgr.getMessage(49)); // a little inelegant, I'm afraid
      System.out.flush();
      System.exit(-1);
    }
    System.exit(0);
    return;
  }

  /**
   * Constructor - takes no arguments.
   */
  MQIVP() {
    messageMgr = new MQSampleMessageManager("mqjcivp");
    if (!messageMgr.loadedOK()) {
      System.exit(-1);
    }
    properties = new Hashtable();
    stdin = new BufferedReader(new InputStreamReader(System.in));
    String encodingName = System.getProperty("file.encoding");

    if (System.getProperty("os.name").substring(0, 3).equalsIgnoreCase("win")) {
      // Need to see if locale is set to a single-byte
      // language. If it is we cannot use the default
      // char-to-byte converter but have to use "850"
      Locale locale = Locale.getDefault();
      String[] lang = new String[]{"de", "en", "es", "fr", "it", "pt"};
      String langUsing = locale.getLanguage();
      for (int i = 0; i < lang.length; i++) {
        if (langUsing.equals(lang[i])) {
          encodingName = "850";
          break;
        }
      }
    }
    try {
      os = new OutputStreamWriter(System.out, encodingName);
    }
    catch (Exception e) {
      // Use default instead
      os = new OutputStreamWriter(System.out);
    }
    return;
  }

  /**
   * get a parameter value from the user, using a numbered prompt from our resource bundle
   * 
   * @param prompt a prompt number
   * @param defaultValue optional default if user enters nothing
   * 
   * @return String containing the value
   */
  private String getParameter(int prompt, String defaultValue) {
    try {
      write(messageMgr.getMessage(prompt));
      String result = stdin.readLine();
      if (result.equals("") && (defaultValue != null)) {
        result = defaultValue;
      }
      return result;
    }
    catch (IOException ioe) {
      return null;
    }
  }

  /**
   * Get the parameters (host, port, queue manager name, etc) from the user
   * 
   * @throws IOException If there is a problem
   */
  @SuppressWarnings("unchecked")
  private void getParameters() throws IOException {

    writeln(messageMgr.getMessage(44));

    hostname = getParameter(45, null);
    properties.put("hostname", hostname);

    if (!hostname.equals("")) {
      port = getParameter(46, "-1");
      try {
        properties.put("port", new Integer(Integer.parseInt(port)));
      }
      catch (NumberFormatException e) {
        writeln(messageMgr.getMessage(33));
        properties.put("port", new Integer(-1));
      }
      channel = getParameter(47, null);
      if (channel != null) {
        properties.put("channel", channel);
      }
    }
    queueMgr = getParameter(48, null);

    // If this is a DBCS queue manager, then we might need to adjust our
    // CCSID
    try {
      String strCCSID = messageMgr.getMessage(57);
      int iCCSID = Integer.parseInt(strCCSID);
      properties.put("CCSID", new Integer(iCCSID));
    }
    catch (Exception ex) {
      // Never mind, leave the CCSID as it was...
    }
    properties.put(MQConstants.APPNAME_PROPERTY, "MQIVP (Java)");
    return;
  }

  /**
   * Performs a simple test of the MQ functionality
   * 
   * @throws IOException
   */
  private void simpleTest() throws IOException {
    // Connect to MQ!!
    try {
      queueManager = new MQQueueManager(queueMgr, properties);
    }
    catch (java.lang.NoClassDefFoundError e) {
      writeln(messageMgr.getMessage(62));
      writeln(e.toString());
      return;
    }
    catch (MQException ex) {
      writeln(messageMgr.getMessage(9));
      switch (ex.reasonCode) {
        case 2009 :
          writeln(messageMgr.getMessage(10));
          break;
        case 2059 :
          writeln(messageMgr.getMessage(11));
          break;
        case 2058 :
          writeln(messageMgr.getMessage(50));
          break;
        case 2063 :
          writeln(messageMgr.getMessage(13));
          break;
        default :
          writeln(messageMgr.getMessage(14, new Object[]{new Integer(ex.reasonCode)}));
      }
      return;
    }

    writeln(messageMgr.getMessage(15));

    // Open a queue...
    MQQueue system_default_local_queue;
    try {
      system_default_local_queue = queueManager.accessQueue("SYSTEM.DEFAULT.LOCAL.QUEUE",
          MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_OUTPUT, // input, output, inq, set
          null, null, null);
    }
    catch (MQException ex) {
      writeln(messageMgr.getMessage(16));
      switch (ex.reasonCode) {
        case 2009 :
          writeln(messageMgr.getMessage(10));
          break;
        case 2085 :
          writeln(messageMgr.getMessage(17));
          break;
        default :
          writeln(messageMgr.getMessage(14, new Object[]{new Integer(ex.reasonCode)}));
      }
      try {
        queueManager.disconnect();
      }
      catch (MQException e) {
        // We'll silently ignore it
      }
      return;
    }

    writeln(messageMgr.getMessage(18));

    // Put a message!!!
    MQMessage hello_world = new MQMessage();
    hello_world.characterSet = 1208;
    try {
      hello_world.writeUTF("Hello World!");
    }
    catch (Exception ex) {
      writeln(messageMgr.getMessage(19));
    }
    try {
      system_default_local_queue.put(hello_world, new MQPutMessageOptions());
    }
    catch (MQException ex) {
      writeln(messageMgr.getMessage(20));
      switch (ex.reasonCode) {
        case 2009 :
          writeln(messageMgr.getMessage(10));
          break;
        case 2030 :
          writeln(messageMgr.getMessage(21));
          break;
        case 2053 :
          writeln(messageMgr.getMessage(22));
          break;
        default :
          writeln(messageMgr.getMessage(14, new Object[]{new Integer(ex.reasonCode)}));
      }
      try {
        system_default_local_queue.close();
      }
      catch (MQException e) {
        // We'll silently ignore it
      }
      try {
        queueManager.disconnect();
      }
      catch (MQException e) {
        // We'll silently ignore it
      }
      return;
    }

    writeln(messageMgr.getMessage(23));

    // Get a message!!!
    MQMessage hello_world_msg;
    try {
      hello_world_msg = new MQMessage();
      hello_world_msg.messageId = hello_world.messageId;
      hello_world_msg.characterSet = 1208;
      system_default_local_queue.get(hello_world_msg, new MQGetMessageOptions(), 30);
    }
    catch (MQException ex) {
      writeln(messageMgr.getMessage(24));
      switch (ex.reasonCode) {
        case 2009 :
          writeln(messageMgr.getMessage(10));
          break;
        case 2033 :
          writeln(messageMgr.getMessage(25));
          break;
        default :
          writeln(messageMgr.getMessage(14, new Object[]{new Integer(ex.reasonCode)}));
      }
      try {
        system_default_local_queue.close();
      }
      catch (MQException e) {
        // We'll silently ignore it
      }
      try {
        queueManager.disconnect();
      }
      catch (MQException e) {
        // We'll silently ignore it
      }
      return;
    }

    String msgReceived;
    try {
      msgReceived = hello_world_msg.readUTF();
    }
    catch (Exception io_ex) {
      writeln(messageMgr.getMessage(26));
      try {
        system_default_local_queue.close();
      }
      catch (MQException e) {
        // We'll silently ignore it
      }
      try {
        queueManager.disconnect();
      }
      catch (MQException e) {
        // We'll silently ignore it
      }
      return;
    }

    if (!msgReceived.equals("Hello World!")) {
      writeln(messageMgr.getMessage(26));
      try {
        system_default_local_queue.close();
      }
      catch (MQException e) {
        // We'll silently ignore it
      }
      try {
        queueManager.disconnect();
      }
      catch (MQException e) {
        // We'll silently ignore it
      }
      return;
    }

    writeln(messageMgr.getMessage(27));

    // Close it again...
    try {
      system_default_local_queue.close();
    }
    catch (MQException ex) {
      writeln(messageMgr.getMessage(28));
      switch (ex.reasonCode) {
        case 2009 :
          writeln(messageMgr.getMessage(10));
          break;
        default :
          writeln(messageMgr.getMessage(14, new Object[]{new Integer(ex.reasonCode)}));
      }
      try {
        queueManager.disconnect();
      }
      catch (MQException e) {
        // We'll silently ignore it
      }
      return;
    }

    writeln(messageMgr.getMessage(29));

    // Disconnect from MQ!!
    try {
      queueManager.disconnect();
    }
    catch (MQException ex) {
      writeln(messageMgr.getMessage(30));
      switch (ex.reasonCode) {
        case 2009 :
          writeln(messageMgr.getMessage(10));
          break;
        default :
          writeln(messageMgr.getMessage(14, new Object[]{new Integer(ex.reasonCode)}));
      }
      return;
    }

    writeln(messageMgr.getMessage(31));

    writeln(messageMgr.getMessage(32));

    // Press Enter to continue ...
    writeln(messageMgr.getMessage(63));
    System.in.read();

    return;
  }

  /**
   * Clean up - close down output stream
   */
  private void finish() {
    try {
      if (os != null) {
        os.close();
        os = null;
      }
    }
    catch (Exception e) {
      // cannot really do anything
    }
    return;
  }

  /**
   * Display a message, ending in a new line
   * 
   * @param message String to be output
   * @throws IOException
   */
  private void writeln(String message) throws IOException {
    os.write(message + "\n");
    os.flush();
    return;
  }

  /**
   * Display a message, not ending in a new line
   * 
   * @param message String to be output
   * @throws IOException
   */
  private void write(String message) throws IOException {
    os.write(message);
    os.flush();
    return;
  }
}
