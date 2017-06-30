package samples;
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="2632876237" > 
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

import com.ibm.mq.MQMessage;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.MQCFH;
import com.ibm.mq.headers.pcf.MQCFIL;
import com.ibm.mq.headers.pcf.PCFParameter;

/**
 * <u>How to use this sample</u><br>
 * The sample demonstrates how PCF commands can be used to display queue manager information.
 * <p>
 * The sample can bind to a local queue manager using local bindings by using the following
 * parameters:-<br>
 * PCF_WalkThroughQueueManagerAttributes QueueManager<br>
 * <br>
 * e.g. PCF_WalkThroughQueueManagerAttributes QM1<br>
 * <br>
 * Or the sample can bind to a remote queue manager using client bindings by using the following
 * parameters:-<br>
 * PCF_WalkThroughQueueManagerAttributes QueueManager Host Port<br>
 * <br>
 * e.g. PCF_WalkThroughQueueManagerAttributes QM1 localhost 1414<br>
 * <br>
 * <b> N.B. When binding to another machine it is assumed that any intervening firewall has been
 * prepared to allow this connection.</b><br>
 * <br>
 * A typical output when running this sample would be:-<br>
 * <br>
 * <code><font face="courier, monospaced">
 * Queue manager attributes:<br>
 * +--------------------------------+----------------------------------------------------------------+<br>
 * |Attribute Name&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; |&nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Value&nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;|<br>
 * +--------------------------------+----------------------------------------------------------------+<br>
 * |MQCA_Q_MGR_NAME&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;|REMOTE&nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; |<br>
 * |MQIA_ACCOUNTING_CONN_OVERRIDE&nbsp; &nbsp;|0&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;|<br>
 * |MQIA_ACCOUNTING_INTERVAL&nbsp; &nbsp; &nbsp; &nbsp; |1800&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; |<br>
 * |: &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
 * &nbsp;  |:&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;|<br>
 * |MQIA_START_STOP_EVENT &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; |1&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;|<br>
 * |MQIA_SYNCPOINT&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; |1&nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
 * &nbsp;|<br>
 * |MQIA_TRIGGER_INTERVAL &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  |999999999&nbsp; &nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;|<br>
 * +--------------------------------+----------------------------------------------------------------+<br></font></code>
 */

public class PCF_WalkThroughQueueManagerAttributes {

  // @COPYRIGHT_START@
  /** Comment for copyright_notice */
  static final String copyright_notice = "Licensed Materials - Property of IBM "
      + "5724-H72, 5655-R36, 5724-L26, 5655-L82                "
      + "(c) Copyright IBM Corp. 2008, 2009 All Rights Reserved. "
      + "US Government Users Restricted Rights - Use, duplication or "
      + "disclosure restricted by GSA ADP Schedule Contract with " + "IBM Corp.";
  // @COPYRIGHT_END@

  /** The SCCSID which is expanded when the file is extracted from CMVC */
  public static final String sccsid = "@(#) MQMBID sn=p750-002-130627 su=_BJRU0N9vEeK1oKoKL_dPJA pn=MQJavaSamples/pcf/PCF_WalkThroughQueueManagerAttributes.java"; //$NON-NLS-1$

  /**
   * PCF sample entry function. When calling this sample, use either of the following formats:-
   * <p>
   * <table border="1">
   * <tr>
   * <td>Format</td>
   * <td>Example</td>
   * <td>Information</td>
   * </tr>
   * <tr>
   * <td>PCF_Sample QueueManager</td>
   * <td>PCF_Sample QM</td>
   * <td>Use this prototype when connecting to a local queue manager.</td>
   * </tr>
   * <tr>
   * <td>PCF_Sample QueueManager Host Port</td>
   * <td>PCF_Sample QM localhost 1414</td>
   * <td>Use this prototype when connecting to a queue manager using client bindings.</td>
   * </tr>
   * </table>
   * 
   * @param args Input parameters.
   */
  public static void main(String[] args) {
    PCF_CommonMethods pcfCM = new PCF_CommonMethods();

    try {
      if (pcfCM.ParseParameters(args)) {
        pcfCM.CreateAgent(args.length);

        WalkThroughQueueManagerAttributes(pcfCM);

        pcfCM.DestroyAgent();
      }
    }
    catch (Exception e) {
      pcfCM.DisplayException(e);
    }
    return;
  }

  /**
   * WalkThroughQueueManagerAttributes uses the PCF command 'MQIACF_Q_MGR_ATTRS' to gather
   * information about the given Queue Manager. The information is displayed in a tabular form on
   * the console. The creation of the PCF parameters have been constructed differently for this
   * sample to show an alternative way of creating the PCF command and message objects.<br>
   * For more information on the Inquire Queue command, please read the "Programmable Command
   * Formats and Administration Interface" section within the Websphere MQ documentation.
   * 
   * @param pcfCM Object used to hold common objects used by the PCF samples.
   * @throws IOException
   * @throws MQDataException
   */
  public static void WalkThroughQueueManagerAttributes(PCF_CommonMethods pcfCM)
      throws MQDataException, IOException {
    int[] pcfParmAttrs = {MQConstants.MQIACF_ALL};
    PCFParameter[] pcfParameters = {new MQCFIL(MQConstants.MQIACF_Q_MGR_ATTRS, pcfParmAttrs)};
    MQMessage[] mqResponse = pcfCM.agent.send(MQConstants.MQCMD_INQUIRE_Q_MGR, pcfParameters);
    MQCFH mqCFH = new MQCFH(mqResponse[0]);
    PCFParameter pcfParam;

    if (mqCFH.getReason() == 0) {
      System.out.println("Queue manager attributes:");
      System.out
          .println("+--------------------------------+----------------------------------------------------------------+");
      System.out
          .println("|Attribute Name                  |                            Value                               |");
      System.out
          .println("+--------------------------------+----------------------------------------------------------------+");

      for (int index = 0; index < mqCFH.getParameterCount(); index++) {
        // Walk through the returned attributes.
        pcfParam = PCFParameter.nextParameter(mqResponse[0]);

        System.out.println("|"
            + (pcfParam.getParameterName() + pcfCM.padding).substring(0, 32)
            + "|"
            + (pcfParam.getValue().toString().length() < 64 ? (pcfParam.getValue() + pcfCM.padding)
                .substring(0, 64) : (pcfParam.getValue() + pcfCM.padding).substring(0, 61) + "...")
            + "|");
      }

      System.out
          .println("+--------------------------------+----------------------------------------------------------------+");
    }
    else {
      System.out.println(" PCF error:\n" + mqCFH);

      // Walk through the returned parameters describing the error.
      for (int index = 0; index < mqCFH.getParameterCount(); index++) {
        System.out.println(PCFParameter.nextParameter(mqResponse[0]));
      }
    }
    return;
  }
}
