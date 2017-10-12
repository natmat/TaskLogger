package tasklogger;

import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date; 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

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

	public static void taskSelectorDialog(final ActionListener al, final ArrayList<String> arrayList) {
		final JDialog dialog = new JDialog(TLView.getInstance(), "Enter Task Code/Info", ModalityType.APPLICATION_MODAL);

		dialog.getContentPane().setLayout(new FlowLayout());		
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final JFrame parentFrame = TLView.getInstance();
		dialog.setLocation(parentFrame.getLocation().x, parentFrame.getLocation().y 
				+ (int) (parentFrame.getSize().getHeight()));

		// Create a comboBox and select the first item
		final JComboBox<String> taskSelectorComboBox =  new JComboBox<>(arrayList.toArray(new String[arrayList.size()]));
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

	public static void printlnMethodName() {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement elem : Thread.currentThread().getStackTrace()) {
			Pattern pattern = Pattern.compile("([^\\.]*)(\\()");
			Matcher matcher = pattern.matcher(elem.toString());
			if (matcher.find()) {
				sb.append(matcher.group(1) + " > ");
			}
		}
		System.out.println(sb);
	}

	public static boolean fileExists(File fileToTest) {
		if (null == fileToTest) {
			return(false);
		}

		final String filePathString = fileToTest.getAbsolutePath();
		final File f = new File(filePathString);
		return ((f.exists() && !f.isDirectory())); 
	}

	class TimedMessagePopupWorker extends SwingWorker<Void, Void> {
		private String message;

		public TimedMessagePopupWorker(final String inInfo) {
			this.message = inInfo;
		}

		@Override
		protected Void doInBackground() throws Exception {
			final JDialog dialog = new JDialog(new JFrame(), message, false);
			dialog.setAlwaysOnTop(true);		
			dialog.setSize(400, 20);
			dialog.setLocationRelativeTo(TLView.getInstance());
			dialog.setVisible(true);

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally {
				dialog.setVisible(false);
				dialog.dispose();
			}
			return(null);
		}
	}

	public enum eFileType {
		FILE_TYPE_UNKNOWN,
		FILE_TYPE_CSV,
		FILE_TYPE_EXCEL
	}

	public static eFileType getFileType(File chosenFile) {
		eFileType fileType = eFileType.FILE_TYPE_UNKNOWN;

		if (chosenFile.getAbsolutePath().toLowerCase().matches("^.*\\.csv$")) {
			fileType = eFileType.FILE_TYPE_CSV;
		}
		else if (chosenFile.getAbsolutePath().toLowerCase().matches("^.*\\.xlsm?$")) {
			fileType = eFileType.FILE_TYPE_EXCEL;
		}
		return(fileType);
	}
}



