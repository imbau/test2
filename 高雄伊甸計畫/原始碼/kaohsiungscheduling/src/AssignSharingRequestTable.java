import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class AssignSharingRequestTable
{	
	public String RequestNumber;//紀錄本身帳號
	public String AssignSharingRequestNumber;//紀錄指定共乘的帳號
	public int originalStartTime;//紀錄本身上車時間
	public int DestinationTime;//紀錄本身下車時間
	public int Number;//紀錄本身識別碼	
	public int AssignSharingNumber;//紀錄本身識別碼	
	public AssignSharingRequestTable()
	{
		RequestNumber="null";
		AssignSharingRequestNumber="null";
		originalStartTime=-1;
		DestinationTime=-1;
		Number=-1;
	    AssignSharingNumber=-1;
	}
	
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			System.out.println(e);
			return null;
		}
	}
	//回傳有成對指定共乘array
	public static List<AssignSharingRequestTable> getpairarray(List<AssignSharingRequestTable> requestTable)
	{
		 List<AssignSharingRequestTable> pairarray=new ArrayList<AssignSharingRequestTable>(requestTable.size());	
		
		 for(int i=0;i<requestTable.size();)
		  {
			  boolean found=false;
			  
			  for(int j=0;j<requestTable.size();)
			  {
				  if(requestTable.get(i).RequestNumber.indexOf(requestTable.get(j).AssignSharingRequestNumber)!=-1
						  &&(requestTable.get(i).originalStartTime==requestTable.get(j).originalStartTime||Math.abs(requestTable.get(i).originalStartTime-requestTable.get(j).originalStartTime)<=900))
				  { 
					  requestTable.get(i).AssignSharingNumber=requestTable.get(j).Number;
					  pairarray.add(requestTable.get(i));
					  requestTable.remove(j);//先刪下面的資料
					  requestTable.remove(i);
					  found=true;
					  break;
				  }
				  else
				  {
					  j++;
				  }
			  }
			  if(!found)
			  {
				  i++;
			  }
		  }		
		//依帳號排序
	      Collections.sort(pairarray,
	       new Comparator<AssignSharingRequestTable>() {
	            public int compare(AssignSharingRequestTable o1, AssignSharingRequestTable o2) {
	                return o1.RequestNumber.compareTo(o2.RequestNumber);
	            }
	        });
		 
		 return pairarray;
	}
}
