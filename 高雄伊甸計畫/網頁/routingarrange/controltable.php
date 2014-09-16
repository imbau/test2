<?php
	include("Mydbconnect.php");	
	require('chinese-unicode.php');
	$option = $_GET["option"];
	$arrangetime = "";
	set_time_limit(0);
	$arrangedate =$_GET["date"];
	$arrangetime =$_GET["time"];
	$carclass = mysql_query("SELECT * FROM availablecars WHERE date = '".$arrangedate."'  AND time = '".$arrangetime."'");
	$pdf=new PDF_Unicode();  //調用PDF_Unicode class 	
	$pdf->Open(); 
	$pdf->AddUniGBhwFont('uni'); 
	
	$StationArray=array('新店', '中和','汐止','土城');
	foreach ($StationArray as &$value) 
	{
		$caridrs = mysql_query("SELECT A1.*,A2 .*	FROM arrangedtable A1, availablecars A2 WHERE A1.carid = A2.車號  AND  A2. date= '".$arrangedate."' AND A2.time = '".$arrangetime."' AND  A1. date = '" .$arrangedate."' AND A1.arrangetime = '".$arrangetime."' AND A2.站名='".$value."' ORDER BY A1.worktime ASC");
		$num_rows=mysql_num_rows($caridrs); 
		printtable($carclass,$pdf,$caridrs,$arrangedate,$arrangetime,$num_rows);
	}
	function printtable($carclass,$pdf,$caridrs,$arrangedate,$arrangetime,$num_rows) 
	{ 
		$carclassrow = mysql_fetch_array($carclass);
		$TurnoutDate=preg_split("/-/",$carclassrow['TurnoutDate']);
		$xpoint=2;
		$ypoint=4;
		$xshift=2;	
		$xshift1=6;
		$count=0;
		$Startprinttableindex=0;	
		$pdf->AddPage(); 
		$pdf->SetFont('uni','B',9);
		$xshift=1;	
		$xshift1=4;
		$pdf->Text($xpoint*35,$ypoint+2,$TurnoutDate[0]." 年 ".$TurnoutDate[1]." 月 ".$TurnoutDate[2]."日      駕駛人員行車時段控制表   第     頁");	
		$pdf->SetY($ypoint+3);
		$pdf->SetX($xpoint*$xshift);
		$pdf->Cell($xpoint*$xshift1,$ypoint,'班別',1,0,'C');
		$pdf->Ln();
		$pdf->SetX($xpoint*$xshift);
		$pdf->Cell($xpoint*$xshift1,$ypoint,'站名',1,0,'C');	
		$pdf->Ln();
		$pdf->SetX($xpoint*$xshift);
		$pdf->Cell($xpoint*$xshift1,$ypoint,'姓名',1,0,'C');
		$pdf->Ln();
		$pdf->SetX($xpoint*$xshift);
		$pdf->Cell($xpoint*$xshift1,$ypoint,'車號',1,0,'C');
		$pdf->Ln();
		$pdf->SetX($xpoint*$xshift);
		$pdf->Cell($xpoint*$xshift1,$ypoint,'呼號',1,0,'C');
		$pdf->Ln();
		$pdf->SetX($xpoint*$xshift);
		$pdf->Cell($xpoint*$xshift1,$ypoint,'電話',1,0,'C');
		$smin =0+30;
		$shour =6;
		$smin1 ="30";
		$shour1 ="06";
		$sworktime=$shour*3600+$smin*60;		
		$Startprinttableindex=($sworktime/1800);
		for($timeindex=$Startprinttableindex;$timeindex<($Startprinttableindex+23);$timeindex++)
		{
			$pdf->Ln();
			$pdf->SetX($xpoint*$xshift);
			$pdf->Cell($xpoint*$xshift1,10.5,$shour1. $smin1,1,0,'C');
			$smin+=30;
			if($smin==60)
			{
				$smin=0;
				$smin1="00";
				$shour++;
				if( $shour<10)
					$shour1="0".$shour;
				else
				{
					$shour1=$shour;
				}
			}
		else
			{
				$smin1="30";
			}
		}
		$nightcarflag=0;
		$nightcarflag=0;
		$carindex=0;	
       do
	{
		$pdf->SetY($ypoint+3);	
		if($count>0&&$count<=8)
		{
		$carid=$caridrsrow['carid'];		
		$carclass = mysql_query("SELECT * FROM availablecars WHERE date = '".$arrangedate."'  AND time = '".$arrangetime."' AND `車號`='$carid'");
		$carclassrow = mysql_fetch_array($carclass);
		$carindex++;
		if($carclassrow['班別']=='晚'&&$nightcarflag==0)
		{  
			if($count>1)
			{
				$count=8;	
			}else
			{
				$count=0;
			}
		 $nightcarflag=1;
		 mysql_data_seek($caridrs, $carindex-1);	
		}
	else
	{	
		/*********line1******************/
		$xshift=24;
		if($count==1)
			$xpoint=$pdf->GetX();
		else
			$xpoint=$xpoint+$xshift;	
			$pdf->SetX($xpoint);	
			$pdf->Cell($xshift,$ypoint,$carclassrow['班別'] ,1,0,'C');
			$pdf->Ln();
			$pdf->SetX($xpoint);	
			$pdf->Cell($xshift,$ypoint,$carclassrow['站名'],1,0,'C');	
			$pdf->Ln();
			$pdf->SetX($xpoint);	
			$pdf->Cell($xshift,$ypoint,$carclassrow['drivername'],1,0,'C');	
			$pdf->Ln();
			$pdf->SetX($xpoint);	
			$pdf->Cell($xshift,$ypoint,$carid,1,0,'C');	
			$pdf->Ln();
			$pdf->SetX($xpoint);	
			$pdf->Cell($xshift,$ypoint,$carclassrow['呼號'],1,0,'C');	
			$pdf->Ln();
			$pdf->SetX($xpoint);	
			$pdf->Cell($xshift,$ypoint,$carclassrow['telephone'],1,0,'C');
	   
			$userrun=1;	
			$userrun1=1;	
			$Resttime = preg_split("/:/",$caridrsrow['resttime1']);  
			$Resttime1=$Resttime[0];
			$Resttime2=$Resttime[1];
			$pdf->Ln();	
			for($timeindex=$Startprinttableindex;$timeindex<($Startprinttableindex+23);$timeindex++)
			{		
				$pdf->SetX($xpoint);  
             if($caridrsrow['run'.$userrun]==-1)		
			{
				$userrun++;
			}
			if($timeindex==$caridrsrow['run'.$userrun])
			{
				$pdf->MultiCell($xshift,10.5,"",1,'L',0);
				$userinfo=userinformation ($caridrsrow['user'.$userrun],$arrangedate,$arrangetime);		
				$pdf->Text($xpoint+1,$pdf->GetY()-8,$userinfo[0]);	
				$pdf->Text($xpoint+1,$pdf->GetY()-4,$userinfo[1]);	
				$pdf->Text($xpoint+1,$pdf->GetY()-1,$userinfo[2]);		
				$userrun++;
				}
				else if ($Resttime1==$timeindex||$Resttime2==$timeindex)
				{
					$pdf->MultiCell($xshift,10.5,"休息",1,'C',0);			
				}
				else
				{
					$pdf->MultiCell($xshift,10.5,"",1,'L',0);
				}
			}
		}
		if($count==8&&$carindex<=$num_rows)
		{		
			$pdf->AddPage();
			$count=0;
			$xpoint=2;		
			$xshift=1;	
			$xshift1=4;
			$pdf->Text($xpoint*35,$ypoint+2,$TurnoutDate[0]." 年 ".$TurnoutDate[1]." 月 ".$TurnoutDate[2]."日      駕駛人員行車時段控制表   第     頁");	
			$pdf->SetY($ypoint+3);
			$pdf->SetX($xpoint*$xshift);
			$pdf->Cell($xpoint*$xshift1,$ypoint,'班別',1,0,'C');
			$pdf->Ln();
			$pdf->SetX($xpoint*$xshift);
			$pdf->Cell($xpoint*$xshift1,$ypoint,'站名',1,0,'C');	
			$pdf->Ln();
			$pdf->SetX($xpoint*$xshift);
			$pdf->Cell($xpoint*$xshift1,$ypoint,'姓名',1,0,'C');
			$pdf->Ln();
			$pdf->SetX($xpoint*$xshift);
			$pdf->Cell($xpoint*$xshift1,$ypoint,'車號',1,0,'C');
			$pdf->Ln();
			$pdf->SetX($xpoint*$xshift);
			$pdf->Cell($xpoint*$xshift1,$ypoint,'呼號',1,0,'C');
			$pdf->Ln();	
			$pdf->SetX($xpoint*$xshift);
			$pdf->Cell($xpoint*$xshift1,$ypoint,'電話',1,0,'C');	
			$time = preg_split("/~/",$carclassrow['時段']);  
			$time1 = preg_split("/:/",$time[0]);             
			$smin =$time1[1]+30;
			$shour =$time1[0];
			if( $nightcarflag==1)
			{
				$smin1 ="00";
				$shour1 ="12";
				$smin =0;
				$shour =12;			
			}
			if($smin>=60)
			{
				$smin-=60;
				$shour++;
			}
			if($smin>0&&$smin<30)
			{
				$smin=0;
			}
			else if($smin>30&&$smin<60)
			{
				$smin=30;
			}
			$sworktime=$shour*3600+$smin*60;	
			$Startprinttableindex=($sworktime/1800);
			for($timeindex=$Startprinttableindex;$timeindex<($Startprinttableindex+23);$timeindex++)
			{
				$pdf->Ln();
				$pdf->SetX($xpoint*$xshift);		
				if($smin==0)
					$smin1="00";
				else if($smin==30)
				{
					$smin1="30";
				}
				if ($shour<=9)
				{
					$shour1="0".$shour;
				}else
				{
					$shour1=$shour;
				}
				$pdf->Cell($xpoint*$xshift1,10.5,  $shour1.$smin1,1,0,'C');
				$smin+=30;
				if($smin==60)
				{
				$smin=0;
				$shour++;
				}
			}
		}
	}
	$count++;
	}while($caridrsrow = mysql_fetch_array($caridrs));
} 
	
