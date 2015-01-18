package nc.impl.print.out.pdfhtml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.pub.pf.IPrintDataGetter;
import nc.bs.pub.pf.PfUtilTools;
import nc.itf.print.out.pdfhtml.IOutPdfHtml;
import nc.itf.uap.print.IPrintEntry;
import nc.ui.pub.print.IDataSource;
import nc.vo.gl.vouchertools.XML_VoucherTranslator;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;

public class OutPdfHtmlImpl implements IOutPdfHtml {

	private static final String ncHome = RuntimeEnv.getInstance().getNCHome();
	private static final String upload_config = ncHome + "/webapps/nc_web/ncupload/";

	@Override
	public String generateBillHtml(String billno, String billID,
			String billType, String templateid) throws BusinessException {
		String billHtml = null;
		String documentid = null;
		String filePath = "";
		
		try {
			if (StringUtil.isEmptyWithTrim(templateid)){
				return null;
			}
			IDataSource ds = getDataSourceOf(billType, billID);
			if (ds == null){
				return null;
			}
			IPrintEntry printEntry = (IPrintEntry) NCLocator.getInstance().lookup(IPrintEntry.class.getName());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			IDataSource[] dataSources = new IDataSource[] { ds };
			printEntry.exportHtml(dataSources, templateid, new OutputStreamWriter(bos, "UTF-8"));
			billHtml = bos.toString();
			billHtml = "<p align=center>" + billHtml + "</p>";
			filePath = upload_config + billno + ".html";

			XML_VoucherTranslator.saveToFile(filePath, new StringBuffer(billHtml));

		} catch (Exception e) {
			// XXX::����־�쳣�����񲻻ع�
			Logger.error(">>��̨��ӡģ�����HTML����=" + e.getMessage(), e);
		} finally {
			// 20140613,chenxfd,�ļ��ϴ��ɹ�ɾ��������ʱ�ļ�
			nc.vo.jcom.io.FileUtil.delete(new java.io.File(filePath));
		}
		return documentid;
	}

	private IDataSource getDataSourceOf(String billType, String billid) throws BusinessException {
		BilltypeVO btVO = PfDataCache.getBillType(billType);
		String referClzName = btVO.getReferclassname();
		IDataSource ds = null;
		// ��ʹ�õ�����ע�������Դ��ȡ����ȡ
		if (!StringUtil.isEmptyWithTrim(referClzName)) {
			Object o = PfUtilTools.instantizeObject(billType, referClzName.trim());
			if (o instanceof IPrintDataGetter) {
				String checkman = InvocationInfoProxy.getInstance().getUserId();
				try {
					ds = ((IPrintDataGetter) o).getPrintDs(billid, billType, checkman);
				} catch (BusinessException e) {
					Logger.error("��ȡ���ݴ�ӡ����Դ����: " + e.getMessage() + ", ���õ���ʵ��vo����Ԫ��������Դ", e);
				}
			}
		}
		return ds;
	}

}
