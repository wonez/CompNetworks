import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BattleGround extends JFrame implements ActionListener {

    private InetAddress ip;
    private int port;

    private JButton[] fields;
    private  static int counter = 5;
    private JLabel counterLabel;
    private JLabel textLabel;
    private JButton readyButton;
    private byte[] sendBuffer;

    private DatagramPacket packet;
    private DatagramSocket socket;

    private boolean opponentReady = false;

    public BattleGround(InetAddress ip, int port ,DatagramSocket socket) throws HeadlessException {

        super("BattleShip");
        setSize(500,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.ip = ip;
        this.port = port;
        this.socket = socket;

        fields = new JButton[20];

        JPanel bricks = new JPanel(new GridLayout(1,20));
        for(int i = 0 ;i < 20 ; i++){
            JButton btn = new JButton(i+"");
            btn.setFont(new Font("Arial",Font.PLAIN,-1));
            btn.setBackground(Color.CYAN);
            btn.addActionListener(this);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            fields[i] = btn;
            bricks.add(btn);
        }
        JPanel panel = new JPanel(new GridLayout(13,1));
        for(int i = 0; i<13;i++){
            if(i==6){
                panel.add(bricks);
            }
            else if(i == 2){
                textLabel = new JLabel("Position your ships");
                JPanel textPanel = new JPanel();
                textPanel.add(textLabel);
                panel.add(textPanel);
            }
            else if(i == 3){
                counterLabel = new JLabel("Ships : " + counter);
                JPanel countPanel = new JPanel();
                countPanel.add(counterLabel);
                panel.add(countPanel);
            }
            else if(i==9){
                 readyButton = new JButton("I am ready");
                 JPanel buttonPanel = new JPanel();
                 readyButton.setVisible(false);
                 readyButton.addActionListener(this);
                 buttonPanel.add(readyButton,BorderLayout.CENTER);
                 panel.add(buttonPanel);
            }
            else{
                panel.add(new JPanel());
            }
        }
        this.add(panel,BorderLayout.CENTER);

        PackageThread pthread = new PackageThread(this,socket) {
            @Override
            public void handleConnection() throws Exception {

                String msg = new String(receivedBuffer);
                if(msg.contains("I am Ready")){
                    textLabel.setText(Game.OPPONENT + " is ready");
                    opponentReady = true;
                }
                if(msg.contains("I am also Ready")){
                    dispose();
                    BattleField bf = new BattleField(ip, port, fields, socket);
                    bf.setLocationRelativeTo(null);
                    bf.setVisible(true);
                }
            }
        };

        pthread.createAndStartListenThread();
        pthread.createAndStartIsAliveThread();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("I am ready")){
            sendPacket();
        }
        if(counter > 0 && addShip(e.getActionCommand())) {
            counter--;
            counterLabel.setText("Ships :" + counter);

        }
       if(counter==0){
            readyButton.setVisible(true);
        }
    }

    private boolean addShip(String text){
        int pos  = 20;
        try{
            pos = Integer.parseInt(text);

        }
        catch (Exception e){
            return false;
        }

        if(pos + 1 > 19 || fields[pos + 1].getText().contains("S"))return false;

        fields[pos].setBackground(Color.GRAY);
        fields[pos+1].setBackground(Color.GRAY);
        fields[pos].setText(pos + "S");
        fields[pos+1].setText((pos+1) + "S");

        return true;
    }

    private void sendPacket(){

        String msg = "I am Ready";

        if(opponentReady){
            msg = "I am also Ready";
        }else{
            textLabel.setText("Waiting on " + Game.OPPONENT);
            readyButton.setEnabled(false);
        }

        sendBuffer = msg.getBytes();
        packet = new DatagramPacket(sendBuffer, msg.length(), ip, port);

        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(opponentReady) {
            dispose();
            BattleField bf = new BattleField(ip, port, fields, socket);
            bf.setVisible(true);
        }
    }
}