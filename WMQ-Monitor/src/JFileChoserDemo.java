import java.io.File;

import javax.swing.JFileChooser;

public class JFileChoserDemo {

	public static void main(String[] args) {
		JFileChooser fd = new JFileChooser();
		fd.showOpenDialog(null);
		File f = fd.getSelectedFile();
		if(null!=f){System.out.println(f.getPath());}

	}

}
