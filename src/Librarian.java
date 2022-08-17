import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Librarian extends JFrame{
    private JButton viewProfileButton;
    private JButton viewBooksButton;
    private JPanel basePanel;
    private JLabel welcomeLabel;
    private JLabel imgLabel;
    private String aadharId, name;

    public Librarian() {

        setContentPane(basePanel);
        //setTitle("Librarian: " + aadharId);
        setLocation(250,200);
        setMinimumSize(new Dimension(430, 400));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        viewProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Connecting to DB... and displaying profile of current librarian
                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement()) {
                    ResultSet rs = st.executeQuery("select * from librarian, librarian_login where (librarian.aadhar_number = '" + aadharId + "' and librarian.aadhar_number = librarian_login.aadhar_number)");
                    LibrarianProfile librarianProfile = new LibrarianProfile();
                    rs.next();
                    librarianProfile.setLibrarianDetails(rs);
                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(Librarian.this, e.toString(), "Error!", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        viewBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BookWindow bookWindow = new BookWindow();
                bookWindow.setAadharId(aadharId, name);
                dispose();
            }
        });

        setVisible(true);
    }

    void setAadharId(String aadharId, String name) {
        this.aadharId = aadharId;
        this.name = name;
        if(name == null) {
            this.setTitle("Librarian: " + aadharId);
        }
        else
        this.setTitle("Librarian: " + name + " (" + aadharId + ")");
    }

}
