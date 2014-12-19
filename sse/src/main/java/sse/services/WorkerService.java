package sse.services;

import java.io.PrintWriter;
import java.net.SocketException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.AsyncContext;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class WorkerService {

	private List<AsyncContext> asyncList = new LinkedList<AsyncContext>();

	public synchronized void addAsync(AsyncContext context) {
		asyncList.add(context);
	}

	@Scheduled(fixedRate = 2000)
	public void writeData() {

		Iterator<AsyncContext> iter = asyncList.iterator();
		while (iter.hasNext()) {
			AsyncContext async = iter.next();
			try {
				PrintWriter writer = async.getResponse().getWriter();
				writer.print("data: event happened at "
						+ System.currentTimeMillis() + " \n\n");
				writer.flush();
			} catch (IllegalStateException illegal) {
				System.out
						.println("Async is completed:" + illegal.getMessage());
				iter.remove();
			} catch (SocketException sEx) {
				System.out.println("Client connection closed");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
