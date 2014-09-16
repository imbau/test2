

public class RecordAddress
{	
	public String originalarea;//原本區域
	public String originaladdress;//原本地址
	public String Address;//	修正後的地址
	public String Description;//修正後的備註
	public String area;//修正後的區域
	public int RecordAddressIndex;//紀錄編輯index
	
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
