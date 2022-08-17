/*
SEARCH BY NAME NOT WORKING
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// connection imports
import java.sql.*;
import java.util.Objects;

public class AdminLibrarianWindow extends JFrame {
    private JPanel basePanel;
    private JFormattedTextField searchTextField;
    private JButton searchButton;
    private JButton addNewButton;
    private JPanel searchPanel;
    private JButton viewAllButton;
    private JTabbedPane filterPanel;
    private JButton backButton;
    private JButton filterSearchButton;
    private JLabel genderLabel;
    private JLabel cityLabel;
    private JLabel iconLabel;
    private JComboBox genderComboBox;
    private JLabel stateLabel;
    private JPanel filterTabbedPanel;
    private JLabel aadharLabel;
    private JTextField cityTextField;
    private JTextField stateTextField;
    private JTextField nameTextField;
    private JLabel orLabel;
    private JLabel nameLabel;
    private JButton resetButton;


    public AdminLibrarianWindow() {

        setContentPane(basePanel);
        setTitle("Librarian Section");
        setLocation(150,150);
        setMinimumSize(new Dimension(600, 450));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String aadharId = searchTextField.getText();
                if(!isValidAadharId(aadharId)) {
                    if(Objects.equals(nameTextField.getText(), ""))
                    JOptionPane.showMessageDialog(AdminLibrarianWindow.this, "Aadhar number should be a 12 digit number", "Error", JOptionPane.ERROR_MESSAGE);
                    else if(!searchByName(nameTextField.getText()))
                        JOptionPane.showMessageDialog(AdminLibrarianWindow.this, "No record found having name as " + nameTextField.getText());
                }
                else
                    getLibrarianDetails(aadharId);
            }
        });
        addNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminLibrarianProfile adminLibrarianProfile = new AdminLibrarianProfile();
                adminLibrarianProfile.setAlreadyPresent(false);
                adminLibrarianProfile.setJoinDate();
                dispose();

            }
        });
        viewAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String query = "select * from librarian ORDER BY name";
                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(query)) {
                 // display in tabular format

                    LibrarianTable librarianTable = new LibrarianTable();
                    librarianTable.showTable(rs);

                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(AdminLibrarianWindow.this ,exception.toString(),"Error!",JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Admin();
                dispose();
            }
        });

        filterSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String city = cityTextField.getText();
                String state = stateTextField.getText();
                String gender = String.valueOf(genderComboBox.getSelectedItem());

                if(!city.equals(""))
                    city = "LOWER('" + city + "')";
                if(!state.equals(""))
                     state = "LOWER('" + state + "')";

                String query;
                if(city.equals("") && state.equals("") && gender.equals("*"))
                    query = "SELECT * FROM librarian";
                else if(city.equals("")) {
                    if(state.equals(""))
                        query = "SELECT * FROM librarian WHERE gender = '" + gender + "'";
                    else if(gender.equals("*"))
                        query = "SELECT * FROM librarian WHERE LOWER(state) = " + state;
                    else {
                        query = "SELECT * FROM librarian WHERE gender = '" + gender +  "' AND LOWER(state) = " + state;
                    }
                }
                else  if(state.equals("")){
                    if(gender.equals("*"))
                        query = "SELECT * FROM librarian WHERE LOWER(city) = " + city;
                    else
                        query = "SELECT * FROM librarian WHERE LOWER(city) = " + city + " AND gender = '" + gender + "'";
                }
                else {
                    if(gender.equals("*"))
                        query = "SELECT * FROM librarian WHERE LOWER(state) = " + state + " AND LOWER(city) = " + city;
                    else
                        query = "SELECT * FROM librarian WHERE LOWER(state) = " + state + " AND LOWER(city) = " + city + " AND gender = '" + gender + "'";
                }

                query += " ORDER BY name";
                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement()) {
                    ResultSet rs = st.executeQuery(query);
                    LibrarianTable librarianTable = new LibrarianTable();
                    librarianTable.showTable(rs);
                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(AdminLibrarianWindow.this ,exception.toString(),"Error!",JOptionPane.ERROR_MESSAGE);
                }

            }
        });


        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cityTextField.setText("");
                stateTextField.setText("");
                genderComboBox.setSelectedItem("*");
            }
        });
    }


    private boolean searchByName(String name) {
        String query = "SELECT * FROM librarian WHERE LOWER(name) = LOWER('" + name  + "') ORDER BY name";
        try(Connection con = ConnectToDB.getConnectToDB();
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = st.executeQuery(query);

            if(!rs.next())
                return false;

            if(!rs.next()) {
                rs.previous();
                AdminLibrarianProfile adminLibrarianProfile = new AdminLibrarianProfile();
                adminLibrarianProfile.setAlreadyPresent(true);
                // fill details ,if exist

                adminLibrarianProfile.setLibrarianDetails(rs);
                dispose();
            }
            else {
                rs.previous();rs.previous();
                LibrarianTable librarianTable = new LibrarianTable();
                //rs.beforeFirst();
                librarianTable.showTable(rs);
            }
        }catch (SQLException e) {
            JOptionPane.showMessageDialog(AdminLibrarianWindow.this ,e.toString(),"Error!",JOptionPane.ERROR_MESSAGE);
        }

        return true;
    }
    private void getLibrarianDetails(String aadharId) {
        // check Database if found -> display, else -> not Exist

        String query = "select * from librarian where aadhar_number = " + aadharId;

        // used try with resources
        try(Connection con = ConnectToDB.getConnectToDB();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query)) {
            /*
              The new driver class is `com.mysql.cj.jdbc.Driver'.
              The driver is automatically registered via the SPI
              and manual loading of the driver class is generally unnecessary.
             */
           // Class.forName("com.mysql.jdbc.Driver");

            if(!rs.next())
                JOptionPane.showMessageDialog(AdminLibrarianWindow.this, "No such Librarian Found!", "Oops!", JOptionPane.INFORMATION_MESSAGE);
            else {
                // open the profile window
                AdminLibrarianProfile adminLibrarianProfile = new AdminLibrarianProfile();
                adminLibrarianProfile.setAlreadyPresent(true);
                // fill details ,if exists
                adminLibrarianProfile.setLibrarianDetails(rs);
                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(AdminLibrarianWindow.this ,e.toString(),"Error!",JOptionPane.ERROR_MESSAGE);
        }



    }

    static boolean isValidAadharId(String aadharId) {
        int n = aadharId.length();
        if(n != 12)
            return false;

        for(int i = 0; i < n; ++i)
            if (aadharId.charAt(i) < '0' || aadharId.charAt(i) > '9')
                return false;
        return true;
    }

}

