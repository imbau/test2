<?php
    include("Mydbconnect.php");
	include("connectInfo.php");
	$arrangetime = $_GET['time'];
	$arrangedate = $_GET['date'];
	$mode = $_GET['mode'];
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta name="generator" content="HTML Tidy for HTML5 (experimental) for Windows https://github.com/w3c/tidy-html5/tree/c63cc39" />
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />  
  <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
  <link href="bootstrap.min.css" rel="stylesheet" />
  <link rel="stylesheet" href="http://blueimp.github.com/cdn/css/bootstrap-responsive.min.css" />
  <link rel="stylesheet" href="http://blueimp.github.com/Bootstrap-Image-Gallery/css/bootstrap-image-gallery.min.css" />
   <link rel="stylesheet" href="css/darkwash.css" media="screen" />
  <script type="text/javascript" src="http://code.jquery.com/jquery-1.8.2.js"></script>
  <script type="text/javascript" src="http://code.jquery.com/ui/1.9.0/jquery-ui.js"></script>
 
  <style type="text/css">
        body {
        	font-size: 90%;
        	color:#000000;
        	background: url(images/bk.jpg) no-repeat ;
        	background-attachment: fixed;
        	font-family:Eras Light ITC, Helvetica, sans-serif;
        	font-weight: bold;
                }        

  </style>
  <script type="text/javascript">
	var arrangedate = '<?php echo $arrangedate; ?>';
	var arrangetime = '<?php echo $arrangetime; ?>';
	var mode = '<?php echo $mode; ?>';
	searchreq();
	 function searchreq()
	{
	     var area= $("select#searcharea").val();		
		 var request = $.ajax
		 ( {
			url: "searchreq.php?date="+arrangedate+"&time="+arrangetime+"&mode="+mode+"&area="+area,
			type: "POST",
			data: {},
			dataType: "html"
			});
			request.done(function( msg )
			{
			   var table = document.getElementById("reqtable");
			   while(table.rows.length > 1)
               {
                    table.deleteRow(table.rows.length - 1);
                }
			   var jsonobj=eval('('+msg+')');  
			   for(var index=1;index<=jsonobj.count;index++)
				{
					var tObj = table.tBodies[0];
                    var row = document.createElement("tr");
                    var cell = document.createElement("td");
					cell.innerHTML = "<center>" + (index) + "<\/center>";
					row.appendChild(cell);
					 for(var line=1;line<=10;line++)
					{
					   var msg="";
					     switch(line)
						 {
						 case 1:
							msg=eval("jsonobj.name"+index);
							break;
						 case 2:
						 	msg=eval("jsonobj.staus"+index);
							break;
						 case 3:
						  	msg=eval("jsonobj.account"+index);
							break;
						 case 4:
						 msg=eval("jsonobj.Period"+index);
							 break;
						 case 5:
						 msg=eval("jsonobj.time"+index);
							 break;
						 case 6:
						 msg=eval("jsonobj.cartype"+index);
							 break;
						 case 7:
						 msg=eval("jsonobj.startaera"+index);
							 break;
						 case 8:
						 msg=eval("jsonobj.startaddress"+index);
							 break;
						 case 9:
						 msg=eval("jsonobj.endtaera"+index);
							 break;
						 case 10:
						 msg=eval("jsonobj.endaddress"+index);
							 break;
						 }
						cell = document.createElement("td");
						cell.innerHTML = "<center>"+msg+ "<\/center>";      
						row.appendChild(cell);
					}
					tObj.appendChild(row);
				//  alert(eval("jsonobj.staus"+index));
				}					
			});
			request.fail(function( jqXHR, textStatus ) 
			{
				alert( "Request failed: " + textStatus );
			});
	}
  </script>
</head>
<body>
  <div class="container">    
    <form>
      <center>
	
	   <?php
	   if($mode==2)
			echo " <h1>候補未排入名單</h1>	";
	   else
			echo " <h1>未排入名單</h1>	";
		?>
		<h1> 排班日期:<?php echo $arrangedate; ?></h1>	
	   <h1>排班時間:<?php    echo $arrangetime ; ?></h1>		  
		<label for="searcharea">搜尋區域</label><select name="searcharea" id="searcharea">
           <option value="-1">
                   請選擇
                 </option>
                 <?
				  $area= mysql_query("SELECT * FROM area ORDER BY Area  DESC ");
				   while($arearow = mysql_fetch_array($area))
                   {
				   echo "<option value='".$arearow['Area']."'>";
				   echo $arearow['Area'];
				    echo "</option>";
				   }
				   ?>
              </select>
			<a  href='#'   class='btn btn-inverse'  onclick=searchreq() > 確定</a>&nbsp;	
      </center>    
     <?php
	$rs2 = mysql_query("SELECT * FROM progress WHERE `index`='4' and `date`='".$arrangedate."' and `time`='".$arrangetime."'");	
	$row2=mysql_fetch_array($rs2);
	if($mode==2)
		if($row2['percent']==100)
			$flag=1;
			else
			$flag=0;
	else
		$flag=1;
	
	if($flag==1)
	{

     echo "<center><table border=\"1\" cellspacing='1' id='reqtable'>";
     echo "<tr><th>流水號</th><th>姓名</th><th>狀態</th><th>帳號</th><th>時段</th><th>旅行時間</th><th>指定車種</th><th>上車區域</th><th>上車地址</th><th>下車區域</th><th>下車地址</th></tr>";
   
	 echo "</table></center>";
	}else
	{
		echo " <br><br><br><br><center><h1>目前尚未完成排班</h1></center>";
	}
?>
</form>
    </div>
</body>
</html>
