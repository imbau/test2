<?php session_start(); ?>
<!--上方語法為啟用session，此語法要放在網頁最前方-->
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<?php
//連接資料庫
//只要此頁面上有用到連接MySQL就要include它
include("Mydbconnect.php");
include("connectInfo.php");
$id = $_POST['identity'];
$pw = $_POST['password'];

//搜尋資料庫資料
$sql = "SELECT * FROM accounts where ID= '$id'";
$result = mysql_query($sql);
$row =mysql_fetch_row($result);

//判斷帳號與密碼是否為空白
//以及MySQL資料庫裡是否有這個會員
if($id != null && $pw != null )
{

        //將帳號寫入session，方便驗證使用者身份
		if($id==$row[1]&&$pw==$row[2])
		{
			mysql_query("INSERT INTO  `onlineuser` ( `ID`,`Password`,`IP`,`Department`)VALUES ('$id','$pw', '$myip','".$row[5]."');");	
			echo '登入成功!';
			if($id=='root1')				
			{
				$host="localhost";
				$user="root";
				$password="Esti168";
				$conn1=mysql_connect($host,"$user","$password"); 
				mysql_select_db("routingarranger1",$conn1);
				mysql_query("SET NAMES 'utf8'");
				mysql_query("INSERT INTO  `onlineuser` ( `ID`,`Password`,`IP`,`Department`)VALUES ('$id','$pw', '$myip','".$row[5]."');");	
				echo "<meta http-equiv=REFRESH CONTENT=1;url=/newroutingarrange/arranger.php>";	
			}
			else
			echo "<meta http-equiv=REFRESH CONTENT=1;url=arranger.php>";
		}
		else
		{
			echo '登入失敗，帳號密碼有誤!';
			echo '<meta http-equiv=REFRESH CONTENT=1;url=login.php>';
		}
	
}
else
{
        echo '請輸入帳號或密碼!';
        echo '<meta http-equiv=REFRESH CONTENT=1;url=login.php>';
}
?>