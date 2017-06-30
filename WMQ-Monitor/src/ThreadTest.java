
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ThreadTest implements Runnable{

	@Override
	public void run() {
		for ( int i = 0 ; i<5; i++){
			Date now = new Date();
			Date ns = new Date(System.currentTimeMillis()-5000);
			System.out.println("Time is: " + new SimpleDateFormat("hh:mm:ss").format(now)+", and Current count is:" + i);
			System.out.println("Five seconds before is: " + ns+", and Current count is:" + i);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				System.out.println(this.toString()+" is stoped!");
				return;
			}
			Thread.yield();
		}
			
		
		
	}
	
	public static void main (String [] args) {
		/*Thread a = new Thread(new ThreadTest());
		a.start();
		Scanner sc = new Scanner(System.in);
		while(sc.hasNextLine())
			if (sc.nextLine()=="y"); a.interrupt();*/
		Thread a = new Thread(new ThreadTest());
		a.start();
	}

}
