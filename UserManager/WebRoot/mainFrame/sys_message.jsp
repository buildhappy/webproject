<br><%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.io.*" %>

<%@ page language="java"%> 
<%@ page import="java.util.*"%> 
<%@ page import="java.text.*"%> 
<%@ page import="java.math.*"%> 

<% 
//String datetime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()); //获取系统时间 
%>
<jsp:useBean id="bean" class="com.cn.BeanWh.BeanWh"></jsp:useBean>
<jsp:useBean id="read" class="com.cn.SysRead"></jsp:useBean>

<html>
	<head>
		<script type="text/javascript">
		var xmlhttp;  
		setInterval("loadXMLDoc()",1000); //每格1秒刷新一次
		function loadXMLDoc(){
		   if(window.XMLHttpRequest){
		      xmlhttp=new XMLHttpRequest();
		   } else {
		      xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		   }
						
			xmlhttp.open("GET","../test.txt",true);
			xmlhttp.onreadystatechange=function(){
				if(xmlhttp.readyState==4 && xmlhttp.status==200){
					var str=xmlhttp.responseText;
					var cpu=str.split(':')[1].split("memory")[0];
					var mem=str.split(':')[2].split("storage")[0];
					var sto=str.split(':')[3].split("network")[0];
					var net=str.split(':')[4].split("starTime")[0];
					var stime=str.split("starTime:")[1];
					document.getElementById("cpu").value=cpu;
			        document.getElementById("mem").value=mem;
			        document.getElementById("sto").value=sto;
			        document.getElementById("net").value=net;
			        
			        var d = new Date();
					var year = d.getUTCFullYear();
					var month = d.getMonth()+1;
					var date = d.getDate();
					var hour = d.getHours();
					var minute = d.getMinutes();
					var second = d.getSeconds();
					
					var syear = stime.split("-")[0];
					var smonth = stime.split("-")[1];
					var sday = stime.split("-")[2].split(" ")[0];
					var shour = stime.split(" ")[1].split(":")[0];
					var sminute = stime.split(" ")[1].split(":")[1];
					var ssecond = stime.split(" ")[1].split(":")[2];

					//var currentTime = year+"-"+month+"-"+date +" "+hour + ":" + minute + ":" + second;
					
					
					//new Date().convertDate("2011-04-08");
					var currentTime = new Date(year,month,date,hour,minute,second);
					var starTime = new Date(syear,smonth,sday,shour,sminute,ssecond);
					var runSecond = ((currentTime - starTime)/1000).toString();
					var runDay = Math.floor(runSecond/86400);
					var runHour = Math.floor((runSecond%86400)/3600);
					var runMinute = Math.floor(((runSecond%86400)%3600)/60);
					var Second = ((runSecond%86400)%3600)%60;
					var runTime = runDay + "天"+runHour+"小时"+runMinute+"分钟"+Second+"秒";
					//var d2 = new Date().format("yyyy-MM-dd hh:mm:ss");
					//var myTime = d1.dateDiff(d2, 'd');
					
                    
					//var between=(d2.getTime()-d1.getTime())/1000;//除以1000是为了转换成秒
					//var day1=String.valueOf(between/(24*3600));
					//var hour1=String.valueOf(between%(24*3600)/3600);
					//var minute1=String.valueOf(between%3600/60);
					//var second1=String.valueOf(between%60/60);
					//var myTime = day1+"天"+hour1+"小时"+minute1+"分"+second1+"秒";  
					
			        document.getElementById("myT").value = runTime;
			        
			        //var datatime = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
					//var datatime = "erbi";
					//document.getElementById("myT").value=datatime;
					
				}else{
					document.getElementById("cpu").value="error";
					document.getElementById("mem").value="error";
					document.getElementById("sto").value="error";
					document.getElementById("net").value="error";
					document.getElementById("myT").value="error";
				}
			}
			xmlhttp.send();
		}
		</script>   
    
		<title>kangle(2.8.3) 信息</title>
		<LINK href=kangle.css type='text/css' rel=stylesheet>
		<%application.setAttribute("now", 0);%>
	</head>
	<body>
		<table width='98%'>
			<tr>
				<td>
					<table>
						<tr>
							<td>代理IP/URL:</td>
							<td>
							<input type=text name="pro_IP" value=<%
								String address = bean.infoAdd();
								if(address.length() <= 3){
									address = "未配置";
								}
							%><%=address%>></td>
						</tr>
						<tr>
							<td>运行时间 :</td>
							<td>
								<input type=text name="myTime" id="myT" value="0">
							</td>
						</tr>
					</table>
					</br>
					<table>
						<tr>
							<td>CPU利用率:</td>
							<td><input type=text name="cupuse" id="cpu" value=0>%</td>
							<td> <br> </td>
							<td>内存:</td>
							<td><input type=text name="inMemory" id="mem" value=0> M</td>
						</tr>
						<tr>
							<td>网络流量 :</td>
							<td><input type=text name="flowRate" id="net" value=0>M</td>
							<td> <br> </td>
							<td>存储:</td>
							<td><input type=text name="storage" id="sto" value=0> M</td>
						</tr>
					</table>
				</td>
				
			</tr>
		</table>

		
		<!--  <hr>
		<center>
			Powered  &nbsp;by &nbsp;
			<a href='http://www.int.bupt.cn/' target='_blank'>BUPT518</a>
			, &nbsp;(c) 2013 &nbsp;&nbsp;北京邮电大学 
		</center>
		<script language='javascript' src='http://www.kanglesoft.com/version_note.php?version=2.8.3&type=free&lang=zh_CN'></script>-->
	</body>
</html>