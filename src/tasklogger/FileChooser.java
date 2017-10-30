package tasklogger;

import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public class FileChooser extends JPanel {
	private static final long serialVersionUID = -6729743289945525689L;
	private File chosenFile;
	private String chooserDescription;
	private String chooserRegex;
	private static JFileChooser fileChooser;

	public static void main(String args[]) {
		FileChooser fc = new FileChooser("CSV & Excel", "^.*\\.(csv|xlsm?)$");
		System.out.println("fileChooser: " + fc.chooseFile());
	}

	public FileChooser(final String inDescription, final String inRegex) {
		fileChooser = new JFileChooser();
		this.chooserDescription = inDescription;
		this.chooserRegex = inRegex;
		this.chosenFile = null;
	}

	public File chooseFile() {
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

		// Run fileChooser on EDT
		try {
			EventQueue.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					chosenFile = null;
					final int returnState = fileChooser.showOpenDialog(null);
					if (returnState == JFileChooser.APPROVE_OPTION) {
						chosenFile = fileChooser.getSelectedFile().getAbsoluteFile();
					}
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return(chosenFile);
	}

	public File getSelectedFile() {
		return(chosenFile);
	}
}

