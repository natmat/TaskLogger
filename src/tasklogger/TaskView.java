package tasklogger;

import javax.swing.JTextField;

public class TaskView {
	private TaskButton button;
	private JTextField timer;
	private int taskID;

	public TaskView(int id) {
		taskID = id;
		addButton(id);
		addTimer();
	}

	private void addButton(final int id) {
		button = new TaskButton(id);
	}

	private void addTimer() {
		timer = new JTextField();
		timer.setText("00:00:00");
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
