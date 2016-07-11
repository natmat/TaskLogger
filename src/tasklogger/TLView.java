package tasklogger;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class TLView extends JFrame implements ActionListener, PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	private TaskButton startButton;
	private static JPanel mainPanel;
	private JPanel topPanel;
	private JButton addNewTaskButton;
	private static JPanel bottomPanel;
	private static JTextField totalTimer;
	private static ArrayList<TaskView> taskViewList;
	private static TLView instance;
	final private Color yellowColor = new Color(51,204,255);

	public static TLView getInstance() {
		if (instance == null) {
			instance = new TLView();
		}
		return(instance);
	}

	private TLView() { 
		taskViewList = new ArrayList<TaskView>();
		setupFrame();
		setAlwaysOnTop(true);
		pack();
	}

	private void setupFrame() {
		// Draw frame with top and bottom panels.
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(2,2));
		addNewTaskButtonToView();
		totalTimer = new JTextField("00:00:00", 8);
		totalTimer.setHorizontalAlignment(JTextField.CENTER);
		totalTimer.setFont(new Font("monospaced", Font.PLAIN, 16));
		topPanel.add(totalTimer);
		
		JButton printButton = new JButton("Print");
		printButton.setBackground(yellowColor);
		printButton.setOpaque(true);
		printButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TLModel.printTaskTimes();
			}
		});
		topPanel.add(printButton);
		mainPanel.add(topPanel);

		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(0, 2));
		mainPanel.add(bottomPanel);

		Container container = this.getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		setLocation(1200, 600);

		container.add(mainPanel);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
	        public void windowClosing(WindowEvent event) {
				TLModel.exportCVSFile();
	            dispose();
	            System.exit(0);
			}
		});
	}

	private void addNewTaskButtonToView() {
		addNewTaskButton = new JButton("Add new task...");
		addNewTaskButton.addActionListener(this);
		addNewTaskButton.setActionCommand("newTaskButtonPressed");
		topPanel.add(addNewTaskButton);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String command = evt.getActionCommand();
		if (command.equals("newTaskButtonPressed")) {
			System.out.println("newTaskButtonPressed");
			TLController.newTask();
		}
	}

	public static void tickTimers(final TLTask inTask, long taskTimeInMs, long totalTimeInMs) {
		setTotalTimer(totalTimeInMs);
		for (TaskView tv : taskViewList) {
			if (tv.getTaskID() == inTask.getTaskID()) {
				tv.getTimer().setText(TLUtilities.getHMSString(taskTimeInMs));
				return;
			}
		}
		System.err.println("setTimer");
	}
	
	public static void setTotalTimer(long timeInMs) {
		totalTimer.setText(TLUtilities.getHMSString(timeInMs));
	}

	public void setTaskState(Boolean running) {
		if (running) {
			startButton.start();
		} else {
			startButton.stop();
		}
	}

	public static void addTask(int taskID) {
		for (TaskView t : taskViewList) {
			if (t.getTaskID() == taskID) {
				System.err.println("Duplicate task");
				return;
			}
		}

		TaskView tv = new TaskView(taskID);
		taskViewList.add(tv);
		bottomPanel.add(tv.getButton());
		bottomPanel.add(tv.getTimer());
		getInstance().pack();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		System.out.println("TLV>name="+name);
		int taskID = Integer.parseInt(name.substring(name.indexOf(":"), name.length()));
		System.out.println("taskID="+taskID);
	}

	public static void taskEvent(int taskID, Object inNewValue) {
		Boolean taskRunning = ((Boolean)inNewValue).booleanValue();
		for (TaskView tv : taskViewList) {
			if (tv.getTaskID() == taskID) {
				if (taskRunning) {
					tv.getButton().start();
				}
				else {
					tv.getButton().stop();
				}
				return;
			}
		}

	}

	public static void deleteTask(int taskID) {
		for (TaskView tv : taskViewList) {
			if (tv.getTaskID() == taskID) {
				removeTaskViewFromPanel(tv);
				tv.deleteTask();
				tv = null;
				return;
			}
		}
	}

	private static void removeTaskViewFromPanel(final TaskView tv) {
		bottomPanel.remove(tv.getButton());
		bottomPanel.remove(tv.getTimer());
		bottomPanel.revalidate();
		getInstance().pack();
	}
}
