import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Servlet implementation class RoutingArranger
 */
@WebServlet("/headtailteam.view")
public class headtailteam extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	defineVariable Variable;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public headtailteam()
    {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * 
	 * @throws IOException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		// TODO Auto-generated method stub
		//�Ѽƫŧi////////////////////////////////////////////////////////////////
		
		double TimeUnit = 0.5;
		//int RestTimeInterval = 0;	
		
		List<reqGroup> requestTable=new ArrayList<reqGroup>();	
		List<carGroup> car = new ArrayList<carGroup>();
		//int ILFEnable = -1;	
		////////////////////////////////////////////////////////////////////////////	
		ILF ilf = null;
		int mode = Integer.valueOf(request.getParameter("mode"));	
		try {
			Variable = new defineVariable();
			Variable.date = request.getParameter("arrangedate");
			Variable.time = request.getParameter("arrangetime");	
			Variable.smt2.executeUpdate("UPDATE `arrange_log` SET `progress`=1  WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
			ilf = new ILF(Variable.con,Variable);
			//ilf = new ILF(Variable.con,Variable.date,Variable.time);
			DriverTable DriverTable = new DriverTable(0);
		    for(int i=0;i<Variable.areanum;i++)
				 car.add(new carGroup(Variable.intervalnum));	
		    for(int i=0;i<Variable.areanum;i++)
		    	requestTable.add(new reqGroup(Variable.intervalnum));	
		    //Ū��request�w����A		
			ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
		    requestTable = input.ReadOrderTable(requestTable,Variable);	
			//Ū���q����	
		    car=DriverTable.readDrivertable(car,Variable);	
			List<reqGroup> tailreq=input.gettailreq();	
			if(mode==1)//���`�A���Y�Z�A�Ƨ��Z...
			{
				double startTime,endTime,totTime;
				startTime = System.currentTimeMillis();
				PreProcess PP = new PreProcess(Variable,requestTable,tailreq, car, Variable.tolerableStartTime, Variable.tolerableEndTime, TimeUnit,DriverTable.getcarsize(), ilf);
				PP.Start();	
				endTime = System.currentTimeMillis();
				//���o�{���������ɶ�
				totTime = endTime - startTime;
				System.out.println("Using Time: " + totTime+" ms");
			}
			if(mode==2)//�Ըɱ��Y�Z�Ƨ��Z...
			{  
				CandidatePreProcess PP1 = new CandidatePreProcess(Variable,requestTable,tailreq, car, Variable.tolerableStartTime, Variable.tolerableEndTime, TimeUnit,DriverTable.getcarsize(),ilf);
				PP1.Start();
			}
			requestTable.clear();
			tailreq.clear();
			car.clear();
			requestTable=null;
			ilf = null;
			System.gc();
			
		} catch (Exception e) {
			// TODO �۰ʲ��ͪ� catch �϶�
			e.printStackTrace();
		}
		ilf=null;	
		System.gc();	
		if(Variable.errorcode<=-2)
		{
			PrintWriter out = response.getWriter();
			out.println(Variable.errorcode);
		}	
		else
		{
			PrintWriter out = response.getWriter();
			out.println("Success");
		}
		 
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub

	}

}