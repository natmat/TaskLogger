package tasklogger;

import java.awt.Color;
import javax.swing.JButton;

public class TaskButton extends JButton {

  private static final long serialVersionUID = -9193221835511157635L;

  public TaskButton(final Task inTask) {
    setBackground(Color.green);
    setActionCommand("taskButton");
    stop();
  }

  public void start() {
    setText("Stop Task");
    setBackground(Color.red);
    repaint();
  }

  public void stop() {
    setText("Start Task");
    setBackground(Color.green);
    repaint();
  }
}
