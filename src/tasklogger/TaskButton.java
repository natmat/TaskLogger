package tasklogger;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class TaskButton extends JButton implements PropertyChangeListener {

	private static final long serialVersionUID = -9193221835511157635L;
	private int taskID;
	private String taskName;

	public TaskButton(final int id) {
		super();
		taskID = id;
		taskName = TLModel.getTaskName(taskID);
		setText(taskName);
		setHorizontalAlignment(SwingConstants.LEFT);
		
		setActionCommand("taskButton");
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if ((evt.getModifiers() & ActionEvent.META_MASK) > 0) {					
					System.out.println("CMD pressed");
					editTaskNameView();
				}
				else {
					TLController.taskButtonPressed(taskID);
				}
			}

			private void editTaskNameView() {
				String taskName = JOptionPane.showInputDialog("Enter new task name", getText());
				if (taskName.length() > 0) {
					setText(taskName);
				}
			}
		});
		
		TLModel.addPropertyChangeListener(this);
		stop();

	}

	public void start() {
		setButtonColor(Color.red);
		repaint();
	}

	public void stop() {
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
