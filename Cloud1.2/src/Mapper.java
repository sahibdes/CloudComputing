import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Mapper {
	public static void main(String[] args) {
		try {
			BufferedReader bfr = new BufferedReader(new InputStreamReader(
					System.in));
			String str = null;
			String fileName = System.getenv("mapreduce_map_input_file");
			while ((str = bfr.readLine()) != null) {
				if (str.startsWith("en ")) {
					String[] s;
					s = str.split(" ");
					if (s.length == 4) {
						if (!s[1].startsWith("Media:")
								&& !s[1].startsWith("Special:")
								&& !s[1].startsWith("Talk:")
								&& !s[1].startsWith("User:")
								&& !s[1].startsWith("User_talk:")
								&& !s[1].startsWith("Project:")
								&& !s[1].startsWith("Project_talk:")
								&& !s[1].startsWith("File:")
								&& !s[1].startsWith("File_talk:")
								&& !s[1].startsWith("MediaWiki:")
								&& !s[1].startsWith("MediaWiki_talk:")
								&& !s[1].startsWith("Template:")
								&& !s[1].startsWith("Template_talk:")
								&& !s[1].startsWith("Help:")
								&& !s[1].startsWith("Help_talk:")
								&& !s[1].startsWith("Category:")
								&& !s[1].startsWith("Category_talk:")
								&& !s[1].startsWith("Portal:")
								&& !s[1].startsWith("Wikipedia:")
								&& !s[1].startsWith("Wikipedia_talk:")) {
							if ((!(s[1].matches("^[a-z](.*)")))) {
								if (!s[1].endsWith(".jpg")
										&& !s[1].endsWith(".gif")
										&& !s[1].endsWith(".png")
										&& !s[1].endsWith(".JPG")
										&& !s[1].endsWith(".GIF")
										&& !s[1].endsWith(".PNG")
										&& !s[1].endsWith(".txt")
										&& !s[1].endsWith(".ico")) {
									if (!s[1].equals("404_error/")
											&& !s[1].equals("Main_Page")
											&& !s[1].equals("Hypertext_Transfer_Protocol")
											&& !s[1].equals("Search")) {
										int i = Integer.valueOf(s[2]);
										// if valid add to hash map
										System.out.println(s[1] + '\t' + i
												+ '\t' + fileName);
									}
								}
							}
						}
					}
				}
			}
			bfr.close();
			str = null;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}