package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

import javax.swing.SwingUtilities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader implements ActionListener {

	//	private static final String FILE_PATH = "C:/My_Workspaces/MyJava/TaskLogger/resources/typhoon.xlsm";
	private static final int WBS_COLUMN_INDEX = 1;

	protected SynchronousQueue<Boolean> queue = null;	
	private static String newTaskName = null;
	
	private static final Object instanceLock = new Object();
	private static volatile ExcelReader instance;

	// private static final String FILE_PATH = "typhoon.xlsm";

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Create a task list (attempting to populate it from a file)
				createTaskListFromExcel(TaskLoader.getExcelFilePath());
			}
		});
	}

	public static ExcelReader getInstance() {
		if (instance == null) {
			synchronized (instanceLock) {
				instance = new ExcelReader();
			}
		}
		return (instance);
	}

	private ExcelReader() {
	}

	/**
	 * Read the WBS tasks from the excel workbook and write to taskList
	 * 
	 * @param excelFile
	 * @return
	 */
	public static ArrayList<String> createTaskListFromExcel(final String excelFile) {
		ArrayList<String> taskList = null;
		
		// Read from file into the wbsList, then convert to taskList
		ArrayList<WBSTask> wbsList = readWbsListFromExcel(excelFile);
		if (wbsList != null) {
			taskList = new ArrayList<>();
			convertWbsListToTaskList(wbsList, taskList);
			taskList.add(0, TaskLoader.getDefaultTaskName());
		}
		
		return(taskList);
	}

	private static void convertWbsListToTaskList(ArrayList<WBSTask> wbsList, ArrayList<String> taskList) {
		sortTaskListAscending(wbsList);
		for (WBSTask t : wbsList) {
			taskList.add(t.info + ": " + t.code);
		}
	}

	/** 
	 * Task a tasklist and sorts it in-situ
	 * @param taskList
	 */
	private static void sortTaskListAscending(List<WBSTask> taskList) {
		Collections.sort(taskList, new Comparator<WBSTask>() {
			@Override
			public int compare(WBSTask task1, WBSTask task2)
			{
				return  task1.getTaskString().compareTo(task2.getTaskString());
			}
		});
	}

	public static ArrayList<WBSTask> readWbsListFromExcel(final String filePath) {
		// Using XSSF for xlsx format, for xls use HSSF
		ArrayList<WBSTask> taskList = new ArrayList<WBSTask>();
		FileInputStream fis;
		Workbook workbook;
		try {
			fis = new FileInputStream(new File(filePath));
			workbook = new XSSFWorkbook(fis);
			//			workbook = new XSSFWorkbook("test.txt");
		} catch (IOException | IllegalStateException e) {
			System.out.println(e.getMessage());
			return(null);
		}

		// iterating over each row
		Sheet wbsSheet = workbook.getSheet("WBS");
		Iterator<Row> rowIterator = wbsSheet.iterator();
		while (rowIterator.hasNext()) {

			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();

			// Iterating over each cell (column wise) in a particular row.
			WBSTask task = null;
			while (cellIterator.hasNext()) {

				Cell cell = cellIterator.next();
				// The Cell Containing String will is name.
				switch (cell.getColumnIndex()) {
				case WBS_COLUMN_INDEX:
					if ((Cell.CELL_TYPE_STRING == cell.getCellType()) 
							&& (cell.getStringCellValue().length() != 0)
							&& (cell.getStringCellValue().startsWith("D"))) {
						// Read task from WBS column 
						task = new WBSTask();
						task.setCode(cell.getStringCellValue());
						// Read info column WBS++
						cell = cellIterator.next();
						task.setInfo(cell.getStringCellValue());

						taskList.add(task); 
					}						
					break;

				default:
					break;
				}
			}
		}

		try {
			workbook.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return taskList;
	}

	private static class WBSTask {
		private String code;
		private String info;

		public WBSTask() {
		}

//		public WBSTask(String inCode, String inInfo) {
//			this.code = inCode;
//			this.info = inInfo;
//		}

		public void setCode(String stringCellValue) {
			code = stringCellValue;
		}

		public void setInfo(String stringCellValue) {
			info = stringCellValue;
		}

		public String getTaskString() {
			return(this.code + ": " + this.info);
		}
	}

	public static String getNewTaskName() {
		return(newTaskName);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("AL invoked");
	}
}


