import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Join extends JFrame{

    private byte[] sendBuffer;
    private byte[] receivedBuffer;

    private DatagramPacket packet;
    private DatagramSocket socket;

    private Thread listenJoin;
    private Thread isAliveJoin;
    private Thread colors;

    public Join(){

        super("Join");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Waiting waiting = new Waiting();
        add(waiting, BorderLayout.CENTER);

        try {
            socket = new DatagramSocket();
            String msg = "I want to play-" + Game.ME;
            sendBuffer = msg.getBytes();
            packet = new DatagramPacket(sendBuffer, msg.length(), InetAddress.getByName("255.255.255.255"), 8080);
            socket.send(packet);
        }catch (Exception e){
            e.printStackTrace();
        }

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
//
//    private void createAndStartListenThread(){
//
//        receivedBuffer = new byte[1024];
//        packet = new DatagramPacket(receivedBuffer, receivedBuffer.length);
//
//        listenJoin = new Thread( () -> {
//            try {
//                socket.receive(packet);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        listenJoin.start();
//    }

//    private void createAndStartIsAliveThread(){
//        isAliveJoin = new Thread( () -> {
//            try{
//                while(true) {
//                    if(!listenJoin.isAlive()) break;
//                }
//                handleConnection();
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        });
//        isAliveJoin.start();
//    }

//    private void handleConnection(){
//
//        colors.interrupt();
//        Game.OPPONENT = new String(receivedBuffer).trim();
//        BattleGround bg = new BattleGround(packet.getAddress(), packet.getPort(), socket);
//        dispose();
//        bg.setVisible(true);
//    }

//    private void createAndStartColorThread(){
//        colors = new Thread( () -> {
//            try{
//                while(true) {
//                    Waiting.colorCount++;
//                    repaint();
//                    Thread.sleep(500);
//                }
//            } catch (InterruptedException e){}
//        });
//        colors.start();
//    }
}
