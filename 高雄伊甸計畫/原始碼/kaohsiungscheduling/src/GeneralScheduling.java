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
@WebServlet("/GeneralScheduling.view")
public class GeneralScheduling extends HttpServlet
{
	private static final long serialVersionUID = 1L;		
	 defineVariable Variable;//��m�w�q���ܼ�

    /**
     * @throws Exception 
     * @see HttpServlet#HttpServlet()
     */
    public GeneralScheduling()
    {
        super();    
        // TODO Auto-generated constructor stub
    }
    /***********************************************
	 *  ���~�N�X���������~
		-2:�w�W�L����t�B
        -3:�n�D�w�D�ڵ�
        -4:���s�b��addres
        -5:�d��(address��latlng)�򥢤F
        -6:�����l���Y���Z
        -7:�ƯZ���_
	*/
	/**
	 * @throws IOException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse respognse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		 System.out.print("\n�ƯZ�}�l");
		// TODO Auto-generated method stub
		//�Ѽƫŧi////////////////////////////////////////////////////////////////	
		double TimeUnit = 0.0;	
		int[] FilterEnable = new int[5];//����Filter�ҰʰѼ�	
		int Percentmode=0;//�������`�έԸɪ��i�׭�
		int reqsize=0;//�����ثe���`�ƯZ�έԸɱƯZ�����ƹL�Z�w���̼�	
	    List<reqGroup> requestTable=new ArrayList<reqGroup>();	//�ݨD��
		List<carGroup> car = new ArrayList<carGroup>();//������
		////////////////////////////////////////////////////////////////////////////		
		ILF ilf = null;
		
		try {
			Variable = new defineVariable();//��l�Ʃw�q�ܼ�							
			ilf = new ILF(Variable.con,Variable);//��l�ƾ��v��Ʒj�M����	
			Variable.date = request.getParameter("arrangedate");//�ƯZ���
			Variable.time = request.getParameter("arrangetime");//�ƯZ�ɶ�	
			
			//ilf = new ILF(Variable.con,Variable.date,Variable.time);//��l�ƾ��v��Ʒj�M����
			int mode = Integer.valueOf(request.getParameter("mode"));	//�P�O�ثe���b�ƪ��O���`�Z���έԸɯZ��
			
			for(int i=0;i<Variable.areanum;i++)
				 car.add(new carGroup(Variable.intervalnum));	//��l�ƥq����
			for(int i=0;i<Variable.areanum;i++)
			    requestTable.add(new reqGroup(Variable.intervalnum));//��l�ƹw����

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
				if(mode==1)//�Ը�
				{
					Percentmode=4;//�p�G�O�ԸɯZ���N�]�w�g�J�i�ת�index��4��
					//��s��Ʈw�����ثe�ާ@���ʧ@
					Variable.smt2.executeUpdate("UPDATE `arrange_log` SET `progress`=5  WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
				}
				else if(mode==2)//����
				{
					Percentmode=1;//�p�G�O���`�Z���N�]�w�g�J�i�ת�index��1��
					//��s��Ʈw�����ثe�ާ@���ʧ@
					Variable.smt2.executeUpdate("UPDATE `arrange_log` SET `progress`=4  WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
				}
				
				DriverTable DriverTable = new DriverTable(0);
			    //��l��Ū���w���o��ƪ���		
				ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
				//Ū��request�w����A
				requestTable = input.ReadOrderTable(requestTable,Variable);	
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
				double startTime,endTime,totTime;
				startTime = System.currentTimeMillis();
				LinkedList<RequestTable> OriginReqlist=readareaarray( requestTable,mode);	
				reqsize=OriginReqlist.size();
				int count=0;	//�p��B�z�L���w���̲έp��
				int tineindex=13;//�ƯZ�B�z���ɶ�����
				//�̮ɶ�����h�ƯZ
				for(;tineindex<=46;tineindex++)
				{
					LinkedList<DriverTable> OriginDriverTable =DriverTable.filterDriverTable(car,DriverTable.carsize,tineindex,6,Variable);
					if(OriginDriverTable.size()==0)
						continue;
					for(int Classification=1;Classification<=7;Classification++)
					{
						for(int i = 0; i < OriginDriverTable.size();i++ )
						{
							if(OriginDriverTable.size()==0)
								continue;
							RequestTable TagetReq=null;//�ﭫ���w����
							//���o�i�Ϊ��w����
							List<RequestTable> FilterReqList = new LinkedList<RequestTable>(OriginReqlist);
							Filter filter= new Filter(tineindex,FilterEnable,OriginDriverTable.get(i),TimeUnit, IndexMap,Variable, ilf);
							filter.AreaCorrespond(FilterReqList);	
							//�s�H�����@����
							filter.AssignSharingCarFilter(FilterReqList);	
							//�S�������W�L����
							filter.SpecialCarFilter(FilterReqList);	
							//�L�o����
							filter.CarFilter(FilterReqList);	
							//�L�o�ɬq
							filter.StatusFilter(FilterReqList);	
							//�����������������w����
							filter.AreaCorrespond(FilterReqList);	
							//�W�U�Z�ɬq�A1�p�ɤ��������W�L2��
							filter.NoMoreThanTwoFilter(FilterReqList);							
							TagetReq=FilterProcess(filter, FilterReqList,Classification,Classification);
							if(Variable.errorcode<=-2)
								 break;
							if(TagetReq!=null)
							{
								count+=Modifyinfo(OriginReqlist,TagetReq, IndexMap ,OriginDriverTable.get(i), TimeUnit, car,requestTable);						
							}
							Variable.recentPercent =(int)(((float)count /reqsize)* 100);//�N�w�ƤJ���w���̼��ഫ���ʤ���								
							//�g�J��Ʈw
							if(Variable.recentPercent==100)
								Variable.recentPercent=99;
							Variable.smt2.executeUpdate("UPDATE progress SET percent ="+Variable.recentPercent+" WHERE `index` ="+Percentmode+" and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");				
						}
					}
				}		
				//���]google api�o�Ϳ��~�ߧY�g�X�ƯZ���G
				if(Variable.errorcode<=-2)
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
					checkdata(-1);
					break;
				}
				 endTime = System.currentTimeMillis();
			    //���o�{���������ɶ�
				 totTime = endTime - startTime;
				 System.out.println("Using Time: " + totTime+" ms");
				 RequestTable.modifyReqstatus(OriginReqlist, Variable);
				/*if(mode==1)
					car =resttime(car);*/
				/***************************��s��Ʈw�̪��q���Z����********************************/
				for(int areaindex=0;areaindex<Variable.areanum;areaindex++)
				 {
					 for(int intervalindex=0;intervalindex<Variable.intervalnum;intervalindex++)
					 {
						 for(int index=0;index<car.get(areaindex).getCar(intervalindex).size();index++)
						 {
							
							 car.get(areaindex).getCar(intervalindex).get(index).UpdateNode(Variable,car.get(areaindex).getCar(intervalindex).get(index).RestTime1,car.get(areaindex).getCar(intervalindex).get(index));
						 }
					}
				 }
				checkdata(0);
				//���槹�ƯZ�g�^�i��100
				Variable.smt2.executeUpdate("UPDATE progress SET percent =100 WHERE `index` ="+Percentmode+" and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
				break;
				/***************************��s��Ʈw�̪��q���Z����********************************/	
						
			}	
			ilf=null;
			Variable.smt.close();			
			Variable.con.close();
			requestTable.clear();
			car.clear();			
			System.gc();
		}
		catch(Exception e)
		{
            //�o�ͱƯZ���~�ߧY���g�X���G
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
			try {
				checkdata(-1);
			} catch (ClassNotFoundException | BiffException | SQLException
					| InterruptedException e1) {
				// TODO �۰ʲ��ͪ� catch �϶�
				e1.printStackTrace();
			}			
			Variable.errorcode=-7;
			PrintWriter out = response.getWriter();
			out.println(Variable.errorcode);
			e.printStackTrace();
		}
		
		if(Variable.errorcode<=-2)
		{
			PrintWriter out = response.getWriter();
			out.println(Variable.errorcode);
			
		}else
		{
			PrintWriter out = response.getWriter();
			out.println("Success");
		}
		
	}
	public RequestTable  FilterProcess(Filter filter,List<RequestTable>FilterReqList,int Classification,int mode ) 
	{
		RequestTable TargetReq=null;//�����ﭫ���w����
		if(mode==1)
		{
			//�Ĥ@�q�H����M
			//�����
			filter.RoadToRoadFilter(FilterReqList);	
		}
	   	//�ˬd�O�_�ݩ�s�������s�� �ùL�o�ϰ�
	//	 filter.xindianRoadSplitAreaFilter(FilterReqList);	
		//�ˬd�ϰ�filter �p�G�ݩ�s�������s�ϴN���ˬd 
	//	if(!filter.xindianRoadSplitArea)		
		filter.areaFilter(false,FilterReqList,Classification);	
		//�ˬd�^�t�ϰ� 
		/*if(!filter.xindianRoadSplitArea)
			filter.endareaFilter(FilterReqList);		*/
		//�ˬd�O�_���������𮧮ɶ�
		filter.restFilter(FilterReqList);
		//�Ȧ�ɶ�
		filter.DistanceTimeFilter(FilterReqList);		
		TargetReq = filter.MinFilter(FilterReqList);
		return TargetReq;
	}
	public int Modifyinfo(	LinkedList<RequestTable> OriginReqlist,RequestTable req,Map<Integer, RequestTable> IndexMap ,DriverTable TargetDriver,double TimeUnit,List<carGroup> car,List<reqGroup> requestTable)
	{
		int Arrangereqnum=0;
		//�ק�q����ModifyOriginDriverTable("��l�q����","�n�ƤJ�q��", "�n�ק諸��ƪ�W��");
		TargetDriver.ModifyOriginDriverTable(Variable,req,TimeUnit,TargetDriver, "userrequests",IndexMap,car);	
		//�N�w���̼аO���w�ƹL�Z
		req.Arrange = true;	
		OriginReqlist.remove(req);
		Arrangereqnum++;
		if(req.AssignSharing!=-1)
		{
			IndexMap.get(req.AssignSharing).Arrange=true;
			Arrangereqnum++;
			OriginReqlist.remove(IndexMap.get(req.AssignSharing));
		}
	    return Arrangereqnum;
	}
	//���o�w����list
	public 	LinkedList <RequestTable> readareaarray(List<reqGroup> requestTable,int mode)
	   {
	    	//��l�ƹw���̰}�C
			LinkedList <RequestTable> readearaarray = new LinkedList<RequestTable>();
		   
		   for(int areaindex=0;areaindex<Variable.areanum;areaindex++)//32�Ӧa��
		   {
			   for(int timeindex=0;timeindex<Variable.intervalnum;timeindex++)//�ɶ��`�϶���
			   {
				   	for(int index=0;index<requestTable.get(areaindex).getreq(timeindex).size();index++)
				   	{		  
				   		
				   		if(requestTable.get(areaindex).getreq(timeindex).get(index).Arrange==false)				   			
				   		{
				   			
				   			if(requestTable.get(areaindex).getreq(timeindex).get(index).Status!=mode)//�̶Ƕi�Ӫ���Ū�����`�έԸɪ��w����
				   			{
				   				readearaarray.add(requestTable.get(areaindex).getreq(timeindex).get(index));
				   			}
				   			
				   		}
				   	}
				}
			}
		    return readearaarray;
		}
  	public void  checkdata(int interrupt) throws ClassNotFoundException, IOException, SQLException, BiffException, InterruptedException
	{
		 ResultSet rs = null;
		 String sqlQuery="SELECT * FROM `arrangedtable`  WHERE `date`='"+	Variable.date+"' and `arrangetime`='"+	Variable.time+"'";
		 String sqlQuery1="";
		 rs=	Variable.smt.executeQuery(sqlQuery); 
		 rs.first();
		 if(interrupt==0)
			 sqlQuery1="UPDATE userrequests SET Targetdrivers='null' ,arranged=0 WHERE arranged=1 AND `arrangedate`='"+Variable.date+"' and `arrangetime`='"+	Variable.time+"'";
		 if(interrupt==-1)
			 sqlQuery1="UPDATE userrequests SET Targetdrivers='null' ,arranged=-1 WHERE  `arrangedate`='"+Variable.date+"' and `arrangetime`='"+	Variable.time+"'";
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
  
  
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub

	}
	

}
