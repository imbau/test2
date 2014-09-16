
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Workbook;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Servlet implementation class ReadExcel
 */
@WebServlet("/WriteExcel.view")
public class WriteExcel extends HttpServlet {
	private static final long serialVersionUID = 1L;  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WriteExcel() {    	
        super();
      
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		// TODO Auto-generated method stub
		defineVariable Variable;	
		String reqFileName = null;
		LinkedList<DriverTable> OriginDriverTable = new LinkedList<DriverTable>();
		Map<String, DriverTable> CarIndexMap = new HashMap<String, DriverTable>();
		LinkInfo linkinfo = new LinkInfo();	
	
		
		try
		{
			System.out.println("excelstart");
			Variable=new defineVariable();
			Variable.date= request.getParameter("arrangedate");
			Variable.time= request.getParameter("arrangetime");						
			Variable.rs = Variable.smt.executeQuery("SELECT * FROM `arrange_log` WHERE `date`='"+Variable.date+"' AND `time`='"+Variable.time+"'");
			
			if(Variable.rs.next())
			{
				reqFileName = Variable.rs.getString("Reqtable").trim();
			}
			//取得excel
			Workbook book=Workbook.getWorkbook(new File(linkinfo.getUploadLink() + reqFileName));
			DriverTable DriverTable=new DriverTable(Variable.intervalnum);
			OriginDriverTable=DriverTable.readDrivertable(Variable.con,Variable.date,Variable.time,Variable.smt,Variable,OriginDriverTable);
			for(int Driverindex = 0; Driverindex < OriginDriverTable.size();Driverindex++ )
			{
				CarIndexMap.put(OriginDriverTable.get(Driverindex).ID, OriginDriverTable.get(Driverindex));
			}
			writeexcel(book,linkinfo.getDownloadLink() + reqFileName,Variable,CarIndexMap);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
    
	/**
	 * @throws IOException 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	{
	
	}
	//計算資料數量，並回傳數值
	public void writeexcel(Workbook wb,String link,defineVariable Variable,Map<String, DriverTable> CarIndexMap)								//get data count
	{
		ResultSet rs;
		//int index=1;	
		int countlow=0;
		try
		{
	        // 打開一個文件，並指定數據寫回到原文件
		    WritableWorkbook workbook = Workbook.createWorkbook(new File(link),wb);
	        //Excel內的第一個分頁
	        WritableSheet sheet = workbook.getSheet(0);
	        // 字體顏色,背景顏色和Formatting的對象
	        jxl.write.WritableFont wfc = new jxl.write.WritableFont(WritableFont.ARIAL,8,WritableFont.NO_BOLD,false,
	        								jxl.format.UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.BLACK);
	        jxl.write.WritableCellFormat wcfFC = new jxl.write.WritableCellFormat(wfc); 
	        //設定邊框
	        wcfFC.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
	        // Label(列數,行數 ,內容，格式) 列數和行數都是從0開始	     
	        countlow= sheet.getRows();
	        for(int index=1;index<countlow;index++)
	        { 
	        	Cell[] data =  sheet.getRow(index);				//read whole row data
	        	rs=Variable.smt2.executeQuery("SELECT * FROM `userrequests` WHERE  `Reservationnumber`='"+data[0].getContents()+"'   AND  `arrangedate`='"+Variable.date+"' AND `arrangetime`='"+Variable.time+"'");
	        	if(rs.next())
	        	{
	        		//如果有車號就寫入呼號
	        		if(rs.getString("Targetdrivers").trim().indexOf("null")==-1)
	        		{
	        			//輸入車號
	        			jxl.write.Label labelCFC = new jxl.write.Label(1,index,CarIndexMap.get(rs.getString("Targetdrivers")).CallNum,wcfFC); // B6
	        			sheet.addCell(labelCFC);
	        		}
	        		else
	        		{
	        			//空資料
	        			jxl.write.Label labelCFC = new jxl.write.Label(1,index," ",wcfFC); // B6
	        			sheet.addCell(labelCFC);
	        		}
	        	}else
	        	{
	        		jxl.write.Label labelCFC = new jxl.write.Label(1,index,"  ",wcfFC); // B6
	        		sheet.addCell(labelCFC);
	        	}
	        }
	        workbook.write();
	        workbook.close(); 
	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
}
