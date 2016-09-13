package tasklogger;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class PomodoroTimer {
	private static Timer timer;
	private static JProgressBar progressBar;
	private static int duration = 25 * 60 * 1000;
	private static int countdown;
	private static JButton button;
	private static PomodoroTimer instance;
	private static int flashCounter;
	final static Color lightRed = new Color(255, 200, 200);
	final static Color darkRed = new Color(255, 100, 100);


	private PomodoroTimer() {
		progressBar = new JProgressBar(0, duration);
		progressBar.setVisible(true);
		progressBar.setStringPainted(false);
		countdown = duration;
		button = new JButton("Pomodoro");
//		button.setFont(new Font("monospaced", Font.PLAIN, 16));
		button.setToolTipText("Start/stop the POMODORO");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!timerIsRunning()) {
					startCountdownTimer();
				} else {
					cancelCountdownTimer();
				}
			}
		});
	}

	public static void startCountdownTimer() {
		if (!timerIsRunning()) {
			cancelCountdownTimer();
		}

		button.setBackground(lightRed);
		progressBar.setForeground(lightRed);
		final int timerTick = 1000;
		timer = new Timer(timerTick, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				progressBar.setUI( new BasicProgressBarUI() {
		            protected Color getSelectionBackground() {
		                return Color.BLACK;
		            }
		            protected Color getSelectionForeground() {
		                return Color.BLACK;
		            }
				});
				
				countdown -= timerTick;
				if (countdown > 0) {
					setProgressBarStatus();
					if (progressBar.getForeground().equals(lightRed) && (countdown < 0.20*duration)) {
						progressBar.setForeground(darkRed);
					}
				} else {
					timer.stop();
					flashCounter = 0;
					progressBar.setValue(0);
					progressBar.setString("Time Up");
					PomodoroTimer.flashPomodoro();
				}
			}
		});
		timer.start();
	}

	private static boolean timerIsRunning() {
		return ((timer != null) && timer.isRunning());
	}

	public static void cancelCountdownTimer() {
		if (timerIsRunning()) {
			timer.stop();
			setProgressBarStatus();
			progressBar.setStringPainted(false);
			progressBar.setBackground(Color.WHITE);
			countdown = duration;
		}
	}

	private static void setProgressBarStatus() {
		progressBar.setValue(duration - countdown);
		progressBar.setString(TLUtilities.getHMSString(countdown).substring(3));
//		progressBar.setFont(new Font("monospaced", Font.PLAIN, 16));
		progressBar.setStringPainted(true);
	}

	public Component getButton() {
		return (button);
	}

	public Component getProgressBar() {
		return (progressBar);
	}

	public static PomodoroTimer getInstance() {
		if (instance == null) {
			instance = new PomodoroTimer();
		}
		return (instance);
	}

	private static void flashPomodoro() {
		timer = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				flashCounter++;
				if ((flashCounter %= 2) == 0) {
					progressBar.setBackground(Color.RED);
				} else {
					progressBar.setBackground(Color.WHITE);
				}
			}
		});
		timer.start();
	}
}
