
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ReadExcel
 */
@WebServlet("/Deleterequst.view")
public class Deleterequst extends HttpServlet {
	private static final long serialVersionUID = 1L;  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Deleterequst() {    	
        super();
      
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
	}
    
	/**
	 * @throws IOException 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	{

		// TODO Auto-generated method stub
		LinkInfo linkinfo = new LinkInfo();	
		defineVariable Variable;	
		String carid = null;
		String datainformation= null;	
		carid = request.getParameter("car").trim();   
		datainformation = request.getParameter("information").trim();			
		String[] splitdata= datainformation.split("_");
		String reqdata=null;
		String sql =null;
	    int operationsnum=0;
		 
		try
		{	
			Variable=new defineVariable();
			Variable.date= request.getParameter("date");
			Variable.time= request.getParameter("time");		
			//讀取司機表		    	
			int intervalCount = (int)(24 / 0.5);
	        DriverTable Target=new DriverTable(intervalCount);
	        Target =Target.readsingleDrivertable(Variable.con, Variable.date, Variable.time,Variable.smt,carid,Variable);
		  
	        if(splitdata.length>3)
	        	reqdata=splitdata[2]+"_"+splitdata[3];   
	        else
	        	reqdata=splitdata[2];
	    	PrintWriter writer = response.getWriter();
	       if(deletereq(Target.TimeInterval,reqdata))
	       {
	    	
	    	    sql = "UPDATE userrequests SET arranged = 0 ,Targetdrivers='null' WHERE 識別碼 = '" +  splitdata[2] + "' AND arrangedate = '" + Variable.date +"' AND arrangetime = '" + Variable.time + "'";
				Variable.smt.executeUpdate(sql);	
				 if(splitdata.length>3)
				 {
					  sql = "UPDATE userrequests SET arranged = 0 ,Targetdrivers='null' WHERE 識別碼 = '" +  splitdata[3] + "' AND arrangedate = '" + Variable.date +"' AND arrangetime = '" + Variable.time + "'";
					  Variable.smt.executeUpdate(sql);						 
				 }
				 Variable.rs = Variable.smt.executeQuery("SELECT * FROM `arrange_log` WHERE date = '" + Variable.date +"' AND time = '" + Variable.time + "'");
				if(Variable.rs.next())
			    {
						operationsnum=Variable.rs.getInt("operationsnum");
						if(operationsnum==5)
						{
							operationsnum=1;
						}
						else
						{
							operationsnum++;
						}
					    sql = "UPDATE arrange_log SET operationsnum ="+operationsnum+" WHERE date = '" + Variable.date +"' AND time = '" + Variable.time + "'";
						Variable.smt.executeUpdate(sql);	
			    }
				Target.UpdateNode(Variable,Target.RestTime1,Target);
	    		writer.write("1");	    		
	       }
	       else
	       {
	    	   writer.write("0");
	       }
	   	writer.flush();
		writer.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public  boolean deletereq(String[] TimeInterval,String req) 
	{
		boolean result=false;
		 for(int i = 0 ; i < TimeInterval.length ; i++)
	     {
	        if(req.equals(TimeInterval[i]))
	        {
	        	TimeInterval[i]="未排班";
	        	result=true;	     
	        }
	     }
		return result;
	}
}
