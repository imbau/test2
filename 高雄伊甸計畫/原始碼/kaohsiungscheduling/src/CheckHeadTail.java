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
@WebServlet("/CheckHeadTail.view")
public class CheckHeadTail extends HttpServlet
{
	private static final long serialVersionUID = 1L;
       
	defineVariable Variable;	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckHeadTail()
    {
        super();
        try 
        {
			Variable = new defineVariable();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // TODO Auto-generated constructor stub
    }

	/**
	 * @throws IOException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse respognse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{

		
		List<carGroup> car = new ArrayList<carGroup>();//車輛表
		//紀錄沒有頭尾班的車輛表
		List<String> table = new ArrayList<String>();
		String Output=new String();
		try 
		{
			Variable = new defineVariable();
			Variable.date = request.getParameter("arrangedate");
	    	Variable.time = request.getParameter("arrangetime");	
	    	for(int i=0;i<Variable.areanum;i++)
				 car.add(new carGroup(Variable.intervalnum));	//初始化司機表
	    	DriverTable DriverTable = new DriverTable(0);		
	    	//讀取司機表	
	    	car=DriverTable.readDrivertable(car,Variable);	
	    	for(int area=0;area<Variable.areanum;area++)//全部區域
			{ 				
				for(int timeindex=0;timeindex<Variable.intervalnum;timeindex++)//時間總區間數
				{ 
					for(int index=0;index<car.get(area).getCar(timeindex).size();index++)
					{ 
						if(Variable.CheckStatus(car.get(area).getCar(timeindex).get(index),Variable))
						{
							table.add(car.get(area).getCar(timeindex).get(index).CallNum);
						}
					}
				}
			}
	    	if(table.size()>0)
	    	{	
	    		Output+=table.get(0);
	    		if(table.size()>1)
	    			for(int index=1;index<table.size();index++)
	    			{
	    				Output+=","+table.get(index);
	    			}
	    	}
	    	else
	    	{
	    		Output="null";
	    	}
	    	PrintWriter out = response.getWriter();
			out.println(Output);
		} catch (Exception e)
		{
			// TODO 自動產生的 catch 區塊	
			PrintWriter out = response.getWriter();
			Output="查詢錯誤";
			out.println(Output);			
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
