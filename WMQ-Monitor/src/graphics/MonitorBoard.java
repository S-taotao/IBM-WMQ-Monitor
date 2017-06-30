package graphics;

import java.awt.EventQueue;
import java.awt.TextArea;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextField;

import datacollect.IntelligentAgent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.ActionEvent;

public class MonitorBoard {

	private JFrame frmWelcomeToWmqmonitor;
	private JTextField textFiled_host;
	private JTextField textField_port;
	private JTextField textField_svrconchl;
	private JTextArea textArea;
	private JButton btnNewButton_1 = null;
	private JButton btnNewButton_3 = null;
	private Thread depthRecordThread = null;
	private Thread chlCntRecordThread = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MonitorBoard window = new MonitorBoard();
					window.frmWelcomeToWmqmonitor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MonitorBoard() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWelcomeToWmqmonitor = new JFrame();
		frmWelcomeToWmqmonitor.setTitle("Welcome to WMQ-Monitor");
		//frmWelcomeToWmqmonitor.setBounds(100, 100, 450, 300);
		frmWelcomeToWmqmonitor.setBounds(600, 350, 450, 300);
		frmWelcomeToWmqmonitor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmWelcomeToWmqmonitor.getContentPane().setLayout(null);
		
		textFiled_host = new JTextField();
		textFiled_host.setBounds(101, 27, 132, 21);
		frmWelcomeToWmqmonitor.getContentPane().add(textFiled_host);
		textFiled_host.setColumns(10);
		
		textField_port = new JTextField();
		textField_port.setBounds(101, 71, 132, 21);
		frmWelcomeToWmqmonitor.getContentPane().add(textField_port);
		textField_port.setColumns(10);
		
		textField_svrconchl = new JTextField();
		textField_svrconchl.setBounds(101, 116, 132, 21);
		frmWelcomeToWmqmonitor.getContentPane().add(textField_svrconchl);
		textField_svrconchl.setColumns(10);
		
		JButton btnNewButton = new JButton("QDepthMonitor");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//textArea.setText(textField.getText());				
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							List<String> qnames = new ArrayList<String>(Arrays.asList(textArea.getText().split("\n")));
							if(qnames.size()<1) return;
							else if(qnames.size()==1){
								JFrame frame = new DepthFrame(textFiled_host.getText(),
										Integer.valueOf(textField_port.getText()), textField_svrconchl.getText(), 1000, 2, qnames.get(0));
							}else{
								JFrame frame = new DepthsFrame(textFiled_host.getText(),
										Integer.valueOf(textField_port.getText()), textField_svrconchl.getText(), 1000, 2, qnames);
							}							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		btnNewButton.setBounds(261, 27, 150, 23);
		frmWelcomeToWmqmonitor.getContentPane().add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("Host");
		lblNewLabel.setBounds(23, 30, 54, 15);
		frmWelcomeToWmqmonitor.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Port");
		lblNewLabel_1.setBounds(23, 74, 54, 15);
		frmWelcomeToWmqmonitor.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("SvrConChl");
		lblNewLabel_2.setBounds(20, 119, 68, 15);
		frmWelcomeToWmqmonitor.getContentPane().add(lblNewLabel_2);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setText("Input queue names to be monitored here.");
		textArea.setBounds(23, 161, 210, 98);
		frmWelcomeToWmqmonitor.getContentPane().add(textArea);
		
		btnNewButton_1 = new JButton("Record Depths");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(null==depthRecordThread){
					depthRecordThread = new Thread(new Runnable(){
						public void run(){
							JFileChooser fd = new JFileChooser();
							fd.showOpenDialog(null);
							File f = fd.getSelectedFile();
							List<String> qnames = new ArrayList<String>(Arrays.asList(textArea.getText().split("\n")));
							//System.out.println(qnames);
							//for(String s: qnames) System.out.println("|"+s+"|");
							IntelligentAgent agent = new IntelligentAgent(textFiled_host.getText(),
									Integer.valueOf(textField_port.getText()), textField_svrconchl.getText());
							agent.recordDepths(f, 10000, qnames);
						}
					});
					depthRecordThread.start();
					if(null!=btnNewButton_1)
						btnNewButton_1.setText("StopDepthRecord");					
					//TODO 不然的话需要一些错误处理						
				} else {
					depthRecordThread.interrupt();
					depthRecordThread = null;
					btnNewButton_1.setText("Record Depths");
				}				
			}
		});
		btnNewButton_1.setBounds(261, 71, 150, 23);
		frmWelcomeToWmqmonitor.getContentPane().add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("ChlCountMonitor");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							JFrame frame = new ChlCntsFrame(textFiled_host.getText(),
										Integer.valueOf(textField_port.getText()), textField_svrconchl.getText(), 1000, 2);
														
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		btnNewButton_2.setBounds(261, 116, 150, 23);
		frmWelcomeToWmqmonitor.getContentPane().add(btnNewButton_2);
		
		btnNewButton_3 = new JButton("RecordChlCount");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*IntelligentAgent agent = new IntelligentAgent(textFiled_host.getText(),
						Integer.valueOf(textField_port.getText()), textField_svrconchl.getText());
				agent.recordChlCnt("E:\\ChlCounts.txt", 10000);*/
				if(null==chlCntRecordThread){
					chlCntRecordThread = new Thread(new Runnable(){
						public void run(){
							JFileChooser fd = new JFileChooser();
							fd.showOpenDialog(null);
							File f = fd.getSelectedFile();							
							IntelligentAgent agent = new IntelligentAgent(textFiled_host.getText(),
									Integer.valueOf(textField_port.getText()), textField_svrconchl.getText());
							agent.recordChlCnt(f, 10000);;
						}
					});
					chlCntRecordThread.start();
					if(null!=btnNewButton_3)
						btnNewButton_3.setText("StopChlCntRecord");					
					//TODO 不然的话需要一些错误处理						
				} else {
					chlCntRecordThread.interrupt();
					chlCntRecordThread = null;
					btnNewButton_3.setText("RecordChlCount");
				}
			}
		});
		btnNewButton_3.setBounds(261, 162, 150, 23);
		frmWelcomeToWmqmonitor.getContentPane().add(btnNewButton_3);
	}
}
