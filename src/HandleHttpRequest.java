import java.io.* ;
import java.net.* ;
import java.util.* ;

final class HandleHttpRequest implements Runnable {
	final static String CRLF = "\r\n";
	Socket socket;

	// Constructor
	public HandleHttpRequest(Socket socket) throws Exception {
		this.socket = socket;
	}

	// Implement the run() method of the Runnable interface.
	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void processRequest() throws Exception {
		// Insert your own code here to:
		// Get the request line of the HTTP request message.
		// Extract the filename from the request line.
		// Hint: use java  StringTokenizer class; be careful about the file
		// directory

		//instantiate printwriter and bufferedreader for output, input respectively
		PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String inputLine = input.readLine();
		boolean found = false;
		String filename_full = null;
		
		while(inputLine != null && !found)
		{
			if(inputLine.contains("GET"))
			{
				String[] get_tokens = inputLine.split("[ ]");
				filename_full = get_tokens[1];
				found = true;
			}
		}
		
		String filename = filename_full.substring(filename_full.indexOf("/"));
		File file = new File(filename);
		boolean fileExists = false;
		if(file.exists() && !file.isDirectory())
			fileExists = true;
		Scanner scan = null;
		
		// Open the requested file.
		// Construct the header part of the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists) { // Check if request file exists locally
			statusLine = "HTTP/1.0 200 OK" + CRLF;
			contentTypeLine = "Content-Type: " + 
					filename + CRLF;
			scan = new Scanner(new File(filename));
			String line = null;
			if(scan.hasNextLine())
				entityBody = scan.nextLine() + "\n";
			while(scan.hasNextLine())
			{
				line = scan.nextLine();
				entityBody += line + "\n";
			}
		} else {
			statusLine = "HTTP/1.0 404 Not Found" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "<HTML>" + 
					"<HEAD><TITLE>Not Found</TITLE></HEAD>" +
					"<BODY>Not Found</BODY></HTML>";
		}

		// Insert your own code here to:
		// Send the status line, content type line, and a blank line to
		// indicate the end of the header lines.  Then, Send the entity body
		// and close socket.
		output.println(statusLine);
		output.println(contentTypeLine);
		output.println();
		output.println(entityBody);
		
		socket.close();
	}
}
