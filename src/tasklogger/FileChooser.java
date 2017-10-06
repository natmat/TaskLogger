package tasklogger;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public class FileChooser extends JPanel {
	private static final long serialVersionUID = -6729743289945525689L;
	private File selectedFile;
	private String chooserDescription;
	private String chooserRegex;

	public static void main(String args[]) {
		FileChooser fc = new FileChooser("*.xls[m]", "^.*\\.(csv|xlsm?)$");
		System.out.println("fileChooser: " + fc.createFileChooser());
	}
	
	public FileChooser(final String inDescription, final String inRegex) {
		this.chooserDescription = inDescription;
		this.chooserRegex = inRegex;
		this.selectedFile = null;
	}

	public File createFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(selectedFile);
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
		final int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile().getAbsoluteFile();
		}
		return(selectedFile);
	}

	public File getSelectedFile() {
		return(selectedFile);
	}
}

