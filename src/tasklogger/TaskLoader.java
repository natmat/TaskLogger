package tasklogger;

import java.util.ArrayList;

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
		return taskList;
	}

	public TaskLoader() {

	}

	private static ArrayList<String> readTaskFromCSVFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ArrayList<String> doInBackground() throws Exception {
		// 2 sources of input: excel and CSV		
		taskList = ExcelReader.createTaskListFromExcel(EXCEL_FILE_PATH);
		if (null == taskList) {
			// Read task from CSV file
			taskList = readTaskFromCSVFile();
		}
		System.out.println("TaskList loaded");
		
		taskList.add(0, defaultTaskName);
		return taskList;
	}

	public static Object getDefaultTaskName() {
		return(defaultTaskName);
	}
}

