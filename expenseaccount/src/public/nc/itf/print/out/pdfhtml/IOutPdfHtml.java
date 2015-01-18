package nc.itf.print.out.pdfhtml;

import nc.vo.pub.BusinessException;

public interface IOutPdfHtml {
	
	public String generateBillHtml(String billno,String billID, String billType,String printTempletid) throws BusinessException;
	
   
}
