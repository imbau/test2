import java.util.ArrayList;
import java.util.List;

public class reqGroup{	
	List<List<RequestTable>> reqGroup;	
	public reqGroup(int timelength){
		//ªì©l¤Æ
		reqGroup = new ArrayList<List<RequestTable>>();		
		 for(int i=0;i<timelength;i++)
			 reqGroup.add(new ArrayList<RequestTable>());
		
		}
	public void addreq(RequestTable reqNode,int timeindex)
	{		
		reqGroup.get(timeindex).add(reqNode);		 
	}
	public void removereq(String reqNode)
	{
		
	}
	public List<RequestTable> getreq(int timeindex)
	{	 		
		return reqGroup.get(timeindex);	
	}	
}