package netty.tomcat.netty.servlet;

import netty.tomcat.netty.http.GPServlet;
import netty.tomcat.netty.http.GPRequest;
import netty.tomcat.netty.http.GPResponse;


public class ThirdServlet extends GPServlet {


    public void doGet(GPRequest request, GPResponse response) {
        this.doPost(request, response);
    }

    public void doPost(GPRequest request, GPResponse response) {
        System.out.println("This is third servlet");
        response.write("This is third servlet");
    }
}
