package tasklogger;

import java.util.ArrayList;
import javax.swing.SwingUtilities;

public class TaskLogger {
	private static TLController controller;
	private static TLView view;
	private static TLModel model;
	private static ArrayList<TLTask> taskList;
	private static TaskLogger instance = null;

	public static void main(String[] args) {    
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	protected static void createAndShowGUI() {
		model = TLModel.getInstance();		
		controller = TLController.getInstance();
		controller.setView(view);
		controller.setModel(model);
		view = TLView.getInstance();
		view.setTitle("Pomodoro tasker");   
		view.setVisible(true);

		taskList = new ArrayList<>();
	}

	public static void taskPulse(TLTask task) {
		view.setTime(task);
	}

}
