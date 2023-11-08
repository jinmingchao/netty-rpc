package netty.tomcat.bio.http;

import java.io.IOException;
import java.io.OutputStream;

public class GPResponse {

    private OutputStream ops;

    public GPResponse(OutputStream ops) {
        this.ops = ops;
    }

    public void write(String msg) {
        //输出要遵循协议
        try {
            StringBuilder outPutMsg = new StringBuilder();
            outPutMsg.append("HTTP/1.1 200 OK");
            outPutMsg.append("\n");
            outPutMsg.append("Content-Type: text/html");
            outPutMsg.append("\n\n");
            outPutMsg.append(msg);

            ops.write(outPutMsg.toString().getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
//        finally {
//            if(ops != null) {
//                try {
//                    ops.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }
}
