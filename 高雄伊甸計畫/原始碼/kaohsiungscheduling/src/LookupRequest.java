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
    static List<reqGroup> requestTable;	//�ݨD��
	//�N�Ҧ��ݨD��mIndexMap�H�Q�d�ߤW�@�Z�ΤU�@�Z�w���̪���T
    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>(); 
    //List<carGroup> car = new ArrayList<carGroup>();//������	
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
	        requestTable=new ArrayList<reqGroup>();	//�ݨD�� 
	        for(int i=0;i<Variable.areanum;i++)
			    requestTable.add(new reqGroup(Variable.intervalnum));//��l�ƹw����H�W���ϰ�	    	    
	        //��l��Ū���w���o��ƪ���		
			ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
			//Ū��request�w����
			requestTable = input.ReadOrderTable(requestTable,Variable);			
		    //�N�Ҧ��w���̩�Jmap
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
			//�P�_�O�_��@��������
			if(mode==1)
			{
				//��X�w�ƤJ�w����
				ShareUser=Target.TimeInterval[StartInterval];
				String[] temp = ShareUser.split("_");
				if(temp.length>1)
					TwoShareUser=1;
				else
					startsec=IndexMap.get(Integer.valueOf(ShareUser)).OriginTime;//�]�w�P�@���̦P�ɶ��W��				
			}
			List<Integer> ReqTarget = new ArrayList<Integer>();	
			//�p�G���@���N����M
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
			// TODO �۰ʲ��ͪ� catch �϶�
			
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
