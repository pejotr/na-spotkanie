package eu.doniec.piotr.naspotkanie.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import eu.doniec.piotr.naspotkanie.entity.Share;
import eu.doniec.piotr.naspotkanie.entity.User;
import eu.doniec.piotr.naspotkanie.web.dao.ShareDAO;
import eu.doniec.piotr.naspotkanie.web.dao.UserDAO;

@SuppressWarnings("serial")
public class PositionRequest extends HttpServlet  {

	/*
	 * Reqest message to serer for last known positions
	 * of specified users 
	 */
	static class AttendeesPositionReqMessage {
		String email;
		String[] emails;
		
		public AttendeesPositionReqMessage() {
			// TODO Auto-generated constructor stub
		}
	}
	
	static class AttendeesPositionRespMessage {
		String[] emails;
		double[] lgt;
		double[] lat;
		
		public AttendeesPositionRespMessage() {
			// TODO Auto-generated constructor stub
		}
		
		public void append(int pos, String email, double lgt, double lat) {
			this.emails[pos] = email;
			this.lgt[pos] = lgt;
			this.lat[pos] = lat;
		}
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException {

		Gson gson = new Gson();
		BufferedReader reader = req.getReader();
		String json = reader.readLine();
		AttendeesPositionReqMessage posreq = gson.fromJson(json, AttendeesPositionReqMessage.class);
		
		System.out.println("[DEBUG] PositionLogServlet: Read JSON [#req<" + json + ">]");
		
		UserDAO udao = new UserDAO();
		ShareDAO sdao = new ShareDAO();
		User owner = udao.getUser(posreq.email);
		List<Share> shares = sdao.getUserFriendShares(owner.getId());
		List<User> users  = udao.getUsersOnList(shares);
		
		AttendeesPositionRespMessage respmsg = new AttendeesPositionRespMessage();
		respmsg.emails = new String[users.size()];
		respmsg.lgt = new double[users.size()];
		respmsg.lat = new double[users.size()];
		
		int cnt = 0;
		for(User u : users) {
			respmsg.append(cnt, u.getEmail(), u.getLongitude(), u.getLatitude());
			cnt++;
		}
		
		PrintWriter out = resp.getWriter();
		resp.setContentType("application/json");
		
		String jsonResp = gson.toJson(respmsg);
		out.println(jsonResp.toString());
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		doGet(req, resp);

	}

	
}
