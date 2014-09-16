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

		
		List<carGroup> car = new ArrayList<carGroup>();//������
		//�����S���Y���Z��������
		List<String> table = new ArrayList<String>();
		String Output=new String();
		try 
		{
			Variable = new defineVariable();
			Variable.date = request.getParameter("arrangedate");
	    	Variable.time = request.getParameter("arrangetime");	
	    	for(int i=0;i<Variable.areanum;i++)
				 car.add(new carGroup(Variable.intervalnum));	//��l�ƥq����
	    	DriverTable DriverTable = new DriverTable(0);		
	    	//Ū���q����	
	    	car=DriverTable.readDrivertable(car,Variable);	
	    	for(int area=0;area<Variable.areanum;area++)//�����ϰ�
			{ 				
				for(int timeindex=0;timeindex<Variable.intervalnum;timeindex++)//�ɶ��`�϶���
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
			// TODO �۰ʲ��ͪ� catch �϶�	
			PrintWriter out = response.getWriter();
			Output="�d�߿��~";
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
