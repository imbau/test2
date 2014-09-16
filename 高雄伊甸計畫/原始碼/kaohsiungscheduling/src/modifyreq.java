import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
@WebServlet("/modify.view")
public class modifyreq extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private String Lat = null, Lon = null;
    private int X = -1, Y = -1;
    defineVariable Variable;	
    List<reqGroup> requestTable;	//�ݨD��
	//�N�Ҧ��ݨD��mIndexMap�H�Q�d�ߤW�@�Z�ΤU�@�Z�w���̪���T
    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>(); 
    //List<carGroup> car = new ArrayList<carGroup>();//������	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public modifyreq() {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException 
	{
		
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=UTF-8");		
		String orderhour = null;
		String ordermin = null;		
		String startarea = null, startadd = null, endarea = null, endadd = null;	
		String cartype = null;
		String sLat = null, sLon = null, eLat = null, eLon = null;
		String name = null;
		String account = null;	
		String startRemark="null";
		String endRemark="null";		
		String telephone="null";
		String reqidentifier="null";	
		String Disabilities="null";	
		int traveltime=0;
		int startsec=-1;
		int endsec=-1;
	    try {
	    	System.out.println("start");
	    	Variable = new defineVariable();
	    	Variable.date = request.getParameter("arrangedate");
	    	Variable.time = request.getParameter("arrangetime");	    	
	    	//�b��
	    	account = request.getParameter("account").trim();
	    	//�W�U���ϰ�Φa�}
	    	startarea = request.getParameter("startarea").trim();
	    	startadd = request.getParameter("startadd").trim();	    	
	    	endarea = request.getParameter("endarea").trim();
	    	endadd = request.getParameter("endadd").trim();  
	    	startRemark= request.getParameter("startRemark").trim();
    		endRemark= request.getParameter("endRemark").trim();
    		orderhour=request.getParameter("orderhour").trim();
    		ordermin=request.getParameter("orderminute").trim();    		
    		telephone= request.getParameter("telephone").trim(); 
    		Disabilities= request.getParameter("Disabilities").trim(); 
    		reqidentifier= request.getParameter("reqidentifier").trim(); 
    		traveltime=Integer.valueOf(request.getParameter("traveltime"));
    		startsec = Integer.valueOf(orderhour) * 3600 + Integer.valueOf(ordermin) * 60;
    		endsec=startsec+traveltime;
    		name= request.getParameter("name").trim(); 
    	    String sql = "UPDATE userrequests SET �m�W='"+name+"', �b�� ='"+account+"' ,telephone='"+telephone+"' ,�٧O='"+Disabilities+
    	    		     "',�ɬq='"+orderhour+ordermin+"',�W���ϰ�='"+startarea+"',�W���a�}='"+startadd+"',�U���ϰ�='"+endarea+"',GETONRemark='"+startRemark+"',OffCarRemark='"+endRemark
    	    		      +"',�U���a�}='"+endadd+"',��F�ɶ�="+endsec+" WHERE �ѧO�X = '" + reqidentifier + "' AND arrangedate = '" + Variable.date +"' AND arrangetime = '" + Variable.time + "'";
    	    Variable.smt.executeUpdate(sql);	    		
    		PrintWriter writer = response.getWriter();
			writer.write("1,���\�ק惡���w�����");
			writer.flush();
			writer.close();
			Variable.smt.close();
			Variable.con.close();			
		} catch (Exception e) {
			// TODO �۰ʲ��ͪ� catch �϶�
			PrintWriter writer = response.getWriter();
			writer.write("0,�L�k�ƤJ�������!!");
			writer.flush();
			writer.close();
		}		
	  
}
	private void InsertSharingData(RequestTable req1,DriverTable Target,int StartInterval) throws SQLException, IOException
	{
		
		for(int i = StartInterval; i <= StartInterval; i++)
		{
			if(Target.TimeInterval[i].indexOf("���ƯZ")!=-1)
			{
				req1.AssignSharing=-1;
			}
			else
			{
				req1.AssignSharing=Integer.valueOf(Target.TimeInterval[i]);
				RequestTable req2=IndexMap.get(req1.AssignSharing);		
				req2.AssignSharing=req1.Number;
				
				String reqinfo=null;
				String reqinfo1=null;
				int arrivetime=0;
				String[] Order=new String[2];
				if(req1.DestinationTime>req2.DestinationTime)
				{
					arrivetime=req1.DestinationTime;
					Order[0]="1_1";
					Order[1]="1_0";	
					reqinfo=req2.Number+"_"+req1.Number;
					reqinfo1=req2.RequestNumber+"_"+req1.RequestNumber;
				}else
				{
					arrivetime=req2.DestinationTime;
					Order[0]="1_1";
					Order[1]="0_1";	
					reqinfo=req1.Number+"_"+req2.Number;
					reqinfo1=req1.RequestNumber+"_"+req2.RequestNumber;
				}
				
				String sql ="INSERT INTO travelinformationofcarsharing(AssignSharing, ���W����,date,arrangetime,�_�I,���~�I1,���~�I2,���I,arrivetime,starttime)VALUES (";
					   sql+="'"+reqinfo+"' , ";//�w��������
				       sql+="'"+reqinfo1+"' , ";//���W����
				       sql+="'"+Variable.date+"' , '"+Variable.time+"',";//���
				       sql+="'0_0' , "+"'"+Order[0]+"' , "+"'"+Order[1]+"' , "+"'0_0' ,";//�_�I,���~�I1,���~�I2,���I
				       sql+=arrivetime+","+req1.OriginTime+")";//arrivetime,starttime
				       Variable.smt.executeUpdate(sql);
			}
		}
		
	}
	public DriverTable check(DriverTable Target,defineVariable Variable,int StartInterval,int EndInterval,String carid,int reqnum,int sharing) throws IOException, SQLException
	{	
		//boolean flag=false;
		//���n�ƤJ��������		
		int arrangeflag=0;
		if(Target.TimeInterval[StartInterval].indexOf("���ƯZ")!=-1)
		{
			arrangeflag=1;
		}
		if(Target.Holiday==1)
				arrangeflag=1;
		for(int i = StartInterval; i <= EndInterval; i++)
		{
			
			if(sharing!=0)
			{
				RequestTable req1=IndexMap.get(reqnum);
				RequestTable req2=IndexMap.get(req1.AssignSharing);				
				if(UpdateSharingData(req1,req2))
				{
				  Target.TimeInterval[i] = String.valueOf(req1.Number+"_"+req2.Number);
				}else
				{
					Target.TimeInterval[i] = String.valueOf(req2.Number+"_"+req1.Number);
				}
			}
			else
			{			
				if((Target.TimeInterval[i].indexOf("���ƯZ")!=-1||Target.TimeInterval[i].indexOf("���W�Z")!=-1)&&arrangeflag==1)
				{
					Target.TimeInterval[i] = String.valueOf(reqnum);
				}
				else
				{
					Target=null;
				}
			}
			//flag=true;
			//}
			//else
			//{
			// flag=false;
			//}
		}
		//if(flag)
			return Target;
		//else
			//return null;
	}
	private boolean UpdateSharingData(RequestTable req1,RequestTable req2) throws SQLException, IOException
	{
		String reqinfo=null;
		int arrivetime=0;
		String[] Order=new String[2];
		reqinfo=String.valueOf(req1.Number)+"_"+String.valueOf(req2.Number);				
		ResultSet rs = Variable.smt2.executeQuery("SELECT AssignSharing FROM travelinformationofcarsharing WHERE date = '" + Variable.date + "' AND arrangetime = '" 
				+ Variable.time + "' AND `AssignSharing`='"+reqinfo+"'");
		if(rs.next())
		 {
			if(req1.DestinationTime>req2.DestinationTime)
			{
				arrivetime=req1.DestinationTime;
				Order[0]="1_1";
				Order[1]="1_0";	
			
			}else
			{
				arrivetime=req2.DestinationTime;
				Order[0]="1_1";
				Order[1]="0_1";	
			
		    }
			String sql = "UPDATE travelinformationofcarsharing SET `arrivetime`="+arrivetime+
					", `���~�I1`='"+Order[0]+"', `���~�I2`='"+Order[1]+"' WHERE `date`= '" + Variable.date + "' AND arrangetime = '" + Variable.time+"' AND `AssignSharing`='"+reqinfo+"'";
			Variable.smt.executeUpdate(sql);			   
			return true;
		 }else
		 {
			return false;
		 }
		
	}
	private void GetGeocode(Statement smt, String area, String add)
	{
		ResultSet rs2 = null;
		Lat = null;
		Lon  = null;
		X = -1;
		Y = -1;
		try
		{
			rs2 = smt.executeQuery("SELECT �W���a�}�g��, �W���a�}�n��, �W���a�}X, �W���a�}Y from traveltime WHERE �W���a�} ='" + area + add + "'");
			if(rs2.next())
			{
				Lon = rs2.getString("�W���a�}�g��");
				Lat = rs2.getString("�W���a�}�n��");
				X = rs2.getInt("�W���a�}X");
				Y = rs2.getInt("�W���a�}Y");
			}
			else
			{
				rs2.close();
				rs2 = smt.executeQuery("SELECT �U���a�}�g��, �U���a�}�n��, �U���a�}X, �U���a�}Y from traveltime WHERE �U���a�} ='" + area + add + "'");
				if(rs2.next())
				{
					Lon = rs2.getString("�U���a�}�g��");
					Lat = rs2.getString("�U���a�}�n��");
					X = rs2.getInt("�U���a�}X");
					Y = rs2.getInt("�U���a�}Y");
				}
				else
				{
					GoogleMapsAPI gmapi = new GoogleMapsAPI(Variable);
					double[] temp =gmapi.GeocodingAPI(area + add);
					Lon = String.valueOf(temp[1]);
					Lat = String.valueOf(temp[0]);
					Y = (int)((temp[0] - 24) * 110754.8256 + 2655032.3);			//coordinate transform
					X = (int)((temp[1] - 121) * 101745.445 + 250000);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
