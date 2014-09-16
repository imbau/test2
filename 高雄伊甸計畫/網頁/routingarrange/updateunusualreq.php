<head>
  <meta name="generator" content="HTML Tidy for HTML5 (experimental) for Windows https://github.com/w3c/tidy-html5/tree/c63cc39" />
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
  </head>  
<?php
	include("Mydbconnect.php");
	include("connectInfo.php");

	$sql = $_POST['dataString'];
	$sql1 = $_POST['reqdata'];
	$sql2 = $_POST['cardata'];
	$sql3 = $_POST['writeSpecialcartime'];
	$sql4= $_POST['writeSpecialcartime1'];
	$Specialcar = $_POST['Specialcar'];
	$sql = str_replace("\\","", $sql);	
	$sql1= str_replace("\\","", $sql1);	
	$sql2= str_replace("\\","", $sql2);	
	$sql3= str_replace("\\","", $sql3);	
	$sql4= str_replace("\\","", $sql4);
	
	if($Specialcar==1)
	{
		$Specialcarrs =mysql_query($sql2 );			
		$Specialcardata=mysql_fetch_array($Specialcarrs);	
		 $time=preg_split("/~/",$Specialcardata[8]);  		
		 $time1 = preg_split("/:/",$time[1]);  
		 $ehour= $time1[0];
		 $emin= $time1[1];		 
		 if($emin>=30)
		 {
			$emin-=30;		
		 }
		 else
		 {
			$emin+=60;
			$ehour--;
			$emin-=30;	
		 }
		if($ehour<9)
			$ehour="0".$ehour;
		if($emin<9)
			$emin="0".$emin;
		$sql5=$sql3.$time[0]."~".$ehour.":".$emin.$sql4;
		mysql_query($sql5 );	
	}
	$myFile = "testFile.txt";
    $fh = fopen($myFile, 'w') or die("can't open file");
    fwrite($fh, $sql );
    fclose($fh);
	
  mysql_query($sql );	
 mysql_query($sql1);
?>
