import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;


public class LinkInfo
{
	private static String LinkURL = null;
	private String LinkURLWithPort = null;
	private String ID = null;
	private String PW = null;
	private String DatabaseLink = null;
	private String UploadLink = null;
	private String DownloadLink = null;
	private String driverClassName = null;
	private static String HostAddress = null;
	
	public LinkInfo()
	{
        InetAddress address;
		try 
		{
			address = InetAddress.getLocalHost();
			//appserv server ip
			LinkURL = "http://"+ address.getHostAddress();								
			
			//servlet server ip with port
			LinkURLWithPort = "http://"+ address.getHostAddress();		
			
			HostAddress=address.getHostAddress();
			
			//��Ʈwurl
			DatabaseLink = "jdbc:mysql://localhost/kaohsiungscheduling?useUnicode=true&characterEncoding=Big5";		
			driverClassName = "com.mysql.jdbc.Driver";
			//��Ʈw�n�JID
			ID = "root";				
			
			//��Ʈw�n�J�v���K�X
			PW = "Esti168";		
			
			//excel��ƤW�Ǹ��|�A�e����appserv���w�˸��|
			UploadLink = "C:/AppServ/www/kaohsiungscheduling/upload/";
			//excel��ƤW�Ǹ��|�A�e����appserv���w�˸��|
			DownloadLink = "C:/AppServ/www/kaohsiungscheduling/download/";
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     
		
		
	}
	public String getIpAddr(HttpServletRequest request) 
	{      
	       String ip = request.getHeader("x-forwarded-for");      
	      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
	          ip = request.getHeader("Proxy-Client-IP");      
	      }      
	      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
	          ip = request.getHeader("WL-Proxy-Client-IP");      
	       }      
	     if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
	           ip = request.getRemoteAddr();      
	      }      
	     return ip;      
	}  
	public static  String GetHostAddress()
	{
		return HostAddress;
	}
	
	public static  String getURL()
	{
		return LinkURL;
	}
	public String getURLWithPort()
	{
		return LinkURLWithPort;
	}
	public String getID()
	{
		return ID;
	}
	public String getPW()
	{
		return PW;
	}
	public String getDatabaseURL()
	{
		return DatabaseLink;
	}
	public String getUploadLink()
	{
		return UploadLink;
	}
	public String getDownloadLink()
	{
		return DownloadLink;
	}
	
	public String getClassName()
	{
		return driverClassName;
	}
}
