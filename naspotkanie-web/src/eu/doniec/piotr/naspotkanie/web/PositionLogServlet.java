package eu.doniec.piotr.naspotkanie.web;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import eu.doniec.piotr.naspotkanie.web.dao.UserDAO;

@SuppressWarnings("serial")
public class PositionLogServlet extends HttpServlet {
	
	static class StorePosition {
		private double lgt;
		private double lat;
		private String username;
		private long id;
		
		StorePosition() {
			// No-arg constructor
		}
	}
	
	static class EnaDisSharing {
		private int action;
		String[] emails;
		
		EnaDisSharing() {
			// No-arg constructor
		}
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException {

		Gson gson = new Gson();
		BufferedReader reader = req.getReader();
		String json = reader.readLine();
		StorePosition pos = gson.fromJson(json, StorePosition.class);
		
		System.out.println("[DEBUG] PositionLogServlet: Read JSON [#req<" + json + ">]");
		System.out.println("[DEBUG] PositionLogServlet: After parsing [#username<" + pos.username + "> " + 
															"#lgt<" + pos.lgt + "> " + 
															"#lat<" + pos.lat + "> " +
															"#id<"  + pos.id  + ">]");
		
		UserDAO dao = new UserDAO();
		dao.setUserPosition(pos.lgt, pos.lat, pos.id);
				
		resp.setContentType("text/plain");
		resp.getWriter().println("Udało się");

	}	

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		doGet(req, resp);

	}

}
