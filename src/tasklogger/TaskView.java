package tasklogger;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JTextField;

public class TaskView {
	private static final int TASK_VIEW_HEIGHT = 8;
	private static final int TASK_VIEW_LENGTH = 16;
	private TaskButton button;
	private JTextField timer;
	private int taskID;

	public TaskView(int id) {
		taskID = id;
		button = new TaskButton(taskID);
		button.setText(TLModel.getTaskName(taskID));
		timer = new JTextField(TLModel.getTaskTimeWithID(taskID), TASK_VIEW_HEIGHT);
		timer.setHorizontalAlignment(JTextField.CENTER);
		timer.setFont(new Font("monospaced", Font.PLAIN, TASK_VIEW_LENGTH));
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

	public static TaskView getTaskViewWithId(final ArrayList<TaskView> taskViews, int taskID) {
		for (TaskView tv : taskViews) {
			if (tv.getTaskID() == taskID) {
				return(tv);
			}
		}
		return(null);
	}
}

