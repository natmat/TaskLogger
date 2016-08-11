package tasklogger;

import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sun.org.apache.xpath.internal.axes.IteratorPool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Created by anirudh on 20/10/14.
 */
public class ExcelReader {

	// private static final String FILE_PATH =
	// "/Users/Nathan/github/TaskLogger/src/file.xlsx";
	// private static final String FILE_PATH =
	// "C:/My_Workspaces/MyJava/TaskLogger/src/tasklogger/file.xlsx";
	// private static final String FILE_PATH =
	// "//Greenlnk.net/Data/Olympia/MA&I/Team Site Folders/B2004MLA/Private/CAGE/Tracking/Cost Tracking Typhoon/008 Jan 2016/Typhoon Cost Tracker Jan 2016 v2.xlsm";
//	private static final String FILE_PATH = "C:/My_Workspaces/MyJava/TaskLogger/src/tasklogger/typhoon.xlsm";
	private static final String FILE_PATH = "resources/typhoon.xlsm";

	// private static final String FILE_PATH = "typhoon.xlsm";

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public static void createAndShowGUI() {
		List<Task> taskList = getTaskNamesFromExcel();

		ArrayList<String> wbc = new ArrayList<>();
		for (Task t : taskList) {
			wbc.add(t.code);
		}
		String[] arr = wbc.toArray(new String[wbc.size()]);
		JComboBox<String> jcb = new JComboBox<>(arr);
		jcb.setMaximumRowCount(40);

		JFrame frame = new JFrame("JComboBox");
		// frame.setSize(500, 500);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.add(jcb);
		frame.add(panel);
		frame.pack();

		jcb.setEditable(true);
		jcb.setVisible(true);
		jcb.setSelectedIndex(0);
		jcb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
				String s = (String) cb.getSelectedItem();
				System.out.println("selected=" + s);
			}
		});
	}

	private static List<Task> getTaskNamesFromExcel() {
		List<Task> taskList = new ArrayList<Task>();
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
				Task task = null;
				while (cellIterator.hasNext()) {

					Cell cell = cellIterator.next();
					// The Cell Containing String will is name.
					switch (cell.getColumnIndex()) {
					case 1:
						if ((Cell.CELL_TYPE_STRING == cell.getCellType()) 
								&& (cell.getStringCellValue().length() != 0)
								&& (cell.getStringCellValue().startsWith("D"))) {
							task = new Task();
							task.setCode(cell.getStringCellValue());
							taskList.add(task);
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

	private static class Task {
		private String code;
		private String info;

		public Task() {
			// TODO
		}

		public void setCode(String stringCellValue) {
			code = stringCellValue;
		}

		public void setInfo(String stringCellValue) {
			info = stringCellValue;
		}

		public String getCode() {
			return (code);
		}

		public String getInfo() {
			return (info);
		}
	}
}
