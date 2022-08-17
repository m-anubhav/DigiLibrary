import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class BookTable extends JFrame{
    private JPanel basePanel;
    private JTable table;
    private JScrollPane scrollPanel;
    private JLabel titleLabel;
    private JTextField recordTextField;
    private JComboBox sortByComboBox;
    private JLabel sortByLabel;
    private JComboBox sortByComboBox2;
    private JLabel sortByLabel2;
    private JButton refreshButton;
    private JButton refreshButton2;
    private String librarianAadharId;
    private boolean isAdmin, isFilter;
    private String query;
    public BookTable() {

        setContentPane(basePanel);
        setTitle("Books' Table");
        setLocation(150,200);
        setSize(1200, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        isAdmin = false;
        isFilter = false;
        query = null;

        sortByComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = (String) sortByComboBox.getSelectedItem();
                // setting the comboBox to the current option being selected
                sortByComboBox.setSelectedItem(s);
                String query2;
                    if(s.equals("*"))
                        return;
                    if(!isFilter)
                        query2 = "SELECT * FROM books ORDER BY " + s;
                    else
                        query2 = query + " ORDER BY " + s;
                    try(Connection con = ConnectToDB.getConnectToDB();
                        Statement st = con.createStatement()) {
                        ResultSet rs = st.executeQuery(query2);
                        BookTable bookTable = new BookTable();
                        // may be invoked by a user, so set aadharId if it does exist
                        if(librarianAadharId != null)
                            bookTable.setAadharId(librarianAadharId);
                        if(isFilter) {
                            bookTable.setFilter(true);
                            bookTable.setQuery(query);
                        }
                        bookTable.showTable(rs);
                        dispose();
                    }catch (SQLException exception) {
                        JOptionPane.showMessageDialog(BookTable.this, exception);
                    }
                }

        });
        sortByComboBox2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = (String) sortByComboBox2.getSelectedItem();
                // setting the comboBox to the current option being selected
                sortByComboBox2.setSelectedItem(s);
                    if(s.equals("*"))
                        return;
                    if(s.equals("return_date"))
                        s = "ret_date";
                    else
                        s = "date";

                    String query;
                    // if invoked by ADMIN then display books assigned by everyone
                    if(librarianAadharId.equals("ADMIN"))
                        query = "SELECT * FROM books_assigned ORDER BY '" + s + "' DESC";
                    // else display books assigned by only the current librarian
                    else
                        query = "SELECT * FROM books_assigned WHERE assigned_by = '" + librarianAadharId + "' ORDER BY '" + s + "'DESC";
                    try(Connection con = ConnectToDB.getConnectToDB();
                        Statement st = con.createStatement()) {
                        ResultSet rs = st.executeQuery(query);
                        BookTable bookTable = new BookTable();
                        bookTable.setAadharId(librarianAadharId);
                        bookTable.showAssignedTable(rs);
                        dispose();
                    }catch (SQLException exception) {
                        JOptionPane.showMessageDialog(BookTable.this, exception);
                    }
                }

        });
        // linked to showTable (books table)
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query2;
                // if invoked by filterSearchButton of the BookWindow(or UserBookWindow)
                if(isFilter)
                    query2 = query + "ORDER BY name";
                else
                    query2 = "SELECT * FROM books ORDER BY name";
                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement()) {
                    ResultSet rs = st.executeQuery(query2);
                    BookTable bookTable = new BookTable();
                    // may be invoked by a user, so set aadharId if it does exist
                    if(librarianAadharId != null)
                    bookTable.setAadharId(librarianAadharId);
                    if(isFilter) {
                        bookTable.setFilter(true);
                        // set query for the upcoming window
                        bookTable.setQuery(query);
                    }
                    bookTable.showTable(rs);
                    dispose();
                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(BookTable.this, exception);
                }
            }
        });
        //linked to showAssignedTable(books_assigned table)
        refreshButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String query;
                if(librarianAadharId.equals("ADMIN"))
                    query = "SELECT * FROM books_assigned ORDER BY date,time DESC";
                else
                    query = "SELECT * FROM books_assigned WHERE assigned_by = '" + librarianAadharId + "' ORDER BY date,time DESC";
                try(Connection con = ConnectToDB.getConnectToDB();
                    Statement st = con.createStatement()) {
                    ResultSet rs = st.executeQuery(query);
                    BookTable bookTable = new BookTable();
                    bookTable.showAssignedTable(rs);
                    bookTable.setAadharId(librarianAadharId);
                    dispose();
                }catch (SQLException exception) {
                    JOptionPane.showMessageDialog(BookTable.this, exception);
                }
            }
        });
    }
    void showAssignedTable(ResultSet rs) throws SQLException {
        sortByLabel.setVisible(false);
        sortByComboBox.setVisible(false);
        refreshButton.setVisible(false);
        // adding name of columns of table to a vector of Strings
        Vector<String> columns = new Vector<>();
        String[] str = {"ISBN", "Assigned To", "Assigned By", "Assign Date", "Assign Time", "Return Date", "Return Time", "Received By"};
        for(int i = 0; i < 8; ++i)
            columns.addElement(str[i]);

        // creating a vector of vectors(of string) to store the values of the table
        Vector<Vector<String>> data = new Vector<>();
        while(rs.next()) {
            Vector<String> v = new Vector<>();
            for(int i = 1; i <= 8; ++i) {
                if(i == 4 || i == 6)
                    v.addElement(String.valueOf(rs.getDate(i)));
                else if(i == 5 || i == 7)
                    v.addElement(String.valueOf(rs.getTime(i)));
                else
                    v.addElement(rs.getString(i));
            }
            data.addElement(v);
        }

        //setting up the table
        table.setModel(new DefaultTableModel(data, columns));
        //displaying number of records
        recordTextField.setText(data.size() + " Record(s) Found");
        recordTextField.setEditable(false);

    }
    void showTable(ResultSet rs) throws SQLException {

        sortByLabel2.setVisible(false);
        sortByComboBox2.setVisible(false);
        refreshButton2.setVisible(false);
        // adding name of columns of table to a vector of Strings
        Vector<String> columns = new Vector<>();
        String[] str = {"ISBN", "Name", "Author", "Publication", "Year of publication", "Subject", "Price", "Available(Yes/No)"};
        for(int i = 0; i <= 7; ++i)
            columns.addElement(str[i]);

        // creating a vector of vectors(of string) to store the values of the table
        Vector<Vector<String>> data = new Vector<>();
        while(rs.next()) {
            Vector<String> v = new Vector<>();
            for(int i = 1; i <= 8; ++i) {
                if(i == 7)
                    v.addElement(String.valueOf(rs.getDouble(i)));
                else
                    v.addElement(rs.getString(i));
            }
            data.addElement(v);
        }

        //setting up the table
        table.setModel(new DefaultTableModel(data, columns));
        //displaying number of records
        recordTextField.setText(data.size() + " Record(s) Found");
        recordTextField.setEditable(false);

    }

    // method to set the query from which the current table being displayed is constructed
    void setQuery(String query) {
        this.query = query;
    }

    // setting aadhar Id(of admin/ librarian)
    void setAadharId(String s) {librarianAadharId = s;
    this.setTitle("Books' Table By " + librarianAadharId);}
    // set if invoked using filterSearchButton
    void setFilter(boolean b){isFilter=b;}
}
