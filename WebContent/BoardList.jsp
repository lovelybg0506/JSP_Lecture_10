<%@page import="model.BoardBean"%>
<%@page import="java.util.Vector"%>
<%@page import="model.BoardDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>BoardList</title>
</head>
<body>
<div align="center">
	<h2>전체 게시글 보기</h2>
	<%
		int pageSize=10; // 화면에 보여질 게시글의 개수를 지정
		
		// 현재 카운터를 클릭한 번호 값을 읽어옴
		String pageNum=request.getParameter("pageNum");
		
		// 만약 처음 boardList.jsp를 클릭하거나 수정 삭제등 다른 게시글에서 이 페이지로 넘어오면
		// pageNum값이 없기에 null값 처리
		if(pageNum == null){
			pageNum="1";
		}
		
		int count=0; // 전체 글의 갯수를 저장하는 변수
		int number=0; // 페이지 넘버링 변수
		int currentPage=Integer.parseInt(pageNum); // 현재 보고자 하는 페이지 숫자를 지정
		
		BoardDAO bdao=new BoardDAO();
		
		count=bdao.getAllCount();
		
		// 현재 페이지에 보여줄 시작 번호를 설정=데이터 베이스에서 불러올 시작 번호
		int startRow=(currentPage-1)*pageSize+1;
		int endRow=currentPage*pageSize;
		
		// 최신글 10개를 기준으로 게시글을 리턴 받아주는 메소드 호출
		Vector<BoardBean> vc=bdao.getAllBoard(startRow, endRow);
		
		// 테이블에 표시할 번호 지정.
		number=count-(currentPage-1)*pageSize;
		
	%>
</div>
</body>
</html>