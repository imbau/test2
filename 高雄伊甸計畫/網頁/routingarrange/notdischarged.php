   <?php
        include("Mydbconnect.php");
        include("connectInfo.php");			
		$reqnum = $_GET["reqnum"];
		$arrangedate = $_GET["date"];
		$arrangetime = $_GET["time"];
		$Reason=$_GET["Reason"];
 ?>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta name="generator" content="HTML Tidy for HTML5 (experimental) for Windows https://github.com/w3c/tidy-html5/tree/c63cc39" />
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />  
  <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
  <link href="bootstrap.min.css" rel="stylesheet" />
  <link rel="stylesheet" href="http://blueimp.github.com/cdn/css/bootstrap-responsive.min.css" />
  <link rel="stylesheet" href="http://blueimp.github.com/Bootstrap-Image-Gallery/css/bootstrap-image-gallery.min.css" />
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
  <style type="text/css">
        body { 
			   color:RoyalBlue;	  
			   background: url(images/background.jpg) no-repeat center top #252525;
	           font-family:Eras Light ITC, Helvetica, sans-serif;}      

</style>

<script>
</script>

  </head>  
  <body>
   <?php
      $notdischarge  = mysql_query("SELECT *FROM userrequests WHERE  識別碼='".$reqnum."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
      $notdischarge_num_row = mysql_fetch_array($notdischarge);
	  echo "<center><h3><table border=\"1\" cellspacing='0'>";
	  echo "<tr bgcolor='#fafafa' ><th colspan=1>客戶姓名:</th><th colspan=4>".$notdischarge_num_row["姓名"]."</th>";
	  echo "<tr bgcolor='#fafafa' ><th colspan=1>帳號:</th><th colspan=4>".$notdischarge_num_row["帳號"]."</th>";
	  echo "<tr bgcolor='#fafafa' ><th colspan=1>障別:</th><th colspan=4>".$notdischarge_num_row["障別"]."</th>";
	  echo "<tr bgcolor='#fafafa' ><th colspan=1>上車時間:</th><th colspan=4>".$notdischarge_num_row["時段"]."</th>";
	  echo "<tr bgcolor='#fafafa' ><th colspan=1>上車區域:</th><th colspan=4>".$notdischarge_num_row["上車區域"]."</th>";
	  echo "<tr bgcolor='#fafafa' ><th colspan=1>上車地址:</th><th colspan=4>".$notdischarge_num_row["上車地址"]."</th>";
	  echo "<tr bgcolor='#fafafa' ><th colspan=1>上車備註:</th><th colspan=4>".$notdischarge_num_row["GETONRemark"]."</th>";
	  echo "<tr bgcolor='#fafafa' ><th colspan=1>下車區域:</th><th colspan=4>".$notdischarge_num_row["下車區域"]."</th>";	
	  echo "<tr bgcolor='#fafafa' ><th colspan=1>下車地址:</th><th colspan=4>".$notdischarge_num_row["下車地址"]."</th>"; 
	  echo "<tr bgcolor='#fafafa' ><th colspan=1>下車備註:</th><th colspan=4>".$notdischarge_num_row["OffCarRemark"]."</th>";		  
	  $time = $notdischarge_num_row["時段"];
	  $hour = substr($time, 0, 2);
      $min  = substr($time, 2, 2);
	  $timesec = $hour * 3600 + $min *60;	
	  $traveltime = $notdischarge_num_row['抵達時間'] - $timesec;	
	  echo "<tr bgcolor='#fafafa' ><th colspan=1 >旅行時間:</th><th colspan=4>".$traveltime."</th>";	
   	  echo "<tr bgcolor='#fafafa' ><th colspan=1 >原因:</th><th colspan=4>".$Reason."</th>";	
	  echo "</table></h3></center>";	
 ?>
  </body>
  </html>