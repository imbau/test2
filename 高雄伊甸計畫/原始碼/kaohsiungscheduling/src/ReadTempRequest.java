import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class ReadTempRequest
{
	private Connection con = null;
	private String date = null;
	private String time = null;
	public int Normalreqsize;
	public int candidatereqsize;
    List<reqGroup> tailreq = new ArrayList<reqGroup>();	
	//�H�U���a�I����
	List<reqGroup> tailrequestTable = new ArrayList<reqGroup>();	
	defineVariable defineVariable ;
	public ReadTempRequest(Connection conn, String d, String t) throws Exception
	{
		defineVariable = new defineVariable();
		con = conn;
		date = d;
		time = t;	
		 for(int i=0;i<defineVariable.areanum;i++)
			 	tailreq.add(new reqGroup(defineVariable.intervalnum));
		 for(int i=0;i<defineVariable.areanum;i++)
			 tailrequestTable.add(new reqGroup(defineVariable.intervalnum));
		
		Normalreqsize=0;
		candidatereqsize=0;
		
	}	
	//Ū����@�ӹw����
	public RequestTable readsinglereq(ResultSet rs,defineVariable Variable) 
	{
		RequestTable reqtable = new RequestTable();	
		try 
		{
			rs.first();
			reqtable.Number =Integer.valueOf(rs.getString("�ѧO�X"));		
			reqtable=buildRequestNode(rs,Variable);	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
		}
		return reqtable;
	}
	//
	public int GetReqSize(Statement smt)
	{
		ResultSet rs = null;
		 int ReqSize=0;
		try 
		{
			rs = smt.executeQuery("SELECT * FROM userrequests WHERE arrangedate = '" + date + "' AND arrangetime = '" + time + "'");
			if(rs.next())
			{
				rs.last();
				ReqSize=rs.getRow();
			}
			else
				ReqSize=0;	
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ReqSize;
	}
	
	
	//Ū���w�g�g�L�ƯZ���w����ơA���عw�����	
	public List<reqGroup> ReadOrderTable(List<reqGroup>req,defineVariable Variable)
	{		
		try
		{
			Statement smt = con.createStatement();
			ResultSet rs = null;
			rs = smt.executeQuery("SELECT * FROM userrequests WHERE arrangedate = '" + date + "' AND arrangetime = '" + time + "'");
			rs.last();	
			RequestTable reqtable;				
			rs.first();
			do
			{
				reqtable=buildRequestNode(rs,Variable);
				reqtable.RequestTableIndaex=rs.getInt("no");
				reqtable.Number =Integer.valueOf(rs.getString("�ѧO�X"));					
				//�H�W���a�I����
				req.get(defineVariable.switchareaindex(reqtable.Originarea)).addreq(reqtable,(int)(reqtable.OriginTime)/1800);
				//�H�U���a�I����
				tailrequestTable.get(defineVariable.switchareaindex(reqtable.Destinationarea)).addreq(reqtable,(int)(reqtable.OriginTime)/1800);				
				tailreq.get(defineVariable.switchareaindex(reqtable.Destinationarea)).addreq(reqtable,(int)(reqtable.OriginTime)/1800);
			
				if(reqtable.Status==1&&reqtable.Arrange!=true)
					Normalreqsize++;//�������ƹL�Z���`�w���̼�
				if(reqtable.Status==2&&reqtable.Arrange!=true)
					candidatereqsize++;//�������ƹL�Z���`�w���̼�					
			}while(rs.next());
			smt.close();			
			return req;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}	
	public List<reqGroup> ReadEndTable()
	{	
		return tailrequestTable;
	}	
	public int getNormalreqsize()
	{
		return Normalreqsize;
	}
	public int getcandidatereqsize()
	{
		return candidatereqsize;
	}
	public List<reqGroup> gettailreq()
	{
		return tailreq;
	}
	//�N���n��T��J
	public RequestTable buildRequestNode(ResultSet rs,defineVariable Variable)
	{
		int hour = 0, min = 0;		
		RequestTable reqNode = new RequestTable();
		String oncartime="null";
		//ILF smartSearch = new ILF(con);
		try
		{
			//�ԸɻP�_
			if(rs.getString("���A").equals("�Ը�"))
			{
				reqNode.Status = 2;
			}
			else
			{
				reqNode.Status = 1;
			}
			
			//�b��
			reqNode.RequestNumber = rs.getString("�b��").trim();			
			//�@����T
			if(rs.getString("�@���N�@").trim().indexOf("�_")!=-1)
			{
				reqNode.Share = false;
			}
			else 
			{
				reqNode.Share = true;
			}	
			
			//�W���a�}
			reqNode.Originarea=rs.getString("�W���ϰ�").trim();	
			reqNode.OriginAddress = rs.getString("�W���ϰ�").trim()+ rs.getString("�W���a�}").trim();
			if(reqNode.Originarea.indexOf("�s��")!=-1)
			 {
				reqNode.xindianRoadSplitArea[0]=Variable.CheckXindianRoad(reqNode.OriginAddress,Variable);
			 }
			//�U���a�}
			reqNode.Destinationarea = rs.getString("�U���ϰ�").trim();
			reqNode.DestinationAddress = rs.getString("�U���ϰ�").trim()+ rs.getString("�U���a�}").trim();
			if(reqNode.Destinationarea.indexOf("�s��")!=-1)
			 {
				reqNode.xindianRoadSplitArea[1]=Variable.CheckXindianRoad(reqNode.DestinationAddress,Variable);
			 }
			//�w���ɶ��A�_�I�g�׽n��XY�y�СA���I�g�׽n��XY�y�СA��F�ɶ��A�Ȧ�ɶ���T
			//smartSearch.SearchHistory(data[9].getContents(), data[10].getContents() + data[11].getContents(), data[12].getContents() + data[13].getContents(), reqNode);
			reqNode.OriginLat = rs.getDouble("sLat");
			reqNode.OriginLon = rs.getDouble("sLon");
			reqNode.DestinationLat = rs.getDouble("eLat");
			reqNode.DestinationLon = rs.getDouble("eLon");
			oncartime = rs.getString("�ɬq");
			hour = Integer.valueOf(oncartime.substring(0, 2));	//hour = XX
			min = Integer.valueOf(oncartime.substring(2, 4));		//min = YY			
			reqNode.OriginTime = hour * 3600 + min * 60;				
			reqNode.Targetdrivers=rs.getString("Targetdrivers").trim(); 
			reqNode.DestinationTime = rs.getInt("��F�ɶ�"); 	
			if(reqNode.DestinationTime == -1)
			{
				reqNode.TravelTime = -1;
			}
			else
			{
				reqNode.TravelTime = reqNode.DestinationTime - reqNode.OriginTime;
			}
			//���w�@����T
			if((rs.getString("�@���N�@").trim().indexOf("�i")!=-1
				||rs.getString("�@���N�@").trim().indexOf("�_")!=-1))
			{
				reqNode.AssignSharing=-1;
			}
			else
			{
				if(!SearchSharingData(reqNode,reqNode.RequestNumber,rs.getString("�@���N�@"),1))
				{
					//���]�d����N�Ϭd
					SearchSharingData(reqNode,rs.getString("�@���N�@"),reqNode.RequestNumber,0);
				}				
			}
			reqNode.Car = rs.getString("����");
			reqNode.Arrange = (rs.getInt("arranged") == 1)?true:false;
			return reqNode;
		}
		catch(Exception e)
		{
			
			return null;
		}
		
	}
	//�N���n�d�ߦ@����T
	public boolean SearchSharingData(RequestTable reqNode,String req1,String req2,int index) throws SQLException, IOException 
	{
		
		ResultSet AssignSharingrs = null;
		String reqinfo=req1+"_"+req2;	
		AssignSharingrs = defineVariable.smt2.executeQuery("SELECT AssignSharing  FROM `travelinformationofcarsharing` WHERE  `date`='" +date + "' and  `arrangetime`= '" 
				+time + "' AND `���W����`='"+reqinfo+"' and `starttime`="+reqNode.OriginTime);
		
		if(AssignSharingrs.next())
		{
			
			String[] Sharingreqnumber=null;			
			Sharingreqnumber=AssignSharingrs.getString("AssignSharing").trim().split("_");			
			reqNode.AssignSharing= Integer.valueOf(Sharingreqnumber[index]);			
			return true;
		}else
		{
			return false;
		}
		
	}
	
}
