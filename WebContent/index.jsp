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
            writeResponse("Otwarlem sie");
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
        
        function waitForSocketConnection(socket, callback){
            setTimeout(
                function () {
                    if (socket.readyState === 1) {
                        console.log("Connection is made")
                        if(callback != null){
                            callback();
                        }
                        return;

                    } else {
                        console.log("wait for connection...")
                        waitForSocketConnection(socket, callback);
                    }

                }, 5); // wait 5 milisecond for the connection...
        }

        window.onload = function(){
        	
        	
        	openSocket();

        	
            waitForSocketConnection(webSocket, function(){
                
            	pobierzNazwy();
            });
        }
        
        window.onunload = function(){
        	closeSocket();

        } 
        </script>
    </head>
    <body>
       
        <div>
            SQL Command :<input type="text" size="70" id="sqlinput"/>
            <button type="button" onclick="wyslijZapytanie();" >Wyslij Zapytanie</button>
        </div>
        <div id="container">
        	
        </div>
        <div>
            <button type="button" onclick="pobierzNazwy();" >Pobierz Tabele</button>
            <button type="button" onclick="pokazZawartosc();" >Pokaz Zawartosc tabeli</button>

<div id="tabela">
<!-- TU BEDA TABELE NARYSOWANE -->
</div>
            
        </div>
        <!-- Server responses get written here -->
        <div id="messages"></div>
       
        <!-- Script to utilise the WebSocket -->
        <script type="text/javascript">
        
        var domyslny = location.search.split('choose=')[1];
        if(domyslny != null){
        	document.getElementById('sqlinput').value = domyslny;
        }
        
                       
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
            	if(json.dzialanie == "blad"){
            		window.location = "http://localhost:8080/PS2Projekt/widoktabeli.jsp?choose=" + json.kodBledu;
            		
            	}
            	if(json.dzialanie == "sukces"){
            		
            		window.location = "http://localhost:8080/PS2Projekt/widoktabeli.jsp?choose=" + json.NazwaTabeli;
            	}
            	
            }
            function wyslijZapytanie(){
            	var e = document.getElementById("sqlinput").value;
            	var objZapytanie = new Object();
            	objZapytanie.dzialanie = "Zapytanie";
            	objZapytanie.komenda = e;
            	var jsonZapytanie = JSON.stringify(objZapytanie);
            	//writeResponse(jsonZapytanie);
            	webSocket.send(jsonZapytanie);
            	//window.location = "http://localhost:8080/PS2Projekt/widoktabeli.jsp?choose=";
            	//window.location = "http://kapustatest.azurewebsites.net/PS2Projekt/widoktabeli.jsp";
            	
            	
            	//closeSocket();
            	
            	
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
                
                window.location = "http://localhost:8080/PS2Projekt/widoktabeli.jsp?choose=" + strUser;
                //window.location = "http://kapustatest.azurewebsites.net/PS2Projekt/widoktabeli.jsp?choose=" + strUser;

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
            	var obj = new Object();
             	obj.dzialanie = "Pobierz";
            	var jsonString= JSON.stringify(obj);
            	writeResponse(jsonString);
            	webSocket.send(jsonString);
            	//webSocket.send(text);
            }
           
        </script>
       
    </body>
</html>