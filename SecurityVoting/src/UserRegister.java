import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.Period;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class UserRegister extends JPanel {

    private JTextField nameField, vIdField, emailField, mobField;
    private JTextField imagePathDisplay;
    private JPasswordField passField;
    private JTextArea addrArea;

    private JComboBox<String> dayBox, monthBox, yearBox;
    private String actualFilePath = "";

    public UserRegister(CardLayout cl, JPanel contentPanel) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel("Voter Registration Form", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(20, 33, 61));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(10, 50, 20, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ---------------- Fields ----------------
        nameField = new JTextField(20);
        vIdField = new JTextField(20);
        emailField = new JTextField(20);
        passField = new JPasswordField(20);
        mobField = new JTextField(20);

        addrArea = new JTextArea(3, 20);
        addrArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        addrArea.setLineWrap(true);

        // ---------------- Image Picker ----------------
        JLabel imgInstruction = new JLabel("Less than 500KB and PNG");
        imgInstruction.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        imgInstruction.setForeground(Color.DARK_GRAY);

        imagePathDisplay = new JTextField("No file selected");
        imagePathDisplay.setEditable(false);

        JButton browseBtn = new JButton("Choose Pic");
        browseBtn.addActionListener(e -> handleImageSelection());

        JPanel imgFieldPanel = new JPanel(new BorderLayout(5, 0));
        imgFieldPanel.add(imagePathDisplay, BorderLayout.CENTER);
        imgFieldPanel.add(browseBtn, BorderLayout.EAST);

        JPanel imgMainPanel = new JPanel(new BorderLayout(0, 3));
        imgMainPanel.setBackground(Color.WHITE);
        imgMainPanel.add(imgInstruction, BorderLayout.NORTH);
        imgMainPanel.add(imgFieldPanel, BorderLayout.CENTER);

        // ---------------- DOB (BS) ----------------
        dayBox = new JComboBox<>();
        monthBox = new JComboBox<>();
        yearBox = new JComboBox<>();
        for (int i = 1; i <= 32; i++)
            dayBox.addItem(String.valueOf(i));

        String[] months = { "Baisakh", "Jestha", "Ashadh", "Shrawan", "Bhadra", "Ashwin",
                "Kartik", "Mangsir", "Poush", "Magh", "Falgun", "Chaitra" };
        monthBox = new JComboBox<>(months);

        for (int i = 1888; i <= 2085; i++)
            yearBox.addItem(String.valueOf(i));

        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dobPanel.add(dayBox);
        dobPanel.add(monthBox);
        dobPanel.add(yearBox);

        // ---------------- Add Rows ----------------
        int row = 0;
        addRow(formPanel, "Full Name:", nameField, gbc, row++);
        addRow(formPanel, "Voter ID:", vIdField, gbc, row++);
        addRow(formPanel, "Email:", emailField, gbc, row++);
        addRow(formPanel, "Password:", passField, gbc, row++);
        addRow(formPanel, "DOB (BS):", dobPanel, gbc, row++);
        addRow(formPanel, "Mobile:", mobField, gbc, row++);
        addRow(formPanel, "Profile Photo:", imgMainPanel, gbc, row++);
        addRow(formPanel, "Address:", new JScrollPane(addrArea), gbc, row++);

        // ---------------- Register Button ----------------
        JButton regBtn = new JButton("REGISTER NOW");
        regBtn.setBackground(new Color(0, 123, 255));
        regBtn.setForeground(Color.WHITE);
        regBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        regBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regBtn.setPreferredSize(new Dimension(0, 45));
        regBtn.addActionListener(e -> handleRegistration(cl, contentPanel));

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 10, 10, 10);
        formPanel.add(regBtn, gbc);

        // ---------------- Login Link ----------------
        JLabel loginLink = new JLabel("<html><u>Already registered? Login here</u></html>", JLabel.CENTER);
        loginLink.setForeground(Color.BLUE);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                cl.show(contentPanel, "UserLogin");
            }
        });
        gbc.gridy = row;
        formPanel.add(loginLink, gbc);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);
    }

    // ---------------- Image Selection Handler ----------------
    private void handleImageSelection() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Profile Picture (PNG, Max 500KB)");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Image", "png");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png")) {
                JOptionPane.showMessageDialog(this,
                        "Only PNG files are allowed!", "File Type Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (file.length() > 500 * 1024) {
                JOptionPane.showMessageDialog(this,
                        "File size too large! Maximum 500KB.", "File Size Error", JOptionPane.ERROR_MESSAGE);
                imagePathDisplay.setText("No file selected");
                actualFilePath = "";
                return;
            }
            actualFilePath = file.getAbsolutePath();
            imagePathDisplay.setText(file.getName());
        }
    }

    // ---------------- Registration Handler ----------------
    private void handleRegistration(CardLayout cl, JPanel contentPanel) {
        String name = nameField.getText().trim();
        String id = vIdField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword());
        String mob = mobField.getText().trim();
        String addr = addrArea.getText().trim();

        // Check if all fields are filled
        if (name.isEmpty() || id.isEmpty() || email.isEmpty() || pass.isEmpty() ||
                mob.isEmpty() || addr.isEmpty() || actualFilePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill up the form!", "Form Incomplete",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Full Name validation
        if (!name.contains(" ")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your full name (first and last).", "Name Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mobile number validation: must be exactly 10 digits
        if (!mob.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this,
                    "Mobile number must be exactly 10 digits.", "Mobile Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String day = (String) dayBox.getSelectedItem();
        String month = (String) monthBox.getSelectedItem();
        String year = (String) yearBox.getSelectedItem();

        // Age validation
        if (!isAgeValid(Integer.parseInt(year), month, Integer.parseInt(day))) {
            JOptionPane.showMessageDialog(this,
                    "You are not yet 18 years old!", "Age Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int monthNum = monthToNumber(month);
        String dobDB = year + "-" + String.format("%02d", monthNum) + "-"
                + String.format("%02d", Integer.parseInt(day));

        try {
            if (DBConnection.voterIdExists(id)) {
                JOptionPane.showMessageDialog(this, "Voter ID already used!");
                return;
            }
            if (DBConnection.emailExists(email)) {
                JOptionPane.showMessageDialog(this, "Email already used!");
                return;
            }
            if (DBConnection.mobileExists(mob)) {
                JOptionPane.showMessageDialog(this, "Mobile already used!");
                return;
            }

            boolean success = DBConnection.registerVoter(name, id, email, pass, dobDB, mob, actualFilePath, addr);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration Successful!\nAccount Pending Approval.");
                clearFields();
                cl.show(contentPanel, "UserLogin");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error : " + ex.getMessage());
        }
    }

    // ---------------- Age Validation ----------------
    private boolean isAgeValid(int year, String month, int day) {
        int adYear = year - 57; // BS to AD
        int adMonth = monthToNumber(month);
        LocalDate birthDate = LocalDate.of(adYear, adMonth, day);
        return Period.between(birthDate, LocalDate.now()).getYears() >= 18;
    }

    // ---------------- Month Conversion ----------------
    private int monthToNumber(String month) {
        switch (month) {
            case "Baisakh":
                return 1;
            case "Jestha":
                return 2;
            case "Ashadh":
                return 3;
            case "Shrawan":
                return 4;
            case "Bhadra":
                return 5;
            case "Ashwin":
                return 6;
            case "Kartik":
                return 7;
            case "Mangsir":
                return 8;
            case "Poush":
                return 9;
            case "Magh":
                return 10;
            case "Falgun":
                return 11;
            case "Chaitra":
                return 12;
            default:
                return 1;
        }
    }

    // ---------------- Utility Methods ----------------
    private void addRow(JPanel p, String label, JComponent comp, GridBagConstraints gbc, int row) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Bold + bigger size
        p.add(lbl, gbc);
        gbc.gridx = 1;
        p.add(comp, gbc);
    }

    private void clearFields() {
        nameField.setText("");
        vIdField.setText("");
        emailField.setText("");
        passField.setText("");
        mobField.setText("");
        addrArea.setText("");
        imagePathDisplay.setText("No file selected");
        actualFilePath = "";
        dayBox.setSelectedIndex(0);
        monthBox.setSelectedIndex(0);
        yearBox.setSelectedIndex(0);
    }
}