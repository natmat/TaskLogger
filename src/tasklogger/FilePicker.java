package tasklogger;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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
		JFrame f = new JFrame();
		f.add(fp);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setLocationRelativeTo(null);    // Centre on screen

		f.setVisible(true);

		System.out.println("PFM=" + pickInputFile(true));
	}

	public FilePicker(String textFieldLabel, String buttonLabel) {
		fileChooser = new JFileChooser();
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		label = new JLabel(textFieldLabel);
		textField = new JTextField(30);

		button = new JButton(buttonLabel);		
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				buttonActionPerformed(e);
			}
		});

		add(label);
		add(textField);
		add(button);
	}

	private void buttonActionPerformed(ActionEvent evt) {
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			inputFileName = fileChooser.getSelectedFile().getAbsolutePath();
//			textField.setText(fileChooser.getSelectedFile().getAbsolutePath());		
			setVisible(false);
		}
	}


	public static String pickInputFile(boolean show) {
//		setVisible(show);
		return(inputFileName);	
	}
}



