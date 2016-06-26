package tasklogger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.SwingPropertyChangeSupport;

public class TaskLoggerModel implements PropertyChangeListener {

	private SwingPropertyChangeSupport pcSupport;
	private ArrayList<Task> taskArray;

	public TaskLoggerModel() {
		taskArray = new ArrayList<>();
	}

	public Task newTask(final String inName) {
		// Find task in arrayTask
		for (Task t : taskArray) {
			if (t.getName().equals(inName)) {
				JOptionPane.showMessageDialog(new JFrame(), "Task already exists.", "New task error",
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
		if ("pressTaskButton".equals(evt.getPropertyName())) {
			System.out.println("pressTaskButton");
		}
	}

	public static void addPropertyChangeListener(TaskButton taskButton) {
		// TODO Auto-generated method stub
		System.out.println("pcl");
	}
}
