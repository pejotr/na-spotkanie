package eu.doniec.piotr.naspotkanie.util;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.datanucleus.util.StringUtils;

public class Authorization {

	public static String[] parseAuthHeader(String authHeader) {
		
		if( authHeader != null ) {
			StringTokenizer st = new StringTokenizer(authHeader);
			
			if(st.hasMoreTokens()) {
				String basicAuth = st.nextToken();
				
				if(basicAuth.equalsIgnoreCase(HttpServletRequest.BASIC_AUTH)) {
					byte[] authData = Base64.decodeBase64(st.nextToken());
					final String[] credentials = StringUtils.split(new String(authData), ":");
					return credentials;
				}	
			}	
		}
		
		return null;
		
	}
	
}
