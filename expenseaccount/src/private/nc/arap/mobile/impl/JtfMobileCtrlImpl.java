package nc.arap.mobile.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.arap.mobile.itf.IJtfMobileCtrl;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.lang.StringUtils;

public class JtfMobileCtrlImpl extends ErmMobileCtrlImpl implements IJtfMobileCtrl{

	@Override
	public Map<String, Object> getJkbxCard(String headpk) throws BusinessException {
		String[] queryFields = new String[] { JKBXHeaderVO.PK_JKBX,
				JKBXHeaderVO.TOTAL,
				JKBXHeaderVO.DJRQ, JKBXHeaderVO.DJBH, JKBXHeaderVO.ZY, 
				JKBXHeaderVO.DJLXBM,JKBXHeaderVO.OPERATOR,JKBXHeaderVO.SPZT ,JKBXHeaderVO.SZXMID};
		String[] itemQueryFields = new String[] { BXBusItemVO.PK_BUSITEM,BXBusItemVO.AMOUNT,
				BXBusItemVO.DEFITEM1,BXBusItemVO.DEFITEM3,BXBusItemVO.DEFITEM4,BXBusItemVO.DEFITEM7};
		Map<String, Object> resultmap = new HashMap<String, Object>();
		if(StringUtils.isEmpty(headpk)){
			return resultmap;
		}
		BaseDAO dao = new BaseDAO();
		BXHeaderVO bxheadvo = (BXHeaderVO) dao.retrieveByPK(BXHeaderVO.class, headpk, queryFields);
		if(bxheadvo == null)
			return resultmap;
		Map<String, Map<String,String>> fyxmtyMap = loadExpenseTypeInfoString(bxheadvo.getOperator());
		for (int i = 0; i < queryFields.length; i++) {
			String queryField = queryFields[i];
			String value = ErmMobileCtrlBO.getStringValue(bxheadvo.getAttributeValue(queryField));
			resultmap.put(queryField, value); 
			if(JKBXHeaderVO.DJRQ.equals(queryField)){
				resultmap.put(queryField, new UFDate(value).toLocalString());
			}else if(JKBXHeaderVO.SPZT.equals(queryField)){
				String spztshow = ErmMobileCtrlBO.getSpztShow(bxheadvo.getSpzt());
				resultmap.put("spztshow", spztshow);
			}else if(JKBXHeaderVO.SZXMID.equals(queryField)){
				// 转换收支项目为name
				String attrname = fyxmtyMap.get(value) == null?"":fyxmtyMap.get(value).get("name");
				resultmap.put("ioname", attrname);
			}
		}
		resultmap.remove(JKBXHeaderVO.OPERATOR); 
		
//		byte[] token = NCLocator.getInstance().lookup(IFwLogin.class).login("cj1", "yonyou11", null);
//        NetStreamContext.setToken(token);
//        InvocationInfoProxy.getInstance().setUserCode("cj1");
//        InvocationInfoProxy.getInstance().setUserId("100466100000000002PE");
//        

        
		IBXBillPrivate service = NCLocator.getInstance().lookup(IBXBillPrivate.class);
		BXBusItemVO[] items = service.queryItems(bxheadvo);
		List<Map<String, Object>> itemResultmapList = new ArrayList<Map<String,Object>>();
		resultmap.put("items", itemResultmapList);
		if(items != null && items.length > 0){
			for (int i = 0; i < items.length; i++) {
				Map<String, Object> itemResultmap = new HashMap<String, Object>();
				BXBusItemVO item = items[i];
				for (int j = 0; j < itemQueryFields.length;j++) {
					String queryField = itemQueryFields[j];
					String attrvalue = ErmMobileCtrlBO.getStringValue(item.getAttributeValue(queryField));
					itemResultmap.put(queryField, attrvalue);
					if(BXBusItemVO.DEFITEM1.equals(queryField) && StringUtils.isNotEmpty(attrvalue)){
						itemResultmap.put(queryField, new UFDate(attrvalue).toLocalString());
					}
				}
				itemResultmapList.add(itemResultmap);
			}
		}
		
		// 获取附件列表
		ErmMobileCtrlBO bo = new ErmMobileCtrlBO(defaultDjlxbm);
		List<Map<String, String>> attatchmapList =bo.getFileList(headpk, bxheadvo.getOperator());
		resultmap.put("attachment", attatchmapList);
		  
//		fileservice.downLoadFile(s, outputstream);
//		FileHeader[] filevos = new ErmMobileCtrlBO(defaultDjlxbm).getAttachmentList(bxheadvo.getPk_group(), headpk);
//		filevos[0].get
//		OutputStream out1 = new ByteArrayOutputStream();
//		IFileSystemService fileservice = NCLocator.getInstance().lookup(IFileSystemService.class);
//		IFileTransfer fileTransfer = FileStorageClient.getInstance()
//		.getFileTransfer("erm");
//		String filePath = "/erm/0001Z31000000000W0Z2/IMG_877910.jpg";
//		String startpos = "0";
//		String endpos = "500";
//		OutputStream out = new ByteArrayOutputStream();
//		String fieldid = "0001Z31000000000W0Z3";
//		String fieldp = "0001Z31000000000W0Z2";
//		fileservice.downLoadFile(fieldp, out1);
//		fileTransfer.download(fieldp, out, Long.valueOf(startpos).longValue(), Long.valueOf(endpos).longValue());
//		try {
//			out1.flush();
//			out.flush();  
//		byte bytes[] = ((ByteArrayOutputStream) out).toByteArray();
//		byte bytes1[] = ((ByteArrayOutputStream) out1).toByteArray();
//		byte bese64Bytes[] = Base64.encodeBase64(bytes);
//		
//		LinkedHashMap<String, String> fileMap = new LinkedHashMap<String, String>();
//		attatchmapList.add(fileMap);
//		fileMap.put("content", new String(bese64Bytes));
//		fileMap.put("id", "1");
//		out.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//			// TODO Auto-generated catch block
//		}
//		
//		if(filevos != null && filevos.length> 0){
//			for (int i = 0; i < filevos.length; i++) {
//				FileHeader ncFileVO = filevos[i];
//				LinkedHashMap<String, String> fileMap = new LinkedHashMap<String, String>();
//				fileMap.put("name", ncFileVO.getFileName()); // 附件文件名称
//				fileMap.put("size", ""+ncFileVO.getFileSize()); // 附件文件大小
//				fileMap.put("type", ncFileVO.getFileType()); // 文件类型
//				fileMap.put("id", ncFileVO.toString()); // 文件标识
////				fileMap.put("downloadpath", ncFileVO.toString()); // 文件标识
//				attatchmapList.add(fileMap);
//			}
//		}

		return resultmap;
	}
	
}
