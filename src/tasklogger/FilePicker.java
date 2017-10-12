package tasklogger;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FilePicker extends JPanel {
	private static final long serialVersionUID = -6729743289945525689L;

	private static JFileChooser fileChooser;
	private static JLabel label;
	private static JTextField textField;
	private static JButton button;
	private static String inputFileName;

	public static void main(String args[]) {
		FilePicker fp = new FilePicker("Open a task list file", "Browse...");
		JFrame frame = new JFrame();
		frame.setTitle("Select a file to open");
		frame.add(fp);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);    // Centre on screen

		frame.setVisible(true);
	}

	public FilePicker(String textFieldLabel, String buttonLabel) {
		FilePicker.fileChooser = new JFileChooser("C:/My_Workspaces/MyGit/MyJava/TaskLogger");
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		FilePicker.label = new JLabel(textFieldLabel);
		FilePicker.textField = new JTextField(30);

		FilePicker.button = new JButton(buttonLabel);		
		FilePicker.button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				buttonActionPerformed(e);
			}
		});

		add(FilePicker.label);
		add(FilePicker.textField);
		add(FilePicker.button);
	}
	
	private void buttonActionPerformed(ActionEvent evt) {
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			inputFileName = fileChooser.getSelectedFile().getAbsolutePath();
			textField.setText(inputFileName);
			System.out.println("DONE " + inputFileName);
			fileChooser.setVisible(false);
		}
	}
}



