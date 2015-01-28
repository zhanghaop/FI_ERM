package nc.itf.print.out.pdfhtml;

import nc.vo.pub.BusinessException;

public interface IOutPdfHtml {
	
	public String generateBillHtml(String funccode,String billID, String billType,String groupid) throws BusinessException;
	
   
}
