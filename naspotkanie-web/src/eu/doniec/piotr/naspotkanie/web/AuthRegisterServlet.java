package eu.doniec.piotr.naspotkanie.web;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import eu.doniec.piotr.naspotkanie.entity.User;
import eu.doniec.piotr.naspotkanie.util.Authorization;
import eu.doniec.piotr.naspotkanie.util.EmailValidator;
import eu.doniec.piotr.naspotkanie.web.dao.UserDAO;


@SuppressWarnings("serial")
public class AuthRegisterServlet extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		Gson gson = new Gson();
		AuthRegisterResponse r = new AuthRegisterResponse();
		PrintWriter out = resp.getWriter();
		
		resp.setContentType("application/json");
		
		final String[] credentials = Authorization.parseAuthHeader(req.getHeader( "Authorization" ));
		if(credentials != null) {
			
			UserDAO dao = new UserDAO();
			String email = credentials[0].toString();
			
			if( !EmailValidator.isValid(email) ) {
				r.statusCode = 400;
				r.statusMessage = "Email addres expected ie. example@sample.com";
				
				String json = gson.toJson(r);
				out.print(json);
				return;
			}
			
			String password = credentials[1].toString();
			boolean registered = dao.isUserRegistered(email);
			
			if( !registered ) {
				System.out.println("[INFO] Trying to register user [#email=" + email + " #password="+ password +"]");
				dao.registerUser(email, password);
				User u = dao.getUser(email);
				
				r.userId = u.getId();
				r.statusCode = 200;
				r.statusMessage = "New account has been created";
			} else {
				
				User u = dao.getUser(email);
				
				if( u.getPasswordHash().equals(password) ) {
					r.userId = u.getId();
					r.statusCode = 200;
					r.statusMessage = "Authentication successful";
				} else {
					r.statusCode = 401;
					r.statusMessage = "Authentication failed. Wrong email or password";					
				}
				
			}
		} else {
			r.statusCode = 400;
			r.statusMessage = "No credentials provided";			
		}
	
		String json = gson.toJson(r);
		out.print(json);		
		
	}
	
	class AuthRegisterResponse {
		public long userId;
		public int statusCode;
		public String statusMessage;
	}
	
}
