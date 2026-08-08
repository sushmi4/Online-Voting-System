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
import java.util.regex.Pattern;

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

    private static final Pattern EMAIL_RULE =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_RULE =
            Pattern.compile("(?=.*[A-Za-z])(?=.*\\d).{8,}");

    public UserRegister(CardLayout cl, JPanel contentPanel) {
        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(24, 30, 24, 30));

        UITheme.Card card = new UITheme.Card();
        card.setBorder(new EmptyBorder(30, 44, 30, 44));
        card.setPreferredSize(new Dimension(760, 720));

        JPanel inner = new JPanel(new BorderLayout(0, 16));
        inner.setOpaque(false);

        // Title
        JLabel title = new JLabel("Voter Registration Form", JLabel.CENTER);
        title.setFont(UITheme.font(Font.BOLD, 26));
        title.setForeground(UITheme.PRIMARY_DARK);
        inner.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // ---------------- Fields ----------------
        nameField = new UITheme.RoundedTextField(20);
        vIdField = new UITheme.RoundedTextField(20);
        emailField = new UITheme.RoundedTextField(20);
        passField = new UITheme.RoundedPasswordField(20);
        mobField = new UITheme.RoundedTextField(20);

        addrArea = new JTextArea(3, 20);
        UITheme.styleTextArea(addrArea);

        // ---------------- Image Picker ----------------
        JLabel imgInstruction = new JLabel("PNG only, max 500KB");
        imgInstruction.setFont(UITheme.font(Font.PLAIN, 11));
        imgInstruction.setForeground(UITheme.TEXT_MUTED);

        imagePathDisplay = new UITheme.RoundedTextField("No file selected");
        imagePathDisplay.setEditable(false);

        JButton browseBtn = UITheme.button("Choose Pic", UITheme.ACCENT);
        browseBtn.addActionListener(e -> handleImageSelection());

        JPanel imgFieldPanel = new JPanel(new BorderLayout(8, 0));
        imgFieldPanel.setOpaque(false);
        imgFieldPanel.add(imagePathDisplay, BorderLayout.CENTER);
        imgFieldPanel.add(browseBtn, BorderLayout.EAST);

        JPanel imgMainPanel = new JPanel(new BorderLayout(0, 4));
        imgMainPanel.setOpaque(false);
        imgMainPanel.add(imgInstruction, BorderLayout.NORTH);
        imgMainPanel.add(imgFieldPanel, BorderLayout.CENTER);

        // ---------------- DOB (BS) ----------------
        dayBox = new JComboBox<>();
        monthBox = new JComboBox<>();
        yearBox = new JComboBox<>();
        for (int i = 1; i <= 32; i++) {
            dayBox.addItem(String.valueOf(i));
        }

        String[] months = { "Baisakh", "Jestha", "Ashadh", "Shrawan", "Bhadra", "Ashwin",
                "Kartik", "Mangsir", "Poush", "Magh", "Falgun", "Chaitra" };
        monthBox = new JComboBox<>(months);

        for (int i = 1950; i <= 2085; i++) {
            yearBox.addItem(String.valueOf(i));
        }

        styleCombo(dayBox);
        styleCombo(monthBox);
        styleCombo(yearBox);

        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        dobPanel.setOpaque(false);
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
        JButton regBtn = UITheme.button("REGISTER NOW");
        regBtn.setPreferredSize(new Dimension(0, 44));
        regBtn.addActionListener(e -> handleRegistration(cl, contentPanel));

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(22, 8, 4, 8);
        formPanel.add(regBtn, gbc);

        // ---------------- Login Link ----------------
        JLabel loginLink = new JLabel("Already registered?  Login here", JLabel.CENTER);
        loginLink.setFont(UITheme.font(Font.BOLD, 13));
        loginLink.setForeground(UITheme.ACCENT);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                cl.show(contentPanel, "UserLogin");
            }
        });
        gbc.gridy = row;
        gbc.insets = new Insets(6, 8, 0, 8);
        formPanel.add(loginLink, gbc);

        // Scrollable card body
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        UITheme.styleScrollPane(scrollPane);

        inner.add(scrollPane, BorderLayout.CENTER);
        card.add(inner, BorderLayout.CENTER);

        centerWrapper.add(card);

        JScrollPane outerScroll = new JScrollPane(centerWrapper);
        outerScroll.setBorder(null);
        outerScroll.setOpaque(false);
        outerScroll.getViewport().setOpaque(false);
        outerScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        outerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(outerScroll, BorderLayout.CENTER);
    }

    private void styleCombo(JComboBox<String> box) {
        box.setFont(UITheme.font(Font.PLAIN, 13));
        box.setBackground(UITheme.WHITE);
        box.setForeground(UITheme.TEXT_DARK);
        box.setBorder(new UITheme.RoundBorder(UITheme.BORDER, 12));
    }

    // ---------------- Image Selection Handler ----------------
    private void handleImageSelection() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Profile Picture (PNG, Max 500KB)");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Image", "png"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png")) {
                UITheme.showMessage(this, "File Type Error",
                        "Only PNG files are allowed!",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (file.length() > 500 * 1024) {
                UITheme.showMessage(this, "File Size Error",
                        "File size too large! Maximum 500KB.",
                        JOptionPane.ERROR_MESSAGE);
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

        if (name.isEmpty() || id.isEmpty() || email.isEmpty() || pass.isEmpty()
                || mob.isEmpty() || addr.isEmpty() || actualFilePath.isEmpty()) {
            UITheme.showMessage(this, "Form Incomplete", "Please fill up the form!",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!name.contains(" ")) {
            UITheme.showMessage(this, "Name Error",
                    "Please enter your full name (first and last).",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!EMAIL_RULE.matcher(email).matches()) {
            UITheme.showMessage(this, "Email Error",
                    "Please enter a valid email address.",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!PASSWORD_RULE.matcher(pass).matches()) {
            UITheme.showMessage(this, "Weak Password",
                    "Password must be at least 8 characters with letters and numbers.",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!mob.matches("\\d{10}")) {
            UITheme.showMessage(this, "Mobile Error",
                    "Mobile number must be exactly 10 digits.",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!id.matches("[A-Za-z0-9\\-]+")) {
            UITheme.showMessage(this, "Voter ID Error",
                    "Voter ID can only contain letters, digits and dashes.",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int day = Integer.parseInt((String) dayBox.getSelectedItem());
        String month = (String) monthBox.getSelectedItem();
        int year = Integer.parseInt((String) yearBox.getSelectedItem());

        if (!isValidBsDate(day, month, year)) {
            UITheme.showMessage(this, "Date Error",
                    "The selected date is invalid for " + month + ".",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isAgeValid(year, month, day)) {
            UITheme.showMessage(this, "Age Error",
                    "You are not yet 18 years old!",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int monthNum = monthToNumber(month);
        String dobDB = year + "-" + String.format("%02d", monthNum) + "-"
                + String.format("%02d", day);

        try {
            if (DBConnection.voterIdExists(id)) {
                UITheme.showMessage(this, "Duplicate Voter ID",
                        "Voter ID already used!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (DBConnection.emailExists(email)) {
                UITheme.showMessage(this, "Duplicate Email",
                        "Email already used!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (DBConnection.mobileExists(mob)) {
                UITheme.showMessage(this, "Duplicate Mobile",
                        "Mobile already used!", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = DBConnection.registerVoter(name, id, email, pass, dobDB, mob, actualFilePath, addr);
            if (success) {
                UITheme.showMessage(this, "Registration Successful",
                        "Registration Successful!\nAccount Pending Approval.",
                        JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                cl.show(contentPanel, "UserLogin");
            } else {
                UITheme.showMessage(this, "Registration Failed",
                        "Registration failed. Please try again.",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            UITheme.showMessage(this, "Error", "Error : " + ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------- BS Date Validation ----------------
    private int maxDaysInBsMonth(String month) {
        switch (month) {
            case "Baisakh":
                return 31;
            case "Jestha":
            case "Ashadh":
            case "Shrawan":
            case "Bhadra":
            case "Ashwin":
                return 32;
            case "Kartik":
            case "Mangsir":
            case "Poush":
            case "Magh":
            case "Falgun":
                return 30;
            case "Chaitra":
                return 31;
            default:
                return 31;
        }
    }

    private boolean isValidBsDate(int day, String month, int year) {
        return day >= 1 && day <= maxDaysInBsMonth(month) && year >= 1950 && year <= 2085;
    }

    // ---------------- Age Validation (BS -> AD conversion) ----------------
    private boolean isAgeValid(int bsYear, String month, int day) {
        int monthNum = monthToNumber(month);

        // Approximate AD year. BS year starts mid-April; the AD year advances
        // one for BS months Magh, Falgun and Chaitra.
        int adYear = (monthNum >= 10) ? bsYear - 56 : bsYear - 57;

        // Approximate AD month: Baisakh ~ April, Jestha ~ May, ... Chaitra ~ March
        int adMonth = ((monthNum + 2) % 12) + 1;

        // Clamp day to be safe against approximate month lengths
        int adDay = Math.min(day, LocalDate.of(2000, adMonth, 1).lengthOfMonth());

        LocalDate birthDate = LocalDate.of(adYear, adMonth, adDay);
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
        lbl.setFont(UITheme.font(Font.BOLD, 13));
        lbl.setForeground(UITheme.TEXT_MUTED);
        p.add(lbl, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 1;
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
