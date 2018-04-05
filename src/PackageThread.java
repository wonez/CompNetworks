import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

abstract public class PackageThread {

    public JFrame parent;

    public byte[] receivedBuffer;
    public byte[] sendBuffer;

    public DatagramPacket packet;
    public DatagramSocket socket;

    public Thread listenHost;
    public Thread isAliveHost;
    public Thread colors;

    public PackageThread(JFrame parent, DatagramSocket socket){
        this.socket =  socket;
        this.parent = parent;
    }

    public void createAndStartListenThread(){
        receivedBuffer = new byte[1024];
        packet = new DatagramPacket(receivedBuffer, receivedBuffer.length);
        listenHost = new Thread( () -> {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        listenHost.start();
    }

    public void createAndStartIsAliveThread(){
        isAliveHost = new Thread( () -> {
            try{
                while(true) {
                    if(!listenHost.isAlive()) {
                        break;
                    }
                }
                handleConnection();

            } catch (Exception e){
                e.printStackTrace();
            }
        });
        isAliveHost.start();
    }

    public void createAndStartColorThread(){
        colors = new Thread( () -> {
            try{
                while(true) {
                    Waiting.colorCount++;
                    parent.repaint();
                    Thread.sleep(500);
                }
            } catch (InterruptedException e){}
        });
        colors.start();
    }

    public abstract void handleConnection() throws Exception;
//    {
//        String msg = new String(receivedBuffer);
//        if(msg.contains("I want to play")){
//
//            Game.OPPONENT = msg.substring(msg.indexOf('-') + 1).trim();
//            sendBuffer = Game.ME.getBytes();
//            BattleGround bg = new BattleGround(packet.getAddress(), packet.getPort(), socket);
//
//            packet = new DatagramPacket(sendBuffer, Game.ME.length(), packet.getAddress(), packet.getPort());
//            socket.send(packet);
//            // TODO: 04/04/2018
//            colors.interrupt();
//            dispose();
//            bg.setVisible(true);
//        }
//    }

}
