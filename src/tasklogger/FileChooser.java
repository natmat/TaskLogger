package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

public class FileChooser extends JPanel {
	private static final long serialVersionUID = -6729743289945525689L;
	private File chosenFile;
	private String chooserDescription;
	private String chooserRegex;
	private static JFileChooser fileChooser;

	public static void main(String args[]) {
		final FileChooser fc = new FileChooser("CSV & Excel", "^.*\\.(csv|xlsm?)$");

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				System.out.println("fileChooser: " + fc.chooseFile());
			}
		});
	}

	public FileChooser(final String inDescription, final String inRegex) {
		fileChooser = new JFileChooser();
		this.chooserDescription = inDescription;
		this.chooserRegex = inRegex;
		this.chosenFile = null;
	}

	public File chooseFile() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("TEST");
				frame.setSize(200, 200);
				JPanel panel = new JPanel();
				JButton button = new JButton("Press Me");
				button.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						frame.setVisible(false);
						// TODO Auto-generated method stub
						
					}
				});
				panel.add(button);
				frame.add(panel);
				frame.setVisible(true);
			}
		});
		
		System.out.println(">chooseFile()");
		fileChooser.setCurrentDirectory(chosenFile);		
		fileChooser.setDialogTitle("Open task code file");

		// Limit file type selection
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return(chooserDescription);
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				} else {
					String filename = f.getName().toLowerCase();
					return filename.matches(chooserRegex);
				}
			}
		});
		
		System.out.println(" chooseFile()");
		chosenFile = null;
		final int returnState = fileChooser.showOpenDialog(getParent());
		if (returnState == JFileChooser.APPROVE_OPTION) {
			chosenFile = fileChooser.getSelectedFile().getAbsoluteFile();
		}
		System.out.println("<chooseFile()");
		return(chosenFile);
	}

	public File getSelectedFile() {
		return(chosenFile);
	}
}

