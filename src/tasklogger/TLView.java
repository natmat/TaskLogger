package tasklogger;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	private JTextField timerField;
	private static TLController controller;
	private JPanel mainPanel;
	private JPanel topPanel;
	private JButton addNewTaskButton;
	private JPanel bottomPanel;
	private ArrayList<TaskView> taskViewList;
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

		pack();
		setLocationRelativeTo(null);
	}

	public void setController(TLController inController) {
		controller = inController;
	}
	
	private void setupFrame() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		topPanel = new JPanel();
		addNewTaskButtonToView();
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
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("newTaskButtonPressed")) {
			System.out.println("newTaskButtonPressed");
			controller.newTask();
		}
	}

	public void setTime(final TLTask inTask) {
		for (TaskView tv : taskViewList) {
			if (tv.getTaskID() == inTask.getTaskID()) {
				tv.getTimer().setText(inTask.getHMSString());
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

	public void addTaskToPanel(String code, String name) {
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
		System.out.println("TLV:PC");
		String name = evt.getPropertyName();
		System.out.println("name="+name);
		int taskID = Integer.parseInt(name.substring(name.indexOf(":"), name.length()));
		System.out.println("taskID="+taskID);
	}
}
