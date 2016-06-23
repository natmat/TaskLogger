package tasklogger;

// Nathan

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class Task {
  private TimerTask timerTask;
  private Timer timer;
  private int seconds;
  private Boolean running;
  private String name;
  private ActionListener actionListender;
  private int taskID;

  public Task() {
    taskID = System.identityHashCode(this);
    seconds = 0;    
    running = new Boolean(false);
    
    actionListender = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("AL=" + getActionListener());
        String command = e.getActionCommand();
        if (command.equals("taskButtonPressed")) {
          actionTask();
        }
      }
    };
  }

  protected void actionTask() {
    if (!running) {
      try {
        start();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    else {
      cancel();
    }
  }

  private void start() throws Exception {
    timer = new Timer();
    timerTask = new TimerTask() {           
      @Override
      public void run() {
        // Update text with hh:mm:ss count
        System.out.println("hms=" + convertSecondToHHMMString(seconds));
        TaskLogger.taskPulse(Task.this);
        seconds++;
      }
    };

    timer.scheduleAtFixedRate(timerTask, 0, 1000);
    running = true;
  }

  protected void cancel() {
    if (timer != null) { 
      timer.cancel();
      running = false;
    }
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
}

