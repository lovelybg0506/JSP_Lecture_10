<%@page import="model.BoardDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>BoardReWriteProc</title>
</head>
<body>
<%
	request.setCharacterEncoding("utf-8");
%>

<!-- 데이터를 한번에 받아오는 빈클래스 사용 -->	
<jsp:useBean id="boardbean" class="model.BoardBean">
	<jsp:setProperty name="boardbean" property="*"/>
</jsp:useBean>

<%
	BoardDAO bdao=new BoardDAO();
	bdao.reWriteBoard(boardbean);
	
	response.sendRedirect("BoardList.jsp");
%>
</body>
</html>