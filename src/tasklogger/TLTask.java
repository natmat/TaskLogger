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

public class TLTask {
	private TimerTask timerTask;
	private Timer timer;
	private int seconds;
	private Boolean running;
	private String name;
	private ActionListener actionListender;
	private int taskID;
	private PropertyChangeSupport pcs;

	public TLTask() {
		taskID = System.identityHashCode(this);
		seconds = 0;		
		running = new Boolean(false);

		pcs = new PropertyChangeSupport(this);

		actionListender = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				if (command.equals("taskButtonPressed")) {
					actionTask();
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
		System.out.println("start()");
		toggleState();
		timer = new Timer();
		timerTask = new TimerTask() {						
			@Override
			public void run() {
				// Update text with hh:mm:ss count
				System.out.println("hms=" + convertSecondToHHMMString(seconds));
				TaskLogger.taskPulse(TLTask.this);
				seconds++;
			}
		};
		timer.scheduleAtFixedRate(timerTask, 0, 1000);
	}

	private void cancel() {
		if (timer != null) { 
			timer.cancel();
			System.out.println("cancel()");
			toggleState();
		}
	}

	private void toggleState() {
		Boolean before = running;
		running = new Boolean(!running.booleanValue());
		Boolean after = running;
		pcs.firePropertyChange("task:"+taskID, before, after);
	}

	private String convertSecondToHHMMString(int seconds)
	{
		TimeZone tz = TimeZone.getTimeZone("UTC");
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		df.setTimeZone(tz);
		String time = df.format(new Date(seconds*1000L));
		return time;
	}

	public String getHMSString() {
		return(convertSecondToHHMMString(seconds));
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
}

