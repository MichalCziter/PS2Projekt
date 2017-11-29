<!DOCTYPE html>
 
<html>
    <head>
        <title>Echo Chamber</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width">
          <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
          <script type="text/javascript">
        function openSocket(){
            // Ensures only one connection is open at a time
            
            if(webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED){
               writeResponse("WebSocket is already opened.");
                return;
            }
            // Create a new instance of the websocket
            webSocket = new WebSocket("ws://localhost:8080/PS2Projekt/echo/roomnumber");
            //webSocket = new WebSocket("ws://kapustatest.azurewebsites.net/PS2Projekt/echo/roomnumber");
             
            writeResponse("Otwarlem sie WIDOKTABELI");
            /**
             * Binds functions to the listeners for the websocket.
             */
             
            webSocket.onopen = function(event){
                // For reasons I can't determine, onopen gets called twice
                // and the first time event.data is undefined.
                // Leave a comment if you know the answer.
                if(event.data === undefined)
                    return;

                writeResponse(event.data);
               
            };

            webSocket.onmessage = function(event){
            	obsluga(event);
                writeResponse(event.data);
            };

            webSocket.onclose = function(event){
                writeResponse("Connection closed");
            };
            
            
        }
        function poprosTabele(){
        	var myParam = location.search.split('choose=')[1];
        	var text = myParam;
        	
        	var obj = new Object();
        	obj.dzialanie = "Tabela";
        	obj.tabela = text;
        	var jsonString= JSON.stringify(obj);
			
        	writeResponse(jsonString + "Dziala xDDdD");
            webSocket.send(jsonString);
        	writeResponse("POSZLO");
        	
        	
        }
        window.onload = function(){
        	openSocket();
        	//var obj = poprosTabele();
        	//webSocket.send(obj);
			
        	//poprosTabele();
        }
        </script>
    </head>
    <body>
       
        <div>
            SQL Command :<input type="text" id="messageinput"/>
        </div>
        <div id="container">
        	
        </div>
        <div>
        	<button type="button" onclick="poprosTabele();" >KLIKAJ TUTAJ</button>
        	
            <button type="button" onclick="pobierzNazwy();" >Pobierz Tabele</button>
            <button type="button" onclick="pokazZawartosc();" >Pokaz Zawartosc tabeli</button>

<div id="tabela">
<!-- TU BEDA TABELE NARYSOWANE -->
</div>
            
        </div>
        <!-- Server responses get written here -->
        <div id="messages"></div>
        
        <div id="showData"></div>
       
        <!-- Script to utilise the WebSocket -->
        <script type="text/javascript">
                       
            var webSocket;
            var messages = document.getElementById("messages");
           
           
            
           
            /**
             * Sends the value of the text input to the server
             */
            function send(){
                //var text = document.getElementById("messageinput").value;
                webSocket.send(text);
                
                
            }
            
            function obsluga(event){
            	            	
            	var json = JSON.parse(event.data);
            	writeResponse(json.dzialanie);
            	if (json.dzialanie == "tabele"){
            		var div = document.querySelector("#container"),
            	    frag = document.createDocumentFragment(),
            	    select = document.createElement("select");

            		select.id = "wybor";

            		select.options.add( new Option(json.tabela0,"AU", true, true) );
            		select.options.add( new Option(json.tabela1,"FI") );


            		frag.appendChild(select);
            		div.appendChild(frag);
            		
            	}
            	if(json.dzialanie == "wysylamTabele"){
            		
            		writeResponse("dupa");
            		var myBooks = json.Tabela;

            		
            		console.log(myBooks);
            		//wypisuje dobrze do loga, ale nie mozna wystwietlic na stronie bo pokazuje object
            		//tworzymy tabele
            		    var col = [];
				        for (var i = 0; i < myBooks.length; i++) {
				            for (var key in myBooks[i]) {
				                if (col.indexOf(key) === -1) {
				                    col.push(key);
				                }
				            }
				        }
				        // CREATE DYNAMIC TABLE.
				        var table = document.createElement("table");

				        // CREATE HTML TABLE HEADER ROW USING THE EXTRACTED HEADERS ABOVE.

				        var tr = table.insertRow(-1);                   // TABLE ROW.

				        for (var i = 0; i < col.length; i++) {
				            var th = document.createElement("th");      // TABLE HEADER.
				            th.innerHTML = col[i];
				            tr.appendChild(th);
				        }

				        // ADD JSON DATA TO THE TABLE AS ROWS.
				        for (var i = 0; i < myBooks.length; i++) {

				            tr = table.insertRow(-1);

				            for (var j = 0; j < col.length; j++) {
				                var tabCell = tr.insertCell(-1);

				                tabCell.innerHTML = myBooks[i][col[j]];
				            }
				        }

				        // FINALLY ADD THE NEWLY CREATED TABLE WITH JSON DATA TO A CONTAINER.
				        var divContainer = document.getElementById("showData");
				        divContainer.innerHTML = "";
				        divContainer.appendChild(table);
            		
            		
            	}
            	
            }
            
            function pokazZawartosc(){

            	var e = document.getElementById("wybor");
            	var strUser = e.options[e.selectedIndex].text;
            	            	
                if(strUser == "Malarze"){
                    alert("DOBRZE"); 
                }
                else {
                	alert("dupa");
                }
                
                window.location = "http://localhost:8080/PS2Projekt/tables.jsp?choose=" + strUser;

                closeSocket();
            }
           
            function closeSocket(){
                webSocket.close();
            }
 
            function writeResponse(text){
                messages.innerHTML += "<br/>" + text;
            }
            
            function pobierzNazwy(){
            	alert('o');
            	var text = "Pobierz"
            	webSocket.send(text);
            }
           
        </script>
       
    </body>
</html>