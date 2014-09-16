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
	$caridrs = mysql_query("SELECT * FROM userrequests WHERE  arranged<=0 AND 狀態!='候補' AND arrangedate = '".$arrangedate."'  AND arrangetime = '".$arrangetime."'");
	$num_rows = mysql_num_rows($caridrs);	
	$xpoint=2;
	$ypoint=4;
	$xshift=2;	
	$xshift1=6;
	$count=0;
	$Startprinttableindex=0;
	$pdf=new PDF_Unicode();  //調用PDF_Unicode class 
	$pdf->Open(); 
	$pdf->AddPage(); 
	$pdf->AddUniGBhwFont('uni'); 
	$pdf->SetFont('uni','B',8);	
	$xshift=1;	
	$xshift1=4;
	$size=5;
	$pdf->Text($xpoint*35,$ypoint+2,"年       月         日          預約需求表");	
	$pdf->SetX(1);
	$pdf->Cell($size*1.5,10,'車號',1,0,'C');
	$pdf->Cell($size*3,10,'狀態',1,0,'C');
	$pdf->Cell($size*3,10,'共乘意願',1,0,'C');
	$pdf->Cell($size*3,10,'姓名',1,0,'C');
	$pdf->Cell($size*2.5,10,'帳號',1,0,'C');
	$pdf->Cell($size*5,10,'障別',1,0,'C');
	$pdf->Cell($size*2,10,'時段',1,0,'C');
	$pdf->Cell($size*4,10,'上車區域',1,0,'C');
	$pdf->Cell($size*6.5,10,'上車地址',1,0,'C');
	$pdf->Cell($size*4,10,'下車區域',1,0,'C');
	$pdf->Cell($size*7,10,'下車地址',1,1,'C');
	 $caridrsrow = mysql_fetch_array($caridrs);
	do
	{
	$pdf->SetX(1);
    $Callsign = mysql_query("SELECT 呼號 FROM availablecars WHERE date = '".$arrangedate."'  AND time = '".$arrangetime."' AND 車號='".$caridrsrow['Targetdrivers']."'");
	 $Callsignrow = mysql_fetch_array($Callsign);
	 if($caridrsrow['Targetdrivers']=="null")
	 	$pdf->Cell($size*1.5,10,'  ',1,0,'C');
	 else
		$pdf->Cell($size*1.5,10,$Callsignrow['呼號'],1,0,'C');
	$pdf->Cell($size*3,10,$caridrsrow['狀態'],1,0,'C');
	$pdf->Cell($size*3,10,$caridrsrow['共乘意願'],1,0,'C');
	$pdf->Cell($size*3,10,$caridrsrow['姓名'],1,0,'C');
	$pdf->Cell($size*2.5,10,$caridrsrow['帳號'],1,0,'C');	
	$pdf->Cell($size*5,10, $caridrsrow['障別'],1,0,'C');
	$pdf->Cell($size*2,10,$caridrsrow['時段'],1,0,'C');
	$pdf->Cell($size*4,10,$caridrsrow['上車區域'],1,0,'C');
	$pdf->Cell($size*6.5,10, $caridrsrow['上車地址'],1,0,'C');
	$pdf->Cell($size*4,10,$caridrsrow['下車區域'],1,0,'C');
	$pdf->Cell($size*7,10,$caridrsrow['下車地址'],1,1,'C');
	
	}while($caridrsrow = mysql_fetch_array($caridrs));
	$pdf->Output('預約需求表.pdf', 'I');  
?>
