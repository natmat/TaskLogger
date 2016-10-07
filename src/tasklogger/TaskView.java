package tasklogger;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JTextField;

public class TaskView {
	private TaskButton button;
	private JTextField timer;
	private int taskID;

	public TaskView(int id) {
		taskID = id;
		button = new TaskButton(taskID);
		button.setText(TLModel.getTaskName(taskID));
		timer = new JTextField(TLModel.getTaskTimeWithID(taskID), 8);
		timer.setHorizontalAlignment(JTextField.CENTER);
		timer.setFont(new Font("monospaced", Font.PLAIN, 16));
	}

	public TaskButton getButton() {
		return (button);
	}

	public int getTaskID() {
		return taskID;
	}

	public JTextField getTimer() {
		return (timer);
	}

	public void deleteTask() {
		button = null;
		timer = null;
		taskID = 0;
	}

	public static TaskView getTaskViewWithId(final ArrayList<TaskView> al, int taskID) {
		for (TaskView tv : al) {
			if (tv.getTaskID() == taskID) {
				return(tv);
			}
		}
		return(null);
	}
}

