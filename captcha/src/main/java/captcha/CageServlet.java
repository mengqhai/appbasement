package captcha;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.cage.Cage;
import com.github.cage.YCage;

@WebServlet(urlPatterns = "/captcha")
public class CageServlet extends HttpServlet {

	private static final Cage cage;

	static {
		//cage = new GCage();
		//cage = new MyCage();
		cage = new YCage();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2962519683686690393L;

	/**
	 * Generates a captcha token and stores it in the session.
	 * 
	 * @param session
	 *            where to store the captcha.
	 */
	public static void generateToken(HttpSession session) {
		String token = cage.getTokenGenerator().next();
		session.setAttribute("captchaToken", token);
	}

	public static void clearToken(HttpSession session) {
		session.removeAttribute("captchaToken");
	}

	/**
	 * Marks token as used/unused for image generation.
	 * 
	 * @param session
	 *            where the token usage flag is possibly stored.
	 * @param used
	 *            false if the token is not yet used for image generation
	 */
	protected static void markTokenUsed(HttpSession session, boolean used) {
		session.setAttribute("captchaTokenUsed", used);
	}

	/**
	 * Used to retrieve previously stored captcha token from session.
	 * 
	 * @param session
	 *            where the token is possibly stored.
	 * @return token or null if there was none
	 */
	public static String getToken(HttpSession session) {
		Object val = session.getAttribute("captchaToken");

		return val != null ? val.toString() : null;
	}

	/**
	 * Checks if the token was used/unused for image generation.
	 * 
	 * @param session
	 *            where the token usage flag is possibly stored.
	 * @return true if the token was marked as unused in the session
	 */
	protected static boolean isTokenUsed(HttpSession session) {
		return !Boolean.FALSE.equals(session.getAttribute("captchaTokenUsed"));
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession(true);
		generateToken(session);
		String token = session != null ? getToken(session) : null;
		if (token == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Captcha not found.");
			return;
		}

		setResponseHeaders(resp);
		markTokenUsed(session, true);
		cage.draw(token, resp.getOutputStream());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// validate the result

		PrintWriter writer = resp.getWriter();

		String userInput = req.getParameter("userInput");
		if (userInput == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No user input.");
		}

		String token = getToken(req.getSession());
		if (userInput.equalsIgnoreCase(token)) {
			writer.print("You input is correct!");
			clearToken(req.getSession());
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong input.");
		}
	}

	/**
	 * Helper method, disables HTTP caching.
	 * 
	 * @param resp
	 *            response object to be modified
	 */
	protected void setResponseHeaders(HttpServletResponse resp) {
		resp.setContentType("image/" + cage.getFormat());
		resp.setHeader("Cache-Control", "no-cache, no-store");
		resp.setHeader("Pragma", "no-cache");
		long time = System.currentTimeMillis();
		resp.setDateHeader("Last-Modified", time);
		resp.setDateHeader("Date", time);
		resp.setDateHeader("Expires", time);
	}

}
