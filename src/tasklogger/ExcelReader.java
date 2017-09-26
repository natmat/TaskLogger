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

	private static JFrame frame2;
	private static JProgressBar progressBar2;

	// private static final String FILE_PATH = "typhoon.xlsm";
	
	private ExcelReader() {
	}

	public static void main(String args[]) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				testProgressBar();
				
				// Create a task list (attempting to populate it from a file)
				if (false) {
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
			progressBar = new JProgressBar();
			progressBar.setMinimum(0);
			progressBar.setMaximum(20);
			progressBar.setValue(0);

			progressBar.setStringPainted(true);
			progressBar.setVisible(true);

			pbFrame = new JFrame("ProgBar");
			pbFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			JPanel panel = new JPanel();
			panel.add(progressBar);

			pbFrame.add(panel, BorderLayout.NORTH);
			pbFrame.pack();		
			pbFrame.setVisible(true);
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		protected Void doInBackground() throws Exception {
			int progress = progressBar.getMinimum();
			while (progress <= progressBar.getMaximum()) {
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
			System.out.println("process " + chunks.get(chunks.size() - 1));
			super.process(chunks);
			progressBar.setValue(chunks.get(chunks.size() - 1));
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
	
	private static void testProgressBar() {
		final ProgBar pb = (new ExcelReader()).new ProgBar();
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
	

	public class ProgressWorker extends SwingWorker<Void, Integer> {
		private JProgressBar progressBar;
		private JFrame pbFrame;

		public ProgressWorker(final JFrame frame, final JProgressBar progressBar) {
			this.pbFrame = frame;
			this.progressBar = progressBar;
		}

		@Override
		protected Void doInBackground() throws Exception {
			System.out.println("pw dIB");
			while (progressBar.getValue() < progressBar.getMaximum()) {
				Thread.sleep(1000);

				progressBar.setValue(progressBar.getValue() + 1);
				publish(Integer.valueOf(progressBar.getValue()));
				System.out.println("pw dIB=" + progressBar.getValue());
				System.out.println(Thread.currentThread());
			}
			return null;
		}

		@Override
		protected void done() {
			// TODO Auto-generated method stub
			super.done();
			this.progressBar.setVisible(false);
			pbFrame.dispose();
			System.out.println("done()");
		}

		@Override
		protected void process(List<Integer> chunks) {
			for (Integer i : chunks) {
				System.out.println("process=" + i);
				progressBar.setValue(i);
			}
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
		
		frame2 = new JFrame();
		progressBar2 = new JProgressBar();
		createProgressBarFrame(frame2, progressBar2);
		ProgressWorker progressWorker = (new ExcelReader()).new ProgressWorker(frame2, progressBar2);
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

	private static void createProgressBarFrame(final JFrame frame2, final JProgressBar progressBar) {
		frame2.setTitle("Reading from excel");
		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame2.setLocation(400, 400);
		frame2.setLayout(new FlowLayout());
		JPanel panel = new JPanel();
		panel.setOpaque(true);

		frame2.add(panel, BorderLayout.PAGE_START);
		frame2.setContentPane(panel);

		progressBar.setMinimum(0);
		progressBar.setMaximum(4);
		progressBar.setIndeterminate(false);
		progressBar.setString("Loading...");
		progressBar.setStringPainted(true);
		progressBar.setLocation(500, 500);
		progressBar.setPreferredSize(new Dimension(200, 50));
		progressBar.setVisible(true);
	
		panel.add(progressBar);

		frame2.pack();
		frame2.setVisible(true);		
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
