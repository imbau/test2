import java.sql.*;

public class ProgressUpdate
{
	Connection Con = null;
	public ProgressUpdate()
	{
		try
		{
			LinkInfo linkinfo = new LinkInfo();
			Class.forName("com.mysql.jdbc.Driver");
			Con = DriverManager.getConnection(linkinfo.getDatabaseURL() , linkinfo.getID(), linkinfo.getPW());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public int updatedatabase(int index, int percent,String date,String time)
	{
		int row = -1;
		Statement smt = null;
		try
		{
		  smt = Con.createStatement();
		  smt.executeUpdate("UPDATE progress SET `percent`="+percent+" WHERE  `index`= '"+index+"' and `date`='"+date+"' and `time`='"+time+"'");		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return row;
	}

}
