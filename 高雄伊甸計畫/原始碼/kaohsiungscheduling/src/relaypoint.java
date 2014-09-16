import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class cararranger
 */
@WebServlet("/relaypoint.view")
public class relaypoint extends HttpServlet {
	private static final long serialVersionUID = 1L;
    defineVariable Variable;	
	//將所有需求放置IndexMap以利查詢上一班或下一班預約者的資訊
    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>(); 
    //List<carGroup> car = new ArrayList<carGroup>();//車輛表	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public relaypoint() {
    	
    	super();
    	     
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @throws IOException 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		List<reqGroup> requestTable;	//需求表
	    List<reqGroup> tailrequestTable;	//需求表
		try 
		{
			String carid = new String() ;
			String orderhour = new String() ;
			String ordermin = new String() ;
			String nextonestartarea = null,previousoneendarea = null;
			int IntervalSec = (int)(0.5 * 3600);
			int StartInterval=-1;
			int startsec =-1;
			int mode=1;
			int flag=0;
			Variable = new defineVariable();
			Variable.date = request.getParameter("arrangedate");
	    	Variable.time = request.getParameter("arrangetime");	
	    	carid = request.getParameter("carid").trim();  
	    	orderhour = request.getParameter("orderhour");
	    	ordermin = request.getParameter("ordermin");
	    	mode=Integer.valueOf(request.getParameter("mode"));
	    	
	    	startsec = Integer.valueOf(orderhour) * 3600 + Integer.valueOf(ordermin) * 60;
	        StartInterval=(int)(startsec / IntervalSec);	       
	        DriverTable Target=new DriverTable(Variable.intervalnum);
	        Target =Target.readsingleDrivertable(Variable.con, Variable.date, Variable.time,Variable.smt,carid,Variable);
	        requestTable=new ArrayList<reqGroup>();	//需求表 
	        tailrequestTable=new ArrayList<reqGroup>();	//需求表 
	        for(int i=0;i<Variable.areanum;i++)
			    requestTable.add(new reqGroup(Variable.intervalnum));//初始化預約表以上車區域
	        for(int i=0;i<Variable.areanum;i++)
	        	tailrequestTable.add(new reqGroup(Variable.intervalnum));//初始化預約表以下車區域
	       
	        //初始化讀取預約這資料物件		
			ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
			//讀取request預約表
			requestTable = input.ReadOrderTable(requestTable,Variable);	
			tailrequestTable=input.ReadEndTable();
		    //將所有預約者放入map
			 for(int j=0;j<Variable.areanum;j++)
			 { 				
				 for(int l=0;l<Variable.intervalnum;l++)
				 { 
					 for(int k=0;k<requestTable.get(j).getreq(l).size();k++)
					 {
					  IndexMap.put(requestTable.get(j).getreq(l).get(k).Number,requestTable.get(j).getreq(l).get(k));	
					 }
				}
			}
		if(startsec>(Target.StartTime+1800)&&startsec<(Target.EndTime+2700))
		{
			for(int i = StartInterval-1; i >= 0; i--)
			{
				
				if(!(Target.TimeInterval[i].indexOf("未排班")!=-1||Target.TimeInterval[i].indexOf("不上班")!=-1))
				{   
					String[] testnumber =Target.TimeInterval[i].split("_");
					previousoneendarea=IndexMap.get(Integer.valueOf(testnumber[0])).Destinationarea;						
					break;
				}
				else if(i==0)
				{
					flag=1;
				}
			}
			if(flag==0)
				for(int i = StartInterval+1; i <Target.TimeInterval.length; i++)
				{
					if(!(Target.TimeInterval[i].indexOf("未排班")!=-1||Target.TimeInterval[i].indexOf("不上班")!=-1))
					{
						String[] testnumber =Target.TimeInterval[i].split("_");
						nextonestartarea=IndexMap.get(Integer.valueOf(testnumber[0])).Originarea;					
						break;
					}
					else if(i==Target.TimeInterval.length-1)
					{
						flag=1;
					}
				}
		
			List<Integer> supportarea = new ArrayList<Integer>();	
			List<Integer> ReqTarget = new ArrayList<Integer>();	
			if(flag==0)
			{
				if(mode==1)
				{
					areaFilter1(previousoneendarea,supportarea,Target.station);
				}
				else
				{
					areaFilter1(nextonestartarea,supportarea,Target.station);
				}
				ReqTarget=Search(requestTable,tailrequestTable,mode,supportarea,StartInterval,previousoneendarea,nextonestartarea,Target);
			}
			String Response=new String();
			for(int i = 0+1; i <ReqTarget.size(); i++)
			{
				if(i<ReqTarget.size()-1)
					Response+=ReqTarget.get(i)+",";
				else
					Response+=ReqTarget.get(i);
			}
			
			PrintWriter out = response.getWriter();
			out.println(Response);	
		}
		else
		{
			response.setCharacterEncoding("utf-8");  
			PrintWriter out = response.getWriter();			
			out.println("點選錯誤時間");	
		}
			
			
		} catch (Exception e) {
			// TODO 自動產生的 catch 區塊
			response.setCharacterEncoding("utf-8");  
			PrintWriter out = response.getWriter();			
			out.println("發生錯誤");	
		}
    	    	
