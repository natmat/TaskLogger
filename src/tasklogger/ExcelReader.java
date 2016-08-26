package tasklogger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import sun.awt.WindowClosingListener;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ExcelReader implements ActionListener {

	// private static final String FILE_PATH =
	// "/Users/Nathan/github/TaskLogger/src/file.xlsx";
	// private static final String FILE_PATH =
	// "C:/My_Workspaces/MyJava/TaskLogger/src/tasklogger/file.xlsx";
	// private static final String FILE_PATH =
	// "//Greenlnk.net/Data/Olympia/MA&I/Team Site Folders/B2004MLA/Private/CAGE/Tracking/Cost Tracking Typhoon/008 Jan 2016/Typhoon Cost Tracker Jan 2016 v2.xlsm";
	//	private static final String FILE_PATH = "C:/My_Workspaces/MyJava/TaskLogger/src/tasklogger/typhoon.xlsm";
	private static final String FILE_PATH = "resources/typhoon.xlsm";
	protected SynchronousQueue<Boolean> queue = null;	
	private static ExcelReader instance;
	
	// private static final String FILE_PATH = "typhoon.xlsm";

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(instance);
			}
		});
	}
	
	public static ExcelReader getIstance() {
		if (instance == null) {
			instance = new ExcelReader();
		}
		return (instance);
	}

	private ExcelReader() {
	}
	
	public static void createAndShowGUI(final ActionListener al) {
		final JDialog dialog = new JDialog(TLView.getInstance(), "Enter Task Code/Info", ModalityType.APPLICATION_MODAL);
		
		dialog.getContentPane().setLayout(new FlowLayout());		
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	

		List<WBSTask> taskList = getTaskNamesFromExcel();
		sortTaskListAscending(taskList);

		ArrayList<String> wbc = new ArrayList<>();
		for (WBSTask t : taskList) {
			wbc.add(t.code + ": " + t.info);
		}

		final JComboBox<String> taskSelectorComboBox = new JComboBox<>(wbc.toArray(new String[wbc.size()]));
		taskSelectorComboBox.setMaximumRowCount(10);
		taskSelectorComboBox.setEditable(true);
		taskSelectorComboBox.setVisible(true);
		taskSelectorComboBox.setSelectedIndex(0);
		taskSelectorComboBox.setActionCommand("taskSelectorComboBox");
		dialog.add(taskSelectorComboBox);		
		

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(taskSelectorComboBox);
		
		JButton b = new JButton("Select");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Button clicked, dispose of the window that reads the comboBox
				dialog.dispose();
				return;
			}
		});
		panel.add(b);
		
		dialog.add(panel);
		dialog.pack();
		dialog.setVisible(true);

		class myWindowAdaptor extends WindowAdapter {
			@Override
			public void windowClosed(WindowEvent we) {
				System.out.println(we);
				String selected = taskSelectorComboBox.getSelectedItem().toString();
				System.out.println(selected);
				
				ActionEvent ae = new ActionEvent(we.getSource(), 0, "taskSelectorComboBox");
				if (null != al) {
					al.setName(selected);
					al.actionPerformed(ae);
				}
			}
		}
		dialog.addWindowListener(new myWindowAdaptor());
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
			fis = new FileInputStream("JHNM" + FILE_PATH);
		} catch (FileNotFoundException e) {
			// No file, so pad a taskList
			for (int i = 0 ; i < 10 ; i++) {
				taskList.add(new WBSTask("code_"+ Integer.toString(i), "info_" + Integer.toString(i)));
			}
			return(taskList);
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

	private static class WBSTask {
		private String code;
		private String info;

		public WBSTask() {
			// TODO
		}

		public WBSTask(String inCode, String inInfo) {
			this.code = inCode;
			this.info = inInfo;
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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		System.out.println("AL invoked");
	}
}


