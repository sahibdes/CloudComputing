// Referenced from : http://www.javatips.net/blog/2013/12/c3p0-connection-pooling-example?page=1
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DataConnector {
	private static DataConnector dataconn;
	private ComboPooledDataSource comboPool;

	private DataConnector() throws IOException, SQLException,
			PropertyVetoException {
		comboPool = new ComboPooledDataSource();
		comboPool.setDriverClass("com.mysql.jdbc.Driver");
		comboPool.setJdbcUrl("jdbc:mysql://localhost:3306/hashtagdb");
		comboPool.setUser("user");
		comboPool.setPassword("password");

		comboPool.setMinPoolSize(5);
		comboPool.setAcquireIncrement(5);
		comboPool.setMaxPoolSize(20);
		comboPool.setMaxStatements(180);
	}

	public static DataConnector getInstance() throws IOException, SQLException,
			PropertyVetoException {
		if (dataconn == null) {
			dataconn = new DataConnector();
			return dataconn;
		} else {
			return dataconn;
		}
	}

	public Connection getConnection() throws SQLException {
		return this.comboPool.getConnection();
	}

}