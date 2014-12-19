<html>
<body>
<h2>Simple SSE Echo Demo</h2>

<script type="text/javascript">
function setupEventSource() {
	var output = document.getElementById("output");
	if (typeof(EventSource) !== "undefined") {
		var msg = document.getElementById("textID").value;
		var source = new EventSource("simplesse?msg="+msg);
		source.onmessage = function(event) {
			output.innerHTML += event.data+"<br>";
		}
	} else {
		output.innerHTML = "Sorry, Server-Sent Event is not supported by your browser.";
	}
	return false;
}
</script>
    <div>
      <input type="text" id="textID" name="message" value="Hello World">
      <input type="button" id="sendID" value="Send" onclick="setupEventSource()"/>
    </div>
    <hr/>
    <div id="output"></div>
</body>
</html>
