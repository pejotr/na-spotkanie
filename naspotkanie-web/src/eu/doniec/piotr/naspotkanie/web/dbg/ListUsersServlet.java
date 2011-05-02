package eu.doniec.piotr.naspotkanie.web.dbg;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.doniec.piotr.naspotkanie.entity.User;
import eu.doniec.piotr.naspotkanie.web.dao.UserDAO;

@SuppressWarnings("serial")
public class ListUsersServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		UserDAO dao = new UserDAO();
		
		List<User> users = dao.getAllUsers();
		req.setAttribute("users_list", users);

		try {
			req.getRequestDispatcher("WEB-INF/list.jsp").forward(req, resp);
		} catch (ServletException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
