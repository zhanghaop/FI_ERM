package nc.impl.print.out.pdfhtml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import nc.bs.er.util.AccrueDataSource;
import nc.bs.er.util.BXDataSource;
import nc.bs.er.util.MatterAppDataSource;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.print.out.pdfhtml.IOutPdfHtml;
import nc.itf.uap.print.IPrintEntry;
import nc.itf.uap.template.ISystemTemplateQueryService;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.pub.print.IDataSource;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;

public class OutPdfHtmlImpl implements IOutPdfHtml {

	private static final String ncHome = RuntimeEnv.getInstance().getNCHome();
	private static final String upload_config = ncHome + "/webapps/nc_web/ncupload/";

	@Override
	public String generateBillHtml(String funccode, String billID,
			String billType,String groupid) throws BusinessException {
		String billHtml = null;

		String templateid = null;
		ISystemTemplateQueryService queryservice = NCLocator.getInstance().lookup(ISystemTemplateQueryService.class);
		try {
			templateid = queryservice.getDefaultSystemplateID(funccode, billType, 3, groupid);
			
			if (StringUtil.isEmptyWithTrim(templateid)){
				templateid = queryservice.getDefaultSystemplateID(funccode, null, 3, groupid);
				if (StringUtil.isEmptyWithTrim(templateid)){
					throw new BusinessException("没有找到该单据的打印模板");
				}
			}
			IDataSource ds = getDataSourceOf(billType+groupid, billID);
			if (ds == null){
				return null;
			}
			IPrintEntry printEntry = (IPrintEntry) NCLocator.getInstance().lookup(IPrintEntry.class.getName());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			IDataSource[] dataSources = new IDataSource[] { ds };
			printEntry.exportHtml(dataSources, templateid, new OutputStreamWriter(bos, "UTF-8"));
			billHtml = bos.toString();
			billHtml = "<p align=center>" + billHtml + "</p>";
			//filePath = upload_config + billID + ".html";

			//XML_VoucherTranslator.saveToFile(filePath, new StringBuffer(billHtml));

		} catch (Exception e) {
			// XXX::仅日志异常，事务不回滚
			Logger.error(">>后台打印模板输出HTML出错=" + e.getMessage(), e);
		} finally {
			// 20140613,chenxfd,文件上传成功删除本地临时文件
//			nc.vo.jcom.io.FileUtil.delete(new java.io.File(filePath));
		}
		return billHtml;
	}

	private IDataSource getDataSourceOf(String billType, String billid) throws BusinessException {
		BilltypeVO btVO = PfDataCache.getBillType(billType);
		String referClzName = btVO.getReferclassname();
		String pk_billtypeid = btVO.getPk_billtypeid();
		String checkman = InvocationInfoProxy.getInstance().getUserId();
		IDataSource ds = null;
		if("ma".equals(btVO.getNcbrcode())){
			AggMatterAppVO[] mattervos = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class)
		    		  .queryBillByPKs(new String[]{billid});
			ds = new MatterAppDataSource(billid, billType, checkman, Arrays.asList(mattervos));
		}else if("ac".equals(btVO.getNcbrcode())){
			AggAccruedBillVO[] accruedvos = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class)
		    		  .queryBillByPks(new String[]{billid});
			ds = new AccrueDataSource(billid, billType, checkman, Arrays.asList(accruedvos));
		}else if("bx".equals(btVO.getNcbrcode())){
			IBXBillPrivate service = NCLocator.getInstance().lookup(IBXBillPrivate.class);
			List<JKBXVO> voList = null;
			voList = service.queryVOsByPrimaryKeys(new String[]{billid},"bx");
			ds = new BXDataSource(billid,billType,checkman,voList);
			// 先使用单据上注册的数据源获取类来取
//			if (!StringUtil.isEmptyWithTrim(referClzName)) {
	//			Object o = PfUtilTools.instantizeObject(billType, referClzName.trim());
	//			if (o instanceof IPrintDataGetter) {
	//				try {
	//					ds = ((IPrintDataGetter) o).getPrintDs(billid, billType, checkman);
	//				} catch (BusinessException e) {
	//					Logger.error("获取单据打印数据源出错: " + e.getMessage() + ", 改用单据实体vo构造元数据数据源", e);
	//				}
	//			}
//				ds = new BXDataSource(billid,billType,checkman,voList);
//			}
		}
		return ds;
	}

}
