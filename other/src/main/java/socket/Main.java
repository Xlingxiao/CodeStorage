package socket;

import java.io.*;
import java.net.Socket;

public class Main {

    private static Socket client;
    private static OutputStream os;
    private static InputStream is;

    public static void main(String[] args) throws IOException {
        System.out.println(0x20);
        String ip = "101.101.101.114";
        int port = 86;
        client = new Socket(ip, port);
        os = client.getOutputStream();
        is = client.getInputStream();
        String msg = "GET / HTTP/1.1\n" +
                "Host: 101.101.101.114:86\n" +
                "Cache-Control: no-cache\n" +
                "Postman-Token: 94b1eb47-4ead-9a57-1731-c0796824e7f5\n";
        send(msg);
        String reMsg = received();
        System.out.println(reMsg);
        os.close();
    }

    static void send(String msg) throws IOException {
        os.write(msg.getBytes());
        os.flush();
        client.shutdownOutput();
    }

    static String received() throws IOException {
        String tmp;
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        while ((tmp = bf.readLine()) != null) {
            sb.append(tmp).append('\n');
        }

        return sb.toString();
    }
}
