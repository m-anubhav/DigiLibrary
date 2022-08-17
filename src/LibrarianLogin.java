import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LibrarianLogin extends JFrame{
    private JPanel basePanel;
    private JTextField aadharIdTextField;
    private JTextField usernameTextField;
    private JButton signInButton;
    private JPasswordField passwordField;
    private JButton signUpButton;
    private JLabel aadharLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;

    public LibrarianLogin() {

        setContentPane(basePanel);
        setTitle("Librarian Login");
        setLocation(250,200);
        setMinimumSize(new Dimension(330, 250));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // checking a basic validity of aadhar number
                if(!AdminLibrarianWindow.isValidAadharId(aadharIdTextField.getText())) {
                    JOptionPane.showMessageDialog(LibrarianLogin.this, "It should be a 12 digit number", "Invalid Aadhar!", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // searching "librarian" table
                String query = "SELECT aadhar_number FROM librarian WHERE aadhar_number = '" + aadharIdTextField.getText() + "'";
                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement()) {
                    ResultSet rs = st.executeQuery(query);
                    // if no results found, you have to first register yourself to admin
                    if(!rs.next())
                        JOptionPane.showMessageDialog(LibrarianLogin.this, "You are not registered by the ADMIN", "Login Denied", JOptionPane.INFORMATION_MESSAGE);
                    else {
                        // now checking filled credentials in librarian_login table
                        try(Statement st2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                            ResultSet rs2 = st2.executeQuery("SELECT * FROM librarian_login WHERE aadhar_number = '" + aadharIdTextField.getText() + "'");
                            // if found such librarian
                            if(rs2.next()) {
                                String username = usernameTextField.getText();
                                String password = String.valueOf(passwordField.getPassword());
                                String msg = "";
                                if(!username.equals(rs2.getString(2)))
                                    msg = "Wrong username";
                                else if(!password.equals(rs2.getString(3)))
                                    msg = "wrong password";
                                else {
                                    // if all credentials match then direct to "librarian" window
                                    // passing on aadhar number
                                    JOptionPane.showMessageDialog(LibrarianLogin.this, "Successfully logged in");
                                    Librarian librarian = new Librarian();
                                    librarian.setAadharId(aadharIdTextField.getText(), rs2.getString(2));
                                    dispose();
                                    return;
                                }
                                JOptionPane.showMessageDialog(LibrarianLogin.this, msg);

                            }
                            // else not found record this means no password and username set
                            // this is to be set by librarians themselves(not by admin)
                            // by clicking on "sign up" button
                            else {
                                JOptionPane.showMessageDialog(LibrarianLogin.this, "No username and password set\n Please click on 'Sign Up' Button");
                            }
                        }
                    }
                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(LibrarianLogin.this, exception.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // new window for sign up
                LibrarianRegister librarianRegister = new LibrarianRegister();
                dispose();
            }
        });

        setVisible(true);
    }
}
