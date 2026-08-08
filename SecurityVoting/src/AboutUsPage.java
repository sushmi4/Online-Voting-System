import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class AboutUsPage extends JPanel {

    public AboutUsPage() {
        setOpaque(false);
        setLayout(new BorderLayout());

        // Centered content card
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(40, 40, 40, 40));

        UITheme.Card card = new UITheme.Card();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(40, 55, 40, 55));

        // Inner vertical content
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("ABOUT OUR SYSTEM");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(UITheme.font(Font.BOLD, 30));
        title.setForeground(UITheme.PRIMARY_DARK);

        JLabel divider = new JLabel("  ");
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);
        divider.setPreferredSize(new Dimension(64, 4));
        divider.setOpaque(true);
        divider.setBackground(UITheme.ACCENT);

        JLabel subtitle = new JLabel("Secure, transparent and convenient e-voting");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(UITheme.font(Font.PLAIN, 15));
        subtitle.setForeground(UITheme.TEXT_MUTED);

        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 12)));
        content.add(divider);
        content.add(Box.createRigidArea(new Dimension(0, 12)));
        content.add(subtitle);
        content.add(Box.createRigidArea(new Dimension(0, 24)));

        String aboutUsText = "Online Voting System helps make the voting process more efficient, "
                + "as it provides features that allow voters to cast their votes anytime and from anywhere.\n\n"
                + "This system aims to make the voting experience more secure, transparent and convenient for voters, "
                + "while also making it easier for election authorities to manage the entire election process.\n\n"
                + "Voters can view candidate details and cast their votes based on eligibility and availability "
                + "without standing in long queues or facing manual verification delays.\n\n"
                + "Features such as voter registration, vote casting, real-time result calculation and secure "
                + "authentication reduce human effort, minimize errors and increase trust and satisfaction among voters.";

        JTextArea textArea = new JTextArea(aboutUsText);
        textArea.setFont(UITheme.font(Font.PLAIN, 15));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setForeground(UITheme.TEXT_DARK);
        textArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        textArea.setMaximumSize(new Dimension(760, Integer.MAX_VALUE));

        content.add(textArea);
        content.add(Box.createRigidArea(new Dimension(0, 28)));

        JLabel featureLabel = new JLabel("KEY FEATURES");
        featureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        featureLabel.setFont(UITheme.font(Font.BOLD, 13));
        featureLabel.setForeground(UITheme.TEXT_MUTED);
        content.add(featureLabel);
        content.add(Box.createRigidArea(new Dimension(0, 14)));

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        chips.setOpaque(false);
        chips.add(chip("Secure Authentication"));
        chips.add(chip("Live Results"));
        chips.add(chip("One Vote Per Voter"));
        chips.add(chip("OTP Recovery"));
        content.add(chips);

        card.add(content, BorderLayout.CENTER);
        centerWrapper.add(card);

        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel chip(String text) {
        return new UITheme.Pill(text, UITheme.ACCENT, UITheme.ACCENT_LIGHT);
    }
}
