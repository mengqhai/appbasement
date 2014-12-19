package sse.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * https://weblogs.java.net/blog/swchan2/archive/2014/05/21/server-sent-events-async-servlet-example
 * 
 * 
 * @author qinghai
 *
 */
@WebServlet(urlPatterns = { "/simplesse" })
public class SSEEchoServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9164581443599768094L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// set content type
		resp.setContentType("text/event-stream");
		resp.setCharacterEncoding("UTF-8");

		String msg = req.getParameter("msg");
		PrintWriter writer = resp.getWriter();
		writer.write("data: " + msg + "\n\n");
	}

}