		requestTable=null;
		tailrequestTable=null;
	}
	public  List<Integer> Search(List<reqGroup> requestTable,List<reqGroup> tailrequestTable,int mode,List<Integer> supportarea,int StartInterval,String area,String area1,DriverTable Target) throws IOException
	{
		List<Integer> ReqTarget = new ArrayList<Integer>();
		
		for(int index=0;index<supportarea.size();index++)
		 { 		
			
			if(mode==1)
			{
				for(int timeindex=StartInterval;timeindex<=StartInterval+2;timeindex++)			
				{ 
					for(int k=0;k<requestTable.get(supportarea.get(index)).getreq(timeindex).size();k++)
					{
						if(defineVariable.Weight[defineVariable.switchareaindex(requestTable.get(supportarea.get(index)).getreq(timeindex).get(k).Destinationarea)][defineVariable.switchareaindex(area1)]!=9)
						{  
							boolean flag=false;
							if(requestTable.get(supportarea.get(index)).getreq(timeindex).get(k).Car.equals(""))//大車的預約者可以選擇大小車
								flag=true;
							else
							{
								if(requestTable.get(supportarea.get(index)).getreq(timeindex).get(k).Car.equals(Target.Car))//如果是小車的預約者只能選擇小車不能選大車
								flag=true;
							 }	
							if(!requestTable.get(supportarea.get(index)).getreq(timeindex).get(k).Arrange&&flag)
							{ 
								ReqTarget.add(requestTable.get(supportarea.get(index)).getreq(timeindex).get(k).Number);
							}
						}
					}
				}
			}else 
			{
			 for(int timeindex=StartInterval;timeindex>=StartInterval-2;timeindex--)			
			  { 
				for(int k=0;k<tailrequestTable.get(supportarea.get(index)).getreq(timeindex).size();k++)
				{
					if(defineVariable.Weight[defineVariable.switchareaindex(tailrequestTable.get(supportarea.get(index)).getreq(timeindex).get(k).Originarea)][defineVariable.switchareaindex(area)]!=9)				 
						{  
							boolean flag=false;
							if(tailrequestTable.get(supportarea.get(index)).getreq(timeindex).get(k).Car.equals(""))//大車的預約者可以選擇大小車
								flag=true;
							else
							{
								if(tailrequestTable.get(supportarea.get(index)).getreq(timeindex).get(k).Car.equals(Target.Car))//如果是小車的預約者只能選擇小車不能選大車
								flag=true;
							 }	
							if(!tailrequestTable.get(supportarea.get(index)).getreq(timeindex).get(k).Arrange&&flag)
							{ 	
								ReqTarget.add(tailrequestTable.get(supportarea.get(index)).getreq(timeindex).get(k).Number);
							}
						}
				}
			 }
			}
		}
		return ReqTarget;
		
	}
	//區域Filter1 與原本的區域Filter功能差別在可置換夜班Weight
	public  void areaFilter1(String area,List<Integer> supportarea,String area1) throws IOException
	{
		for(int x=0;x<Variable.areanum;x++)
		{ 
			if(defineVariable.Weight[defineVariable.switchareaindex(area)][x]!=9)
			{
				supportarea.add(x);
			}
		}
		for(int index = 0; index<supportarea.size();)
		{
			if(defineVariable.Weight[defineVariable.switchareaindex(area1)][supportarea.get(index)]==9)
			{
				supportarea.remove(index);
			}
			else
			{
				 index++;
			}
		}
		
	}
}
