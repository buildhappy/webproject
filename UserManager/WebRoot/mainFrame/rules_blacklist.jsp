<%@ page language="java" import="java.util.*" pageEncoding="GBK"%>
<jsp:useBean id="bean" class="com.cn.BeanWh.BeanWh"></jsp:useBean>

<html>
	<head>
	<title>管理界面</title><LINK href='left.css' type='text/css' rel='stylesheet'></LINK>
	<meta http-equiv="refresh"content="10;url=rules_blacklist.jsp">
	<script type="text/javascript">	
	    window.onload = readXML();	
		function readXML(){ //load config.xml
		  if (window.XMLHttpRequest)
		   {// code for IE7+, Firefox, Chrome, Opera, Safari
			  xmlhttp=new XMLHttpRequest();
		   }else{// code for IE6, IE5
			  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		   }
		   xmlhttp.open("GET","config.xml",false);
		   xmlhttp.send();
		   xmlDoc=xmlhttp.responseXML;
		   var node = xmlDoc.getElementsByTagName("black-list")[0].childNodes;			
	     }		
		function ChkAllClick(sonName, cbAllId){//select all checkboxs;
			 var arrSon = document.getElementsByName(sonName);
			 var cbAll = document.getElementById(cbAllId);
			 var tempState=cbAll.checked;
			 for( var i=0;i<arrSon.length;i++) {
			  if(arrSon[i].checked!=tempState)
			           arrSon[i].click();
			 }
			}		
	</script>
	</head>
	<body  marginheight="20" >
		<form name="f1" id="f1" action="../control/black_rules_con.jsp" method="post">
			<table border="1" width="100%" align="center">
				<tr>
					<td width="2.5%"><input name="CHKALL" id="chkAll" title="全选" onClick="ChkAllClick('rules','chkAll')" type="checkbox" /></td>
					<td width="8%">规则</td>
					<td>描述</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="rules" value="xss_attack" id="xss"></td>
					<td>XSS_ATTACK</td>
					<td>跨站脚本攻击</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="rules" value="sql_attack" id="sql"></td>
					<td>SQL_ATTACK</td>
					<td>数据库相关攻击（主要为数据库注入攻击）</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="rules" value="inbound_attack" id="inbound"  ></td>
					<td>INBOUND</td>
					<td>边界溢出攻击</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="rules" value="outbound_attack" id="outbound" ></td>
					<td>OUTBOUND</td>
					<td>边界溢出攻击</td>
				</tr>
			</table>

			<table>
				<tr>
					<td colspan="2" align="center"><input type="submit"  value="提交" ></td>
				</tr>
			</table>
		</form>
		
	  <script>
	  readXML();
	  var node = xmlDoc.getElementsByTagName("black-list")[0].childNodes;	  
			for(var i=1;i<node.length;i++,i++){
				switch(node[i].getAttribute("name")){
					case 'xss'://其中node[i].getAttribute("name")是config.xml中name的属性；
						//alert(document.getElementById("xss_attack").checked);
						document.getElementById("xss").checked=true;
						break;
					case 'sql':
						document.getElementById("sql").checked=true;
						break;
					case 'inbound':
						document.getElementById("inbound").checked=true;
						break;
					case 'outbound':
						document.getElementById("outbound").checked=true;
						break;
					default:
						break;	
					} 
					}
	  </script>
	</body>
</html>