package tasklogger;

import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingWorker;


public class TaskLoader extends SwingWorker<Void, Void> {
	static boolean loaded = false;
	static ArrayList<String> taskList = null;

	private static String excelFilePath;
	private static final String defaultTaskName = "[Enter new task info/code]";

	public static void main(String[] args) {
		TaskLoader loader = new TaskLoader();
		try {
			loader.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get os-specific excel file path 
	 * @return Path of excel code file
	 */
	public static final String getExcelFilePath() {
		if ("Mac OS X".equals(System.getProperty("os.name"))) {
			excelFilePath = "/Users/Nathan/github/TaskLogger/resources/typhoon.xlsm";
		}
		else {
			excelFilePath = "C:/My_Workspaces/MyGit/MyJava/TaskLogger/resources/tasks.xlsm";
		}
		return(excelFilePath);
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

	public TaskLoader() {
		ArrayList<String> csvTaskList = readTaskFromCSVFile();
	}

	private static ArrayList<String> readTaskFromCSVFile() {
		FileChooser fileChooser = new FileChooser();
		return null;
	}

	@Override
	protected Void doInBackground() throws Exception {
		// 2 sources of input: excel, or if that fails try CSV	
		System.out.println("Tasks loading...");
		
		taskList = ExcelReader.createTaskListFromExcel(getExcelFilePath());
		if (null == taskList) {
			showTimedInfoDialog("ERROR: No excel datafile");
			taskList = readTaskFromCSVFile();
		}
		taskList.add(0, TaskLoader.getDefaultTaskName());

		System.out.println(((null == taskList) ? "No " : "") + "Tasks Loaded");
		return null;
	}

	@Override
	protected void done() {
		// TODO Auto-generated method stub
		System.out.println("DONE");
		super.done();
	}

	public static String getDefaultTaskName() {
		return(defaultTaskName);
	}

}

