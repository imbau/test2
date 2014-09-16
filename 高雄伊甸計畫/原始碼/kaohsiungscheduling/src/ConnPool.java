import java.sql.*;
import java.util.*;

public class ConnPool 
{
	private Vector<Connection> pool;
	private LinkInfo link = null;
	private int poolSize = 1;
	private static ConnPool instance = null;
	
	private ConnPool()
	{
		init();
	}
	
	private void init()
	{
		pool = new Vector<Connection>(poolSize);
		//readConfig();
		addConnection();
	}
	
	public synchronized void release(Connection conn)
	{
		pool.add(conn);
	}
	
	public synchronized void closePool()
	{
		for(int i = 0; i < pool.size(); i++)
		{
			try
			{
				((Connection)pool.get(i)).close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			pool.remove(i);
		}
	}
	
	public static ConnPool getInstance()
	{
		if(instance == null)
		{
			instance = new ConnPool();
		}
		return instance;
	}
	
	public synchronized Connection getConnection()
	{
		if(pool.size() > 0)
		{
			Connection conn = pool.get(0);
			pool.remove(conn);
			return conn;
		}
		else
		{
			return null;
		}
	}
	
	private void addConnection()
	{
		Connection conn = null;
		for(int i = 0; i < poolSize; i++)
		{
			try
			{
				Class.forName(link.getClassName());
				conn = java.sql.DriverManager.getConnection(link.getDatabaseURL(), link.getID(), link.getPW());
				pool.add(conn);
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void readConfig()
	{
		try
		{
			String path = System.getProperty("user.dir") + "\\dbpool.properties";
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("讀取文件出錯");
		}
	}
	
}
