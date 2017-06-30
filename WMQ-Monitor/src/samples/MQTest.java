package samples;
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="1997,2012" 
 *   crc="1571593006" > 
 *  Licensed Materials - Property of IBM  
 *   
 *  5724-H72,5655-R36,5655-L82,5724-L26, 
 *   
 *  (C) Copyright IBM Corp. 1997, 2012 All Rights Reserved.  
 *   
 *  US Government Users Restricted Rights - Use, duplication or  
 *  disclosure restricted by GSA ADP Schedule Contract with  
 *  IBM Corp.  
 *   </copyright> 
 */
/*
 This class is a basic extension of the Applet class.  It would generally be
 used as the main class with a Java browser or the AppletViewer.  But an instance
 can be added to a subclass of Container.  To use this applet with a browser or
 the AppletViewer, create an html file with the following code:

 <HTML>
 <HEAD>
 <TITLE>MQTest window</TITLE>
 </HEAD>
 <BODY>

 <APPLET CODE="MQTest.class" CODEBASE=".."
 ARCHIVE="%AN_APPROPRIATE_PATH%/com.ibm.mq.jar"
 WIDTH=360 HEIGHT=225 align=center>

 </BODY>

 </HTML>

 You can add controls to Simple with Cafe Studio.
 (Menus can be added only to subclasses of Frame.)


 Applet security is such that these additional permissions will be needed to
 allow the applet to function correctly:-

 permission java.util.PropertyPermission "user.dir", "read";
 permission java.util.PropertyPermission "com.ibm.mq.commonservices", "read";
 permission java.util.PropertyPermission "com.ibm.mq.commonservices.*", "read";
 permission java.lang.RuntimePermission "modifyThreadGroup";
 permission java.lang.RuntimePermission "modifyThread";
 permission java.util.PropertyPermission "java.library.path", "read";
 permission java.io.FilePermission "/opt/mqm/java/lib/libmqjbnd.so", "read";
 permission java.lang.RuntimePermission "loadLibrary.mqjbnd";
 permission java.io.FilePermission "/var/mqm/mqclient.ini", "read";

 */

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;

/**
 * Applet to demonstrate basic MQ functionality
 * <p>
 * <strong>Note</strong> While this can be made to work, it requires major compromises in the Java
 * security configuration to achieve this
 * <p>
 * It is probably better to avoid doing this if possible.
 */
public class MQTest extends java.applet.Applet implements ActionListener {

  /**
   * 
   */
  private static final long serialVersionUID = -752880773794963936L;

  static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/wmqjava/MQTest.java";

  // @COPYRIGHT_START@
  /** Comment for copyright_notice */
  static final String copyright_notice = "Licensed Materials - Property of IBM "
      + "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
      + "(c) Copyright IBM Corp. 1997, 2009 All Rights Reserved. "
      + "US Government Users Restricted Rights - Use, duplication or "
      + "disclosure restricted by GSA ADP Schedule Contract with " + "IBM Corp.";
  // @COPYRIGHT_END@

  private ResultsDisplay results;
  // this is so the appletviewer will only
  // call the function once.
  private boolean initialized = false;

  private MQSampleMessageManager messageMgr;

