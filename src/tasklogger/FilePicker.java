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
		frame.add(fp);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);    // Centre on screen

		frame.setVisible(true);

		System.out.println("PFM=" + pickInputFile(true));
	}

	public FilePicker(String textFieldLabel, String buttonLabel) {
		setFileChooser(new JFileChooser());
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
	
	private static void setFileChooser(final JFileChooser fc) {
		FilePicker.fileChooser = fc;
	}

	private void buttonActionPerformed(ActionEvent evt) {
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			inputFileName = fileChooser.getSelectedFile().getAbsolutePath();
//			textField.setText(fileChooser.getSelectedFile().getAbsolutePath());		
			setVisible(false);
			System.out.println(inputFileName);
		}
	}

	public static String pickInputFile(boolean show) {
//		setVisible(show);
		return(inputFileName);	
	}
}



