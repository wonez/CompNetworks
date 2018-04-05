import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Host extends JFrame{

    private Thread colors;
    private Thread listenHost;
    private Thread isAliveHost;

    private DatagramSocket socket;
    private DatagramPacket packet;

    private byte sendBuffer[];
    private byte receivedBuffer[];

    public Host() {

        super("Host");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            socket = new DatagramSocket(8080);
        } catch (SocketException e) {
            e.printStackTrace();
        }


        Waiting waiting = new Waiting();
        add(waiting, BorderLayout.CENTER);

        PackageThread pThread = new PackageThread(this, socket) {
            @Override
            public void handleConnection() throws Exception{
                    String msg = new String(receivedBuffer);
                    if(msg.contains("I want to play")){

                        Game.OPPONENT = msg.substring(msg.indexOf('-') + 1).trim();
                        sendBuffer = Game.ME.getBytes();
                        BattleGround bg = new BattleGround(packet.getAddress(), packet.getPort(), socket);

                        packet = new DatagramPacket(sendBuffer, Game.ME.length(), packet.getAddress(), packet.getPort());
                        socket.send(packet);
                        // TODO: 04/04/2018
                        colors.interrupt();
                        dispose();
                        bg.setVisible(true);
                    }
            }
        };

        pThread.createAndStartColorThread();
        pThread.createAndStartListenThread();
        pThread.createAndStartIsAliveThread();

    }
//        private void createAndStartColorThread(){
//            colors = new Thread( () -> {
//                try{
//                    while(true) {
//                        Waiting.colorCount++;
//                        repaint();
//                        Thread.sleep(500);
//                    }
//                } catch (InterruptedException e){}
//            });
//            colors.start();
//        }
//        private void createAndStartListenThread(){
//            receivedBuffer = new byte[1024];
//            packet = new DatagramPacket(receivedBuffer, receivedBuffer.length);
//            listenHost = new Thread( () -> {
//                try {
//                    socket.receive(packet);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//            listenHost.start();
//        }

//        private void createAndStartIsAliveThread(){
//            isAliveHost = new Thread( () -> {
//                try{
//                    while(true) {
//                        if(!listenHost.isAlive()) {
//                            break;
//                        }
//                    }
//                    handleConnection();
//
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            });
//            isAliveHost.start();
//        }

//        private void handleConnection() throws Exception    {
//            String msg = new String(receivedBuffer);
//            if(msg.contains("I want to play")){
//
//                Game.OPPONENT = msg.substring(msg.indexOf('-') + 1).trim();
//                sendBuffer = Game.ME.getBytes();
//                BattleGround bg = new BattleGround(packet.getAddress(), packet.getPort(), socket);
//
//                packet = new DatagramPacket(sendBuffer, Game.ME.length(), packet.getAddress(), packet.getPort());
//                socket.send(packet);
//                // TODO: 04/04/2018
//                colors.interrupt();
//                dispose();
//                bg.setVisible(true);
//            }
//        }
}

class Waiting extends JPanel{

    public static int colorCount = 0;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString("Waiting for opponent", 70, 220);

        if (colorCount % 2 == 0) {
            g.setColor(Color.BLUE);
        } else {
            g.setColor(Color.RED);
        }
        g.drawString(".", 400, 220);
        g.drawString(".", 420, 220);

        if (colorCount % 2 == 0) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.BLUE);
        }
        g.drawString(".", 410, 220);
    }
}
