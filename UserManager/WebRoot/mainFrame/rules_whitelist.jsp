<%@ page language="java" import="java.util.*" pageEncoding="GBK"%>
<html> 
	<head> 
	<meta http-equiv="Content-Type" content="text/html; charset=GBK" />
	<script type="text/javascript"> 

		function insertRow() //add a row in the table when click the "add" button;
		{ 
			var tab = document.getElementById("myTable");
			var len = tab.rows.length;
			var tr=tab.insertRow(len);//insert into the last row		
			var td0=tr.insertCell(0); 
			var td1=tr.insertCell(1); 
			var cellValue = document.getElementById("cell2").value;			
            var input = document.createElement("input");   
            input.id=len;  
            input.name="url";
            input.type="checkbox";
            input.value=cellValue;
            td0.appendChild(input);           
			td1.innerHTML=document.getElementById("cell2").value;
			tab.rows[len].cells[1].align="middle";//change cells[1]'s property;
			tab.rows[len].cells[0].align="middle";
            var td2=tr.insertCell(2);  
            var input = document.createElement("input");   
            input.id="hidden";  
            input.name="hidden1";
            input.type="hidden";
            input.value=cellValue;
            td2.appendChild(input);
		} 		
		function ChkAllClick(sonName, cbAllId){//select all checkboxs;
			 var arrSon = document.getElementsByName(sonName);
			 var cbAll = document.getElementById(cbAllId);
			 //alert(arrSon.length);
			 var tempState=cbAll.checked;
			 for( var i=0;i<arrSon.length;i++) {
			  if(arrSon[i].checked!=tempState)
			           arrSon[i].click();
			 }
			}
		
		function delRow(){ //this function is for deleting the selected rows
			var tab = document.getElementById("myTable");
			//var len = tab.rows.length;
			var ruleArrays = document.getElementsByName("url");
			var j=0;
			for( var i=0;i<ruleArrays.length;i++) {
				 if(ruleArrays[i].checked==false){
					 j++;//the flag variable
				 }
			}
			if(j==ruleArrays.length){
				alert("请选择要删除的行！");
			}else{
				delRowSon();//perform when the user select at least one checkbox
			}			
		}
		function delRowSon(){ //This method is of low efficiency,you can try to improve it!		
			var tab = document.getElementById("myTable");
			 var ruleArrays = document.getElementsByName("url");//read all url from this page;
			 for( var i=0;i<ruleArrays.length;i++) {				 
				 dele();
				 delRowSon();
			 }
		} 		
		 function dele(){
			 var tab = document.getElementById("myTable");
			 var ruleArrays = document.getElementsByName("url");
			 for( var i=0;i<ruleArrays.length;i++) {
				 //alert(i);
				 if(ruleArrays[i].checked==true){
					 tab.deleteRow(i+1);
					 break;
				 }
		     }
		 } 
		
		 function readInsertInfo(){	//Save the last submitted content for 15mins           				 
		     <%
		        String  inRule[] = (String [])session.getAttribute("arr");//get value from session;
		        if(inRule !=null){//如果数据不为空的时候继续操作；
		          for(int i=0;i<inRule.length;i++){
		        	 String s=inRule[i];
		     %>
		          var name = "<%= s%>";
		          var number = "<%=i %>";
		          if(name!=null){
					addRow(name); //the definition of this function is below;
		          }
		      <%  
		         }
		         }
		      %>			 
		 }
		 
		 function addRow(name){
			 var tab = document.getElementById("myTable");
			// alert("这是函数中的name");
			 var len = tab.rows.length;
				var tr=tab.insertRow(len);					
				var td0=tr.insertCell(0); 
				var td1=tr.insertCell(1); 			
				td0.innerHTML="<input type=\"checkbox\" name=\"url\" value=\"check\">";
				td1.innerHTML=name;
				//alert(name);
				tab.rows[len].cells[0].align="middle";
				tab.rows[len].cells[1].align="middle";//change cells[1] property;	
				//alert(name);
		        var td2=tr.insertCell(2);  
		        var input = document.createElement("input");   
	            input.id="hidden";  
	            input.name="hidden1";
	            input.type="hidden";
	            input.value = name;
	            td2.appendChild(input);
		 }
		 

	</script> 
	</head> 
	<body onload="readInsertInfo()"> 
		<form  name="white" id="f2" action="../control/white_rules_con.jsp" method="post">
			<table id="myTable" border="1" width="80%" name="table" > 
				<tr> 
					<td width="4%" align="center"><input type="checkbox" name="CHKALL" id="chkAll" title="全选" onClick="ChkAllClick('url','chkAll')"></td> 
					<td align="center" width="98%">URL列表</td>
				</tr> 
				<tr > 
					<td align="center"><input type="checkbox" name="url" value="xss_attack" id="1"></td> 
					<td align="center">model1:http://www.bupt.edu.cn/content/index.html</td> 
				</tr> 
				<tr> 
					<td align="center"><input type="checkbox" name="url" value="xss_attack" id="2"></td> 
					<td align="center">model2:http://www.bupt.edu.cn/content.php?p=30/</td>
				</tr> 
			</table> 
			<p> 
		    	请您输入URL:<input type="text" id= "cell2" size="29" value="按照model中的格式在此输入URL" onclick="if(value==defaultValue){value='';this.style.color='#000'}" onBlur="if(!value){value=defaultValue;this.style.color='#999'}" style="color:#999"/>  
			</p> 
			<input type="button" onclick="insertRow()" value="添加"> 
			<input type="button" onclick="delRow()" value="删除"> 
			<input type="submit" onclick="getURLInfo()" value="提交"> 
		</form>		
	</body> 
</html> 