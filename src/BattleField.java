import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BattleField extends JFrame implements ActionListener{

    private InetAddress ip;
    private int port;

    private JButton[] fields;
    private JButton[] clickFields;

    private byte[] sendBuffer;

    private DatagramPacket packet;
    private DatagramSocket socket;

    private DatagramSocket endSocket;

    private JLabel textLabel;
    private String text;

    public BattleField(InetAddress ip, int port, JButton[] fields, DatagramSocket socket) {

        super("BattleShip");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);

        this.ip = ip;
        this.port = port;
        this.fields = fields;
        this.socket = socket;
        text = "";

        clickFields = new JButton[20];

        JPanel bricks = new JPanel(new GridLayout(1,20));
        for(int i = 0 ;i < 20 ; i++){
            JButton btn = new JButton(i+"");
            btn.setFont(new Font("Arial",Font.PLAIN,-1));
            btn.setBackground(Color.CYAN);
            btn.addActionListener(this);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            clickFields[i] = btn;
            bricks.add(btn);
        }

        JPanel panel = new JPanel(new GridLayout(13,1));
        for(int i = 0; i<13;i++){
            if(i==6){
                panel.add(bricks);
            }else if(i == 2){
                textLabel = new JLabel(text);
                JPanel textPanel = new JPanel();
                textPanel.add(textLabel);
                panel.add(textPanel);
            }else{
                panel.add(new JPanel());
            }
        }

        try {
            endSocket = new DatagramSocket(8888);
        }catch (Exception e){
            System.out.println("Socket already created");
        }
        PackageThread pt = new PackageThread(this, endSocket) {
            @Override
            public void handleConnection() throws Exception {
                String msg = new String(this.receivedBuffer);
                if(msg.contains("Lost")){
                    parent.dispose();
                    End end = new End("Lost");
                    end.setLocationRelativeTo(null);
                    end.setVisible(true);
                    //end of the game
                }
            }
        };
        pt.createAndStartListenThread();
        pt.createAndStartIsAliveThread();

        handleTurns();
        this.add(panel,BorderLayout.CENTER);
    }

    public void handleTurns(){

        if(Game.TYPE % 2 == 1){
            text = "Opponents Turn";
            textLabel.setText(text);
            for(JButton jb : clickFields) {
                jb.setEnabled(false);
            }
            PackageThread pThread = new PackageThread(this, socket) {
                @Override
                public void handleConnection() throws Exception {

                    int pos = 0;
                    String msg = new String(receivedBuffer).trim();

                    try {
                         pos = Integer.parseInt(msg);
                    }catch(Exception e ){
                        System.out.println("Packets got mixed not our fault");
                    }

                    String hit = "false";

                    if(fields[pos].getText().contains("S")){
                        hit = "true";
                    }

                    sendBuffer = hit.getBytes();
                    packet = new DatagramPacket(sendBuffer, hit.length(), ip, port);
                    socket.send(packet);

                    Game.TYPE++;
                    handleTurns();
                }
            };
            pThread.createAndStartListenThread();
            pThread.createAndStartIsAliveThread();
        } else {
            text = "Your Turn";
            textLabel.setText(text);
            for(JButton jb : clickFields) {
                if(jb.getBackground().equals(Color.CYAN))
                    jb.setEnabled(true);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String pos = e.getActionCommand();
        sendBuffer = pos.getBytes();
        packet = new DatagramPacket(sendBuffer, pos.length(), ip, port);
        try {
            socket.send(packet);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        PackageThread pt = new PackageThread(this, socket) {
            @Override
            public void handleConnection() throws Exception {

                String answer = new String (receivedBuffer);
                int pos = Integer.parseInt(e.getActionCommand());

                if(answer.contains("true")){
                    clickFields[pos].setBackground(Color.RED);
                    Game.HITS++;
                    if(handleWin(parent))
                        return;

                } else {
                    clickFields[pos].setBackground(Color.BLUE);
                }
                //end of turn
                Game.TYPE++;
                handleTurns();
            }
        };
        pt.createAndStartListenThread();
        pt.createAndStartIsAliveThread();
    }

    private boolean handleWin(JFrame parent){

        if(Game.HITS == 10){
            parent.dispose();

            End end = new End("Won");
            end.setLocationRelativeTo(null);
            end.setVisible(true);

            String msg = "You Lost";
            sendBuffer = msg.getBytes();
            packet = new DatagramPacket(sendBuffer, msg.length(), ip, 8888);
            try {
                socket.send(packet);
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
