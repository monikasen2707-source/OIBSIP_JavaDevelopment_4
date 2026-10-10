OIBSIP_JavaDevelopment_Task4 · Online Examination System

A desktop examination application built with Java and Swing. Students log in, update their profile, answer multiple-choice questions against a countdown timer, and see a detailed result at the end. If the timer reaches zero, the exam is submitted automatically.

Objective

Build a GUI-based online examination system where students can:

log in securely,
answer MCQs one at a time within a fixed time limit,
be auto-submitted when time runs out,
review their score and a correct/incorrect breakdown.
Features
Feature	Description
Login	Username + password validation with error messages
Profile update	Change display name and password before starting the exam
Exam screen	One MCQ at a time with 4 radio-button options
Navigation	Previous / Next buttons; selected answers are remembered
Countdown timer	30-minute timer always visible; turns red in the last 5 minutes; auto-submits at 00:00
Manual submit	Submit button with a confirmation dialog (warns about unanswered questions)
Result screen	Score (X out of Y), time taken, correct / incorrect / unanswered counts, per-question breakdown table
Session management	Closing the window during an exam shows "Are you sure you want to quit?"
Logout	Logout button on the result screen returns to the login screen
Tools & Technologies
Language: Java (JDK 8 or newer)
GUI: Swing (JFrame, JPanel, JRadioButton, ButtonGroup, JTable, JOptionPane)
Screen switching: CardLayout
Timer: javax.swing.Timer (fires every second on the Event Dispatch Thread)
Steps Performed
Designed four screens (Login, Profile, Exam, Result) and managed them with a single CardLayout.
Created User and Question data models and a sample question bank with 10 Java MCQs.
Implemented login validation and the profile update screen (name and password change with validation).
Built the exam screen: question text, four radios grouped with ButtonGroup, and Previous/Next navigation that stores each answer in an array.
Added a javax.swing.Timer countdown that updates the label every second and triggers auto-submit at zero.
Added manual submit with a confirmation dialog.
Built the result screen with the score, time taken, summary counts, and a colour-coded breakdown table.
Handled the window close button with DO_NOTHING_ON_CLOSE and a custom quit confirmation while an exam is active.
Added logout to return to the login screen.
How to Run
bash
javac OnlineExamSystem.java
java OnlineExamSystem

Demo accounts

Username	Password
student1	pass123
student2	pass456

To test auto-submit quickly, set EXAM_SECONDS at the top of the file to 60.

Project Structure
OIBSIP_JavaDevelopment_Task4/
├── OnlineExamSystem.java
├── README.md
└── screenshots/
    ├── 01-login.png
    ├── 02-profile.png
    ├── 03-exam.png
    └── 04-result.png

Demo Video
https://lnkd.in/p/gXtaNMFp

Watch the demo

Outcome

A working, single-file Swing application that covers the full exam flow: login → profile update → timed MCQ exam → result → logout. I learned how to structure a multi-screen Swing app with CardLayout, drive a countdown with javax.swing.Timer, keep UI state (answers, current question) separate from the view, and handle window-close events for session management.

Possible Improvements
Load questions and users from a database (SQLite/MySQL) or a file instead of hard-coding them
Hash stored passwords
Add a question navigator panel and a "mark for review" option
Add an admin screen to create exams and view all results
Author

Monika S · www.linkedin.com/in/monika-s-54596339a · https://github.com/monikasen2707-source

Completed as part of the Oasis Infobyte Summer Internship Program (OIBSIP).
