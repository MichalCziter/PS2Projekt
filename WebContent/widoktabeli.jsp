<!DOCTYPE html>

<html>
<head>
<title>Projekt PS2</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.16/css/dataTables.bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/squel/5.12.0/squel.min.js"></script>
<script src=https://code.jquery.com/jquery-1.12.4.js></script>
<script
	src=https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js></script>
<script
	src="//cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.1/bootstrap-table.min.js"></script>

<link rel="stylesheet"
	href="//cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.1/bootstrap-table.min.css">
<script
	src=https://cdn.datatables.net/1.10.16/js/dataTables.bootstrap.min.js></script>
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
	var content = document.createTextNode("Zalogowany jako:  " + obj.user_id);
	theDiv.appendChild(content);

}


	function openSocket() {
		// Ensures only one connection is open at a time

		if (webSocket !== undefined
				&& webSocket.readyState !== WebSocket.CLOSED) {
			console.log("WebSocket is already opened.");
			return;
		}
		// Create a new instance of the websocket
		webSocket = new WebSocket("wss://ps2projekt2017.azurewebsites.net/PS2Projekt/echo/roomnumber");
		//webSocket = new WebSocket("ws://localhost:8080/PS2Projekt/echo/roomnumber");

		console.log("Otwarlem sie WIDOKTABELI");
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
		};

		webSocket.onclose = function(event) {
			console.log("Connection closed");
		};

	}
	function poprosTabele() {

		var myParam = location.search.split('choose=')[1];
		console.log("HEHE " + myParam[0])
		if (myParam[0] === 'I') {

			return;

		} else {
			var text = myParam;

			var obj = new Object();
			obj.dzialanie = "Tabela";
			obj.tabela = text;
			var jsonString = JSON.stringify(obj);
			webSocket.send(jsonString);

		}

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

		}, 10); // wait 5 milisecond for the connection...
	}
	window.onload = function() {
		openSocket();

		waitForSocketConnection(webSocket, function() {

			poprosTabele();
		});

	}
	var pomocniczeDodawanie;
	var licznik = 1;
	var flagaReczna;

	window.onunload = function() {
		closeSocket();
	}
