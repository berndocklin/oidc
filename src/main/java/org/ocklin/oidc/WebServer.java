package org.ocklin.oidc;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;

/*
 * Web server, only used to demonstrate the functionality of the 
 * actual authorization flows.
 */
public class WebServer implements Runnable {

    public WebServer() {
    }

    public void simple() throws Exception {
        System.out.println("Create server at port " + 5443);

        HttpServer server = HttpServer.create(new InetSocketAddress(5443), 0);

        server.createContext("/login", new LoginHandler());
        server.createContext("/callback", new CallbackHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    @Override
    public void run() {
        try {
            simple();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void main(String args[]){  
        Thread t = new Thread(new WebServer());
        t.start();
        try {
            t.join();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }  

}