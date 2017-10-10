package tasklogger;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public class FileChooser extends JPanel {
	private static final long serialVersionUID = -6729743289945525689L;
	private File selectedFile;
	private String chooserFileDescription;
	private String chooserRegex;

	public static void main(String args[]) {
		FileChooser fc = new FileChooser("CSV & Excel", "^.*\\.(csv|xlsm?)$");
		System.out.println("fileChooser: " + fc.showFileChooser());
		System.exit(0);
	}
	
	public FileChooser(final String inDescription, final String inRegex) {
		this.chooserFileDescription = inDescription;
		this.chooserRegex = inRegex;
		this.selectedFile = null;
	}

	public File showFileChooser() {
		System.out.println("showFileChooser()");

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(selectedFile);

		fileChooser.setDialogTitle("Open task code file");

		// Permit only Excel files to be chosen
		fileChooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return(chooserFileDescription);
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				} else {
					return f.getName().toLowerCase().matches(chooserRegex);
				}
			}
		});

		selectedFile = null;
		final int returnValue = fileChooser.showOpenDialog(new JFrame());

		switch(returnValue) {
		case JFileChooser.APPROVE_OPTION:
			selectedFile = fileChooser.getSelectedFile().getAbsoluteFile();
		break;
		case JFileChooser.CANCEL_OPTION: 
		    System.out.println("Cancel was selected");
		    break;
		}
		return(selectedFile);
	}

	public File getSelectedFile() {
		return(selectedFile);
	}
}

