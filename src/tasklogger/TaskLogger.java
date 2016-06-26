package tasklogger;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class TaskLogger {
	private static TaskLoggerController controller;
	private static TaskLoggerView view;
	private static ArrayList< Task> taskList;
	private static TaskLoggerModel model;
	private static TaskLogger instance = null;

	public static void main(String[] args) {    
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	protected static void createAndShowGUI() {
		model = new TaskLoggerModel();
		
		controller = new TaskLoggerController(getInstance());
		controller.setModel(model);
		view = new TaskLoggerView(controller);    
		controller.setView(view);

		taskList = new ArrayList<>();

		view.setTitle("Pomodoro tasker");   
		view.setVisible(true);
	}

	// Private CTOR for singleton
	private TaskLogger() {}

	public static TaskLogger getInstance() {
		if (instance == null) {
			instance = new TaskLogger();
		}
		return(instance);
	}

	public static TaskLoggerController getController() {
		return controller;
	}

	public static void setController(TaskLoggerController controller) {
		TaskLogger.controller = controller;
	}

	public static void taskPulse(Task task) {
		view.setTime(task);
	}

	public void startButtonPressed() {
		if (taskList.isEmpty()) {
		}
		try {
			taskList.get(0).actionTask();
			view.setTaskState(taskList.get(0).getTaskState());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
