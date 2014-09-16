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
@WebServlet("/PreRoutingArrangertest.view")
public class preRoutingArrangertest extends HttpServlet
{
	private static final long serialVersionUID = 1L;		
	 defineVariable Variable;//��m�w�q���ܼ�	
	 
    /**
     * @throws Exception 
     * @see HttpServlet#HttpServlet()
     */
    public preRoutingArrangertest()
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
		//int ILFEnable = -1;	
		List<reqGroup> requestTable=new ArrayList<reqGroup>();	//�H�W�������ݨD��		
		List<carGroup> car = new ArrayList<carGroup>();//������
		List<reqGroup> TailRequestTable=new ArrayList<reqGroup>();	//�H�U�������ݨD��	
		////////////////////////////////////////////////////////////////////////////		
		ILF ilf = null;
		System.out.println("�u���a�ϱƯZ�}�l");
		try
		{
			Variable = new defineVariable();//��l�Ʃw�q�ܼ�		
			Variable.date = request.getParameter("arrangedate");//�ƯZ���
			Variable.time = request.getParameter("arrangetime");//�ƯZ�ɶ�	
			//ilf = new ILF(Variable.con,Variable.date,Variable.time);//��l�ƾ��v��Ʒj�M����	
			//int mode = Integer.valueOf(request.getParameter("mode"));	//�P�O�ثe���b�ƪ��O���`�Z���έԸɯZ��
			for(int i=0;i<Variable.areanum;i++)
			{
				car.add(new carGroup(Variable.intervalnum));	//��l�ƥq����
				requestTable.add(new reqGroup(Variable.intervalnum));//��l�ƹw����H�W���ϰ����
				TailRequestTable.add(new reqGroup(Variable.intervalnum));//��l�ƹw����H�U���ϰ����
			}
		
			 Variable.smt2.executeUpdate("UPDATE progress SET percent =0 WHERE `index` =6 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
			 Variable.smt2.executeUpdate("UPDATE `arrange_log` SET `progress`=3  WHERE `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");

			//////////////////////////////////////////////////////////////////////
									
			//�q��ƮwŪ���Ѽưt�m��
			Variable.rs = Variable.smt.executeQuery("select *from setting");//���o�]�w�Ѽ�
			
			//���o�@���Ѽ�set
			while(Variable.rs.next())
			{
				//�]�w�Ѽ�
				TimeUnit = Variable.rs.getFloat("Time unit");				
				//ILFEnable =Variable.rs.getInt("ILFEnable");
				FilterEnable[0] = Variable.rs.getInt("Filter1");
				FilterEnable[1] = Variable.rs.getInt("Filter2");
				FilterEnable[2] = Variable.rs.getInt("Filter3");
				FilterEnable[3] = Variable.rs.getInt("Filter4");
				FilterEnable[4] = Variable.rs.getInt("Filter5");	
				ilf = new ILF(Variable.con,Variable);//��l�ƾ��v��Ʒj�M����	
				DriverTable DriverTable = new DriverTable(0);				
			    //��l��Ū���w���o��ƪ���		
				ReadTempRequest input = new ReadTempRequest(Variable.con, Variable.date, Variable.time);
				//Ū��request�w����A
				requestTable = input.ReadOrderTable(requestTable,Variable);			
				TailRequestTable= input.ReadEndTable();
				//�N�Ҧ��ݨD��mIndexMap�H�Q�d�ߤW�@�Z�ΤU�@�Z�w���̪���T
			    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>();			   
			    Map<String, DriverTable> carIndexMap = new HashMap<String, DriverTable>();
			    IndexMap=RequestTable.getindexmap(IndexMap,requestTable,Variable);			    
				//Ū���q����	
				 car=DriverTable.readDrivertable(Variable.con, Variable.date, Variable.time, Variable.smt, car,ilf,IndexMap,Variable,requestTable);			    
				 if(Variable.errorcode<=-2)
						break;
				 //�N�Ҧ��q����Jmap �H�Q�d��
				 carIndexMap=DriverTable.GetcarIndexMap(carIndexMap,car,Variable);
				//����filter					
				Variable.recentPercent=0;//�ƯZ�i�׭��k�s		
				//test
				double startTime,endTime,totTime;
				startTime = System.currentTimeMillis();
				//Ū���W���Ȧ�ɶ�array				
				List<RequestTable> longtimearray=RequestTable.getlongtimearray(requestTable,Variable);					
				//Ū������array				
			//	List<RequestTable> pairarray=RequestTable.getpairarray(longtimearray);	
				
				//Ū���ɶ��b�U��4�I��ߤW8�I���x�_ 
				List<RequestTable> nighttotaipeiarray =RequestTable.getnighttotaipeiarray(requestTable,Variable); 		    		
				
				//Ū���ߤW6�I�L�᪺�ݨD��
				List<RequestTable> nightreqarray =RequestTable.getnightreqarray(requestTable,Variable); 		    		
			   
				//��]�ߪ�req�[�J�U���a�I�b�x�_array�᭱
				nighttotaipeiarray.addAll(nightreqarray);				
				//�h�����Ƹ�� �]��nighttotaipeiarray����Ʀ���nightreqarray���|
				nighttotaipeiarray=removeDuplicateWithOrder(nighttotaipeiarray);	
				
				//Ū���W�U���a�I�b�F�_�����ݨD 0�N��Ū��defineVariable�̪�Specialeara array�Ĥ@��
				List<RequestTable> NortheastSpecialearaarray =RequestTable.getSpecialareaarray(0,requestTable,TailRequestTable,IndexMap,Variable); 		    		
				//�������Ƨ�����ݨD�� 
				//��]:����F�_���ݨD�̪���k�O�P�ɦa�ϦP�ɬq����H�W���a�I�b�F�_�� 
				//�M��A��H�U���a�I�b�F�_�� 
				//���Ǹ�Ʒ|���Ƨ�� �ݲ���
				NortheastSpecialearaarray=removeDuplicateWithOrder(NortheastSpecialearaarray);
				
				List<RequestTable> Specialearaarray =RequestTable.getSpecialareaarray(2,requestTable,TailRequestTable,IndexMap,Variable); 		    		
			  //����`�|���w����
				Specialearaarray=removeDuplicateWithOrder(Specialearaarray);
			
				//Ū���W�U���a�I�b�T�a�L�f��骺�ݨD 1�N��Ū��defineVariable�̪�Specialeara array�Ĥ@��
				List<RequestTable> Specialearaarray1 =RequestTable.getSpecialareaarray(1,requestTable,TailRequestTable,IndexMap,Variable); 		    		
				//�������Ƹ��
				Specialearaarray1=removeDuplicateWithOrder(Specialearaarray1);
				
				//�ݳB�z���w�����`��
				Variable.reqsize=longtimearray.size()+nighttotaipeiarray.size()+NortheastSpecialearaarray.size()+Specialearaarray.size()+Specialearaarray1.size();
				
				//�i�榨��y�{
			/*	if(Variable.errorcode>-2)
					pairprocess(requestTable,car,FilterEnable,ilf,IndexMap,pairarray,TimeUnit,carIndexMap,DriverTable,longtimearray);
				RequestTable.modifyReqstatus(pairarray, Variable);*/
				//�i�椣����.
				if(longtimearray.size()>0&&Variable.errorcode>-2)
					process(car,FilterEnable,ilf,IndexMap,longtimearray,TimeUnit,DriverTable,requestTable, 0);
				RequestTable.modifyReqstatus(longtimearray, Variable);
				//����]�Z�y�{
				if(nighttotaipeiarray.size()>0&&Variable.errorcode>-2)					
					process(car,FilterEnable,ilf,IndexMap,nighttotaipeiarray,TimeUnit,DriverTable,requestTable,1);
				RequestTable.modifyReqstatus(nighttotaipeiarray, Variable);		
			//�Ĥ@��Ū��������� �ĤG��Ū���s��
				for(int carrun=0;carrun<1;carrun++)
				 { 
					if(Variable.errorcode<=-2)
						break;
					//0�N��Ū��defineVariable�̪�Specialearacar array�Ĥ@��Ū������s�����l��index
					//��mcarindex�n�hŪ���Y�Ӱϰ쪺���lindex
					int carindex=Variable.Specialareacar[0][carrun];				
					NortheastSpecialearaarray=northeastprocess	(0,carindex,car,FilterEnable,ilf,IndexMap,NortheastSpecialearaarray,TimeUnit,DriverTable,requestTable);
				 }				
				RequestTable.modifyReqstatus(NortheastSpecialearaarray, Variable);
			/*	
			//�N���B�z�����[�J�����@�_��
				Specialearaarray.addAll(NortheastSpecialearaarray);
				//�Ĥ@��Ū��������� �ĤG��Ū���s��
				for(int carrun=0;carrun<2;carrun++)
				 { 
					if(Variable.errorcode<=-2)
						break;
					//0�N��Ū��defineVariable�̪�Specialearacar array�Ĥ@��Ū������s�����l��index
					//��mcarindex�n�hŪ���Y�Ӱϰ쪺���lindex
					int carindex=Variable.Specialareacar[0][carrun];							
					Specialearaarray=northeastprocess	(2,carindex,car,FilterEnable,ilf,IndexMap,Specialearaarray,TimeUnit,DriverTable,requestTable);
				 }
				
				RequestTable.modifyReqstatus(Specialearaarray, Variable);*/
			/*	
				//�Ĥ@��Ū���g������ �ĤG��Ū�����M
				for(int carrun=0;carrun<3;carrun++)
				 { 
					if(Variable.errorcode<=-2)
						break;
					//0�N��Ū��defineVariable�̪�Specialearacar array�Ĥ@��Ū���g�����M���l��index
					//��mcarindex�n�hŪ���Y�Ӱϰ쪺���lindex
					int carindex=Variable.Specialareacar[1][carrun];
					Specialearaarray1=Southwestprocess(carrun,carindex,car,FilterEnable,ilf,IndexMap,Specialearaarray1,TimeUnit,DriverTable,requestTable);
				 }
				RequestTable.modifyReqstatus(Specialearaarray1, Variable);
				
				*/
				if(Variable.errorcode<=-2)
				{
					break;
				}
			    endTime = System.currentTimeMillis();
			    //���o�{���������ɶ�
			    totTime = endTime - startTime;
			    System.out.println("Using Time: " + totTime+" ms");
				System.out.println(":�u���ϰ�ƯZ����:");
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
				//���槹�ƯZ�g�^�i��100
				Variable.smt2.executeUpdate("UPDATE progress SET percent =100 WHERE `index` =6 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
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
	//����req filterprocess
	public boolean filterprocess(List<carGroup> car,int run,List<DriverTable> filterDriverTable,List<DriverTable> filterDriverTable1,RequestTable Req
			,RequestTable Req1,double TimeUnit,Map<Integer, RequestTable> IndexMap,ILF ilf,int[] FilterEnable)
	{
		 PreRountingArrangerFilter reqfilter=null;
		 PreRountingArrangerFilter req1filter=null;
		 DriverTable TargetDriver=null;
		 boolean Result=false;
		 try
		 {
			 //filter��l��	
			reqfilter = new PreRountingArrangerFilter(Variable.TolerableTime,FilterEnable,Req,TimeUnit,Req.Number, IndexMap,Variable, ilf);
		
			//filter��l��					 
			req1filter = new PreRountingArrangerFilter(Variable.TolerableTime,FilterEnable,Req1,TimeUnit,Req1.Number, IndexMap,Variable, ilf);

			 //����filter
			 reqfilter.CarFilter(filterDriverTable);
			 //����filter
			 req1filter.CarFilter(filterDriverTable1);
			//�L�o�s��q��
			 reqfilter.AssignSharingCarFilter(filterDriverTable);	
			//�L�o�s��q��
			 req1filter.AssignSharingCarFilter(filterDriverTable1);	
			 //�ˬd�ɬqfilter
			 reqfilter.StatusFilter(filterDriverTable);	
			 //�ˬd�ɬqfilter
			 req1filter.StatusFilter(filterDriverTable1);	
			 
			 //�ˬd�W�U�Z�ɬq�@�p�ɤ����i���W�L2��
			 reqfilter.NoMoreThanTwoFilter(filterDriverTable);				
			 req1filter.NoMoreThanTwoFilter(filterDriverTable1);				 
			 
			//�ˬd�ϰ�filter
			 reqfilter.areaFilter1(filterDriverTable,run);
			//�ˬd�ϰ�filter
			 req1filter.areaFilter1(filterDriverTable1,run);
			 
			//�ˬd�^�t�ϰ�filter
			 reqfilter.endareaFilter(filterDriverTable);
			 //�ˬd�^�t�ϰ�filter
			 req1filter.endareaFilter(filterDriverTable1);
			 
			//�ˬd�O�_���������𮧮ɶ�
			 reqfilter.restFilter(filterDriverTable);
			//�ˬd�O�_���������𮧮ɶ�
			 req1filter.restFilter(filterDriverTable1);
			 
			//�ˬd�O�_�ӱo�α���e�w���̻P�ӱo�α��U�@�Z�w����
			 reqfilter.DistanceTimeFilter(filterDriverTable);		 
			 //�ˬd�O�_�ӱo�α���e�w���̻P�ӱo�α��U�@�Z�w����
			 req1filter.DistanceTimeFilter(filterDriverTable1);	
			  
			 //�ˬd�O�_�ӱo�α���e�w���̻P�ӱo�α��U�@�Z�w����
			 reqfilter.AssignSharingDistanceTimeFilter(filterDriverTable);		
			 //�ˬd�O�_�ӱo�α���e�w���̻P�ӱo�α��U�@�Z�w����
			 req1filter.AssignSharingDistanceTimeFilter(filterDriverTable1);	
			 
			 //���o�̨Υq��
			if(run==0)
			{
			 TargetDriver = MinFilter(filterDriverTable,filterDriverTable1);		
			 if(TargetDriver != null)
			 {	
				 double IntervalSec = 0.5 * 3600;//�Ninterval���ɶ��Ѥp�ɬ�����ন�Ѭ����
				 int startInterval = (int)((TargetDriver.StartTime+1800+9000) / IntervalSec);//�p���Y�Z�Ҧb�϶�
					//�p����Z�Ҧb�϶�
				 int endInterval = (((TargetDriver.EndTime+2700-9000) % IntervalSec)  > 0.0 ? (int)((TargetDriver.EndTime+2700-9000)/ IntervalSec) : (int)((TargetDriver.EndTime+2700-9000)/ IntervalSec)-1);
				 int startindex=startInterval;//�Y�Z����2�p��
				 int endindex=endInterval;//���Z���e2�p��				
				
				 //�P�B��ӻݨD���ҥe�Ϊ����
				 //��1�ӹw���̪��϶� �έ쥻�W�U���ɶ��h��϶�
				 int StartInterval = (int)( (Req.originalStartTime) / IntervalSec);//�W���ɶ��b�@�Ѥ���interval index
				 //�U���ɶ��b�@�Ѥ���interval index
				 int EndInterval = (((Req.originalDestinationTime) % IntervalSec)  > 0.0 ? (int)((Req.originalDestinationTime) / IntervalSec) : (int)((Req.originalDestinationTime) / IntervalSec) - 1);
				
				 //��2�ӹw���̪��϶� �έ쥻�W�U���ɶ��h��϶�
				 int StartInterval1 = (int)( (Req1.originalStartTime) / IntervalSec);//�W���ɶ��b�@�Ѥ���interval index
				 //�U���ɶ��b�@�Ѥ���interval index
				 int EndInterval1 = (((Req1.originalDestinationTime) % IntervalSec)  > 0.0 ? (int)((Req1.originalDestinationTime) / IntervalSec) : (int)((Req1.originalDestinationTime) / IntervalSec) - 1);
				
				 
				 //���ƻs�쥻�𮧰϶���array��temprelaxarry
				 TargetDriver.temprelaxarry=new ArrayList<String>(TargetDriver.relaxarry);
				 
				 
				 //���R����1�ӹw���̩Ҧ����϶�
				 for(int index = StartInterval; index <= EndInterval; index++)
				 {
					//�����b�𮧰϶��N�R��
					 if(index>=startindex&&index<=endindex)
						 TargetDriver.temprelaxarry.remove(String.valueOf(index));
				 }
				 //�R����2�ӹw���̩Ҧ����϶�
				 for(int index = StartInterval1; index <= EndInterval1; index++)
				 {
					//�����b�𮧰϶��N�R��
					 if(index>=startindex&&index<=endindex)
						 TargetDriver.temprelaxarry.remove(String.valueOf(index));
				 }
							 
				 //�ק�q����ModifyOriginDriverTable("��l�q����","�n�ƤJ�q��", "�n�ק諸��ƪ�W��");
				 TargetDriver.ModifyOriginDriverTable(Variable,Req,TimeUnit,TargetDriver, "userrequests",IndexMap,car);	
				 TargetDriver.ArrangedCount++;
				 TargetDriver.ModifyOriginDriverTable(Variable,Req1,TimeUnit,TargetDriver, "userrequests",IndexMap,car);
				 TargetDriver.ArrangedCount++;
				 //�P�B�ݨD��
				Req.Arrange=true;
				Req1.Arrange=true;
				if(Req.AssignSharing!=-1)
					IndexMap.get(Req.AssignSharing).Arrange=true;			
				if(Req1.AssignSharing!=-1)
					IndexMap.get(Req1.AssignSharing).Arrange=true;
				Variable.DealReqNum++;//��s�i�ת�
				Variable.recentPercent=(int)((float)Variable.DealReqNum/Variable.reqsize*100);
				Variable.smt2.executeUpdate("UPDATE progress SET percent ="+Variable.recentPercent+" WHERE `index` =6 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
				Result=true;
			 }
		   }
		   else
		   {
			 TargetDriver = MinFilter(filterDriverTable,filterDriverTable1);		
			 if(TargetDriver != null)
			 {	
				 double IntervalSec = 0.5 * 3600;//�Ninterval���ɶ��Ѥp�ɬ�����ন�Ѭ����
				 int startInterval = (int)((TargetDriver.StartTime+1800+9000) / IntervalSec);//�p���Y�Z�Ҧb�϶�
					//�p����Z�Ҧb�϶�
				 int endInterval = (((TargetDriver.EndTime+2700-9000) % IntervalSec)  > 0.0 ? (int)((TargetDriver.EndTime+2700-9000)/ IntervalSec) : (int)((TargetDriver.EndTime+2700-9000)/ IntervalSec)-1);
				 int startindex=startInterval;//�Y�Z����2�p��
				 int endindex=endInterval;//���Z���e2�p��				
				
				 //�P�B��ӻݨD���ҥe�Ϊ����
				 //��1�ӹw���̪��϶� �έ쥻�W�U���ɶ��h��϶�
				 int StartInterval = (int)( (Req.originalStartTime) / IntervalSec);//�W���ɶ��b�@�Ѥ���interval index
				 //�U���ɶ��b�@�Ѥ���interval index
				 int EndInterval = (((Req.originalDestinationTime) % IntervalSec)  > 0.0 ? (int)((Req.originalDestinationTime) / IntervalSec) : (int)((Req.originalDestinationTime) / IntervalSec) - 1);
				
				 //��2�ӹw���̪��϶� �έ쥻�W�U���ɶ��h��϶�
				 int StartInterval1 = (int)( (Req1.originalStartTime) / IntervalSec);//�W���ɶ��b�@�Ѥ���interval index
				 //�U���ɶ��b�@�Ѥ���interval index
				 int EndInterval1 = (((Req1.originalDestinationTime) % IntervalSec)  > 0.0 ? (int)((Req1.originalDestinationTime) / IntervalSec) : (int)((Req1.originalDestinationTime) / IntervalSec) - 1);
				
				 
				 //���ƻs�쥻�𮧰϶���array��temprelaxarry
				 TargetDriver.temprelaxarry=new ArrayList<String>(TargetDriver.relaxarry);
				 
				 
				 //���R����1�ӹw���̩Ҧ����϶�
				 for(int index = StartInterval; index <= EndInterval; index++)
				 {
					//�����b�𮧰϶��N�R��
					 if(index>=startindex&&index<=endindex)
						 TargetDriver.temprelaxarry.remove(String.valueOf(index));
				 }
				 //�R����2�ӹw���̩Ҧ����϶�
				 for(int index = StartInterval1; index <= EndInterval1; index++)
				 {
					//�����b�𮧰϶��N�R��
					 if(index>=startindex&&index<=endindex)
						 TargetDriver.temprelaxarry.remove(String.valueOf(index));
				 }
				 
				 
					//�ק�q����ModifyOriginDriverTable("��l�q����","�n�ƤJ�q��", "�n�ק諸��ƪ�W��");
				 	 TargetDriver.ModifyOriginDriverTable(Variable,Req,TimeUnit,TargetDriver, "userrequests",IndexMap,car);	
					 TargetDriver.ArrangedCount++;
					 TargetDriver.ModifyOriginDriverTable(Variable,Req1,TimeUnit,TargetDriver, "userrequests",IndexMap,car);
					 TargetDriver.ArrangedCount++;
					 //�P�B�ݨD��
					 Req.Arrange=true;
					 Req1.Arrange=true;
					 if(Req.AssignSharing!=-1)
						 IndexMap.get(Req.AssignSharing).Arrange=true;					 
					 if(Req1.AssignSharing!=-1)
						 IndexMap.get(Req1.AssignSharing).Arrange=true;		
					 Variable.DealReqNum++;//��s�i�ת�
					 Variable.recentPercent=(int)((float)Variable.DealReqNum/Variable.reqsize*100);
					 Variable.smt2.executeUpdate("UPDATE progress SET percent ="+Variable.recentPercent+" WHERE `index` =6 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
					 Result=true;
			 }
			 else
			 {
				 DriverTable TargetDriver1=null;
				 DriverTable TargetDriver2=null;
				 
				//��1�ӻݨD��
				 TargetDriver1=reqfilter.MinFilter(filterDriverTable,false);
				 if(TargetDriver1 != null)
				 {
					 double IntervalSec = 0.5 * 3600;//�Ninterval���ɶ��Ѥp�ɬ�����ন�Ѭ����
					 int startInterval = (int)((TargetDriver1.StartTime+1800+9000) / IntervalSec);//�p���Y�Z�Ҧb�϶�
						//�p����Z�Ҧb�϶�
					 int endInterval = (((TargetDriver1.EndTime+2700-9000) % IntervalSec)  > 0.0 ? (int)((TargetDriver1.EndTime+2700-9000)/ IntervalSec) : (int)((TargetDriver1.EndTime+2700-9000)/ IntervalSec)-1);
					 int startindex=startInterval;//�Y�Z����2�p��				
					 int endindex=endInterval;//���Z���e2�p��	
					 //�P�B��ӻݨD���ҥe�Ϊ����
					 //��1�ӹw���̪��϶� �έ쥻�W�U���ɶ��h��϶�
					 int StartInterval = (int)( (Req.originalStartTime) / IntervalSec);//�W���ɶ��b�@�Ѥ���interval index
					 //�U���ɶ��b�@�Ѥ���interval index
					 int EndInterval = (((Req.originalDestinationTime) % IntervalSec)  > 0.0 ? (int)((Req.originalDestinationTime) / IntervalSec) : (int)((Req.originalDestinationTime) / IntervalSec) - 1);
					 //���ƻs�쥻�𮧰϶���array��temprelaxarry
					 TargetDriver1.temprelaxarry=new ArrayList<String>(TargetDriver1.relaxarry);
					 
					 
					 //���R����1�ӹw���̩Ҧ����϶�
					 for(int index = StartInterval; index <= EndInterval; index++)
					 {
						//�����b�𮧰϶��N�R��
						 if(index>=startindex&&index<=endindex)
							 TargetDriver1.temprelaxarry.remove(String.valueOf(index));
					 }
					 //�ק�q����ModifyOriginDriverTable("��l�q����","�n�ƤJ�q��", "�n�ק諸��ƪ�W��");
					 TargetDriver1.ModifyOriginDriverTable(Variable,Req,TimeUnit,TargetDriver1, "userrequests",IndexMap,car);	
				   
				   TargetDriver1.ArrangedCount++;
				   //�P�B�ݨD��
				   Req.Arrange=true;
				   if(Req.AssignSharing!=-1)
					   IndexMap.get(Req.AssignSharing).Arrange=true;				   
				 }
				 
				 //��2�ӻݨD��
				 TargetDriver2=req1filter.MinFilter(filterDriverTable1,false);
				 if(TargetDriver2 != null)
				 {
					 double IntervalSec = 0.5 * 3600;//�Ninterval���ɶ��Ѥp�ɬ�����ন�Ѭ����
					 int startInterval = (int)((TargetDriver2.StartTime+1800+9000) / IntervalSec);//�p���Y�Z�Ҧb�϶�
						//�p����Z�Ҧb�϶�
					 int endInterval = (((TargetDriver2.EndTime+2700-9000) % IntervalSec)  > 0.0 ? (int)((TargetDriver2.EndTime+2700-9000)/ IntervalSec) : (int)((TargetDriver2.EndTime+2700-9000)/ IntervalSec)-1);
					 int startindex=startInterval;//�Y�Z����2�p��
					 int endindex=endInterval;//���Z���e2�p��	
					 //��2�ӹw���̪��϶� �έ쥻�W�U���ɶ��h��϶�
					 int StartInterval1 = (int)( (Req1.originalStartTime) / IntervalSec);//�W���ɶ��b�@�Ѥ���interval index
					 //�U���ɶ��b�@�Ѥ���interval index
					 int EndInterval1 = (((Req1.originalDestinationTime) % IntervalSec)  > 0.0 ? (int)((Req1.originalDestinationTime) / IntervalSec) : (int)((Req1.originalDestinationTime) / IntervalSec) - 1);
					//���ƻs�쥻�𮧰϶���array��temprelaxarry
					 TargetDriver2.temprelaxarry=new ArrayList<String>(TargetDriver2.relaxarry);
					//�R����2�ӹw���̩Ҧ����϶�
					 for(int index = StartInterval1; index <= EndInterval1; index++)
					 {
						//�����b�𮧰϶��N�R��
						 if(index>=startindex&&index<=endindex)
							 TargetDriver2.temprelaxarry.remove(String.valueOf(index));
					 }				 
					//�ק�q����ModifyOriginDriverTable("��l�q����","�n�ƤJ�q��", "�n�ק諸��ƪ�W��");
				   TargetDriver2.ModifyOriginDriverTable(Variable,Req1,TimeUnit,TargetDriver2, "userrequests",IndexMap,car);	
				   TargetDriver2.ArrangedCount++;
				   //�P�B�ݨD��
				   Req1.Arrange=true;
				   if(Req1.AssignSharing!=-1)
					   IndexMap.get(Req1.AssignSharing).Arrange=true;				   
				   }
				 Result=true;		
			 }
			}
		 } catch (Exception e) 
		 {
			 // TODO Auto-generated catch block
			 System.out.println("�ƯZ�X��");
		 }
		 return Result;		
	}
	//�S��ϰ쪺filterprocess :�F�_���P�T�a�L�f�g��
/*public boolean Specialearafilterprocess(List<carGroup> car,List<DriverTable> filterDriverTable,RequestTable Req
				,double TimeUnit,Map<Integer, RequestTable> IndexMap,ILF ilf,int[] FilterEnable) throws Exception
	{
		boolean Found=false;
		DriverTable TargetDriver=null;
		PreRountingArrangerFilter filter=null;
		
		for(int Classification=1;Classification<=5;Classification++)
		{
			List<DriverTable> DriverTable= new LinkedList<DriverTable>(filterDriverTable);
			TargetDriver=null;
			//filter��l��	
			 //filter��l��					 
			 filter = new PreRountingArrangerFilter(Variable.TolerableTime,FilterEnable,Req, TimeUnit,Req.Number, IndexMap,Variable, ilf);
			 //����filter
			 filter.CarFilter(DriverTable);
			// filter.RoadToRoadFilter(filterDriverTable);
			 //�L�o�s��q��
			 filter.AssignSharingCarFilter(DriverTable);		
			 //�ˬd�ɬqfilter
			 filter.StatusFilter(DriverTable);	
			 //�ˬd�W�U�Z�ɬq�@�p�ɤ����i���W�L2��
			 filter.NoMoreThanTwoFilter(filterDriverTable);					
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
			 filter.AssignSharingDistanceTimeFilter(DriverTable);
			 //���o�̨Υq��	
			 TargetDriver = filter.MinFilter(DriverTable,false);
			 if(TargetDriver != null)
			 {
				//�ק�q����ModifyOriginDriverTable("��l�q����","�n�ƤJ�q��", "�n�ק諸��ƪ�W��");			
				TargetDriver.ModifyOriginDriverTable(Variable,Req,TimeUnit,TargetDriver, "userrequests",IndexMap,car);	
				TargetDriver.ArrangedCount++;

				 //�P�B�ݨD��
				 Req.Arrange=true;
				 if(Req.AssignSharing!=-1)
					 IndexMap.get(Req.AssignSharing).Arrange=true;	
				 Variable.DealReqNum++;//��s�i�ת�
				 Variable.recentPercent=(int)((float)Variable.DealReqNum/Variable.reqsize*100);
				 Variable.smt2.executeUpdate("UPDATE progress SET percent ="+Variable.recentPercent+" WHERE `index` =6 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
				 Found=true;
				 break;
			 }else if(Classification==5)
			 {
				 Found=false;
			 }
		}
		return Found;			
	}*/
	//�B�z�D��٪��ɶ��P�]�Z�h�x�_��filterprocess
	public RequestTable filterprocess(boolean NightFlag,Filter filter,List<RequestTable> FilterReqList,int Classification)
	{
		
		  RequestTable TagetReq=null;//�ﭫ���w����		
		//����filter
		 filter.CarFilter(FilterReqList);		
		 if(Classification<=1)
		 {
				//�Ĥ@�q�H����M
				//�����
				filter.RoadToRoadFilter(FilterReqList);	
		 }
		 //�L�o�s��q��
		 filter.AssignSharingCarFilter(FilterReqList);		
		 //�ˬd�ɬqfilter
		 filter.StatusFilter(FilterReqList);		
		 //�����������������w����
		 filter.AreaCorrespond(FilterReqList);	
		//�S�������W�L����
		filter.SpecialCarFilter(FilterReqList);			 
		 //�ˬd�W�U�Z�ɬq�@�p�ɤ����i���W�L2��
		 filter.NoMoreThanTwoFilter(FilterReqList);		
		//�ˬd�ϰ�filter
		 filter.areaFilter(NightFlag,FilterReqList,Classification);		
		//�ˬd�^�t�ϰ�filter
		 //filter.endareaFilter(FilterReqList);		
		//�ˬd�O�_���������𮧮ɶ�
		 filter.restFilter(FilterReqList);
		 //�ˬd�O�_�ӱo�α���e�w���̻P�ӱo�α��U�@�Z�w����
		 filter.DistanceTimeFilter(FilterReqList);	
		 TagetReq = filter.MinFilter(FilterReqList);	  
		  return TagetReq;
	}
	//�B�z��n���{��
   public List<RequestTable> Southwestprocess(int carrun,int carindex,List<carGroup> car,int[] FilterEnable,ILF ilf,Map<Integer, RequestTable> IndexMap
					,List<RequestTable> RequestArray,double TimeUnit,DriverTable DriverTable,List<reqGroup> requestTable) 
	{
	   if(RequestArray.size()>0)
	   {
		   boolean NightFlag=false;
		   //�q����
		   LinkedList<DriverTable> OriginDriverTable= new LinkedList<DriverTable>();
		   if(carrun==0)//Ū���g������
		   {
			   OriginDriverTable =DriverTable.getareafilterDriverTable(carindex,car,Variable); 
		   }
		   else if(carrun==1) //�|�j����
		   {
			   OriginDriverTable =DriverTable.getfFournobileDriverTable(car,Variable); 
			}
		   else if(carrun==2)  // Ū�����M��
		   {			   
			   OriginDriverTable =DriverTable.getareafilterDriverTable(carindex,car,Variable); 
			}
		   for(int i = 0; i < OriginDriverTable.size();i++ )
		   {
			   //�ƯZ�B�z���ɶ����ǡA�̥�l�ثe6�I�b�O���Z�ҥH�]�w13			
			   for(int tineindex=13;tineindex<=46;tineindex++)
			   {
				   //�ثe�n�ƪ��ɶ��I���b�q���W�Z�ɬq
				   if(tineindex>OriginDriverTable.get(i).StartTimeInterval&&tineindex<OriginDriverTable.get(i).EndTimeInterval)
					   for(int Classification=1;Classification<=7;Classification++)//�̾ڰϰ��v�����C�h�j�M
						{
						   //�P�_�O�_�n�Ω]�Z�v��
						   NightFlag=false;
						   RequestTable TagetReq=null;//�ﭫ���w����
						   //���o�i�Ϊ��w����
						   List<RequestTable> FilterReqList = new LinkedList<RequestTable>(RequestArray);
							try 
							{
								//filter��l��		
								Filter filter= new Filter(tineindex,FilterEnable, OriginDriverTable.get(i),TimeUnit,IndexMap,Variable,ilf);
								if(OriginDriverTable.get(i).StartTime>43200)//�̥q�����X�Z�ɶ��h�M�w�O�_�ҥΩ]�Z�v���A�p�G�j�󤤤�12�I�N�ҥΩ]�Z�v��
									 NightFlag=true;
								TagetReq=filterprocess(NightFlag,filter,FilterReqList,Classification);
								if(TagetReq!=null)
								{												
									Modifyinfo(RequestArray,TagetReq, IndexMap ,OriginDriverTable.get(i), TimeUnit, car,requestTable);			
									break;
								}
							} catch (Exception e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
				   }
			   }
		   }
				return RequestArray;
	}
		//�B�z�F�_���{��
		public List<RequestTable> northeastprocess(int specialareaindex,int carindex,List<carGroup> car,int[] FilterEnable,ILF ilf,Map<Integer, RequestTable> IndexMap
				,List<RequestTable> RequestArray,double TimeUnit,DriverTable DriverTable,List<reqGroup> requestTable ) 
		{
			if(RequestArray.size()>0)
			{
				boolean NightFlag=false;			
				//�q����
				LinkedList<DriverTable> OriginDriverTable =DriverTable.getareafilterDriverTable(carindex,car,Variable); 
			  
				//�ھ�specialareaindex�L�o���l
				if(!(specialareaindex==2&&carindex==0))//�b�Ʀ���w���̮ɨ��o������L�o
					filterCar(OriginDriverTable, specialareaindex, IndexMap);				
				for(int i = 0; i < OriginDriverTable.size();i++ )
				{
					//�ƯZ�B�z���ɶ����ǡA�̥�l�ثe6�I�b�O���Z�ҥH�]�w13			
					for(int tineindex=13;tineindex<=46;tineindex++)
					{
						//�ثe�n�ƪ��ɶ��I���b�q���W�Z�ɬq
						 if(tineindex>OriginDriverTable.get(i).StartTimeInterval&&tineindex<OriginDriverTable.get(i).EndTimeInterval)
							 for(int Classification=1;Classification<=7;Classification++)//�̾ڰϰ��v�����C�h�j�M
							 {
								 //�P�_�O�_�n�Ω]�Z�v��
								 NightFlag=false;
								 RequestTable TagetReq=null;//�ﭫ���w����
								 //���o�i�Ϊ��w����
								 List<RequestTable> FilterReqList = new LinkedList<RequestTable>(RequestArray);
								 try 
									{
								    	 //filter��l��		
								    	Filter filter= new Filter(tineindex,FilterEnable, OriginDriverTable.get(i),TimeUnit,IndexMap,Variable,ilf);
								    	if(OriginDriverTable.get(i).StartTime>43200)//�̥q�����X�Z�ɶ��h�M�w�O�_�ҥΩ]�Z�v���A�p�G�j�󤤤�12�I�N�ҥΩ]�Z�v��
								    		NightFlag=true;
								    	TagetReq=filterprocess(NightFlag,filter,FilterReqList,Classification);
								    	if(TagetReq!=null)
										{
											Modifyinfo(RequestArray,TagetReq, IndexMap ,OriginDriverTable.get(i), TimeUnit, car,requestTable);			
											
											break;
										}
									 } catch (Exception e)
									 {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
							 }
					}
					
					
				}
			}
			return RequestArray;
		}	
	//�]�Z�{�ǤΤ�����Ȧ�ɶ������w����
	public void process(List<carGroup> car,int[] FilterEnable,ILF ilf,Map<Integer, RequestTable> IndexMap
			,List<RequestTable> RequestArray,double TimeUnit,DriverTable DriverTable,List<reqGroup> requestTable,int Night) 
	{
		boolean NightFlag=false;
		int tineindex=13;//�ƯZ�B�z���ɶ����ǡA�̥�l�ثe6�I�b�O���Z�ҥH�]�w13
		if(Night==1)//�]�Z���h�O����12�I�}�l �ҥH�]�w23
			tineindex=23;//�ƯZ�B�z���ɶ�����
		for(;tineindex<=46;tineindex++)
		{
			//�q����
			LinkedList<DriverTable> OriginDriverTable =DriverTable.filterDriverTable(car,DriverTable.carsize,tineindex,0,Variable);
			if(OriginDriverTable.size()==0)
				continue;
			//�̾ڰϰ��v�����C�h�j�M			
			for(int Classification=1;Classification<=7;Classification++)
			{
				for(int i = 0; i < OriginDriverTable.size();i++ )
				{
					if(OriginDriverTable.size()==0)
						continue;
					RequestTable TagetReq=null;//�ﭫ���w����
					//���o�i�Ϊ��w����
					List<RequestTable> FilterReqList = new LinkedList<RequestTable>(RequestArray);
				    try 
					{
				    	 //filter��l��		
				    	Filter filter= new Filter(tineindex,FilterEnable, OriginDriverTable.get(i),TimeUnit,IndexMap,Variable,ilf);
				    	if(OriginDriverTable.get(i).StartTime>43200)//�̥q�����X�Z�ɶ��h�M�w�O�_�ҥΩ]�Z�v���A�p�G�j�󤤤�12�I�N�ҥΩ]�Z�v��
				    		NightFlag=true;
				    	TagetReq=filterprocess(NightFlag,filter,FilterReqList,Classification);
				    	if(TagetReq!=null)
						{
							Modifyinfo(RequestArray,TagetReq, IndexMap ,OriginDriverTable.get(i), TimeUnit, car,requestTable);			
							//���ƤJ�Z�����q�������A�קK���ƶi�h�ƯZ
							OriginDriverTable.remove(i);
							i--;
						}
					 } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	public void pairprocess(List<reqGroup> requestTable,List<carGroup> car,int[] FilterEnable,ILF ilf,Map<Integer, RequestTable> IndexMap
			,List<RequestTable> pairarray,double TimeUnit,Map<String, DriverTable> carIndexMap,DriverTable DriverTable,	List<RequestTable> longtimearray) throws Exception
	{
		
		 for(int j=0;j<pairarray.size();)
		  {
			 if(Variable.errorcode<=-2)
					break;
			if(Math.abs(pairarray.get(j).DestinationTime-pairarray.get(j+1).OriginTime)<Variable.differencevalue)
			{
			 //��ӳ�����
		    if(!pairarray.get(j).Arrange&&!pairarray.get(j+1).Arrange)
		    {
		    	 for(int run=0;run<2;run++)
				 {
		    		 //�Ĥ@��req�����l
		    		 List<DriverTable> filterDriverTable =DriverTable.getfilterDriverTable(car,Variable);
			    	 //�ĤG��req���l
		    		 List<DriverTable> filterDriverTable1 =DriverTable.getfilterDriverTable(car,Variable);
					 if(filterprocess(car,run,filterDriverTable,filterDriverTable1,pairarray.get(j)
			    			 ,pairarray.get(j+1),TimeUnit,IndexMap,ilf,FilterEnable))
			    	 {
			    		break;
			    	 }					
				 }
		    	 j+=2;
		    }
		    else if(pairarray.get(j).Arrange&&pairarray.get(j+1).Arrange)//��ӳ��w�ƹL
		    {
		    	j+=2;
		    	continue;				    	
		    }
		    else 
		    {
		    	//�䤤�@�ӥ���
		    	if(pairarray.get(j).Arrange)
		    	{
		    		DriverTable TargetDriver=null;	
		    		TargetDriver=carIndexMap.get(pairarray.get(j).Targetdrivers);
		    		//�ק�q����ModifyOriginDriverTable("��l�q����","�n�ƤJ�q��", "�n�ק諸��ƪ�W��");				    		
		    		DriverTable.ModifyOriginDriverTable(Variable,pairarray.get(j+1),TimeUnit,TargetDriver,"userrequests",IndexMap,car);
		    		//�P�B�ݨD��
		    		requestTable.get(pairarray.get(j+1).index[0]).getreq(pairarray.get(j+1).index[1]).get(pairarray.get(j+1).index[2]).Arrange=true;
		    		if(pairarray.get(j+1).AssignSharing!=-1)
		    			IndexMap.get(pairarray.get(j+1).AssignSharing).Arrange=true;
		    	}
		    	else
		    	{
		    		DriverTable TargetDriver=null;	
		    		TargetDriver=carIndexMap.get(pairarray.get(j+1).Targetdrivers);
		    		//�ק�q����ModifyOriginDriverTable("��l�q����","�n�ƤJ�q��", "�n�ק諸��ƪ�W��");				    		
		    		DriverTable.ModifyOriginDriverTable(Variable,pairarray.get(j),TimeUnit,TargetDriver,"userrequests",IndexMap,car);
		    		//�P�B�ݨD��
		    		requestTable.get(pairarray.get(j).index[0]).getreq(pairarray.get(j).index[1]).get(pairarray.get(j).index[2]).Arrange=true;
		    		if(pairarray.get(j).AssignSharing!=-1)
		    			IndexMap.get(pairarray.get(j).AssignSharing).Arrange=true;
		    	}
		    	j+=2;
		     }
		  }
		 else
		 {
			if(!pairarray.get(j).Arrange)
			{
				longtimearray.add(pairarray.get(j));
			}
			
			if(!pairarray.get(j+1).Arrange)		    
			{
				longtimearray.add(pairarray.get(j+1));
			}
			j+=2;
		  }
	    }
	}
	
	public DriverTable MinFilter(List<DriverTable> DriverList,List<DriverTable> DriverList1) throws IOException
	{		
		DriverTable TargetDriver=null;//�w���̿ﭫ���q��	
		List<DriverTable> CandidateDriverList=new ArrayList<DriverTable>(200);
		if(DriverList.size()>0&&DriverList1.size()>0)
		{
			
			for(int i=0;i<DriverList.size();i++)
			{
				for(int j=0;j<DriverList1.size();j++)
				{
					if(DriverList.get(i).ID.indexOf(DriverList1.get(j).ID)!=-1)
					{
						CandidateDriverList.add(DriverList.get(i));
					}
				}
			}
		}
		//�q�����`�ƭn�j��0�~���
		int minValue =999;//�����q����w���̪��̵u�Z��
		
		for(int i = 0; i < CandidateDriverList.size(); i++)
		{//�p�G��������F���w�����W���a�I�Ҫ�O���ɶ����O�����ɶ��u
		    if(CandidateDriverList.get(i).StartDistanceValue< minValue)
			  {
			   //��s��q�ɶ�����
			   minValue = CandidateDriverList.get(i).StartDistanceValue;
			   //��s�ﭫ����							
			  TargetDriver=CandidateDriverList.get(i);
			  }	
		}
	
		return TargetDriver;
	}
	public   static   List<RequestTable>  removeDuplicateWithOrder(List<RequestTable> list)  
	{ 
		for(int z=0;z<list.size()-1;z++)
		{
			for(int j=list.size()-1;j>z;j--)
			{
				if(list.get(j).equals(list.get(z)))  
				{ 
			        list.remove(j); 
			    } 
			}
		}
	     return list;
	}
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
	public void Modifyinfo(	List<RequestTable> OriginReqlist,RequestTable req,Map<Integer, RequestTable> IndexMap ,DriverTable TargetDriver,double TimeUnit,List<carGroup> car,List<reqGroup> requestTable)
	{
		//�ק�q����ModifyOriginDriverTable("��l�q����","�n�ƤJ�q��", "�n�ק諸��ƪ�W��");
		TargetDriver.ModifyOriginDriverTable(Variable,req,TimeUnit,TargetDriver, "userrequests",IndexMap,car);	
		//�N�w���̼аO���w�ƹL�Z
		req.Arrange = true;	
		OriginReqlist.remove(req);
		Variable.DealReqNum++;//��s�i�ת�
		if(req.AssignSharing!=-1)
		{
			IndexMap.get(req.AssignSharing).Arrange=true;
			Variable.DealReqNum++;//��s�i�ת�
			OriginReqlist.remove(IndexMap.get(req.AssignSharing));
		}		
		Variable.recentPercent=(int)((float)Variable.DealReqNum/Variable.reqsize*100);
		try {
			Variable.smt2.executeUpdate("UPDATE progress SET percent ="+Variable.recentPercent+" WHERE `index` =6 and `date`='"+Variable.date+"' and `time`='"+Variable.time+"'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//�L�o�S�]�t�ϰ쪺�����H�Q�@�ϰ춰���ƯZ
	public void filterCar(LinkedList<DriverTable> OriginDriverTable,int  specialareaindex,Map<Integer, RequestTable> IndexMap )
	{
		for(int i = 0; i < OriginDriverTable.size();i++ )
		{
			if(OriginDriverTable.get(i).CallNum.indexOf("52")!=-1)
				System.out.println("222");
			String[] Area=new String[2];
			//���줤������
			int StopTimeIndex=(OriginDriverTable.get(i).StartTimeInterval+OriginDriverTable.get(i).EndTimeInterval)/2;
			//�������W�b���̫�U�����ϰ�(�q��node,�_�l�϶�,�����϶�,1�N�����-1�N���e��,�w���̬M�g��)
			Area[0]=GetArea(OriginDriverTable.get(i), OriginDriverTable.get(i).StartTimeInterval, StopTimeIndex, 1, IndexMap);
			
			//�����U�b���̫�U�����ϰ�(�q��node,�_�l�϶�,�����϶�,1�N�����-1�N���e��,�w���̬M�g��)
			Area[1]=GetArea(OriginDriverTable.get(i), OriginDriverTable.get(i).EndTimeInterval, StopTimeIndex, -1, IndexMap);
			
			for(int area=0;area<Variable.Specialarea[specialareaindex].length;area++)
			{
				if(Variable.switchareaindex(Area[0])==Variable.Specialarea[specialareaindex][area]
								 ||Variable.switchareaindex(Area[1])==Variable.Specialarea[specialareaindex][area])
				{
					//�N���]�t�ϰ�				
					break;
				}else if(area==(Variable.Specialarea[specialareaindex].length-1))	
				{
					//�p�G���U�Z�����j�S�]�t�ϰ�N�R��
					OriginDriverTable.remove(i);
					i--;
				}
			}	
		}	
	}
	//���o�줤�I�̫�W�U�����ϰ�
	public String GetArea( DriverTable driver,int starttimeindex,int stoptimeindex,int direction,Map<Integer, RequestTable> indexmap)
	{
		String Area="";			
		for(int timeindex=starttimeindex;timeindex<=stoptimeindex;timeindex=timeindex+direction)
		{
			if(driver.TimeInterval[timeindex].indexOf("���ƯZ")==-1&&driver.TimeInterval[timeindex].indexOf("���W�Z")==-1)
			{
				 String[]  temp =driver.TimeInterval[timeindex].split("_");
				 RequestTable Requst= indexmap.get(Integer.valueOf(temp[0]));
				 if(starttimeindex<stoptimeindex)
					 Area=Requst.Destinationarea;
				 else
					 Area=Requst.Originarea;
			}
	
		}
		return Area;
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub

	}
	

}
