package netty.tomcat.bio.servlet;

import netty.tomcat.bio.http.GPRequest;
import netty.tomcat.bio.http.GPResponse;
import netty.tomcat.bio.http.GPServlet;

public class SecondServlet extends GPServlet {


    public void doGet(GPRequest request, GPResponse response) {
        this.doPost(request, response);
    }

    public void doPost(GPRequest request, GPResponse response) {
        System.out.println("This is second servlet");
        response.write("This is second servlet");
    }
}