</script>
</head>
<body>

	<div id="login" style="font-size:180%">
        <a href="https://ps2projekt2017.azurewebsites.net/.auth/logout" class="btn btn-default" align="right">WYLOGUJ SIE</a>
    </div>
	<div id="container"><br/></div>
	<div>
		<!--  <button id = "1" type="button" onclick="poprosTabele();" >KLIKAJ TUTAJ</button> -->
		<button id="glownaButton" type="button" onclick="naGlowna();">Wroc
			na glowna</button>
		<button id="1" type="button" onclick="dodajWpis();">DODAJ</button>
		<button id="2" type="button" onclick="usunWpis();">USUN</button>
		<button id="3" type="button" onclick="edytujWpis();">EDYTUJ</button>
		


		<div id="tabela">
			<!-- TU BEDA TABELE NARYSOWANE -->
		</div>

	</div>
	<!-- Server responses get written here -->
	<div id="messages"></div>

	<div id="showData">
		<table id="tabBOOT" class="display" width="100%">

		</table>
	</div>

	<!-- Script to utilise the WebSocket -->
	<script type="text/javascript">
		var globalUpdatePowrot;
		var globalInsertPowrot;

		var webSocket;
		var messages = document.getElementById("messages");

		/*
		 * Sends the value of the text input to the server
		 */
		function send() {
			webSocket.send(text);

		}

		function edytujWpis() {
			var nazwaTabeli = location.search.split('choose=')[1];
			var co = prompt("Co chcesz edytowac?", "Wpisz");
			var na = prompt("Na co chcesz zmienic?", "Wpisz");
			var indeks = prompt("Podaj Indeks", "Wpisz");
			var gdzie = prompt("Podaj obecna wartosc edytowanego pola", "Wpisz");

			var zapytanieEdytuj = squel.update().table(nazwaTabeli).set(co, na)
					.where(co + "='" + gdzie + "'").toString();

			var objEdytuj = new Object();
			objEdytuj.dzialanie = "Edytuj";
			objEdytuj.zapytanie = zapytanieEdytuj;
			objEdytuj.tabela = nazwaTabeli;
			var jsonEdytuj = JSON.stringify(objEdytuj);

			webSocket.send(jsonEdytuj);
		}

		function usunWpis(event) {
			var nazwaTabeli = location.search.split('choose=')[1];
			var myTable = pomocniczeDodawanie;
			var col = [];
			var pomocnicza = [];
			var testJson = {};
			var flaga = 1;

			for (var i = 0; i < myTable.length; i++) {
				for ( var key in myTable[i]) {
					if (col.indexOf(key) === -1) {
						if (i == 0) {
							if (flaga == 1) {
								pomocnicza = key.toString();
								console.log(pomocnicza);
								flaga = 2;
							}
						}
					}
				}
			}

			var wartoscID = prompt("Podaj " + pomocnicza + " do usuniecia", "Wpisz");
			var zapytanieUsun = squel.remove().from(nazwaTabeli).where(pomocnicza + "=" + wartoscID).toString();

			var objUsun = new Object();
			objUsun.dzialanie = "Usun";
			objUsun.zapytanie = zapytanieUsun;
			objUsun.tabela = nazwaTabeli;
			var jsonUsun = JSON.stringify(objUsun);

			webSocket.send(jsonUsun);

		}

		function dodajWpis(event) {
			var nazwaTabeli = location.search.split('choose=')[1];
			var myTable = pomocniczeDodawanie;
			var col = [];
			var pomocnicza = [];
			var testJson = {};
			alert("Aby dodac rekord, nalezy podac wszystkie dane");

			for (var i = 0; i < myTable.length; i++) {
				for ( var key in myTable[i]) {
					if (col.indexOf(key) === -1) {
						col.push(key);
						pomocnicza = prompt("Podaj " + key.toString(), "Wpisz");
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

		function obsluga(event) {
			try {
				var json = JSON.parse(event.data);

			} catch (err) {
				console.log("Wystapil blad " + err);
				return;
			}

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
			if (json.dzialanie == "wysylamTabele") {

				var nazwaTabeli = location.search.split('choose=')[1];

				if (nazwaTabeli == json.NazwaTabeli) {
					console.log("serwer odpowiedzial");
					pomocniczeDodawanie = json.Tabela;
					var myTable = json.Tabela;

					var parenttbl = document.getElementById('tabBOOT');
					var a = myTable;
					if (licznik == 1) {

						var mojThead = parenttbl.createTHead();
						var mojTR = mojThead.insertRow(0);

						var col = [];
						for (var i = 0; i < myTable.length; i++) {
							for ( var key in myTable[i]) {
								if (col.indexOf(key) === -1) {
									col.push(key);
								}
							}
						}

						flagaReczna = col[0];

						for ( var i in a[0]) {

							var mojTH = document.createElement('th');
							mojTH.setAttribute("data-field", i);
							mojTH.setAttribute("data-sortable", 'true');
							mojTH.innerHTML = i;
							mojTR.appendChild(mojTH);

						}

						licznik = 2;
					}
					$('#tabBOOT').bootstrapTable({
						data : myTable
					});

					$('#tabBOOT').bootstrapTable("load", myTable);
				}

			}
			if (json.dzialanie == "sukces") {
				for (var i = 1; i < 4; i++) {
					var elem = document.getElementById(i.toString());
					elem.parentNode.removeChild(elem);

				}
				alert("UPDATE SUCCEDED");
				globalUpdatePowrot = json.ostatnieZapytanie;

			}
			if (json.dzialanie == "sukcesTabela") {
				for (var i = 1; i < 4; i++) {
					var elem = document.getElementById(i.toString());
					elem.parentNode.removeChild(elem);

				}

				pomocniczeDodawanie = json.Tabela;
				var myTable = json.Tabela;

				var parenttbl = document.getElementById('tabBOOT');
				var a = myTable;

				var col = [];
				for (var i = 0; i < myTable.length; i++) {
					for ( var key in myTable[i]) {
						if (col.indexOf(key) === -1) {
							col.push(key);
						}
					}
				}

				flagaReczna = col[0];

				if (flagaReczna == location.search.split('choose=')[1]) {
					globalInsertPowrot = json.ostatnieZapytanie;

					if (licznik == 1) {

						var mojThead = parenttbl.createTHead();
						var mojTR = mojThead.insertRow(0);

						for ( var i in a[0]) {

							var mojTH = document.createElement('th');
							mojTH.setAttribute("data-field", i);
							mojTH.setAttribute("data-sortable", 'true');
							mojTH.innerHTML = i;
							mojTR.appendChild(mojTH);

						}

						licznik = 2;
					}

					$('#tabBOOT').bootstrapTable({
						data : myTable
					});

					$('#tabBOOT').bootstrapTable("load", myTable);

				}

			}
			if (json.dzialanie == "blad") {
				var bladParametr = location.search.split('choose=')[1];
				if (bladParametr == json.kodBledu) {
					for (var i = 1; i < 4; i++) {
						var elem = document.getElementById(i.toString());
						elem.parentNode.removeChild(elem);

					}

					wypiszTekst("Tekst bledu:" + json.bladTekst);
					wypiszTekst("Kod bledu: " + json.kodBledu);
				} else
					return;

			}

		}

		function naBootstrap() {
			$(document).ready(function() {
				$('#table').DataTable();
			});
		}

		function naGlowna() {
			var i = 1;
			if (globalUpdatePowrot != null) {
				var strUser = globalUpdatePowrot;

			} else if (globalInsertPowrot != null) {
				var strUser = globalInsertPowrot;
			}
			else {
				
				window.location = "https://ps2projekt2017.azurewebsites.net/PS2Projekt/index.jsp";
				//window.location = "http://localhost:8080/PS2Projekt/index.jsp";
				
			}

			

			window.location = "https://ps2projekt2017.azurewebsites.net/PS2Projekt/index.jsp?choose="
				+ strUser;
			//window.location = "http://localhost:8080/PS2Projekt/index.jsp?choose="+strUser;

		}

		function closeSocket() {
			webSocket.close();
		}
		
		function wypiszTekst(text) {
			messages.innerHTML += "<br />" + text;
		}

	</script>

</body>
</html>