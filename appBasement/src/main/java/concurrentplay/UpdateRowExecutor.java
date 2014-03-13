package concurrentplay;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateRowExecutor extends Thread {

	private Map<String, UpdateRow> updateWorks = new ConcurrentHashMap<String, UpdateRow>();

	private long period;

	private boolean shutDown = false;

	public UpdateRowExecutor(long period) {
		this.period = period;
	}

	public void run() {
		while (!isShutDown()) {
			Iterator<Map.Entry<String, UpdateRow>> iter = updateWorks
					.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, UpdateRow> entry = iter.next();
				UpdateRow updateRow = entry.getValue();
				try {
					updateRow.doUpdate();
					sleep(period);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (updateRow.getRemainCount() <= 0) {
						iter.remove();
					}
				}
			}
		}
	}

	public void addUpdateRow(UpdateRow t) {
		updateWorks.put(t.getJobId(), t);
	}

	public boolean isShutDown() {
		return shutDown;
	}

	public void shutDown() {
		this.shutDown = true;
	}
}
