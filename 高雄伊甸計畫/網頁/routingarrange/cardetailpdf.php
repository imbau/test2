<?php
    include("Mydbconnect.php");	
	require('chinese-unicode.php');	 
	$option = $_GET["option"];
	$carid = $_GET["carid"];	
	$arrangedate = "";
	$arrangetime = "";
	set_time_limit(0);
	$arrangedate =$_GET["date"];
	$arrangetime =$_GET["time"];
	
	$cartable= mysql_query("SELECT * FROM `arrangedtable` WHERE `carid`='".$carid."' and  date = '".$arrangedate."'  AND arrangetime = '".$arrangetime."'");	
	$num_rows = mysql_num_rows($cartable);	
	$availablecars= mysql_query("SELECT * FROM `availablecars` WHERE `車號`='".$carid."' and  date = '".$arrangedate."'  AND time = '".$arrangetime."'");	
	$carclassrow = mysql_fetch_array($availablecars);	
	$row = mysql_fetch_array($cartable);	
	$interval = $row['timeinterval'];
	$starthour =  6;
	$endhour = 24;
	$tolertime = 600;
	$intervalsec = $interval * 3600;
	$Carclass=$carclassrow['班別'];
	$station=$carclassrow['站名'];
	$drivername=$carclassrow['drivername'];
	header("Content-type: application/msword"); 
	header("Content-disposition: attachment; filename=出勤總表.doc;charset=utf-8"); 
	$File = fopen( "templete.htm", "r" );
	$Output = fread( $File, filesize("templete.htm") );
	fclose( $File );	
	$Output = str_replace( "(station)", mb_convert_encoding($carclassrow['站名'],'Big5','auto'), $Output );
	$Output = str_replace( "(carid)", mb_convert_encoding($carid,'Big5','auto'), $Output );
	$Output = str_replace( "(driver)", mb_convert_encoding($carclassrow['drivername'],'Big5','auto'), $Output );
	$Output = str_replace( "(class)", mb_convert_encoding($carclassrow['班別'],'Big5','auto'), $Output );
	$Output = str_replace( "(date)", mb_convert_encoding($carclassrow['TurnoutDate'],'Big5','auto'), $Output );	
	
	$pos = strrpos($Output, "<tr style='mso-yfti-irow:5;mso-yfti-lastrow:yes;height:17.45pt'>");
	$Output1=substr($Output,$pos);
	
	$pos = strrpos($Output, "<tr style='mso-yfti-irow:4;height:17.45pt'>");
	$Output=substr($Output,0,$pos);	
	
		
	for($i=1;$i<=16;$i++)  
	{		
	    $dataarray = array (" "," "," "," "," ");
		if($row["run".$i]!=-1)
		{
			$information = $row["user".$i];
			$temp2 = explode('_', $information);
			if($information!=''&&$information!=' ')
			{   
				if(count($temp2) > 1)
				{
					$rs2 = mysql_query("SELECT * FROM userrequests WHERE 識別碼 ='".$temp2[0]."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
					$rs3 = mysql_query("SELECT * FROM userrequests WHERE 識別碼 = '".$temp2[1]."'  AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
					$row1 = mysql_fetch_array($rs2);	
					$row2 = mysql_fetch_array($rs3);	
					$adapttime1 = $row1['抵達時間'] + $tolertime;
					$tempInterval = (int)($adapttime1 / $intervalsec);
					$arrivehour1 =  (int)($adapttime1 / 3600);
					$arrivemin1 = (int)(($adapttime1 % 3600) / 60);	
					$adapttime2 = $row2['抵達時間'] + $tolertime;
					$tempInterval1 = (int)($adapttime2 / $intervalsec);
					$arrivehour11 =  (int)($adapttime2 / 3600);
					$arrivemin11 = (int)(($adapttime2% 3600) / 60);
					
					$dataarray[0]=mb_convert_encoding("共乘:<br>".$row1['姓名']."/".$row1['帳號']."<br>".$row2['姓名']."/".$row2['帳號'],'Big5','auto');
					$dataarray[1]=mb_convert_encoding($row1['時段']."<br>".$row2['時段'],'Big5','auto');
			 		$dataarray[2]=mb_convert_encoding("(".$row1['障別'].$row1['telephone'].")-".$row1['上車區域']."".$row1['上車地址']."(".$row1['GETONRemark'].")<br><br>(".$row2['障別'].$row2['telephone'].")-".$row2['上車區域']."".$row2['上車地址']."(".$row2['GETONRemark'].")",'Big5','auto');
			 		$dataarray[3]=mb_convert_encoding($arrivehour1.$arrivemin1."<br>".$arrivehour11.$arrivemin11,'Big5','auto');
			 		$dataarray[4]=mb_convert_encoding($row1['下車區域']."".$row1['下車地址']."(".$row1['OffCarRemark'].")<br><br>".$row2['下車區域']."".$row2['下車地址']."(".$row2['OffCarRemark'].")",'Big5','auto');
					
				}
				else
				{
					$rs2 = mysql_query("SELECT * FROM userrequests WHERE 識別碼 = '$information' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");
					$row1 = mysql_fetch_array($rs2);
					$porint[$i][0]=$row1['sLat'];
					$porint[$i][1]=$row1['sLon'];
					$porint[$i][2]=$row1['eLat'];
					$porint[$i][3]=$row1['eLon'];
					$countpoint=$i;
					$adapttime1 = $row1['抵達時間'] + $tolertime;
					$tempInterval = (int)($adapttime1 / $intervalsec);
					$arrivehour1 =  (int)($adapttime1 / 3600);
					$arrivemin1 = (int)(($adapttime1 % 3600) / 60);
					if($arrivehour1<10)
						$arrivehour1="0".$arrivehour1;
					if($arrivemin1<10)
						$arrivemin1="0".$arrivemin1;
					if($row1['狀態']=="候補")
						$str2="候補/";
					else
						$str2="";
						
						$dataarray[0]=mb_convert_encoding($str2.$row1['姓名']."/".$row1['帳號'],'Big5','auto');
						$dataarray[1]=mb_convert_encoding($row1['時段'],'Big5','auto');	
						$dataarray[2]=mb_convert_encoding("(".$row1['障別'].$row1['telephone'].")-".$row1['上車區域']."".$row1['上車地址']."(".$row1['GETONRemark'].")",'Big5','auto');	
						$dataarray[3]=mb_convert_encoding($arrivehour1.$arrivemin1,'Big5','auto');	
						$dataarray[4]=mb_convert_encoding($row1['下車區域']."".$row1['下車地址']."(".$row1['OffCarRemark'].")",'Big5','auto');	
					
				}
				$Output=$Output.addline($i,$i+4,$dataarray);
			}
		}
		else
		{
			if($i<=12)
				$Output=$Output.addline($i,$i+4,$dataarray);
		}	
	}
	$worktime=$row['worktime'];
	$resttime=$row['resttime1'];
	if($resttime!="未選定")
	{
	$resttime1 = explode(':', $resttime);
	$resthour=floor($resttime1[0]/2);
	if($resttime1[0]%2==0)
		$restmin="00";
		else
		$restmin="30";
		
	$resttime1[0]=$resttime1[0]+1;	
	$resthour1=floor($resttime1[0]/2);
	if($resttime1[0]%2==0)
		$restmin1="00";
		else
		$restmin1="30";	
		
	$resthour2=floor($resttime1[1]/2);
	if($resttime1[1]%2==0)
		$restmin2="00";
		else
		$restmin2="30";
		
	$resttime1[1]=$resttime1[1]+1;	
	$resthour3=floor($resttime1[1]/2);
	if($resttime1[1]%2==0)
		$restmin3="00";
		else
		$restmin3="30";
	$restttime=$resthour.':'.$restmin.'~'.$resthour1.':'.$restmin1.' 與 '.$resthour2.':'.$restmin2.'~'.$resthour3.':'.$restmin3;
	}
	else
	{
		$restttime=$resttime;
	}
	$Output=$Output.$Output1;
	$Output = str_replace("(time)", mb_convert_encoding($restttime,'Big5','auto'), $Output );
	
	echo $Output;
	function addline($index,$irowindex,$data)
	 {
	 	$stringg="
	 	<tr style='mso-yfti-irow:$irowindex;mso-yfti-lastrow:yes;height:17.45pt'>
	 	  <td width=45 colspan=2 valign=top style='width:33.45pt;border:solid windowtext 1.0pt;border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;
  			  padding:0cm 5.4pt 0cm 5.4pt;height:30pt'>
  		  <p class=MsoNormal><b><span lang=EN-US style='font-size:9.0pt;font-family:標楷體;color:black'><o:p>$index</o:p></span></b></p>
  		</td>
  	    <td width=75 valign=top style='width:56.15pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  			mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;padding:0cm 0cm 0cm 0cm;height:30pt'>
  		<p class=MsoNormal><b><span lang=EN-US style='font-size:9.0pt;font-family:標楷體;color:black'><o:p>$data[0]</o:p></span></b></p>
  		</td>
  	  	<td width=57 valign=top style='width:42.85pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  			mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;padding:0cm 0cm 0cm 0cm;height:30pt'>
  		<p class=MsoNormal><b><span lang=EN-US style='font-size:9.0pt;font-family:標楷體;color:black'><o:p>$data[1]</o:p></span></b></p>
  		</td>
  		<td width=151 colspan=2 valign=top style='width:113.35pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  			mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;padding:0cm 0cm 0cm 0cm;height:30pt'>
  		<p class=MsoNormal><b><span lang=EN-US style='font-size:9.0pt;font-family:標楷體;color:black'><o:p>$data[2]</o:p></span></b></p>
 		</td>
 		<td width=57 valign=top style='width:42.7pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  			mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;padding:0cm 0cm 0cm 0cm;height:30pt'>
  		<p class=MsoNormal><b><span lang=EN-US style='font-size:9.0pt;font-family:標楷體;color:black'><o:p>$data[3]</o:p></span></b></p>
  		</td>
  		<td width=161 colspan=4 valign=top style='width:120.7pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  			mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;padding:0cm 0cm 0cm 0cm;height:30pt'>
 	    <p class=MsoNormal><b><span lang=EN-US style='font-size:9.0pt;font-family:標楷體;color:black'><o:p>$data[4]</o:p></span></b></p>
  		</td>
  	    <td width=38 valign=top style='width:28.55pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  			mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;padding:0cm 0cm 0cm 0cm;height:30pt'>
  		<p class=MsoNormal><b><span lang=EN-US style='font-size:9.0pt;font-family:標楷體;color:black'><o:p>&nbsp;</o:p></span></b></p>
  		</td>
  		<td width=38 valign=top style='width:28.15pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  			mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;padding:0cm 0cm 0cm 0cm;height:30pt'>
  		<p class=MsoNormal><b><span lang=EN-US style='font-size:9.0pt;font-family:標楷體;color:black'><o:p>&nbsp;</o:p></span></b></p>
  		</td>
  		<td width=36 valign=top style='width:26.85pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  			mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;padding:0cm 0cm 0cm 0cm;height:30pt'>
  		<p class=MsoNormal><b><span lang=EN-US style='font-size:9.0pt;font-family:標楷體;color:black'><o:p>&nbsp;</o:p></span></b></p>
  		</td>
  		</tr>";
	  return 	$stringg;
	}
	
?>
