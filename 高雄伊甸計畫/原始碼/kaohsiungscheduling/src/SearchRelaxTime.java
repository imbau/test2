import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
@WebServlet("/SearchRelaxTime.view")
public class SearchRelaxTime extends HttpServlet
{
	private static final long serialVersionUID = 1L;	
	 defineVariable Variable;//��m�w�q���ܼ�
    /**
     * @throws Exception 
     * @see HttpServlet#HttpServlet()
     */
    public SearchRelaxTime()
    {
        super();    
        // TODO Auto-generated constructor stub
    }

	/**
	 * @throws IOException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse respognse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		// TODO Auto-generated method stub
		//�Ѽƫŧi////////////////////////////////////////////////////////////////	
		double TimeUnit = 0.0;	
		int[] FilterEnable = new int[5];//����Filter�ҰʰѼ�			
		List<reqGroup> requestTable=new ArrayList<reqGroup>();	//�ݨD��		
	    List<carGroup> car = new ArrayList<carGroup>();//������
	    
		////////////////////////////////////////////////////////////////////////////		
		
		try 
		{
			System.out.println("start");
			Variable = new defineVariable();//��l�Ʃw�q�ܼ�				
			Variable.date = request.getParameter("arrangedate");//�ƯZ���
			Variable.time = request.getParameter("arrangetime");//�ƯZ�ɶ�	
			ILF ilf = new ILF(Variable.con,Variable);//��l�ƾ��v��Ʒj�M����	
			for(int i=0;i<Variable.areanum;i++)
				 car.add(new carGroup(Variable.intervalnum));	//��l�ƥq����
			for(int i=0;i<Variable.areanum;i++)
			    requestTable.add(new reqGroup(Variable.intervalnum));//��l�ƹw����H�W���ϰ�	
			
			//////////////////////////////////////////////////////////////////////
									
			//�q��ƮwŪ���Ѽưt�m��
			Variable.rs = Variable.smt.executeQuery("select *from setting");//���o�]�w�Ѽ�
			
			//���o�@���Ѽ�set
			while(Variable.rs.next())
			{
				//�]�w�Ѽ�
				TimeUnit = Variable.rs.getFloat("Time unit");	
				FilterEnable[0] = Variable.rs.getInt("Filter1");
				FilterEnable[1] = Variable.rs.getInt("Filter2");
				FilterEnable[2] = Variable.rs.getInt("Filter3");
				FilterEnable[3] = Variable.rs.getInt("Filter4");
				FilterEnable[4] = Variable.rs.getInt("Filter5");	
				
				DriverTable DriverTable = new DriverTable(0);		   
			    //��l��Ū���w���o��ƪ���		
				ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
				//Ū��request�w����A
				requestTable = input.ReadOrderTable(requestTable,Variable);
				//�N�Ҧ��ݨD��mIndexMap�H�Q�d�ߤW�@�Z�ΤU�@�Z�w���̪���T
			    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>();	
			    //�N�Ҧ��w���̩�Jmap
			    IndexMap=RequestTable.getindexmap(IndexMap,requestTable,Variable);	
				//Ū���q����	
				car=DriverTable.readDrivertable(Variable.con, Variable.date, Variable.time, Variable.smt, car,ilf,IndexMap,Variable,requestTable);	
				if(Variable.errorcode<=-2)
					break;
				resttime(car);
		
				/***************************��s��Ʈw�̪��q���Z����********************************/
				for(int j=0;j<Variable.areanum;j++)
				 {
					 for(int l=0;l<Variable.intervalnum;l++)
					 {
						 for(int k=0;k<car.get(j).getCar(l).size();k++)
						 {
							 car.get(j).getCar(l).get(k).UpdateNode(Variable,car.get(j).getCar(l).get(k).RestTime1,car.get(j).getCar(l).get(k));
						 }
					}
				 }
				break;
				/***************************��s��Ʈw�̪��q���Z����********************************/	
						
			}
			System.out.println("�M��𮧮ɶ�����");	
			
		}
		catch(Exception e)
		{
			for(int j=0;j<Variable.areanum;j++)
			{
				 for(int l=0;l<Variable.intervalnum;l++)
				 {
					 for(int k=0;k<car.get(j).getCar(l).size();k++)
					 {
						 car.get(j).getCar(l).get(k).UpdateNode(Variable,car.get(j).getCar(l).get(k).RestTime1,car.get(j).getCar(l).get(k));
					 }
				}
			}
			Variable.errorcode=-7;
			PrintWriter out = response.getWriter();
			out.println(String.valueOf(Variable.errorcode));
			e.printStackTrace();
		}
		if(Variable.errorcode<=-2)
		{
			
			PrintWriter out = response.getWriter();
			out.println(Variable.errorcode);
			
		}else
		{
			try 
			{
				Variable.smt.close();			
				Variable.con.close();
				requestTable.clear();
				car.clear();			
				System.gc();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			PrintWriter out = response.getWriter();
			out.println("Success");
			
		}
			
		
	}
	 //�M��𮧮ɶ�
  	public List<carGroup> resttime(List<carGroup> DriverList) throws IOException
	{
  		 String resttime = "null";//�Ĥ@�ӥb�p�ɥ𮧮ɶ�
		 String resttime1 = "null";//�ĤG�ӥb�p�ɪ��𮧮ɶ�
		 int midtime=0;//�u�ɤ�����
		for(int j=0;j<Variable.areanum;j++)
		 {
			 for(int l=0;l<Variable.intervalnum;l++)
			 {
				 for(int k=0;k<DriverList.get(j).getCar(l).size();k++)
				 {
					 resttime = "null";//�Ĥ@�ӥb�p�ɥ𮧮ɶ�
					 resttime1 = "null";//�ĤG�ӥb�p�ɪ��𮧮ɶ�
					 
					//�u�ɤp��6�p�ɤ���𮧮ɶ�
					if((DriverList.get(j).getCar(l).get(k).EndTime-DriverList.get(j).getCar(l).get(k).StartTime)<Variable.nonrelax)
					{	
						 //�����T�w�� �����u��4�p��
						 if(Integer.valueOf(DriverList.get(j).getCar(l).get(k).CallNum)==5||Integer.valueOf(DriverList.get(j).getCar(l).get(k).CallNum)==27)	 
						 {	 
							 DriverList.get(j).getCar(l).get(k).RestTime1="31:32";							
						 }
						continue;
					}
					midtime=DriverList.get(j).getCar(l).get(k).halfworktime/1800;
				    //���䤤���ɬq��
					if(DriverList.get(j).getCar(l).get(k).relaxarry.contains(String.valueOf(midtime)))
					{
						resttime=String.valueOf(midtime);
						DriverList.get(j).getCar(l).get(k).relaxarry.remove(String.valueOf(midtime));
					}
					 if(resttime=="null")
						 resttime=Search(DriverList.get(j).getCar(l).get(k).relaxarry,midtime);
					 if(resttime1=="null")
					  resttime1=Search(DriverList.get(j).getCar(l).get(k).relaxarry,midtime);		
				    // DriverList.get(j).getCar(l).get(k).RestTime1=resttime1+":"+resttime;//�g��q�����𮧮ɶ�
					 if(resttime!="null"&&resttime1 !="null")
					 { 
						 if(Integer.valueOf(resttime)>Integer.valueOf(resttime1))
							 DriverList.get(j).getCar(l).get(k).RestTime1=resttime1+":"+resttime;//�g��q�����𮧮ɶ�
						 else
							  DriverList.get(j).getCar(l).get(k).RestTime1=resttime+":"+resttime1;//�g��q�����𮧮ɶ�
					 }
					 else
					 {
						  DriverList.get(j).getCar(l).get(k).RestTime1=resttime+":"+resttime1;//�g��q�����𮧮ɶ�
					 }
				 }
			}
		 }
		
		return DriverList;
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub

	}
	public String Search(List<String> relaxarry,int midtime) 
	{
		 String resttime = "null";
		//�M��W�Z�����ɬq����	
		 for(int  shiftindex =1 ; shiftindex <=7; shiftindex++)
		 {
			 //�����e�j�M
			 if(relaxarry.contains(String.valueOf(midtime-shiftindex)))
			 {
				 resttime=String.valueOf(midtime-shiftindex);
				 relaxarry.remove(String.valueOf(midtime-shiftindex));	
				 break;
			 }//����j�M
			 else  if(relaxarry.contains(String.valueOf(midtime+shiftindex)))
			 {
				 resttime=String.valueOf(midtime+shiftindex);
				 relaxarry.remove(String.valueOf(midtime+shiftindex));
				 break;
			 }
		}		 
		return resttime;		
	}

}
