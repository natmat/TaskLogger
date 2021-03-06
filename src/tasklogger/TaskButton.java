package tasklogger;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class TaskButton extends JButton {

	private static final long serialVersionUID = -9193221835511157635L;
	private int taskID;
	private String taskName;
	final private Color redColor = new Color(255,153,153); 
	final private Color greenColor = new Color(124,252,0);

	public TaskButton(final int id) {
		super();
		taskID = id;
		taskName = TLModel.getTaskName(taskID);
		setText(taskName);
		setHorizontalAlignment(SwingConstants.LEFT);

		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if ((evt.getModifiers() & ActionEvent.CTRL_MASK) > 0) {
					// Pressed with CTRL: rename
					final String dialogString = "Enter new task name";
					Object dialogObj = JOptionPane.showInputDialog(null,
							"Enter new task name", "Edit task name",
							JOptionPane.QUESTION_MESSAGE, null, null, getText());
					
					if (dialogObj != null) {
						String taskRenamed = dialogObj.toString();
						if (TLUtilities.isValidName(taskRenamed, dialogString)) {							
							if (TLModel.setTaskName(taskID, taskRenamed)) {
								setText(taskRenamed);
							}
						}
					}
				} else if ((evt.getModifiers() & ActionEvent.ALT_MASK) > 0) {
					// Pressed with ALT: delete
					JFrame frameOnTop = new JFrame();
					frameOnTop.setLocation(getLocation().x, getLocation().y);
					frameOnTop.setAlwaysOnTop(true);
					frameOnTop.setVisible(true);
					
					int dialogResult = JOptionPane.showConfirmDialog(frameOnTop,
							"Delete task " + TaskButton.this.taskName + "?",
							"Delete?", JOptionPane.YES_NO_OPTION);					
					if (dialogResult == JOptionPane.YES_OPTION) {
						TLController.deleteTaskFormView(taskID);
					}
					frameOnTop.dispose();
				} else {
					TLController.taskButtonPressed(taskID);
				}
			}
		});

		// Task inactive on new
		stop();
	}

	public void start() {
		setButtonColor(redColor);
		repaint();
	}

	public void stop() {
		setButtonColor(greenColor);
		repaint();
	}

	private void setButtonColor(Color buttonColor) {
		setBackground(buttonColor);
		setOpaque(true);
		setBorderPainted(true);
	}
}


