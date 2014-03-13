package concurrentplay;

public class ScheduledExecutorPlay {

	public static void main(String[] args) throws Exception {
		UpdateRowExecutor executor = new UpdateRowExecutor(2 * 1000);

		executor.start();

		UpdateRow u11 = new UpdateRow("job1", "Created");
		UpdateRow u21 = new UpdateRow("job2", "Created");
		executor.addUpdateRow(u11);
		executor.addUpdateRow(u21);
		Thread.sleep(3000);

		UpdateRow u12 = new UpdateRow("job1", "Running");
		UpdateRow u22 = new UpdateRow("job2", "Success");
		executor.addUpdateRow(u12);
		executor.addUpdateRow(u22);
		Thread.sleep(5000);

		UpdateRow u13 = new UpdateRow("job1", "Success");
		executor.addUpdateRow(u13);

		Thread.sleep(60 * 1000);
		executor.shutDown();
	}

}
