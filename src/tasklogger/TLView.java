package tasklogger;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.charset.MalformedInputException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

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
		setLocationRelativeTo(null);
	}

	private void setupFrame() {
		// Draw frame with top and bottom panels.
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(2,2));
		addNewTaskButtonToView();
		totalTimer = new JTextField("00:00:00", 8);
		topPanel.add(totalTimer);
		
		JButton printButton = new JButton("Print");
		printButton.setBackground(Color.YELLOW);
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
		container.add(mainPanel);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
		totalTimer.setText(TLUtilities.getHMSString(totalTimeInMs));
		for (TaskView tv : taskViewList) {
			if (tv.getTaskID() == inTask.getTaskID()) {
				tv.getTimer().setText(TLUtilities.getHMSString(taskTimeInMs));
				return;
			}
		}
		System.err.println("setTimer");
	}

	public void setTaskState(Boolean running) {
		if (running) {
			startButton.start();
		} else {
			startButton.stop();
		}
	}

	public void addTask(int taskID) {
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
		pack();
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
