package tasklogger;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.Timer;

public class PomodoroTimer {
	private static Timer timer;
	private static JProgressBar progressBar;
	private static int duration = 25 * 60 * 1000;
	private static int countdown;
	private static JButton button;
	private static PomodoroTimer instance;
	private static int flashCounter;

	private PomodoroTimer() {
		progressBar = new JProgressBar(0, duration);
		progressBar.setVisible(true);
		progressBar.setStringPainted(false);
		countdown = duration;
		button = new JButton("Pomodoro");
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

		progressBar.setBackground(new Color(255, 204, 204));
		timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (countdown > 1000) {
					countdown -= 1000;
					setProgressBarStatus();
				} else {
					timer.stop();
					flashCounter = 0;
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
		progressBar.setString(TLUtilities.getHMSString(countdown));
		progressBar.setStringPainted(true);
	}

	public static Component getButton() {
		return (button);
	}

	public static Component getProgressBar() {
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
				if (flashCounter > 20) {
					flashCounter = 0;
					timer.stop();
				} else {
					flashCounter++;
					if (PomodoroTimer.flashCounter % 2 == 0) {
						progressBar.setBackground(Color.RED);
					} else {
						progressBar.setBackground(Color.WHITE);
					}
				}
			}
		});
		timer.start();
	}
}
