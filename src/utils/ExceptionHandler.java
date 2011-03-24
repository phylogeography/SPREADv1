package utils;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

	public void uncaughtException(final Thread t, final Throwable e) {
		
		if (SwingUtilities.isEventDispatchThread()) {
			showException(t, e);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					showException(t, e);
				}
			});
		}
	}

	private void showException(Thread t, Throwable e) {
		String msg = String.format("Unexpected problem on thread %s: %s", t
				.getName(), e.getMessage());

		logException(t, e);

		JOptionPane.showMessageDialog(Utils.getActiveFrame(), msg);
	}

	private void logException(Thread t, Throwable e) {
		// TODO: start a thread that logs it
		e.printStackTrace();
	}

}
