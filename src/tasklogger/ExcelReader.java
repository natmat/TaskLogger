package tasklogger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ExcelReader implements ActionListener {

	// private static final String FILE_PATH =
	private static final String FILE_PATH = "resources/typhoon.xlsm";
	//	private static final String FILE_PATH = "C:/My_Workspaces/MyJava/TaskLogger/resources/typhoon.xlsm";

	private static final int WBS_COLUMN_INDEX = 1;

	protected SynchronousQueue<Boolean> queue = null;	
	private static ExcelReader instance;
	private static String newTaskName = null;

	// private static final String FILE_PATH = "typhoon.xlsm";

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ArrayList<String> taskList = readTaskListFromExcel();
				if (taskList != null) {
					taskSelectorDialog(instance, taskList);
				}
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

	public static ArrayList<String> readTaskListFromExcel() {
		ArrayList<WBSTask> wbsList = getTaskInfoCodeFromExcel();
		
		ArrayList<String> taskList = new ArrayList<>();
		convertWbsListToTaskList(wbsList, taskList);
		
		return(taskList);
	}

	public static void taskSelectorDialog(final ActionListener al, ArrayList<String> taskList) {
		final JDialog dialog = new JDialog(TLView.getInstance(), "Enter Task Code/Info", ModalityType.APPLICATION_MODAL);

		dialog.getContentPane().setLayout(new FlowLayout());		
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final JFrame parentFrame = TLView.getInstance();
		dialog.setLocation(parentFrame.getLocation().x, parentFrame.getLocation().y 
				+ (int) (parentFrame.getSize().getHeight()));

		// Create a comboBox and select the first item
		final JComboBox<String> taskSelectorComboBox = new JComboBox<>(taskList.toArray(new String[taskList.size()]));
		taskSelectorComboBox.setMaximumRowCount(20);
		taskSelectorComboBox.setEditable(true);
		taskSelectorComboBox.setVisible(true);
		taskSelectorComboBox.setSelectedIndex(0);
		taskSelectorComboBox.getEditor().selectAll();
		taskSelectorComboBox.setActionCommand("taskSelectorComboBox");
		dialog.add(taskSelectorComboBox);		

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(taskSelectorComboBox);

		// Button invoke close on dialog and rely on its handler to extract the selected string
		JButton b = new JButton("Select");
		b.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				// Select button clicked, so close the window
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
				ExcelReader.newTaskName = taskSelectorComboBox.getSelectedItem().toString();
				ActionEvent ae = new ActionEvent(we.getSource(), 0, "taskSelectorComboBox");
				if (null != al) {
					al.actionPerformed(ae);
				}
			}
		}
		dialog.addWindowListener(new myWindowAdaptor());
	}

	private static void convertWbsListToTaskList(ArrayList<WBSTask> wbsList, ArrayList<String> taskList) {
		if (null != wbsList) {
			taskList.add("[Enter new task info/code]"); // Default zero indexed entry
			sortTaskListAscending(wbsList);
			for (WBSTask t : wbsList) {
				taskList.add(t.info + ": " + t.code);
			}
		}
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

	public static ArrayList<WBSTask> getTaskInfoCodeFromExcel() {
		// Using XSSF for xlsx format, for xls use HSSF
		FileInputStream fis;
		Workbook workbook;
		try {
			fis = new FileInputStream(new File(FILE_PATH));
			workbook = new XSSFWorkbook(fis);
			workbook = new XSSFWorkbook("test.txt");
		} catch (IOException | IllegalStateException e) {
			System.out.println(e.getMessage());
			return(null);
		}

		ArrayList<WBSTask> taskList = new ArrayList<WBSTask>();

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

	public static String getNewTaskName() {
		return(newTaskName);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		System.out.println("AL invoked");
	}
}


