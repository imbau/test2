   <?php
        include("Mydbconnect.php");
        include("connectInfo.php");	
		
		$response = $_GET["response"];
		$arrangedate = $_GET["date"];
		$arrangetime = $_GET["time"];
		$carid = $_GET["carid"];
		$mode = $_GET["mode"];
		$share = $_GET["share"];
 ?>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta name="generator" content="HTML Tidy for HTML5 (experimental) for Windows https://github.com/w3c/tidy-html5/tree/c63cc39" />
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <title>復康巴士第一營運中心排班系統</title>
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
		function insert(date,time,startarea,staradd,endarea,endadd,account)
		{	  
		var arrangedate = '<?php echo $arrangedate; ?>';
		var arrangetime = '<?php echo $arrangetime; ?>';
		var carid = '<?php echo $carid; ?>';		
		var sharing = '<?php echo $share; ?>';
		 
		$.post("<?php echo $linkurlport;?>/WebRoutingArranger/cararranger.view", 
                                            { 
                                                arrangedate: arrangedate,
                                                arrangetime: arrangetime,
                                                account:account,
                                                carid:carid,
                                                startarea: startarea,
                                                startadd: staradd,
                                                endarea:endarea,
                                                endadd: endadd,
												sharing: sharing,
                                                tolorent:600
                                                 }, function(returnString)
                                                        {
                                                                var temp = returnString.split(",");
                                                                if(temp[0] == 0)
                                                                {
                                                                      alert(temp[1]);
                                                                }
                                                                else
                                                                {
                                                                       window.opener.location.reload();	
																	    alert(temp[1]);
																		window.close(); 	
                                                                }
                                                        });
				
		 }
</script>

  </head>  
  <body>
  <?php
		echo "<center><h3><table border=\"1\" cellspacing='0'>";
		if($mode==0)		
		echo "<tr bgcolor='#fafafa' ><th colspan=7><h2>候選車趟</h2></th>";
		else
		echo "<tr bgcolor='#fafafa' ><th colspan=7><h2>候選中繼點</h2></th>";
		echo "<tr bgcolor='#fafafa' ><th colspan=1 >帳號:</th><th colspan=1 >姓名</th><th colspan=1 >上車地址</th><th colspan=1 >下車地址</th><th colspan=1 >上車時間</th><th colspan=1 >旅行時間</th><th colspan=1 >排班功能</th>";	
		if($response=="找不到中繼點")
				echo "<tr bgcolor='#fafafa' ><th colspan=7><h2>此時段找不到適合中繼點</h2></th>";
		 else if($response=="找不到車趟")
				echo "<tr bgcolor='#fafafa' ><th colspan=7><h2>此時段找不到車趟</h2></th>";
		 else
		 {
		  $relayreq = preg_split("/,/",$response);  
		 foreach ($relayreq as $value) {   
		   $rs2 = mysql_query("SELECT * FROM userrequests WHERE 識別碼 = $value AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
		   $req1 = mysql_fetch_array($rs2);	
			$time = $req1["時段"];
			$hour = substr($time, 0, 2);
			$min  = substr($time, 2, 2);
			$timesec = $hour * 3600 + $min *60;
			$Arrivaltime = $req1["抵達時間"];
			$Traveltime=$Arrivaltime-$timesec;
			
     			
			echo "<tr bgcolor='#fafafa' ><th colspan=1 >".$req1["帳號"]."</th><th colspan=1 >".$req1["狀態"]."-".$req1["姓名"]."</th><th colspan=1 >".$req1["上車區域"].$req1["上車地址"]."</th><th colspan=1 >".$req1["下車區域"].$req1["下車地址"]."</th><th colspan=1 >".$req1["時段"]."</th><th colspan=1 >".floor($Traveltime/60)."分".($Traveltime%60)."秒"."</th><th colspan=1 >  <button class=\"btn btn-success\" onclick=\"insert('$arrangedate','.$arrangetime.','". $req1["上車區域"]."','". $req1["上車地址"]."','".$req1["下車區域"]."','".$req1["下車地址"]."','".$value."')\" >排入</button></th>";	
			 
			}  
		 }
	
  
  ?>
   </body>
  </html>