<!DOCTYPE html>

<html>
<head>
<title>Echo Chamber</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script type="text/javascript">
	function openSocket() {
		// Ensures only one connection is open at a time
		if (webSocket !== undefined
				&& webSocket.readyState !== WebSocket.CLOSED) {
			console.log("WebSocket is already opened.");
			return;
		}
		// Create a new instance of the websocket
		//webSocket = new WebSocket("ws://localhost:8080/PS2Projekt/echo/roomnumber");
		webSocket = new WebSocket("wss://ps2projekt2017.azurewebsites.net/PS2Projekt/echo/roomnumber");
		//webSocket = new WebSocket("ws://localhost:8080/PS2Projekt/echo/roomnumber");
		console.log("Otwarlem sie");
		/**
		 * Binds functions to the listeners for the websocket.
		 */
		webSocket.onopen = function(event) {
			// For reasons I can't determine, onopen gets called twice
			// and the first time event.data is undefined.
			// Leave a comment if you know the answer.
			if (event.data === undefined)
				return;

			console.log(event.data);
		};

		webSocket.onmessage = function(event) {
			obsluga(event);
			console.log(event.data);
		};

		webSocket.onclose = function(event) {
			console.log("Connection closed");
		};

	}

	function waitForSocketConnection(socket, callback) {
		setTimeout(function() {
			if (socket.readyState === 1) {
				console.log("Connection is made")
				if (callback != null) {
					callback();
				}
				return;

			} else {
				console.log("wait for connection...")
				waitForSocketConnection(socket, callback);
			}

		}, 5); // wait 5 milisecond for the connection...
	}

	window.onload = function() {

		openSocket();

		waitForSocketConnection(webSocket, function() {

			pobierzNazwy();
		});
	}

	window.onunload = function() {
		closeSocket();

	}
</script>
</head>
<body>

	<div id="login" style="font-size:180%">
        <a href="https://ps2projekt2017.azurewebsites.net/.auth/logout" class="btn btn-default" align="right">WYLOGUJ SIE</a>
	</div>
	<div> 
	<br/>
		SQL Command :<input type="text" size="70" id="sqlinput" />
		
		<button type="button" onclick="wyslijZapytanie();">Wyslij Zapytanie</button>
	
		
	</div>
	<div id="container" style="font-size:130%" > <br/></div>
	<div><br/>
		<button type="button" onclick="pokazZawartosc();">Pokaz Zawartosc tabeli</button>

		<div id="tabela">
			<!-- TU BEDA TABELE NARYSOWANE -->
		</div>

	</div>
	<!-- Server responses get written here -->
	<div id="messages"></div>

	<!-- Script to utilise the WebSocket -->
	<script type="text/javascript">
	
	$.ajax({
	    url: 'https://ps2projekt2017.azurewebsites.net/.auth/me',
	    type: 'GET',
	    dataType: "json",
	    success: displayAll
	});


	function displayAll(data){

		var obj = data[0];
		
		var theDiv = document.getElementById("login");
		var content = document.createTextNode("Zalogowano jako:  " + obj.user_id);
		theDiv.appendChild(content);

	}
	
		var licznikZapytan = 1;

		var domyslny = location.search.split('choose=')[1];
		domyslny = decodeURIComponent(domyslny);

		
		if (domyslny == "undefined") {
			document.getElementById('sqlinput').value = null;
		}
		
		else if (domyslny != null) {
			document.getElementById('sqlinput').value = domyslny;
		}

		var webSocket;
		var messages = document.getElementById("messages");

		/**
		 * Sends the value of the text input to the server
		 */
		function send() {
			webSocket.send(text);

		}

		function obsluga(event) {

			var json = JSON.parse(event.data);
			console.log(json.dzialanie);
			console.log(licznikZapytan);
			if (json.dzialanie == "tabele") {
				var div = document.querySelector("#container"), frag = document
						.createDocumentFragment(), select = document
						.createElement("select");

				select.id = "wybor";

				select.options.add(new Option(json.tabela0, "AU", true, true));
				select.options.add(new Option(json.tabela1, "FI"));

				frag.appendChild(select);
				div.appendChild(frag);

			}
			if (json.dzialanie == "blad") {
				if (licznikZapytan == 2) {
					window.location = "https://ps2projekt2017.azurewebsites.net/PS2Projekt/widoktabeli.jsp?choose="+ json.kodBledu;
					//window.location = "http://localhost:8080/PS2Projekt/widoktabeli.jsp?choose="+ json.kodBledu;

				}

			}
			if (json.dzialanie == "sukcesTabela") {
				if (licznikZapytan == 2) {
					window.location = "https://ps2projekt2017.azurewebsites.net/PS2Projekt/widoktabeli.jsp?choose="
							+ json.NazwaTabeli;
					//window.location = "http://localhost:8080/PS2Projekt/widoktabeli.jsp?choose="
					//	+ json.NazwaTabeli;

				} else
					return;

			}
			if (json.dzialanie == "sukces") {
				if (licznikZapytan == 2) {
					window.location = "https://ps2projekt2017.azurewebsites.net/PS2Projekt/widoktabeli.jsp?choose=I";
					//window.location = "http://localhost:8080/PS2Projekt/widoktabeli.jsp?choose=I"; 

				} else
					return;
			}

		}
		function wyslijZapytanie() {
			var e = document.getElementById("sqlinput").value;
			var objZapytanie = new Object();
			objZapytanie.dzialanie = "Zapytanie";
			objZapytanie.komenda = e;
			var jsonZapytanie = JSON.stringify(objZapytanie);
			licznikZapytan = 2;
			webSocket.send(jsonZapytanie);

		}

		function pokazZawartosc() {

			var e = document.getElementById("wybor");
			var strUser = e.options[e.selectedIndex].text;


			window.location = "https://ps2projekt2017.azurewebsites.net/PS2Projekt/widoktabeli.jsp?choose="
					+ strUser;
			//window.location = "http://localhost:8080/PS2Projekt/widoktabeli.jsp?choose=" + strUser;


			closeSocket();
		}

		function closeSocket() {
			webSocket.close();
		}


		function pobierzNazwy() {
			var text = "Pobierz"
			var obj = new Object();
			obj.dzialanie = "Pobierz";
			var jsonString = JSON.stringify(obj);
			console.log(jsonString);
			webSocket.send(jsonString);
		}
	</script>

</body>
</html>