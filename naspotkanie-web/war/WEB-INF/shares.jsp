<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="eu.doniec.piotr.naspotkanie.entity.Share" %>
<%@ page import="eu.doniec.piotr.naspotkanie.web.dao.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<table>
<tr><th>Id</th><th>Id1</th><th>Id2</th></tr>
<%
	ShareDAO dao = new ShareDAO();
	List<Share> shareList = dao.getAllShares();

	if( shareList == null ) {
%>
	<h1>No shares !!!</h1>
<%
	} else {
		for(Share s : shareList) {	

%>
			<tr><td><%= s.getId()  %></td><td><%= s.getId1() %></td><td><%= s.getId2() %></td></tr>
<%
		}
	}
%>
</table>
</body>
</html>