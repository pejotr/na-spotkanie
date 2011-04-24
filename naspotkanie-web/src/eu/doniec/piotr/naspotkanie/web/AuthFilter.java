package eu.doniec.piotr.naspotkanie.web;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.datanucleus.util.StringUtils;

public class AuthFilter implements Filter {

	protected FilterConfig filterConfig;
	
	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;

		final String authHeader = req.getHeader( "Authorization" );
				
		if( authHeader != null ) {
			StringTokenizer st = new StringTokenizer(authHeader);
			
			if(st.hasMoreTokens()) {
				String basicAuth = st.nextToken();
				
				if(basicAuth.equalsIgnoreCase(HttpServletRequest.BASIC_AUTH)) {
					byte[] authData = Base64.decodeBase64(st.nextToken());
					final String[] credentials = StringUtils.split(new String(authData), ":");
					
					if(credentials.length == 2 && credentials[0].equals("admin") && credentials[1].equals("mypass") ) {
						chain.doFilter(request, response);
						return;
					}
				}	
			}	
		}
		
		res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		return;
		
	}

	public void init(FilterConfig conf) throws ServletException {
			this.filterConfig = conf;
	}
}
