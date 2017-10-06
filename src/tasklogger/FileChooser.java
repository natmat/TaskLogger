package tasklogger;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public class FileChooser extends JPanel {
	private static final long serialVersionUID = -6729743289945525689L;

	public static void main(String args[]) {
		createFileChooser();
	}
	
	public FileChooser(String note, String extension) {
		
	}

	public static File createFileChooser() {
		JFileChooser fileChooser = new JFileChooser("/Users/Nathan/github/TaskLogger");
		fileChooser.setDialogTitle("Open code file");

		// Permit only Excel files to be chosen
		fileChooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "Excel files (*.xls[m])";
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				} else {
					String filename = f.getName().toLowerCase();
					return filename.endsWith(".xls") || filename.endsWith(".xlsm") ;
				}
			}
		});

		File selectedFile = null;
		final int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile().getAbsoluteFile();
			System.out.println(selectedFile);
		}
		return(selectedFile);
	}
}

