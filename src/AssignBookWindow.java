import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;

public class AssignBookWindow extends JFrame{
    private JPanel basePanel;
    private JPanel bottomPanel;
    private JPanel topPanel;
    private JTextField addressTextField;
    private JTextField contactTextField;
    private JTextField nameTextField;
    private JTextField aadharIdTextField;
    private JButton saveButton;
    private JLabel aadharIdLabel;
    private JLabel nameLabel;
    private JLabel contactLabel;
    private JLabel addressLabel;
    private JButton cancelButton;
    private String librarianAadharId;
    private String isbn;

    public AssignBookWindow() {

        setContentPane(basePanel);
        setTitle("Assign Book Window");
        setLocation(250,200);
        setMinimumSize(new Dimension(600, 500));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setVisible(true);
        librarianAadharId = "null";

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String aadharId = aadharIdTextField.getText();
                String name, contact, address;
                name = nameTextField.getText();
                contact = contactTextField.getText();
                address = addressTextField.getText();
                if(!AdminLibrarianWindow.isValidAadharId(aadharId)) {
                    JOptionPane.showMessageDialog(AssignBookWindow.this, "Aadhar number should be a 12 digit number!");
                    return;
                }
                if(!AdminLibrarianProfile.validContactNumber(contact)) {
                    JOptionPane.showMessageDialog(AssignBookWindow.this, "contact number is a 10 digit number");
                    return;
                }
                if(name.equals("") || address.equals("")) {
                    JOptionPane.showMessageDialog(AssignBookWindow.this, "Fill ALL fields!!");
                    return;
                }

                LocalDate date = LocalDate.now();
                LocalTime time = LocalTime.now();
                String query = "INSERT INTO books_assigned VALUES( '" + isbn + "' , '" + aadharId + "' , '" + librarianAadharId + "' , '" + date + "' , '" + time + "' , null, null,null)";
                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement()) {
                    st.executeUpdate(query);
                    try(Statement st2 = con.createStatement()) {
                        st2.executeUpdate("UPDATE books SET available = 'no' WHERE isbn = '" + isbn + "'");
                    }
                    JOptionPane.showMessageDialog(AssignBookWindow.this, "Book assigned!");
                    dispose();
                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(AssignBookWindow.this, exception);
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    void setAadharId(String librarianAadharId) {
        this.librarianAadharId = librarianAadharId;
        this.setTitle("");
    }
    void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
