package tasklogger;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class TLView extends JFrame implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	private TaskButton startButton;
	private static JPanel mainPanel;
	private JButton newTaskButton;
	private static JPanel controlsPanel;
	private static JPanel taskPanel;
	private static JPanel pomodoroPanel;
	private static JTextField totalTimer;
	private static ArrayList<TaskView> taskViewList;
	private static TLView instance;
	final private Color saveColor = new Color(255, 255, 102);
	final private Color newTaskColor = new Color(255, 153, 51);
	final private Color resetColor = new Color(204,229,255);

	public static TLView getInstance() {
		if (instance == null) {
			instance = new TLView();
		}
		return (instance);
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
		
		add(new TLMenu());
		
		controlsPanel = new JPanel();
		controlsPanel.setLayout(new GridLayout(0, 2));		
		addResetButtonToControls();
		addTotalTimerFieldToControls();
		addNewTaskButtonToControls();
		addSaveButtonToControls();
		mainPanel.add(controlsPanel);

		taskPanel = new JPanel();
		taskPanel.setLayout(new GridLayout(0, 2));
		mainPanel.add(taskPanel);

		pomodoroPanel = new JPanel();
		pomodoroPanel.setLayout(new GridLayout(1, 2));
		addPomodoroToView();
		mainPanel.add(pomodoroPanel);

		Container container = this.getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		setLocation(600, 300);
		setResizable(false);
		container.add(mainPanel);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				int quit = JOptionPane.YES_OPTION;
				try {
					TLModel.exportCVSFile();
				} catch (IOException e) {
					// e.printStackTrace();
					quit = JOptionPane.showConfirmDialog(null,
							"Export failed.\nQuit anyway?",
							"Export CSV logging", JOptionPane.YES_NO_OPTION);
				}
				if (JOptionPane.YES_OPTION == quit) {
					dispose();
					System.exit(0);
				}
			}
		});
	}

	private void addPomodoroToView() {
		PomodoroTimer pomodoroTimer = PomodoroTimer.getInstance();
		// try {
		// Image img = ImageIO.read(new File("/resources/pomodoro.png"));
		// final URL img = this.getClass().getResource("pomodoro.png");
		// pomodoro.setIcon(new ImageIcon(img));
		// throw(new IOException());
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// }

//		URL url = getClass().getResource("/pomodoro.png");
//		System.out.println(url.getPath());

		pomodoroPanel.add(pomodoroTimer.getButton());
		pomodoroPanel.add(pomodoroTimer.getProgressBar());
		mainPanel.add(pomodoroPanel);
	}

	private void addSaveButtonToControls() {
		JButton saveButton = new JButton("Save times to file");
		saveButton.setBackground(saveColor);
		saveButton.setOpaque(true);
		saveButton.setHorizontalAlignment(SwingConstants.CENTER);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TLModel.saveTaskTimes();
			}
		});
		controlsPanel.add(saveButton);
	}

	private void addTotalTimerFieldToControls() {
		totalTimer = new JTextField("00:00:00", 8);
		totalTimer.setHorizontalAlignment(JTextField.CENTER);
		totalTimer.setFont(new Font("monospaced", Font.PLAIN, 24));
		controlsPanel.add(totalTimer);
	}

	private void addResetButtonToControls() {
		JButton resetButton = new JButton("Reset");
		resetButton.setBackground(resetColor);
		resetButton.setOpaque(true);
		resetButton.setHorizontalAlignment(SwingConstants.LEFT);
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("RESET");
				TLModel.reset();
			}
		});
		controlsPanel.add(resetButton);
	}

	private void addNewTaskButtonToControls() {
		newTaskButton = new JButton("Add New Task");
//		newTaskButton.setToolTipText("Add a new task to the left column's task list");
		newTaskButton.setHorizontalAlignment(SwingConstants.LEFT);
		newTaskButton.addActionListener(new AddNewTaskListener());
		newTaskButton.setActionCommand("newTaskButtonPressed");
		newTaskButton.setBackground(newTaskColor);
		controlsPanel.add(newTaskButton);
	}

	private class AddNewTaskListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.equals("newTaskButtonPressed")) {
//				System.out.println("newTaskButtonPressed");
				TLController.newTask();
			}
		}
	}

	public static void tickTimers(final TLTask inTask, long taskTimeInMs,
			long totalTimeInMs) {
		setTotalTimerInMs(totalTimeInMs);
		for (TaskView tv : taskViewList) {
			if (tv.getTaskID() == inTask.getTaskID()) {
				tv.getTimer().setText(TLUtilities.getHMSString(taskTimeInMs));
				return;
			}
		}
		System.err.println("setTimer");
	}

	public static void setTotalTimerInMs(long timeInMs) {
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
		taskPanel.add(tv.getButton());
		taskPanel.add(tv.getTimer());
		getInstance().pack();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("PCE:" + evt);
	}

	public static void taskEvent(int taskID, Object inNewValue) {
		Boolean taskRunning = ((Boolean) inNewValue).booleanValue();
		for (TaskView tv : taskViewList) {
			if (tv.getTaskID() == taskID) {
				if (taskRunning) {
					tv.getButton().start();
				} else {
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
		taskPanel.remove(tv.getButton());
		taskPanel.remove(tv.getTimer());
		taskPanel.revalidate();
		getInstance().pack();
	}

	public static void setActiveTimeInMs(int taskId, Object inObj) {
		long timeInMs = ((Number) inObj).longValue();
		for (TaskView tv : taskViewList) {
			if (tv.getTaskID() == taskId) {
				tv.getTimer().setText(TLUtilities.getHMSString(timeInMs));
			}
		}
	}

	public Dimension getDimension() {
		Dimension dim = new Dimension();		
		return dim;
	}
}
