<?php
    include("Mydbconnect.php");
	include("connectInfo.php");
	$arrangetime = $_GET['time'];
	$arrangedate = $_GET['date'];
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
        	font-size: 100%;
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
		var index;    
		 function CheckHeadTail()
		 {
		     var table1 = document.getElementById("checktable");	
		    while(table1.rows.length > 1)
            {
                table1.deleteRow(table1.rows.length - 1);
             }
			$.get("<?php  echo $linkurlport; ?>/WebRoutingArranger/CheckHeadTail.view", {arrangetime: arrangetime, arrangedate: arrangedate},
			function(response)
			{ 	
				 $("span#Status").text("查詢完成");
				  var temp = response.split(",");
				   var tObj = table1.tBodies[0];
				   
				   for(i = 0; i < temp.length; i++)
                   {
				        if(temp[0].indexOf("null")!=-1)
						{
							var row = document.createElement("tr");
							var cell = document.createElement("td");
							cell.innerHTML = "<center>" + (i+1) + "<\/center>";
							row.appendChild(cell);											
							cell = document.createElement("td");
							cell.innerHTML = "<center>" +"目前尚無沒有頭尾班車輛"+ "<\/center>";			
							row.appendChild(cell);							
							tObj.appendChild(row);	
							break;
						}else
						{
						var row = document.createElement("tr");
						var cell = document.createElement("td");
						cell.innerHTML = "<center>" + (i+1) + "<\/center>";
                        row.appendChild(cell);											
					    cell = document.createElement("td");
						cell.innerHTML = "<center>" +temp[i]+ "<\/center>";			
						row.appendChild(cell);							
						tObj.appendChild(row);		
						}					   
					}
			}
					);		
          }   		 
	</script>
</head>
<body>
  <div class="container">    
    <form>
      <center>
	
	   <?php
			echo " <h1>無頭尾班次的司機</h1>	";
		?>
		<h1> 排班日期:<?php echo $arrangedate; ?></h1>	
	   <h1>排班時間:<?php    echo $arrangetime ; ?></h1>	
	   <p>查詢狀態: <span id="Status">處理中請稍後</span></p>
      </center>    
	  <center><table border=1 cellspacing='1' id="checktable">
	  <tr>
	  <th WIDTH=100>流水號</th>
	  <th WIDTH=100>呼號</th>	
	  </tr>
	  </table></center>
	   <script>CheckHeadTail();</script>
</form>
    </div>
</body>
</html>
