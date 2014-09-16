import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Servlet implementation class RoutingArranger
 */
@WebServlet("/changedriver.view")
public class changedriver extends HttpServlet
{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public changedriver()
    {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse respognse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		//�Ѽƫŧi////////////////////////////////////////////////////////////////		
		////////////////////////////////////////////////////////////////////////////
		Connection con = null;
		Statement smt = null,smt1=null,smt2=null;	
		LinkInfo linkinfo = new LinkInfo();		
	    String carid="-1";
	    String callnum="-1";
	    int mode=-1;	  
	    double TimeUnit =0.5;
		int RestTimeInterval =-5;
		try
		{
			//Ū����Ʈw
			defineVariable Variable=new defineVariable();
			Class.forName("com.mysql.jdbc.Driver");		
			con = DriverManager.getConnection(linkinfo.getDatabaseURL() , linkinfo.getID(), linkinfo.getPW());
			smt = con.createStatement();	
			smt1 = con.createStatement();	
			smt2 = con.createStatement();	
			carid= request.getParameter("carid");
			mode=  Integer.valueOf(request.getParameter("Action"));
			String date = request.getParameter("arrangedate");
			String time = request.getParameter("arrangetime");	
			String[] driverindex = carid.split(",");
			if(mode==1)
			{
				for(int index=0;index<driverindex.length;index++)
				{
					
					ResetReq(driverindex[index],date,time,con);
					smt.execute("delete  FROM `arrangedtable` WHERE `carid`='"+driverindex[index]+"' and `arrangetime`='"+time+"' and  `date`='"+date+"'");
					smt.execute("delete  FROM `availablecars` WHERE `����`='"+driverindex[index]+"' and `time`='"+time+"' and  `date`='"+date+"'");
				}
				
				PrintWriter out = response.getWriter();
				out.println("Success");	
			}
			else if(mode==0)
			{
				ResultSet rs,rs1;
				for(int index=0;index<driverindex.length;index++)
				{
					String temp = "";					
					String carInsertSQL = "insert into availablecars (���W, �I��,telephone,drivername,����, �Z�O, ����, �ɬq, �a�}, ���W,date,time,TurnoutDate) values ('";
					
					rs=smt.executeQuery("SELECT * FROM `car` WHERE `carid`='"+driverindex[index]+"'");
					if(rs.next())
					{
						temp=temp+carInsertSQL;						
						rs.first();
						temp+=rs.getString("station").trim();	
						temp+="','"+rs.getString("callnumber");
						temp+="','"+rs.getString("telephone").trim();
						temp+="','"+rs.getString("drivername").trim();
						temp+="','"+rs.getString("carid").trim();
						temp+="','"+rs.getString("shift").trim();
						temp+="','"+rs.getString("cartype").trim();
						temp+="','"+rs.getString("worktime").trim();
						temp+="','"+rs.getString("address").trim();
						temp+="','"+rs.getString("parkname").trim();
						temp+="','"+date;
						temp+="','"+time;					
						rs1=smt1.executeQuery("SELECT * FROM `arrange_log` WHERE `time`='"+time+"' and  `date`='"+date+"'");
						if(rs1.next())
						{
							temp+="','"+rs1.getString("TurnoutDate").trim()+"')";
							smt2.execute(temp);			
							ReadInputfromExcel input = new ReadInputfromExcel(Variable);
							DriverTable Driver =new DriverTable(Variable.intervalnum);								
							Driver=input.buildDriverNode(TimeUnit,RestTimeInterval,rs);
							Driver.PrintNode(con,date, time, "arrangedtable");							
							PrintWriter out = response.getWriter();
							out.println("Success");	
						}
						else
						{
							PrintWriter out = response.getWriter();
							out.println("fail");	
						}
					}
					else
					{
						PrintWriter out = response.getWriter();
						out.println("fail");	
					}
				}
			}else if(mode==2)
			{
				ResultSet rs,rs1;
				callnum=request.getParameter("callnum");
				rs1=smt2.executeQuery("SELECT * FROM `availablecars` WHERE `�I��`='"+callnum+"'and `time`='"+time+"' and  `date`='"+date+"'");
			    if(rs1.next())
				{
					PrintWriter out = response.getWriter();
					out.println("0");	
			    }
			   else
			   {
				   rs=smt.executeQuery("SELECT * FROM `car` WHERE `callnumber`='"+callnum+"'");
					if(rs.next())
					{
						String updatecarSQL="UPDATE arrangedtable SET `carid`='"+rs.getString("carid").trim()+"',`cartype`='"+rs.getString("cartype").trim()+"' WHERE `carid`='"+carid+"' and `arrangetime`='"+time+"' and  `date`='"+date+"'";
						smt1.execute(updatecarSQL);
						updatecarSQL="UPDATE availablecars SET ���W='"+rs.getString("station").trim()+"',�I��='"+callnum+"',telephone='"+rs.getString("telephone").trim()+"',drivername='"+rs.getString("drivername").trim()
								  +"',�Z�O='"+rs.getString("shift").trim()+"',`����`='"+rs.getString("carid").trim()+
								  "',`����`='"+rs.getString("cartype").trim()+"',`�a�}`='"+rs.getString("address").trim()+"',`���W`='"+rs.getString("parkname").trim()+
								  "' WHERE `����`='"+carid+"' and `time`='"+time+"' and  `date`='"+date+"'";
						smt1.execute(updatecarSQL);
						PrintWriter out = response.getWriter();
						out.println("1");	
					}
					else
					{
						PrintWriter out = response.getWriter();
						out.println("2");	
					}
			  }
			}
			else
			{
				PrintWriter out = response.getWriter();
				out.println("fail");	
			}
			smt.close();
			con.close();
			System.gc();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			PrintWriter out = response.getWriter();
			out.println("fail");	
		}
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub

	}
	public void ResetReq(String carid,String date,String time,Connection con)		
	{
		ResultSet rs;
		Statement smt = null,smt1 = null;
	
		
		String presql="UPDATE userrequests SET Targetdrivers='null' , `arranged`=-2 WHERE `arrangedate`='"+date+"' and `arrangetime`='"+time+"'";		
		String sql="";
		try 
		{ 
			smt=con.createStatement();	
			smt1=con.createStatement();	
			rs = smt.executeQuery("SELECT * FROM `arrangedtable` WHERE `carid`='"+carid+"' and `arrangetime`='"+time+"' and  `date`='"+date+"'");
			rs.first();
			do
			{			
				//�^�_�������A				
				for(int i = 1; i < 17; i++)
				{
					//Ū���Z����ƨæ^�_Node����timeinterval
					if(rs.getInt("run" + String.valueOf(i)) != -1&&rs.getInt("run" + String.valueOf(i)) != 0)
					{  
						String information = rs.getString("user"+ String.valueOf(i));
						String[] testnumber = information.split("_");
						sql=presql+"and �ѧO�X='"+testnumber[0]+"'";
						smt1.execute(sql);	
						if(testnumber.length>1)
						{
							sql=presql+"and �ѧO�X='"+testnumber[1]+"'";
							smt1.execute(sql);	
						}
					}
				}			
			}while(rs.next());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
