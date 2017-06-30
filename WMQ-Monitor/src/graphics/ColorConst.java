package graphics;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;

public class ColorConst {
	public static final Color Black = new Color(0, 0, 0);
	public static final Color Blue = new Color(0, 0, 255);
	public static final Color Green = new Color(0, 255, 0);
	public static final Color Red = new Color(192, 80, 77);
	public static final Color Grey = new Color(205, 205, 205);
	public static final Color Violet = new Color(128, 100, 162);
	public static final Color Yellow = new Color(255, 192, 0);
	public static final Color Pink = new Color(255, 102, 204);

	public static LinkedList<Color> ColorQ = new LinkedList<Color>(
			Arrays.asList(Blue, Green, Red, Pink, Grey, Violet, Yellow));
	
	public static void main(String[] args){
		while(!ColorQ.isEmpty()){
			System.out.println(ColorQ.poll());
		}
	}

}
