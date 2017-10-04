package tasklogger;

import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date; 

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TLUtilities {
	
	private static String newTaskName;
	
	public static String getHMSString(long totalTime) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		df.setTimeZone(tz);
		String time = df.format(new Date(totalTime));
		return time;
	}

	public static boolean isValidName(String taskName, String invalidString) {		
		boolean isValid = false;
		if ((taskName != null) 
				&& !taskName.isEmpty() 
				&& !(taskName.equals(invalidString))) { 
			isValid = true;
		}
		return(isValid);
	}

	public static String getToday() { 
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		return(dateFormat.format(cal.getTime()));
	}

	public enum ePropertyNames {
		TASK_STATE_CHANGE
	}

	public static void taskSelectorDialog(final ActionListener al, final ArrayList<String> taskList) {
		final JDialog dialog = new JDialog(TLView.getInstance(), "Enter Task Code/Info", ModalityType.APPLICATION_MODAL);

		dialog.getContentPane().setLayout(new FlowLayout());		
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final JFrame parentFrame = TLView.getInstance();
		dialog.setLocation(parentFrame.getLocation().x, parentFrame.getLocation().y 
				+ (int) (parentFrame.getSize().getHeight()));

		// Create a comboBox and select the first item
		final JComboBox<String> taskSelectorComboBox = 
				new JComboBox<>(taskList.toArray(new String[taskList.size()]));
		taskSelectorComboBox.setMaximumRowCount(20);
		taskSelectorComboBox.setEditable(true);
		taskSelectorComboBox.setVisible(true);
		taskSelectorComboBox.setSelectedIndex(0);
		taskSelectorComboBox.getEditor().selectAll();
		taskSelectorComboBox.setActionCommand("taskSelectorComboBox");
		dialog.add(taskSelectorComboBox);		

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(taskSelectorComboBox);

		// Button invoke close on dialog and rely on its handler to extract the selected string
		JButton b = new JButton("Select");
		b.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				// Select button clicked, so close the window
				dialog.dispose();
				return;
			}
		});
		panel.add(b);

		dialog.add(panel);
		dialog.pack();
		dialog.setVisible(true);

		class myWindowAdaptor extends WindowAdapter {
			@Override
			public void windowClosed(WindowEvent we) {
				newTaskName = taskSelectorComboBox.getSelectedItem().toString();
				ActionEvent ae = new ActionEvent(we.getSource(), 0, "taskSelectorComboBox");
				if (null != al) {
					al.actionPerformed(ae);
				}
			}
		}
		dialog.addWindowListener(new myWindowAdaptor());
	}

	public static String getNewTaskName() {
		if (newTaskName.equals(TaskLoader.getDefaultTaskName())) {
			return(null);
		}
		else {
			return (newTaskName);
		}
	}
}


