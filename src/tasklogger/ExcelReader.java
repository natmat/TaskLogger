package tasklogger;

import java.awt.BorderLayout;
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

	// private static final String FILE_PATH = "typhoon.xlsm";

	private ExcelReader() {
	}

	public static void main(String args[]) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Create a task list (attempting to populate it from a file)
				ArrayList<String> taskList = createTaskListFromExcel(TaskLoader.getExcelFilePath());
			}
		});
	}

	static void test(Integer i) {
		i = new Integer(1);
	}

	private void initProgressBarGui(final JFrame inFrame, JProgressBar inProgressBar) {
		inFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel pbPanel = new JPanel();
		inFrame.add(pbPanel, BorderLayout.NORTH);

		inProgressBar.setMinimum(0);
		inProgressBar.setMaximum(100);
		inProgressBar.setValue(0);
		inProgressBar.setIndeterminate(false);

		inProgressBar.setStringPainted(true);
		inProgressBar.setVisible(true);

		pbPanel.add(inProgressBar);

		inFrame.pack();		
		inFrame.setVisible(true);
	}

	public static ExcelReader getInstance() {
		if (instance == null) {
			synchronized (instanceLock) {
				instance = new ExcelReader();
			}
		}
		return (instance);
	}

	private class ProgressBarWorker extends SwingWorker<Void, Integer> {
		private JFrame frame;
		private JProgressBar progressBar;

		public ProgressBarWorker() {
			frame = new JFrame("ProgressWorker");
			frame.setLocation(400, 400);
			progressBar = new JProgressBar();

			initProgressBarGui(this.frame, this.progressBar);
		}

		@Override
		protected Void doInBackground() throws Exception {
			int progress = progressBar.getValue();
			while (progress < progressBar.getMaximum()) {
				Thread.sleep(200);
				publish(progress++);
			}
			return null;
		}

		@Override
		protected void process(List<Integer> chunks) {
			super.process(chunks);
			Integer chunk = chunks.get(chunks.size() - 1);
			progressBar.setValue(chunk);
		}

		@Override
		protected void done() {
			super.done();
			progressBar.setVisible(false);
			frame.dispose();
		}
	}

	/**
	 * Read the WBS tasks from the excel workbook and write to taskList
	 * 
	 * @param excelFile
	 * @return
	 */
	public static ArrayList<String> createTaskListFromExcel(final String excelFile) {
		ArrayList<String> taskList = null;

		// Create and start a progressBar for this operation
		ProgressBarWorker progressBarWorker = getInstance().new ProgressBarWorker();
		progressBarWorker.execute();

		class ExcelReaderWorker extends SwingWorker<ArrayList<WBSTask>, Void> {
			private ArrayList<WBSTask> wbsTaskList;

			@Override
			protected ArrayList<WBSTask> doInBackground() throws Exception {
				System.out.println("Reading...");
				wbsTaskList = readWbsListFromExcel(excelFile);
				printWBSTaskList(wbsTaskList);
				System.out.println("DONE");
				return (wbsTaskList);
			}

			@Override
			protected void process(List<Void> chunks) {
				// TODO Auto-generated method stub
				super.process(chunks);
			}

			@Override
			protected void done() {
				super.done();
				Boolean tasksFound = (wbsTaskList != null);
				if (tasksFound) {
					ArrayList<String> taskList = new ArrayList<>();
					convertWbsListToTaskList(wbsTaskList, taskList);
				}
				String message = tasksFound ? "ExcelReader complete" : "ExcelReader failed";
				JOptionPane.showMessageDialog(new JFrame(), message, "ExcelReader",
						tasksFound ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
			}
		}

		// Start the reader.
		ExcelReaderWorker excelReaderWorker = new ExcelReaderWorker();
		excelReaderWorker.execute();

//		excelReaderWorker.cancel(true);
		return(taskList);
	}

	public static void printWBSTaskList(ArrayList<WBSTask> wbsTaskList) {
		for (WBSTask task : wbsTaskList) {
			System.out.println(task.getTask());
		}
	}

	private static void convertWbsListToTaskList(ArrayList<WBSTask> wbsList, ArrayList<String> taskList) {
		sortTaskListAscending(wbsList);
		for (WBSTask t : wbsList) {
			taskList.add(t.getTask());
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
				return task1.getTask().compareTo(task2.getTask());
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

		public void setCode(String stringCellValue) {
			code = stringCellValue;
		}

		public void setInfo(String stringCellValue) {
			info = stringCellValue;
		}

		public String getTask() {
			return (this.code + ":" + this.info);
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
