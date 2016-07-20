package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;

import javax.swing.Timer;

public class TLTask {
	private static TLTask activeTask = null;
	private long activeTimeInMs;
	private ActionListener actionListender;
	private String name;
	private Boolean running;
	private int taskID;
	private Timer clockTimer;
	private ClockListener clock;
	static private long totalRunTimeInMs;

	private class ClockListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Increment times
			if (activeTask != null) {
				activeTimeInMs += 1000;
			}
			totalRunTimeInMs += 1000;
			TLView.tickTimers(TLTask.this, activeTimeInMs, totalRunTimeInMs);
		}
	}
	
	public TLTask() {
		taskID = System.identityHashCode(this);
		activeTimeInMs = 0;
		running = new Boolean(false);
		clock = new ClockListener();
		clockTimer = new javax.swing.Timer(1000, clock);

		new PropertyChangeSupport(this);
	}

	public TLTask(String inName) {
		this();
		name = inName;
	}

	public TLTask(String inName, long timeInMs) {
		this(inName);
		activeTimeInMs = timeInMs;
	}

	protected void actionTask() {
		if (running) {
			cancel();
		} else {
			try {
				start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void start() {
		toggleState();
		clockTimer.start();
		setActiveTask(this);
	}

	private void cancel() {
		clockTimer.stop();
		toggleState();
		setActiveTask(null);
	}

	private void toggleState() {
		running = new Boolean(!running.booleanValue());
	}

	public Boolean getTaskState() {
		return (running);
	}

	public void setTitle(String taskName) {
		name = taskName;
	}

	public String getName() {
		return name;
	}

	public ActionListener getActionListener() {
		return (actionListender);
	}

	public int getTaskID() {
		return taskID;
	}

	public Boolean getRunning() {
		return (running);
	}

	public static TLTask getActiveTask() {
		return (activeTask);
	}

	public static long getTotalRunTimeInMs() {
		return(totalRunTimeInMs);
	}

	public long getActiveTimeInMs() {
		return (activeTimeInMs);
	}

	/**
	 * @param inTask
	 *            The task that is the active task
	 */
	public static void setActiveTask(final TLTask inTask) {
		activeTask = inTask;
	}

	/**
	 * @param timeInMs
	 *            The sum time tasks have been active
	 */
	public static void setTotalTime(long timeInMs) {
		totalRunTimeInMs = timeInMs;
	}

	/** 
	 * Set this activeTimeInMS
	 * @param timeInMs new active time (in ms)
	 */
	public void setActiveTimeInMs(long timeInMs) {
		activeTimeInMs = timeInMs;
	}
}
