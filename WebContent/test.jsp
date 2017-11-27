<%@ page import="java.sql.*" %>
<%@ page import="com.microsoft.sqlserver.jdbc.*" %>
<%@ page import="java.io.IOException;" %>
<%ResultSet resultset = null;%>

<HTML>
<HEAD>
  <title>Bootstrap Example</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

</HEAD>

<BODY BGCOLOR=##f89ggh>
<div>


<%
    try{
    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");


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

<left>
    <h1> Wybierz tabele</h1>
        <select id="wybor">
        <%  
        int l = 1;
        while(resultset.next()){ %>
        var i = 1;
            <option value=""+i><%= resultset.getString(1)%></option>
			i++;
        <% } %>
        </select>
            <input type="submit" value="click"> 
            
        <select id="wybor2">

            <option value="1">janek</option>
            <option value="2">dzbanek</option>

        </select>
            
        
</left>
<center>



<button type="button" onclick="getZawartosc()">Basic</button>

<%

int i = 1;
String adres = "dupa";

%>



<input type="button" onclick="location.href='tables.jsp?choose=<%= adres%>'" value="Go to Google" />
<a href="tables.jsp?choose=<%= adres%>">TABLES</a>

</center>
<right>
	<%
	resultset =statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'Malarze'") ;
	%>
	  <div class="table-responsive">          
  <table class="table">
    <thead>

      <tr>
          <% while(resultset.next()){ %>
        <th><%= resultset.getString(4)%></th>

        <% } %>
      </tr>
    </thead>
    	<%
	resultset =statement.executeQuery("SELECT * FROM dbo.Malarze") ;
	%>
    <tbody>
          <% while(resultset.next()){ %>
      <tr>

        <td><%= resultset.getString(1)%></td>
        <td><%= resultset.getString(2)%></td>
        <td><%= resultset.getString(3)%></td>
        <td><%= resultset.getString(4)%></td>
        <td><%= resultset.getString(5)%></td>
        <td><%= resultset.getString(6)%></td>

        <% } %>
      </tr>
    </tbody>
  </table>
  </div>

</right>
</div>
<div id="messages"></div>


<script type="text/javascript">
var messages = document.getElementById("messages");

var e = document.getElementById("wybor");
var strUser = e.options[e.selectedIndex].text;

function getZawartosc() {
	
    <%resultset =statement.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_CATALOG='malarzeBaza'") ;
%>
<% while(resultset.next()){ %>
<% String str=resultset.getString(1);
System.out.println(resultset.getString(1));
%>
<%}%>





	e = document.getElementById("wybor");
	strUser = e.options[e.selectedIndex].text;

	
    if(strUser == "Malarze"){
        alert("DOBRZE"); 
    }
    else {
    	alert("dupa");
    }
    
    window.location = "http://localhost:8080/PS2Projekt/tables.jsp?choose=" + strUser;
    
	

}





function writeResponse(text){
    messages.innerHTML += "<br/>" + text;
}
</script>
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