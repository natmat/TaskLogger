package tasklogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import tasklogger.TLUtilities.TimedMessagePopupWorker;

public class TaskLoader {
	static Set<String> setOfTasks = null;
	private static final String defaultTaskName = "[Enter new task info/code]";
	private static final String inputFileRegex = "^.*\\.(csv|xlsm?)$";

	public static void main(String[] args) {
		new TaskLoader(); 
		TaskLoader.load();
	}

	static void load() {
//		TLUtilities.printlnMethodName();
		readTaskCodeFile();
		if (setOfTasks.size() == 0) {
			TimedMessagePopupWorker messageWorker = (new TLUtilities()).new TimedMessagePopupWorker("ERROR: No task code data");
			messageWorker.execute();
			setOfTasks.add(TaskLoader.getDefaultTaskName());
			TLView.writeInfo("Default task: " + TaskLoader.getDefaultTaskName());
		}
		System.out.println("Tasks Loaded: " + setOfTasks.size());
	}

	/**
	 * Get os-specific excel file path 
	 * @return Path of excel code file
	 */
	public static final File getExcelFile() {
		FileChooser fileChooser = new FileChooser("Excel files (*.xlsm?)", "^.*\\.xlsm?$");
		return(fileChooser.chooseFile());
	}

	public static ArrayList<String> getTaskList() {
		// Add taskList default index zero entry
		if (null == setOfTasks) {
			Set<String> tempSet = new HashSet<String>();
			tempSet.add(defaultTaskName);
			return(new ArrayList<String>(tempSet));
		}
		return (new ArrayList<String>(new TreeSet<String>(setOfTasks)));
	}

	private static void readTaskCodeFile() {
		System.out.println("readTaskCodeFile");
		
		if (null == setOfTasks) {
			setOfTasks = new HashSet<String>();
		}		

		// Call the appropriate handler for the input file format
		FileChooser fileChooser = new FileChooser("CSV or Excel", inputFileRegex);
		final File chosenFile = fileChooser.chooseFile();
		if (!TLUtilities.fileExists(chosenFile)) {
			return;
		}

		switch(TLUtilities.getFileType(chosenFile)) {
		case FILE_TYPE_CSV:
			setOfTasks.addAll(readTaskHashSetFromCSV(chosenFile));
			break;
		case FILE_TYPE_EXCEL:
			setOfTasks.addAll(ExcelReader.readTaskListFromExcelFile(chosenFile));
			break;
		default:
			// Unknown type
			break;
		}
	}

	private static Set<String> readTaskHashSetFromCSV(File inputFile) {
		Set<String> csvTaskHashSet = new HashSet<String>();
		BufferedReader br = null;
		String line = "";
		try {
			br = new BufferedReader(new FileReader(inputFile));
			while ((line = br.readLine()) != null) {
				System.out.println("line="+ line);
				if (line.length() == 0) {
					continue;
				}
				String[] taskInfo = line.split(",");
//				System.out.println(taskInfo[0] + ":" + taskInfo[1]);
				csvTaskHashSet.add(taskInfo[0] + "," + taskInfo[1]);
			}
		} catch (FileNotFoundException e) {			
			TLView.writeInfo("Input file " + inputFile + " not found.");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return csvTaskHashSet;
	}

	public static String getDefaultTaskName() {
		return(defaultTaskName);
	}
}
