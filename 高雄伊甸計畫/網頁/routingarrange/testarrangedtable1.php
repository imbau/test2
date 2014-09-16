<?php
	include("Mydbconnect.php");
	include("connectInfo.php");
	
	$option = $_GET["option"];
	$arrangedate = "";
	$arrangetime = "";
	set_time_limit(0);
	$arrangedate =$_GET["date"];
	$arrangetime =$_GET["time"];     
	$car_no = 0;
	$starthour =  6;
	$endhour = 24;
	$tolertime = 600;
	$rs1 = mysql_query("SELECT * FROM availablecars WHERE date = '".$arrangedate."'  AND time = '".$arrangetime."' ORDER BY no");
	$num_rows = mysql_num_rows($rs1);	
    

?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
<link href="bootstrap.min.css" rel="stylesheet" />
<link rel="stylesheet" href="http://blueimp.github.com/cdn/css/bootstrap-responsive.min.css">
<link rel="stylesheet" href="http://blueimp.github.com/Bootstrap-Image-Gallery/css/bootstrap-image-gallery.min.css">
<link type="text/css" href="menu.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="css/demo.css" />
<link rel="stylesheet" type="text/css" href="css/style6.css" />		
<link rel="stylesheet" href="css/darkwash.css" media="screen" />
<script type="text/javascript" src="http://code.jquery.com/jquery-1.8.2.js"></script>
<script type="text/javascript" src="/resources/demos/external/jquery.bgiframe-2.1.2.js"></script>
<script type="text/javascript" src="http://code.jquery.com/ui/1.9.0/jquery-ui.js"></script>
		 <script type="text/javascript" src="jquery.lettering.js"></script>
		  <script type="text/javascript" src="menu.js"></script>
		  <script type="text/javascript" src="script.js"></script>
		    <script  src="tools.js">  </script>  
<style type="text/css">
        body { font-size: 80%; 	
			   color:SaddleBrown;	  
			   background: url(images/background.jpg) no-repeat center top #252525;
	           font-family:Eras Light ITC, Helvetica, sans-serif;
			   }		
</style>
<script type="text/javascript">
		var arrangedate = '<?php echo $arrangedate; ?>';
        var arrangetime = '<?php echo $arrangetime; ?>';
        var carid = '<?php echo $carid; ?>';
		if(window.name != "bencalie")
		{
		 location.reload();
		 window.name = "bencalie";
		}
		else
		{
			window.name = "";
		}
       function pdfcardetail()
			{				
				window.open ("cardetailpdf1.php?date="+arrangedate+"&time="+arrangetime,"Editar notícia", "location=1, status=1, scrollbars=1, width=1200, height=750");
			}
/*******************載入頁面div*********************/
function loadmunuphp()
			{		
			 $("#preview").load("menu.php");
			}				
/*******************載入頁面div*********************/	

$(function(){
	loadmunuphp();
});
		</script>
</head>

<body>
  <div class="container">
 <div id="preview" style="width: 1150px;  height:200px;"></div>
 <div class="well1">
<p>
  <input name="return" type="button" onclick="history.back()" value="回上一頁" class="btn btn-primary start"/>&nbsp 
  
<?php
echo "<a href='#' class='btn btn-success' onClick=pdfcardetail()>列印全部班表</a>";
$row = mysql_fetch_array($rs1);
echo "<br><h3>排班日期:".$row['date']." <br><h3>點擊呼號顯示詳細資訊 ";
echo "<table border=\"3\" cellspacing='0'  >";
do
{    
	if(($car_no%15)==0)
	echo " <tr>";
    $car_no++;		
	echo "<td bgcolor='#fafafa' style='font-size:25px; border-width:2px; '>".$car_no."</td>
	<td bgcolor='#fafafa' style='font-size:25px;border-width:2px;'>
	<a style='color: #4169E1;' href='./cardetail.php?date=$arrangedate&time=$arrangetime&carid=".$row['車號']."' target=_blank>".$row['呼號']."</a>
	</td>	
	";		
}while($row = mysql_fetch_array($rs1));
echo "</table>";
?>
</p>
<p>&nbsp;</p>
 </div>
    <center>
	<img src="images/logo1.png"  />		
	</center>
	<p align=center><font color=#FFC125>建議解析度 1024x768 以上觀看</font></p>
</body>
</html>