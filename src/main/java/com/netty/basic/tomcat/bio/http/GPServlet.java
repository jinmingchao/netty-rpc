package netty.tomcat.bio.http;

import netty.tomcat.bio.http.GPRequest;
import netty.tomcat.bio.http.GPResponse;

public abstract class GPServlet {

    public void service(GPRequest request, GPResponse response) {

        String method = request.getMethod();

        switch (method) {
            case "GET" : doGet(request, response);break;
            case "POST": doPost(request, response);break;
            default:
                System.out.println("Illegal Method Error");
        }

    }

    public abstract void doPost(GPRequest request, GPResponse response);

    public abstract void doGet(GPRequest request, GPResponse response);

}
