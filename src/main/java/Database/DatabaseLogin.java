package Database;

import javax.swing.*;

public class DatabaseLogin {
    public static String username;
    public static String password;

    public void login(){
        username = JOptionPane.showInputDialog("Enter login name: ");

// Note: password will be echoed to console;
//        String password = JOptionPane.showInputDialog("Enter password: ");
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter a password: ");
        JPasswordField pass = new JPasswordField(20);
        panel.add(label);
        panel.add(pass);
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, "Input",
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[1]);
        password = new String(pass.getPassword());

        JOptionPane.showMessageDialog(null, "Connecting as user '" + username + "' . . .");
    }
}
