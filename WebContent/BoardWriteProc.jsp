<%@page import="model.BoardDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>BoardWriterProc</title>
</head>
<body>
<%
	request.setCharacterEncoding("utf-8");
%>

<jsp:useBean id="boardBean" class="model.BoardBean">
	<jsp:setProperty name="boardBean" property="*" />
</jsp:useBean>

<%
	BoardDAO bdao=new BoardDAO();
	bdao.insertBoard(boardBean);
	
	// 게시글 저장후 전체 게시글 보기
	response.sendRedirect("BoardList_1.jsp");
%>
</body>
</html>