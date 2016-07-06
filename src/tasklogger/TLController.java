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
//		model.addPropertyChangeListener(this);
//		view.addPropertyChangeListener(this);
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

	public static void taskButtonPressed(int taskID) {
		model.tasktButtonPressed(taskID);
	}

	public static void newTask() {
		String taskName = JOptionPane.showInputDialog(TLController.getInstance(), "Enter Task : [WBS][Summary]");
		TLTask task = TLModel.newTask(taskName);
		if (task == null) {
			return;
		}
	
		view.addTask(task.getTaskID());
		task.addPropertyChangeListener(TLController.getInstance());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		if ("task:".equals(name.substring(0, 5))) {
			System.out.println("TLC>name="+name);
			int taskID = Integer.parseInt(name.substring(name.indexOf(":")+1, name.length()));
			System.out.println("taskID="+taskID);
			TLView.taskEvent(taskID, evt.getNewValue());
			
		}
	}
}

