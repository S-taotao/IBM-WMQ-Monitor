package graphics;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.util.concurrent.TimeUnit;
import graphics.ColorConst;

import javax.swing.JFrame;

import datacollect.GetCurrDepByPcf;

public class ShowDepth extends JFrame {
	
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 600;
	private int y=100;
	
	private GetCurrDepByPcf curDepPcf;	
	
	public ShowDepth(String host, int port, String queueName){
		curDepPcf= new GetCurrDepByPcf(host, port, queueName);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, WIDTH, HEIGHT);
		setTitle("Queue Depth Monitor");
		setVisible(true);		
	}
	
	public void paint(Graphics g2d) {
		System.out.println("Start painting.....");		
		g2d.clearRect(0, 0, WIDTH, HEIGHT);
		g2d.setColor(ColorConst.Pink);
		g2d.drawLine(100, 100, 200, y);	
		if(y<500){
			y+=200;
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			repaint();
		}else return;
	}
	
	public void dis(){		
		if(y<500){
			y+=200;
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			repaint();
		}else return;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new ShowDepth("22.188.59.35", 1411, "WCB.CONN");	
					//((ShowDepth) frame).dis();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
