package eu.doniec.piotr.naspotkanie.web;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.*;

public class AuthFilter implements Filter {

	protected FilterConfig filterConfig;
	
	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;

		String authType = req.getAuthType();
		
		Enumeration names = req.getHeaderNames();
	    while (names.hasMoreElements()) {
	      String name = (String) names.nextElement();
	      Enumeration values = req.getHeaders(name); // support multiple values
	      if (values != null) {
	        while (values.hasMoreElements()) {
	          String value = (String) values.nextElement();
	          System.out.println(name + ": " + value);
	        }
	      }
	    }
		
		System.out.println("authtype " + authType);
		
		if( authType != null && authType.equals(HttpServletRequest.BASIC_AUTH) ) {
			String authHeader = req.getHeader("Authorization");
			String authParts[] = authHeader.split(":");
			
			if( authParts[0].equals("admin") && authParts[1].equals("pass") ) {
				chain.doFilter(request, response);
			} else {
				res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;				
			}
		} else {
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
	}

	public void init(FilterConfig conf) throws ServletException {
			this.filterConfig = conf;
	}
}
