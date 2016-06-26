package tasklogger;

import java.awt.Component;

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

	private void addButton(Task inTask) {
		button = new TaskButton(inTask);
		button.setActionCommand("taskButtonPressed");
		button.addActionListener(inTask.getActionListener());
	}

	private void addTimer() {
		timer = new JTextField();
		timer.setText("00:00:00");
	}

	public Component getButton() {
		return (button);
	}

	public int getTaskID() {
		return taskID;
	}

	public JTextField getTimer() {
		return (timer);
	}
}
