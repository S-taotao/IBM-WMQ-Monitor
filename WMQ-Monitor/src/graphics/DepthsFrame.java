package graphics;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import datacollect.IntelligentAgent;

public class DepthsFrame extends JFrame {
		
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
	private List<String> Qs;
	// dataQs is a map stored queue names and the data queues correspond to the queue names
	private Map<String, List<Integer>> dataQs = new HashMap<String, List<Integer>>();   
	// Mapping the different color to specific queue name
	private Map<String, Color> colorMap = new HashMap<String, Color>();
	
	
	private int preX=0, preY=0;
	
	private IntelligentAgent agent;	
	
	public DepthsFrame(String host, int port, String svrConChl, int totalcounts, int interval, List<String> Qnames){
		agent = new IntelligentAgent(host, port, svrConChl);
		this.totalcounts = totalcounts;
		this.interval = interval;
		this.Qs = Qnames;
		for(String qname: Qnames){
			dataQs.put(qname, new LinkedList<Integer>());
			colorMap.put(qname, ColorConst.ColorQ.poll());
		}
		
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(200, 200, WIDTH, HEIGHT);
		setTitle("Queues' Depth Monitor");
		setVisible(true);
		
	}
	
		
	public void paint(Graphics g2d) {
		System.out.println("Paint() is Running!");		
					
		Map<String, Integer> result = agent.CurrDepths(Qs);
		int max = 0;
		for(String s:dataQs.keySet()){
			((LinkedList<Integer>) dataQs.get(s)).offer(result.get(s));
			if(dataQs.get(s).size()>dataLength) ((LinkedList<Integer>) dataQs.get(s)).poll();
			int t_max = Collections.max((LinkedList<Integer>) dataQs.get(s));
			max = t_max > max ? t_max : max;
		}		
		System.out.println(dataQs);
		
		// Coordinate adjustment				
		while (max > MaxDepth)	   MaxDepth = MaxDepth * 2;
		while (2 * max < MaxDepth) MaxDepth = MaxDepth / 2;
		System.out.println("Start to paint");
		// The procedure of data collection if not fast enough, so we have to clear Frame after queues' depth query
		g2d.clearRect(0, 0, WIDTH, HEIGHT);                             
		g2d.setColor(ColorConst.Black);	
		// Draw labels of Y axis
		g2d.drawString(String.valueOf(MaxDepth), OX, TopMargin ); 
		g2d.drawString(String.valueOf(MaxDepth/4), OX, TopMargin+(OY-TopMargin)*3/4 );
		g2d.drawString(String.valueOf(MaxDepth/2), OX, TopMargin+(OY-TopMargin)/2 );
		g2d.drawString(String.valueOf(MaxDepth*3/4), OX, TopMargin+(OY-TopMargin)*1/4 );
		// Draw labels of X axis(ie, Time axis)
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
		g2d.drawString(dateFormat.format(new Date()), 900, 580);
		g2d.drawString(dateFormat.format(new Date(System.currentTimeMillis()-150000)), 450, 580);
		// Draw label of Queue names
		// TODO Change 3, 20 and 50 into variable or final variable
		int yoff=1;
		for(String s : Qs){
			g2d.setColor(colorMap.get(s));
			g2d.drawString(s, WIDTH-3*RightMargin, 50+20*yoff++);
		}
		g2d.setColor(ColorConst.Black);
		g2d.drawLine(OX, OY, OX, TopMargin);                  // Draw Y axis
		g2d.drawLine(OX, OY, WIDTH-RightMargin, OY);          // Draw X axis
		// Draw grid line
		g2d.setColor(ColorConst.Grey);
		g2d.drawLine(OX, TopMargin+(OY-TopMargin)*3/4, WIDTH-RightMargin, TopMargin+(OY-TopMargin)*3/4);
		g2d.drawLine(OX, TopMargin+(OY-TopMargin)/2, WIDTH-RightMargin, TopMargin+(OY-TopMargin)/2);
		g2d.drawLine(OX, TopMargin+(OY-TopMargin)*1/4, WIDTH-RightMargin, TopMargin+(OY-TopMargin)*1/4);
		
		if(dataQs.get(Qs.get(0)).isEmpty()) return;
		int x_interval = (WIDTH-OX-RightMargin)/dataLength;
		System.out.println("DrawPoints here, x_interval is:" + x_interval);		
		double y_interval = (OY - TopMargin)/MaxDepth;
		System.out.println("DrawPoints here, y_interval is:" + y_interval);
		
		for(String s: dataQs.keySet()){
			g2d.setColor(colorMap.get(s));
			int x = OX, y;
			for(int i : dataQs.get(s)){
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
		}		
				
		try {			
			TimeUnit.SECONDS.sleep(interval);         
		} catch (InterruptedException e) {				
			//e.printStackTrace();
			System.out.println(this.toString()+" is stoped!");	
			this.dispose();
			return;
		}
		// Set loops/recursion with repaint() function
		if (count < totalcounts) {
			count++;
			repaint();
		} else
			return;
	}
	

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new DepthsFrame("22.188.59.35",1411,"WCB.CONN", 1000,2,Arrays.asList("WCB.QUEUE","QL1","QL2", "QL3"));
					//((DepthFrame) frame).display();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
	}

}
