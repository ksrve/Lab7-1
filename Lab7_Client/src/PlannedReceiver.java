import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class PlannedReceiver {

    private DatagramSocket socket;
    private MessageSender sender;

    public PlannedReceiver(DatagramSocket socket, MessageSender sender) {
        this.socket = socket;
        try {
            this.socket.setSoTimeout(1000000000);
        } catch (SocketException e) {
            System.out.println("SocketTime is too little");
        }
        this.sender = sender;
    }

    public Response listenServer() {
        byte[] data = new byte[8192];

        DatagramPacket dp = new DatagramPacket(data, data.length);
        try {
            socket.receive(dp);
        } catch (SocketTimeoutException e) {
            boolean connected = false;

            for (int i = 1; i < 11; i++) {
                System.out.println("Attempt to re-establish connectivity №" + i);
                sender.sendCommand("connecting", "");
                try {
                    socket.receive(dp);
                    connected = true;
                    break;
                } catch (IOException ex) {
                    continue;
                }
            }
            if (connected) {
                System.out.println("Connection re-established");
                return null;
            } else {
                System.out.println("Connection wasn't re-established");
                System.exit(0);
            }
        } catch (IOException e) {
            System.err.println("Connection wasn't re-established:(. Sorry...");
        }
        return MessageReceiver.decodeResponseObject(dp.getData());
    }
}
