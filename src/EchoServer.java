import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.inject.Inject;
import javax.websocket.EncodeException;
 
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.DriverManager;

import com.google.gson.Gson;
import com.microsoft.sqlserver.jdbc.*;
import org.json.*;




 

/**
 * @ServerEndpoint gives the relative name for the end point
 * This will be accessed via ws://localhost:8080/EchoChamber/echo
 * Where "localhost" is the address of the host,
 * "EchoChamber" is the name of the package
 * and "echo" is the address to access this class from the server
 */
@ServerEndpoint(value = "/echo/{roomnumber}")
public class EchoServer {


	
    String hostName = "malarzeserwer.database.windows.net";
    String dbName = "malarzeBaza";
    String user = "maras314";
    String password = "malarze314Y";
    String url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);
    Connection connection = null;



    /**
     * @throws EncodeException 
     * @throws IOException 
     * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     * In the method onOpen, we'll let the user know that the handshake was
     * successful.
     */    
 
   
    @OnOpen
    public void onOpen(Session session, @PathParam("roomnumber") String roomnumber) throws IOException, EncodeException{
        System.out.println(session.getId() + " has opened a connection");
        System.out.println("ok1");    
        session.getUserProperties().put("roomnumber", roomnumber);
        System.out.println("ok2");    
           // SessionHandler.openSessions.put(String.valueOf(session.getId()), session);
            System.out.println("ok3");
            SessionHandler.addSession(session);

            System.out.println("ok4");
            
            //session.getBasicRemote().sendObject("szczeka");
            System.out.println("ok5");
            
            //GetTableNames.getNames(connection, url, session);

            


            
            
    }
 
    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String.
     * @throws JSONException 
     * @throws InterruptedException 
     */
    @OnMessage
    public void onMessage(String message, Session session) throws JSONException, InterruptedException{
    	
    	JSONObject jsonObj = new JSONObject(message);
    	System.out.println("ODEBRANE SPRAWDZAM");
    	System.out.println(message);
    	System.out.println("SPRAWDZAM JSON");
    	System.out.println(jsonObj);
    	String proszedzialaj = jsonObj.getString("dzialanie");

        String room = (String) session.getUserProperties().get("roomnumber");
        try{
            for (Session s : session.getOpenSessions()){
                if (s.isOpen() && s.getUserProperties().get("roomnumber").equals(room)){
                    //s.getBasicRemote().sendObject(message);
                    //SessionHandler.sendToallConnectedSessionsInRoom(room, message);

                    
                    if(proszedzialaj.equals("Pobierz")) {
                    //if(message.equals("Pobierz")) {
                    //if(jsonObj.getJSONObject("dzialanie").equals("Pobierz")) {
                    	////////////////////////////////////////////////////////////////////
                		try {
                			   
                        	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                            connection = DriverManager.getConnection(url);
                            String schema = connection.getSchema();
                            System.out.println("Successful connection - Schema: " + schema);

                            String selectSql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_CATALOG='malarzeBaza'";
                            //wypisanie komendy sql do okna przegladarki
                            //SessionHandler.sendToSession(session, selectSql);
                            
                            Statement statement = connection.createStatement();
                            
                        	ResultSet resultSet = statement.executeQuery(selectSql);
                        	
                        	
                        	                       	
                        	//while (resultSet.next())
                            //{
                                //System.out.println(resultSet.getString(1));
                                
                                //System.out.println(split.length);
                                
                                
                                //SessionHandler.sendToallConnectedSessionsInRoom(room, split[1]);
                                //SessionHandler.sendToallConnectedSessionsInRoom(room, resultSet.getString(1));


                            //}
                            //String split[] = resultSet.getString(1);
                        	
                        	JSONObject cos = new JSONObject();
                        	cos.put("dzialanie", "tabele");
                        	String licznik;
                        	
                            int i = 0;
                            String arr[] = new String[50];
                            while (resultSet.next()) {
                                String em = resultSet.getString(1);
                                arr[i] = em;
                                licznik= "tabela" + String.valueOf(i);
                                //System.out.println(arr);
                            	//System.out.println(em);
                            	cos.put(licznik, em);
                                i++;
                                
                            }
                            
                            System.out.println(cos.toString());
                            // 0 - malarze , 1 - obrazy
                            
                            
                            
                            //String tab[] = arr;
                            
                            //SessionHandler.sendToallConnectedSessionsInRoom(room, cos.toString());
                            if(s == session) {
                            	SessionHandler.sendToSession(s,cos.toString());
                            }
                            
                        	
                        	
                			}
            		 catch (Exception e) {
                         e.printStackTrace();
                         SessionHandler.sendToSession(session, "Lipa");
                     }
                    	///////////////////////////////////////////////////////////////////
                    	
                    	//SessionHandler.sendToallConnectedSessionsInRoom(room, message);
                    }
                    if(proszedzialaj.equals("Tabela")) {
                    	try {
             			   
                        	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                            connection = DriverManager.getConnection(url);
                            String schema = connection.getSchema();
                            System.out.println("Successful connection - Schema: " + schema);
                            String wybranaTabela = jsonObj.getString("tabela");

                            String selectSql = "SELECT * FROM dbo." + wybranaTabela;
                            //wypisanie komendy sql do okna przegladarki
                            //SessionHandler.sendToSession(session, selectSql);
                            
                            Statement statement = connection.createStatement();
                            
                        	ResultSet resultSet = statement.executeQuery(selectSql);
                        	ResultSetMetaData rsmd = resultSet.getMetaData();

                        	int columnsNumber = rsmd.getColumnCount();
                        	String columnName[]=new String[10];
                        	for(int k=1;k<columnsNumber+1;k++) {
                        		columnName[k] = rsmd.getColumnName(k);
                        	}
                        	
                        	
                        	System.out.println(columnsNumber);
                        	                       	
                        	//while (resultSet.next())
                            //{
                                //System.out.println(resultSet.getString(1));
                                
                                //System.out.println(split.length);
                                
                                
                                //SessionHandler.sendToallConnectedSessionsInRoom(room, split[1]);
                                //SessionHandler.sendToallConnectedSessionsInRoom(room, resultSet.getString(1));


                            //}
                            //String split[] = resultSet.getString(1);
                        	
                        	
                        	JSONArray tablicaCos = new JSONArray();
                        	JSONArray tablicaCos2 = new JSONArray();
                        	JSONObject mainObj = new JSONObject();
                        	//cos.put("dzialanie", "wysylamTabele");
                        	String licznik = " ";
                        	String test[] = new String[500];
                        	
                            //int i = 0;
                        	int i = 1;
                            int j = 1;
                            
                            String arr[][] = new String[100][100];
                            String arr1[] = new String[100];
                            ArrayList<Map<String, String>> lista = new ArrayList<Map<String, String>>();

                            JSONObject cos2 = new JSONObject();
                            while (resultSet.next()) {
                            	//System.out.println(resultSet.getString(1));
                            	
                            	Map<String, String> obj = new LinkedHashMap<String, String>();
                            	JSONObject cos = new JSONObject();
                            	


                            	for(j=1;j<columnsNumber+1;j++) {  
                                	//cos = new JSONObject();
                                	 //cos2 = new JSONObject();


                            		//arr[i][j] = resultSet.getString(j);
                            		arr1[j] = resultSet.getString(j);
                            		licznik= columnName[j];
                            		//System.out.println(licznik);
                            		//cos.put(licznik, arr[i][j]);
                            		//cos.put(licznik, arr1[j]);
                            		//cos2.put(licznik, j);
                            		
                            		obj.put(licznik, arr1[j]);
                            		//System.out.println(cos);
                                	//System.out.println("cos w petli"+cos);
                                	tablicaCos2.put(cos);

                            	}
                            	
                            	lista.add(obj);
                            	
                            	//System.out.println("cCOS2"+cos2);
                            	//System.out.println("cos nie w petli"+cos);
                            	tablicaCos.put(obj);
                            	//Gson gson = new Gson();
                            	//String json = gson.toJson(obj, LinkedHashMap.class);
                            	
                            	//cos = new JSONObject(json);
                            	//System.out.println(cos);
                            	//System.out.println(obj);
                            	//String json2 = json.replace("\\\\", "");
                            	//System.out.println("json=" + json);
                            	//System.out.println("json2=" + json2);
                            	
                            	
                            	//tablicaCos.put(json2);
                                i++;                                                              
                            }
                        	JSONArray tablicaCosSll = new JSONArray();
                        	
                        	
                        
                     	
                          // for(int kk = 0; kk < lista.size(); kk += 1) {
               	
                            	MyObj mo = new MyObj();
                            	Map<String, String> obj = lista.get(0);
                            	obj.forEach((key, value) -> {
                        			
                            		mo.Ad(key, value);
                            		
                            		
                            		
                            		
                            	});
                            	
                            	Pojo p = new Pojo(mo);
                            	
                         //   }
                            
                      
                       
                                Gson gson = new Gson();
	                            String json = gson.toJson(mo);
	                            //System.out.println(json);
	                            	
	                            
	                           // JSONArray jsonArray = new JSONArray(sb.toString());
                            
	                            
                            
                            //System.out.println("WYNIK"+tablicaCos2);
                            //System.out.println("TABLICACOS=" + tablicaCos);
                            //System.out.println(tablicaCos);
                            /////////////////////////////
                            /*JSONArray json = new JSONArray();
                            
                            while(resultSet.next()) {
                              int numColumns = rsmd.getColumnCount();
                              JSONObject obj = new JSONObject();
                              for (int k=1; k<=numColumns; k++) {
                                String column_name = rsmd.getColumnName(k);
                                obj.put(column_name, resultSet.getObject(column_name));
                              }
                              json.put(obj);
                            }*/
                            
                            
                            /////////////////////////////

                            
                            mainObj.put("dzialanie", "wysylamTabele");
                            mainObj.put("Tabela", tablicaCos);
                            mainObj.put("NazwaTabeli", wybranaTabela);
                            
                            
                            //mainObj.put("Kolumny", cos2);
                            //mainObj.put("Tabela", json);
                            
                            System.out.println(mainObj.toString());
                            // 0 - malarze , 1 - obrazy
                            
                            
                            
                            //String tab[] = arr;
                            
                            //SessionHandler.sendToallConnectedSessionsInRoom(room, mainObj.toString());
                            if(s == session) {
                            	SessionHandler.sendToSession(s, mainObj.toString());
                            }
                            //SessionHandler.sendToallConnectedSessionsInRoom(room, json.toString());

                            
                        	
                        	
                			}
            		 catch (Exception e) {
                         e.printStackTrace();
                         SessionHandler.sendToSession(session, "Lipa");
                     }
                    	
                    }
                    if(proszedzialaj.equals("Edytuj")) {
                    	try {
                    		if(s == session) {

                    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        connection = DriverManager.getConnection(url);
                        String schema = connection.getSchema();
                        System.out.println("Successful connection - Schema: " + schema);

                        String selectSql = jsonObj.getString("zapytanie");
                        //wypisanie komendy sql do okna przegladarki
                        //SessionHandler.sendToSession(session, selectSql);
                        
                        Statement statement = connection.createStatement();
                        
                    	statement.executeUpdate(selectSql);
                    	
                        String wybranaTabela = jsonObj.getString("tabela");

                        selectSql = "SELECT * FROM dbo." + wybranaTabela;
                        ResultSet resultSet = statement.executeQuery(selectSql);
                    	
                    	ResultSetMetaData rsmd = resultSet.getMetaData();

                    	int columnsNumber = rsmd.getColumnCount();
                    	String columnName[]=new String[10];
                    	for(int k=1;k<columnsNumber+1;k++) {
                    		columnName[k] = rsmd.getColumnName(k);
                    	}
                    	
                    	JSONArray tablicaCos = new JSONArray();
                    	JSONObject mainObj = new JSONObject();

                    	String licznik = " ";

                    	int i = 1;
                        int j = 1;
                        
                        String arr1[] = new String[100];

                        while (resultSet.next()) {
                        	Map<String, String> obj = new LinkedHashMap<String, String>();
                        	JSONObject cos = new JSONObject();                       	
                        	for(j=1;j<columnsNumber+1;j++) {  
                        		arr1[j] = resultSet.getString(j);
                        		licznik= columnName[j];                        		
                        		obj.put(licznik, arr1[j]);
                        	}
                        	tablicaCos.put(obj);
                            i++;                                                              
                        }
                    	
                        mainObj.put("dzialanie", "wysylamTabele");
                        mainObj.put("Tabela", tablicaCos);
                        mainObj.put("NazwaTabeli", wybranaTabela);

                        System.out.println(mainObj);
                        SessionHandler.sendToallConnectedSessionsInRoom(room, mainObj.toString());
                    	}

                    	
                    	
                    	}
                    	catch (Exception e) {
                            e.printStackTrace();
                            SessionHandler.sendToSession(session, "Blad edycji");
                        }
                    	
                    }
                    if(proszedzialaj.equals("Usun")) {
                    	try {
                    		if(s == session) {

                    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        connection = DriverManager.getConnection(url);
                        String schema = connection.getSchema();
                        System.out.println("Successful connection - Schema: " + schema);

                        String selectSql = jsonObj.getString("zapytanie");
                        //wypisanie komendy sql do okna przegladarki
                        //SessionHandler.sendToSession(session, selectSql);
                        
                        Statement statement = connection.createStatement();
                        
                    	statement.executeUpdate(selectSql);
                    	
                        String wybranaTabela = jsonObj.getString("tabela");

                        selectSql = "SELECT * FROM dbo." + wybranaTabela;
                        ResultSet resultSet = statement.executeQuery(selectSql);
                    	
                    	ResultSetMetaData rsmd = resultSet.getMetaData();

                    	int columnsNumber = rsmd.getColumnCount();
                    	String columnName[]=new String[10];
                    	for(int k=1;k<columnsNumber+1;k++) {
                    		columnName[k] = rsmd.getColumnName(k);
                    	}
                    	
                    	JSONArray tablicaCos = new JSONArray();
                    	JSONObject mainObj = new JSONObject();

                    	String licznik = " ";

                    	int i = 1;
                        int j = 1;
                        
                        String arr1[] = new String[100];

                        while (resultSet.next()) {
                        	Map<String, String> obj = new LinkedHashMap<String, String>();
                        	JSONObject cos = new JSONObject();                       	
                        	for(j=1;j<columnsNumber+1;j++) {  
                        		arr1[j] = resultSet.getString(j);
                        		licznik= columnName[j];                        		
                        		obj.put(licznik, arr1[j]);
                        	}
                        	tablicaCos.put(obj);
                            i++;                                                              
                        }
                    	
                        mainObj.put("dzialanie", "wysylamTabele");
                        mainObj.put("Tabela", tablicaCos);
                        mainObj.put("NazwaTabeli", wybranaTabela);

                        //mainObj.put("NazwaTabeli", wybranaTabela);
                        System.out.println(mainObj);
                        SessionHandler.sendToallConnectedSessionsInRoom(room, mainObj.toString());
                    	}

                    	
                    	
                    	}
                    	catch (Exception e) {
                            e.printStackTrace();
                            SessionHandler.sendToSession(session, "Blad edycji");
                        }
                    	
                    }
                    if(proszedzialaj.equals("Dodaj")) {
                    	try {
                    		if(s == session) {
                    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        connection = DriverManager.getConnection(url);
                        String schema = connection.getSchema();
                        System.out.println("Successful connection - Schema: " + schema);
                        String wybranaTabela = jsonObj.getString("tabela");
                        
                        String selectSql = "SELECT * FROM dbo." + wybranaTabela;
                        System.out.println("TO JEST SELECT SQL");
                        System.out.println(selectSql);
                        System.out.println("TO JEST SELECT SQL");
                        
                        Statement statement = connection.createStatement();
                        
                        ResultSet rs = statement.executeQuery(selectSql);
                    	
                        String zapytanie = "INSERT INTO dbo." + wybranaTabela + " VALUES (";                       
                        
                        rs.next();
                            ResultSetMetaData rsmd = rs.getMetaData();
                            for (int i = 2; i <= rsmd.getColumnCount(); i++) {


                                int type = rsmd.getColumnType(i);
                                if(i==(rsmd.getColumnCount())) {
                                    if (type == Types.VARCHAR || type == Types.CHAR) {
                                        //System.out.print("TEXT "+rs.getString(i));
                                        zapytanie = new StringBuilder(zapytanie).append("'").toString();
                                        zapytanie = new StringBuilder(zapytanie).append(jsonObj.get(jsonObj.names().getString(i-1))).toString();
                                        zapytanie = new StringBuilder(zapytanie).append("'").toString();
                                    } else 
                                    {
                                        //System.out.print("LICZBA "+rs.getLong(i));
                                        zapytanie = new StringBuilder(zapytanie).append(jsonObj.get(jsonObj.names().getString(i-1))).toString();
                                    }
                                	
                                }
                                else
                                if (type == Types.VARCHAR || type == Types.CHAR) {
                                    //System.out.print("TEXT "+rs.getString(i));
                                    zapytanie = new StringBuilder(zapytanie).append("'").toString();
                                    zapytanie = new StringBuilder(zapytanie).append(jsonObj.get(jsonObj.names().getString(i-1))).toString();
                                    zapytanie = new StringBuilder(zapytanie).append("'").toString();
                                    zapytanie = new StringBuilder(zapytanie).append(", ").toString();
                                } else {
                                    //System.out.print("LICZBA "+rs.getLong(i));
                                    zapytanie = new StringBuilder(zapytanie).append(jsonObj.get(jsonObj.names().getString(i-1))).toString();
                                    zapytanie = new StringBuilder(zapytanie).append(", ").toString();
                                }
                            }
                            System.out.println("ZAPYTANIE");
                            zapytanie = new StringBuilder(zapytanie).append(")").toString();
                            
                            System.out.println(zapytanie);
                            System.out.println("ZAPYTANIE");
                        
                        
////////////////////////////////////////////////////////////////////////////                       
                        
                        statement.executeUpdate(zapytanie);
                        ResultSet resultSet = statement.executeQuery(selectSql);
                        
                        
                    	
                    	rsmd = resultSet.getMetaData();
                    	

                    	int columnsNumber = rsmd.getColumnCount();
                    	String columnName[]=new String[10];
                    	for(int k=1;k<columnsNumber+1;k++) {
                    		columnName[k] = rsmd.getColumnName(k);
                    	}
                    	
                    	JSONArray tablicaCos = new JSONArray();
                    	JSONObject mainObj = new JSONObject();

                    	String licznik = " ";

                    	int i = 1;
                        int j = 1;
                        
                        String arr1[] = new String[100];

                        while (resultSet.next()) {
                        	Map<String, String> obj = new LinkedHashMap<String, String>();
                        	JSONObject cos = new JSONObject();                       	
                        	for(j=1;j<columnsNumber+1;j++) {  
                        		arr1[j] = resultSet.getString(j);
                        		licznik= columnName[j];                        		
                        		obj.put(licznik, arr1[j]);
                        	}
                        	tablicaCos.put(obj);
                            i++;                                                              
                        }
                    		
                    	
                        mainObj.put("dzialanie", "wysylamTabele");
                        mainObj.put("Tabela", tablicaCos);
                        mainObj.put("NazwaTabeli", wybranaTabela);
                        System.out.println(mainObj);
                        SessionHandler.sendToallConnectedSessionsInRoom(room, mainObj.toString());
                    		}

                    	
                    	
                    	}
                    	catch (Exception e) {
                            e.printStackTrace();
                            SessionHandler.sendToSession(session, "Blad edycji");
                        }
                    	
                    }
                    if(proszedzialaj.equals("Zapytanie")) {
                    	try {
                    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        connection = DriverManager.getConnection(url);
                        String schema = connection.getSchema();
                        System.out.println("Successful connection - Schema: " + schema);

                        String selectSql = jsonObj.getString("komenda");
                        
                        Statement statement = connection.createStatement();
                        
                    	statement.executeUpdate(selectSql);
                    	                   	                    	
                    	JSONObject mainObj = new JSONObject();

                        mainObj.put("dzialanie", "sukces");
                        mainObj.put("ostatnieZapytanie", selectSql);
                        mainObj.put("NazwaTabeli", selectSql);
                        System.out.println(mainObj);
                        if(s == session) {
                        	SessionHandler.sendToSession(s, mainObj.toString());
                        	TimeUnit.SECONDS.sleep(1);
                        	SessionHandler.sendToallConnectedSessions(mainObj.toString());
                        }
                        
                   	                    	
                    	}
                    	catch (Exception e) {
                    		try {
                            	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                                connection = DriverManager.getConnection(url);
                                String schema = connection.getSchema();
                                System.out.println("Successful connection - Schema: " + schema);

                                String selectSql = jsonObj.getString("komenda");
                                //wypisanie komendy sql do okna przegladarki
                                //SessionHandler.sendToSession(session, selectSql);
                                
                                Statement statement = connection.createStatement();
                                
                                ResultSet resultSet = statement.executeQuery(selectSql);
                            	
                            	ResultSetMetaData rsmd = resultSet.getMetaData();
                            	                  	
                               	String nazwaTabeli = rsmd.getColumnName(1);
                            	
                            	System.out.println("TO JEST NAZWA TABELI " + nazwaTabeli);

                            	int columnsNumber = rsmd.getColumnCount();
                            	String columnName[]=new String[10];
                            	for(int k=1;k<columnsNumber+1;k++) {
                            		columnName[k] = rsmd.getColumnName(k);
                            	}
                            	
  
                            	
                            	JSONArray tablicaCos = new JSONArray();
                            	JSONObject mainObj = new JSONObject();

                            	String licznik = " ";

                            	int i = 1;
                                int j = 1;
                                
                                String arr1[] = new String[100];

                                while (resultSet.next()) {
                                	Map<String, String> obj = new LinkedHashMap<String, String>();
                                	JSONObject cos = new JSONObject();                       	
                                	for(j=1;j<columnsNumber+1;j++) {  
                                		arr1[j] = resultSet.getString(j);
                                		licznik= columnName[j];                        		
                                		obj.put(licznik, arr1[j]);
                                	}
                                	tablicaCos.put(obj);
                                    i++;                                                              
                                }
                                
                            	
                                mainObj.put("dzialanie", "sukcesTabela");
                                mainObj.put("Tabela", tablicaCos);
                                mainObj.put("ostatnieZapytanie", selectSql);
                                mainObj.put("NazwaTabeli", nazwaTabeli);
                                System.out.println(mainObj);
                                if(s == session) {
                                	
                                    SessionHandler.sendToSession(s, mainObj.toString());
                                    TimeUnit.SECONDS.sleep(1);
                                    SessionHandler.sendToallConnectedSessions(mainObj.toString());

                                }
                            	   	
                            	}
                            	catch (Exception f) {
                            		
                            		//TimeUnit.SECONDS.sleep(timeout);
                            		System.out.println("TUTAJ BLAD" + f.getMessage());
                            		JSONObject mainObj = new JSONObject();
                            		mainObj.put("dzialanie", "blad");
                            		mainObj.put("bladTekst", f.getMessage());
                            		mainObj.put("kodBledu", ((SQLException)f).getErrorCode());
                            		
                            			if(s == session ) {
                                        SessionHandler.sendToSession(s, mainObj.toString());
                                        TimeUnit.SECONDS.sleep(1);
                                        SessionHandler.sendToallConnectedSessions(mainObj.toString());

                            			}
                                    
                                }
                        }
                    }
                    else {
                        s.getBasicRemote().sendObject(message);
                    }
                }
            }
        }
        catch (IOException| EncodeException e){
           
        }
    }
    

    
 
    /**
     * The user closes the connection.
     *
     * Note: you can't send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session){
        System.out.println("Session " +session.getId()+" has ended");
        SessionHandler.removeSession(session);
       
    }
   
    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, error);
    }
}