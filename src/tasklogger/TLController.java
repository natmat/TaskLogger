package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TLController implements ActionListener, PropertyChangeListener, Runnable {
	private static TLModel model;
	private static TLController instance;
	private static SynchronousQueue<Boolean> queue;

	public static TLController getInstance() {
		if (instance == null) {
			instance = new TLController();
		}
		return (instance);
	}

	private TLController() {
		queue = new SynchronousQueue<>();
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

	public static void newTask() {
		// Enter new task from Excel
		new Thread(new ExcelReader()).start();
		try {
			queue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		final String dialogString = "[enter task name]";
		CustomDialog cd = new TLController().new CustomDialog(TLView.getInstance(), dialogString);
		String taskName = cd.showDialog();
		if (!TLUtilities.isValidName(taskName, dialogString)) {
			return;
		}

		TLTask task = TLModel.newTask(taskName);
		if (task == null) {
			TLView.getInstance().setAlwaysOnTop(false);
			JOptionPane.showMessageDialog(new JFrame(),
					"Task already exists.", "New task error",
					JOptionPane.ERROR_MESSAGE);			
			TLView.getInstance().setAlwaysOnTop(true);
			return;
		}
		TLView.addTask(task.getTaskID());
		TLController.taskButtonPressed(task.getTaskID());
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
		// TODO Auto-generated method stub
		
	}
}
