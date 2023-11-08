package netty.tomcat.bio;

import netty.tomcat.bio.GPTomcat;

public class App1 {
    public static void main(String[] args) {
        //System.out.println(14 << 20); //14MB

        new GPTomcat().start();

        //String a = "servlet.abc.do.do";
        //System.out.println(a.replaceAll("\\.do$",""));
    }


}
