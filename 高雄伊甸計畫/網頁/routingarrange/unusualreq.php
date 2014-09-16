   <?php
        include("Mydbconnect.php");
        include("connectInfo.php");	
		
		$reqnum = $_GET["reqnum"];
		$arrangedate = $_GET["date"];
		$arrangetime = $_GET["time"];
		$carid = $_GET["carid"];
	   $progressrs=mysql_query("SELECT percent FROM `progress` WHERE `index`=1 AND date = '".$arrangedate."' AND time = '".$arrangetime."'");
       $progressrs1=mysql_fetch_array($progressrs);	
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
		function insert(carid,date,time,run,timeinterval,area,area1,Depotname)
		{	  
	       var reqnum=<? echo $reqnum; ?>	
		   
	       var sql="UPDATE arrangedtable SET run"+run.trim()+"="+timeinterval+ ",user"+run.trim()+"="+reqnum+" WHERE `carid`='"+carid.trim()+"' and `date`='"+date+"' and `arrangetime`='"+time+"'";  
          
		   var sql1 ="UPDATE userrequests SET arranged=1 WHERE 識別碼 =' "+reqnum+"'  and `arrangedate`='"+date+"' and `arrangetime`='"+time+"'";	
		   var sql2 ="SELECT * FROM `availablecars` WHERE `車號`='"+carid.trim()+"' and  `date`='"+date+"' and  `time`='"+time+"'";			 
		   var sql3 ="   UPDATE `availablecars` SET `時段`='";					
		   var sql4="'WHERE `車號`='"+carid.trim()+"' and  `date`='"+date+"' and  `time`='"+time+"'";	
		   var Specialcar=0;
		   if(Depotname.indexOf("錦和停車場")!=-1)
				if(area.indexOf("新北市三峽區")!=-1||area1.indexOf("新北市三峽區")!=-1||area.indexOf("新北市鶯歌區")!=-1||area1.indexOf("新北市鶯歌區")!=-1)
				{
					Specialcar=1;
				}
		   $.ajax
                    ({
                    type: "POST",
                    url: "updateunusualreq.php",
                    data: { 'dataString': sql ,'reqdata':sql1,'cardata':sql2,'Specialcar':Specialcar,'writeSpecialcartime':sql3,'writeSpecialcartime1':sql4},
                    cache: false,
                    success: function()
                        {
                            alert("已排入");
                            var progressrs1 = '<?php echo  $progressrs1['percent']; ?>';                                                   
                            if(progressrs1==100)
                            {
                          		window.opener.location.reload();	
                            }
                            else
                            { 
                            	window.opener.RefreshUpdatePanel();	
                            } 				
							window.close(); 						
                        }
                    });
		   
		 }
