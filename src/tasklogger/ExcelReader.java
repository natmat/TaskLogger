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

	// private static final String FILE_PATH = "typhoon.xlsm";

	private ExcelReader() {
	}

	public static void main(String args[]) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
//				testProgressBar();

				// Create a task list (attempting to populate it from a file)
//				if (false) 
				{
					ArrayList<String> taskList = createTaskListFromExcel(TaskLoader.getExcelFilePath());
					System.out.println("TL=" + taskList);

					Boolean tasksFound = (taskList != null);
					String message = tasksFound ? "ExcelReader complete" : "ExcelReader failed";
					JOptionPane.showMessageDialog(new JFrame(), message, "ExcelReader",
							tasksFound ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
					System.out.println("<main()");
				}
			}
		});
	}

	static void test(Integer i) {
		i = new Integer(1);
	}

	private class ProgBar extends SwingWorker<Void, Integer> {
		private JProgressBar progressBar;
		private JFrame pbFrame;

		public ProgBar() {
			pbFrame = new JFrame("ProgBar");
			progressBar = new JProgressBar();

			initProgBarGui(pbFrame, progressBar);
		}

		/* (non-Javadoc)
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		protected Void doInBackground() throws Exception {
			int progress = progressBar.getMinimum();
			while (progress < progressBar.getMaximum()) {
				progress++;
				try {
					Thread.sleep(100);
					publish(progress);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			publish();
			return null;
		}

		/* (non-Javadoc)
		 * @see javax.swing.SwingWorker#process(java.util.List)
		 */
		@Override
		protected void process(List<Integer> chunks) {
			super.process(chunks);
			Integer chunk = chunks.get(chunks.size() - 1);
			System.out.println("process " + chunk);
			progressBar.setValue(chunk);
		}

		/* (non-Javadoc)
		 * @see javax.swing.SwingWorker#done()
		 */
		@Override
		protected void done() {
			// TODO Auto-generated method stub
			super.done();
			pbFrame.dispose();
		}
	}

	private void initProgBarGui(final JFrame inFrame, JProgressBar inProgressBar) {
		inFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel pbPanel = new JPanel();
		inFrame.add(pbPanel, BorderLayout.NORTH);

		inProgressBar.setMinimum(0);
		inProgressBar.setMaximum(20);
		inProgressBar.setValue(0);

		inProgressBar.setStringPainted(true);
		inProgressBar.setVisible(true);

		pbPanel.add(inProgressBar);

		inFrame.pack();		
		inFrame.setVisible(true);
	}

	private static void testProgressBar() {
		final ProgBar pb = getInstance().new ProgBar();
		pb.execute();
	}

	public static ExcelReader getInstance() {
		if (instance == null) {
			synchronized (instanceLock) {
				instance = new ExcelReader();
			}
		}
		return (instance);
	}


	private class ProgressWorker extends SwingWorker<Void, Integer> {
		private JFrame pwFrame;
		private JProgressBar pwProgressBar;

		public ProgressWorker() {
			this.pwFrame = new JFrame();
			this.pwProgressBar = new JProgressBar();
//			createProgressBarFrame(pwFrame, pwProgressBar);
			initProgBarGui(this.pwFrame, this.pwProgressBar);
		}

		@Override
		protected Void doInBackground() throws Exception {
			while (pwProgressBar.getValue() < pwProgressBar.getMaximum()) {
				Thread.sleep(1000);

				publish(Integer.valueOf(pwProgressBar.getValue()));
				System.out.println("pw dIB=" + pwProgressBar.getValue());
				System.out.println(Thread.currentThread());
			}
			return null;
		}

		@Override
		protected void done() {
			// TODO Auto-generated method stub
			super.done();
			pwProgressBar.setVisible(false);
			pwFrame.dispose();
			System.out.println("done()");
		}

		@Override
		protected void process(List<Integer> chunks) {
			super.process(chunks);
			Integer chunk = chunks.get(chunks.size() - 1);
			System.out.println("process=" + chunk);
			pwProgressBar.setValue(chunk);
		}
	}

	/**
	 * Read the WBS tasks from the excel workbook and write to taskList
	 * 
	 * @param excelFile
	 * @return
	 */
	public static ArrayList<String> createTaskListFromExcel(final String excelFile) {
		System.out.println("createTaskListFromExcel " + Thread.currentThread());

		ArrayList<String> taskList = null;

		ProgressWorker progressWorker = getInstance().new ProgressWorker();
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

		// Start the reader.
		ArrayList<WBSTask> wbsList = null;
		WBSReader wbsReader = new WBSReader();
		//		wbsReader.execute();
		try {
			System.out.println("Looping...");
			while (!wbsReader.isDone()) {
				Thread.sleep(10);
			} 
			wbsList = wbsReader.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			// wbsList is incomplete
			System.out.println("wbsReader.get()");
			e.printStackTrace();
		}

		if (wbsList != null) {
			taskList = new ArrayList<>();
			convertWbsListToTaskList(wbsList, taskList);
		}

		//		progressWorker.cancel(true);

		return(taskList);
	}

	private static void createProgressBarFrame(final JFrame inFrame, final JProgressBar inProgressBar) {
		inFrame.setTitle("Reading from excel");
		inFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		inFrame.setLocation(400, 400);
		inFrame.setLayout(new FlowLayout());
		JPanel panel = new JPanel();
		panel.setOpaque(true);

		inFrame.add(panel, BorderLayout.PAGE_START);
		inFrame.setContentPane(panel);

		inProgressBar.setMinimum(0);
		inProgressBar.setMaximum(4);
		inProgressBar.setIndeterminate(false);
		inProgressBar.setString("Loading...");
		inProgressBar.setStringPainted(true);
		inProgressBar.setLocation(500, 500);
		inProgressBar.setPreferredSize(new Dimension(200, 50));
		inProgressBar.setVisible(true);

		panel.add(inProgressBar);

		inFrame.pack();
		inFrame.setVisible(true);		
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
