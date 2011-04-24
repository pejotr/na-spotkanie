package eu.doniec.piotr.naspotkanie.web;

import java.io.IOException;

import javax.servlet.http.*;

@SuppressWarnings("serial")
public class PositionLogServlet extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException {

		resp.setContentType("text/plain");
		resp.getWriter().println("Udało się");

	}	

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		doGet(req, resp);

	}

}
