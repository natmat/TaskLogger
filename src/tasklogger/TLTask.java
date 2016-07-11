package tasklogger;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Timer;
import java.util.TimerTask;

public class TLTask {
	private static TLTask activeTask = null;
	private long activeTimeInMs;
	private ActionListener actionListender;
	private String name;
	private PropertyChangeSupport pcs;
	private Boolean running;
	private long runTimeInMS;
	private int taskID;
	private Timer timer;
	private TimerTask timerTask;
	static private long totalRunTimeInMs;

	public TLTask() {
		taskID = System.identityHashCode(this);
		activeTimeInMs = 0;
		running = new Boolean(false);

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
			runTimeInMS = 0;
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
		timer = new Timer();
		final long startTime = System.currentTimeMillis();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				// Update text with hh:mm:ss count
				runTimeInMS = System.currentTimeMillis() - startTime;
				TLView.tickTimers(TLTask.this, (activeTimeInMs + runTimeInMS), (totalRunTimeInMs + runTimeInMS));

			}
		};
		timer.scheduleAtFixedRate(timerTask, 0, 1000);
		setActiveTask(this);
	}

	private void cancel() {
		if (timer != null) {
			timer.cancel();
			toggleState();
			setActiveTask(null);
			totalRunTimeInMs += runTimeInMS;
			activeTimeInMs += runTimeInMS;
		}
	}

	private void toggleState() {
		Boolean before = running;
		running = new Boolean(!running.booleanValue());
		Boolean after = running;
		pcs.firePropertyChange("task:" + taskID, before, after);
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
		long timeInMs = 0;
		if (getActiveTask() != null) {
			TLTask t = getActiveTask();
			t.cancel();
			timeInMs = totalRunTimeInMs;
			t.start();
			setActiveTask(t);
		} else {
			timeInMs = totalRunTimeInMs;
		}
		return (timeInMs);
	}

	public long getTaskTimeInMs() {
		return (activeTimeInMs + runTimeInMS);
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
}
