

public class RecordAddress
{	
	public String originalarea;//�쥻�ϰ�
	public String originaladdress;//�쥻�a�}
	public String Address;//	�ץ��᪺�a�}
	public String Description;//�ץ��᪺�Ƶ�
	public String area;//�ץ��᪺�ϰ�
	public int RecordAddressIndex;//�����s��index
	
	public RecordAddress()
	{
		Address="null";
		originalarea="null";
		originaladdress="null";
		Description="";
		area="null";
		RecordAddressIndex=-1;
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
}
