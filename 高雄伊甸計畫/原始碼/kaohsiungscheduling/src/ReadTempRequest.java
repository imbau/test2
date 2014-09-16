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
	//以下車地點分類
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
	//讀取單一個預約者
	public RequestTable readsinglereq(ResultSet rs,defineVariable Variable) 
	{
		RequestTable reqtable = new RequestTable();	
		try 
		{
			rs.first();
			reqtable.Number =Integer.valueOf(rs.getString("識別碼"));		
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
	
	
	//讀取已經經過排班的預約資料，重建預約資料	
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
				reqtable.Number =Integer.valueOf(rs.getString("識別碼"));					
				//以上車地點分類
				req.get(defineVariable.switchareaindex(reqtable.Originarea)).addreq(reqtable,(int)(reqtable.OriginTime)/1800);
				//以下車地點分類
				tailrequestTable.get(defineVariable.switchareaindex(reqtable.Destinationarea)).addreq(reqtable,(int)(reqtable.OriginTime)/1800);				
				tailreq.get(defineVariable.switchareaindex(reqtable.Destinationarea)).addreq(reqtable,(int)(reqtable.OriginTime)/1800);
			
				if(reqtable.Status==1&&reqtable.Arrange!=true)
					Normalreqsize++;//紀錄未排過班正常預約者數
				if(reqtable.Status==2&&reqtable.Arrange!=true)
					candidatereqsize++;//紀錄未排過班正常預約者數					
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
	//將必要資訊填入
	public RequestTable buildRequestNode(ResultSet rs,defineVariable Variable)
	{
		int hour = 0, min = 0;		
		RequestTable reqNode = new RequestTable();
		String oncartime="null";
		//ILF smartSearch = new ILF(con);
		try
		{
			//候補與否
			if(rs.getString("狀態").equals("候補"))
			{
				reqNode.Status = 2;
			}
			else
			{
				reqNode.Status = 1;
			}
			
			//帳號
			reqNode.RequestNumber = rs.getString("帳號").trim();			
			//共乘資訊
			if(rs.getString("共乘意願").trim().indexOf("否")!=-1)
			{
				reqNode.Share = false;
			}
			else 
			{
				reqNode.Share = true;
			}	
			
			//上車地址
			reqNode.Originarea=rs.getString("上車區域").trim();	
			reqNode.OriginAddress = rs.getString("上車區域").trim()+ rs.getString("上車地址").trim();
			if(reqNode.Originarea.indexOf("新店")!=-1)
			 {
				reqNode.xindianRoadSplitArea[0]=Variable.CheckXindianRoad(reqNode.OriginAddress,Variable);
			 }
			//下車地址
			reqNode.Destinationarea = rs.getString("下車區域").trim();
			reqNode.DestinationAddress = rs.getString("下車區域").trim()+ rs.getString("下車地址").trim();
			if(reqNode.Destinationarea.indexOf("新店")!=-1)
			 {
				reqNode.xindianRoadSplitArea[1]=Variable.CheckXindianRoad(reqNode.DestinationAddress,Variable);
			 }
			//預約時間，起點經度緯度XY座標，終點經度緯度XY座標，抵達時間，旅行時間資訊
			//smartSearch.SearchHistory(data[9].getContents(), data[10].getContents() + data[11].getContents(), data[12].getContents() + data[13].getContents(), reqNode);
			reqNode.OriginLat = rs.getDouble("sLat");
			reqNode.OriginLon = rs.getDouble("sLon");
			reqNode.DestinationLat = rs.getDouble("eLat");
			reqNode.DestinationLon = rs.getDouble("eLon");
			oncartime = rs.getString("時段");
			hour = Integer.valueOf(oncartime.substring(0, 2));	//hour = XX
			min = Integer.valueOf(oncartime.substring(2, 4));		//min = YY			
			reqNode.OriginTime = hour * 3600 + min * 60;				
			reqNode.Targetdrivers=rs.getString("Targetdrivers").trim(); 
			reqNode.DestinationTime = rs.getInt("抵達時間"); 	
			if(reqNode.DestinationTime == -1)
			{
				reqNode.TravelTime = -1;
			}
			else
			{
				reqNode.TravelTime = reqNode.DestinationTime - reqNode.OriginTime;
			}
			//指定共乘資訊
			if((rs.getString("共乘意願").trim().indexOf("可")!=-1
				||rs.getString("共乘意願").trim().indexOf("否")!=-1))
			{
				reqNode.AssignSharing=-1;
			}
			else
			{
				if(!SearchSharingData(reqNode,reqNode.RequestNumber,rs.getString("共乘意願"),1))
				{
					//假設查不到就反查
					SearchSharingData(reqNode,rs.getString("共乘意願"),reqNode.RequestNumber,0);
				}				
			}
			reqNode.Car = rs.getString("車種");
			reqNode.Arrange = (rs.getInt("arranged") == 1)?true:false;
			return reqNode;
		}
		catch(Exception e)
		{
			
			return null;
		}
		
	}
	//將必要查詢共乘資訊
	public boolean SearchSharingData(RequestTable reqNode,String req1,String req2,int index) throws SQLException, IOException 
	{
		
		ResultSet AssignSharingrs = null;
		String reqinfo=req1+"_"+req2;	
		AssignSharingrs = defineVariable.smt2.executeQuery("SELECT AssignSharing  FROM `travelinformationofcarsharing` WHERE  `date`='" +date + "' and  `arrangetime`= '" 
				+time + "' AND `車上乘員`='"+reqinfo+"' and `starttime`="+reqNode.OriginTime);
		
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
