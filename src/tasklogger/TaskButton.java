package tasklogger;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;

public class TaskButton extends JButton implements PropertyChangeListener {

	private static final long serialVersionUID = -9193221835511157635L;
	private int taskId;
	private Task task;

	public TaskButton(final Task inTask) {
		task = inTask;
		setActionCommand("taskButton");
		stop();
		
		inTask.addPropertyChangeListener(this);
	}

	public void start() {
		setText("Stop Task");
		setButtonColor(Color.red);
		repaint();
	}

	public void stop() {
		setText("Start Task");
		setButtonColor(Color.green);
		repaint();
	}

	private void setButtonColor(Color col) {
		setBackground(col);
		setOpaque(true);
		setBorderPainted(false);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		System.out.println("Name = " + evt.getPropertyName());
		System.out.println("Old Value = " + evt.getOldValue());
		System.out.println("New Value = " + evt.getNewValue());
		System.out.println("**********************************");

		if ("taskRunning".equals(evt.getPropertyName())) {
			boolean running = (boolean) evt.getNewValue();
			if (running) {
				start();
			}
			else {
				stop();
			}
		}
	}
}


