import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Servlet implementation class RoutingArranger
 */
@WebServlet("/clear.view")
public class clear extends HttpServlet
{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public clear()
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
		//把计////////////////////////////////////////////////////////////////		
		////////////////////////////////////////////////////////////////////////////
		Connection con = null;
		Statement smt = null,wrismt = null;	
		LinkInfo linkinfo = new LinkInfo();		
		ProgressUpdate proupdate = new ProgressUpdate();
		try
		{
			//弄戈畐
			Class.forName("com.mysql.jdbc.Driver");		
			con = DriverManager.getConnection(linkinfo.getDatabaseURL() , linkinfo.getID(), linkinfo.getPW());
			smt = con.createStatement();		
			wrismt= con.createStatement();	
			String carFileName=new String();
			//////////////////////////////////////////////////////////////////////
			//眖戈畐弄把计皌竚	
			defineVariable Variable=new defineVariable();
			Variable.date = request.getParameter("arrangedate");
			Variable.time = request.getParameter("arrangetime");
			Variable.rs =Variable.smt.executeQuery("SELECT * FROM `arrange_log` WHERE `date`='"+Variable.date+"' AND `time` = '"+Variable.time +"'");
			if(Variable.rs.next())
			{
				carFileName=Variable.rs.getString("VehicleTable").trim();
			}
			smt.executeUpdate("DELETE FROM `availablecars` WHERE `time`='"+Variable.time+"' AND `date`='"+Variable.date+"'");				
		    smt.executeUpdate("DELETE FROM `arrangedtable` WHERE `arrangetime`='"+Variable.time+"' AND `date`='"+Variable.date+"'");
		    DriverTable InsertDriverTable=new DriverTable(Variable.intervalnum);
		    InsertDriverTable.insertDrivertable(Variable,carFileName);
		    ReadInputfromExcel input = new ReadInputfromExcel(Variable);
		    input.inicartable();					
			proupdate.updatedatabase(1, 0,Variable.date,Variable.time);
			proupdate.updatedatabase(2, 0,Variable.date,Variable.time);
			proupdate.updatedatabase(5, 0,Variable.date,Variable.time);
			proupdate.updatedatabase(4, 0,Variable.date,Variable.time);
			proupdate.updatedatabase(6, 0,Variable.date,Variable.time);
			proupdate.updatedatabase(7, 0,Variable.date,Variable.time);
			smt.executeUpdate("UPDATE notdischarged SET Candidate = 0 WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
			smt.executeUpdate("UPDATE notdischarged SET Normal = 0 WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");				
			smt.executeUpdate("UPDATE userrequests SET arranged=-1,Targetdrivers='null' WHERE `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"'");				
			
			smt.close();	
			wrismt.close();
			con.close();
			System.gc();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		PrintWriter out = response.getWriter();
		out.println("Success");	
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub

	}

}
