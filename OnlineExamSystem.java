import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
public class OnlineExamSystem extends JFrame
 {
    private static final int EXAM_SECONDS = 30 * 60;   // 30 minutes (change for quick testing, e.g. 60)
    private static final int WARNING_SECONDS = 5 * 60; // timer turns red below this

    private static final String LOGIN = "login";
    private static final String PROFILE = "profile";
    private static final String EXAM = "exam";
    private static final String RESULT = "result";
    private static class User {
        final String username;
        String password;
        String displayName;

        User(String username, String password, String displayName) {
            this.username = username;
            this.password = password;
            this.displayName = displayName;
        }
    }

    private static class Question {
        final String text;
        final String[] options;
        final int correct; // index 0..3

        Question(String text, String a, String b, String c, String d, int correct) {
            this.text = text;
            this.options = new String[]{a, b, c, d};
            this.correct = correct;
        }
    }
    private final Map<String, User> users = new HashMap<>();
    private final List<Question> bank = new ArrayList<>();
    private User currentUser;
    private List<Question> examQuestions;
    private int[] answers;          
    private int current;
    private int remaining;
    private boolean examActive;
    private javax.swing.Timer timer;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);
    private JTextField loginUser;
    private JPasswordField loginPass;
    private JLabel loginMsg;
    private JButton loginButton;
    private JLabel profileWelcome;
    private JTextField nameField;
    private JPasswordField newPass, confirmPass;
    private JLabel profileMsg;
    private JLabel examHeader, counterLabel, timerLabel, answeredLabel;
    private JTextArea questionArea;
    private final JRadioButton[] radios = new JRadioButton[4];
    private final ButtonGroup group = new ButtonGroup();
    private JButton prevBtn, nextBtn, submitBtn;
    private JLabel scoreLabel, timeLabel, summaryLabel, resultHeader;
    private DefaultTableModel tableModel;
    public OnlineExamSystem() {
        super("Online Examination System");
        seedData();

        root.add(buildLoginPanel(), LOGIN);
        root.add(buildProfilePanel(), PROFILE);
        root.add(buildExamPanel(), EXAM);
        root.add(buildResultPanel(), RESULT);
        setContentPane(root);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClose();
            }
        });

        setSize(820, 580);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);
        cardLayout.show(root, LOGIN);
    }
    private void seedData() {
        users.put("student1", new User("student1", "pass123", "Student One"));
        users.put("student2", new User("student2", "pass456", "Student Two"));

        bank.add(new Question("Which keyword is used to inherit a class in Java?",
                "implements", "extends", "inherits", "super", 1));
        bank.add(new Question("Which of these is NOT a primitive data type in Java?",
                "int", "boolean", "String", "char", 2));
        bank.add(new Question("What is the default value of an int instance variable?",
                "null", "1", "0", "undefined", 2));
        bank.add(new Question("Which Swing class is used to show a modal pop-up message?",
                "JFrame", "JOptionPane", "JPanel", "JLabel", 1));
        bank.add(new Question("Which layout manager lets you switch between several panels like cards?",
                "BorderLayout", "FlowLayout", "GridLayout", "CardLayout", 3));
        bank.add(new Question("Which method is the entry point of a Java application?",
                "start()", "run()", "main()", "init()", 2));
        bank.add(new Question("Which collection does NOT allow duplicate elements?",
                "ArrayList", "LinkedList", "HashSet", "Vector", 2));
        bank.add(new Question("What does the 'final' keyword on a variable mean?",
                "It can be overridden", "Its value cannot be changed after assignment",
                "It is static", "It is private", 1));
        bank.add(new Question("Which Swing component groups radio buttons so only one can be selected?",
                "ButtonGroup", "JPanel", "JList", "JToggleBar", 0));
        bank.add(new Question("Swing GUI updates should be done on which thread?",
                "Main thread", "Garbage collector thread", "Event Dispatch Thread", "Any new thread", 2));
    }
    private JPanel buildLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(245, 247, 250));
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 222)),
                new EmptyBorder(28, 36, 28, 36)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridwidth = 2;
        JLabel title = new JLabel("Online Examination System", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        c.gridy = 0;
        card.add(title, c);
        JLabel sub = new JLabel("Student Login", SwingConstants.CENTER);
        sub.setForeground(Color.GRAY);
        c.gridy = 1;
        card.add(sub, c);
        c.gridwidth = 1;
        c.gridy = 2; c.gridx = 0;
        card.add(new JLabel("Username:"), c);
        loginUser = new JTextField(16);
        c.gridx = 1;
        card.add(loginUser, c);
        c.gridy = 3; c.gridx = 0;
        card.add(new JLabel("Password:"), c);
        loginPass = new JPasswordField(16);
        c.gridx = 1;
        card.add(loginPass, c);
        loginMsg = new JLabel(" ", SwingConstants.CENTER);
        loginMsg.setForeground(new Color(190, 40, 40));
        c.gridy = 4; c.gridx = 0; c.gridwidth = 2;
        card.add(loginMsg, c);
        JButton loginBtn = new JButton("Login");
        loginButton = loginBtn;
        c.gridy = 5;
        card.add(loginBtn, c);
        JLabel hint = new JLabel("Demo: student1 / pass123", SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);
        hint.setFont(hint.getFont().deriveFont(11f));
        c.gridy = 6;
        card.add(hint, c);
        loginBtn.addActionListener(e -> doLogin());
        loginPass.addActionListener(e -> doLogin()); // Enter key
        loginUser.addActionListener(e -> loginPass.requestFocusInWindow());
        getRootPane().setDefaultButton(loginBtn);
        p.add(card);
        return p;
    }
    private void doLogin() {
        String u = loginUser.getText().trim();
     String pw = new String(loginPass.getPassword());
        if (u.isEmpty() || pw.isEmpty()) {
            loginMsg.setText("Please enter both username and password.");
            return;
        }
        User user = users.get(u);
        if (user == null || !user.password.equals(pw)) {
            loginMsg.setText("Invalid username or password.");
            loginPass.setText("");
            return;
        }
        currentUser = user;
        loginMsg.setText(" ");
        loginUser.setText("");
        loginPass.setText("");
        getRootPane().setDefaultButton(null);
        showProfile();
    }
     private JPanel buildProfilePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(245, 247, 250));
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 222)),
                new EmptyBorder(24, 36, 24, 36)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        profileWelcome = new JLabel("Welcome", SwingConstants.CENTER);
        profileWelcome.setFont(profileWelcome.getFont().deriveFont(Font.BOLD, 20f));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(profileWelcome, c);
        JLabel info = new JLabel("<html><div style='text-align:center'>Update your profile (optional), then start the exam.<br>"
                + "The exam has " + bank.size() + " questions and lasts " + (EXAM_SECONDS / 60) + " minutes.</div></html>",
                SwingConstants.CENTER);
        info.setForeground(Color.DARK_GRAY);
        c.gridy = 1;
        card.add(info, c);
        c.gridwidth = 1;
        c.gridy = 2; c.gridx = 0;
        card.add(new JLabel("Display name:"), c);
        nameField = new JTextField(18);
        c.gridx = 1;
        card.add(nameField, c);
        c.gridy = 3; c.gridx = 0;
        card.add(new JLabel("New password:"), c);
        newPass = new JPasswordField(18);
        c.gridx = 1;
        card.add(newPass, c);
        c.gridy = 4; c.gridx = 0;
        card.add(new JLabel("Confirm password:"), c);
        confirmPass = new JPasswordField(18);
        c.gridx = 1;
        card.add(confirmPass, c);
        JLabel note = new JLabel("Leave the password fields empty to keep your current password.");
        note.setFont(note.getFont().deriveFont(11f));
        note.setForeground(Color.GRAY);
        c.gridy = 5; c.gridx = 0; c.gridwidth = 2;
        card.add(note, c);
        profileMsg = new JLabel(" ", SwingConstants.CENTER);
        c.gridy = 6;
        card.add(profileMsg, c);
        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.setOpaque(false);
        JButton saveBtn = new JButton("Save Changes");
        JButton startBtn = new JButton("Start Exam");
        JButton logoutBtn = new JButton("Logout");
        buttons.add(saveBtn);
        buttons.add(startBtn);
        c.gridy = 7;
        card.add(buttons, c);
        c.gridy = 8;
        card.add(logoutBtn, c);
        saveBtn.addActionListener(e -> saveProfile());
        startBtn.addActionListener(e -> startExam());
        logoutBtn.addActionListener(e -> logout());
        p.add(card);
        return p;
    }
    private void showProfile() {
        profileWelcome.setText("Welcome, " + currentUser.displayName);
        nameField.setText(currentUser.displayName);
        newPass.setText("");
        confirmPass.setText("");
        setProfileMessage(" ", false);
        cardLayout.show(root, PROFILE);
    }
    private void setProfileMessage(String msg, boolean ok) {
        profileMsg.setForeground(ok ? new Color(30, 130, 60) : new Color(190, 40, 40));
        profileMsg.setText(msg);
    }
    private boolean saveProfile() {
        String name = nameField.getText().trim();
        String np = new String(newPass.getPassword());
        String cp = new String(confirmPass.getPassword());

        if (name.isEmpty()) {
            setProfileMessage("Display name cannot be empty.", false);
            return false;
        }
        if (!np.isEmpty() || !cp.isEmpty()) {
            if (np.length() < 4) {
                setProfileMessage("Password must be at least 4 characters.", false);
                return false;
            }
            if (!np.equals(cp)) {
                setProfileMessage("Passwords do not match.", false);
                return false;
            }
            currentUser.password = np;
        }
        currentUser.displayName = name;
        profileWelcome.setText("Welcome, " + name);
        newPass.setText("");
        confirmPass.setText("");
        setProfileMessage("Profile updated successfully.", true);
        return true;
    }
    private JPanel buildExamPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBorder(new EmptyBorder(14, 20, 14, 20));
        JPanel top = new JPanel(new BorderLayout());
        examHeader = new JLabel("Student");
        examHeader.setFont(examHeader.getFont().deriveFont(Font.BOLD, 14f));
        counterLabel = new JLabel("Question 1 of 1", SwingConstants.CENTER);
        counterLabel.setFont(counterLabel.getFont().deriveFont(Font.BOLD, 14f));
        timerLabel = new JLabel("Time Left: 30:00", SwingConstants.RIGHT);
        timerLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        top.add(examHeader, BorderLayout.WEST);
        top.add(counterLabel, BorderLayout.CENTER);
        top.add(timerLabel, BorderLayout.EAST);
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                new EmptyBorder(0, 0, 8, 0)));
        p.add(top, BorderLayout.NORTH);
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        questionArea = new JTextArea(3, 40);
        questionArea.setEditable(false);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setOpaque(false);
        questionArea.setFont(questionArea.getFont().deriveFont(Font.BOLD, 18f));
        questionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionArea.setBorder(new EmptyBorder(10, 0, 14, 0));
        center.add(questionArea);

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            radios[i] = new JRadioButton();
            radios[i].setFont(radios[i].getFont().deriveFont(16f));
            radios[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            radios[i].setBorder(new EmptyBorder(8, 4, 8, 4));
            radios[i].setFocusPainted(false);
            group.add(radios[i]);
            radios[i].addActionListener(e -> {
                answers[current] = idx;
                updateAnsweredLabel();
            });
            center.add(radios[i]);
        }
        p.add(center, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        prevBtn = new JButton("\u25C0 Previous");
        nextBtn = new JButton("Next \u25B6");
        submitBtn = new JButton("Submit Exam");
        submitBtn.setBackground(new Color(200, 60, 60));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setOpaque(true);
        submitBtn.setBorderPainted(false);

        answeredLabel = new JLabel("Answered: 0/0", SwingConstants.CENTER);

        JPanel navLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        navLeft.add(prevBtn);
        navLeft.add(nextBtn);
        bottom.add(navLeft, BorderLayout.WEST);
        bottom.add(answeredLabel, BorderLayout.CENTER);
        bottom.add(submitBtn, BorderLayout.EAST);
        p.add(bottom, BorderLayout.SOUTH);

        prevBtn.addActionListener(e -> {
            if (current > 0) {
                current--;
                loadQuestion();
            }
        });
        nextBtn.addActionListener(e -> {
            if (current < examQuestions.size() - 1) {
                current++;
                loadQuestion();
            }
        });
        submitBtn.addActionListener(e -> confirmAndSubmit());

        return p;
    }

    private void startExam() {
        boolean pending = !nameField.getText().trim().equals(currentUser.displayName)
                || newPass.getPassword().length > 0 || confirmPass.getPassword().length > 0;
        if (pending && !saveProfile()) {
            return;
        }

        examQuestions = new ArrayList<>(bank);
        Collections.shuffle(examQuestions);
        answers = new int[examQuestions.size()];
        java.util.Arrays.fill(answers, -1);
        current = 0;
        remaining = EXAM_SECONDS;
        examActive = true;

        examHeader.setText("Student: " + currentUser.displayName);
        updateTimerLabel();
        loadQuestion();
        cardLayout.show(root, EXAM);

        if (timer != null) timer.stop();
        timer = new javax.swing.Timer(1000, e -> tick());
        timer.start();
    }

    private void tick() {
        remaining--;
        updateTimerLabel();
        if (remaining <= 0) {
            autoSubmit();
        }
    }

    private void updateTimerLabel() {
        int m = Math.max(remaining, 0) / 60;
        int s = Math.max(remaining, 0) % 60;
        timerLabel.setText(String.format("Time Left: %02d:%02d", m, s));
        timerLabel.setForeground(remaining <= WARNING_SECONDS ? new Color(200, 30, 30) : Color.BLACK);
    }

    private void loadQuestion() {
        Question q = examQuestions.get(current);
        counterLabel.setText("Question " + (current + 1) + " of " + examQuestions.size());
        questionArea.setText((current + 1) + ". " + q.text);

        group.clearSelection();
        for (int i = 0; i < 4; i++) {
            radios[i].setText((char) ('A' + i) + ")  " + q.options[i]);
        }
        if (answers[current] >= 0) {
            radios[answers[current]].setSelected(true);
        }
        prevBtn.setEnabled(current > 0);
        nextBtn.setEnabled(current < examQuestions.size() - 1);
        updateAnsweredLabel();
    }

    private void updateAnsweredLabel() {
        int done = 0;
        for (int a : answers) if (a >= 0) done++;
        answeredLabel.setText("Answered: " + done + "/" + answers.length);
    }
    private void confirmAndSubmit() {
        int unanswered = 0;
        for (int a : answers) if (a < 0) unanswered++;
        String msg = unanswered > 0
                ? "You have " + unanswered + " unanswered question(s).\nDo you really want to submit the exam?"
                : "Do you really want to submit the exam?";
        int choice = JOptionPane.showConfirmDialog(this, msg, "Confirm Submission",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION && examActive) {
            finishExam(false);
        }
    }

    private void autoSubmit() {
        if (!examActive) return;
        finishExam(true);
    }

    private void finishExam(boolean timedOut) {
        examActive = false;
        if (timer != null) timer.stop();
        showResult();
        if (timedOut) {
            JOptionPane.showMessageDialog(this, "Time is up! Your exam was submitted automatically.",
                    "Time's Up", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private JPanel buildResultPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel head = new JPanel();
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        resultHeader = new JLabel("Exam Result");
        resultHeader.setFont(resultHeader.getFont().deriveFont(Font.BOLD, 22f));
        scoreLabel = new JLabel("Score: -");
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 28f));
        scoreLabel.setForeground(new Color(30, 90, 170));
        timeLabel = new JLabel("Time taken: -");
        summaryLabel = new JLabel("-");
        for (JLabel l : new JLabel[]{resultHeader, scoreLabel, timeLabel, summaryLabel}) {
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            head.add(l);
            head.add(Box.createVerticalStrut(4));
        }
        p.add(head, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"#", "Question", "Your Answer", "Correct Answer", "Result"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(4).setMaxWidth(90);
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                String s = String.valueOf(v);
                if (!sel) {
                    c.setForeground("Correct".equals(s) ? new Color(25, 130, 55)
                            : "Incorrect".equals(s) ? new Color(190, 35, 35) : Color.GRAY);
                }
                setFont(getFont().deriveFont(Font.BOLD));
                return c;
            }
        });
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(logoutBtn);
        p.add(south, BorderLayout.SOUTH);

        return p;
    }

    private void showResult() {
        int total = examQuestions.size();
        int correct = 0, incorrect = 0, skipped = 0;
        tableModel.setRowCount(0);

        for (int i = 0; i < total; i++) {
            Question q = examQuestions.get(i);
            String yours, status;
            if (answers[i] < 0) {
                yours = "Not answered";
                status = "Skipped";
                skipped++;
            } else {
                yours = (char) ('A' + answers[i]) + ") " + q.options[answers[i]];
                if (answers[i] == q.correct) {
                    status = "Correct";
                    correct++;
                } else {
                    status = "Incorrect";
                    incorrect++;
                }
            }
            String right = (char) ('A' + q.correct) + ") " + q.options[q.correct];
            tableModel.addRow(new Object[]{i + 1, q.text, yours, right, status});
        }

        int taken = Math.min(EXAM_SECONDS, EXAM_SECONDS - Math.max(remaining, 0));
        int pct = total == 0 ? 0 : Math.round(correct * 100f / total);

        resultHeader.setText("Exam Result - " + currentUser.displayName);
        scoreLabel.setText("Score: " + correct + " out of " + total + "  (" + pct + "%)");
        timeLabel.setText(String.format("Time taken: %02d:%02d (of %02d:00)",
                taken / 60, taken % 60, EXAM_SECONDS / 60));
        summaryLabel.setText("Correct: " + correct + "    Incorrect: " + incorrect + "    Not answered: " + skipped);

        cardLayout.show(root, RESULT);
    }
    private void logout() {
        currentUser = null;
        examActive = false;
        if (timer != null) timer.stop();
        loginMsg.setText(" ");
        cardLayout.show(root, LOGIN);
        getRootPane().setDefaultButton(loginButton);
        loginUser.requestFocusInWindow();
    }

    private void handleWindowClose() {
        if (examActive) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to quit?\nYour exam will NOT be submitted.",
                    "Quit Exam", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return; // stay in the exam
            }
        }
        if (timer != null) timer.stop();
        dispose();
        System.exit(0);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new OnlineExamSystem().setVisible(true);
        });
    }
}