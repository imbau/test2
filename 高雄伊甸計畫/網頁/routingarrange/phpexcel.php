<?php 
$filename = $_GET["filename"];
include 'C:/AppServ/www/phpexcel/Classes/PHPExcel/IOFactory.php';
$inputFileName = "C:/AppServ/www/routingarrange/upload/".$filename;
$objPHPExcel = PHPExcel_IOFactory::load($inputFileName);
$var1 = $objPHPExcel->getSheet(0)->toArray();
$var1= iconv("java", "UTF-8", json_encode($var1));
echo $var1;
?>