package tasklogger;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public class FileChooser extends JPanel {
	private static final long serialVersionUID = -6729743289945525689L;
	private File chosenFile;
	private String chooserDescription;
	private String chooserRegex;

	public static void main(String args[]) {
		FileChooser fc = new FileChooser("CSV & Excel", "^.*\\.(csv|xlsm?)$");
		System.out.println("fileChooser: " + fc.chooseFile());
		System.exit(0);
	}
	
	public FileChooser(final String inDescription, final String inRegex) {
		this.chooserDescription = inDescription;
		this.chooserRegex = inRegex;
		this.chosenFile = null;
	}

	public File chooseFile() {
		System.out.println("chooseFile:" + Thread.currentThread());
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(chosenFile);
		
		fileChooser.setDialogTitle("Open task code file");

		// Permit only Excel files to be chosen
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

		chosenFile = null;
		final int returnState = fileChooser.showOpenDialog(null);
		if (returnState == JFileChooser.APPROVE_OPTION) {
			chosenFile = fileChooser.getSelectedFile().getAbsoluteFile();
		}
		return(chosenFile);
	}

	public File getSelectedFile() {
		return(chosenFile);
	}
}