  /**
   * Initialisation method
   */
  public void init() {

    if (!initialized) {
      initialized = true;
      super.init();

      messageMgr = new MQSampleMessageManager("mqjcivp");

      if (!messageMgr.loadedOK()) {
        getAppletContext().showStatus("Unable to load message catalogue - mqjcivp.properties");
        System.out.println("Unable to load the message catalogue mqjcivp.properties");
        System.out.println("Please ensure that the samples directory is in your CLASSPATH");
        System.out.println("and try again.");
        setLayout(new BorderLayout());
        label1 = new Label("Could not load message catalogue.");
        label2 = new Label("Please ensure the samples directory is in");
        label3 = new Label("your CLASSPATH and retry.");
        Panel buttonPanel = new Panel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        closeBtn = new java.awt.Button("Close");
        buttonPanel.add(closeBtn);
        Panel msgPanel = new Panel();
        msgPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        msgPanel.add(label1);
        msgPanel.add(label2);
        msgPanel.add(label3);
        add(msgPanel, "Center");
        add(buttonPanel, "South");
        closeBtn.addActionListener(this);
        return;
      }

      // Pass the button names into the ResultsDisplay
      results = new ResultsDisplay(messageMgr.getMessage(3), messageMgr.getMessage(56));
      GridBagLayout gridbag = new GridBagLayout();
      setLayout(gridbag);

      // Create a panel for the buttons, and add the test and close
      // buttons
      // to it...
      Panel buttonPanel = new Panel();
      buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
      testConnectionBtn = new java.awt.Button(messageMgr.getMessage(2));
      buttonPanel.add(testConnectionBtn);
      closeBtn = new java.awt.Button(messageMgr.getMessage(3));
      buttonPanel.add(closeBtn);
      add(buttonPanel);

      // register for button presses
      testConnectionBtn.addActionListener(this);
      closeBtn.addActionListener(this);

      // Place a copyright notice at the very bottom...
      // copyright notice removed for now due to incorrect properties file
      // content
      // Label copyright = new Label(messageMgr.getMessage(1)); @P1C
      // add(copyright); @P1C
      // constrain(this,copyright,0,5,2,1,GridBagConstraints.NONE, @P1C
      // GridBagConstraints.WEST,0,0,0,0); @P1C

      // Position the panel at the bottom of the window...
      constrain(this, buttonPanel, 0, 4, 2, 1, GridBagConstraints.NONE, GridBagConstraints.CENTER);

      // Position the labels down the left hand side...
      label1 = new java.awt.Label(messageMgr.getMessage(4));
      add(label1);
      constrain(this, label1, 0, 0, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
      label2 = new java.awt.Label(messageMgr.getMessage(5));
      add(label2);
      constrain(this, label2, 0, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
      label3 = new java.awt.Label(messageMgr.getMessage(6));
      add(label3);
      constrain(this, label3, 0, 2, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
      label4 = new java.awt.Label(messageMgr.getMessage(7));
      add(label4);
      constrain(this, label4, 0, 3, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);

      // Position the text fields down the right hand side...
      hostnameText = new java.awt.TextField(22);
      add(hostnameText);
      constrain(this, hostnameText, 1, 0, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
      portText = new java.awt.TextField(5);
      add(portText);
      constrain(this, portText, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
      channelText = new java.awt.TextField(22);
      add(channelText);
      constrain(this, channelText, 1, 2, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
      queueMgrText = new java.awt.TextField(22);
      add(queueMgrText);
      constrain(this, queueMgrText, 1, 3, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);

      // Set the hostname
      String host = getParameter("hostname");
      if (host != null) {
        hostnameText.setText(host);
      }
      else {
        URL codebase = getCodeBase();
        if (codebase != null) {
          host = codebase.getHost();
          if ((host != null) && (!host.equals(""))) {
            hostnameText.setText(host);
          }
          else {
            hostnameText.setText("localhost");
          }
        }
      }

      // Set the port
      String portParam = getParameter("port");
      if (portParam != null) {
        portText.setText(portParam);
      }
      else {
        portText.setText("1414");
      }

      // Set the channel
      String channelParam = getParameter("channel");
      if (channelParam != null) {
        channelText.setText(channelParam);
      }

      // set the queue manager
      String qMgrParam = getParameter("queueManager");
      if (qMgrParam != null) {
        queueMgrText.setText(qMgrParam);
      }

      Dimension dim = getPreferredSize();
      int lineHeight = getFontMetrics(getFont()).getHeight();
      dim.height += (2 * lineHeight); // To cope with status line that
      // applet viewer overwrites
      // the copyright notice with otherwsie!!
      setSize(dim);
    } // endif for initalized
    return;
  }

  /**
   * Start method for the Applet
   */
  public void start() {
    // Needs to do nothing
    return;
  }

  private void constrain(Container container, Component component, int gridx, int gridy, int width,
      int height, int fill, int anchor) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = gridx;
    c.gridy = gridy;
    c.gridwidth = width;
    c.gridheight = height;
    c.fill = fill;
    c.anchor = anchor;
    c.insets = new Insets(5, 5, 5, 5);
    ((GridBagLayout) container.getLayout()).setConstraints(component, c);
    return;
  }

  private void simpleTest() {

    // Set up MQ environment
    MQEnvironment.hostname = hostnameText.getText();
    MQEnvironment.channel = channelText.getText();
    MQEnvironment.password = getParameter("password");
    MQEnvironment.userID = getParameter("userID");

    String strCCSID = getParameter("ccsid");
    if ((strCCSID != null) && !strCCSID.equals("")) {
      // Read the trace level
      try {
        MQEnvironment.CCSID = Integer.parseInt(strCCSID);
      }
      catch (NumberFormatException e) {
        // We'll silently ignore it in this demonstration program
      }
    }

    String trace = getParameter("trace");
    if ((trace != null) && !trace.equals("")) {
      // Read the trace level
      try {
        int level = Integer.parseInt(trace);
        MQEnvironment.enableTracing(level);
      }
      catch (NumberFormatException e) {
        results.addResult(messageMgr.getMessage(8));
      }
    }

    // Connect to MQ!!
    MQQueueManager queueManager;
    try {
      queueManager = new MQQueueManager(queueMgrText.getText());
    }
    catch (MQException ex) {
      results.addResult(messageMgr.getMessage(9));
      switch (ex.reasonCode) {
        case 2009 :
          results.addResult(messageMgr.getMessage(10));
          break;
        case 2059 :
          results.addResult(messageMgr.getMessage(11));
          break;
        case 2058 :
          results.addResult(messageMgr.getMessage(12));
          break;
        case 2063 :
          results.addResult(messageMgr.getMessage(13));
          break;
        default :
          results.addResult(messageMgr.getMessage(14, new Object[]{new Integer(ex.reasonCode)}));
      }
      return;
    }

    results.addResult(messageMgr.getMessage(15));

    // Open a queue...
    MQQueue system_default_local_queue;
    try {
      system_default_local_queue = queueManager.accessQueue("SYSTEM.DEFAULT.LOCAL.QUEUE",
          MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_OUTPUT, // input, output, inq, set
          null, null, null);
    }
    catch (MQException ex) {
      results.addResult(messageMgr.getMessage(16));
      switch (ex.reasonCode) {
        case 2009 :
          results.addResult(messageMgr.getMessage(10));
          break;
        case 2085 :
          results.addResult(messageMgr.getMessage(17));
          break;
        default :
          results.addResult(messageMgr.getMessage(14, new Object[]{new Integer(ex.reasonCode)}));
      }
      try {
        queueManager.disconnect();
      }
      catch (MQException e) {
        // We'll silently ignore it in this demonstration program
      }
      return;
    }

    results.addResult(messageMgr.getMessage(18));

    // Put a message!!!
    MQMessage hello_world = new MQMessage();
    hello_world.characterSet = 1208;
    try {
      hello_world.writeUTF("Hello World!");
    }
    catch (Exception ex) {
      results.addResult(messageMgr.getMessage(19));
    }
    try {
      system_default_local_queue.put(hello_world, new MQPutMessageOptions());
    }
    catch (MQException ex) {
      results.addResult(messageMgr.getMessage(20));
      switch (ex.reasonCode) {
        case 2009 :
          results.addResult(messageMgr.getMessage(10));
          break;
        case 2030 :
          results.addResult(messageMgr.getMessage(21));
          break;
        case 2053 :
          results.addResult(messageMgr.getMessage(22));
          break;
        default :
          results.addResult(messageMgr.getMessage(14, new Object[]{new Integer(ex.reasonCode)}));
      }
      try {
        system_default_local_queue.close();
      }
      catch (MQException e) {
        // We'll silently ignore it in this demonstration program
      }
      try {
        queueManager.disconnect();
      }
      catch (MQException e) {
        // We'll silently ignore it in this demonstration program
      }
      return;
    }

    results.addResult(messageMgr.getMessage(23));

    // Get a message!!!
    MQMessage hello_world_msg;
    try {
      hello_world_msg = new MQMessage();
      hello_world_msg.messageId = hello_world.messageId;
      hello_world_msg.characterSet = 1208;
      system_default_local_queue.get(hello_world_msg, new MQGetMessageOptions(), 30);
    }
    catch (MQException ex) {
      results.addResult(messageMgr.getMessage(24));
      switch (ex.reasonCode) {
        case 2009 :
          results.addResult(messageMgr.getMessage(10));
          break;
        case 2033 :
          results.addResult(messageMgr.getMessage(25));
          break;
        default :
          results.addResult(messageMgr.getMessage(14, new Object[]{new Integer(ex.reasonCode)}));
      }
      try {
        system_default_local_queue.close();
      }
      catch (MQException e) {
        // We'll silently ignore it in this demonstration program
      }
      try {
        queueManager.disconnect();
      }
      catch (MQException e) {
        // We'll silently ignore it in this demonstration program
      }
      return;
    }

    String msgReceived;
    try {
      msgReceived = hello_world_msg.readUTF();
    }
    catch (Exception io_ex) {
      results.addResult(messageMgr.getMessage(26));
      try {
        system_default_local_queue.close();
      }
      catch (MQException e) {
        // We'll silently ignore it in this demonstration program
      }
      try {
        queueManager.disconnect();
      }
      catch (MQException e) {
        // We'll silently ignore it in this demonstration program
      }
      return;
    }

    if (!msgReceived.equals("Hello World!")) {
      results.addResult(messageMgr.getMessage(26));
      try {
        system_default_local_queue.close();
      }
      catch (MQException e) {
        // We'll silently ignore it in this demonstration program
      }
      try {
        queueManager.disconnect();
      }
      catch (MQException e) {
        // We'll silently ignore it in this demonstration program
      }
      return;
    }

    results.addResult(messageMgr.getMessage(27));

    // Close it again...
    try {
      system_default_local_queue.close();
    }
    catch (MQException ex) {
      results.addResult(messageMgr.getMessage(28));
      switch (ex.reasonCode) {
        case 2009 :
          results.addResult(messageMgr.getMessage(10));
          break;
        default :
          results.addResult(messageMgr.getMessage(14, new Object[]{new Integer(ex.reasonCode)}));
      }
      try {
        queueManager.disconnect();
      }
      catch (MQException e) {
        // We'll silently ignore it in this demonstration program
      }
      return;
    }

    results.addResult(messageMgr.getMessage(29));

    // Disconnect from MQ!!
    try {
      queueManager.disconnect();
    }
    catch (MQException ex) {
      results.addResult(messageMgr.getMessage(30));
      switch (ex.reasonCode) {
        case 2009 :
          results.addResult(messageMgr.getMessage(10));
          break;
        default :
          results.addResult(messageMgr.getMessage(14, new Object[]{new Integer(ex.reasonCode)}));
      }
      return;
    }

    results.addResult(messageMgr.getMessage(31));

    results.addResult(messageMgr.getMessage(32));
    return;
  } // end of simple test...

  TextField hostnameText;
  TextField portText;
  TextField channelText;
  TextField queueMgrText;
  Label label1;
  Label label2;
  Label label3;
  Label label4;
  Button testConnectionBtn;
  Button closeBtn;

  void clickedTestConnection() {
    if (!results.isShowing()) {
      results.setVisible(true);
    }

    results.addResult(messageMgr.getMessage(44));

    // Get the port number
    try {
      MQEnvironment.port = Integer.parseInt(portText.getText());
    }
    catch (NumberFormatException e) {
      results.addResult(messageMgr.getMessage(33));
    }
    catch (NoClassDefFoundError e) {
      results.addResult(messageMgr.getMessage(34));
      return;
    }

    // Test that we can connect to the specified host
    if (hostnameText.getText().equals("")) {
      results.addResult(messageMgr.getMessage(36));
      return;
    }

    results.addResult(messageMgr.getMessage(37));
    try {
      Socket s = new Socket(hostnameText.getText(), MQEnvironment.port);
      s.close();
    }
    catch (SecurityException ex) {
      results.addResult(messageMgr.getMessage(38, new Object[]{hostnameText.getText()}));
      return;
    }
    catch (UnknownHostException ex) {
      results.addResult(messageMgr.getMessage(39, new Object[]{hostnameText.getText()}));
      return;
    }
    catch (IOException ex) {
      results.addResult(messageMgr.getMessage(40, new Object[]{hostnameText.getText(),
          hostnameText.getText(), new Integer(MQEnvironment.port)}));
      return;
    }

    results.addResult(messageMgr.getMessage(41, new Object[]{hostnameText.getText()}));

    // Host is accessible, let the MQ work begin...
    if (channelText.getText().equals("")) {
      results.addResult(messageMgr.getMessage(42));
      return;
    }

    results.addResult(messageMgr.getMessage(43));
    simpleTest();
    return;
  }

  /**
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent event) {
    if (event.getSource() == closeBtn) {
      clickedClose();
    }
    else

    if (event.getSource() == testConnectionBtn) {
      clickedTestConnection();
    }

    return;
  }

  void clickedClose() {
    setVisible(false);
    System.exit(0);
    return;
  }

  class ResultsDisplay extends Frame implements ActionListener, WindowListener {

    /**
     * 
     */
    private static final long serialVersionUID = 332588523916311398L;
    // instance variables.
    private String lineSeparator = "";
    private Button okButton;
    private Button clearButton;
    private TextArea resultsText;

    ResultsDisplay(String closeButtonText, String resetButtonText) {

      super(" ");

      setSize(400, 400);
      setLayout(new BorderLayout(5, 5));
      Panel p = new Panel();
      p.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
      okButton = new Button(closeButtonText);
      clearButton = new Button(" " + resetButtonText + " ");
      p.add(okButton);
      p.add(clearButton);
      add("South", p);
      resultsText = new TextArea();
      add("Center", resultsText);
      clearButton.addActionListener(this);
      okButton.addActionListener(this);
      addWindowListener(this);
      // get the line separator System property
      lineSeparator = System.getProperty("line.separator", "\n");
      return;
    }

    void addResult(String result) {
      resultsText.append(result);
      resultsText.append(lineSeparator);
      return;
    }

    /**
     * @see Component#setVisible(boolean)
     */
    public synchronized void setVisible(boolean b) {
      if (b) {
        setLocation(50, 50);
      }
      super.setVisible(b);
    }

    /**
     * @see ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {
      if (event.getSource() == okButton) {
        clickedOkButton();
      }
      else if (event.getSource() == clearButton) {
        clickedClearButton();
      }
      return;
    }

    void clickedOkButton() {
      // to do: put event handler code here.
      clickedClearButton();
      setVisible(false);
      return;
    }

    void clickedClearButton() {
      resultsText.setText("");
      return;
    }

    // Window Listener Methods...
    /**
     * @see WindowListener#windowClosing(WindowEvent)
     */
    public void windowClosing(WindowEvent e) {
      setVisible(false);
      return;
    }

    /**
     * @see WindowListener#windowClosed(WindowEvent)
     */
    public void windowClosed(WindowEvent e) {
      // Nothing to do
      return;
    }

    /**
     * @see WindowListener#windowOpened(WindowEvent)
     */
    public void windowOpened(WindowEvent e) {
      // Nothing to do
      return;
    }

    /**
     * @see WindowListener#windowIconified(WindowEvent)
     */
    public void windowIconified(WindowEvent e) {
      // Nothing to do
      return;
    }

    /**
     * @see WindowListener#windowDeiconified(WindowEvent)
     */
    public void windowDeiconified(WindowEvent e) {
      // Nothing to do
      return;
    }

    /**
     * @see WindowListener#windowActivated(WindowEvent)
     */
    public void windowActivated(WindowEvent e) {
      // Nothing to do
      return;
    }

    /**
     * @see WindowListener#windowDeactivated(WindowEvent)
     */
    public void windowDeactivated(WindowEvent e) {
      // Nothing to do
      return;
    }
  }

}
