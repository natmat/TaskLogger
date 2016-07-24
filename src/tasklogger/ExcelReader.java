package tasklogger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by anirudh on 20/10/14.
 */
public class ExcelReader {

    private static final String FILE_PATH = "/Users/Nathan/github/TaskLogger/src/file.xlsx";

    public static void main(String args[]) {
    	List<Task> taskList = getTaskNamesFromExcel();
    	for (Task t : taskList) {
    		System.out.print(t.getName() + ":");
    		for (int i = 0 ; i < 3 ; i++) {
    			System.out.print(t.getVar(i) + " ");
    		}
    		System.out.println();
    	}
    }

    private static List<Task> getTaskNamesFromExcel() {
        List<Task> taskList = new ArrayList<Task>();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FILE_PATH);

            // Using XSSF for xlsx format, for xls use HSSF
            Workbook workbook = new XSSFWorkbook(fis);

            int numberOfSheets = workbook.getNumberOfSheets();

            //looping over each workbook sheet
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                Iterator<Row> rowIterator = sheet.iterator();

                //iterating over each row
                while (rowIterator.hasNext()) {

                	Task task = new Task();
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();

                    //Iterating over each cell (column wise)  in a particular row.
                    while (cellIterator.hasNext()) {

                        Cell cell = cellIterator.next();
                        //The Cell Containing String will is name.
                        if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
                            task.setName(cell.getStringCellValue());

                            //The Cell Containing numeric value will contain marks
                        } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                            //Cell with index 1-3 contains integer values
							task.setVars(cell.getColumnIndex(), (int) cell.getNumericCellValue());
                        }
                    }
                    //end iterating a row, add all the elements of a row in list
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
    	private String name;
    	private int[] var = new int[3];

    	public Task() {	
    	}
   
    	public int getVar(int i) {
    		return(var[i]);
		}

		public String getName() {
    		return(name);
		}

		public void setName(String inName) {
    		name = inName;
    	}
    	
    	public void setVars(int column, int value) {
    		int i = column-1;
    		var[i] = value;
    	}
    }
}
