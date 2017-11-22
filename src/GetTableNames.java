import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.inject.Inject;

import javax.websocket.Session;



import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;

import com.microsoft.sqlserver.jdbc.*;

public class GetTableNames {
	
	public static void getNames(Connection connection, String url, Session session) {
		try {
   
            	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(url);
                String schema = connection.getSchema();
                System.out.println("Successful connection - Schema: " + schema);

                String selectSql = "SELECT *  FROM dbo.Malarze";
                SessionHandler.sendToSession(session, selectSql);
                
                Statement statement = connection.createStatement();
                
            	ResultSet resultSet = statement.executeQuery(selectSql);
            	

            	
            	selectSql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_CATALOG='malarzeBaza'";

            	resultSet = statement.executeQuery(selectSql);
            	
            	while (resultSet.next())
                {
                    System.out.println(resultSet.getString(1));
                }
            	
	}
		 catch (Exception e) {
             e.printStackTrace();
             SessionHandler.sendToSession(session, "Lipa");
         }
		
	

}
}
