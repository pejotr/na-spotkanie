package eu.doniec.piotr.naspotkanie.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.doniec.piotr.naspotkanie.entity.User;
import eu.doniec.piotr.naspotkanie.util.Authorization;
import eu.doniec.piotr.naspotkanie.web.dao.UserDAO;

public class AuthFilter implements Filter {

	protected FilterConfig filterConfig;
	
	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;

		System.out.println("[INFO] Access attemp");
		
		final String[] credentials = Authorization.parseAuthHeader(req.getHeader( "Authorization" ));
		
		if( credentials != null && credentials.length == 2 ) {
			
			UserDAO dao = new UserDAO();
			User u = dao.getUser(credentials[0]);
			
			if( credentials[1].equals(u.getPasswordHash())) {
				chain.doFilter(request, response);
				System.out.println("[INFO] Authorizaion successful");
				return;
			}
		}
		
		System.out.println("[ERROR] Authorizaion failed");
		res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		return;
		
	}

	public void init(FilterConfig conf) throws ServletException {
			this.filterConfig = conf;
	}
}
