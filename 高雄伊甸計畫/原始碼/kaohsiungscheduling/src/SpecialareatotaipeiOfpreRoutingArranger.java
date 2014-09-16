import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
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

import jxl.read.biff.BiffException;
/**
 * Servlet implementation class RoutingArranger
 */
@WebServlet("/Specialareatotaipei.view")
public class SpecialareatotaipeiOfpreRoutingArranger extends HttpServlet
{
	private static final long serialVersionUID = 1L;	
	 defineVariable Variable;//��m�w�q���ܼ�
    /**
     * @throws Exception 
     * @see HttpServlet#HttpServlet()
     */
    public SpecialareatotaipeiOfpreRoutingArranger()
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
		List<reqGroup> tailrequestTable=new ArrayList<reqGroup>();	//�ݨD��
	    List<carGroup> car = new ArrayList<carGroup>();//������
	    
		////////////////////////////////////////////////////////////////////////////		
		ILF ilf = null;
		try {
			Variable = new defineVariable();//��l�Ʃw�q�ܼ�				
			Variable.date = request.getParameter("arrangedate");//�ƯZ���
			Variable.time = request.getParameter("arrangetime");//�ƯZ�ɶ�		
			Variable.smt2.executeUpdate("UPDATE progress SET percent =0 WHERE `index` =7 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
			Variable.smt2.executeUpdate("UPDATE `arrange_log` SET `progress`=2  WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
			//ilf = new ILF(Variable.con,Variable.date,Variable.time);//��l�ƾ��v��Ʒj�M����	
			ilf = new ILF(Variable.con,Variable);//��l�ƾ��v��Ʒj�M����	
			for(int i=0;i<Variable.areanum;i++)
				 car.add(new carGroup(Variable.intervalnum));	//��l�ƥq����
			for(int i=0;i<Variable.areanum;i++)
			    requestTable.add(new reqGroup(Variable.intervalnum));//��l�ƹw����H�W���ϰ�
			for(int i=0;i<Variable.areanum;i++)
				tailrequestTable.add(new reqGroup(Variable.intervalnum));//��l�ƹw����H�U���ϰ찵����
			
			
			

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
				//Ū��request�w����A
				tailrequestTable =input.ReadEndTable();
				
				//�N�Ҧ��ݨD��mIndexMap�H�Q�d�ߤW�@�Z�ΤU�@�Z�w���̪���T
			    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>();			   
			    Map<String, DriverTable> carIndexMap = new HashMap<String, DriverTable>();
			    //�N�Ҧ��w���̩�Jmap
			    IndexMap=RequestTable.getindexmap(IndexMap,requestTable,Variable);		
						
				//Ū���q����	
				 car=DriverTable.readDrivertable(Variable.con, Variable.date, Variable.time, Variable.smt, car,ilf,IndexMap,Variable,requestTable);	
				 if(Variable.errorcode<=-2)
						break;
				 //�N�Ҧ��q����Jmap �H�Q�d��
				 carIndexMap=DriverTable.GetcarIndexMap(carIndexMap,car,Variable);
				//����filter					
				Variable.recentPercent=0;//�ƯZ�i�׭��k�s
				//int count=0;	//�p��B�z�L���w���̲έp��				//Ū���W���Ȧ�ɶ�array
				//Ū���T�a�L�f���U���a�I�b�x�_���ݨD 1�N��Ū��defineVariable�̪�Specialeara array�Ĥ@��
				List<RequestTable> Specialearaarray1 =RequestTable.getSpecialareatotaiperarray(1,requestTable,Variable); 		    		
			    //0�N��Ū��defineVariable�̪�Specialearacar array�Ĥ@��Ū���g�����M���l��index
				//��mcarindex�n�hŪ���Y�Ӱϰ쪺���lindex				
				Specialearaarray1=Southwestprocess(car,FilterEnable,ilf,IndexMap,Specialearaarray1,TimeUnit,carIndexMap,DriverTable);
				//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
				if(Variable.errorcode<=-2)
					 break;	
				
				/***************************�g�X�q�������Z��********************************/
			   /* File file = new File("C:/AppServ/www/routingarrange/log/�q���Z��.txt");
				file.delete();		
				debug=new debug("�q���Z��","txt",1);				
				debug.printDriverTable(car);
				debug.fileclose();*/
				/***************************���ե\��********************************/
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
				checkdata();
				break;
				/***************************��s��Ʈw�̪��q���Z����********************************/	
						
			}
			System.out.println(":�u���ϰ쩹�x�_�ƯZ����:");	
			
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
			//���槹�ƯZ�g�^�i��100
			try 
			{
				Variable.smt2.executeUpdate("UPDATE progress SET percent =100 WHERE `index` =7 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
				ilf=null;
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
	//�S��ϰ쪺filterprocess :�T�a�L�f�g���U���b�g��
	public boolean Specialearafilterprocess(List<DriverTable> filterDriverTable,RequestTable Req
				,double TimeUnit,Map<Integer, RequestTable> IndexMap,ILF ilf,int[] FilterEnable,List<carGroup> car) throws Exception
	{		
		boolean Found=false;
		DriverTable TargetDriver=null;
		for(int Classification=1;Classification<=5;Classification++)
		{
			List<DriverTable> DriverTable= new LinkedList<DriverTable>(filterDriverTable);
			PreRountingArrangerFilter filter=null;
			TargetDriver=null;
			//filter��l��	
			filter = new PreRountingArrangerFilter(Variable.TolerableTime,FilterEnable,Req, TimeUnit,Req.Number, IndexMap, Variable, ilf);
			//����filter
			filter.CarFilter(DriverTable);
			//�ˬd�ɬqfilter
			filter.StatusFilter(DriverTable);	
			//�ˬd�ϰ�filter
			filter.areaFilter(DriverTable,Classification);
			//�ˬd�^�t�ϰ�filter
			filter.endareaFilter(DriverTable);	
			//�ˬd�O�_���������𮧮ɶ�
			filter.restFilter(DriverTable);
			//�ˬd�O�_�W�L�w�w�����ȤW����
			filter.maxofTrip(DriverTable);	
			//�ˬd�O�_�ӱo�α���e�w���̻P�ӱo�α��U�@�Z�w����
			filter.DistanceTimeFilter(DriverTable);		
			//���o�̨Υq��			 
			TargetDriver = filter.MinFilter(DriverTable,false);
			if(TargetDriver != null)
			{
				//�ק�q����ModifyOriginDriverTable("��l�q����","�n�ƤJ�q��", "�n�ק諸��ƪ�W��");			
				TargetDriver.ModifyOriginDriverTable(Variable,Req,TimeUnit,TargetDriver, "userrequests",IndexMap,car);	
				TargetDriver.ArrangedCount++;
				//�P�B�ݨD��
				Req.Arrange=true;
				Found=true;
				break;
			}else if(Classification==5)
			{
				//��s�w���̪��ƯZ�аO
				 String sql = "UPDATE userrequests SET arranged = -4 WHERE �ѧO�X = '" +Req.Number + "' AND arrangedate = '" + Variable.date +"' AND arrangetime = '" + Variable.time + "'";
				 Variable.smt2.executeUpdate(sql);//�g���Ʈw
				 Found=false;
			}
		}
		return Found;
	}
	//�B�z�T�a�L�f���
	 public List<RequestTable> Southwestprocess(List<carGroup> car,int[] FilterEnable,ILF ilf,Map<Integer, RequestTable> IndexMap
					,List<RequestTable> Specialearaarray,double TimeUnit,Map<String, DriverTable> carIndexMap,DriverTable DriverTable) throws Exception
			{
				//�P�_�O�_���T�a�L�f��骺�ݨD��
				if(Specialearaarray.size()>0)
				{
					
					//�̧�Ū���X�ӳB�z
					for(int reqindex=0;reqindex<Specialearaarray.size();)
					{
						if(!Specialearaarray.get(reqindex).Arrange)
						{
							//���o�|����
							List<DriverTable> filterDriverTable=DriverTable.getfFournobileDriverTable(car,Variable); 
							if(Specialearafilterprocess(filterDriverTable,Specialearaarray.get(reqindex),
								TimeUnit,IndexMap,ilf,FilterEnable,car))
							{
								//System.out.println(reqindex+"ok"); 
								Specialearaarray.remove(reqindex);						
							}
							else
							{
								reqindex++;
							}	
					    }
						//�^�Ǥp�󵥩�-2�N��google�d�ߦ����ߧY�פ�{��
						if(Variable.errorcode<=-2)
							break;
					}
				}
				return Specialearaarray;
		}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	 public void  checkdata() throws ClassNotFoundException, IOException, SQLException, BiffException, InterruptedException
		{
			 ResultSet rs = null;
			 String sqlQuery="SELECT * FROM `arrangedtable`  WHERE `date`='"+	Variable.date+"' and `arrangetime`='"+	Variable.time+"'";
			 String sqlQuery1="";
			 rs=	Variable.smt.executeQuery(sqlQuery); 
			 rs.first();
			 sqlQuery1="UPDATE userrequests SET Targetdrivers='null' ,arranged=-4 WHERE arranged=1 and `arrangedate`='"+Variable.date+"' and `arrangetime`='"+	Variable.time+"'";
			 Variable.smt2.executeUpdate(sqlQuery1);
			  do
				{	
				for(int i = 1; i < 17; i++)
				{
					//Ū���Z����ƨæ^�_Node����timeinterval
					if(rs.getInt("run" + String.valueOf(i)) != -1)
					{  
						String information = rs.getString("user"+ String.valueOf(i));
						String[] testnumber = information.split("_");				
						if(testnumber.length == 1)
						{
							int informationNum = Integer.valueOf(testnumber[0]);	
							//sqlQuery1="UPDATE userrequests SET Targetdrivers='null' WHERE `arrangedate`='2013-07-30' and `arrangetime`='13:53:11'";
							sqlQuery1="UPDATE userrequests SET arranged=1 ,Targetdrivers='"+rs.getString("carid")+"' WHERE `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"' AND `�ѧO�X`='"+informationNum+"'";
							Variable.smt2.executeUpdate(sqlQuery1);
						}
						else
						{
							
							int informationNum = Integer.valueOf(testnumber[0]);	
							int informationNum1 = Integer.valueOf(testnumber[1]);	
							sqlQuery1="UPDATE userrequests SET  arranged=1 ,Targetdrivers='"+rs.getString("carid")+"' WHERE `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"' AND `�ѧO�X`='"+informationNum+"'";
							Variable.smt2.executeUpdate(sqlQuery1);
							sqlQuery1="UPDATE userrequests SET  arranged=1 , Targetdrivers='"+rs.getString("carid")+"' WHERE `arrangedate`='"+Variable.date+"' and `arrangetime`='"+Variable.time+"' AND `�ѧO�X`='"+informationNum1+"'";
							Variable.smt2.executeUpdate(sqlQuery1);
						}
					}else
					{
						break;
					}
				}
				}while(rs.next());
		}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub

	}
	

}
