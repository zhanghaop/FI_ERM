package nc.arap.mobile.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.arap.mobile.itf.IClfMobileCtrl;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
public class ClfMobileCtrlImpl extends ErmMobileCtrlImpl implements IClfMobileCtrl{
	
	@Override
	public Map<String, Object> getJkbxCard(String headpk) throws BusinessException { 
		String[] queryFields = new String[] { JKBXHeaderVO.PK_JKBX,
				JKBXHeaderVO.TOTAL,
				JKBXHeaderVO.DJRQ, JKBXHeaderVO.DJBH, JKBXHeaderVO.ZY, 
				JKBXHeaderVO.DJLXBM,JKBXHeaderVO.OPERATOR,JKBXHeaderVO.SPZT,JKBXHeaderVO.SZXMID};
		String[] itemQueryFields = new String[] { BXBusItemVO.PK_BUSITEM,BXBusItemVO.AMOUNT,BXBusItemVO.SZXMID,
				BXBusItemVO.DEFITEM1,BXBusItemVO.DEFITEM2,BXBusItemVO.DEFITEM3,BXBusItemVO.DEFITEM4,BXBusItemVO.DEFITEM5,BXBusItemVO.TABLECODE};
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
        
		IBXBillPrivate service = NCLocator.getInstance().lookup(IBXBillPrivate.class);
		BXBusItemVO[] items = service.queryItems(bxheadvo);
		List<Map<String, Object>> itemResultmapList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> itemOtherResultmapList = new ArrayList<Map<String,Object>>();
		resultmap.put("items", itemResultmapList);
//		resultmap.put("itemsother", itemOtherResultmapList);
		if(items != null && items.length > 0){
			for (int i = 0; i < items.length; i++) {
				Map<String, Object> itemResultmap = new HashMap<String, Object>();
				BXBusItemVO item = items[i];
				String tabcode = (String) item.getAttributeValue(BXBusItemVO.TABLECODE);
					for (int j = 0; j < itemQueryFields.length;j++) {
						String queryField = itemQueryFields[j];
						String attrvalue = ErmMobileCtrlBO.getStringValue(item.getAttributeValue(queryField));
						itemResultmap.put(queryField, attrvalue);
						if(BXBusItemVO.SZXMID.equals(queryField) && "other".equals(tabcode)){
							// 转换收支项目为name
							String attrname = fyxmtyMap.get(attrvalue) == null?"":fyxmtyMap.get(attrvalue).get("name");
							itemResultmap.put("ioname", attrname);
						}
						if((BXBusItemVO.DEFITEM1.equals(queryField) || (BXBusItemVO.DEFITEM2.equals(queryField) && "arap_bxbusitem".equals(tabcode))) && StringUtils.isNotEmpty(attrvalue)){
							itemResultmap.put(queryField, new UFDate(attrvalue).toLocalString());
						}
					}    
					
					
				if("arap_bxbusitem".equals(tabcode)){
						itemResultmap.put("itemflag", "0");
				}else{
//					for (int j = 0; j < itemOhterQueryFields.length;j++) {
//						String queryField = itemOhterQueryFields[j];
//						String attrvalue = ErmMobileCtrlBO.getStringValue(item.getAttributeValue(queryField));
//						itemResultmap.put(queryField, attrvalue);
//					}
//					itemOtherResultmapList.add(itemResultmap);
					itemResultmap.put("itemflag", "1");
				}
				itemResultmapList.add(itemResultmap);
			}
		}
		// 获取附件列表
		ErmMobileCtrlBO bo = new ErmMobileCtrlBO(defaultDjlxbm);
		List<Map<String, String>> attatchmapList =bo.getFileList(headpk, bxheadvo.getOperator());
		resultmap.put("attachment", attatchmapList);
		
		return resultmap;
	}

	
}
