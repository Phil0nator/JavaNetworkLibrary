package in.Kaulk.NetLib.http;


import in.Kaulk.NetLib.Client;
import in.Kaulk.NetLib.Server;
import in.Kaulk.NetLib.ServerAction;
import in.Kaulk.NetLib.util.Events.ErrorEvent;
import in.Kaulk.NetLib.util.Logging.LoggingMode;

import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;

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


public class HTTPServer {

    public Server subServer;

    private String rootPath;


    private HTTPResopnse otherMethods = new DefaultNonExceptableMethodResponse();

    public HTTPServer(int port, int maxClients, String rootPath){
        this.rootPath = rootPath;
        subServer = new Server(port,maxClients);
        subServer.setupLogger(null,LoggingMode.off);
    }

    public void startup(){
        subServer.startAcceptingClients();
        subServer.doToEachClient(new HandleNormalRequests(this));
    }



    private class HandleNormalRequests implements ServerAction{
        HTTPServer parent;
        public HandleNormalRequests(HTTPServer parent){
            this.parent=parent;
        }

        private String getContentType(String s){
            if(s.endsWith(".htm") || s.endsWith(".html")){
                return "text/html";
            }
            return "text/plain";
        }

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

        public void run(Client c) {

            try {
                String msg = c.readHTTPRequest();
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
                }catch (Exception e){
                    parent.subServer.feedLoggingEvent(new ErrorEvent(e));
                }
            } catch (Exception e) {
                parent.subServer.feedLoggingEvent(new ErrorEvent(e));
            }
        }




    }


}
