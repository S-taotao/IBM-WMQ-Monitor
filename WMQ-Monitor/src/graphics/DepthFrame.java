package graphics;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import datacollect.IntelligentAgent;

public class DepthFrame extends JFrame {
	
	private static final int WIDTH = 1000;        // Width of this Frame
	private static final int HEIGHT = 600;        // Height of this Frame
	private static final int TopMargin = 50;      // Top margin of this Frame
	private static final int RightMargin = 50;    // Right margin of this Frame
	private static final int OX=50;               // Origin Coordinate of X axis
	private static final int OY=550;              // Origin Coordinate of Y axis
	private final int dataLength = 150;           // Counts of the data points to be displayed in this Frame
	private final int totalcounts;                // Counts of data collection operation
	private final int interval;                   // Interval between two data collection operation
	private double MaxDepth=100;                  // Max value of the points to be displayed subsequently	
	private int count = 0;
	private String Q2bMonitd;
	private Queue<Integer> dataQ = new LinkedList<Integer>();   // Queue to store data collected
	
	private int preX=0, preY=0;
	
	private IntelligentAgent agent;	
	
	public DepthFrame(String host, int port, String svrConChl, int totalcounts, int interval, String Qname){
		agent = new IntelligentAgent(host, port, svrConChl);
		this.totalcounts = totalcounts;
		this.interval = interval;
		this.Q2bMonitd = Qname;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, WIDTH, HEIGHT);
		setTitle("Queue Depth Monitor");
		setVisible(true);
		
	}
	
	/*public void display(){
		for(int i = 0 ; i< totalcounts; i++){
			dataQ.offer(agent.CurrDepth("WCB.QUEUE"));    //Queue的插入、引出以及遍历的函数要再确认一下
			System.out.println(dataQ);
			if(dataQ.size()>dataLength) dataQ.poll();
			//调整纵坐标
			int max = Collections.max(dataQ);
			while (max > MaxDepth)	   MaxDepth = MaxDepth * 2;
			while (2 * max < MaxDepth) MaxDepth = MaxDepth / 2;
			System.out.println("Start to paint");
			this.repaint();
			try {
				//TODO Sleep的性能可能不好，后续在多线程时尽量优化
				TimeUnit.SECONDS.sleep(interval);         
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
	}*/
	
	public void paint(Graphics g2d) {
		System.out.println("Paint() is Running!");	
		//TODO There is Color class, not necessarily to define ColorConst class
		g2d.setColor(ColorConst.Black);			
		
		dataQ.offer(agent.CurrDepth(Q2bMonitd));    // put data into Queue
		System.out.println(dataQ);
		if(dataQ.size()>dataLength) dataQ.poll();   // keep Queue's length
		// Coordinate adjustment
		int max = Collections.max(dataQ);
		while (max > MaxDepth)	   MaxDepth = MaxDepth * 2;
		while (2 * max < MaxDepth) MaxDepth = MaxDepth / 2;
		System.out.println("Start to paint");
		g2d.clearRect(0, 0, WIDTH, HEIGHT);
		//  Draw labels of Y axis
		g2d.drawString(String.valueOf(MaxDepth), OX, TopMargin );    
		g2d.drawString(String.valueOf(MaxDepth/4), OX, TopMargin+(OY-TopMargin)*3/4 );
		g2d.drawString(String.valueOf(MaxDepth/2), OX, TopMargin+(OY-TopMargin)/2 );
		g2d.drawString(String.valueOf(MaxDepth*3/4), OX, TopMargin+(OY-TopMargin)*1/4 );
		// Draw labels of X axis(ie, Time axis)
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
		g2d.drawString(dateFormat.format(new Date()), 900, 580);
		g2d.drawString(dateFormat.format(new Date(System.currentTimeMillis() - 150000)), 450, 580);
		
		g2d.drawLine(OX, OY, OX, TopMargin);                  // Draw Y axis
		g2d.drawLine(OX, OY, WIDTH-RightMargin, OY);          // Draw X axis
		
		// Draw grid line
		g2d.setColor(ColorConst.Grey);
		g2d.drawLine(OX, TopMargin+(OY-TopMargin)*3/4, WIDTH-RightMargin, TopMargin+(OY-TopMargin)*3/4);
		g2d.drawLine(OX, TopMargin+(OY-TopMargin)/2, WIDTH-RightMargin, TopMargin+(OY-TopMargin)/2);
		g2d.drawLine(OX, TopMargin+(OY-TopMargin)*1/4, WIDTH-RightMargin, TopMargin+(OY-TopMargin)*1/4);
		
		g2d.setColor(ColorConst.Blue);
		// Draw label of Queue name
		// TODO Change 3, 70 into variable or final variable
		g2d.drawString(Q2bMonitd, WIDTH - 3 * RightMargin, 70);
		
		
		if(dataQ.isEmpty()) return;
		int x_interval = (WIDTH-OX-RightMargin)/dataLength;
		System.out.println("DrawPoints here, x_interval is:" + x_interval);
		
		double y_interval = (OY - TopMargin)/MaxDepth;
		System.out.println("DrawPoints here, y_interval is:" + y_interval);
		int x = OX, y;		
		for(int i : dataQ){
			y = (int) (OY-i*y_interval);
			g2d.fillOval(x, y, 3, 3);
			if(0!=preX){
				g2d.drawLine(preX, preY, x, y);				
			}
			preX=x;
			x +=x_interval;
			preY=y;			
		}
		preX=preY=0;
		
		
		try {			
			TimeUnit.SECONDS.sleep(interval);         
		} catch (InterruptedException e) {				
			//e.printStackTrace();
			System.out.println(this.toString()+" is stoped!");					
			return;
		}
		// Set loops/recursion with repaint() function
		if (count < totalcounts) {
			count++;
			repaint();
		} else
			return;
	}
	
	/*private void drawPoints(Graphics g2d){
		System.out.println("Start!!");
		if(dataQ.isEmpty()) return;
		int x_interval = (WIDTH-OX-RightMargin)/dataLength;
		System.out.println("DrawPoints here, x_interval is:" + x_interval);
		int y_interval = (OY - TopMargin)/MaxDepth;
		System.out.println("DrawPoints here, y_interval is:" + y_interval);
		int x = OX, y;		
		for(int i : dataQ){
			y = OY-i*y_interval;
			g2d.drawOval(x, y, 3, 3);
			x +=x_interval;			
		}
	}
	
	private void drawLines(Graphics g2d){
		if(dataQ.isEmpty()) return;
		int x_interval = (WIDTH-OX-RightMargin)/dataLength;
		int y_interval = (OY - TopMargin)/MaxDepth;
		int x = OX, y;
		for(int i : dataQ){
			y=OY-i*y_interval;
			if(0!=preX){
				g2d.drawLine(preX, preY, x, y);				
			}
			preX=x;
			x +=x_interval;
			preY=y;
		}
		preX=preY=0;		
	}*/

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new DepthFrame("22.188.59.35",1411,"WCB.CONN", 1000,2,"WCB.QUEUE");
					//((DepthFrame) frame).display();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
	}

}
