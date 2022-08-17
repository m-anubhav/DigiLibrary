import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Admin extends JFrame {
    private JLabel sideLabel;
    private JButton viewLibrariansButton;
    private JButton viewBooksButton;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel basePanel;
    private JButton backButton;


    public Admin() {

        setContentPane(basePanel);

        setTitle("Admin Window");
        setSize(400, 220);
        setLocation(200, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        viewLibrariansButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminLibrarianWindow();
                dispose();
            }
        });
        viewBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BookWindow bookWindow = new BookWindow();
                bookWindow.setAdmin();
               // dispose();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // new HomeWindow();
                dispose();
            }
        });
        setVisible(true);
    }



}
