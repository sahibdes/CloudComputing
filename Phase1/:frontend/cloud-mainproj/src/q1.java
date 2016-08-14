import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * Servlet implementation class q1
 */

@WebServlet("/q1")
public class q1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * Default constructor.
	 */

	public q1() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		response.setContentType("text/plain");
		PrintWriter p = response.getWriter();
		String key = request.getParameter("key");
		String message = request.getParameter("message");
		BigInteger X = new BigInteger(

				"8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773");

		// GET XY FROM LOAD GENERATOR.

		String spiralized_cipher = spiralize(message);
		BigInteger keyInt = new BigInteger(key);
		BigInteger Y = keyInt.divide(X);
		// PrintWriter print = response.getWriter();

		BigInteger twentyFive = new BigInteger("25");
		BigInteger one = new BigInteger("1");
		BigInteger Z = one.add((Y.mod(twentyFive)));
		String teamID = "MadHatters";
		String awsID1 = "1166-8424-3822";
		String awsID2 = "2404-3085-8626";
		String awsID3 = "8246-0444-6166";

		java.util.Date dateTime = new java.util.Date();
		String messageVal = caesarify(Z, spiralized_cipher);
		StringBuilder sb = new StringBuilder();

		sb.append(teamID)
				.append(",")
				.append(awsID1)
				.append(",")
				.append(awsID2)
				.append(",")
				.append(awsID3)
				.append("\n")
				.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new java.util.Date())).append("\n")
				.append(messageVal).append("\n");

		p.println("MadHatters,1166-8424-3822,2404-3085-8626,8246-0444-6166");
		p.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new java.util.Date()));
		p.println(messageVal);

		// print.println(sb.toString());

	}

	public static String caesarify(BigInteger Z, String spiralized_cipher) {
		String s = spiralized_cipher;
		int k = Z.intValue();
		StringBuffer decoded = new StringBuffer();

		for (int i = 0; i < s.length(); i++) {
			int x = ((s.charAt(i))) - k;
			if (x < 65) {
				x += 26;
			}
			decoded.append((char) (x));
		}
		return decoded.toString();
	}

	public static String spiralize(String s) {
		int len = s.length();
		int m = (int) Math.sqrt(len);
		int n = (int) Math.sqrt(len);

		StringBuffer Cipher_2D = new StringBuffer();
		char a[][] = new char[m][n];
		int index = 0;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				a[i][j] = s.charAt(index);
				index++;
			}
		}

		int top = 0, bottom = m - 1, left = 0, right = n - 1;
		int direction = 0;

		while (top <= bottom && left <= right) {
			if (direction == 0) {
				for (int i = left; i <= right; i++) {

					Cipher_2D.append(a[top][i]);
				}
				top++;
			} else if (direction == 1) {
				for (int i = top; i <= bottom; i++) {

					Cipher_2D.append(a[i][right]);
				}
				right--;
			}

			else if (direction == 2) {
				for (int i = right; i >= left; i--) {

					Cipher_2D.append(a[bottom][i]);
				}
				bottom--;
			} else if (direction == 3) {
				for (int i = bottom; i >= top; i--) {

					Cipher_2D.append(a[i][left]);
				}

				left++;
				direction = 3;
			}
			direction = (direction + 1) % 4;
		}
		return (Cipher_2D.toString());
	}

	/**
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

}