import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BattleGround extends JFrame implements ActionListener{

    private InetAddress ip;
    private int port;
    private DatagramSocket socket;

    private int ships = 5;
    private JLabel counterLabel;
    private JButton fields[];
    private JButton readyButton;

    private Thread listenJoin;


    public BattleGround(InetAddress ip, int port, DatagramSocket socket) throws HeadlessException {
        super("BattleShip");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.ip = ip;
        this.port = port;
        this.socket = socket;

        fields = new JButton[20];

        JPanel bricks = new JPanel(new GridLayout(1, 20));
        for(int i=0; i<20;i ++){
            JButton btn = new JButton(i + "");
            btn.setFont(new Font("Arial", Font.PLAIN, 0));
            btn.addActionListener(this);
            btn.setBackground(Color.BLUE);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            fields[i] = btn;
            bricks.add(btn);
        }

        JPanel panel = new JPanel(new GridLayout(13, 1));

        for(int i=0; i<13; i++){
            if(i == 6){
                panel.add(bricks);
            }else if(i == 2){
                JPanel jp = new JPanel();
                jp.add(new JLabel("Position your ships"), BorderLayout.CENTER);
                panel.add(jp);
            }else if(i == 3){
                JPanel jp = new JPanel();
                counterLabel = new JLabel("Ships: " + ships);
                jp.add(counterLabel, BorderLayout.CENTER);
                panel.add(jp);
            }else if(i == 9){
                JPanel jp = new JPanel();
                readyButton = new JButton("I am ready");
                readyButton.setVisible(false);
                readyButton.addActionListener(this);
                jp.add(readyButton, BorderLayout.CENTER);
                panel.add(jp);
            }else{
                panel.add(new JPanel());
            }
        }
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getActionCommand().equals("I am ready")){

            sendPacket();

        }else if(ships > 0 && addShip(e.getActionCommand())) {
            ships--;
            counterLabel.setText("Ships " + ships);
        }

        if(ships == 0){
            readyButton.setVisible(true);
        }
    }

    private boolean addShip(String text){

        int pos = 20;
        try{
            pos = Integer.parseInt(text);
        }catch (Exception e){
            return false;
        }

        if(pos +1 > 19 || fields[pos+1].getText().contains("S")) return false;

        fields[pos].setBackground(Color.GRAY);
        fields[pos + 1].setBackground(Color.GRAY);

        fields[pos].setText(pos + "S");
        fields[pos + 1].setText((pos + 1)  + "S");

        return true;
    }

    private void sendPacket(){

    }

    private void createAndStartListenThread(){

        byte[] receivedBuffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(receivedBuffer, receivedBuffer.length);

        listenJoin = new Thread( () -> {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        listenJoin.start();
    }

    private void createAndStartIsAliveThread(){
        Thread isAliveJoin = new Thread( () -> {
            try{
                while(true) {
                    if(!listenJoin.isAlive()) break;
                }
                handleConnection();
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        isAliveJoin.start();
    }

    private void handleConnection() throws Exception    {
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
    }
}
