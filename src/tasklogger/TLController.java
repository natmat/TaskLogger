package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;

public class TLController implements ActionListener, PropertyChangeListener {
	private static TLView view;
	private static TLModel model;
	private static TLController instance;

	public static TLController getInstance() {
		if (instance == null) {
			instance = new TLController();			
		}
		return(instance);
	}
	
	private TLController() {
		model.addPropertyChangeListener(this);
		view.addPropertyChangeListener(this);
	}
	
	public void setModel(final TLModel inModel) {
		model = inModel;
	}

	public void setView(final TLView inView) {
		view = inView;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("task")) {
			System.out.println("Action");
		}
	}

	public void taskButtonPressed(int taskID) {
		model.tasktButtonPressed(taskID);
	}

	public void newTask() {
		String taskName = JOptionPane.showInputDialog(this, "Enter Task : [WBS][Summary]");
		TLTask task = model.newTask(taskName);
		if (task == null) {
			return;
		}
	
		view.addTask(task.getTaskID());
		task.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		System.out.println("pC name="+name);
		if (name.equals("taskRunning")) {
		}
	}
}

