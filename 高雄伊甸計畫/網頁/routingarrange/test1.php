<?php
        include("Mydbconnect.php");
        include("connectInfo.php");
        
        $option = $_GET["option"];
        $arrangedate =$_GET["date"];
        $arrangetime =$_GET["time"];      
        $car_no = 0;
        $starthour =  6;
        $endhour = 24;
        $tolertime = 0;
       $rs1 = mysql_query("SELECT A1.*,A2 .*	FROM arrangedtable A1, availablecars A2 WHERE A1.carid = A2.車號  AND  A2. date= '".$arrangedate."' AND A2.time = '".$arrangetime."' AND  A1. date = '" .$arrangedate."' AND A1.arrangetime = '".$arrangetime."' GROUP BY A1.`carid` ORDER BY A1.worktime ASC");
        $num_rows = mysql_num_rows($rs1);

?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <head>
        <title>FixedHeaderTable Test</title>  
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.5.2/jquery.min.js"></script>
		 <link href="css/defaultTheme.css" rel="stylesheet" media="screen" />
		<link href="css/myTheme.css" rel="stylesheet" media="screen" />
        <script src="js/jquery.fixedheadertable.js"></script>
        <script src="js/demo.js"></script>
    </head>
    <body>
		<?php
    	echo "<div style=\"width:1500px;height: 458px;\">";
    	echo "<table class=\"fancyTable\" id=\"table1\" cellpadding=\"0\" cellspacing=\"0\">";
        echo "<thead>";
					
						echo "<tr bgcolor='#fafafa' >
								<th colspan=1>車輛編號</th>
								 <th colspan=1 >站名 </th>
								 <th colspan=1 >呼號</th>
								 <th colspan=1>工作時段</th>
								 <th colspan=1  >車種</th>";
								  $row = mysql_fetch_array($rs1);  
									$interval = $row['timeinterval'];
									$number = $starthour -1;
								$divi = (int)(($endhour - $starthour)/$interval);
								$temp = (int)(1/$interval);
							//表格標題
						for($i = 0; $i < $divi; $i++)
						{
							if($i % $temp == 0)
							{
								$number++;              
								echo "<th>".$number.":00</th>";
							}
							else
							{
								echo "<th>".$number.":".(int)(60/$temp*($i%$temp))."</th>";
							}
						}
						echo "</tr>";
						   $offset = (int)($starthour/$interval);
						   $stopinterval = (int)($endhour/$interval);
						   $intervalsec = $interval * 3600;
						  $intervalcount = 24 / $interval;
					 echo "</thead>";
    			   	echo " <tbody>";
			
					  do
							{
							    $car_no++;
								echo "<tr align='center' valign='middle' bgcolor='#fafafa' style='cursor:default;' onMouseOver=\"this.style.backgroundColor='#DEF8FA'\" onMouseOut=\"this.style.backgroundColor='#FFFFFF'\"><td>".$car_no."</td><td>"
									.$row['站名']."</td><td class=\"carid\">(".$row['呼號'].")<input type=\"hidden\" value=\"".$row['carid']."\" name=\"".$row['carid']."\" /></td>";
							 
			  $time = preg_split("/~/",$row['時段']);  
			  $time1 = preg_split("/:/",$time[0]);             
			  $smin =$time1[1]+30;
			  $shour =$time1[0];
			  if($smin>=60)
			  {
			  	$smin=$smin-60;
				$shour++;
			  }
			  if($smin == 0)
              {
                 $smin = "00";
              }
			  
              $time2 = preg_split("/:/",$time[1]);             
			  $emin =$time2[1]+45;
			  $ehour =$time2[0];
			  if($emin>=60)
			  {
			  	
			  	$emin=$emin-60;
				$ehour++;
			  }
			  if($emin == 0)
              {
                      $emin = "00";
              }
             echo "<td>".$shour.":". $smin."~". $ehour.":".$emin."</td><td class=\"cartype\">".$row['cartype']."<input type=\"hidden\" value=\"".$row['cartype']."\" name=\"".$row['cartype']."\" /></td>";
            
			 if($smin>=30&&$smin<=59)
			  {
			  $startinterval =($shour*2)+1;
			   $endinterval =($ehour*2)+1;
			  }
              else
			   {
			   $startinterval =($shour*2);
			    $endinterval =$ehour*2;
			   }
			   
             
              //陣列初始化
              $resttime=$row['resttime1'];
				$resttime1 = explode(':', $resttime);
				
              for($i = 0; $i < $intervalcount; $i++)
              {
                      if($i < $startinterval || $i >$endinterval)
                      {
                              $carinterval[$i] = "<td>X</td>";
                      }
					  else if(($i==$resttime1[0])||(($i==$resttime1[1])))
					  {
					  $carinterval[$i] = "<td>休息</td>";
					  }			 				  
                      else
                      {
                          $carinterval[$i] = "<td id=$i.$car_no class=\"empty\" onmousedown=test(this.id)></td>";
                      }
              }
				  for($i = 1; $i < 13; $i++)
              {
                      $fillinterval = $row["run".$i];
                      if($fillinterval != -1)
                      {
                              $information = $row["user".$i];
                              $temp2 = explode('_', $information);
                              if(count($temp2) > 1)
                              {
										$fontcolor="#EE1289";
                                      $hiddenmessage = $i."_1_".$information;
                                      $rs2 = mysql_query("SELECT 抵達時間, 姓名, 帳號, 時段, 上車區域, 上車地址, 下車區域, 下車地址 FROM userrequests WHERE 識別碼 = '".$temp2[0]."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
                                      $rs3 = mysql_query("SELECT 抵達時間, 姓名, 帳號, 時段, 上車區域, 上車地址, 下車區域, 下車地址 FROM userrequests WHERE 識別碼 = '".$temp2[1]."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
                                      $rs4 = mysql_query("SELECT *FROM travelinformationofcarsharing WHERE AssignSharing = '".$information."' AND date = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
                                      if($row4 = mysql_fetch_array($rs4))
                                      {
                                              $sharing = $row4['起點'];
                                              $splitsharing = explode('_', $sharing);
                                              if(strcmp($splitsharing[0], '0') == 0)
                                              {
                                                      if($row2 = mysql_fetch_array($rs2))
                                                      {      
                                                              $carinterval[$fillinterval] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2><font color=\"#8B4726\">共乘:</font> <br /><font color=\"#FF0000\">時間: ".$row2['時段']."</font><br /><font color=\"#0000FF\">上車</font>: <font color=\"#8B4726\">".$row2['姓名']."</font><br /><font color=\"#8B4726\">".$row2['上車區域'].$row2['上車地址']."</font></font>";
                                                              if($row3 = mysql_fetch_array($rs3))
                                                              {
                                                                      $carinterval[$fillinterval].= "<br /><br /><font color=\"#FF0000\">時間: ".$row3['時段']."</font><br /><font color=\"#0000FF\">上車</font>:<font color=\"#8B4726\"> ".$row3['姓名']."</font><br /><font color=\"#8B4726\"> ".$row3['上車區域'].$row3['上車地址']."</font>";
                                                                      $sharing2 = $row4['終點'];
                                                                      $splitsharing2 = explode('_', $sharing2);
                                                                       if(strcmp($splitsharing2[0], '0') == 0)
                                                                      {
                                                                              $adapttime1 = $row2['抵達時間'] + $tolertime;
                                                                              $tempInterval = (int)($adapttime1 / $intervalsec);
                                                                              $arrivehour1 =  (int)($adapttime1 / 3600);
                                                                              $arrivemin1 = (int)(($adapttime1 % 3600) / 60);
                                                                              if($arrivehour1 < 10)
                                                                              {
                                                                                      $arrivehour1 = "0".$arrivehour1;
                                                                              }
                                                                              if($arrivemin1 < 10)
                                                                              {
                                                                                      $arrivemin1 = "0".$arrivemin1;
                                                                              }
                                                                              $adapttime2 = $row3['抵達時間'] + $tolertime;
                                                                              $arrivehour2 =  (int)($adapttime2 / 3600);
                                                                              $arrivemin2 = (int)(($adapttime2 % 3600) / 60);
                                                                              if($arrivehour2 < 10)
                                                                              {
                                                                                      $arrivehour2 = "0".$arrivehour2;
                                                                              }
                                                                              if($arrivemin2 < 10)
                                                                              {
                                                                                      $arrivemin2 = "0".$arrivemin2;
                                                                              }
                                                                              if($tempInterval == $fillinterval)
                                                                              {
                                                                                      $carinterval[$fillinterval].= "<br /><br /><font color=\"#339900\">下車</font>: <font color=\"#8B4726\">".$row3['下車']."</font ><br /><font color=\"#8B4726\">".$row3['下車區域'].$row3['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$row2['姓名']."</font><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font></td>";
                                                                              }
                                                                              else
                                                                              {
                                                                                      $carinterval[$fillinterval].= "</font></td>";
                                                                                      $j = -1;
                                                                                      for($j = $fillinterval + 1; $j < $tempInterval; $j++)
                                                                                      {
                                                                                              $carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
                                                                                      }
                                                                                      $carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2><font color=\"#8B4726\">共乘: </font>".$row4['車上乘員']."<br /><br /><font color=\"#339900\">下車</font>: <font color=\"#8B4726\">".$row3['姓名']."</font><br /><font color=\"#8B4726\">".$row3['下車區域'].$row3['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\">  ".$row2['姓名']."</font><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font></font></td>";
                                                                              }
                                                                      }
                                                                      else
                                                                      {
                                                                              $adapttime1 = $row3['抵達時間'] + $tolertime;
                                                                              $tempInterval = (int)($adapttime1 / $intervalsec) - $offset;
                                                                              $arrivehour1 =  (int)($adapttime1 / 3600);
                                                                              $arrivemin1 = (int)(($adapttime1 % 3600) / 60);
                                                                              if($arrivehour1 < 10)
                                                                              {
                                                                                      $arrivehour1 = "0".$arrivehour1;
                                                                              }
                                                                              if($arrivemin1 < 10)
                                                                              {
                                                                                      $arrivemin1 = "0".$arrivemin1;
                                                                              }
                                                                              $adapttime2 = $row2['抵達時間'] + $tolertime;
                                                                              $arrivehour2 =  (int)($adapttime2 / 3600);
                                                                              $arrivemin2 = (int)(($adapttime2 % 3600) / 60);
                                                                              if($arrivehour2 < 10)
                                                                              {
                                                                                      $arrivehour2 = "0".$arrivehour2;
                                                                              }
                                                                              if($arrivemin2 < 10)
                                                                              {
                                                                                      $arrivemin2 = "0".$arrivemin2;
                                                                              }
                                                                              if($tempInterval == $fillinterval)
                                                                              {
                                                                                      $carinterval[$fillinterval].= "<br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$row2['姓名']."</font><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$row3['姓名']."</font><br /><font color=\"#8B4726\">".$row3['下車區域'].$row3['下車地址']."</font></td>";
                                                                              }
                                                                              else
                                                                              {
                                                                                      $carinterval[$fillinterval].= "</font></td>";
                                                                                      $j = -1;
                                                                                      for($j = $fillinterval + 1; $j < $tempInterval; $j++)
                                                                                      {
                                                                                              $carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
                                                                                      }
                                                                                      $carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2><font color=\"#8B4726\">共乘:</font><br /><font color=\"#339900\">下車</font>: <font color=\"#8B4726\">".$row2['姓名']."</font><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$row3['姓名']."</font><br /><font color=\"#8B4726\">".$row3['下車區域'].$row3['下車地址']."</font></td>";
                                                                                      //$i++;
                                                                              }
                                                                      }
                                                              }
                                                              else
                                                              {
                                                                      echo $temp2[1]."error1!!!!<br />";
                                                              }
                                                      }
                                              }
                                              else
                                              {
                                                      if($row3 = mysql_fetch_array($rs3))
                                                      {       
                                                              $carinterval[$fillinterval] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2>共乘: ".$row4['車上乘員']."<br /><br /><font color=\"#FF0000\">時間: ".$row3['時段']."</font><br /><font color=\"#0000FF\">上車</font>: ".$temp2[1]."<br />".$row3['上車區域'].$row3['上車地址'];
                                                              if($row2 = mysql_fetch_array($rs2))
                                                              {
                                                                      $carinterval[$fillinterval].= "<br /><br /><font color=\"#FF0000\">時間: ".$row2['時段']."</font><br /><font color=\"#0000FF\">上車</font>: <font color=\"#8B4726\">".$temp2[0]."</font ><br /><font color=\"#8B4726\">".$row2['上車區域'].$row2['上車地址']."</font>";
                                                                      $sharing2 = $row4['終點'];
                                                                      $splitsharing2 = explode('_', $sharing2);
                                                                      if(strcmp($splitsharing2[0], '0') == 0)
                                                                      {
                                                                              $adapttime1 = $row2['抵達時間'] + $tolertime;
                                                                              $tempInterval = (int)($adapttime1 / $intervalsec) - $offset;
                                                                              $arrivehour1 =  (int)($adapttime1 / 3600);
                                                                              $arrivemin1 = (int)(($adapttime1 % 3600) / 60);
                                                                              if($arrivehour1 < 10)
                                                                              {
                                                                                      $arrivehour1 = "0".$arrivehour1;
                                                                              }
                                                                              if($arrivemin1 < 10)
                                                                              {
                                                                                      $arrivemin1 = "0".$arrivemin1;
                                                                              }
                                                                              $adapttime2 = $row3['抵達時間'] + $tolertime;
                                                                              $arrivehour2 =  (int)($adapttime2 / 3600);
                                                                              $arrivemin2 = (int)(($adapttime2 % 3600) / 60);
                                                                              if($arrivehour2 < 10)
                                                                              {
                                                                                      $arrivehour2 = "0".$arrivehour2;
                                                                              }
                                                                              if($arrivemin2 < 10)
                                                                              {
                                                                                      $arrivemin2 = "0".$arrivemin2;
                                                                              }
                                                                              if($tempInterval == $fillinterval)
                                                                              {
                                                                                      $carinterval[$fillinterval].= "<br /><br /><font color=\"#339900\">下車</font>: <font color=\"#8B4726\">".$temp2[1]."</font>br / <font color=\"#8B4726\">".$row3['下車區域'].$row3['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$temp2[0]."</font><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font></td>";
                                                                              }
                                                                              else
                                                                              {
                                                                                      $carinterval[$fillinterval].= "</font></td>";
                                                                                      $j = -1;
                                                                                      for($j = $fillinterval + 1; $j < $tempInterval; $j++)
                                                                                      {
                                                                                              $carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
                                                                                      }
                                                                                      $carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2>共乘: ".$row4['車上乘員']."<br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$temp2[1]."</font><br /><font color=\"#8B4726\">".$row3['下車區域'].$row3['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$temp2[0]."</font><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font></td>";
                                                                              }
                                                                      }
                                                                      else
                                                                      {
                                                                              $adapttime1 = $row3['抵達時間'] + $tolertime;
                                                                              $tempInterval = (int)($adapttime1 / $intervalsec) - $offset;
                                                                              $arrivehour1 =  (int)($adapttime1 / 3600);
                                                                              $arrivemin1 = (int)(($adapttime1 % 3600) / 60);
                                                                              if($arrivehour1 < 10)
                                                                              {
                                                                                      $arrivehour1 = "0".$arrivehour1;
                                                                              }
                                                                              if($arrivemin1 < 10)
                                                                              {
                                                                                      $arrivemin1 = "0".$arrivemin1;
                                                                              }
                                                                              $adapttime2 = $row2['抵達時間'] + $tolertime;
                                                                              $arrivehour2 =  (int)($adapttime2 / 3600);
                                                                              $arrivemin2 = (int)(($adapttime2 % 3600) / 60);
                                                                              if($arrivehour2 < 10)
                                                                              {
                                                                                      $arrivehour2 = "0".$arrivehour2;
                                                                              }
                                                                              if($arrivemin2 < 10)
                                                                              {
                                                                                      $arrivemin2 = "0".$arrivemin2;
                                                                              }
                                                                              if($tempInterval == $fillinterval)
                                                                              {
                                                                                      $carinterval[$fillinterval].= "<br /><br /><font color=\"#339900\">下車</font>: <font color=\"#8B4726\">".$temp2[0]."</font><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>:<font color=\"#8B4726\"> ".$temp2[1]."</font><br /><font color=\"#8B4726\">".$row3['下車區域'].$row3['下車地址']."</font></td>";
                                                                              }
                                                                              else
                                                                              {
                                                                                      $carinterval[$fillinterval].= "</font></td>";
                                                                                      $j = -1;
                                                                                      for($j = $fillinterval + 1; $j < $tempInterval; $j++)
                                                                                      {
                                                                                        $carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
                                                                                      }
                                                                                      $carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" /><font size=2><font color=\"#8B4726\">共乘: ".$row4['車上乘員']."</font><br /><br /><font color=\"#339900\">下車</font>: <font color=\"#8B4726\">".$temp2[0]."</font ><br /><font color=\"#8B4726\">".$row2['下車區域'].$row2['下車地址']."</font><br /><br /><font color=\"#339900\">下車</font>: <font color=\"#8B4726\">".$temp2[1]."<br />".$row3['下車區域'].$row3['下車地址']."</font></font></td>";
                                                                              }
                                                                      }
                                                              }
                                                              else
                                                              {
                                                                      echo $information."error2!!!!<br />";
                                                              }
                                                      }
                                              }
                                      }
                                      else
                                      {
                                              echo "<td>".$information."</td>";
                                      }
                              }
                              else
                              {
                                      $hiddenmessage = $i."_0_".$information;	
									  if($i!=1)										  
									  	{
									  		$temrs = mysql_query("SELECT 抵達時間, 姓名 ,下車區域, 下車地址 FROM userrequests WHERE 識別碼 = '".$teminformation."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");	
											$temrsrow = mysql_fetch_array($temrs);
											$temendintervel=floor(($temrsrow['抵達時間']+$tolertime)/1800);
										}					 								 
                                      $rs2 = mysql_query("SELECT 抵達時間, 姓名, 帳號,狀態, 時段, 上車區域, 上車地址, 下車區域, 下車地址 FROM userrequests WHERE 識別碼 = '".$information."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
                                      if($row2 = mysql_fetch_array($rs2))
                                      {
                                              $add = $row2['上車地址'];
                                              $block = $row2['上車區域'];
											  $adapttime = $row2['抵達時間'] + $tolertime;
                                              $tempInterval = (int)($adapttime / $intervalsec);
                                              $arrivehour =  (int)($adapttime / 3600);                                           
                                              $arrivemin = (int)(($adapttime % 3600) / 60);
                                              if($arrivehour < 10)
                                              {
                                                      $arrivehour = "0".$arrivehour;
                                              }
                                              if($arrivemin < 10)
                                              {
                                                      $arrivemin = "0".$arrivemin;
                                              }
											   if(($temendintervel==$fillinterval)&&$i!=1)
													$tempstr=123;
											   else	
											   		$tempstr=456;
                                              if($tempInterval == $fillinterval)
                                              {
                                              		  if($row2['狀態']=="候補")
													  $fontcolor="#EE1289";
													  else
													  $fontcolor="#000000";   
													     
                                                      $tempadd = $row2['下車地址'];
                                                      $tempblock = $row2['下車區域'];
													  
													  if(($temendintervel==$fillinterval)&&$i!=1)	
													   $carinterval[$fillinterval] = 
													   "<td class=\"nonempty\">
													   <input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />
													   <font size=2 color=$fontcolor>".$row2['狀態'].$row2['姓名']."</font><br />
													   <font color=\"#FF0000\">時間: ".$row2['時段']."</font><br />
													   <font color=\"#0000FF\">上車</font>: 
													   <font color=\"$fontcolor\">".$block.$add."<br /><br />
													   <font color=\"#FF0000\">時間: ".$arrivehour.$arrivemin."</font><br />
													   <font color=\"#339900\">下車</font>: 
													   <font color=\"$fontcolor\">".$tempblock.$tempadd."</font></td>";
													  else							  
                                                      {
                                                      	$carinterval[$fillinterval] = 
                                                      	"<td class=\"nonempty\">
                                                      	<input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />
                                                      	<font size=2 color=$fontcolor >".$row2['狀態']."-".$row2['姓名']."</font><br />
                                                      	<font color=\"FF0000\">時間: ".$row2['時段']."</font><br />
                                                      	<font color=\"#0000FF\">上車</font>: 
                                                      	<font color=\"$fontcolor\">".$block.$add."<br /><br />
                                                      	<font color=\"#FF0000\">時間: ".$arrivehour.$arrivemin."</font><br />
                                                      	<font color=\"#0000FF\">下車</font>: 
                                                      	<font color=\"$fontcolor\">".$tempblock.$tempadd."</font></td>";
													}
                                              }
                                              else
                                              {
                                              	      if($row2['狀態']=="候補")
													  $fontcolor="#EE1289";
													  else
													  $fontcolor="#000000";   
                                                      $carinterval[$fillinterval] = 
                                                      "<td class=\"nonempty\">
                                                      <input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />
                                                      <font size=2 color=\"$fontcolor\">".$row2['狀態']."-".$row2['姓名']."<br />
                                                      <font color=\"#FF0000\">時間: ".$row2['時段']."</font><br />
                                                      <font color=\"#0000FF\">上車</font>:
                                                      <font color=\"$fontcolor\">".$block.$add."</font></td>";
                                                      $j = -1;
                                                      for($j = $fillinterval + 1; $j < $tempInterval; $j++)
                                                      {
                                                       $carinterval[$j] = "<td class=\"nonempty\"><input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />-></td>";
                                                      }
                                                      $tempadd = $row2['下車地址'];
                                                      $tempblock = $row2['下車區域'];
                                                      $carinterval[$j] = 
                                                      "<td class=\"nonempty\">
                                                      <input class=\"runnumber\" type=\"hidden\" value=\"".$hiddenmessage."\" name=\"".$i."\" />
                                                      <font size=2 color=\"$fontcolor\">".$row2['狀態']."-".$row2['姓名']."<br />
                                                      <font color=\"#FF0000\">時間: ".$arrivehour.$arrivemin."</font><br />
                                                      <font color=\"#0000FF\">下車</font>:
                                                      <font color=\"$fontcolor\">".$tempblock.$tempadd."</font></td>";
                                              }
                                      }
 							  $teminformation=$information;
                              }
                      }
              }
              for($i = $offset; $i < $stopinterval; $i++)
              {
                      echo $carinterval[$i];
              }				
				 echo "</tr>";
				}while($row = mysql_fetch_array($rs1));
					
					
				echo " </tbody>";
    			echo " </table>";
				
    
    		echo "<div class=\"clear\"></div>";
    	echo "</div>";
		?>
    </body>
</html>
