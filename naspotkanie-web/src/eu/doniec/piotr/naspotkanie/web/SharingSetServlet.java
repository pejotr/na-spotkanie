package eu.doniec.piotr.naspotkanie.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import eu.doniec.piotr.naspotkanie.entity.User;
import eu.doniec.piotr.naspotkanie.web.dao.ShareDAO;
import eu.doniec.piotr.naspotkanie.web.dao.UserDAO;

@SuppressWarnings("serial")
public class SharingSetServlet extends HttpServlet {

	static class EnaDisSharing {
		int action;
		String username;
		String[] attendees;
		
		EnaDisSharing() {
			// No-arg constructor
		}
		
		String getUsername() {
			return username;
		}
		
		String[] getAttendees() {
			return attendees;
		}
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException {
	
		Gson gson	= new Gson();
		UserDAO udao = new UserDAO();
		ShareDAO sdao = new ShareDAO();
		
		BufferedReader reader = req.getReader();
		String json = reader.readLine();
		EnaDisSharing set = gson.fromJson(json, EnaDisSharing.class);
		
		System.out.println("[DEBUG] SharingSetServlet: Read JSON [#req<" + json + ">]");
		System.out.println("[DEBUG] SharingSetServlet: After parsing [#action<" + set.action + "> #emails_cnt<"+ set.getAttendees().length +">]");
		
		List<User> usersFromList = udao.getUsersOnList(set.getAttendees());
		User owner = udao.getUser(set.getUsername());
		sdao.addShares(owner, usersFromList);
		
		resp.setContentType("text/plain");
		resp.getWriter().println("Udało się");
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		doGet(req, resp);

	}
}
