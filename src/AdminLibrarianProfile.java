import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class AdminLibrarianProfile extends JFrame{
    private JPanel basePanel;
    private JPanel infoPanel;
    private JLabel nameLabel;
    private JTextField nameTextField;
    private JLabel aadharIdLabel;
    private JTextField aadharIdTextField;
    private JLabel dobLabel;
    private JTextField dobTextField;
    private JLabel genderLabel;
    private JLabel contactNumberLabel;
    private JTextField contactNumberTextField;
    private JComboBox genderComboBox;
    private JLabel emailLabel;
    private JTextField emailTextField;
    private JLabel PINLabel;
    private JTextField PINTextField;
    private JLabel cityLabel;
    private JLabel stateLabel;
    private JLabel addressLabel;
    private JTextField addressTextField;
    private JLabel joinDateLabel;
    private JTextField joinDateTextField;
    private JLabel salaryLabel;
    private JTextField salaryTextField;
    private JButton deleteButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel iconLabel;
    private JTextField cityTextField;
    private JTextField stateTextField;
    private JButton resetButton;
    private boolean isAlreadyPresent;
    private String name, dob, gender, contactNumber, email, pin, city, state, address, salary;

    public AdminLibrarianProfile() {

        setContentPane(basePanel);
        setTitle("Librarian Profile");
        setLocation(250,200);
        setMinimumSize(new Dimension(600, 450));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
         email = "null";city = "null";state = "null";

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isAlreadyPresent) {
                    String query = "DELETE FROM librarian WHERE aadhar_number = " + aadharIdTextField.getText();
                    int option = JOptionPane.showConfirmDialog(AdminLibrarianProfile.this, "Are you sure to delete " + aadharIdTextField.getText(), "Warning!, this operation is permanent", JOptionPane.YES_NO_OPTION);
                    if(option == JOptionPane.NO_OPTION)
                        return;

                    try(Connection con = ConnectToDB.getConnectToDB();
                        Statement st = con.createStatement()) {
                        st.executeUpdate(query);

                        JOptionPane.showMessageDialog(AdminLibrarianProfile.this, "Record deleted successfully", "Success!", JOptionPane.INFORMATION_MESSAGE);
                        new AdminLibrarianWindow();
                        dispose();
                    }catch (SQLException exception) {
                        JOptionPane.showMessageDialog(AdminLibrarianProfile.this,exception,"Error!", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else
                    JOptionPane.showMessageDialog(AdminLibrarianProfile.this, "No such Librarian Found!", "Oops!", JOptionPane.INFORMATION_MESSAGE);

            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(validDetails()) {
                    String query;

                    name = nameTextField.getText(); dob = dobTextField.getText(); gender = (String) genderComboBox.getSelectedItem();
                    contactNumber = contactNumberTextField.getText(); pin = PINTextField.getText(); address = addressTextField.getText();
                    email = emailTextField.getText(); city = cityTextField.getText(); state = stateTextField.getText();
                    salary = salaryTextField.getText();
                    if(Objects.equals(email, ""))
                        email = "null";
                    if(Objects.equals(city, ""))
                        city = "null";
                    if(Objects.equals(state, ""))
                        state = "null";
                    if(isAlreadyPresent) {
                        query = "UPDATE librarian " +
                                "SET " +
                                "name = '" + name +
                                "', dob = '" + dob +
                                "', gender = '" + gender +
                                "', contact_number = '" + contactNumber+
                                "', email = '" + email +
                                "', pin = '" + pin +
                                "', city = '" + city +
                                "', state = '" + state +
                                "', address = '" + address +
                                "', salary = '" + salary +
                                "' WHERE aadhar_number = '" + aadharIdTextField.getText() + "'";
                    }
                    else {
                        LocalDate joinDate = LocalDate.now();
                        query = "INSERT INTO librarian VALUES( '" +
                                aadharIdTextField.getText() + "' , '" + name+ "' , '" + dob + "' , '" + gender+ "' , '" + contactNumber+ "' , '" + email + "' , '" + pin + "' , '" + city + "' , '" + state + "' , '" + address+ "' , '" + joinDate + "' , '" + salary + "')";
                        System.out.println(query);
                        int option = JOptionPane.showConfirmDialog(AdminLibrarianProfile.this, "Aadhar number is permanent and can't be changed later.\n Are you sure to save " + aadharIdTextField.getText(), "Warning!", JOptionPane.YES_NO_OPTION);
                        if(option == JOptionPane.NO_OPTION)
                            return;
                        // Make Aadhar Id Uneditable after first addition
                        aadharIdTextField.setEditable(false);
                    }


                    try(Connection con = ConnectToDB.getConnectToDB();
                        Statement st = con.createStatement()) {

                        st.executeUpdate(query);

                        JOptionPane.showMessageDialog(AdminLibrarianProfile.this, "Details Updated Successfully", "Success!",JOptionPane.INFORMATION_MESSAGE);
                    }catch(SQLException exception) {
                        JOptionPane.showMessageDialog(AdminLibrarianProfile.this, exception.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    isAlreadyPresent = true;
                }



            }
        });


        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminLibrarianWindow();
                dispose();
            }
        });

        setVisible(true);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminLibrarianProfile();
                dispose();
            }
        });
    }

    void setAlreadyPresent(boolean b) {
        isAlreadyPresent = b;
    }
    void setJoinDate() {
        joinDateTextField.setText(String.valueOf(LocalDate.now()));
    }
    boolean validDetails() {
        boolean result = true;
        if(!AdminLibrarianWindow.isValidAadharId(aadharIdTextField.getText())) {
            JOptionPane.showMessageDialog(AdminLibrarianProfile.this, "It should be a 12 digit number", "Invalid Aadhar!", JOptionPane.ERROR_MESSAGE);
            result = false;
        }
        else if(!validContactNumber(contactNumberTextField.getText())) {
            JOptionPane.showMessageDialog(AdminLibrarianProfile.this, "It should be a 10 digit number", "Invalid Contact Number!", JOptionPane.ERROR_MESSAGE);
            result = false;
        }
        else if(!validDOB(dobTextField.getText())) {
            JOptionPane.showMessageDialog(AdminLibrarianProfile.this, "Ineligible Age", "Invalid DOB", JOptionPane.ERROR_MESSAGE);
            result = false;
        }
        else if(Objects.equals(nameTextField.getText(), "") || Objects.equals(PINTextField.getText(), "") || Objects.equals(addressTextField.getText(), "") || Objects.equals(salaryTextField.getText(), "")) {
            JOptionPane.showMessageDialog(AdminLibrarianProfile.this, "Fields marked with(*) are Required", "Attention!!", JOptionPane.WARNING_MESSAGE);
            result = false;
        }
        System.out.println("inside isValid: " + result);
        return result;
    }
    private static boolean validDOB(String d) {

        try {
            LocalDate date = LocalDate.parse(d);
            LocalDate currentDate = LocalDate.now();
            if(currentDate.compareTo(date) <= 0 || (currentDate.getYear() - date.getYear() >= 60))
                return false;
        }catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
     static boolean validContactNumber(String s) {
        int n = s.length();
        if(n != 10)
            return false;

        for(int i = 0; i < n; ++i)
            if(s.charAt(i) < '0' || s.charAt(i) > '9')
                return false;

        return true;
    }

    void setLibrarianDetails(ResultSet rs) {

        try {
            aadharIdTextField.setText(rs.getString(1));
            aadharIdTextField.setEditable(false);       // No changes in the aadharId, once saved
            nameTextField.setText(rs.getString(2));
            dobTextField.setText(rs.getDate(3).toString());
            genderComboBox.setSelectedItem(rs.getString(4));
            contactNumberTextField.setText(rs.getString(5));
            emailTextField.setText(rs.getString(6));
            PINTextField.setText(rs.getString(7));
            cityTextField.setText(rs.getString(8));
            stateTextField.setText(rs.getString(9));
            addressTextField.setText(rs.getString(10));
            joinDateTextField.setText(rs.getDate(11).toString());
            salaryTextField.setText(String.valueOf(rs.getDouble(12)));


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(AdminLibrarianProfile.this, e.toString(), "Error!", JOptionPane.ERROR_MESSAGE);
        }

    }


}
