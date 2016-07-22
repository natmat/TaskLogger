package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Calendar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

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
//		TLUtilities.importFromExcel();
		
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
		long startTime = now.getTimeInMillis();
		
		// Set the end of day (17:00)
		now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 17, 00, 00);
		long endTime = now.getTimeInMillis();
		long difference = endTime - startTime;
		if (difference < 0) {
			return;
		}
		
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


