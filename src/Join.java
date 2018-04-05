import javax.swing.*;
import java.awt.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Join extends JFrame{

    private byte[] sendBuffer;

    private DatagramPacket packet;
    private DatagramSocket socket;

    public Join(){

        super("Join");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Waiting waiting = new Waiting();
        add(waiting, BorderLayout.CENTER);

        sendGameRequest();

        PackageThread pThread = new PackageThread(this, socket) {
            @Override
            public void handleConnection() throws Exception {
                colors.interrupt();
                Game.OPPONENT = new String(receivedBuffer).trim();
                BattleGround bg = new BattleGround(packet.getAddress(), packet.getPort(), socket);
                dispose();
                bg.setVisible(true);
            }
        };

        pThread.createAndStartColorThread();
        pThread.createAndStartListenThread();
        pThread.createAndStartIsAliveThread();

    }

    private void sendGameRequest(){
        try {
            socket = new DatagramSocket();
            String msg = "I want to play-" + Game.ME;
            sendBuffer = msg.getBytes();
            packet = new DatagramPacket(sendBuffer, msg.length(), InetAddress.getByName("255.255.255.255"), 8080);
            socket.send(packet);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
