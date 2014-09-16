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
@WebServlet("/LookupRequest.view")
public class LookupRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;   
    defineVariable Variable;	
    static List<reqGroup> requestTable;	//需求表
	//將所有需求放置IndexMap以利查詢上一班或下一班預約者的資訊
    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>(); 
    //List<carGroup> car = new ArrayList<carGroup>();//車輛表	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LookupRequest() {
    	
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
		try 
		{
			
			String carid = new String() ;
			String orderhour = new String() ;
			String ordermin = new String() ;
			int mode =0;
			int IntervalSec = (int)(0.5 * 3600);
			int StartInterval=-1;
			int startsec =-1;
			int TwoShareUser=0;
			String ShareUser="null";			
			Variable = new defineVariable();
			Variable.date = request.getParameter("arrangedate");
	    	Variable.time = request.getParameter("arrangetime");	
	    	carid = request.getParameter("carid").trim();  
	    	orderhour = request.getParameter("orderhour");
	    	ordermin = request.getParameter("ordermin");
	    	mode = Integer.valueOf(request.getParameter("mode"));
	    	startsec = Integer.valueOf(orderhour) * 3600 + Integer.valueOf(ordermin) * 60;
	        StartInterval=(int)(startsec / IntervalSec);	       
	        DriverTable Target=new DriverTable(Variable.intervalnum);
	        Target =Target.readsingleDrivertable(Variable.con, Variable.date, Variable.time,Variable.smt,carid,Variable);
	        requestTable=new ArrayList<reqGroup>();	//需求表 
	        for(int i=0;i<Variable.areanum;i++)
			    requestTable.add(new reqGroup(Variable.intervalnum));//初始化預約表以上車區域	    	    
	        //初始化讀取預約這資料物件		
			ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
			//讀取request預約表
			requestTable = input.ReadOrderTable(requestTable,Variable);			
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
			//判斷是否找共乘的乘客
			if(mode==1)
			{
				//找出已排入預約者
				ShareUser=Target.TimeInterval[StartInterval];
				String[] temp = ShareUser.split("_");
				if(temp.length>1)
					TwoShareUser=1;
				else
					startsec=IndexMap.get(Integer.valueOf(ShareUser)).OriginTime;//設定與共乘者同時間上車				
			}
			List<Integer> ReqTarget = new ArrayList<Integer>();	
			//如果有共乘就不找尋
			if(TwoShareUser==0)
				ReqTarget=Search(StartInterval,Target,mode,startsec);
			String Response=new String();
			for(int i = 0; i <ReqTarget.size(); i++)
			{
				if(i<ReqTarget.size()-1)
					Response+=ReqTarget.get(i)+",";
				else
					Response+=ReqTarget.get(i);
			}
			Variable=null;
			requestTable=null;
			System.gc();
			PrintWriter out = response.getWriter();
			out.println(Response);	
		} catch (Exception e) {
			// TODO 自動產生的 catch 區塊
			
		}
    	    	
		
	}
	public  List<Integer> Search(int StartInterval,DriverTable Target,int mode ,int startsec) throws IOException
	{
		List<Integer> ReqTarget = new ArrayList<Integer>();
		
		for(int index=0;index<Variable.areanum;index++)
		 {
			for(int timeindex=StartInterval;timeindex<=StartInterval+2;timeindex++)			
			{ 
			  for(int k=0;k<requestTable.get(index).getreq(timeindex).size();k++)
				{
				  if(mode==0)
				  {
					  if(!requestTable.get(index).getreq(timeindex).get(k).Arrange)
						  ReqTarget.add(requestTable.get(index).getreq(timeindex).get(k).Number);
				  }
				  else
				  {
					  if(!requestTable.get(index).getreq(timeindex).get(k).Arrange
							  &&requestTable.get(index).getreq(timeindex).get(k).OriginTime<=(startsec+Variable.tolerableShareTime)
							  &&requestTable.get(index).getreq(timeindex).get(k).OriginTime>=(startsec-Variable.tolerableShareTime))
						  ReqTarget.add(requestTable.get(index).getreq(timeindex).get(k).Number);
				  }
				}
			}
		 }
		return ReqTarget;
		
	}
}
