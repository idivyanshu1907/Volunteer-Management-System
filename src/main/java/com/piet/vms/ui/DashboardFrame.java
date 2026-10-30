package com.piet.vms.ui;

import com.piet.vms.model.Role;
import com.piet.vms.model.User;
import com.piet.vms.service.AppService;
import com.piet.vms.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public final class DashboardFrame extends JFrame {
    private final User user;
    private final AppService service = new AppService();
    private final JPanel content = new JPanel(new CardLayout());
    private final JPanel sidebar = new JPanel();
    private final java.util.Map<String, JPanel> pages = new java.util.LinkedHashMap<>();
    private String currentPage = "Dashboard";
    private Long editingEventId = null;

    public DashboardFrame(User user) {
        super("Volunteer Management System — " + user.role().label());
        this.user = user;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1380, 850);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BG);
        getContentPane().add(buildSidebar(), BorderLayout.WEST);
        content.setBackground(Theme.BG);
        content.setBorder(new EmptyBorder(24, 24, 24, 24));
        getContentPane().add(content, BorderLayout.CENTER);
        buildPages();
        showPage("Dashboard");
    }

    private JPanel buildSidebar() {
        sidebar.setBackground(Theme.NAVY);
        sidebar.setPreferredSize(new Dimension(242, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(18, 12, 16, 12));

        // Logo header: place the original, high-resolution PNG at
        // src/main/resources/images/vms-logo.png
        JPanel brandPanel = new JPanel();
        brandPanel.setOpaque(false);
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandPanel.setBorder(new EmptyBorder(0, 8, 22, 0));
        java.net.URL logoUrl = getClass().getResource("/images/vms-logo.png");
        if (logoUrl != null) {
            ImageIcon originalIcon = new ImageIcon(logoUrl);
            int sourceWidth = originalIcon.getIconWidth();
            int sourceHeight = originalIcon.getIconHeight();
            if (sourceWidth > 0 && sourceHeight > 0) {
                double scale = Math.min(76.0 / sourceWidth, 76.0 / sourceHeight);
                int logoWidth = Math.max(1, (int) Math.round(sourceWidth * scale));
                int logoHeight = Math.max(1, (int) Math.round(sourceHeight * scale));
                Image scaled = originalIcon.getImage().getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
                JLabel logo = new JLabel(new ImageIcon(scaled));
                logo.setAlignmentX(Component.LEFT_ALIGNMENT);
                logo.setBorder(new EmptyBorder(0, 0, 14, 0));
                brandPanel.add(logo);
            }
        }
        JLabel brand = new JLabel("<html><div style='width:190px'>Volunteer Management System</div></html>");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 15));
        brand.setForeground(Theme.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandPanel.add(brand);
        sidebar.add(brandPanel);

        JLabel name = new JLabel("  " + user.name());
        name.setFont(new Font("Segoe UI", Font.BOLD, 13));
        name.setForeground(new Color(215, 226, 242));
        name.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(name);
        JLabel role = new JLabel("  " + user.role().label().toUpperCase());
        role.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        role.setForeground(new Color(145, 174, 211));
        role.setBorder(new EmptyBorder(4, 0, 24, 0));
        role.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(role);
        for (String item : navItems()) {
            JButton b = new JButton(item);
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setForeground(new Color(205, 218, 237));
            b.setBackground(Theme.NAVY);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setOpaque(true);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setPreferredSize(new Dimension(220, 45));
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            b.setBorder(new EmptyBorder(0, 18, 0, 8));
            b.setAlignmentX(LEFT_ALIGNMENT);
            b.addActionListener(e -> showPage(item));
            b.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!item.equals(currentPage))
                        b.setBackground(Theme.NAVY2);
                }

                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!item.equals(currentPage))
                        b.setBackground(Theme.NAVY);
                }
            });
            sidebar.add(b);
        }
        sidebar.add(Box.createVerticalGlue());
        JButton signout = Theme.button("Sign out", new Color(44, 66, 97));
        signout.setAlignmentX(LEFT_ALIGNMENT);
        signout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        signout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        sidebar.add(signout);
        return sidebar;
    }

    private List<String> navItems() {
        return switch (user.role()) {
            case STUDENT -> List.of("Dashboard", "Browse Events", "My Registrations", "Volunteer Passport");
            case HOST -> List.of("Dashboard", "Create Event", "My Events", "Review Applications", "Record Attendance");
            case HOD -> List.of("Dashboard", "Pending Approvals", "All Proposals");
            case PROCTOR -> List.of("Dashboard", "Attendance Records");
        };
    }

    private void buildPages() {
        for (String n : navItems()) {
            JPanel p = switch (n) {
                case "Dashboard" -> dashboard();
                case "Browse Events" -> browseEvents();
                case "My Registrations" -> myRegistrations();
                case "Volunteer Passport" -> passport();
                case "Create Event" -> buildEventPage(editingEventId);
                case "My Events" -> myEvents();
                case "Review Applications" -> applications();
                case "Record Attendance" -> attendance();
                case "Pending Approvals" -> pendingApprovals();
                case "All Proposals" -> allProposals();
                case "Attendance Records" -> proctorAttendance();
                default -> new JPanel();
            };
            pages.put(n, p);
            content.add(p, n);
        }
    }

    private void showPage(String name) {
        currentPage = name;
        ((CardLayout) content.getLayout()).show(content, name);
        for (Component c : sidebar.getComponents())
            if (c instanceof JButton b && !b.getText().equals("Sign out")) {
                boolean active = b.getText().equals(name);
                b.setBackground(active ? Theme.BLUE : Theme.NAVY);
                b.setForeground(active ? Theme.WHITE : new Color(205, 218, 237));
            }
        refreshPage(name);
    }

    private void refreshPage(String name) {
        try {
            if (!pages.containsKey(name))
                return;
            JPanel fresh = switch (name) {
                case "Dashboard" -> dashboard();
                case "Browse Events" -> browseEvents();
                case "My Registrations" -> myRegistrations();
                case "Volunteer Passport" -> passport();
                case "Create Event" -> buildEventPage(editingEventId);
                case "My Events" -> myEvents();
                case "Review Applications" -> applications();
                case "Record Attendance" -> attendance();
                case "Pending Approvals" -> pendingApprovals();
                case "All Proposals" -> allProposals();
                case "Attendance Records" -> proctorAttendance();
                default -> pages.get(name);
            };
            content.remove(pages.get(name));
            pages.put(name, fresh);
            content.add(fresh, name);
            ((CardLayout) content.getLayout()).show(content, name);
            content.revalidate();
            content.repaint();
        } catch (RuntimeException e) {
            error(e);
        }
    }

    private JPanel dashboard() {
        JPanel p = base("Welcome back, " + user.name(), "Your " + user.role().label().toLowerCase() + " workspace");
        p.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setOpaque(false);

        List<Object[]> data = service.dashboardStats(user);
        JPanel stats = new JPanel(new GridLayout(1, Math.max(1, data.size()), 16, 0));
        stats.setOpaque(false);
        Color[] colors = { Theme.BLUE, Theme.GREEN, Theme.ORANGE };
        for (int i = 0; i < data.size(); i++) {
            String label = String.valueOf(data.get(i)[1]);
            // Make the third student metric explicit: this is an accumulated total.
            if (user.role() == Role.STUDENT && i == 2) {
                label = "Total Volunteer Hours";
            }
            stats.add(Theme.stat(String.valueOf(data.get(i)[0]), label, colors[i % colors.length]));
        }
        body.add(stats, BorderLayout.NORTH);

        JPanel lower = new JPanel(new GridLayout(1, 2, 18, 0));
        lower.setOpaque(false);

        JPanel activity = Theme.card();
        JLabel activityTitle = new JLabel(
                user.role() == Role.STUDENT ? "Recent Registration Activity" : "Workspace Overview");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        activityTitle.setForeground(Theme.TEXT);
        activityTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        activity.add(activityTitle, BorderLayout.NORTH);

        if (user.role() == Role.STUDENT) {
            List<Object[]> registrations = service.myRegistrations(user);
            if (registrations.isEmpty()) {
                JLabel empty = new JLabel(
                        "<html><div style='width:330px;line-height:1.6'>You haven't registered for an event yet.<br>When you apply to an event, its status and details will appear here.</div></html>");
                empty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                empty.setForeground(Theme.MUTED);
                activity.add(empty, BorderLayout.CENTER);
            } else {
                String[] cols = { "Event", "Date", "Status" };
                DefaultTableModel model = new DefaultTableModel(cols, 0) {
                    public boolean isCellEditable(int r, int c) {
                        return false;
                    }
                };
                int count = 0;
                for (Object[] r : registrations) {
                    if (count++ >= 5)
                        break;
                    model.addRow(new Object[] {
                            r.length > 0 ? String.valueOf(r[0]) : "—",
                            r.length > 1 ? String.valueOf(r[1]) : "—",
                            r.length > 3 ? String.valueOf(r[3]) : "—"
                    });
                }
                JTable recent = Theme.table(cols);
                recent.setModel(model);
                activity.add(Theme.scroll(recent), BorderLayout.CENTER);
            }
        } else {
            String overview = switch (user.role()) {
                case HOST ->
                    "Manage event proposals, review applications, and record attendance for accepted volunteers.";
                case HOD ->
                    "Review assigned proposals and record approval decisions. Hosts can see your feedback on their events.";
                case PROCTOR -> "Review attendance records across events. This workspace is read-only for proctors.";
                case STUDENT -> "Track your registrations and participation.";
            };
            JLabel overviewText = new JLabel(
                    "<html><div style='width:330px;line-height:1.6'>" + overview + "</div></html>");
            overviewText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            overviewText.setForeground(Theme.MUTED);
            activity.add(overviewText, BorderLayout.CENTER);
        }

        JPanel guidance = Theme.card();
        JLabel guidanceTitle = new JLabel("Your Next Steps");
        guidanceTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        guidanceTitle.setForeground(Theme.TEXT);
        guidanceTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        guidance.add(guidanceTitle, BorderLayout.NORTH);

        String guidanceText = switch (user.role()) {
            case STUDENT ->
                "Browse approved college events, check registration deadlines, and review application status here. Your Volunteer Passport records completed participation and verified service hours.";
            case HOST ->
                "Submit event proposals to the assigned HOD. After approval, review applications and record attendance for accepted volunteers.";
            case HOD ->
                "Review pending proposals, provide feedback when changes are needed, and keep event approvals moving.";
            case PROCTOR -> "Use Attendance Records to review attendance recorded by event hosts.";
        };
        JLabel guidanceBody = new JLabel(
                "<html><div style='width:330px;line-height:1.7'>" + guidanceText + "</div></html>");
        guidanceBody.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        guidanceBody.setForeground(Theme.MUTED);
        guidance.add(guidanceBody, BorderLayout.CENTER);

        lower.add(activity);
        lower.add(guidance);
        body.add(lower, BorderLayout.CENTER);
        p.add(body, BorderLayout.CENTER);
        return p;
    }

    private JPanel browseEvents() {
        JPanel p = base("Browse Events", "Explore approved opportunities and register before the deadline.");
        JTable t = table(p, "Available events", new String[] { "Event ID", "Event", "Date", "Time", "Venue",
                "Volunteers", "Reward", "Deadline", "Applications" }, service.browseEvents());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton register = Theme.button("Register for selected event", Theme.BLUE);
        register.addActionListener(e -> {
            Object id = selected(t, 0);
            if (id == null)
                return;
            confirm("Register for this event?", () -> {
                service.register(user, Long.parseLong(id.toString()));
                success("Registration submitted. The host will review your application.");
                refreshPage("Browse Events");
            });
        });
        actions.add(register);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private JPanel myRegistrations() {
        JPanel p = base("My Registrations", "Track the status of your applications.");
        table(p, "Your applications",
                new String[] { "Event", "Date", "Time", "Status", "Applied on", "Venue", "Reward" },
                service.myRegistrations(user));
        return p;
    }

    private JPanel passport() {
        JPanel p = base("Volunteer Passport", "Your recorded participation and verified service hours.");
        List<Object[]> rows = service.passport(user);
        int hours = 0;
        for (Object[] r : rows)
            hours += Integer.parseInt(String.valueOf(r[4]));
        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0));
        stats.setOpaque(false);
        stats.add(Theme.stat(String.valueOf(rows.size()), "Attendance records", Theme.BLUE));
        stats.add(Theme.stat(String.valueOf(rows.stream().filter(r -> "PRESENT".equals(r[3])).count()),
                "Events attended", Theme.GREEN));
        stats.add(Theme.stat(String.valueOf(hours), "Verified hours", Theme.ORANGE));
        p.add(stats, BorderLayout.NORTH);
        JTable t = table(p, "Participation history",
                new String[] { "Event", "Date", "Venue", "Attendance", "Verified hours" }, rows);
        return p;
    }

    private JPanel createEventPage() {
        return buildEventPage(null);
    }

    private JPanel buildEventPage(Long editId) {
        JPanel p = base(editId == null ? "Create New Event" : "Edit Event",
                "Event details are submitted to the assigned HOD for approval.");
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);
        JPanel form = Theme.card();
        form.setPreferredSize(new Dimension(900, 560));
        form.setLayout(new GridBagLayout());
        JTextField title = new JTextField(), date = new JTextField(LocalDate.now().plusDays(7).toString()),
                time = new JTextField("09:00"), venue = new JTextField(), volunteers = new JTextField("10"),
                deadline = new JTextField(LocalDate.now().plusDays(5).toString()), reward = new JTextField();
        JTextArea description = new JTextArea(3, 30);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        JComboBox<AppService.HodOption> hod = new JComboBox<>();
        for (var h : service.getHods())
            hod.addItem(h);
        if (hod.getItemCount() == 0) {
            errorMessage("No HOD accounts exist. Add an HOD user to the database first.");
        }
        if (editId != null) {
            Object[] r = service.eventDetails(user, editId);
            title.setText(String.valueOf(r[0]));
            description.setText(r[1] == null ? "" : String.valueOf(r[1]));
            date.setText(String.valueOf(r[2]));
            time.setText(String.valueOf(r[3]).substring(0, 5));
            venue.setText(String.valueOf(r[4]));
            volunteers.setText(String.valueOf(r[5]));
            deadline.setText(String.valueOf(r[6]));
            reward.setText(r[7] == null ? "" : String.valueOf(r[7]));
            for (int i = 0; i < hod.getItemCount(); i++)
                if (hod.getItemAt(i).id() == ((Number) r[8]).longValue())
                    hod.setSelectedIndex(i);
        }
        int row = 0;
        row = field(form, row, "Event title *", title);
        row = field(form, row, "Description", new JScrollPane(description));
        row = field(form, row, "Event date * (YYYY-MM-DD)", date);
        row = field(form, row, "Event time * (HH:MM)", time);
        row = field(form, row, "Venue *", venue);
        row = field(form, row, "Volunteers required *", volunteers);
        row = field(form, row, "Registration deadline *", deadline);
        row = field(form, row, "Reward", reward);
        row = field(form, row, "Assign HOD *", hod);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        JButton save = Theme.button(editId == null ? "Submit for approval" : "Save changes & request approval",
                Theme.BLUE);
        JButton clear = Theme.button("Clear", new Color(108, 117, 125));
        clear.addActionListener(e -> {
            title.setText("");
            description.setText("");
            venue.setText("");
            reward.setText("");
        });
        save.addActionListener(e -> {
            try {
                if (hod.getSelectedItem() == null)
                    throw new ServiceException("Select an HOD.");
                AppService.EventForm f = new AppService.EventForm(title.getText(), description.getText(),
                        LocalDate.parse(date.getText().trim()), LocalTime.parse(time.getText().trim()), venue.getText(),
                        Integer.parseInt(volunteers.getText().trim()), LocalDate.parse(deadline.getText().trim()),
                        reward.getText(), ((AppService.HodOption) hod.getSelectedItem()).id());
                if (editId == null)
                    service.createEvent(user, f);
                else
                    service.updateEvent(user, editId, f);
                success(editId == null ? "Event proposal submitted for HOD approval."
                        : "Event updated and sent back for HOD approval.");
                editingEventId = null;
                showPage("My Events");
            } catch (DateTimeParseException ex) {
                errorMessage("Use YYYY-MM-DD for dates and HH:MM for time.");
            } catch (NumberFormatException ex) {
                errorMessage("Volunteers required must be a whole number.");
            } catch (RuntimeException ex) {
                error(ex);
            }
        });
        buttons.add(clear);
        buttons.add(save);
        form.add(buttons, grid(0, row, 2));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(0, 0, 20, 0);
        outer.add(form, c);
        p.add(new JScrollPane(outer), BorderLayout.CENTER);
        return p;
    }

    private JPanel myEvents() {
        JPanel p = base("My Events", "Review your proposals, edit event details, or cancel an approved event.");
        JTable t = table(p, "Events created by you", new String[] { "Event ID", "Title", "Date", "Time", "Venue",
                "Volunteers", "Status", "HOD feedback", "Deadline", "Reward" }, service.hostEvents(user));
        JPanel a = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        a.setOpaque(false);
        JButton edit = Theme.button("Edit selected", Theme.BLUE),
                cancel = Theme.button("Cancel approved event", Theme.RED),
                create = Theme.button("Create event", Theme.GREEN);
        edit.addActionListener(e -> {
            Object id = selected(t, 0);
            if (id != null) {
                editingEventId = Long.parseLong(id.toString());
                showPage("Create Event");
            }
        });
        cancel.addActionListener(e -> {
            Object id = selected(t, 0);
            if (id != null)
                confirm("Cancel this approved event? Students will no longer be able to register.", () -> {
                    service.cancelEvent(user, Long.parseLong(id.toString()));
                    success("Event cancelled.");
                    refreshPage("My Events");
                });
        });
        create.addActionListener(e -> {
            editingEventId = null;
            showPage("Create Event");
        });
        a.add(create);
        a.add(edit);
        a.add(cancel);
        p.add(a, BorderLayout.SOUTH);
        return p;
    }

    private JPanel applications() {
        JPanel p = base("Review Applications",
                "Accept or reject student applications. Accepted volunteers appear in attendance records.");
        JTable t = table(p, "Student applications",
                new String[] { "Registration ID", "Event", "Student", "College ID", "Email", "Status", "Applied on" },
                service.hostApplications(user, null));
        JPanel a = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        a.setOpaque(false);
        JButton accept = Theme.button("Accept volunteer", Theme.GREEN),
                reject = Theme.button("Reject application", Theme.RED);
        accept.addActionListener(e -> decideApplication(t, "ACCEPTED"));
        reject.addActionListener(e -> decideApplication(t, "REJECTED"));
        a.add(accept);
        a.add(reject);
        p.add(a, BorderLayout.SOUTH);
        return p;
    }

    private void decideApplication(JTable t, String status) {
        Object id = selected(t, 0);
        if (id != null)
            confirm((status.equals("ACCEPTED") ? "Accept" : "Reject") + " this application?", () -> {
                service.decideApplication(user, Long.parseLong(id.toString()), status);
                success("Application " + status.toLowerCase() + ".");
                refreshPage("Review Applications");
            });
    }

    private JPanel attendance() {
        JPanel p = base("Record Attendance",
                "Only accepted volunteers are listed. Mark each person present or absent.");
        List<Object[]> events = service.attendanceEvents(user);
        JComboBox<EventChoice> choice = new JComboBox<>();
        for (Object[] e : events)
            choice.addItem(new EventChoice(((Number) e[0]).longValue(), e[1] + " — " + e[2]));
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filter.setOpaque(false);
        filter.add(new JLabel("Event:"));
        filter.add(choice);
        JTable t = Theme.table("Registration ID", "Student ID", "Student", "College ID", "Attendance");
        Runnable load = () -> {
            if (choice.getSelectedItem() == null) {
                setRows(t, List.of());
                return;
            }
            EventChoice e = (EventChoice) choice.getSelectedItem();
            setRows(t, service.hostAttendance(user, e.id));
        };
        choice.addActionListener(e -> load.run());
        load.run();
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(filter, BorderLayout.NORTH);
        center.add(Theme.scroll(t), BorderLayout.CENTER);
        p.add(center, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton present = Theme.button("Mark Present", Theme.GREEN), absent = Theme.button("Mark Absent", Theme.RED);
        present.addActionListener(e -> mark(t, choice, "PRESENT", load));
        absent.addActionListener(e -> mark(t, choice, "ABSENT", load));
        actions.add(present);
        actions.add(absent);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private void mark(JTable t, JComboBox<EventChoice> choice, String status, Runnable load) {
        Object id = selected(t, 1);
        if (id == null || choice.getSelectedItem() == null)
            return;
        EventChoice e = (EventChoice) choice.getSelectedItem();
        try {
            service.markAttendance(user, e.id, Long.parseLong(id.toString()), status);
            success("Attendance saved as " + status + ".");
            load.run();
        } catch (RuntimeException ex) {
            error(ex);
        }
    }

    private JPanel pendingApprovals() {
        JPanel p = base("Pending Approvals", "Select a proposal and approve, reject, or request changes.");
        JTable t = table(p, "Waiting for your review", new String[] { "Event ID", "Title", "Date", "Time", "Host",
                "Venue", "Volunteers", "Deadline", "Reward", "Status" }, service.hodPending(user));
        JPanel a = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        a.setOpaque(false);
        JButton approve = Theme.button("Approve", Theme.GREEN), reject = Theme.button("Reject", Theme.RED),
                changes = Theme.button("Request changes", Theme.ORANGE);
        approve.addActionListener(e -> hodDecision(t, "APPROVED"));
        reject.addActionListener(e -> hodDecision(t, "REJECTED"));
        changes.addActionListener(e -> hodDecision(t, "CHANGES_REQUESTED"));
        a.add(approve);
        a.add(reject);
        a.add(changes);
        p.add(a, BorderLayout.SOUTH);
        return p;
    }

    private void hodDecision(JTable t, String status) {
        Object id = selected(t, 0);
        if (id == null)
            return;
        String feedback = "";
        if (!status.equals("APPROVED")) {
            feedback = JOptionPane.showInputDialog(this,
                    status.equals("REJECTED") ? "Reason for rejection:" : "Changes required:");
            if (feedback == null)
                return;
            if (feedback.isBlank()) {
                errorMessage("Please provide feedback for the host.");
                return;
            }
        }
        final String f = feedback;
        confirm("Confirm decision: " + status.replace('_', ' ') + "?", () -> {
            service.decideEvent(user, Long.parseLong(id.toString()), status, f);
            success("Decision saved.");
            refreshPage("Pending Approvals");
        });
    }

    private JPanel allProposals() {
        JPanel p = base("All Proposals", "All event proposals assigned to you and their current status.");
        table(p, "Department proposals", new String[] { "Event ID", "Title", "Date", "Time", "Host", "Venue",
                "Volunteers", "Status", "HOD feedback" }, service.hodAll(user));
        return p;
    }

    private JPanel proctorAttendance() {
        JPanel p = base("Attendance Records", "Read-only view of attendance recorded by event hosts.");
        table(p, "Recorded attendance",
                new String[] { "Event", "Date", "Student", "College ID", "Status", "Recorded by", "Last updated" },
                service.proctorAttendance());
        JLabel note = new JLabel("Attendance is view-only for proctors.");
        note.setForeground(Theme.MUTED);
        note.setBorder(new EmptyBorder(10, 2, 0, 0));
        p.add(note, BorderLayout.SOUTH);
        return p;
    }

    private JPanel base(String heading, String subtitle) {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBackground(Theme.BG);
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JLabel h = Theme.title(heading);
        h.setAlignmentX(LEFT_ALIGNMENT);
        top.add(h);
        JLabel sub = new JLabel(subtitle);
        sub.setForeground(Theme.MUTED);
        sub.setBorder(new EmptyBorder(6, 0, 0, 0));
        sub.setAlignmentX(LEFT_ALIGNMENT);
        top.add(sub);
        p.add(top, BorderLayout.NORTH);
        return p;
    }

    private JTable table(JPanel parent, String cardTitle, String[] columns, List<Object[]> rows) {
        JTable t = Theme.table(columns);
        setRows(t, rows);
        JPanel card = Theme.card();
        JLabel h = new JLabel(cardTitle);
        h.setFont(new Font("Segoe UI", Font.BOLD, 16));
        h.setForeground(Theme.TEXT);
        card.add(h, BorderLayout.NORTH);
        card.add(Theme.scroll(t), BorderLayout.CENTER);
        parent.add(card, BorderLayout.CENTER);
        return t;
    }

    private void setRows(JTable table, List<Object[]> rows) {
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        m.setRowCount(0);
        for (Object[] r : rows)
            m.addRow(r);
    }

    private Object selected(JTable t, int column) {
        int row = t.getSelectedRow();
        if (row < 0) {
            errorMessage("Select a row first.");
            return null;
        }
        return t.getValueAt(row, column);
    }

    private void confirm(String message, Runnable task) {
        if (JOptionPane.showConfirmDialog(this, message, "Please confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                task.run();
            } catch (RuntimeException e) {
                error(e);
            }
        }
    }

    private void success(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(RuntimeException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Action failed", JOptionPane.WARNING_MESSAGE);
    }

    private void errorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Check input", JOptionPane.WARNING_MESSAGE);
    }

    private int field(JPanel panel, int row, String label, JComponent input) {
        JLabel l = new JLabel(label);
        l.setForeground(Theme.TEXT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        GridBagConstraints a = grid(0, row, 1);
        a.weightx = 0;
        a.fill = GridBagConstraints.NONE;
        a.anchor = GridBagConstraints.WEST;
        a.insets = new Insets(6, 8, 6, 14);
        panel.add(l, a);
        if (input instanceof JScrollPane) {
            input.setPreferredSize(new Dimension(300, 78));
        } else {
            Theme.styleInput(input);
        }
        GridBagConstraints b = grid(1, row, 1);
        b.weightx = 1;
        b.fill = GridBagConstraints.HORIZONTAL;
        b.insets = new Insets(6, 4, 6, 8);
        panel.add(input, b);
        return row + 1;
    }

    private GridBagConstraints grid(int x, int y, int width) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = width;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(2, 4, 2, 4);
        return c;
    }

    private record EventChoice(long id, String label) {
        @Override
        public String toString() {
            return label;
        }
    }
}
