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

import com.microsoft.sqlserver.jdbc.*;
import org.json.*;

/**
 * @ServerEndpoint gives the relative name for the end point This will be
 *                 accessed via ws://localhost:8080/EchoChamber/echo Where
 *                 "localhost" is the address of the host, "EchoChamber" is the
 *                 name of the package and "echo" is the address to access this
 *                 class from the server
 */
@ServerEndpoint(value = "/echo/{roomnumber}")
public class EchoServer {

	String hostName = "malarzeserwer.database.windows.net";
	String dbName = "malarzeBaza";
	String user = "maras314";
	String password = "malarze314Y";
	String url = String.format(
			"jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;",
			hostName, dbName, user, password);
	Connection connection = null;

	/**
	 * @throws EncodeException
	 * @throws IOException
	 * @OnOpen allows us to intercept the creation of a new session. The session
	 *         class allows us to send data to the user. In the method onOpen, we'll
	 *         let the user know that the handshake was successful.
	 */

	@OnOpen
	public void onOpen(Session session, @PathParam("roomnumber") String roomnumber)
			throws IOException, EncodeException {
		System.out.println(session.getId() + " has opened a connection");
		session.getUserProperties().put("roomnumber", roomnumber);
		SessionHandler.addSession(session);


	}

	/**
	 * When a user sends a message to the server, this method will intercept the
	 * message and allow us to react to it. For now the message is read as a String.
	 * 
	 * @throws JSONException
	 * @throws InterruptedException
	 */
	@OnMessage
	public void onMessage(String message, Session session) throws JSONException, InterruptedException {

		JSONObject jsonObj = new JSONObject(message);
		System.out.println("ODEBRANE SPRAWDZAM");
		System.out.println(message);
		System.out.println("SPRAWDZAM JSON");
		System.out.println(jsonObj);
		String dzialanieKlienta = jsonObj.getString("dzialanie");

		String room = (String) session.getUserProperties().get("roomnumber");
		try {
			for (Session s : session.getOpenSessions()) {
				if (s.isOpen() && s.getUserProperties().get("roomnumber").equals(room)) {

					if (dzialanieKlienta.equals("Pobierz")) {
						try {

							Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
							connection = DriverManager.getConnection(url);
							String schema = connection.getSchema();
							System.out.println("Successful connection - Schema: " + schema);

							String selectSql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_CATALOG='malarzeBaza'";

							Statement statement = connection.createStatement();

							ResultSet resultSet = statement.executeQuery(selectSql);

							JSONObject record = new JSONObject();
							record.put("dzialanie", "tabele");
							String licznik;

							int i = 0;
							String arr[] = new String[50];
							while (resultSet.next()) {
								String em = resultSet.getString(1);
								arr[i] = em;
								licznik = "tabela" + String.valueOf(i);

								record.put(licznik, em);
								i++;

							}

							System.out.println(record.toString());

							if (s == session) {
								TimeUnit.SECONDS.sleep(1);
								SessionHandler.sendToSession(s, record.toString());
							}

						} catch (Exception e) {
							e.printStackTrace();
							SessionHandler.sendToSession(session, "Lipa");
						}

					}
					if (dzialanieKlienta.equals("Tabela")) {
						try {

							Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
							connection = DriverManager.getConnection(url);
							String schema = connection.getSchema();
							System.out.println("Successful connection - Schema: " + schema);
							String wybranaTabela = jsonObj.getString("tabela");

							String selectSql = "SELECT * FROM dbo." + wybranaTabela;

							Statement statement = connection.createStatement();

							ResultSet resultSet = statement.executeQuery(selectSql);
							ResultSetMetaData rsmd = resultSet.getMetaData();

							int columnsNumber = rsmd.getColumnCount();
							String columnName[] = new String[10];
							for (int k = 1; k < columnsNumber + 1; k++) {
								columnName[k] = rsmd.getColumnName(k);
							}

							System.out.println(columnsNumber);

							JSONArray tablicaRecordow = new JSONArray();
							JSONObject mainObj = new JSONObject();

							String licznik = " ";

							int j = 1;

							String arr1[] = new String[100];
							ArrayList<Map<String, String>> lista = new ArrayList<Map<String, String>>();

							while (resultSet.next()) {

								Map<String, String> obj = new LinkedHashMap<String, String>();
								JSONObject record = new JSONObject();

								for (j = 1; j < columnsNumber + 1; j++) {

									arr1[j] = resultSet.getString(j);
									licznik = columnName[j];

									obj.put(licznik, arr1[j]);

								}

								lista.add(obj);

								tablicaRecordow.put(obj);

							}

							mainObj.put("dzialanie", "wysylamTabele");
							mainObj.put("Tabela", tablicaRecordow);
							mainObj.put("NazwaTabeli", wybranaTabela);

							System.out.println(mainObj.toString());

							if (s == session) {
								SessionHandler.sendToSession(s, mainObj.toString());
							}

						} catch (Exception e) {
							e.printStackTrace();
							SessionHandler.sendToSession(session, "Lipa");
						}

					}
					if (dzialanieKlienta.equals("Edytuj")) {
						try {
							if (s == session) {

								Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
								connection = DriverManager.getConnection(url);
								String schema = connection.getSchema();
								System.out.println("Successful connection - Schema: " + schema);

								String selectSql = jsonObj.getString("zapytanie");

								Statement statement = connection.createStatement();

								statement.executeUpdate(selectSql);

								String wybranaTabela = jsonObj.getString("tabela");

								selectSql = "SELECT * FROM dbo." + wybranaTabela;
								ResultSet resultSet = statement.executeQuery(selectSql);

								ResultSetMetaData rsmd = resultSet.getMetaData();

								int columnsNumber = rsmd.getColumnCount();
								String columnName[] = new String[10];
								for (int k = 1; k < columnsNumber + 1; k++) {
									columnName[k] = rsmd.getColumnName(k);
								}

								JSONArray tablicaRecordow = new JSONArray();
								JSONObject mainObj = new JSONObject();

								String licznik = " ";

								int j = 1;

								String arr1[] = new String[100];

								while (resultSet.next()) {
									Map<String, String> obj = new LinkedHashMap<String, String>();

									for (j = 1; j < columnsNumber + 1; j++) {
										arr1[j] = resultSet.getString(j);
										licznik = columnName[j];
										obj.put(licznik, arr1[j]);
									}
									tablicaRecordow.put(obj);

								}

								mainObj.put("dzialanie", "wysylamTabele");
								mainObj.put("Tabela", tablicaRecordow);
								mainObj.put("NazwaTabeli", wybranaTabela);

								System.out.println(mainObj);
								SessionHandler.sendToallConnectedSessionsInRoom(room, mainObj.toString());
							}

						} catch (Exception e) {
							e.printStackTrace();
							SessionHandler.sendToSession(session, "Blad edycji");
						}

					}
					if (dzialanieKlienta.equals("Usun")) {
						try {
							if (s == session) {

								Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
								connection = DriverManager.getConnection(url);
								String schema = connection.getSchema();
								System.out.println("Successful connection - Schema: " + schema);

								String selectSql = jsonObj.getString("zapytanie");

								Statement statement = connection.createStatement();

								statement.executeUpdate(selectSql);

								String wybranaTabela = jsonObj.getString("tabela");

								selectSql = "SELECT * FROM dbo." + wybranaTabela;
								ResultSet resultSet = statement.executeQuery(selectSql);

								ResultSetMetaData rsmd = resultSet.getMetaData();

								int columnsNumber = rsmd.getColumnCount();
								String columnName[] = new String[10];
								for (int k = 1; k < columnsNumber + 1; k++) {
									columnName[k] = rsmd.getColumnName(k);
								}

								JSONArray tablicaRecordow = new JSONArray();
								JSONObject mainObj = new JSONObject();

								String licznik = " ";

								int j = 1;

								String arr1[] = new String[100];

								while (resultSet.next()) {
									Map<String, String> obj = new LinkedHashMap<String, String>();

									for (j = 1; j < columnsNumber + 1; j++) {
										arr1[j] = resultSet.getString(j);
										licznik = columnName[j];
										obj.put(licznik, arr1[j]);
									}
									tablicaRecordow.put(obj);

								}

								mainObj.put("dzialanie", "wysylamTabele");
								mainObj.put("Tabela", tablicaRecordow);
								mainObj.put("NazwaTabeli", wybranaTabela);

								System.out.println(mainObj);
								SessionHandler.sendToallConnectedSessionsInRoom(room, mainObj.toString());
							}

						} catch (Exception e) {
							e.printStackTrace();
							SessionHandler.sendToSession(session, "Blad edycji");
						}

					}
					if (dzialanieKlienta.equals("Dodaj")) {
						try {
							if (s == session) {
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

								///////////////////////////////////////
	
								
								
								//////////////////////
								String zapytanie = "INSERT INTO dbo." + wybranaTabela + " VALUES (";

								rs.next();
								ResultSetMetaData rsmd = rs.getMetaData();
								int flgaNull = 0;
								for (int i = 2; i <= rsmd.getColumnCount(); i++) {

									int type = rsmd.getColumnType(i);
									if(jsonObj.isNull(jsonObj.names().getString(i - 1))) {
										flgaNull = 1;
										break;
										
									}
									else if (i == (rsmd.getColumnCount())) {
										if (type == Types.VARCHAR || type == Types.CHAR) {

											zapytanie = new StringBuilder(zapytanie).append("'").toString();
											zapytanie = new StringBuilder(zapytanie)
													.append(jsonObj.get(jsonObj.names().getString(i - 1))).toString();
											zapytanie = new StringBuilder(zapytanie).append("'").toString();
										} else {

											zapytanie = new StringBuilder(zapytanie)
													.append(jsonObj.get(jsonObj.names().getString(i - 1))).toString();
										}

									} else if (type == Types.VARCHAR || type == Types.CHAR) {

										zapytanie = new StringBuilder(zapytanie).append("'").toString();
										zapytanie = new StringBuilder(zapytanie)
												.append(jsonObj.get(jsonObj.names().getString(i - 1))).toString();
										zapytanie = new StringBuilder(zapytanie).append("'").toString();
										zapytanie = new StringBuilder(zapytanie).append(", ").toString();
									} else {

										zapytanie = new StringBuilder(zapytanie)
												.append(jsonObj.get(jsonObj.names().getString(i - 1))).toString();
										zapytanie = new StringBuilder(zapytanie).append(", ").toString();
									}
								}
								if(flgaNull != 1) {
									System.out.println("ZAPYTANIE");
									zapytanie = new StringBuilder(zapytanie).append(")").toString();

									System.out.println(zapytanie);
									System.out.println("ZAPYTANIE");

									statement.executeUpdate(zapytanie);
								}  
								///////////////////////////////
								ResultSet resultSet = statement.executeQuery(selectSql);

								rsmd = resultSet.getMetaData();

								int columnsNumber = rsmd.getColumnCount();
								String columnName[] = new String[10];
								for (int k = 1; k < columnsNumber + 1; k++) {
									columnName[k] = rsmd.getColumnName(k);
								}

								JSONArray tablicaRecordow = new JSONArray();
								JSONObject mainObj = new JSONObject();

								String licznik = " ";

								int j = 1;

								String arr1[] = new String[100];

								while (resultSet.next()) {
									Map<String, String> obj = new LinkedHashMap<String, String>();

									for (j = 1; j < columnsNumber + 1; j++) {
										arr1[j] = resultSet.getString(j);
										licznik = columnName[j];
										obj.put(licznik, arr1[j]);
									}
									tablicaRecordow.put(obj);

								}

								mainObj.put("dzialanie", "wysylamTabele");
								mainObj.put("Tabela", tablicaRecordow);
								mainObj.put("NazwaTabeli", wybranaTabela);
								System.out.println(mainObj);
								SessionHandler.sendToallConnectedSessionsInRoom(room, mainObj.toString());
							}

						} catch (Exception e) {
							e.printStackTrace();
							SessionHandler.sendToSession(session, "Blad edycji");
						}

					}
					if (dzialanieKlienta.equals("Zapytanie")) {
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
							if (s == session) {
								SessionHandler.sendToSession(s, mainObj.toString());
								TimeUnit.SECONDS.sleep(1);
								SessionHandler.sendToallConnectedSessions(mainObj.toString());
							}

						} catch (Exception e) {
							try {
								Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
								connection = DriverManager.getConnection(url);
								String schema = connection.getSchema();
								System.out.println("Successful connection - Schema: " + schema);

								String selectSql = jsonObj.getString("komenda");

							    Statement statement = connection.createStatement();

								ResultSet resultSet = statement.executeQuery(selectSql);

								ResultSetMetaData rsmd = resultSet.getMetaData();

								String nazwaTabeli = rsmd.getColumnName(1);

								System.out.println("TO JEST NAZWA TABELI " + nazwaTabeli);

								int columnsNumber = rsmd.getColumnCount();
								String columnName[] = new String[10];
								for (int k = 1; k < columnsNumber + 1; k++) {
									columnName[k] = rsmd.getColumnName(k);
								}

								JSONArray tablicaRecordow = new JSONArray();
								JSONObject mainObj = new JSONObject();

								String licznik = " ";

								int j = 1;

								String arr1[] = new String[100];

								while (resultSet.next()) {
									Map<String, String> obj = new LinkedHashMap<String, String>();

									for (j = 1; j < columnsNumber + 1; j++) {
										arr1[j] = resultSet.getString(j);
										licznik = columnName[j];
										obj.put(licznik, arr1[j]);
									}
									tablicaRecordow.put(obj);

								}

								mainObj.put("dzialanie", "sukcesTabela");
								mainObj.put("Tabela", tablicaRecordow);
								mainObj.put("ostatnieZapytanie", selectSql);
								mainObj.put("NazwaTabeli", nazwaTabeli);
								System.out.println(mainObj);
								if (s == session) {

									SessionHandler.sendToSession(s, mainObj.toString());
									TimeUnit.SECONDS.sleep(1);
									SessionHandler.sendToallConnectedSessions(mainObj.toString());

								}

							} catch (Exception f) {

								System.out.println("TUTAJ BLAD" + f.getMessage());
								JSONObject mainObj = new JSONObject();
								mainObj.put("dzialanie", "blad");
								mainObj.put("bladTekst", f.getMessage());
								mainObj.put("kodBledu", ((SQLException) f).getErrorCode());

								if (s == session) {
									SessionHandler.sendToSession(s, mainObj.toString());
									TimeUnit.SECONDS.sleep(1);
									SessionHandler.sendToallConnectedSessions(mainObj.toString());

								}

							}
						}
					} else {
						s.getBasicRemote().sendObject(message);
					}
				}
			}
		} catch (IOException | EncodeException e) {

		}
	}

	/**
	 * The user closes the connection.
	 *
	 * Note: you can't send messages to the client from this method
	 */
	@OnClose
	public void onClose(Session session) {
		System.out.println("Session " + session.getId() + " has ended");
		SessionHandler.removeSession(session);

	}

	@OnError
	public void onError(Throwable error) {
		Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, error);
	}
}