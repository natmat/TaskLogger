package tasklogger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.SynchronousQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader implements ActionListener {

	// private static final String FILE_PATH =
	// "C:/My_Workspaces/MyJava/TaskLogger/resources/typhoon.xlsm";
	private static final int WBS_COLUMN_INDEX = 1;

	protected SynchronousQueue<Boolean> queue = null;
	private static String newTaskName = null;

	private static final Object instanceLock = new Object();
	private static volatile ExcelReader instance;

	private static JProgressBar progressBar;

	// private static final String FILE_PATH = "typhoon.xlsm";

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Create a task list (attempting to populate it from a file)
				ArrayList<String> taskList = createTaskListFromExcel(TaskLoader.getExcelFilePath());
				System.out.println("TL=" + taskList);

				Boolean tasksFound = (taskList != null);
				String message = tasksFound ? "ExcelReader complete" : "ExcelReader failed";
				JOptionPane.showMessageDialog(new JFrame(), message, "ExcelReader",
						tasksFound ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
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

		class ProgressWorker extends SwingWorker<Void, Void> {
			private JFrame frame;

			@Override
			protected Void doInBackground() throws Exception {
				frame = new JFrame("Reading from excel");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setLayout(new FlowLayout());

				progressBar = new JProgressBar(0, 10);
				progressBar.setIndeterminate(false);
				progressBar.setString("Loading...");
				progressBar.setStringPainted(true);
				progressBar.setPreferredSize(new Dimension(200, 200));
				progressBar.setVisible(true);

				JPanel panel = new JPanel();
				panel.add(progressBar);
				frame.add(panel, BorderLayout.PAGE_START);

				panel.setOpaque(true);
				frame.setContentPane(panel);
				frame.pack();
				frame.setVisible(true);

				for (int i = 0; i <= 10; i++) {
					progressBar.setValue(i);
					Thread.sleep(1000);
				}
				return null;
			}

			@Override
			protected void done() {
				progressBar.setVisible(false);
				frame.dispose();
			}
		}

		ProgressWorker progressWorker = new ProgressWorker();
		progressWorker.execute();

		class WBSReader extends SwingWorker<ArrayList<WBSTask>, Void> {
			private ArrayList<WBSTask> wbsTaskList;

			@Override
			protected ArrayList<WBSTask> doInBackground() throws Exception {
				System.out.println("Reading...");
				this.wbsTaskList = readWbsListFromExcel(excelFile);
				System.out.println("DONE");
				return (wbsTaskList);
			}
		}
		;

		// Start the reader.
		WBSReader wbsReader = new WBSReader();
		wbsReader.execute();
		ArrayList<WBSTask> wbsList = null;
		try {
			wbsList = wbsReader.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			// wbsList is incomplete
			System.out.println("wbsReader.get()");
			e.printStackTrace();

		}
		progressWorker.cancel(true);

		if (wbsList != null) {
			taskList = new ArrayList<>();
			convertWbsListToTaskList(wbsList, taskList);
		}

		return (taskList);
	}

	private static void convertWbsListToTaskList(ArrayList<WBSTask> wbsList, ArrayList<String> taskList) {
		sortTaskListAscending(wbsList);
		for (WBSTask t : wbsList) {
			taskList.add(t.getTaskString());
		}
	}

	/**
	 * Task a tasklist and sorts it in-situ
	 * 
	 * @param taskList
	 */
	private static void sortTaskListAscending(List<WBSTask> taskList) {
		Collections.sort(taskList, new Comparator<WBSTask>() {
			@Override
			public int compare(WBSTask task1, WBSTask task2) {
				return task1.getTaskString().compareTo(task2.getTaskString());
			}
		});
	}

	public static ArrayList<WBSTask> readWbsListFromExcel(final String filePath) {
		// Using XSSF for xlsx format, for xls use HSSF
		ArrayList<WBSTask> taskList = new ArrayList<>();
		FileInputStream fis;
		Workbook workbook;
		try {
			fis = new FileInputStream(new File(filePath));
			workbook = new XSSFWorkbook(fis);
		} catch (IOException | IllegalStateException e) {
			System.out.println(e.getMessage());
			return (null);
		}

		// iterating over each row of WBS sheet
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
					if ((Cell.CELL_TYPE_STRING == cell.getCellType()) && (cell.getStringCellValue().length() != 0)
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

		// public WBSTask(String inCode, String inInfo) {
		// this.code = inCode;
		// this.info = inInfo;
		// }

		public void setCode(String stringCellValue) {
			code = stringCellValue;
		}

		public void setInfo(String stringCellValue) {
			info = stringCellValue;
		}

		public String getTaskString() {
			return (this.info + " : " + this.code);
		}
	}

	public static String getNewTaskName() {
		return (newTaskName);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("AL invoked");
	}
}
