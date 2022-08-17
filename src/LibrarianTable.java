import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class LibrarianTable extends JFrame{
    private JLabel titleLabel;
    private JPanel basePanel;
    private JPanel topPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private JTextField recordTextField;
    private JComboBox sortByComboBox;
    private JLabel sortByLabel;

    LibrarianTable() {
        setContentPane(basePanel);
        setTitle("Librarian Table");
        setLocation(250,200);
        setSize(1200, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        sortByComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = (String) sortByComboBox.getSelectedItem();
                if(!s.equals("*")) {
                    String query = "SELECT * FROM librarian ORDER BY " + s;
                    try(Connection con = ConnectToDB.getConnectToDB();
                        Statement st = con.createStatement()) {
                        ResultSet rs = st.executeQuery(query);
                        LibrarianTable librarianTable = new LibrarianTable();
                        librarianTable.showTable(rs);
                        dispose();
                    }catch (SQLException exception) {
                        JOptionPane.showMessageDialog(LibrarianTable.this, exception);
                    }
                }
            }
        });
    }

    void showTable(ResultSet rs) throws SQLException {
        Vector<String> columns = new Vector<>();
        String[] str = {"Aadhar Number", "Name", "DOB", "Gender", "Contact Number", "Email", "PIN", "City", "State", "Address","Joining Date","Salary"};
        for(int i = 0; i < 12; ++i)
            columns.addElement(str[i]);
        Vector<Vector<String>> data = new Vector<Vector<String>>();

        while(rs.next()) {
            Vector<String> v = new Vector<>();
            for(int i = 1; i <= 12; ++i) {
                if (i == 3 || i == 11)
                    v.addElement(String.valueOf(rs.getDate(i)));
                else if(i == 12)
                    v.addElement(String.valueOf(rs.getDouble(i)));
                else
                    v.addElement(rs.getString(i));
            }
            data.addElement(v);
        }

        table.setModel(new DefaultTableModel(data, columns));
        recordTextField.setText(data.size() + " Record(s) Found");
        recordTextField.setEditable(false);

    }
}
