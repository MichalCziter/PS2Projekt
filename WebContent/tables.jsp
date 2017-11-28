<%@ page import="java.sql.*" %>
<%@ page import="com.microsoft.sqlserver.jdbc.*" %>
<%ResultSet resultset = null;%>

<HTML>
<HEAD>
  <title>Bootstrap Example</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script src="require.js"></script>
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


<right>


	<%
	resultset =statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = N'Malarze'") ;
	%>
	<br><br><br>
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
    	String wybranaTabela = request.getParameter("choose");
    	System.out.println(wybranaTabela);
	//resultset =statement.executeQuery("SELECT * FROM dbo." + wybranaTabela) ;
	resultset =statement.executeQuery("SELECT * FROM dbo.Malarze") ;
	%>
    <tbody>
          <%int i =1; 
          while(resultset.next()){ %>
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
  
  <a href="test.jsp">powrot</a>
  <button type="button" onclick="getZawartosc()">Basic</button>

</right>
</div>
<div id="messages"></div>


<script type="text/javascript">
var messages = document.getElementById("messages");

function getZawartosc() {
	var Connection = require(['tedious']).Connection;
	var Request = require(['tedious']).Request;

	// Create connection to database
	var config = 
	   {
	     userName: 'maras314', // update me
	     password: 'malarze314Y', // update me
	     server: 'malarzeserwer.database.windows.net', // update me
	     options: 
	        {
	           database: 'malarzeBaza' //update me
	           , encrypt: true
	        }
	   }
	var connection = new Connection(config);

	// Attempt to connect and execute queries if connection goes through
	connection.on('connect', function(err) 
	   {
	     if (err) 
	       {
	          console.log(err)
	       }
	    else
	       {
	           queryDatabase()
	       }
	   }
	 );
	
    // Read all rows from table
    request = new Request(
         "SELECT * FROM dbo.Malarze",
            function(err, rowCount, rows) 
               {
                   console.log(rowCount + ' row(s) returned');
                   process.exit();
               }
           );

    request.on('row', function(columns) {
       columns.forEach(function(column) {
           console.log("%s\t%s", column.metadata.colName, column.value);
        });
            });
    connection.execSql(request);


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