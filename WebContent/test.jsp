<%@ page import="java.sql.*" %>
<%@ page import="com.microsoft.sqlserver.jdbc.*" %>
<%ResultSet resultset =null;%>

<HTML>
<HEAD>
    <TITLE>Select element drop down box</TITLE>
</HEAD>

<BODY BGCOLOR=##f89ggh>

<%
    try{
//Class.forName("com.mysql.jdbc.Driver").newInstance();

String hostName = "malarzeserwer.database.windows.net";
String dbName = "malarzeBaza";
String user = "maras314";
String password = "malarze314Y";
String url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);
Connection connection = null;   
connection = DriverManager.getConnection(url);

Statement statement = connection.createStatement() ;

       resultset =statement.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_CATALOG='malarzeBaza'") ;
%>

<center>
    <h1> Drop down box or select element</h1>
        <select>
        <%  while(resultset.next()){ %>
            <option><%= resultset.getString(1)%></option>
        <% } %>
        </select>
</center>

<%
//**Should I input the codes here?**
        }
        catch(Exception e)
        {
             out.println("wrong entry"+e);
        }
%>

</BODY>
</HTML>