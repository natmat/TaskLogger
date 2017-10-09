package tasklogger;

import java.io.File;
import java.time.LocalDateTime;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

public class FileChooser extends JPanel {
	private static final long serialVersionUID = -6729743289945525689L;
	private File selectedFile;
	private String chooserDescription;
	private String chooserRegex;

	public static void main(String args[]) {
		FileChooser fc = new FileChooser("CSV & Excel", "^.*\\.(csv|xlsm?)$");
		System.out.println("fileChooser: " + fc.createFileChooser());
		System.exit(0);
	}
	
	public FileChooser(final String inDescription, final String inRegex) {
		this.chooserDescription = inDescription;
		this.chooserRegex = inRegex;
		this.selectedFile = null;
	}

	public File createFileChooser() {
		System.out.println("createFileChooser()");
		System.out.println(LocalDateTime.now());

		System.out.println(Thread.currentThread());
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(selectedFile);
		System.out.println(LocalDateTime.now());

		fileChooser.setDialogTitle("Open code file");

		// Permit only Excel files to be chosen
		fileChooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return(chooserDescription); // "Excel files (*.xls[m])";
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

		selectedFile = null;
		System.out.println(LocalDateTime.now());
		final int returnValue = fileChooser.showOpenDialog(new JFrame());
		System.out.println(LocalDateTime.now());

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile().getAbsoluteFile();
		}
		return(selectedFile);
	}

	public File getSelectedFile() {
		return(selectedFile);
	}
}

