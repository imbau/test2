import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
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
@WebServlet("/FalseTrip.view")
public class FalseTrip extends HttpServlet {
	private static final long serialVersionUID = 1L;
    defineVariable Variable;	
    static List<reqGroup> requestTable;	//�ݨD��  
	//�N�Ҧ��ݨD��mIndexMap�H�Q�d�ߤW�@�Z�ΤU�@�Z�w���̪���T
    Map<Integer, RequestTable> IndexMap = new HashMap<Integer, RequestTable>();    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FalseTrip() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	{
		
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=UTF-8");	
		String carid = null;
		String orderhour = null;		
		String ordermin = null;
		String TimeLength = null;
		String Meeting = null;
		String insertSQL = "insert into userrequests (`�ѧO�X`,`arrangedate` ,`arrangetime`,`Reservationnumber`,`Targetdrivers`,`���A`,"+
				"`�@���N�@`,`�m�W`,`�b��`,`telephone`,`level`,`�٧O`,`VisuallyImpaired`,`Wheelchair`,`�ɬq`,`�W���ϰ�`,"+
				 "`�W���a�}`,`�U���ϰ�`,`�U���a�}`,`�q���ɶ�`,`Customcar`,`Waiting`,`subsidizeNumber`,`����`,`��F�ɶ�`,`GETONRemark`,"+
				 "`OffCarRemark`,`sLat`,`sLon`,`eLat`,`eLon`,`arranged`) values (";
		try 
		{
			Variable = new defineVariable();
			//�ƯZ���
			Variable.date = request.getParameter("arrangedate");
	    	Variable.time = request.getParameter("arrangetime");	  
	    	//����
	    	carid = request.getParameter("carid").trim();   
	    	//�B�z�w���ɶ�	    	
    		orderhour = request.getParameter("orderhour");	    		
    		ordermin = request.getParameter("orderminute");
    		//�Ž몺�ɶ�����
    		TimeLength= request.getParameter("TimeLength");
    		//�|ĳ���ܼ�
    		Meeting= request.getParameter("Meeting");
    		RequestTable reqNode = new RequestTable();
    		//�P�_�O�_���|ĳ��
    		if(Meeting.indexOf("1")!=-1)
    		{
    			reqNode.DestinationAddress="�s�_���O���Ϥ�Ƹ��G�q331��";
    			reqNode.OriginAddress="�s�_���O���Ϥ�Ƹ��G�q331��";
    			reqNode.TravelTime=Integer.valueOf(TimeLength)*1800;
    			reqNode.OriginTime= Integer.valueOf(orderhour) * 3600 + Integer.valueOf(ordermin) * 60;
    			reqNode.DestinationTime=reqNode.OriginTime+reqNode.TravelTime-1;
    			reqNode.RequestNumber="�_"; 
    			
    			if(Integer.valueOf(orderhour)<=9)
    				orderhour="0"+orderhour;    		
    			//�p��W�U���ɶ����϶�
    			int StartInterval=(int)(reqNode.OriginTime / Variable.IntervalSec);
    			int EndInterval=((reqNode.DestinationTime% Variable.IntervalSec)  > 0.0 ? (int)(reqNode.DestinationTime / Variable.IntervalSec) : (int)(reqNode.DestinationTime / Variable.IntervalSec)-1);
                //�]�wgps
    			SetGps(reqNode);
    			//���o�w���`��
    			Variable.rs = Variable.smt.executeQuery("SELECT �ѧO�X from userrequests WHERE arrangedate = '" + Variable.date + "' AND arrangetime = '" + Variable.time + "' ORDER BY �ѧO�X");
    			if(Variable.rs.last())
			  	{
    				reqNode.Number = Variable.rs.getInt("�ѧO�X")+1;
			  	}
    			int intervalCount = (int)(24 / 0.5);
    	        DriverTable Target=new DriverTable(intervalCount);
    	        Target =Target.readsingleDrivertable(Variable.con, Variable.date, Variable.time,Variable.smt,carid,Variable);
    			if(!(check(Target,Variable,StartInterval,EndInterval,carid,reqNode.Number).ID.indexOf("null")!=-1))
    			{
    				//�s�W�Ž�
    				insertSQL+=reqNode.Number+",'"+Variable.date+"','"+Variable.time+"','12345','"+carid+"','�Ը�','�_','��l','9999','�L','���d','���d','�_','�_','"+(orderhour+ordermin)+"'";
    				insertSQL+=",'�s�_���O����','��Ƹ��G�q331��','�s�_���O����','��Ƹ��G�q331��','"+(Variable.date+Variable.time)+"','�ƯZ�H��',' ',' ',' ',"+reqNode.DestinationTime+",' ',' ',"+reqNode.OriginLat+","+reqNode.OriginLon+",";
    				insertSQL+=reqNode.DestinationLat+","+reqNode.DestinationLon+",1)";
    				Variable.smt.executeUpdate(insertSQL);    				
    			    Target.UpdateNode(Variable,Target.RestTime1,Target);
    				PrintWriter writer = response.getWriter();
    				writer.write("1,���\�s�W�����w�����");
    				writer.flush();
    				writer.close();
    				Variable.smt.close();
    				Variable.con.close();
    				Target=null;
    			}
    			else
    			{
    				PrintWriter writer = response.getWriter();
    				writer.write("0,�L�k�ƤJ");
    				writer.flush();
    				writer.close();
    				Variable.smt.close();
    				Variable.con.close();
    				Target=null;    				
    			}
    		}
    		else
    		{
    			
    		}
	    	
	    	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public DriverTable check(DriverTable Target,defineVariable Variable,int StartInterval,int EndInterval,String carid,int reqnum) throws IOException, SQLException
	{	
		//���n�ƤJ��������		
		int arrangeflag=0;
		if(Target.TimeInterval[StartInterval].indexOf("���ƯZ")!=-1)
		{
			arrangeflag=1;
		}
		for(int i = StartInterval; i <=EndInterval; i++)
		{
			if(Target.TimeInterval[i].indexOf("���ƯZ")!=-1||arrangeflag==1)
			{
				Target.TimeInterval[i] = String.valueOf(reqnum);
			}
			else
			{
				Target.ID="null";
			}
			if(StartInterval==i)
				arrangeflag=0;
		}
		return Target;		
	}
	private void SetGps(RequestTable reqNode)
	{
		try
		{
			GoogleMapsAPI gmapi = new GoogleMapsAPI(Variable);
			double[] temp =gmapi.GeocodingAPI(reqNode.OriginAddress);
			reqNode.OriginLon = temp[1];
			reqNode.OriginLat = temp[0];
			temp =gmapi.GeocodingAPI(reqNode.DestinationAddress);
			reqNode.DestinationLon = temp[1];
			reqNode.DestinationLat = temp[0];
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
