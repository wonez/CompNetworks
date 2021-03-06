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
            } catch (Exception e) {
                System.out.println("Already listening on this socket, this happens if game is run on the same pc");
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
}
