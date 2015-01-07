package nc.arap.mobile.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.arap.mobile.itf.IArapMobileCtrl;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.reimtype.ReimTypeVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
public class ArapMobileCtrlImpl extends ErmMobileCtrlImpl implements IArapMobileCtrl{
	 
	@Override
	public Map<String, Object> getJkbxCard(String headpk) throws BusinessException { 
		String[] queryFields = new String[] { JKBXHeaderVO.PK_JKBX,
				JKBXHeaderVO.TOTAL,
				JKBXHeaderVO.DJRQ, JKBXHeaderVO.DJBH, JKBXHeaderVO.ZY, 
				JKBXHeaderVO.DJLXBM,JKBXHeaderVO.OPERATOR,JKBXHeaderVO.SPZT,JKBXHeaderVO.SZXMID};
		String[] itemQueryFields = new String[] { BXBusItemVO.PK_BUSITEM,BXBusItemVO.AMOUNT,
				BXBusItemVO.DEFITEM4,BXBusItemVO.DEFITEM5,BXBusItemVO.DEFITEM2,BXBusItemVO.DEFITEM12,BXBusItemVO.PK_REIMTYPE};
		BaseDAO dao = new BaseDAO();
		Map<String, Object> resultmap = new HashMap<String, Object>();
		if(StringUtil.isEmpty(headpk)){
			return resultmap;
		}
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
				String ioname = fyxmtyMap.get(value) == null?"":fyxmtyMap.get(value).get("name");
				resultmap.put("ioname", ioname);
			}
		}
		resultmap.remove(JKBXHeaderVO.OPERATOR);
		Map<String, Map<String,String>> reimtypemap = queryReimType(bxheadvo.getOperator());
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
					if(BXBusItemVO.PK_REIMTYPE.equals(queryField)){
						// 转换报销类型为name
						String attrname = reimtypemap.get(attrvalue) == null?"":reimtypemap.get(attrvalue).get(ReimTypeVO.NAME);
						itemResultmap.put("reimname", attrname);
					}
					itemResultmap.put(queryField, attrvalue);
				}
				itemResultmapList.add(itemResultmap);
			}
		}
		
		// 获取附件列表
//		ErmMobileCtrlBO bo = new ErmMobileCtrlBO(defaultDjlxbm);
//		List<Map<String, String>> attatchmapList =bo.getFileList(headpk, bxheadvo.getOperator());
//		resultmap.put("attachment", attatchmapList);
		
		return resultmap;
	}
	
	@Override
	public Map<String, Map<String, String>> queryReimType(String userid)
			throws BusinessException {
		return new ErmMobileCtrlBO().queryReimType(userid);
	}

}
