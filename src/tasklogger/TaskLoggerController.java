package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TaskLoggerController implements ActionListener{
  private static TaskLoggerView view;
  private static TaskLogger logger;

  public TaskLoggerController(final TaskLogger inLogger) {
    logger = inLogger;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("task")) {
      System.out.println("Action");
    }
  }

  public void startButtonPressed() {
    logger.startButtonPressed();
  }

  public void newTask() {
    // TODO Auto-generated method stub
    logger.newTask();   
  }

  public void addTaskToView(Task t) {
    // TODO Auto-generated method stub
    view.addTask(t);    
  }

  public void setView(TaskLoggerView inView) {
    view = inView;
  }
}

