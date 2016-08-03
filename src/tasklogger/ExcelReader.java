package tasklogger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Created by anirudh on 20/10/14.
 */
public class ExcelReader {

//	private static final String FILE_PATH = "/Users/Nathan/github/TaskLogger/src/file.xlsx";
//	private static final String FILE_PATH = "C:/My_Workspaces/MyJava/TaskLogger/src/tasklogger/file.xlsx";
//	private static final String FILE_PATH = "//Greenlnk.net/Data/Olympia/MA&I/Team Site Folders/B2004MLA/Private/CAGE/Tracking/Cost Tracking Typhoon/008 Jan 2016/Typhoon Cost Tracker Jan 2016 v2.xlsm";
	private static final String FILE_PATH = "C:/My_Workspaces/MyJava/TaskLogger/src/tasklogger/typhoon.xlsm";
//	private static final String FILE_PATH = "typhoon.xlsm";

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	
	public static void createAndShowGUI() {
		List<Task> taskList = getTaskNamesFromExcel();
		for (Task t : taskList) {
			System.out.println(t.getCode() + ":" + t.getInfo());
		}

		ArrayList<String> wbc = new ArrayList<>();
		for (Task t : taskList) {
			wbc.add(t.code);
		}
		String[] arr = wbc.toArray(new String[wbc.size()]);
		JComboBox<String> jcb = new JComboBox<>(arr);

		JFrame frame = new JFrame("JComboBox");
//		frame.setSize(500, 500);
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
				JComboBox<String> cb = (JComboBox<String>)e.getSource();
				String s = (String)cb.getSelectedItem();
				System.out.println("selected=" + s);
			}
		});
	}

	private static List<Task> getTaskNamesFromExcel() {
		List<Task> taskList = new ArrayList<Task>();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(FILE_PATH);

			// Using XSSF for xlsx format, for xls use HSSF
			Workbook workbook = new XSSFWorkbook(fis);

			int numberOfSheets = workbook.getNumberOfSheets();

			// looping over each workbook sheet
			for (int i = 0; i < numberOfSheets; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				Iterator<Row> rowIterator = sheet.iterator();

				// iterating over each row
				while (rowIterator.hasNext()) {

					Task task = new Task();
					Row row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();

					// Iterating over each cell (column wise) in a particular
					// row.
					while (cellIterator.hasNext()) {

						Cell cell = cellIterator.next();
						// The Cell Containing String will is name.
						switch (cell.getColumnIndex()) {
						case 0:
							if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
								task.setCode(cell.getStringCellValue());
							}
							break;
						case 1:
						default:
							if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
								task.setInfo(cell.getStringCellValue());
							}
							break;
						}
					}
					// end iterating a row, add all the elements of a row in
					// list
					taskList.add(task);
				}
			}

			fis.close();
			workbook.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return taskList;
	}

	private static class Task {
		private String code;
		private String info;
		private static int size;
		
		public Task() {
			size = 0;
		}
		
		public static int getSize() {
			return(size);	
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
