package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		return(instance);
	}
	
	private TLController() {
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
		task.addPropertyChangeListener(TLController.getInstance());
	}	

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		if ("taskStateChange".equals(name.substring(0, 5))) {
			int taskID = Integer.parseInt(name.substring(name.indexOf(":")+1, name.length()));
			TLView.taskEvent(taskID, evt.getNewValue());
		}
		else if ("taskActiveTime".equals(name.substring(0, 5))) {
		}
	}

	public static void deleteTask(int taskID) {
		TLView.deleteTask(taskID);
	}
}

