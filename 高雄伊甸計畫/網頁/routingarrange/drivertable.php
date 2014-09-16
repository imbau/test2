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
	var count ;
	 var jsondata;	 
	 searchdriver();
	 function driveralteration()
	{
		var carid="";
		 for(var i = 1; i <= count; i++)
        {
			 var choose=document.getElementById('choose'+i); 
		     if(choose.checked) 
			 { 
			   carid=carid+eval("jsondata.carid"+i)+",";
			 }
		}
		if(carid!="")
		 {
				$.get("<?php  echo $linkurlport; ?>/WebRoutingArranger/changedriver.view",
				{carid:carid,Action:mode,arrangedate:arrangedate,arrangetime:arrangetime},function(response) 
				{
				      alert(response);
					  opener.location.reload();
					  if(mode==1)
						location.reload();
					  else
						window.close();
				});
			
		}
	}
	 function searchdriver()
	{	
	     var area= $("select#searchstation").val();			
		 var request = $.ajax
		 ( {
			url: "searchdriver.php?date="+arrangedate+"&time="+arrangetime+"&area="+area+"&mode="+mode,
			type: "POST",
			data: {},
			dataType: "html"
			});
			request.done(function( msg )
			{
			   var table = document.getElementById("drivertable");
			   while(table.rows.length > 1)
               {
                    table.deleteRow(table.rows.length - 1);
                }
			   var jsonobj=eval('('+msg+')');  
			   jsondata=jsonobj;
			   count=jsonobj.count;
			   for(var index=1;index<=count;index++)
				{
					var tObj = table.tBodies[0];
                    var row = document.createElement("tr");
                    var cell = document.createElement("td");				
					 for(var line=1;line<=10;line++)
					{
					   var msg="";
					     switch(line)
						 {
						 case 1:
							msg=eval("jsonobj.station"+index);
							break;
						 case 2:
						 	msg=eval("jsonobj.callnumber"+index);
							break;
						 case 3:
						  	msg=eval("jsonobj.telephone"+index);
							break;
						 case 4:
						 msg=eval("jsonobj.drivername"+index);
							 break;
						 case 5:
						 msg=eval("jsonobj.carid"+index);
							 break;
						 case 6:
						 msg=eval("jsonobj.shift"+index);
							 break;
						 case 7:
						 msg=eval("jsonobj.cartype"+index);
							 break;
						 case 8:
						 msg=eval("jsonobj.worktime"+index);
							 break;
						 case 9:
						 msg=eval("jsonobj.address"+index);
							 break;
						 case 10:
						 msg=eval("jsonobj.parkname"+index);
							 break;
						 }
						cell = document.createElement("td");
						cell.innerHTML = "<center>"+msg+ "</center>";   
						row.appendChild(cell);
					}
					if(mode<2)
					{
						cell = document.createElement("td");
						cell.innerHTML = "<center><input type=checkbox id=choose"+index+"><\/center>";
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
	  <? 
	    if($mode==1)
		{	
			$sql=" SELECT * FROM `arrange_log` WHERE  date = '".$arrangedate."' AND time = '".$arrangetime."'"; 
			$rs1 = mysql_query($sql);
			$row = mysql_fetch_array($rs1);
			echo  "<h1>".$row['TurnoutDate']."當日出勤司機名單</h1>	";
		}
		else if($mode==0)
		{
			echo  "<h1>全部司機名單</h1>	";
		}
		else if($mode==2)
		{
			echo  "<h1>沒有休息時間的司機名單</h1>	";
		}
		if($mode<2)
		{	
			echo  "	<label for='searchstation'>選擇場站</label>	";
			echo  "	<select name='searchstation' id='searchstation'>
						<option value='-1'>
						請選擇
						</option>
						<option value='中和'>中和</option>
						<option value='新店'>新店</option>
						<option value='土城'>土城</option>
						<option value='汐止'>汐止</option>
						</select>";
			  echo 	 " <a  href='#'   class='btn btn-inverse'  onclick=searchdriver() > 確定</a>&nbsp;";
			  echo	 "<label for='Action'>選擇動作</label>";
		}
			  if($mode ==1)
				echo "<a  href='#'   class='btn btn-inverse'  onclick=driveralteration() > 刪除</a>&nbsp;";
			else  if($mode ==0)
				echo "<a  href='#'   class='btn btn-inverse' onclick=driveralteration()   > 新增</a>&nbsp;";

  ?>	
      </center>    
     <?php
			 echo "<center><table border=\"1\" cellspacing='1' id='drivertable'>";
			 echo "<tr><th>站名</th><th>呼號</th><th>電話</th><th>司機姓名</th><th>車牌號碼</th><th>班別</th><th>車種</th><th>工作時段</th><th>車廠地址</th><th>車廠名稱</th>";
			 if($mode <2)
				echo  "<th>選取</th></tr>";
		 	 echo "</table></center>";
	?>
</form>
    </div>
</body>
</html>
