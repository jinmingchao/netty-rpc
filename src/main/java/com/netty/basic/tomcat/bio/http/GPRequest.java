package netty.tomcat.bio.http;

import java.io.IOException;
import java.io.InputStream;

public class GPRequest {

    private String method;
    private String url;

    public GPRequest(InputStream ins) {

          String content = "";
          int len = 0;
          byte[] buffer = new byte[1024];

          try {
             //TODO: while会卡住，研究一下。
             if ((len = ins.read(buffer)) > 0){
                 System.out.println("THE len IS: " + len);
             }
             content = new String(buffer,0,len);
             System.out.println("THE LENGTH IS: " + content.length());
             System.out.println("THE CONTENT IS: ");
             System.out.println(content);

             //取第一行
              String firstLine = content.split("\\n")[0];
              if(firstLine.length() > 0) {
                  this.method = firstLine.split("\\s")[0];
                  this.url = firstLine.split("\\s")[1].split("\\?")[0]; // \\?是去掉url的参数
              }
          } catch (IOException e) {
                e.printStackTrace();
          }

    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }
}
