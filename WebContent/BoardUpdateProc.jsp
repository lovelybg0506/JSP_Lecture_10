<%@page import="model.BoardDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>BoardUpdateProc</title>
</head>
<body>
<%
	request.setCharacterEncoding("utf-8");
%>

	<jsp:useBean id="boardbean" class="model.BoardBean">
	<jsp:setProperty name="boardbean" property="*"/>
	</jsp:useBean>
	
<%
	BoardDAO bdao=new BoardDAO();
	
	// 해당 게시글의 패스워드 값을 얻어옴
    String pass=bdao.getPass(boardbean.getNum());
	
	if(pass.equals(boardbean.getPassword())){
		bdao.updateBoard(boardbean);
		response.sendRedirect("BoardList.jsp");
	}else{
%>	
	<script type="text/javascript">
		alert("패스워드가 일치하지 않습니다. 다시 확인 후 수정해 주세요");
		history.go(-1);
	</script>
<%
	}
%>
</body>
</html>













