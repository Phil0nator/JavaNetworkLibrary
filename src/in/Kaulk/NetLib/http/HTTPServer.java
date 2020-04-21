package in.Kaulk.NetLib.http;


import in.Kaulk.NetLib.Client;
import in.Kaulk.NetLib.Server;
import in.Kaulk.NetLib.ServerAction;
import in.Kaulk.NetLib.util.Events.ErrorEvent;
import in.Kaulk.NetLib.util.Events.HTTPRequestEvent;
import in.Kaulk.NetLib.util.Logging.LoggingMode;

import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;


/**
 * Default HTTPResponse for unknown methods (not GET or HEAD)
 * @see HTTPResopnse
 * @see HTTPServer#overrrideHTTPResponse(HTTPResopnse)  to make your own
 */
class DefaultNonExceptableMethodResponse implements HTTPResopnse{

    private String methodNotSupportedResponse = "<p>HTTP Method not supported </p>";

    public void run(Client c, String msg){
        try{

            PrintWriter writer = new PrintWriter(c.getOutputStream());
            BufferedOutputStream dataOut = new BufferedOutputStream(c.getOutputStream());
            int fileLength = (int) methodNotSupportedResponse.length();
            String contentMimeType = "text/html";

            writer.println("HTTP/1.1 501 Not Implemented");
            writer.println("Server: Java HTTP Server from SSaurel : 1.0");
            writer.println("Date: " + new Date());
            writer.println("Content-type: " + contentMimeType);
            writer.println("Content-length: " + fileLength);
            writer.println(); // blank line between headers and content, very important !
            writer.flush(); // flush character output stream buffer
            // file
            dataOut.write(methodNotSupportedResponse.getBytes(), 0, fileLength);
            dataOut.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

/**
 * A contained class for a standard HTTP Server
 * (Does not support HTTPS)
 * Note: To support HTTP functions other that GET and HEAD, you will need to implement your own HTTPResponse
 * Type.
 * @see HTTPResopnse
 * Default example:
 * @see DefaultNonExceptableMethodResponse
 * Method:
 * @see HTTPServer#overrrideHTTPResponse(HTTPResopnse)
 */
public class HTTPServer {

    /**
     * Server object for lower level client interaction.
     * The server's scope is public so that you may use any method
     * you normally could for a server object
     * @see Server
     */
    public Server subServer;

    /**
     * The root directory of html/server files
     */
    private String rootPath;

    /**
     * The default response for methods other than GET or HEAD
     * @see HTTPResopnse
     * @see DefaultNonExceptableMethodResponse
     */
    private HTTPResopnse otherMethods = new DefaultNonExceptableMethodResponse();

    /**
     * Constructor
     * @param port port to bind to
     * @param maxClients maximum number of clients at one time
     * @param rootPath root director of server files
     * @see Server#Server(int, int)
     * @see Server#setupLogger(File, LoggingMode)
     */
    public HTTPServer(int port, int maxClients, String rootPath){
        this.rootPath = rootPath;
        subServer = new Server(port,maxClients);
        subServer.setupLogger(new File("log.txt"),LoggingMode.HTTP_Standard);
    }

    /**
     * Initiate the server, once all other properties have been set up
     * @see Server
     */
    public void startup(){
        subServer.startAcceptingClients();
        subServer.doToEachClient(new HandleNormalRequests(this));


    }

    /**
     * Used to override the HTTP response for methods other than GET or HEAD
     * @param n new HTTPResponse object
     * @see HTTPResopnse
     * @see HTTPServer#otherMethods
     */
    public void overrrideHTTPResponse(HTTPResopnse n){
        if(n==null)return;
        otherMethods = n;
    }

    /**
     * Subclass ServerAction for the subServer object to act with
     * @see ServerAction
     * @see Server#doToEachClient(ServerAction)
     */
    final class HandleNormalRequests implements ServerAction{
        HTTPServer parent;
        public HandleNormalRequests(HTTPServer parent){
            this.parent=parent;
        }

        /**
         * Get the type of document being requested
         * @param s message from client
         * @return doctype
         */
        private String getContentType(String s){
            if(s.endsWith(".htm") || s.endsWith(".html")){
                return "text/html";
            }else if (s.endsWith(".css")){
                return "text/css";
            }
            return "text/plain";
        }

        /**
         * Extract byte array from file
         * @param file file to be read
         * @param len length to be read
         * @return byte array of file contents
         * @throws IOException during file reading
         */
        private byte[] readFileData(File file, int len) throws IOException {
            FileInputStream fileInputStream = null;
            byte[] fileData = new byte[len];

            try {
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(fileData);
            } finally {
                if (fileInputStream != null)
                    fileInputStream.close();
            }

            return fileData;
        }

        /**
         * Main content
         * @param c relevant Client object
         */
        public void run(Client c) {
            String msg = c.readHTTPRequest();

            try {
                PrintWriter writer = new PrintWriter(c.getOutputStream());
                BufferedOutputStream dataOut = new BufferedOutputStream(c.getOutputStream());
                if (msg != null) {

                    StringTokenizer parse = new StringTokenizer(msg);
                    String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
                    // we get file requested
                    String fileRequested = parse.nextToken().toLowerCase();

                    // we support only GET and HEAD methods, we check
                    if (!method.equals("GET") && !method.equals("HEAD")) {
                        if (otherMethods != null) {
                            otherMethods.run(c,msg);
                            return;
                        }
                    } else {
                        if (fileRequested.endsWith("/")) {
                            fileRequested += "index.html";
                        }

                        File file = new File(parent.rootPath, fileRequested);
                        int fileLength = (int) file.length();
                        String content = getContentType(fileRequested);

                        if (method.equals("GET")) {
                            byte[] fileData = readFileData(file, fileLength);

                            writer.println("HTTP/1.1 200 OK");
                            writer.println("Server: Java HTTP Server in.Kaulk.NetLib : .01-alpha");
                            writer.println("Date: " + new Date());
                            writer.println("Content-type: " + content);
                            writer.println("Content-length: " + fileLength);
                            writer.println(); // blank line between headers and content!
                            writer.flush(); // flush character output stream buffer

                            dataOut.write(fileData, 0, fileLength);
                            dataOut.flush();

                            parent.subServer.feedLoggingEvent(new HTTPRequestEvent(c,msg,200));//logging

                        }


                    }
                }

            } catch (FileNotFoundException fnfe) {
                try {
                    PrintWriter writer = new PrintWriter(c.getOutputStream());
                    BufferedOutputStream dataOut = new BufferedOutputStream(c.getOutputStream());
                    writer.println("HTTP/1.1 404 File Not Found");
                    writer.println("Server: Java HTTP Server in.Kaulk.NetLib : .01-alpha");
                    writer.println("Date: " + new Date());
                    writer.println("Content-type: " + "");
                    writer.println("Content-length: " + "0");
                    writer.println(); // blank line between headers and content, very important !
                    writer.flush(); // flush character output stream buffer
                    parent.subServer.feedLoggingEvent(new HTTPRequestEvent(c,msg,404));
                }catch (Exception e){
                    parent.subServer.feedLoggingEvent(new ErrorEvent(e));
                }
            } catch (Exception e) {
                parent.subServer.feedLoggingEvent(new ErrorEvent(e));
            }
        }




    }


}
