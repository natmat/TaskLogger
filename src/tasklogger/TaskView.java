package tasklogger;

import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.JTextField;

public class TaskView {
	private Task task;
	private TaskButton button;
	private JTextField timer;

	public TaskView(Task inTask) {
		task = inTask;
		addButton(inTask);
		addTimer();
	}

	private void addButton(Task inTask) {
		button = new TaskButton(inTask);
		button.setActionCommand("taskButtonPressed");
		button.addActionListener(inTask.getActionListener());
		System.out.println("AL=" + inTask.getActionListener());
	}

	private void addTimer() {
		timer = new JTextField();
		timer.setText("00:00:00");
	}

	public Component getButton() {
		return(button);
	}

	public Task getTask() {
		return task;
	}

	public JTextField getTimer() {
		return(timer);
	}
}
