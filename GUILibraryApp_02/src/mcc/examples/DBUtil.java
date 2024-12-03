package mcc.examples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
	
	private Connection connection = null;

	public Connection getConnection() {
		if(connection == null) {
			connection = this.connectToDB();
		}
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private Connection connectToDB() {
		Connection conn = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
			// Connect to the database
			String connectionString = "jdbc:sqlite:/home/clay/apps/sqlite/library_sqlite_app";
			conn = DriverManager.getConnection(connectionString);
			if(conn != null && !conn.isClosed()) {
				System.out.println("Connected to Database");
			}else {
				System.out.println("NOT Connected to Database");
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return conn;
	}

}
