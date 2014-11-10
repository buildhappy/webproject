<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<jsp:useBean id='sql' class="com.cn.BeanWh.SQL"></jsp:useBean>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'SQLshow.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	
	<style type="text/css">
		table{margin: 2px 5px;}
		div{margin:0px;padding:0px;width:100%;height:90%;float:left;}
		a:link{text-decoration:none ; color:white ;}
		a:visited {text-decoration:none ; color:white;}
		a:hover {text-decoration:underline ; color:white;}
		a:active {text-decoration:none ; color:white;}
	</style>
	
</head>
<body>
<!-- the values about the sql information -->
	<%
		int tableNo = 0;
		int correntTabNo = 0;
		String correntTable = "connections";
		String[] tables = sql.getTable();
		int totalTable = tables.length;

		int pageNo = 1;

		if(request.getParameter("table") != null){
			correntTable = request.getParameter("table");
		}else{
			correntTable = "connections";
		}
		
		if(request.getParameter("page") != null){
			pageNo = Integer.parseInt(request.getParameter("page"));
		}else{
			pageNo = 1;
		}
		
		int totalPage = sql.getDataNumber(correntTable)/15 + 1;
				
		String[] columnName = sql.getCoName(correntTable); 

		
		if(pageNo <= 0){pageNo = 1;}
		if(pageNo >= totalPage){pageNo = totalPage;}
		
	%>
	
<!-- table information -->
	<script language="javascript">
		function changeTab(obj){
			window.location.href = "control/SQLshow.jsp?"+obj.value;
		}
	</script>
	<br><br>
	<%for(tableNo=0;tables[tableNo]!=null;tableNo++){;}%>
	共有 <%=tableNo %> 个表格	;
	当前表格为:<select name="table" onchange="changeTab(this)">
		<%for(tableNo=0;tableNo<totalTable && tables[tableNo] != null;tableNo++){ %>
			<%String para = "page=" + String.valueOf(pageNo) + "&table=" + tables[tableNo]; %>
			<%="<option value="%><%=para %>
			<%
			if(tables[tableNo].equalsIgnoreCase(correntTable)){ 
			%><%="selected='selceted'" %>
			<%} %>
			<%=">" %>
			<%=tables[tableNo] %>
			<%="</option>" %>
		<%} %>
	</select>
	<br>
	
<!-- page display,previous page,next page,select page -->	
	共有<%=sql.getDataNumber(correntTable) %>条数据，每页15条数据，共分为<%=totalPage%>页
	
	<%if(pageNo > 1){ %>
		<%String para = "control/SQLshow.jsp?page=" + String.valueOf(pageNo-1) + "&table=" + correntTable; %>
		<a href=<%=para %>>
			<input type="button" value="上一页">
		</a>
	<%} %>
		
	<script language="javascript">
		function changese(obj){
			window.location.href="control/SQLshow.jsp?"+obj.value;
		}
	</script>	
	第<select name="page" onchange="changese(this)">
		<%for(int selectNo=1;selectNo<=totalPage;selectNo++){
			String para = "page="+String.valueOf(selectNo)+"&table="+correntTable;%>
			<%="<option value="%>
			<%=para %>
			<%
			if(selectNo == pageNo){ 
			%><%="selected='selceted'" %>
			<%} %>
			<%=">" %>
			<%=selectNo %>
			<%="</option>" %>
		<%} %>
	</select>页
	
	<%if(pageNo < totalPage){ %>
		<%String para = "control/SQLshow.jsp?page=" + String.valueOf(pageNo+1) + "&table=" + correntTable; %>
		<a href=<%=para %>>
			<input type="button" value="下一页">
		</a>
	<%} %>

<!-- table of the SQL information -->	
<!-- 	<table border="1" align="center" width="99%">
		<colgroup>
    		<col width="5%" />
    		<col width="10%" />
    		<col width="10%" />
    		<col width="10%" />
    		<col width="10%" />
    	</colgroup>
		
		<tr>
			<%//for(int columnNo = 0;columnNo < columnName.length;columnNo++){ %>
				<td><%//=columnName[columnNo] %></td>
			<%//}%>
		</tr>
	</table>
-->

	<br><br>
	<div>
	    <table border="1" align="center" width="100%">
	    	<colgroup>
	    		<col width="5%" />
	    		<col width="10%" />
	    		<col width="10%" />
	    		<col width="10%" />
	    		<col width="10%" />
	    	</colgroup>
	    	<thead>
		    	<tr>
					<%for(int columnNo = 0;columnNo < columnName.length;columnNo++){ %>
						<td><%=columnName[columnNo] %></td>
					<%}%>
				</tr>
			</thead>
			<tbody>
		    	<%
		    		String s[][]= sql.read(pageNo,correntTable);
		    		for(int j=0;j<15;j++){
		    	%>		<tr height='2%'>
		    	<%		for(int i=0;i<columnName.length && s[j][i]!=null;i++){
		    	%>			<%="<td>"%><%=s[j][i] %><%="</td>"%>
		    	<%		} %>
		    			</tr>
		    	<%} %>
		    </tbody>		
	    </table>
    </div>
    
	<br>
</body>
</html>
