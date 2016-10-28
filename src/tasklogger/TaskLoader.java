package tasklogger;

import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingWorker;

public class TaskLoader extends SwingWorker<Void, Void> {
	static boolean loaded = false;
	static ArrayList<String> taskList = null;

	private static final String EXCEL_FILE_PATH = "C:/My_Workspaces/MyGit/MyJava/TaskLogger/resources/tasks.xlsm";
	private static final String defaultTaskName = "[Enter new task info/code]";

	public static void main() {
		TaskLoader loader = new TaskLoader();
		try {
			loader.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final String getExcelFilePath() {
		return(EXCEL_FILE_PATH);
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
				final int delay2seconds = 4000;
				System.out.println(">>");
				try {
					Thread.sleep(delay2seconds);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finally {
					dialog.setVisible(false);
					dialog.dispose();
					System.out.println("<<");
				}
			}
		});
		t.start();
		dialog.setVisible(true);
	}

	public TaskLoader() {
		//		ArrayList<String> l = readTaskFromCSVFile();
	}

	private static ArrayList<String> readTaskFromCSVFile() {
		@SuppressWarnings("unused")
		FilePicker fp = new FilePicker("Load task file", "Load");
		return null;
	}

	@Override
	protected Void doInBackground() throws Exception {
		System.out.println("Tasks loading...");

		// 2 sources of input: excel, or that fails try CSV		
		taskList = ExcelReader.createTaskListFromExcel(EXCEL_FILE_PATH);
		if (null == taskList) {
			showTimedInfoDialog("ERROR: No excel datafile");
			taskList = readTaskFromCSVFile();
		}
		taskList.add(0, TaskLoader.getDefaultTaskName());

		System.out.println(((null == taskList) ? "No " : "") + "Tasks Loaded");
		return null;
	}

	public static String getDefaultTaskName() {
		return(defaultTaskName);
	}
}

