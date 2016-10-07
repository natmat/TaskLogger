package tasklogger;

import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class TaskLoader extends SwingWorker<ArrayList<String>, Void> {
	static boolean loaded = false;
	static ArrayList<String> taskList = null;
	
	private static final String EXCEL_FILE_PATH = "resources/typhoon.xlsm";
	private static final String defaultTaskName = "[Enter new task info/code]";

	public static final String getExcelFilePath() {
		return(EXCEL_FILE_PATH);
	}

	public static ArrayList<String> getTaskList() {
		// Add taskList default index zero entry
		if (null == taskList) {
//			showTimedInfoDialog("No tasklist loaded");
			ArrayList<String> tempArray = new ArrayList<>();
			tempArray.add(defaultTaskName);
			return(tempArray);
		}
		return taskList;
	}

	@SuppressWarnings("unused")
	private static void showTimedInfoDialog(final String msgString) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				// Create 2s popup
				final int delay2seconds = 2000;
				JOptionPane pane = new JOptionPane(msgString, JOptionPane.INFORMATION_MESSAGE);
				JDialog dialog = pane.createDialog("Info");
				dialog.setVisible(true);
				System.out.println(">>");
				try {
					Thread.sleep(delay2seconds);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("<<");
				dialog.setVisible(false);
			}
		});
		t.start();
	}

	public TaskLoader() {

	}

	private static ArrayList<String> readTaskFromCSVFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ArrayList<String> doInBackground() throws Exception {
		System.out.println("Task loading...");
		
		// 2 sources of input: excel and CSV		
		taskList = ExcelReader.createTaskListFromExcel(EXCEL_FILE_PATH);
		if (null == taskList) {
			// Read task from CSV file
			taskList = readTaskFromCSVFile();
		}
		System.out.println("TaskList loaded");		
		taskList.add(defaultTaskName);
		return taskList;
	}

	public static String getDefaultTaskName() {
		return(defaultTaskName);
	}
}

