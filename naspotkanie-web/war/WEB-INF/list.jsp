<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="eu.doniec.piotr.naspotkanie.entity.User" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Listing userow :)</title>
</head>
<body>

<table>
<tr><th>Id</th><th>Email</th><th>RegisterDate</th><th>Lat</th><th>Lgt</th></tr>
<%
	List<User> usersList = (List<User>)request.getAttribute("users_list");
	if( usersList == null ) {
%>
	<h1>No Users in DB</h1>		
<%		
	}
	else
	{
	for (User u : usersList) {
%>

<tr><td><%= u.getId() %></td><td><%= u.getEmail() %></td><td><%= u.getPasswordHash() %></td><td><%= u.getLatitude() %></td><td><%= u.getLongitude() %></td></tr>

<%
	}
	}
%>
</table>

</body>
</html>