package tasklogger;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Timer;
import java.util.TimerTask;

public class TLTask {
	private TimerTask timerTask;
	private Timer timer;
	private long activeTimeInMs;
	private Boolean running;
	private String name;
	private ActionListener actionListender;
	private int taskID;
	private PropertyChangeSupport pcs;
	private long runTimeInMS;
	static private long totalRunTimeInMs;
	private static TLTask activeTask = null;

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
		}
		else {
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
				TLView.tickTimers(TLTask.this, 
						(activeTimeInMs + runTimeInMS), 
						(totalRunTimeInMs + runTimeInMS));

			}
		};
		timer.scheduleAtFixedRate(timerTask, 0, 1000);
		activeTask = this;
	}

	private void cancel() {
		if (timer != null) { 
			timer.cancel();
			toggleState();
			activeTask = null;
			totalRunTimeInMs += runTimeInMS;
			activeTimeInMs += runTimeInMS;
		}
	}

	private void toggleState() {
		Boolean before = running;
		running = new Boolean(!running.booleanValue());
		Boolean after = running;
		pcs.firePropertyChange("task:"+taskID, before, after);
	}

	public Boolean getTaskState() {
		return(running);
	}

	public void setTitle(String taskName) {
		// TODO Auto-generated method stub
		name = taskName;
	}

	public String getName() {		
		return name;
	}

	public ActionListener getActionListener() {
		return(actionListender);
	}

	public int getTaskID() {
		return taskID;
	}

	public Boolean getRunning() {
		return(running);
	}

	public static TLTask getActiveTask() {
		return(activeTask);
	}

	public static long getTotalRunTimeInMs() {
		long timeInMs = 0;
		if (activeTask != null) {
			TLTask t = activeTask;
			t.cancel();
			timeInMs = totalRunTimeInMs;
			t.start();
			activeTask = t;
		}
		else {
			timeInMs = totalRunTimeInMs;
		}
		return(timeInMs);
	}

	public long getTaskTimeInMs() {
		return(activeTimeInMs + runTimeInMS);
	}
}

