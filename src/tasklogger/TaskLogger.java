package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Calendar;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class TaskLogger {
	private static TLView view;
	private static TLModel model;

	/**
	 * @param args
	 */
	public static void main(String[] args) { 
		model = TLModel.getInstance();
		view = TLView.getInstance();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				loadTodaysBackup();
				createAndShowGUI();
				runShutDownTimer();
				TaskLoader.load();
			}
		});
	}

	private static void loadTodaysBackup() {
		System.out.println("loadTodaysBackup()");
		try {
			model.importTodaysCSVBackup();
		} catch (IOException e) {
			TLView.writeInfo("Error today's model");
			JOptionPane.showMessageDialog(null, "Import error", "Could not import times from file.", 
					JOptionPane.WARNING_MESSAGE);
		}
	}

	private static void createAndShowGUI() {
		view.setTitle("Task logger");
		view.setVisible(true);
		TLView.addModel(model);
	}

	private static void runShutDownTimer() {		
		final long timeNow = Calendar.getInstance().getTimeInMillis();

		int hour = 17;
		if ("Mac OS X".equals(System.getProperty("os.name"))) {
			hour = 23;
		}

		final Calendar endOfDay = Calendar.getInstance();
		endOfDay.set(
				endOfDay.get(Calendar.YEAR), 
				endOfDay.get(Calendar.MONTH), 
				endOfDay.get(Calendar.DAY_OF_MONTH), 
				hour, 30, 00); // 17.30 timer fires

		final long timeToEndOfDay = endOfDay.getTimeInMillis()  - timeNow;
		new Timer((int)timeToEndOfDay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TLModel.writeTaskTimesToFile();
				JOptionPane.showMessageDialog(null, "TaskLogger terminating");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ie) {
					// TODO Auto-generated catch block
					ie.printStackTrace();
				}
				System.exit(0);
			}
		}).start();
	}
}


