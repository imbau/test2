import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class AssignSharingRequestTable
{	
	public String RequestNumber;//���������b��
	public String AssignSharingRequestNumber;//�������w�@�����b��
	public int originalStartTime;//���������W���ɶ�
	public int DestinationTime;//���������U���ɶ�
	public int Number;//���������ѧO�X	
	public int AssignSharingNumber;//���������ѧO�X	
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
	//�^�Ǧ�������w�@��array
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
					  requestTable.remove(j);//���R�U�������
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
		//�̱b���Ƨ�
	      Collections.sort(pairarray,
	       new Comparator<AssignSharingRequestTable>() {
	            public int compare(AssignSharingRequestTable o1, AssignSharingRequestTable o2) {
	                return o1.RequestNumber.compareTo(o2.RequestNumber);
	            }
	        });
		 
		 return pairarray;
	}
}
