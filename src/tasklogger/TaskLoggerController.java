package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

public class TaskLoggerController implements ActionListener{
	private static TaskLoggerView view;
	private static TaskLogger logger;
	private static TaskLoggerModel model;

	public TaskLoggerController(final TaskLogger inLogger) {
		logger = inLogger;
	}

	public void setModel(final TaskLoggerModel inModel) {
		model = inModel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("task")) {
			System.out.println("Action");
		}
	}

	public void startButtonPressed() {
		logger.startButtonPressed();
	}

	public void newTask() {
		String taskName = JOptionPane.showInputDialog(this, "Enter Task : [WBS][Summary]");
		Task task = model.newTask(taskName);
		if (task != null) { 
			addTaskToView(task);
		}
	}

	public void addTaskToView(Task t) {
		view.addTask(t);    
	}

	public void setView(TaskLoggerView inView) {
		view = inView;
	}
}

