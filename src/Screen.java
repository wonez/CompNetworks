import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Screen extends JFrame implements ActionListener {

    private JButton host;
    private JButton join;
    private JTextField nickname;

    public Screen(){

        super("Battleship");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Caption cap = new Caption();

        host = new JButton("Host");
        join = new JButton("Join");
        nickname = new JTextField("kurac", 25);

        host.addActionListener(this);
        join.addActionListener(this);

        JPanel buttons = new JPanel(new GridLayout(3, 4));

        for(int i=0; i<9; i++)
            buttons.add(new JPanel());
        buttons.add(host);
        buttons.add(join);
        buttons.add(new JPanel());

        add(buttons, BorderLayout.NORTH);
        add(cap, BorderLayout.CENTER);


        JLabel nameLabel = new JLabel("Nickname: ");

        JPanel bottom = new JPanel(new GridLayout(1, 4));
        bottom.add(new JPanel());
        bottom.add(nameLabel);
        bottom.add(nickname);
        bottom.add(new JPanel());

        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(nickname.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this, "Nickname field cannot be empty" );
            return;
        }

        Game.ME = nickname.getText();

        if(e.getActionCommand().equals("Host")){

            Game.TYPE = "Host";
            this.dispose();
            Host host = new Host();
            host.setVisible(true);

        }else if(e.getActionCommand().equals("Join")){

            Game.TYPE = "Join";
            this.dispose();
            Join join = new Join();
            join.setVisible(true);
        }
    }

}

class Caption extends JPanel{

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);
        g.setFont(new Font("Arial", Font.BOLD, 64));
        g.drawString("Battle", 90, 200);
        g.setColor(Color.RED);
        g.drawString("Ship", 270, 200);
    }
}
