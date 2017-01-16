package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Calendar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * 
 * @author Nathan
 *
 */
public class TaskLogger {
	private static TLView view;
	private static TLModel model;

	public static void main(String[] args) {    
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
				new TaskLoader().execute();
				runShutDownTimer();
			}
		});	
	}

	protected static void createAndShowGUI() {
		model = TLModel.getInstance();
		view = TLView.getInstance();
		
		view.setTitle("Task logger");
		view.setVisible(true);
		
		try {
			model.importCSVFile();
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
		final int endHourOfDay = 17;
		now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 
				endHourOfDay, 00, 00);
		long endTime = now.getTimeInMillis();
		long endTimeDifference = endTime - startTime;
		if (endTimeDifference < 0) {
			return;
		}
		
		Timer shutdownTimer = new Timer((int)endTimeDifference, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TLModel.writeTaskTimesToFile();
				System.exit(0);
			}
		});
		shutdownTimer.start();
	}
}


