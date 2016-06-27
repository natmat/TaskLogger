package tasklogger;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;

public class TaskButton extends JButton implements PropertyChangeListener {

	private static final long serialVersionUID = -9193221835511157635L;
	private int taskID;

	public TaskButton(final int id) {
		super();
		taskID = id;
		setActionCommand("taskButton");
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(">AL");
				TaskLoggerView.buttonPressed(taskID);
			}
		});
		addPropertyChangeListener(TaskLoggerModel.getInstance());
		setText("Stop");
		stop();
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
		System.out.println("Name = " + evt.getPropertyName());
		System.out.println("Old Value = " + evt.getOldValue());
		System.out.println("New Value = " + evt.getNewValue());
		System.out.println("**********************************");

		if ("taskRunning".equals(evt.getPropertyName())) {
			boolean running = (boolean) evt.getNewValue();
			if (running) {
				start();
			} else {
				stop();
			}
		}
	}
}
