package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

public class TLTask {
	private TimerTask timerTask;
	private Timer timer;
	private long taskRunTimeInMs;
	private Boolean running;
	private String name;
	private ActionListener actionListender;
	private int taskID;
	private PropertyChangeSupport pcs;
	private long elapsedTimeInMs;
	static private long totalTimeInMs;
	private static TLTask activeTask;

	public TLTask() {
		taskID = System.identityHashCode(this);
		taskRunTimeInMs = 0;		
		running = new Boolean(false);
		activeTask = null;

		pcs = new PropertyChangeSupport(this);

		actionListender = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				String command = evt.getActionCommand();
				if (command.equals("taskButtonPressed")) {
					int i = evt.getModifiers();
					if ((evt.getModifiers() & ActionEvent.CTRL_MASK) > 0) {
//						editTaskNameView();
					}
					else {
						actionTask();
					}
				}
			}
		};
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

	private void start() throws Exception {
		toggleState();
		timer = new Timer();
		final long startTime = System.currentTimeMillis();
		timerTask = new TimerTask() {		
			@Override
			public void run() {
				// Update text with hh:mm:ss count
				elapsedTimeInMs = System.currentTimeMillis() - startTime;
				TLView.tickTimers(TLTask.this, 
						(taskRunTimeInMs + elapsedTimeInMs), 
						(totalTimeInMs + elapsedTimeInMs));
						
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
			totalTimeInMs += elapsedTimeInMs;
			taskRunTimeInMs += elapsedTimeInMs;
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

	public static void setTotalSeconds(int totalSeconds) {
		TLTask.totalTimeInMs = totalSeconds;
	}

	public static TLTask getActiveTask() {
		return(activeTask);
	}
}

