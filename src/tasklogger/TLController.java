package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;

public class TLController implements ActionListener, PropertyChangeListener {
	private static TLModel model;
	private static TLController instance;

	public static TLController getInstance() {
		if (instance == null) {
			instance = new TLController();
		}
		return (instance);
	}

	private TLController() {
		TLModel.addPropertyChangeListener(this);
	}

	public void setModel(final TLModel inModel) {
		model = inModel;
	}

	public void setView(final TLView inView) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("task")) {
			System.out.println("Action");
		}
	}

	public static void taskButtonPressed(int taskID) {
		System.out.println("taskButtonPressed");
		model.tasktButtonPressed(taskID);
	}

	public static void newTask() {
		final String dialogString = "Enter task name";
		String taskName = JOptionPane.showInputDialog(TLController.getInstance(), dialogString);
		if (!TLUtilities.isValidName(taskName, dialogString)) {
			return;
		}

		TLTask task = TLModel.newTask(taskName);
		if (task == null) {
			return;
		}
		TLView.addTask(task.getTaskID());
	}	

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt instanceof IndexedPropertyChangeEvent) {
			// Process per task pce's
		     taskIndexedPropertyChange((IndexedPropertyChangeEvent)evt);
		}
		else {
			// Process per class pce's
			String name = evt.getPropertyName();
			if (name.startsWith("totalRunTimeInMs")) {
				TLView.setTotalTimerInMs(((Number)evt.getNewValue()).longValue());
			}
		}
	}
	
	private void taskIndexedPropertyChange(IndexedPropertyChangeEvent evt) {
		String name = evt.getPropertyName();			
		int taskID = evt.getIndex();
		if (name.startsWith("taskStateChange")) {
			TLView.taskEvent(taskID, evt.getNewValue());
		}
		else if (name.startsWith("activeTimeInMs")) {
			TLView.setActiveTimeInMs(taskID, evt.getNewValue());
		}
	}

	public static void deleteTask(int taskID) {
		TLView.deleteTask(taskID);
	}

	public static void removeTask(int taskID) {
		TLView.removeTask(taskID);
	}
}
