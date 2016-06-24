package tasklogger;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;

public class TaskButton extends JButton {

	private static final long serialVersionUID = -9193221835511157635L;
	private int taskId;
	private Task task;

	public TaskButton(final Task inTask) {
		task = inTask;
		setBackground(Color.green);
		setActionCommand("taskButton");
		stop();
	}

	public void start() {
		setText("Stop Task");
		setBackground(Color.red);
		repaint();
	}

	public void stop() {
		setText("Start Task");
		setBackground(Color.green);
		repaint();
	}

	public static class MyPropertyChangeListener implements PropertyChangeListener {
		// This method is called every time the property value is changed
		public void propertyChange(PropertyChangeEvent evt) {
			System.out.println("Name = " + evt.getPropertyName());
			System.out.println("Old Value = " + evt.getOldValue());
			System.out.println("New Value = " + evt.getNewValue());
			System.out.println("**********************************");
		}
	}
}
