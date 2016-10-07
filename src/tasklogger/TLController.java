package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TLController implements ActionListener, PropertyChangeListener, Runnable {
	
	private static TLController instance = null;
	private final static Object waiter = new Object();

	public static TLController getInstance() {
		if (instance == null) {
			instance = new TLController();
		}
		return (instance);
	}

	private TLController() {
		TLModel.addPropertyChangeListener(this);		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("task")) {
			System.out.println("Action");
		}
	}

	/**
	 * Notify model on taskButton press
	 * @param taskID
	 */
	public static void taskButtonPressed(int taskID) {
		TLModel.tasktButtonPressed(taskID);
	}

	private class CustomDialog extends JDialog {
		private static final long serialVersionUID = -817888208126512619L;
		private JPanel inputPanel = null;
		private JTextField inputText = null;
		private String newTaskName;
		private final JFrame parentFrame;
		private final String defaultName;

		public CustomDialog(JFrame frame, final String dialogString) {
			super(frame, true);
			defaultName = dialogString;
			parentFrame = frame;
			setLocationRelativeTo(frame);
		}

		String showDialog() {
			inputPanel = new JPanel();
			getContentPane().add(inputPanel);
			inputText = new JTextField(defaultName, 24);

			// Handle user pressing ENTER
			newTaskName = null;
			inputText.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					newTaskName = inputText.getText();
					setVisible(false);
					dispose();
				}
			});
			inputText.selectAll();
			inputPanel.add(inputText);
			pack();

			// Set location relative to frame
			setLocation(
					parentFrame.getLocation().x,
					parentFrame.getLocation().y
					+ (int) (parentFrame.getSize().getHeight()));

			setVisible(true);
			return (newTaskName);
		}
	}

	public static void newTask(final String newName) {
		// Add new named task
		TLModel.newTask(newName);
	}

	public static void deleteTaskFormView(int taskID) {
		TLView.deleteTask(taskID);
	}

	public static void deleteTaskFromModel(int taskID) {
		TLModel.deleteTask(taskID);
	}

	@Override
	public void run() {
	}

	public static Object getWaiter() {
		return waiter;
	}

	@SuppressWarnings("unused")
	private static String showTaskNameDialog() {
		String taskName = null;
		final String dialogString = "[enter task name]";
		CustomDialog cd = new TLController().new CustomDialog(TLView.getInstance(), dialogString);
		taskName = cd.showDialog();
		if (!TLUtilities.isValidName(taskName, dialogString)) {
			taskName = null;
		}
		return taskName;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("TLController PCE");
	}
}
