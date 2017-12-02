<!DOCTYPE html>
 
<html>
    <head>
        <title>Echo Chamber</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width">
          <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
			<link rel="stylesheet" href="https://cdn.datatables.net/1.10.16/css/dataTables.bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/squel/5.12.0/squel.min.js"></script>
  <script src=https://code.jquery.com/jquery-1.12.4.js></script>
  <script src=https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js></script>
  <script src="//cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.1/bootstrap-table.min.js"></script>
  <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.1/bootstrap-table.min.css">
	  <script src=https://cdn.datatables.net/1.10.16/js/dataTables.bootstrap.min.js></script>
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
                //writeResponse(event.data);
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
			
        	//writeResponse(jsonString + "Dziala xDDdD");
            webSocket.send(jsonString);
        	//writeResponse("POSZLO");
        	
        	
        }
        window.onload = function(){
        	openSocket();
        	//var obj = poprosTabele();
        	//webSocket.send(obj);
			
        	//poprosTabele();
        }
        var pomocniczeDodawanie;
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
        	
            <button type="button" onclick="naBootstrap();" >PRZEROB</button>
            <button type="button" onclick="naGlowna();" >Wroc na glowna</button>
            <button type="button" onclick="dodajWpis();" >DODAJ</button>
            <button type="button" onclick="usunWpis();" >USUN</button>
            <button type="button" onclick="edytujWpis();" >EDYTUJ</button>

<div id="tabela">
<!-- TU BEDA TABELE NARYSOWANE -->
</div>
            
        </div>
        <!-- Server responses get written here -->
        <div id="messages"></div>
        
        <div id="showData">
        <table id="tabBOOT">
        </table>
        </div>
       
        <!-- Script to utilise the WebSocket -->
        <script type="text/javascript">
        
        var globalUpdatePowrot;
        var globalInsertPowrot;
                       
            var webSocket;
            var messages = document.getElementById("messages");
           
           
            
           
            /**
             * Sends the value of the text input to the server
             */
            function send(){
                //var text = document.getElementById("messageinput").value;
                webSocket.send(text);
                
                
            }
            
            function edytujWpis(){
            	var nazwaTabeli = location.search.split('choose=')[1];
            	var co = prompt("Co chcesz edytowac?", "Wpisz");
            	var na = prompt("Na co chcesz zmienic?", "Wpisz");
            	var indeks = prompt("Podaj Indeks", "Wpisz");
            	var gdzie = prompt("Podaj obecna wartosc edytowanego pola", "Wpisz");
            	//alert(squel.update().table(nazwaTabeli).set(co,na).where(co+"="+gdzie).toString());
            	
            	var zapytanieEdytuj = squel.update().table(nazwaTabeli).set(co,na).where(co+"='"+gdzie+"'").toString();
            	//alert(zapytanieEdytuj);
            	
            	var objEdytuj = new Object();
            	objEdytuj.dzialanie = "Edytuj";
            	objEdytuj.zapytanie = zapytanieEdytuj;
            	objEdytuj.tabela = nazwaTabeli;
            	var jsonEdytuj= JSON.stringify(objEdytuj);
    			
            	//writeResponse(jsonString + "Dziala xDDdD");
                webSocket.send(jsonEdytuj);
            	//alert("POSZLO");
            
            }
            
            function usunWpis(){
            	var nazwaTabeli = location.search.split('choose=')[1];
            	var co = prompt("Podaj nazwe kolumny z ID w nazwie", "Wpisz");
            	var wartoscID = prompt("Podaj ID", "Wpisz");
            	var zapytanieUsun = squel.remove().from(nazwaTabeli).where(co+"="+wartoscID).toString();
            	
            	var objUsun = new Object();
            	objUsun.dzialanie = "Usun";
            	objUsun.zapytanie = zapytanieUsun;
            	objUsun.tabela = nazwaTabeli;
            	var jsonUsun = JSON.stringify(objUsun);
            	
            	webSocket.send(jsonUsun);
            	
            }
            
            function dodajWpis(event){
            	var nazwaTabeli = location.search.split('choose=')[1];
            	///////////////////////////////////////////
            	var myBooks = pomocniczeDodawanie;
            	var col = [];
            	var pomocnicza = [];
            	var testJson = {};
            
		        for (var i = 0; i < myBooks.length; i++) {
		            for (var key in myBooks[i]) {
		                if (col.indexOf(key) === -1) {
		                    col.push(key);
		                    //var co[i] = prompt(key, "Wpisz");
		                    pomocnicza = prompt("Podaj "+key.toString(),"Wpisz");
		                    testJson[key.toString()] = pomocnicza;
		                    
		                }
		            }
		        }
		        testJson.dzialanie = "Dodaj";
		        testJson.tabela = nazwaTabeli;
		        var jsonDodaj = JSON.parse(JSON.stringify(testJson));
		        var jsonDodaj2 = JSON.stringify(jsonDodaj);
		        console.log(jsonDodaj);
		        
		        webSocket.send(jsonDodaj2);
		                    	
            }
            
            function obsluga(event){
            	//alert(event.data);
            	            	
            	var json = JSON.parse(event.data);
            	//writeResponse(json.dzialanie);
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
            		
            		//writeResponse("dupa");
            		pomocniczeDodawanie = json.Tabela;
            		var myBooks = json.Tabela;

            		$('#tabBOOT').bootstrapTable({
            		    data: myBooks
            		});
            		
            		console.log(myBooks);
            		
            		//////////////////
            		//TEST BOOSTRAP
            		
            		
            		
            		/////////////////
            		//wypisuje dobrze do loga, ale nie mozna wystwietlic na stronie bo pokazuje object
            		//tworzymy tabele
            		/*    var col = [];
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
				        table.setAttribute("id", "table");*/
				        
				        

            		
            		
            	}
            	if(json.dzialanie=="zapytanieWykonane"){
            		alert("UPDATE SUCCEDED");
                    globalUpdatePowrot = json.ostatnieZapytanie;
                    
            		
            	}
            	if(json.dzialanie == "zapytanieWykonaneTabela"){
            		globalInsertPowrot = json.ostatnieZapytanie;
            		
            		//writeResponse("dupa");
            		pomocniczeDodawanie = json.Tabela;
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
				        table.setAttribute("id", "table");	
				        
            	}
            	
            }
            
            function naBootstrap(){
		        $(document).ready(function() {
		            $('#table').DataTable();
		        } );
            }
            
            function naGlowna(){
            	var i = 1;
            	if(globalUpdatePowrot != null){
            		var strUser = globalUpdatePowrot;
                    
            	}
            	else if(globalInsertPowrot != null){
            		var strUser = globalInsertPowrot;
            	}

                closeSocket();
                
                
                window.location = "http://localhost:8080/PS2Projekt/index.jsp?choose="+strUser;


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