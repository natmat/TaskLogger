package tasklogger;

import javax.swing.JTextField;

public class TaskView {
	private TaskButton button;
	private JTextField timer;
	private int taskID;

	public TaskView(int id) {
		taskID = id;
		button = new TaskButton(taskID);
		timer = new JTextField("00:00:00", 8);
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
}
