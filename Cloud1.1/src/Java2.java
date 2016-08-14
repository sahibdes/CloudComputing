import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Java2 {

	public static void main(String[] args) throws IOException {
		File f = new File("/Users/Sahib/Documents/workspace/Cloud1.1/output");
		RandomAccessFile rfile = new RandomAccessFile(f, "r");
		rfile.seek(f.length());
		
		
	}
}