$pdf->Output('控制總表.pdf', 'I');  
function userinformation ($Usernonumber,$arrangedate,$arrangetime) {
    $userinfoarray =array('', '');
   $tempuser = preg_split("/_/",$Usernonumber); 
	$userrs = mysql_query("SELECT  時段, 上車區域, 上車地址, 下車區域, 下車地址,狀態 FROM userrequests WHERE 識別碼 = '".$Usernonumber."' AND arrangedate = '".$arrangedate."' AND arrangetime = '".$arrangetime."'");	
	$userinfo1 = mysql_fetch_array($userrs );
	if($userinfo1['狀態']=="候補")
		$userinfoarray[0]=$userinfo1['時段']."--後補";	
		else
		$userinfoarray[0]=$userinfo1['時段'];		
		if(mb_strpos( $userinfo1['上車區域'], "隆", 0,'UTF-8' )==1)
			$userinfoarray[1]=mb_substr($userinfo1['上車區域'] ,-10,4, 'BIG-5');	
			else
			$userinfoarray[1]=mb_substr($userinfo1['上車區域'] ,-5,4, 'BIG-5');	
			
		if(mb_strpos( $userinfo1['下車區域'], "隆", 0,'UTF-8' )==1)
			$userinfoarray[2]=mb_substr($userinfo1['下車區域'] ,-10,4, 'BIG-5');	
			else
			$userinfoarray[2]=mb_substr($userinfo1['下車區域'] ,-5,4, 'BIG-5');	
		
	
	
	/*********************處理上車地址**************************************/		
	 if(strpos($userinfo1['上車地址'], '街')==true)
	{
		$tempuseradd = preg_split("/街/",$userinfo1['上車地址']); 		
		$userinfoarray[1]=$userinfoarray[1].$tempuseradd[0].'街';	
	}
	else  if(strpos($userinfo1['上車地址'], '路')==true)
	{
		$tempuseradd = preg_split("/路/",$userinfo1['上車地址']); 
		$userinfoarray[1]=$userinfoarray[1].$tempuseradd[0].'路';			
	}		
	else  if(strpos($userinfo1['上車地址'], '道')==true)
	{
		$tempuseradd = preg_split("/道/",$userinfo1['上車地址']); 
		  $userinfoarray[1]=$userinfoarray[1].$tempuseradd[0].'道';		
	}
	else
	{
		$tempuseradd = preg_split("/\d/",$userinfo1['上車地址']); 
		  $userinfoarray[1]=$userinfoarray[1].$tempuseradd[0];				
	}
	/*********************處理下車地區*********************************/
	 if(strpos($userinfo1['下車地址'], '街')==true)
	{
		$tempuseradd = preg_split("/街/",$userinfo1['下車地址']); 
		$userinfoarray[2]=$userinfoarray[2].$tempuseradd[0].'街';	
	}
	else  if(strpos($userinfo1['下車地址'], '路')==true)
	{
		$tempuseradd = preg_split("/路/",$userinfo1['下車地址']); 
		$userinfoarray[2]=$userinfoarray[2].$tempuseradd[0].'路';			
	}	  
	else  if(strpos($userinfo1['下車地址'], '道')==true)
	{
		$tempuseradd = preg_split("/道/",$userinfo1['下車地址']); 
		 $userinfoarray[2]=$userinfoarray[2].$tempuseradd[0].'道';		
	}
	else
	{
		$tempuseradd = preg_split("/\d/",$userinfo1['下車地址']); 
		  $userinfoarray[2]=$userinfoarray[2].$tempuseradd[0];		
	}
	
	if(count($tempuser)>1)
	{
			$userinfoarray[0]=$userinfoarray[0].'       X2';			
	}
return  $userinfoarray;
}
?>
