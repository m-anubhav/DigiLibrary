import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectToDB {
    private static final String url = "jdbc:mysql://localhost:3306/librarymanagement", username = "root", password = "";
    static Connection getConnectToDB() throws SQLException{

        return DriverManager.getConnection(url,username,password);
    }
}
