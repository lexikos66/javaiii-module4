package module4;

import javafx.concurrent.Task;

public class TaskRunner extends Task<Integer> {

	@Override
	protected Integer call() throws Exception {

		int iterations;
		for (iterations = 0; iterations < 1000; iterations++) {
			if (isCancelled()) {
				updateMessage("Cancelled");
				break;
			}
			updateMessage("Iteration " + iterations);
			updateProgress(iterations, 1000);

			// Block the thread for a short time, but be sure
			// to check the InterruptedException for cancellation
			try {
				Thread.sleep(100);
			} catch (InterruptedException interrupted) {
				if (isCancelled()) {
					updateMessage("Cancelled");
					break;
				}
			}
		}
		return iterations;
	}

}
