package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TLController implements ActionListener, PropertyChangeListener {
	private static TLModel model;
	private static TLController instance;

	public static TLController getInstance() {
		if (instance == null) {
			instance = new TLController();			
		}
		return(instance);
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

	public static void taskButtonPressed(int taskID) {
		model.tasktButtonPressed(taskID);
	}
	
	private class CustomDialog extends JDialog {
		private JPanel myPanel = null;
		private JButton yesButton = null;
		private JButton noButton = null;

		public CustomDialog(JFrame frame, boolean modal, String myMessage) {
			super(frame, modal);
			myPanel = new JPanel();
			getContentPane().add(myPanel);
			myPanel.add(new JLabel(myMessage));
			yesButton = new JButton("Yes");
			myPanel.add(yesButton);
			noButton = new JButton("No");
			myPanel.add(noButton);
			pack();
			//setLocationRelativeTo(frame);
			setLocation(10, 10); // <--
			setVisible(true);
		}
	}

	public static void newTask() {
		final String dialogString = "Enter task name";
		new TLController().new CustomDialog(TLView.getInstance(), false, "Hello");
//		String taskName = JOptionPane.showInputDialog(
//				new TLController().new CustomDialog(TLView.getInstance(), false, "Hello"),
//				 dialogString,
//				 "Add new task",
//				 JOptionPane.QUESTION_MESSAGE);
		String taskName = new String("nathan");
		if (!TLUtilities.isValidName(taskName, dialogString)) {
			return;
		}
		
		TLTask task = TLModel.newTask(taskName);
		if (task == null) {
			return;
		}
		TLView.addTask(task.getTaskID());
		TLController.taskButtonPressed(task.getTaskID());
	}	

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt instanceof IndexedPropertyChangeEvent) {
			// Process per task pce's
		     taskIndexedPropertyChange((IndexedPropertyChangeEvent)evt);
		}
		else {
			// Process per class pce's
			String name = evt.getPropertyName();
			if (name.startsWith("totalRunTimeInMs")) {
				TLView.setTotalTimerInMs(((Number)evt.getNewValue()).longValue());
			}
		}
	}
	
	private void taskIndexedPropertyChange(IndexedPropertyChangeEvent evt) {
		String name = evt.getPropertyName();			
		int taskID = evt.getIndex();
		if (name.startsWith("taskStateChange")) {
			TLView.taskEvent(taskID, evt.getNewValue());
		}
		else if (name.startsWith("activeTimeInMs")) {
			TLView.setActiveTimeInMs(taskID, evt.getNewValue());
		}
	}

	public static void deleteTask(int taskID) {
		TLView.deleteTask(taskID);
	}
}

