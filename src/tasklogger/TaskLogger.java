package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

public class TaskLogger {
	private static TLController controller;
	private static TLView view;
	private static TLModel model;

	public static void main(String[] args) {    
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
				runShutDownTimer();
			}
		});
	}

	protected static void createAndShowGUI() {
		model = TLModel.getInstance();
		controller = TLController.getInstance();
		view = TLView.getInstance();
		
		controller.setView(view);
		controller.setModel(model);
		
		view.setTitle("Task logger");   
		view.setVisible(true);
		
		try {
			TLModel.importCSVFile();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Import error",
					"Could not import times from file.", JOptionPane.WARNING_MESSAGE);
		}
		TLModel.addModelToView();
	}
	
	private static void runShutDownTimer() {
		
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
		int day = now.get(Calendar.DAY_OF_MONTH);
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		int second = now.get(Calendar.SECOND);
		int millis = now.get(Calendar.MILLISECOND);
		System.out.printf("%d-%02d-%02d %02d:%02d:%02d.%03d", year, month, day, hour, minute, second, millis);
		
		Calendar endOfDay = now;
		endOfDay.set(hour, 17);
		endOfDay.set(minute, 00);
		endOfDay.set(second, 00);
		System.out.printf("%d-%02d-%02d %02d:%02d:%02d.%03d", year, month, day, hour, minute, second, millis);
		
//		long difference = endOfDay.getTimeInMillis() - Calendar.getInstance().getTimeInMillis(); 
		
		Timer shutdownTimer = new Timer((int)difference, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TLModel.saveTaskTimes();
				System.exit(0);
			}
		});
		shutdownTimer.start();
	}
}


