package com.piet.vms.ui;

import com.piet.vms.model.Role;
import com.piet.vms.model.User;
import com.piet.vms.service.AuthService;
import com.piet.vms.service.ServiceException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public final class LoginFrame extends JFrame {
    private final AuthService auth = new AuthService();
    private final JTextField email = new JTextField("student@piet.local");
    private final JPasswordField password = new JPasswordField("Welcome123!");
    private Role selectedRole = Role.STUDENT;
    private final JButton[] roleButtons = new JButton[4];

    private final Color navy = Theme.NAVY;
    private final Color navyLight = Theme.NAVY2;
    private final Color blue = Theme.BLUE;
    private final Color white = Color.WHITE;
    private final Color muted = new Color(101, 117, 139);
    private final Color border = new Color(218, 227, 239);
    private final Color pageBackground = new Color(239, 244, 251);
    private final Color inputBackground = new Color(250, 252, 255);
    private static final int FORM_WIDTH = 390;
    private static final int INPUT_HEIGHT = 46;

    public LoginFrame() {
        super("Volunteer Management System | PIET");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setSize(1280, 800);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildBrandPanel());
        root.add(buildLoginPanel());
        setContentPane(root);
        getRootPane().setBorder(null);
    }

    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(navy);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(34, 44, 28, 38));

        JLabel logo = new JLabel();
        Dimension logoSize = new Dimension(190, 165);
        setFixedSize(logo, logoSize.width, logoSize.height);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        loadLogo(logo);
        panel.add(logo);
        panel.add(Box.createVerticalStrut(18));

        JLabel brand = new JLabel("Volunteer Management System");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brand.setForeground(new Color(205, 224, 255));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(brand);
        panel.add(Box.createVerticalStrut(42));

        JLabel eyebrow = new JLabel("COLLEGE VOLUNTEER PORTAL");
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 11));
        eyebrow.setForeground(new Color(117, 181, 255));
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(eyebrow);
        panel.add(Box.createVerticalStrut(17));

        JLabel heading = new JLabel("<html>Make every<br>contribution count.</html>");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 37));
        heading.setForeground(white);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(20));

        JLabel description = new JLabel("<html><div style='width:430px; line-height:1.55'>One place to coordinate college events, approve proposals, welcome volunteers and record verified service hours.</div></html>");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        description.setForeground(new Color(204, 218, 238));
        description.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(description);
        panel.add(Box.createVerticalStrut(32));

        JLabel featuresTitle = new JLabel("YOUR VOLUNTEER WORKSPACE");
        featuresTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        featuresTitle.setForeground(new Color(177, 197, 223));
        featuresTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(featuresTitle);
        panel.add(Box.createVerticalStrut(13));

        JPanel features = new JPanel(new GridLayout(2, 2, 11, 11));
        features.setOpaque(false);
        features.setAlignmentX(Component.LEFT_ALIGNMENT);
        features.setMaximumSize(new Dimension(480, 116));
        features.add(featureTile("01", "Event Management"));
        features.add(featureTile("02", "HOD Approvals"));
        features.add(featureTile("03", "Volunteer Records"));
        features.add(featureTile("04", "Verified Attendance"));
        panel.add(features);
        panel.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("POORNIMA INSTITUTE OF ENGINEERING & TECHNOLOGY");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer.setForeground(new Color(164, 188, 218));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(footer);
        return panel;
    }

    private void loadLogo(JLabel logo) {
        URL logoUrl = getClass().getResource("/images/vms-logo.png");
        if (logoUrl == null) {
            logo.setText("VMS");
            logo.setFont(new Font("Segoe UI", Font.BOLD, 40));
            logo.setForeground(white);
            logo.setToolTipText("Place vms-logo.png in src/main/resources/images/");
            return;
        }
        try {
            BufferedImage original = ImageIO.read(logoUrl);
            if (original == null) throw new IllegalArgumentException("Invalid PNG image");
            int maxWidth = 190, maxHeight = 165;
            double scale = Math.min((double) maxWidth / original.getWidth(), (double) maxHeight / original.getHeight());
            int width = Math.max(1, (int) Math.round(original.getWidth() * scale));
            int height = Math.max(1, (int) Math.round(original.getHeight() * scale));
            BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g.drawImage(original, 0, 0, width, height, null);
            g.dispose();
            logo.setIcon(new ImageIcon(scaled));
            logo.setText(null);
        } catch (Exception ex) {
            System.err.println("Unable to load logo: " + ex.getMessage());
            logo.setText("VMS");
            logo.setFont(new Font("Segoe UI", Font.BOLD, 40));
            logo.setForeground(white);
        }
    }

    private JPanel featureTile(String number, String text) {
        JPanel tile = new JPanel(new BorderLayout(10, 0));
        tile.setBackground(navyLight);
        tile.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(54, 79, 110)), new EmptyBorder(13, 12, 13, 8)));
        JLabel numberLabel = new JLabel(number);
        numberLabel.setForeground(new Color(117, 181, 255));
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel textLabel = new JLabel(text);
        textLabel.setForeground(white);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tile.add(numberLabel, BorderLayout.WEST);
        tile.add(textLabel, BorderLayout.CENTER);
        return tile;
    }

    private JPanel buildLoginPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(pageBackground);
        outer.setBorder(new EmptyBorder(28, 25, 28, 25));

        RoundedPanel card = new RoundedPanel(22, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(34, 35, 30, 35));
        card.setPreferredSize(new Dimension(540, 680));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        setFixedSize(form, FORM_WIDTH, 600);

        JLabel heading = new JLabel("Welcome back");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 31));
        heading.setForeground(navy);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = new JLabel("Sign in to your volunteer workspace");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(muted);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(heading);
        form.add(Box.createVerticalStrut(7));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(30));

        addFieldLabel(form, "SELECT YOUR ROLE");
        form.add(Box.createVerticalStrut(10));
        JPanel roleGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        roleGrid.setOpaque(false);
        setFixedSize(roleGrid, FORM_WIDTH, 102);
        roleGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
        Role[] roles = Role.values();
        for (int i = 0; i < roles.length && i < roleButtons.length; i++) {
            Role role = roles[i];
            JButton button = new JButton(role.label());
            roleButtons[i] = button;
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setFocusPainted(false);
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.addActionListener(e -> selectRole(role));
            roleGrid.add(button);
        }
        form.add(roleGrid);
        form.add(Box.createVerticalStrut(25));

        addFieldLabel(form, "EMAIL ADDRESS");
        form.add(Box.createVerticalStrut(8));
        styleLoginInput(email);
        form.add(centeredInput(email));
        form.add(Box.createVerticalStrut(19));

        addFieldLabel(form, "PASSWORD");
        form.add(Box.createVerticalStrut(8));
        styleLoginInput(password);
        form.add(centeredInput(password));
        form.add(Box.createVerticalStrut(25));

        JButton login = new JButton("SIGN IN   →");
        login.setFont(new Font("Segoe UI", Font.BOLD, 14));
        login.setForeground(Color.WHITE);
        login.setBackground(blue);
        login.setOpaque(true);
        login.setContentAreaFilled(true);
        login.setBorderPainted(false);
        login.setFocusPainted(false);
        login.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFixedSize(login, FORM_WIDTH, 48);
        login.setAlignmentX(Component.CENTER_ALIGNMENT);
        login.addActionListener(e -> signIn());
        form.add(login);
        form.add(Box.createVerticalStrut(16));

        JLabel demo = new JLabel("Demo password: Welcome123!");
        demo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        demo.setForeground(muted);
        demo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(demo);
        form.add(Box.createVerticalStrut(6));
        JLabel accounts = new JLabel("Demo accounts are listed in README.md");
        accounts.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        accounts.setForeground(muted);
        accounts.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(accounts);
        form.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("PIET   •   Volunteer Management System");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(muted);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(footer);

        GridBagConstraints formConstraints = new GridBagConstraints();
        formConstraints.gridx = 0;
        formConstraints.gridy = 0;
        formConstraints.weightx = 1;
        formConstraints.weighty = 1;
        formConstraints.anchor = GridBagConstraints.CENTER;
        formConstraints.fill = GridBagConstraints.NONE;
        card.add(form, formConstraints);

        GridBagConstraints cardConstraints = new GridBagConstraints();
        cardConstraints.gridx = 0;
        cardConstraints.gridy = 0;
        cardConstraints.anchor = GridBagConstraints.CENTER;
        cardConstraints.fill = GridBagConstraints.NONE;
        outer.add(card, cardConstraints);
        getRootPane().setDefaultButton(login);
        selectRole(Role.STUDENT);
        return outer;
    }

    private JPanel centeredInput(JComponent input) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        setFixedSize(row, FORM_WIDTH, INPUT_HEIGHT);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        row.add(input, c);
        return row;
    }

    private void addFieldLabel(JPanel form, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(64, 82, 108));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        setFixedSize(label, FORM_WIDTH, 16);
        form.add(label);
    }

    private void styleLoginInput(JComponent input) {
        input.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        input.setForeground(new Color(32, 49, 73));
        input.setBackground(inputBackground);
        if (input instanceof JTextField) ((JTextField) input).setCaretColor(blue);
        if (input instanceof JPasswordField) ((JPasswordField) input).setCaretColor(blue);
        input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 220, 238)),
                new EmptyBorder(10, 12, 10, 12)));
        setFixedSize(input, FORM_WIDTH, INPUT_HEIGHT);
    }

    private void setFixedSize(Component component, int width, int height) {
        Dimension size = new Dimension(width, height);
        component.setPreferredSize(size);
        component.setMinimumSize(size);
        component.setMaximumSize(size);
    }

    private void selectRole(Role role) {
        selectedRole = role;
        Role[] roles = Role.values();
        for (int i = 0; i < roleButtons.length && i < roles.length; i++) {
            JButton button = roleButtons[i];
            if (button == null) continue;
            boolean selected = roles[i] == role;
            button.setBackground(selected ? new Color(225, 239, 255) : Color.WHITE);
            button.setForeground(selected ? new Color(22, 69, 130) : new Color(24, 43, 69));
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(selected ? new Color(38, 110, 210) : border, selected ? 2 : 1),
                    new EmptyBorder(10, 5, 10, 5)));
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setBorderPainted(true);
            button.setFocusPainted(false);
        }
    }

    private void signIn() {
        try {
            User user = auth.login(email.getText().trim(), password.getPassword(), selectedRole);
            new DashboardFrame(user).setVisible(true);
            dispose();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Sign in failed", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static final class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;
        RoundedPanel(int radius, Color fill) {
            this.radius = radius;
            this.fill = fill;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setColor(new Color(28, 48, 78, 22));
            g.fillRoundRect(3, 5, getWidth() - 7, getHeight() - 8, radius, radius);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, radius, radius);
            g.setColor(new Color(216, 226, 240));
            g.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, radius, radius);
            g.dispose();
        }
    }
}
