package Progetto;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;

public class ServerREST {

    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServerFactory.create("http://"+HOST+":"+PORT+"/");
        server.start();
        System.out.println("Il Gateway è ora attivo!");
        System.out.println("è raggiungibile al seguente indirizzo: http://"+HOST+":"+PORT);
    }
}