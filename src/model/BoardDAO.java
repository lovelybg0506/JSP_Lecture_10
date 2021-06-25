package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDAO {
	ResultSet rs; // 데이터베이스 테이블의 결과를 리턴받아 저장해주는 객체
	Connection conn; // 데이터베이스에 접근할 수 있도록 설정
	PreparedStatement pstmt; // 데이터베이스에서 쿼리를 실행시켜 주는 객체
	
	// 데이터베이스의 커넥션풀을 사용하도록 설정하는 메소드
	public void getCon() {
	/*	
		try {
			String dburl="jdbc:oracle:thin:@localhost:1521:xe";
			String dbID="scott";
			String dbPassword="tiger";
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn=DriverManager.getConnection(dburl,dbID,dbPassword);
		}catch(Exception e) {
			e.printStackTrace();
		}
	*/
		
		/*
		  	"jdbc:oracle:thin ==> 드라이버 레지스트리에 등록된 Oracle JDBC Driver의 명칭
		 	localhost ==> DatabBase가 설치된 PC
		 	1521 ==> Database가 점유하고 있는 포트번호
		 	xe ==> Database의 식별자		  
		*/
		
		try {
				Context initctx=new InitialContext();
				Context envctx=(Context)initctx.lookup("java:comp/env");
				DataSource ds=(DataSource)envctx.lookup("jdbc/Oracle11g");
				conn=ds.getConnection();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
		// 하나의 새로운 게시글이 넘어와서 저장되는 메소드
	public void insertBoard(BoardBean bean) {
		getCon();
		// 빈 클래스에 넘어오지 않았던 데이터들을 초기화 해주어야 한다
		int ref=0; // 글 그룹을 의미=쿼리를 실행시켜 가장 큰 ref값을 가져온 후 +1을 더해준다
		int re_step=1; // 새 글이기에 = 부모 글이기에
		int re_level=1;
		
		try {
			// 가장 큰 ref값을 읽어오는 쿼리 준비
			String refsql="select max(ref) from board";
			// 쿼리 실행 객체
			pstmt=conn.prepareStatement(refsql);
			// 쿼리를 실행 후 결과를 리턴
			rs=pstmt.executeQuery();
			if(rs.next()) { // 결과 값이 있다면
				ref=rs.getInt(1)+1; // 최대값에 +1을 더해서 글 그룹을 설정
			}
			String sql="insert into board values(board_seq.nextval,?,?,?,?,sysdate,?,?,?,0,?)";
			pstmt=conn.prepareStatement(sql);
			
			// ?의 값을 맵핑
			pstmt.setString(1, bean.getWrite());
			pstmt.setString(2, bean.getEmail());
			pstmt.setString(3, bean.getSubject());
			pstmt.setString(4, bean.getPassword());
			pstmt.setInt(5, ref);
			pstmt.setInt(6, re_step);
			pstmt.setInt(7, re_level);
			pstmt.setString(8, bean.getContent());
			// 쿼리를 실행
			pstmt.executeUpdate();
			// 자원 반납
			conn.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	
	}
	
	// 모든 게시글을 리턴해 주는 메소드
	public Vector<BoardBean> getAllBoard(int start,int end){ // 이전 이후 카운터
		// 리턴할 객체 선언
		Vector<BoardBean> v=new Vector<>();
		getCon();
		
		try {
			// 쿼리 준비
			String sql="select * from (select A.*,Rownum Rnum from(select * from board order by ref desc,re_step asc)A)"
						+"where Rnum >= ? and Rnum <= ?";
			// String sql="select * from board order by ref desc,re_step asc";
			// 쿼리 실행 객체
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, start);
			pstmt.setInt(2, end); // 10개 기준
			
			// 쿼리를 실행 후 결과를 저장
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				BoardBean bean=new BoardBean();
				bean.setNum(rs.getInt(1));
				bean.setWrite(rs.getString(2));
				bean.setEmail(rs.getString(3));
				bean.setSubject(rs.getString(4));
				bean.setPassword(rs.getString(5));
				bean.setReg_date(rs.getString(6).toString());
				bean.setRef(rs.getInt(7));
				bean.setRe_step(rs.getInt(8));
				bean.setRe_level(rs.getInt(9));
				bean.setReadcount(rs.getInt(10));
				bean.setContent(rs.getString(11));
				
				v.add(bean); // 벡터에 저장
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return v;
	}
	
	// BoardInfo일 때 : 하나의 게시글을 리턴하는 메소드
	public BoardBean getOneBoard(int num) {
		// 리턴타입 선언
		BoardBean bean=new BoardBean();
		getCon();
		
		try {
			// 조회수 증가 쿼리
			String readsql="update board set readcount=readcount+1 where num=?";
			pstmt=conn.prepareStatement(readsql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
			
			// 쿼리 준비
			String sql = "select * from board where num=?";
			
			// 쿼리 실행 객체
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			// 쿼리 실행후 결과를 리턴
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				bean.setNum(rs.getInt(1));
				bean.setWrite(rs.getString(2));
				bean.setEmail(rs.getString(3));
				bean.setSubject(rs.getString(4));
				bean.setPassword(rs.getString(5));
				bean.setReg_date(rs.getString(6).toString());
				bean.setRef(rs.getInt(7));
				bean.setRe_step(rs.getInt(8));
				bean.setRe_level(rs.getInt(9));
				bean.setReadcount(rs.getInt(10));
				bean.setContent(rs.getString(11));
			}
			conn.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return bean;
	}
	
	// 답변글이 저장되는 메소드
	public void reWriteBoard(BoardBean bean) {
		// 부모글 그룹과 글레벨 글스텝 읽어옴
		int ref=bean.getRef();
		int re_step=bean.getRe_step();
		int re_level=bean.getRe_level();
		
		getCon();
		
		try {
			// 핵심 코드
			// 부모글 보다 큰 re_level의 값을 전부 1씩 증가시켜줌
			String levelsql="update board set re_level=re_level+1 where ref=? and re_level > ?";
			// 쿼리 삽입 객체 선언
			pstmt=conn.prepareStatement(levelsql);
			pstmt.setInt(1, ref);
			pstmt.setInt(2, re_level);
			// 쿼리 실행
			pstmt.executeUpdate();
			// 답변글 데이터를 저장
			String sql="insert into board values(board_seq.nextval,?,?,?,?,sysdate,?,?,?,0,?)";
			pstmt=conn.prepareStatement(sql);
			// ?에 값을 대입
			pstmt.setString(1, bean.getWrite());
			pstmt.setString(2, bean.getEmail());
			pstmt.setString(3, bean.getSubject());
			pstmt.setString(4, bean.getPassword());
			pstmt.setInt(5, ref); // 부모의 ref 값을 넣어줌
			pstmt.setInt(6, re_step); // 답글이기에 부모글 re_step에 1을 넣어줌
			pstmt.setInt(7, re_level+1);
			pstmt.setString(8, bean.getContent());
			
			// 쿼리를 실행
			pstmt.executeUpdate();
			conn.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
		
		// Boardupdate용 하나의 게시글을 리턴
		// Boardupdate용 delete시 하나의 게시글을 리턴
		public BoardBean getOneUpdateBoard(int num) {
			// 리턴타입 선언
			BoardBean bean=new BoardBean();
			getCon();
			
			try {
				// 쿼리 준비
				String sql="select * from board where num=?";
				// 쿼리 실행 객체
				pstmt=conn.prepareStatement(sql);
				pstmt.setInt(1, num);
				// 쿼리 실행 후 결과를 리턴
				rs=pstmt.executeQuery();
				
				if(rs.next()) {
					bean.setNum(rs.getInt(1));
					bean.setWrite(rs.getString(2));
					bean.setEmail(rs.getString(3));
					bean.setSubject(rs.getString(4));
					bean.setPassword(rs.getString(5));
					bean.setReg_date(rs.getString(6).toString());
					bean.setRef(rs.getInt(7));
					bean.setRe_step(rs.getInt(8));
					bean.setRe_level(rs.getInt(9));
					bean.setReadcount(rs.getInt(10));
					bean.setContent(rs.getString(11));
				}
				conn.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			return bean;
		}
		
		// update와 delete시 필요한 패스워드 값을 리턴해 주는 메소드
		public String getPass(int num) {
			// 리턴할 변수 객체 선언
			String pass="";
			getCon();
			
			try {
				// 쿼리 준비
				String sql="select password from board where num=?";
				// 쿼리를 실행할 객체 선언
				pstmt=conn.prepareStatement(sql);
				pstmt.setInt(1, num);
				// 쿼리 실행 후 결과를 리턴
				rs=pstmt.executeQuery();
				// 패스워드 값을 저장
				if(rs.next()) {
					pass=rs.getString(1);
				}
				// rs.getString() - getString함수는 해당 순서의 열에 있는 데이터를 String형으로 받아온다
				// 예를 들어 rs.getString(2)를 하게되면 2번째 열에 있는 데이터를 가져오게 된다.
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			return pass;
		}
		
		// 하나의 게시글을 수정하는 메소드
		public void updateBoard(BoardBean bean) {
			getCon();
			
			try {
				// 쿼리준비
				String sql="update board set subject=?, content=? where num=?";
				
				// 쿼리 실행할 객체선언
				pstmt=conn.prepareStatement(sql);
				
				pstmt.setString(1, bean.getSubject());
				pstmt.setString(2, bean.getContent());
				pstmt.setInt(3, bean.getNum());
			
				// 쿼리 실행 후 결과를 리턴
				pstmt.executeUpdate();
				
				// 자원 반납
				conn.close();
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		// 하나의 게시글을 삭제하는 메소드
		public void deleteBoard(int num) {
			getCon();
			
			try {
				// 쿼리 준비
				String sql="delete from board where num=?";
				// 쿼리 실행할 객체 선언
				pstmt=conn.prepareStatement(sql);
				// ?
				pstmt.setInt(1, num);
				// 쿼리 실행
				pstmt.executeUpdate();
				// 자원 반납
				conn.close();
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		// 전체 글의 갯수를 리턴하는 메소드
		public int getAllCount() {
			getCon();
			// 게시글 전체 수를 저장하는 변수
			int count=0;
			
		try {
			// 쿼리 준비
			String sql ="select count(*) from board";
			// 쿼리 실행할 객체 선언
			pstmt=conn.prepareStatement(sql);
			// 쿼리 실행후 결과 리턴
			rs=pstmt.executeQuery();
			if(rs.next()) {
				count=rs.getInt(1);
			}
			// 자원 반납
			conn.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return count;
	}

}