</script>

  </head>  
  <body>
   <?php      	
        $rs2 = mysql_query("SELECT *  FROM userrequests WHERE 識別碼 =$reqnum AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
		$carr = mysql_query("SELECT * FROM `specialcar` WHERE reqindex='".$reqnum."' and `time`='".$arrangetime."' and `date`='".$arrangedate."'");		
		$req1 = mysql_fetch_array($rs2);		
		$trav = mysql_fetch_array($carr);		
		echo "<center><h3><table border=\"1\" cellspacing='0'>";
		echo "<tr bgcolor='#fafafa' ><th colspan=1>客戶姓名:</th><th colspan=5>".$req1["狀態"].'-'.$req1["姓名"]."</th>";
		echo "<tr bgcolor='#fafafa' ><th colspan=1>客戶帳號:</th><th colspan=5>".$req1["帳號"]."</th>";
		echo "<tr bgcolor='#fafafa' ><th colspan=1>障別:</th><th colspan=5>".$req1["障別"]."</th>";
		echo "<tr bgcolor='#fafafa' ><th colspan=1>上車時間:</th><th colspan=5>".$req1["時段"]."</th>";
		echo "<tr bgcolor='#fafafa' ><th colspan=1>上車區域:</th><th colspan=5>".$req1["上車區域"]."</th>";
		echo "<tr bgcolor='#fafafa' ><th colspan=1>上車地址:</th><th colspan=5>". $req1["上車地址"]."</th>";
		echo "<tr bgcolor='#fafafa' ><th colspan=1>上車備註:</th><th colspan=5>".$req1["GETONRemark"]."</th>";	
		echo "<tr bgcolor='#fafafa' ><th colspan=1>下車區域:</th><th colspan=5>".$req1["下車區域"]."</th>";
		echo "<tr bgcolor='#fafafa' ><th colspan=1>下車地址:</th><th colspan=5>". $req1["下車地址"]."</th>";
		echo "<tr bgcolor='#fafafa' ><th colspan=1>下車備註:</th><th colspan=4>".$req1["OffCarRemark"]."</th>";	
		$time = $req1['時段'];
        $hour = substr($time, 0, 2);
        $min  = substr($time, 2, 2);
        $timesec = $hour * 3600 + $min *60;
        $traveltime = $req1['抵達時間'] - $timesec;
     
		echo "<tr bgcolor='#fafafa' ><th colspan=1>旅行時間:</th><th colspan=5>".  $traveltime."</th>";
		$array = array ();		  
		for($i=1;$i<=4;$i++)
		{
		$traveltime=$trav["carpark".$i];
		 $traveltimemin = (int)($traveltime / 60);
         $traveltimesec = $traveltime % 60;
		$array[$i]=$traveltimemin."分".$traveltimesec ."秒";
			
		}		
	     echo "<tr bgcolor='#fafafa' ><th colspan=6><h2>候選車輛</h2></th>";
		 $time = $req1["時段"];
		 $hour = substr($time, 0, 2);
         $min  = substr($time, 2, 2);
		 $timesec = $hour * 3600 + $min *60;	
		 $run =1;
		 $timeinterval =floor($timesec/1800);
		
		 if($timesec<=30600)
		{
		   
		    $timesec1=$timesec-3000;
		    $timesec=$timesec+1800;		
     		$Candidatecar = mysql_query("SELECT * FROM `arrangedtable` WHERE `worktime`<='".$timesec."' and `worktime`>='".$timesec1."' and `date`='".$arrangedate."' and `arrangetime`='".$arrangetime."'  and `run1`=-1 ORDER BY worktime");
     		//$Candidatecar = mysql_query("SELECT * FROM `arrangedtable` WHERE `worktime`<='".$timesec."' and `date`='".$arrangedate."' and `arrangetime`='".$arrangetime."'  and `run1`=-1 ORDER BY worktime");
    			
	        $Candidatecar1 = mysql_fetch_array($Candidatecar);		
			$run =1;			
		}else if($timesec>=57600&&$timesec<=64800)
		{
		    $timesec=$timesec+1800;
     		$Candidatecar = mysql_query("SELECT * FROM `arrangedtable` WHERE `worktime`<'".$timesec."' and `date`='".$arrangedate."' and `arrangetime`='".$arrangetime."'  and `run2`=-1 ORDER BY worktime");		
	        $Candidatecar1 = mysql_fetch_array($Candidatecar);		
			$run =2;
		}
		else if($timesec>=75600)
		{
		    $timesec=$timesec-36000-1800;
		    $Candidatecar = mysql_query("SELECT carid,worktime,cartype,run1,run2 FROM `arrangedtable` WHERE `worktime`>='".$timesec."' and `date`='".$arrangedate."' and `arrangetime`='".$arrangetime."'  and `run2`=-1 ORDER BY worktime");		
	        $Candidatecar1 = mysql_fetch_array($Candidatecar);
			$run =2;
		}
	
		 println($Candidatecar1 ,$arrangetime,$arrangedate,$timeinterval,$run,$Candidatecar,$array,$req1);	
		 echo "</table></h3></center>";	
		 
		
         mysql_free_result($rs2);
		 mysql_free_result($carr);		
		 mysql_free_result($Candidatecar);
		 
		function println($Candidatecar1,$arrangetime,$arrangedate,$timeinterval,$run,$Candidatecar,$array,$req1)
		{ 
			if($run==1)
				echo "<tr bgcolor='#fafafa' ><th colspan=1 >上班時間</th><th colspan=1 >呼號</th><th colspan=1 >車廠位置</th><th colspan=1 >旅行時間</th><th colspan=1 >排班功能</th>";	
			else
				echo "<tr bgcolor='#fafafa' ><th colspan=1 >下班時間</th><th colspan=1 >呼號</th><th colspan=1 >車廠位置</th><th colspan=1 >旅行時間</th><th colspan=1 >排班功能</th>";	
		
		do
		 {	
		if($Candidatecar1["carid"]=="")
		{
			    echo "<tr bgcolor='#fafafa' ><th colspan=5 >無適合車輛</th>";	
				break;
		}
		 $availablecars = mysql_query("SELECT 時段,車種,場名,呼號 FROM `availablecars` WHERE  `車號`='".$Candidatecar1["carid"]."'");			  	 
         $availablecars1 = mysql_fetch_array($availablecars);				
		 $worktime = preg_split("/~/", $availablecars1[0]);				  
         if($run==1)
		{
		     $setworktime=preg_split("/:/", $worktime[0]);	
			 $sethour=$setworktime[0];
			 $setmin=$setworktime[1];
			 $setmin+=30;
			 if($setmin>=60)
			 {
				$setmin-=60;
			    $sethour++;
			 }
			 if($setmin<10)
				$setmin="0".$setmin;
			 if($sethour<10)
				$sethour=$sethour;			
			$worktime1=$sethour.":".$setmin;
		}
		else
		{
			 $setworktime=preg_split("/:/", $worktime[1]);		
			//$worktime1=$worktime[1];					
			 $sethour=$setworktime[0];
			 $setmin=$setworktime[1];
			 $setmin+=45;
			 if($setmin>=60)
			 {
				$setmin-=60;
			    $sethour++;
			 }
			 if($setmin<10)
				$setmin="0".$setmin;
			 if($sethour<10)
				$sethour=$sethour;			
			$worktime1=$sethour.":".$setmin;			
		}
		 if($req1["車種"]=="")
		 {
		  if($worktime1=="")
			{
		    echo "<tr bgcolor='#fafafa' ><th colspan=5 >無適合車輛</th>";		 
			break;
			}
		   Switch($availablecars1['場名'])
		   {
		   	case '錦和停車場':
			$cartraveltime=$array[1];
				break;
			case '三多停車場':
			$cartraveltime=$array[2];
				break;
			case '安興停車場':
			$cartraveltime=$array[3];
				break;
			case '中州停車場':
			$cartraveltime=$array[4];
				break;
			
		   }
		   echo "<tr bgcolor='#fafafa' ><th colspan=1 >$worktime1</th><th colspan=1 >".$availablecars1[3]."</th><th colspan=1 >".$availablecars1["場名"]."</th><th colspan=1 >$cartraveltime</th><th colspan=1 >
		            <button class=\"btn btn-success\" onclick=\"insert(' ".$Candidatecar1['carid']."','".$arrangedate."','".$arrangetime."',' ".$run ."',' ".$timeinterval."',' ".$req1["上車區域"]."',' ".$req1["下車區域"]."',' ".$availablecars1["場名"]."')\" >排入</button>
		            </th>";		 
		  }
		 else  if($req1["車種"]=="小車")
		   {
 		    if($availablecars1["車種"]=="小車")
		    {
			if($worktime1=="")
			{
		    echo "<tr bgcolor='#fafafa' ><th colspan=5 >無適合車輛</th>";		 
			break;
			}
		   echo "<tr bgcolor='#fafafa' ><th colspan=1 >".$availablecars1[3]."</th><th colspan=1 >$worktime1</th><th colspan=2 >$a".$availablecars1["場名"]."</th><th colspan=1 >
		            <button class=\"btn btn-success\" onclick=\"insert('".$Candidatecar1['carid']."','".$arrangedate."','".$arrangetime."','".$run ."',' ".$timeinterval."','".$req1["上車區域"]."','".$req1["下車區域"]."','".$availablecars1["場名"]."')\" >排入</button></th>";		 
			}
		   }
		   }while($Candidatecar1 = mysql_fetch_array($Candidatecar));									
			
			} 	 	  
       if($progressrs1['percent']==100)
       {
         $notdischarged=mysql_query("SELECT Normal FROM `notdischarged` WHERE date = '".$arrangedate."' AND time = '".$arrangetime."'");
		 $notdischarged1=mysql_fetch_array($notdischarged);	
		 $notdischarged1['Normal']--;
		 mysql_query("UPDATE notdischarged SET Normal =".$notdischarged1['Normal']." WHERE date = '".$arrangedate."' AND time = '".$arrangetime."'");	
		 }
 ?>
  </body>
  </html>