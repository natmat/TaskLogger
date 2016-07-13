package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.Timer;

public class TLTask {
	private static TLTask activeTask = null;
	private long activeTimeInMs;
	private ActionListener actionListender;
	private String name;
	private PropertyChangeSupport pcs;
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

		pcs = new PropertyChangeSupport(this);
	}

	public TLTask(String inName) {
		this();
		name = inName;
	}

	public TLTask(String inName, long timeInMs) {
		this(inName);
		activeTimeInMs = timeInMs;
		System.out.println("TLTask[" + this.getTaskID() + "]:" + inName + "," + timeInMs);
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
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
		setActiveTask(this);
		toggleState();
		clockTimer.start();
	}

	private void cancel() {
		clockTimer.stop();
		toggleState();
		setActiveTask(null);
	}

	private void toggleState() {
		Boolean before = running;
		running = new Boolean(!running.booleanValue());
		Boolean after = running;
		pcs.firePropertyChange("taskStateChange" + taskID, before, after);
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

	public long getTaskTimeInMs() {
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
	public void setActiveTime(long timeInMs) {
//		long t = getTotalRunTimeInMs();
//		t -= (activeTimeInMs - timeInMs);
//		setTotalTime(t);
//		activeTimeInMs = timeInMs;
//		TLView.tickTimers(this, timeInMs, totalRunTimeInMs);
	}
}
