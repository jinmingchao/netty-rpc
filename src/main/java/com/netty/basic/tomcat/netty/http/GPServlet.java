package netty.tomcat.netty.http;

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
