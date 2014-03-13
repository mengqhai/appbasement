package concurrentplay;

public class UpdateRow {

	private String jobId;

	private String newState;

	private int remainCount = 5;

	public UpdateRow(String jobId, String newState) {
		this.jobId = jobId;
		this.newState = newState;
	}

	public synchronized void doUpdate() {
		System.out.println("Sending update message for job " + this.jobId + " "
				+ this.newState + "    ----- Active thread count:"
				+ Thread.activeCount());
		remainCount--;
	}

	public synchronized int getRemainCount() {
		return remainCount;
	}

	public String getJobId() {
		return jobId;
	}

	public String getNewState() {
		return newState;
	}

}
