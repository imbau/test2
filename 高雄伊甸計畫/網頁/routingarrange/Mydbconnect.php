<?

//*************************************
//MyDbconnect is used to connect to db.
//-for pctc website
//*************************************

$host="localhost";
$user="root";
$password="Esti168";

$conn=mysql_connect($host,"$user","$password"); 
mysql_select_db("routingarranger",$conn);
mysql_query("SET NAMES 'utf8'");

?>