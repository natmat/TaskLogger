package tasklogger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ExcelReader implements Runnable {

	// private static final String FILE_PATH =
	// "/Users/Nathan/github/TaskLogger/src/file.xlsx";
	// private static final String FILE_PATH =
	// "C:/My_Workspaces/MyJava/TaskLogger/src/tasklogger/file.xlsx";
	// private static final String FILE_PATH =
	// "//Greenlnk.net/Data/Olympia/MA&I/Team Site Folders/B2004MLA/Private/CAGE/Tracking/Cost Tracking Typhoon/008 Jan 2016/Typhoon Cost Tracker Jan 2016 v2.xlsm";
	//	private static final String FILE_PATH = "C:/My_Workspaces/MyJava/TaskLogger/src/tasklogger/typhoon.xlsm";
	private static final String FILE_PATH = "resources/typhoon.xlsm";
	protected SynchronousQueue<Boolean> queue = null;	
	
	// private static final String FILE_PATH = "typhoon.xlsm";

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public static void createAndShowGUI() {
		List<WBSTask> taskList = getTaskNamesFromExcel();
		sortTaskListAscending(taskList);

		ArrayList<String> wbc = new ArrayList<>();
		for (WBSTask t : taskList) {
			wbc.add(t.code + ": " + t.info);
		}

		JComboBox<String> taskSelectorComboBox = new JComboBox<>(wbc.toArray(new String[wbc.size()]));
		taskSelectorComboBox.setMaximumRowCount(20);

		JFrame frame = new JFrame("JComboBox");
		// frame.setSize(500, 500);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.add(taskSelectorComboBox);
		frame.add(panel);
		frame.pack();

		taskSelectorComboBox.setEditable(true);
		taskSelectorComboBox.setVisible(true);
		taskSelectorComboBox.setSelectedIndex(0);
		taskSelectorComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<?> cb = (JComboBox<?>)e.getSource();
				String s = (String) cb.getSelectedItem();
				System.out.println("selected=" + s);
				// TODO
				SynchronousQueue<Boolean> queue = new SynchronousQueue<>();
				try {
					queue.put(true);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	private static void sortTaskListAscending(List<WBSTask> taskList) {
		Collections.sort(taskList, new Comparator<WBSTask>() {
			@Override
			public int compare(WBSTask task1, WBSTask task2)
			{
				return  task1.getTaskString().compareTo(task2.getTaskString());
			}
		});
	}

	private static List<WBSTask> getTaskNamesFromExcel() {
		List<WBSTask> taskList = new ArrayList<WBSTask>();
		FileInputStream fis = null;
		try {
			JFileChooser fileChooser = new JFileChooser();
			fis = new FileInputStream(FILE_PATH);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return(null);
		}

		// Using XSSF for xlsx format, for xls use HSSF
		Workbook workbook;
		try {
			workbook = new XSSFWorkbook(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return(null);
		}

		int numberOfSheets = workbook.getNumberOfSheets();

		// looping over each workbook sheet
		for (int i = 0; i < numberOfSheets; i++) {			
			Sheet sheet = workbook.getSheetAt(i);
			if (!sheet.getSheetName().equals("WBS")) {
				continue;				
			}

			// iterating over each row
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {

				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();

				// Iterating over each cell (column wise) in a particular row.
				WBSTask task = null;
				while (cellIterator.hasNext()) {

					Cell cell = cellIterator.next();
					// The Cell Containing String will is name.
					switch (cell.getColumnIndex()) {
					case 1:
						if ((Cell.CELL_TYPE_STRING == cell.getCellType()) 
								&& (cell.getStringCellValue().length() != 0)
								&& (cell.getStringCellValue().startsWith("D"))) {
							task = new WBSTask();
							task.setCode(cell.getStringCellValue());
							taskList.add(task);

							// Append the 'info' in col3 to the WBS code 
							cell = cellIterator.next();
							task.setInfo(cell.getStringCellValue());
						}						
						break;

					default:
						break;
					}
				}
			}
		}

		try {
			fis.close();
			workbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return taskList;
	}

	@Override
	public void run() {
		while(true) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					createAndShowGUI();
				}
			});
		}
	}

	private static class WBSTask {
		private String code;
		private String info;

		public WBSTask() {
			// TODO
		}

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
}


