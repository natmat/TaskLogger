package tasklogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class TaskLoaderWorker extends SwingWorker<Void, ArrayList<String>> {
	static ArrayList<String> taskList = null;
	private static final String defaultTaskName = "[Enter new task info/code]";
	private static final String inputFileRegex = "^.*\\.(csv|xlsm?)$";

	public static void main(String[] args) throws Exception {
		final TaskLoaderWorker tlw = new TaskLoaderWorker();
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				System.out.println("1 " + SwingUtilities.isEventDispatchThread());
				try {
					tlw.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		while (!tlw.isDone()) {
			Thread.sleep(50);
		}
		tlw.cancel(false);	
		System.out.println("tLW 4 " + SwingUtilities.isEventDispatchThread());

		//		System.exit(0);
	}

	/**
	 * Get os-specific excel file path 
	 * @return Path of excel code file
	 */
	public static final File getExcelFile() {
		FileChooser fileChooser = new FileChooser("Excel file *.xls[m]", ".*\\.xlsm?^");
		return(fileChooser.showFileChooser());
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
		System.out.println("showTimedInfoDialog");
		final JDialog dialog = new JDialog(new JFrame(), msgString, false);
		dialog.setAlwaysOnTop(true);		
		dialog.setSize(400, 20);
		dialog.setLocationRelativeTo(TLView.getInstance());
		dialog.setVisible(true);

		new Thread(new Runnable() {
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
		}).start();
	}

	private static ArrayList<String> inputTaskCodesFromFile() {
		System.out.println("inputTaskCodesFromFile");
		ArrayList<String> taskList = new ArrayList<>();
		FileChooser fileChooser = new FileChooser("CSV or Excel", inputFileRegex);

		// Call the appropriate handler for the input file format
		fileChooser.showFileChooser();
		final File inputFile = fileChooser.getSelectedFile();
		
		if (null != inputFile) {
			if (inputFile.getAbsolutePath().toLowerCase().matches("^.*\\.csv$")) {
				taskList = readTaskListFromCSV(inputFile);
			}
			else {
				ExcelReader.readTaskListFromExcelFile(inputFile);
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
		System.out.println("tLW 2 " + SwingUtilities.isEventDispatchThread());

		taskList = inputTaskCodesFromFile();
		System.out.println("taskList=" + taskList);
		System.out.println(((taskList.size() == 0) ? "No " : "") + "Tasks Loaded");
		if (taskList.size() == 0) {
			showTimedInfoDialog("ERROR: No task code data");
			taskList.add(0, TaskLoaderWorker.defaultTaskName);
		}

		publish(taskList);
		System.out.println("dIB return");
		return null;
	}

	@Override
	protected void process(List<ArrayList<String>> chunks) {
		super.process(chunks);
		for (ArrayList<String> task : chunks) {
			System.out.println("process=" + task);
		}
	}

	public static String getDefaultTaskName() {
		return(defaultTaskName);
	}

	@Override
	protected void done() {
		super.done();
		System.out.println("tLW 3 " + SwingUtilities.isEventDispatchThread());
	}


}

