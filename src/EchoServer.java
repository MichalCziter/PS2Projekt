import java.io.IOException;
import java.util.Map;
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
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;

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
     */
    @OnMessage
    public void onMessage(String message, Session session) throws JSONException{
    	
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
                    SessionHandler.sendToallConnectedSessionsInRoom(room, message);

                    
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
                            
                            SessionHandler.sendToallConnectedSessionsInRoom(room, cos.toString());

                            
                        	
                        	
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
                        	JSONObject mainObj = new JSONObject();
                        	//cos.put("dzialanie", "wysylamTabele");
                        	String licznik = " ";
                        	String test[] = new String[500];
                        	
                            //int i = 0;
                        	int i = 1;
                            int j = 1;
                            
                            String arr[][] = new String[100][100];
                            
                            while (resultSet.next()) {
                            	JSONObject cos = new JSONObject();
                            	//licznik= columnName[i];

                            	
                            	for(j=1;j<columnsNumber+1;j++) {
                            		
                            		
                            		arr[i][j] = resultSet.getString(j);
                            		licznik= columnName[j];
                            		cos.put(licznik, arr[i][j]);
                            		
                                	//System.out.print(resultSet.getString(j));
                                	

                            		
                            	}
                            	tablicaCos.put(cos);
                                //String em = resultSet.getString(1);
                                //arr[i] = em;
                                //licznik= "tabela" + String.valueOf(i);
                                //System.out.println(arr);
                            	//System.out.println(em);
                            	//cos.put(licznik, arr[i]);
                                i++;
                                
                            }

                            
                            mainObj.put("dzialanie", "wysylamTabele");
                            mainObj.put("Tabela", tablicaCos);
                            
                            System.out.println(mainObj.toString());
                            // 0 - malarze , 1 - obrazy
                            
                            
                            
                            //String tab[] = arr;
                            
                            SessionHandler.sendToallConnectedSessionsInRoom(room, mainObj.toString());

                            
                        	
                        	
                			}
            		 catch (Exception e) {
                         e.printStackTrace();
                         SessionHandler.sendToSession(session, "Lipa");
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