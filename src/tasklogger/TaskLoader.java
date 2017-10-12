package tasklogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class TaskLoader extends SwingWorker<Void, Void> {
	static ArrayList<String> taskList = null;
	private static final String defaultTaskName = "[Enter new task info/code]";
	private static final String inputFileRegex = "^.*\\.(csv|xlsm?)$";

	public static void main(String[] args) {
		final TaskLoader taskLoader = new TaskLoader(); 
		
		TLUtilities.printlnMethodName();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					System.out.println(SwingUtilities.isEventDispatchThread());
					taskLoader.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		try {
			taskLoader.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		taskLoader.cancel(false);
		System.exit(0);
	}

	/**
	 * Get os-specific excel file path 
	 * @return Path of excel code file
	 */
	public static final File getExcelFile() {
		FileChooser fileChooser = new FileChooser("Excel file *.xls[m]", ".*\\.xlsm?^");
		return(fileChooser.chooseFile());
	}

	public static ArrayList<String> getTaskList() {
		// Add taskList default index zero entry
		if (null == taskList) {
			ArrayList<String> tempArray = new ArrayList<>();
			tempArray.add(defaultTaskName);
			return(tempArray);
		}
		return taskList;
	}

	private static void showTimedInfoDialog(final String msgString) {
		final JDialog dialog = new JDialog(new JFrame(), msgString, false);
		dialog.setAlwaysOnTop(true);		
		dialog.setSize(400, 20);
		dialog.setLocationRelativeTo(TLView.getInstance());

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final int delay2seconds = 2000;
				try {
					Thread.sleep(delay2seconds);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finally {
					dialog.setVisible(false);
					dialog.dispose();
				}
			}
		});
		t.start();
		dialog.setVisible(true);
	}

	private static ArrayList<String> readTaskCodeFile() {
		System.out.println("readTaskCodeFile");
		ArrayList<String> taskList = new ArrayList<>();
		FileChooser fileChooser = new FileChooser("CSV or Excel", inputFileRegex);

		// Call the appropriate handler for the input file format
		final File chosenFile = fileChooser.chooseFile();
		if (null != chosenFile) {
			if (chosenFile.getAbsolutePath().toLowerCase().matches("^.*\\.csv$")) {
				taskList = readTaskListFromCSV(chosenFile);
			}
			else {
				ExcelReader.readTaskListFromExcelFile(chosenFile);
			}
		}
		return(taskList);
	}

	private static ArrayList<String> readTaskListFromCSV(File inputFile) {
		ArrayList<String> taskLists = new ArrayList<>();
		System.out.println("canRead CSV: " + inputFile.getAbsolutePath());
		BufferedReader br = null;
		String line = "";
		final String csvDelimiter = ",";
		try {
			br = new BufferedReader(new FileReader(inputFile));
			while ((line = br.readLine()) != null) {
				String[] taskInfo = line.split(csvDelimiter);
				System.out.println(taskInfo[0] + ":" + taskInfo[1]);
				taskLists.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected Void doInBackground() throws Exception {
		System.out.println("TaskLoad dIB...");

		taskList = readTaskCodeFile();
		if (taskList.size() == 0) {
			showTimedInfoDialog("ERROR: No task code data");
			taskList.add(0, TaskLoader.getDefaultTaskName());
			TLView.writeInfo("Default task: " + taskList.get(0));
		}

		System.out.println("Tasks Loaded: " + taskList.size());
		return null;
	}



	public static String getDefaultTaskName() {
		return(defaultTaskName);
	}

	@Override
	protected void process(List<Void> chunks) {
		// TODO Auto-generated method stub
		super.process(chunks);
	}
}

