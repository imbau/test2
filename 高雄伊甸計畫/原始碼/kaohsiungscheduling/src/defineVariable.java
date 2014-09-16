import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;



public class defineVariable {	
	Connection con = null;
	Statement smt = null,smt2=null;
	ResultSet  rs=null,rs2 = null,rs3=null;				
	String date = null;
    String time = null;
    double timeinterval=0.0;
    double recentLat = -1.0, recentLon = -1.0;//�ΨӰO���W�x�������X���a�}���g�n�סA�[�J���ƤJ�C�����Y���Z�������A���������\��ĥΤ��j
    int ProcessTableIndex=-1;  
    int DealReqNum=0;  
    int TolerableTime =600;
	int tolerableStartTime =1800;
	int tolerableEndTime = 2700;	
	int tolerablebacktime= 4500;	
	int IntervalSec=1800;
	int morningpeaktime=900;//�W�Ȧy�p�ɨ詵��ɶ�
	int afternoonpeaktime=1200;//�U�Ȧy�p�ɨ詵��ɶ�
	int tolerableShareTime=900;
	int ZhonghetoXindianAllowTravelTime=1200;//�U�Ȧy�p�ɨ詵��ɶ�
	int timeindex=0;
	int recentPercent=0;	
	int halfworktimeTolerableTime=3600;//�׳��ɺݩ��᪺�ɶ� �ثe���@�p��
	int areanum=32;//32�Ӧa��
    int intervalnum=48;//24�p�ɨC�b�p�ɬ��@��
	int areaPrioritymorningpeaktime=31500;//�B�z�u���B�ϰ쪺���W�y�p�ɨ�
	int areaPrioritystartafternoonpeaktime=59400;//�B�z�U�Ȧy�p�ɨ�_�l�ɶ�16:30
	int areaPriorityendafternoonpeaktime=67200;//�B�z�U�Ȧy�p�ɨ赲���ɶ�18:30
	int areaPrioritystartmorningpeaktime=27600;//�B�z�W�Ȧy�p�ɨ�_�l�ɶ�7:45
	int areaPriorityendmorningpeaktime=31200;//�B�z�W�Ȧy�p�ɨ赲���ɶ�8:30
	int searchRangetime=3600;
	int differencevalue=10800;//���Z���w���̤W���U���ɶ��������̤j�ɶ��~�t
	int errorcode=-1;//����google���~�N�X		
	final static int map_Revise_Traveltime=300;//�ץ�GOOGLE�^�Ǯɶ� �e�Ԯɶ���1����
	int maxofTrip=10;//�w�w�����ȤW����
	int reqsize=0;//�����ثe���`�ƯZ�έԸɱƯZ�����ƹL�Z�w���̼�
	static int[][] AreaWeight;
    static int AssignSharingnum=10;//���w�@���ƥت���l��
    Map<String, Integer> Area = new HashMap<String, Integer> ();//�ϰ��ӽX    
	List<String> Zhonghespecialcar= new ArrayList<String>(10);//���M�S��䴩����	
	List<String> TiroDriver= new ArrayList<String>(10);//�s�⨮	
	List<String> SpecialCar= new ArrayList<String>(10);//�S��	
	List<String>  xindianRoad= new ArrayList<String>(100);//�s������
	double[] input = new double[4];	
	String[] address= new String[2];	
	int nonrelax=21600;
	LinkInfo linkinfo ;
	HttpServletResponse Response;
	int[][] Specialareacar={
			{0,1},
			{3,2,2}
           };
	int[][] Specialarea={
						{12,8,18,23},
						{19,11,7,6},
						{0,10}
	                   };
	//Ū�����Ȧ�ɶ�������
	int[][] longtime={
			{12,8,23,18,31,0,19,11,7,6,30,29,28,27,22,21,1,2,3,4,5,9,10,13,14,15,16,17,20,24,25,26}
			};
    int[][] areaPriority={
							 {17,6,7,15,13,24,25,26,19},//�B�z�u���B�ϰ쪺�y�p�ɨ�
							 {12,23,31,20,18,8,21,20,19,11,7,6,15,13,24,25,26,16,14},//�ϰ��u������
							 {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31}//�ĤT�������A�ˬd�@��
							 };	
    static boolean[][] backareaWeight={ /*req*/
		  				//����,�s��, ���M, �g��	,�éM	,�O��	,�T�l	,�a�q	,���	,�s��	,�`�|	,�L�f	,�^�d	,�K��	,���s	,�H��	,��L	,�x�_��	  ,�򶩥�	 ,��鿤		,�Q��	,�W�L	,����	 ,����	,����     ,�T��	 ,Ī�w   ,���s	 ,�T��	 ,�۪�	 ,�U�� ,����
    		/*car����*/	{true,true,true ,false  ,true   ,true  ,false   ,false	,true	,false	,true	 ,false ,true  ,false	,false	,false	,false	,true	  ,true     ,false		,false	,false	,true	 ,true	 ,false	 ,false  ,false  ,true   ,true  ,true   ,true ,true},
    		/* �s��*/   {true,true,true ,true   ,true   ,true  ,true   ,true		,false	,true	,true	 ,true ,false  ,true		,true	,true	,true	,true	  ,false     ,false		,true	,true	,true	 ,false	 ,false	 ,false  ,false  ,false   ,false  ,false   ,false ,true},
    		/*���M*/	 	{true,true,true ,true  ,true   ,true  ,true   ,true		,false	,true	,false	 ,true ,false  ,true		,true	,true	,true	,true	  ,false     ,true		,false	,false	,true	 ,false	 ,true	 ,true   ,true  ,false   ,false  ,false   ,false ,false},
    		/*�g��*/		{false,true,true ,true  ,true   ,true  ,true   ,true		,false	,true	,false	 ,true ,false  ,true		,true	,false	,true	,true	  ,false     ,true		,false	,false	,false	 ,false	 ,true	 ,true   ,true  ,false   ,false  ,false   ,false ,false},
    					};
    
    
    //����P�g���v�����ϥ�
	static int[][] nightareaWeight={ /*req*/
					  //����,�s��,���M,�g��,�éM,�O��,�T�l,�a�q,���,�s��,�`�|,�L�f,�^�d,�K��,���s,�H��,��L,�x�_��,�򶩥�,��鿤,�Q��,�W�L,����,����,����,�T��,Ī�w,���s,�T��,�۪�,�U��,����
		 /*car����*/		{9	,9	,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,9	,9	,9	  ,9     ,9		,9	,9	,9	 ,9	 ,9	 ,9	  ,9  ,9   ,9  ,9   ,9  ,9},
			/* �s��*/   {2	,1  ,1	 ,3  ,1   ,3   ,9   ,9 	,3	,3	,1	 ,9	 ,3	  ,9	,9	,9	,3	,2	  ,3     ,9		,1	,1	,1   ,3  ,9	 ,3	  ,9  ,9   ,9  ,9   ,9  ,3},
			/*���M*/	 	{3  ,1  ,1   ,1   ,1   ,2   ,3   ,3  ,9	,3	,9	 ,3	 ,9	  ,3	,3	,3	,3	,2	  ,9     ,3		,9	,9	,9	,9  ,3	 ,3	  ,3  ,9   ,9  ,9   ,9  ,9},
			/*�g��*/		{9	,9  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9   ,9	,9	,9	,9	,9	  ,9     ,9		,9	,9	,9	,9  ,9	 ,9	  ,9  ,9   ,9  ,9   ,9  ,9},
			             };
	//�Y���Z�v��	
	static int[][] areaWeight={ /*req*/
					  //����,�s��,���M,�g��,�éM,�O��,�T�l,�a�q,���,�s��,�`�|,�L�f,�^�d,�K��,���s,�H��,��L,�x�_��,�򶩥�,��鿤,�Q��,�W�L,����,����,����,�T��,Ī�w,���s,�T��,�۪�,�U��,����
		 /*car����*/		{2	,9	,9   ,9  ,9   ,9   ,9   ,9	,1	,9	,9	 ,9	 ,1	  ,9	,9	,9	,9	,9	  ,9     ,9		,9	,1	,9	 ,1	 ,9	 ,9	  ,9  ,9   ,9  ,9   ,9  ,9},
			/* �s��*/   {1	,2  ,3	 ,9  ,3   ,9   ,9   ,9 	,9	,9	,2	 ,9	 ,9	  ,9	,9	,9	,9	,9	  ,9     ,9		,2	,2	,2   ,9  ,9	 ,9	  ,9  ,9   ,9  ,9   ,9  ,9},
			/*���M*/	 	{9  ,3  ,2   ,1  ,2   ,3   ,1   ,1  ,9	,3	,9	 ,9	 ,9	  ,9	,9	,9	,3	,9	  ,9     ,9		,9	,9	,9	,9  ,9	 ,9	  ,9  ,9   ,9  ,9   ,9  ,9},
			/*�g��*/		{9	,9  ,9   ,3  ,9   ,9   ,1   ,1	,9	,9	,9	 ,9	 ,9   ,9	,9	,9	,1	,9	  ,9     ,2		,9	,9	,9	,9  ,9	 ,9	  ,9  ,9   ,9  ,9   ,9  ,9},
			             };
	static int[][] Weight={ /*req*/
					  //����,�s��,���M,�g��,�éM,�O��,�T�l,�a�q,���,�s��,�`�|,�L�f,�^�d,�K��,���s,�H��,��L,�x�_��,�򶩥�,��鿤,�Q��,�W�L,����,����,����,�T��,Ī�w,���s,�T��,�۪�,�U��,����
			/*car����*/	{1	,2	,9	 ,9	 ,9	  ,9	,9	,9	,1	,9	,2	 ,9	 ,2	  ,9	,9	,9	,9	,2	  ,2     ,9		,9	,9	,9	,2	 ,9  ,9   ,9  ,9  ,9   ,9  ,9   ,1},
			/* �s��*/   	{2	,1	,2	 ,9	 ,2	  ,3	,9	,9 	,9	,9	,1	 ,9	 ,9	  ,9	,9	,9	,9	,2	  ,9     ,9		,1	,1	,1	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,3},
			/*���M*/	 	{9  ,1	,1	 ,1	 ,1	  ,2	,3	,3  ,9	,3	,9	 ,9	 ,9	  ,9	,3	,9	,3	,3	  ,9     ,9		,9	,9	,9	,9	 ,9	 ,3	  ,3  ,9  ,9   ,9  ,9   ,9},
			/*�g��*/		{9	,9	,1 	 ,1	 ,3	  ,2	,2	,2	,9	,3	,9	 ,3	 ,9   ,9	,9	,9	,2	,9	  ,9     ,3		,9	,9	,9	,9	 ,3	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},		
			/*�éM*/		{9	,1	,1	 ,3	 ,1	  ,3	,9	,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,9	,9	,2	  ,9     ,9		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*�O��*/		{9	,3	,2	 ,2	 ,3   ,1		,3	,9	,9	,2	,9	 ,9	 ,9	  ,9	,3	,9	,2	,3	  ,9     ,9		,9	,9	,9	,9	 ,3	 ,2	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*�T�l*/		{9	,9  ,3	 ,2	 ,9	  ,3	,1	,1 	,9	,3	,9	 ,2	 ,9	  ,9	,9	,9	,2	,9	  ,9     ,1		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*�a�q*/		{9	,9  ,3   ,2	 ,9	  ,9	,1	,1	,9	,9	,9	 ,1	 ,9	  ,3	,9	,9	,3	,9	  ,9     ,1		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*���*/		{1	,9  ,9   ,9	 ,9	  ,9	,9	,9	,1	,9	,9	 ,9	 ,2	  ,9	,9	,9	,9	,9	  ,2     ,9		,9	,9	,9	,2	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*�s��*/		{9	,9  ,3   ,3  ,9   ,2   ,3   ,9	,9	,1	,9	 ,3	 ,9	  ,3	,2	,9	,2	,3	  ,9     ,3		,9	,9	,9	,9	 ,2	 ,2	  ,2  ,9  ,9   ,9  ,9   ,9},
			/*�`�|*/		{2	,1  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,1	 ,9	 ,9	  ,9	,9	,9	,9	,2	  ,9     ,9		,9	,9	,2	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*�L�f*/		{9	,9  ,9   ,3  ,9   ,9   ,2   ,1	,9	,3	,9	 ,1	 ,9	  ,2	,2	,9	,3	,9	  ,9     ,2		,9	,9	,9	,9	 ,2	 ,3	  ,3  ,9  ,9   ,9  ,9   ,9},
			/*�^�d*/		{2	,9  ,9   ,9  ,9   ,9   ,9   ,9	,2	,9	,9	 ,9	 ,1	  ,9	,9	,9	,9	,9	  ,3     ,9		,9	,9	,9	,3	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,3},
			/*�K��*/		{9	,9  ,9   ,9  ,9   ,9   ,9   ,3	,9	,3	,9	 ,2	 ,9	  ,1		,3	,2	,9	,3	  ,9     ,9		,9	,9	,9	,9	 ,2	 ,3	  ,3  ,9  ,9   ,9  ,9   ,9},
			/*���s*/		{9	,9  ,3   ,9  ,9   ,3   ,9   ,9	,9	,2	,9	 ,2	 ,9	  ,3	,1	,3	,3	,9	  ,9     ,3		,9	,9	,9	,9	 ,2	 ,3	  ,3  ,9  ,9   ,9  ,9   ,9},
			/*�H��*/		{9	,9  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,2	,3	,1	,9	,2	  ,9     ,9		,9	,9	,9	,9	 ,3	 ,3	  ,3  ,3  ,2   ,3  ,9   ,9},
			/*��L*/		{9	,9  ,3   ,2  ,9   ,2   ,2   ,3	,9	,2	,9	 ,3	 ,9	  ,9	,3	,9	,1	,9	  ,9     ,3		,9	,9	,9	,9	 ,3	 ,3	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*�x�_��*/	{2	,2  ,3   ,9  ,2	   ,3   ,9   ,9	,9	,3	,2	 ,9	 ,9	  ,3	,9	,2	,9	,1	  ,2     ,9		,9	,9	,9	,9	 ,3	 ,2   ,3  ,3  ,3   ,3  ,9   ,9},
			/*�򶩥�*/	{2	,9  ,9   ,9  ,9   ,9   ,9   ,9	,2	,9	,9	 ,9	 ,3	  ,9	,9	,9	,9	,2	  ,1     ,9		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,2   ,3},
			/*��鿤*/ 	{9	,9  ,9   ,3  ,9   ,9   ,1   ,1	,9	,3	,9	 ,2	 ,9	  ,9	,3	,9	,3	,9	  ,9     ,1		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*�Q��*/ 	{9	,1  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,9	,9	,9	  ,9     ,9		,1	,9	,9	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*�W�L*/ 	{9	,1  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,9	,9	,9	  ,9     ,9		,9	,1	,1	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,9},
			/*����*/ 	{9	,1  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,2	 ,9	 ,9	  ,9	,9	,9	,9	,9	  ,9     ,9		,9	,1	,1	,9	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,1},
			/*����*/ 	{2	,9  ,9   ,9  ,9   ,9   ,9   ,9	,2	,9	,9	 ,9	 ,3	  ,9	,9	,9	,9	,9	  ,2     ,9		,9	,9	,2	,1	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,1},
			/*����*/ 	{9	,9  ,9   ,3  ,9   ,3   ,9   ,9	,9	,2	,9	 ,2	 ,9	  ,2	,2	,3	,3	,3	  ,9     ,9		,9	,9	,9	,9	 ,1	 ,1	  ,1  ,9  ,9   ,9  ,9   ,9},
			/*�T��*/ 	{9	,9  ,3   ,9  ,9   ,2   ,9   ,9	,9	,2	,9	 ,3	 ,9	  ,3	,3	,3	,3	,2	  ,9     ,9		,9	,9	,9	,9	 ,1	 ,1	  ,1  ,9  ,9   ,9  ,9   ,9},
			/*Ī�w*/ 	{9	,9  ,3   ,9  ,9   ,3   ,9   ,9	,9	,2	,9	 ,3	 ,9	  ,3	,3	,3	,9	,3	  ,9     ,9		,9	,9	,9	,9	 ,1	 ,1	  ,1  ,9  ,9   ,9  ,9   ,9},
			/*���s*/ 	{9	,9  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,3	,9	,3	  ,9     ,9		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,1  ,1   ,1  ,1   ,9},
			/*�T��*/ 	{9	,9  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,2	,9	,3	  ,9     ,9		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,9  ,1   ,1  ,9   ,9},
			/*�۪�*/ 	{9	,9  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,3	,9	,3	  ,9     ,9		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,1  ,1   ,1  ,2   ,9},
			/*�U��*/ 	{9	,9  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,9	  ,9	,9	,9	,9	,3	  ,2     ,9		,9	,9	,9	,9	 ,9	 ,9	  ,9  ,1  ,9   ,2  ,1   ,9},
			/*����*/ 	{2	,3  ,9   ,9  ,9   ,9   ,9   ,9	,9	,9	,9	 ,9	 ,3	  ,9	,9	,9	,9	,9	  ,3     ,9		,9	,9	,1	,1	 ,9	 ,9	  ,9  ,9  ,9   ,9  ,9   ,1},
						};
	public defineVariable() throws Exception{
		Class.forName("com.mysql.jdbc.Driver");
		linkinfo = new LinkInfo();	
		con = DriverManager.getConnection(linkinfo.getDatabaseURL() , linkinfo.getID(), linkinfo.getPW());
		smt = con.createStatement();	
		smt2 = con.createStatement();
		GetSetting();
		GetSpecialCar();
		GetArea(Area);
		GetAreaWeight();
		
	}
	public void GetAreaWeight() throws SQLException
	{
				AreaWeight = new int[61][]; 	
				int rscrowindex=0;
				for(int index = 0; index< AreaWeight.length; index++) 
				{ 
					AreaWeight[index] = new int[62]; 
			    } 
				rs = smt.executeQuery("SELECT * FROM `area` WHERE 1");					
				while(rs.next())
				{
					for(int rsindex=3;rsindex<=64;rsindex++)						
					 	AreaWeight[rscrowindex][rsindex-3]=rs.getInt(rsindex);
					rscrowindex++;
				}
	}
	public void GetSetting() throws SQLException
	{
		rs = smt.executeQuery("SELECT * FROM `setting` WHERE 1");
		rs.first();
		//Ū���]�w
		if(rs.next())
		{
			TolerableTime=rs.getInt("tolerabletime");
		}
	}
	public void GetXindianRoad() throws SQLException
	{
		ResultSet Xindianrs = null; 
		Xindianrs = smt.executeQuery("SELECT * FROM `xindian` WHERE 1");
		do
		{
		  xindianRoad.add(rs.getString("roadname"));
		}while(Xindianrs.next());
	}
	public void GetSpecialCar() throws SQLException
	{
		rs = smt.executeQuery("SELECT * FROM `filtratecar` WHERE 1");
		rs.first();
		do
		{
			if(rs.getInt("option")==0)//���M���䴩�g�������q��
				Zhonghespecialcar.add(rs.getString("carid"));
			else if(rs.getInt("option")==1)//�s�ӥq�������@��
				TiroDriver.add(rs.getString("carid"));
			else if(rs.getInt("option")==2)//�S���������W�L5��
				SpecialCar.add(rs.getString("carid"));
		}while(rs.next());
		
		
	}
	public Map<String, Integer> GetArea(Map<String, Integer> Area) throws SQLException
	{
		ResultSet rs = null; 
		String sqlQuery="SELECT * FROM `area` WHERE 1";
        try 
        {
			rs=	smt.executeQuery(sqlQuery);
			rs.first();
			do
			{	
				Area.put(rs.getString("Area"),rs.getInt("no"));			
			}while(rs.next());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        return Area;
	}
	

	//��X��U�ݨD��		
	public RequestTable RequestTableQuery(String req,defineVariable Variable,Map<Integer, RequestTable> IndexMap)
	{
		RequestTable tableIndex=null;
		//���P�_��U���W���a�I�b��
		 String[]  temp =req.split("_");
		 tableIndex = IndexMap.get(Integer.valueOf(temp[0]));
		return tableIndex;
	}
	
	//��X�U�@�몺�ݨD��		
	public RequestTable NextRequestTableQuery(DriverTable DriverNode,int EndInterval,defineVariable Variable,Map<Integer, RequestTable> IndexMap)
	{
		RequestTable tableIndex=null;		
		for(int  index = EndInterval; index <=DriverNode.TimeInterval.length; index++)
		{
			//���U�@�Ӧ��ƯZ��TimeInterval				
			if(!(DriverNode.TimeInterval[index].indexOf("���W�Z")!=-1) 
				&&!(DriverNode.TimeInterval[index].indexOf("���ƯZ")!=-1))
			{
				String[]  temp = DriverNode.TimeInterval[index].split("_");					
				tableIndex = IndexMap.get(Integer.valueOf(temp[0]));
				break;
			}
		}
		return tableIndex;
	}	
	//��X�W�@�몺�ݨD��		
	public RequestTable PreRequestTableQuery(DriverTable DriverNode,int StartInterval,defineVariable Variable,Map<Integer, RequestTable> IndexMap)
	{
		RequestTable tableIndex=null;		
		for(int  index = StartInterval; index >=0; index--)
		{
			//���W�@�Ӧ��ƯZ��TimeInterval				
			if(!(DriverNode.TimeInterval[index].indexOf("���W�Z")!=-1) 
					&&!(DriverNode.TimeInterval[index].indexOf("���ƯZ")!=-1))
			{
				String[]  temp = DriverNode.TimeInterval[index].split("_");					
				tableIndex = IndexMap.get(Integer.valueOf(temp[0]));
				 break;
			}
		}
		return tableIndex;
	}	
	//�p��o�@����W�@�뤧�����Ȧ�ɶ�
	public int[] DistanceTime(RequestTable Tripreq,RequestTable Currentreq,ILF ilf,defineVariable Variable,double IntervalSec)
	{
		int [] traveltime=new int[2];//�Ĥ@������W�@��req��Interval index  �ĤG��Ȧ�ɶ�
		try {
			int recentTimeResult=-1;
			//�p��W�@��U���Ҧb���϶�
			int index =(Tripreq.originalDestinationTime/ (int)IntervalSec);
			traveltime[0]=index;//�x�s�W�@��Ҧb���϶�
			Variable.input[0] =Tripreq.DestinationLat;
			Variable.input[1] = Tripreq.DestinationLon;	
			Variable.address[0] =Tripreq.DestinationAddress;
			//��J�o����ƪ��_�I�a�}��T
			Variable.input[2] = Currentreq.OriginLat;
			Variable.input[3] =Currentreq.OriginLon;
			Variable.address[1] = Currentreq.OriginAddress;
			//���o�W�@�Z�����U���a�I���e�w���̤W���a�I���Ȧ�ɶ�
			recentTimeResult = ilf.SearchHistory(Variable.input, Variable.address,Tripreq.DestinationTime);
		
			if(recentTimeResult==-1)//�p�G�Ȧ�ɶ��^��-1����1���s�M��
			{
				Thread.sleep(1000);
				recentTimeResult = ilf.SearchHistory(Variable.input,Variable.address,Tripreq.DestinationTime);
			}	
			traveltime[1]=recentTimeResult;			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return traveltime;
	}
	public static int switchareaindex(String areadata)
	{
	int area=0;
	switch(areadata)
	{	
		case "�s�_�������":
		case "����":
			area=0;
		break;	
		case "�s�_���s����":
		case "�s��":
			area=1;
		break;
		case "�s�_�����M��":
		case "���M":
			area=2;
			break;
		case "�s�_���g����":
		case "�g��":
			area=3;
			break;			
		case "�s�_���éM��":
			area=4;
			break;			
		case "�s�_���O����":
			area=5;
			break;
		case "�s�_���T�l��":
			area=6;
			break;			
		case "�s�_���a�q��":
			area=7;
			break;
		case "�s�_����ڰ�":
			area=8;
			break;
		case "�s�_���s����":
			area=9;
			break;
		case "�s�_���`�|��":
			area=10;
			break;
		case "�s�_���L�f��":
			area=11;
			break;
		case "�s�_���^�d��":
			area=12;
			break;			
		case "�s�_���K����":
			area=13;
			break;		
		case "�s�_�����s��":
			area=14;
			break;
		case "�s�_���H����":
			area=15;
			break;
		case "�s�_����L��":			
			area=16;
			break;		
		case "�x�_���h�L��":	
		case "�x�_���n���":
		case "�x�_���j�P��":	
		case "�x�_���j�w��":			
		case "�x�_��������":			
		case "�x�_�������":			
		case "�x�_����s��":			
		case "�x�_���_���":			
		case "�x�_���Q�s��":
		case "�x�_���U�ذ�":
		case "�x�_���H�q��":
		case "�x�_�����s��":
			area=17;
			break;	
		case "�򶩥��C����":
		case "�򶩥�������":
		case "�򶩥��w�ְ�":
		case "�򶩥����R��":
		case "�򶩥��H�q��":	
		case "�򶩥��x�x��":	
		case "�򶩥�������":	
		case "�򶩥����s��":	
			area=18;
			break;	
		case "��鿤���c��":
		case "��鿤��饫":
		case "��鿤�t�s�m":
		case "��鿤�K�w��":
		case "��鿤�j����":
		case "��鿤�j��m":
		case "��鿤������":
		case "��鿤�s�ζm":
		case "��鿤�[���m":
		case "��鿤�_���m":
		case "��鿤����":
		case "��鿤Ī�˶m":	
		case "��鿤�s��m":	
		case "��鿤���c��":
		case "��鿤����":
		case "��鿤�t�s��":
		case "��鿤�K�w��":
		case "��鿤�j�˰�":
		case "��鿤�j���":
		case "��鿤������":
		case "��鿤�s�ΰ�":
		case "��鿤�[����":
		case "��鿤�_����":
		case "��鿤�����":
		case "��鿤Ī�˰�":	
		case "��鿤�s���":	
			area=19;
			break;	
		case "�s�_���Q�Ӱ�":
			area=20;
			break;	
		case "�s�_���W�L��":
			area=21;
			break;	
		case "�s�_�������":
			area=22;
			break;	
		case "�s�_�����˰�":
			area=23;
			break;
		case "�s�_�����Ѱ�":
			area=24;
			break;	
		case "�s�_���T����":
			area=25;
			break;	
		case "�s�_��Ī�w��":
			area=26;
			break;	
		case "�s�_�����s��":
			area=27;
			break;	
		case "�s�_���T�۰�":
			area=28;
			break;	
		case "�s�_���۪���":
			area=29;
			break;	
		case "�s�_���U����":
			area=30;
			break;	
		case "�s�_�����˰�":
			area=31;
			break;	
		default:
			area=-1;
		    break;
			
			
		}
		return area;
	}
	//�ˬd�Y�Z����2��O�_���Z ���Z���e2��
	public void SetHeadTailteamTime(int StartTimeInterval,int EndTimeInterval,DriverTable Driver,Map<Integer,RequestTable> IndexMap)
	{
		
		//�ŦX�Y�Z�ɶ����Ĥ@�Z�w���̤W���ɶ�
		for(int index=StartTimeInterval;index<=StartTimeInterval+3;index++)
		{
			if(!(Driver.TimeInterval[index].indexOf("���W�Z")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("���ƯZ")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("��")!=-1))
			{
				String[] testnumber = Driver.TimeInterval[index].split("_");
				Driver.startreqtime=IndexMap.get(Integer.valueOf(testnumber[0])).originalStartTime;
				Driver.StartArrange=true;
				Driver.StartTimeInterval=index;
				break;
			}
		}
		//�ŦX���Z�ɶ����̫�@�Z�W���ɶ�
		for(int index=EndTimeInterval;index>=EndTimeInterval-3;index--)
		{
			if(!(Driver.TimeInterval[index].indexOf("���W�Z")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("���ƯZ")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("��")!=-1))
			{

				String[] testnumber = Driver.TimeInterval[index].split("_");
				Driver.endreqtime=IndexMap.get(Integer.valueOf(testnumber[0])).originalStartTime;
				Driver.EndTimeInterval=index;
				Driver.EndArrange=true;
				break;
			}
		}
	}
	//���骩�]�w�Y���Z
	public void SetHeadTailteamTime1(DriverTable Driver,Map<Integer,RequestTable> IndexMap)
	{
		//�ŦX�Y�Z�ɶ����Ĥ@�Z�w���̤W���ɶ�
		for(int index=0;index<=Driver.HalfWorkTimeInterval-1;index++)
		{
			if(!(Driver.TimeInterval[index].indexOf("���W�Z")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("���ƯZ")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("��")!=-1))
			{
				String[] testnumber = Driver.TimeInterval[index].split("_");
				Driver.startreqtime=IndexMap.get(Integer.valueOf(testnumber[0])).originalStartTime;
				Driver.StartArrange=true;
				Driver.StartTimeInterval=index;
				break;
			}
		}
		//�ŦX���Z�ɶ����̫�@�Z�W���ɶ�
		for(int index=Driver.TimeInterval.length-1;index>=Driver.HalfWorkTimeInterval+1;index--)
		{
			if(!(Driver.TimeInterval[index].indexOf("���W�Z")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("���ƯZ")!=-1)
				&&!(Driver.TimeInterval[index].indexOf("��")!=-1))
			{
				String[] testnumber = Driver.TimeInterval[index].split("_");
				Driver.endreqtime=IndexMap.get(Integer.valueOf(testnumber[0])).originalStartTime;
				Driver.EndArrange=true;
				Driver.EndTimeInterval=index;
				break;
			}
		}
	}
	//�ˬd�Y�Z����1��O�_���Z ���Z���e1�� mode:0�ˬd�Y�Z1�ˬd���Z
	public boolean Check(int Interval,DriverTable Driver,int mode,int HalfWorkTime)
	{
		boolean[] CheckStuas={false,false};
		
		if(mode==0)
		{
			//�Y�Z
			int maxindex=0;
			int minindex=0;
			if(Driver.Holiday==0)
			{	//�����ˬd�Y�Z�ɶ���@��
				minindex=Interval;
				maxindex=Interval+3;
			}
			else
			{
				//�����ˬd�줤�Ȯɬq
				maxindex=HalfWorkTime-1;
			}
			for(int index=minindex;index<=maxindex;index++)
			{
				if(!(Driver.TimeInterval[index].indexOf("���W�Z")!=-1)
					&&!(Driver.TimeInterval[index].indexOf("���ƯZ")!=-1)
					&&!(Driver.TimeInterval[index].indexOf("��")!=-1))
				{
					if(index==Interval)
						CheckStuas[0]=true;
					else
						CheckStuas[1]=true;
					break;
				}
				else
				{
					if(index==Interval)
						CheckStuas[0]=false;
					else
						CheckStuas[1]=false;
				}
			}
		}
		else
		{
			//���Z
			int maxindex=0;
			int minindex=0;
			if(Driver.Holiday==0)
			{	
				//�����ˬd���Z�e�@��
				minindex=Interval-3;
				maxindex=Interval;
			}
			else
			{
				//�ˬd�줤�Ȯɬq
				maxindex=Driver.TimeInterval.length-1;
				minindex=HalfWorkTime+1;
			}
			for(int index=maxindex;index>=minindex;index--)
			{
				if(!(Driver.TimeInterval[index].indexOf("���W�Z")!=-1)
					&&!(Driver.TimeInterval[index].indexOf("���ƯZ")!=-1)
					&&!(Driver.TimeInterval[index].indexOf("��")!=-1))
				{
					if(index==Interval)
						CheckStuas[0]=true;
					else
						CheckStuas[1]=true;
					break;
				}
				else
				{
					if(index==Interval)
						CheckStuas[0]=false;
					else
						CheckStuas[1]=false;
				}					
			}
		}
		if(CheckStuas[1]==true||CheckStuas[0]==true)
			return true;
		else
			return false;		
	}	
	public boolean CheckStatus(DriverTable node,defineVariable Variable)
	{
		boolean[] flag={false,false};
		int starttime = node.StartTime + Variable.tolerableStartTime;//�Y�Z�ɶ�
		int endtime = node.EndTime + Variable.tolerableEndTime;//�߯Z�ɶ�:�X�Ԯɶ�+45��		
		//�p���Y�Z�϶�
		int StartInterval = (int)(starttime / Variable.IntervalSec);//�W���ɶ��b�@�Ѥ���interval index
		//�p����Z�϶�
		int EndInterval=(endtime/ Variable.IntervalSec);
		node.HalfWorkTimeInterval=(StartInterval+EndInterval)/2;
		flag[0]=Variable.Check(StartInterval,node,0,node.HalfWorkTimeInterval);
		flag[1]=Variable.Check(EndInterval,node,1,node.HalfWorkTimeInterval);
		if(flag[0]==false||flag[1]==false)
			return true;
		else
			return false;
    }
	//�h���^��r���P���I�Ÿ��A���O�d-�
	public  String clearNotChinese(String buff)
	 {
	   	String tmpString =buff.replaceAll("(?i)[^0-9\u4E00-\u9FA5-]", "");	
	    	return tmpString;
	   }
	public void Checkreq(List<reqGroup> requestTable,String reqnum) 
	{
		boolean Found=false;
		 for(int areaindex=0;areaindex<areanum;areaindex++)
		 { 				
			 for(int timeindex=0;timeindex<intervalnum;timeindex++)
			 { 
				 for(int index=0;index<requestTable.get(areaindex).getreq(timeindex).size();index++)
				 {
					 if(requestTable.get(areaindex).getreq(timeindex).get(index).Number==Integer.valueOf(reqnum))
					 {
						 requestTable.get(areaindex).getreq(timeindex).get(index).Arrange=true;
						 Found=true;
						 break;
					 }
				 }
				 if(Found)
					 break;
			 }
			 if(Found)
				 break;
		 } 
	}
	public void CheckErrorCode(int j)
	{
		switch(j)
		{
			case 2:
				errorcode=-9;						
				break;
			case 8:
				errorcode=-10;
				break;
			case 9:	
				errorcode=-11;
				break;
			default:
				errorcode=-1;
			    break;
		}
		
	}
	public String deleteCommon(String a,Map <String, Integer> Area1)
	{
		  String Area="";
		  String[] Region={"��","�m","��","��"};
		  int lastindex=-1;
		  for(int i=0;i<4;i++)
		  {
			  if(a.lastIndexOf(Region[i])!=-1)
			  {
				  lastindex=a.lastIndexOf(Region[i]);
				  break;
			  }
		  }	
		  if(lastindex>-1)
		  {
			  LinkedHashSet<String> lhs = new LinkedHashSet<String> ();			  
			  for(int i = 0 ; i < lastindex-1 ; i++)
			  {
				  for(int j=0;j<3;j++)
				  {
					  if(Region[j].indexOf(a.substring(i,i+1))==-1)
					  { 
						  lhs.add(a.substring(i,i+1));// ���Ъ��r�� �u�|�d�@�� ,�o�˥i�H�ѨM�R���r�ꤤ���Ʀr�����D~
					  }
					  else
					  {
						  lhs.remove(Region[j]);
					  }
				  }
			  }
			  Iterator<String> iterator=lhs.iterator();
			  while(iterator.hasNext())
			  {
			   Area+=iterator.next();
			  }
			  for (Object key : Area1.keySet()) 
			  {
				  if(a.indexOf(key.toString())!=-1)			     	
				  {
						Area=key.toString();		
						break;
				 }
				 if(key.toString().indexOf(a)!=-1)			     	
				 {
						Area=key.toString();		
						break;
			      }
			  }
		  }	
		  return Area;
	} 
	//�P�_�O�_���s�����s�Ϫ���
	public boolean CheckXindianRoad(String address,defineVariable Variable)
	{ 
		boolean flag=false;
		//int Spendtimecount=0;
		for(int i = 0; i < Variable.xindianRoad.size(); i++)
		{
			if(address.indexOf(Variable.xindianRoad.get(i))!=-1)
			{
				flag=true;	
				break;
			}
		}
		return flag;
	}
	
}
