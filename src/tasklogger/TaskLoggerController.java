package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;

public class TaskLoggerController implements ActionListener, PropertyChangeListener {
	private static TaskLoggerView view;
	private static TaskLogger logger;
	private static TaskLoggerModel model;
	private static PropertyChangeListener pcl;

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

	public void startButtonPressed(int taskID) {
		model.startButtonPressed(taskID);
	}

	public void newTask() {
		String taskName = JOptionPane.showInputDialog(this, "Enter Task : [WBS][Summary]");
		Task task = model.newTask(taskName);
		if (task != null) { 
			addTaskToView(task);
			task.addPropertyChangeListener(this);
		}
	}

	public void addTaskToView(Task t) {
		view.addTask(t.getTaskID());    
	}

	public void setView(TaskLoggerView inView) {
		view = inView;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		if (name.equals("taskRunning")) {
			System.out.println("PC");
		}
	}
}

