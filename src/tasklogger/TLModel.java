package tasklogger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author Nathan
 *
 */
public class TLModel implements PropertyChangeListener {

	private static ArrayList<TLTask> taskArray;
	private static TLModel instance;
	private static PropertyChangeSupport pcs;
	private static ArrayList<TLTask> taskList;

	private TLModel() {
		taskArray = new ArrayList<>();
		pcs = new PropertyChangeSupport(this);
	}

	private int extractTaskIDFromString(final String name) {
		int taskID = 0;
		if (name.contains("taskButton")) {
			int iID = name.indexOf(':') + 1;
			taskID = Integer.parseInt(name.substring(iID, name.length()));
			System.out.println("taskID=" + taskID);
		}
		return (taskID);
	}

	public static TLModel getInstance() {
		if (instance == null) {
			instance = new TLModel();
		}
		return (instance);
	}

	private static TLTask getTaskWithID(int taskID) {
		for (TLTask t : taskArray) {
			if (t.getTaskID() == taskID) {
				return (t);
			}
		}
		return null;
	}

	public static TLTask newTask(final String inName) {
		// Find task in arrayTask
		for (TLTask t : taskArray) {
			if (t.getName().equals(inName)) {
				JOptionPane.showMessageDialog(new JFrame(), "Task already exists.", "New task error",
						JOptionPane.ERROR_MESSAGE);
				return (null);
			}
		}

		TLTask task = new TLTask(inName);
		PropertyChangeSupport pcs = new PropertyChangeSupport(TLModel.getInstance());
		pcs.firePropertyChange("taskAction:" + task.getTaskID(), task.getRunning().booleanValue(), 0);
		taskArray.add(task);
		return (task);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		int taskID = extractTaskIDFromString(evt.getPropertyName());
		if (taskID > 0) {
			System.out.println("TLM>" + evt.getPropertyName());
			for (TLTask t : taskArray) {
				if (taskID == t.getTaskID()) {
					t.actionTask();
					return;
				}
			}
		}
	}

	/**
	 * @param taskID
	 */
	public void tasktButtonPressed(int taskID) {
		TLTask task = getTaskWithID(taskID);
		if (task == null) {
			return;
		}

		if (task != TLTask.getActiveTask()) {
			TLTask.getActiveTask().actionTask();
		}
		task.actionTask();
	}

	public static void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public static void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

	public void taskViewButtonPressed() throws Exception {
		if (taskList.isEmpty()) {
			throw (new Exception("empty taskList"));
		}
		try {
			taskList.get(0).actionTask();
			// view.setTaskState(taskList.get(0).getTaskState());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getTaskName(int inTaskID) {
		return (getTaskWithID(inTaskID).getName());
	}

	public static void addTeamLeaderTask() {
		newTask("Team Leader");
	}
}
