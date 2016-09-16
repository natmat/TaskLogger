package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TLController implements ActionListener, PropertyChangeListener, Runnable {
	private static TLModel model;
	private static TLController instance;
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

	public void setModel(final TLModel inModel) {
		model = inModel;
	}

	public void setView(final TLView inView) {
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
		model.tasktButtonPressed(taskID);
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
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt instanceof IndexedPropertyChangeEvent) {
			// Process per task pce's
			taskIndexedPropertyChange((IndexedPropertyChangeEvent) evt);
		} else {
			// Process per class pce's
			String name = evt.getPropertyName();
			if (name.startsWith("totalRunTimeInMs")) {
				TLView.setTotalTimerInMs(((Number) evt.getNewValue())
						.longValue());
			}
		}
	}

	private void taskIndexedPropertyChange(IndexedPropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		int taskID = evt.getIndex();
		if (name.startsWith("taskStateChange")) {
			TLView.taskEvent(taskID, evt.getNewValue());
		} else if (name.startsWith("activeTimeInMs")) {
			TLView.setActiveTimeInMs(taskID, evt.getNewValue());
		}
	}

	public static void deleteTask(int taskID) {
		TLView.deleteTask(taskID);
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
}
