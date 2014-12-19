package sse.servlet;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import sse.services.WorkerService;

@WebServlet(urlPatterns = { "/sseasync" }, asyncSupported = true)
public class SSEAsyncServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8141529499308812024L;

	@Autowired
	private WorkerService workerService;

	/**
	 * To add spring autowire support
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// set content type
		res.setContentType("text/event-stream");
		res.setCharacterEncoding("UTF-8");
		// final String msg = req.getParameter("msg");
		AsyncContext ac = req.startAsync();
		ac.setTimeout(0); // no timeout
		workerService.addAsync(ac);
	}

}
