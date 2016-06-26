package tasklogger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TaskLoggerModel implements PropertyChangeListener {

	private static ArrayList<Task> taskArray;
	private static TaskLoggerModel instance;

	public static TaskLoggerModel getInstance() {
		if (instance == null) {
			instance = new TaskLoggerModel();
		}
		return(instance);
	}
	
	private TaskLoggerModel() {
		taskArray = new ArrayList<>();
	}

	public static Task newTask(final String inName) {
		// Find task in arrayTask
		for (Task t : taskArray) {
			if (t.getName().equals(inName)) {
				JOptionPane.showMessageDialog(new JFrame(), 
						"Task already exists.", 
						"New task error",
						JOptionPane.ERROR_MESSAGE);
				return (null);
			}
		}

		Task task = new Task(inName);
		taskArray.add(task);
		return (task);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		int taskID = extractTaskIDFromString(evt.getPropertyName());
		for (Task t : taskArray) {
			if (taskID == t.getTaskID()) {
				t.actionTask();
				return;
			}
		}
		System.out.println("pcl not found");
	}
	
	private int extractTaskIDFromString(final String name) {
		int iID = name.indexOf(':') + 1;
		int taskID = Integer.parseInt(name.substring(iID, name.length()));
		System.out.println("taskID=" + taskID);
		return taskID;
	}
}
