import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
	public static Connection connection;
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		// connection = DriverManager.getConnection("jdbc:mysql://10.96.122.161:3306/shooting", "shooting", "0000"); // 원격 접속 용
		connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/shooting", "shooting", "0000"); // 테스트 용
		
		game_Frame fms = new game_Frame();
	}

}