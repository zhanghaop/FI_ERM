package nc.vo.erm.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.StringUtils;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

/**
 * @author twei
 * 
 * nc.vo.erm.util.ErVOUtils
 */
public class ErVOUtils {
	
	public static String getPsninfoByUser(String userid,String pk_org){
		try{
			String[] queryPsnidAndDeptid = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPsnidAndDeptid(userid, pk_org);
			if(!StringUtils.isNullWithTrim(queryPsnidAndDeptid[0]))
				return queryPsnidAndDeptid[0];
			else
				return null;
		}catch (Exception e) {
			ExceptionHandler.consume(e);
			return null;
		}
	}
	
	
	public static void clearContrastInfo(JKBXVO vo) {
		if (vo == null || vo.getParentVO() == null)
			return;
		
		vo.setContrastVO(null);
		
		BXBusItemVO[] childrenVO = vo.getChildrenVO();
		JKBXHeaderVO parentVO = vo.getParentVO();
		
		parentVO.setCjkybje(UFDouble.ZERO_DBL);
		parentVO.setCjkbbje(UFDouble.ZERO_DBL);
		
		parentVO.setHkybje(UFDouble.ZERO_DBL);
		parentVO.setHkbbje(UFDouble.ZERO_DBL);
		
		parentVO.setZfybje(parentVO.getYbje());
		parentVO.setZfbbje(parentVO.getBbje());
		
		parentVO.setGlobalcjkbbje(UFDouble.ZERO_DBL);
		parentVO.setGroupcjkbbje(UFDouble.ZERO_DBL);
		parentVO.setGlobalhkbbje(UFDouble.ZERO_DBL);
		parentVO.setGrouphkbbje(UFDouble.ZERO_DBL);
		parentVO.setGlobalzfbbje(parentVO.getGlobalbbje());
		parentVO.setGroupzfbbje(parentVO.getGroupbbje());
		
		
		if (childrenVO == null || childrenVO.length == 0) {
			
		} else {
			for(BXBusItemVO item:childrenVO){
				item.setCjkybje(UFDouble.ZERO_DBL);
				item.setCjkbbje(UFDouble.ZERO_DBL);
				
				item.setHkybje(UFDouble.ZERO_DBL);
				item.setHkbbje(UFDouble.ZERO_DBL);
				
				item.setZfybje(item.getYbje());
				item.setZfbbje(item.getBbje());
				
				item.setGlobalcjkbbje(UFDouble.ZERO_DBL);
				item.setGroupcjkbbje(UFDouble.ZERO_DBL);
				item.setGlobalhkbbje(UFDouble.ZERO_DBL);
				item.setGrouphkbbje(UFDouble.ZERO_DBL);
				item.setGlobalzfbbje(item.getGlobalbbje());
				item.setGroupzfbbje(item.getGroupbbje());
				
			}
		}
	}
	
	public static void prepareBxvoHeaderToItem(JKBXVO vo) {
		if (vo == null || vo.getParentVO() == null)
			return;
		BXBusItemVO[] childrenVO = vo.getChildrenVO();
		JKBXHeaderVO parentVO = vo.getParentVO();
		if (childrenVO == null || childrenVO.length == 0) {
			BXBusItemVO item = new BXBusItemVO();
			String[] attributeNames = item.getAttributeNames();
			for (String attr : attributeNames) {
				item.setAttributeValue(attr, parentVO.getAttributeValue(attr));
			}
			item.setPrimaryKey(parentVO.getPrimaryKey()==null?BXConstans.TEMP_ZB_PK:parentVO.getPrimaryKey());
			
			//业务表体为空的处理,设置特殊的dr标识，考虑帐表，预算，会计平台，结算接口的处理。
			item.setDr(BXConstans.SPECIAL_DR);
			item.setTablecode(BXConstans.JK_DJDL.equals(parentVO.getDjdl())?BXConstans.BUS_PAGE_JK:BXConstans.BUS_PAGE);
			vo.setChildrenVO(new BXBusItemVO[] { item });
		} 
	}
	
	public static JKBXVO prepareBxvoHeaderToItemClone(JKBXVO vo) {
		if (vo == null || vo.getParentVO() == null)
			return vo;
		BXBusItemVO[] childrenVO = vo.getChildrenVO();
		JKBXHeaderVO parentVO = vo.getParentVO();
		if (childrenVO == null || childrenVO.length == 0) {
			JKBXVO voclone=(JKBXVO) vo.clone();
			BXBusItemVO item = new BXBusItemVO();
			String[] attributeNames = item.getAttributeNames();
			for (String attr : attributeNames) {
				item.setAttributeValue(attr, parentVO.getAttributeValue(attr));
			}
			item.setPrimaryKey(parentVO.getPrimaryKey());
			voclone.setChildrenVO(new BXBusItemVO[] { item });
			return voclone;
		} else {
		}
		return vo;
	}
	
	public static JKBXHeaderVO[] prepareBxvoItemToHeaderClone(JKBXVO vo) {
		if (vo == null || vo.getParentVO() == null)
			return new JKBXHeaderVO[]{};
		
		BXBusItemVO[] childrenVO = vo.getChildrenVO();
		JKBXHeaderVO parentVO = vo.getParentVO();
		if (childrenVO == null || childrenVO.length == 0) {
			return new JKBXHeaderVO[]{(JKBXHeaderVO)parentVO.clone()};
		} else {
			JKBXHeaderVO[] vos=new JKBXHeaderVO[childrenVO.length];
			for (int i = 0; i < vos.length; i++) {
				vos[i]=(JKBXHeaderVO) parentVO.clone();
				BXBusItemVO item = childrenVO[i];
				String[] attributeNames = item.getAttributeNames();
				for (String attr : attributeNames) {
					if(attr.equals("cashproj") || attr.equals("jkbxr") || attr.equals("cashitem")){
						continue;
					}
					vos[i].setAttributeValue(attr, item.getAttributeValue(attr));
				}
				//事项审批 关联表 arap_item_clb
				vos[i].setPk_jkbx(parentVO.getPk_jkbx());
				
				//主表记录子表业务行pk，回写费用申请单使用
				vos[i].setPk_busitem(item.getPk_busitem());
				vos[i].setBx_busitemPK(item.getBx_busitemPK());
				vos[i].setJk_busitemPK(item.getJk_busitemPK());
				vos[i].setPreItemJe(vos[i].getItemJe());
				// JKheadvo情况下cjkybje设置不上，需要额外重新设置
				vos[i].setCjkybje(item.getCjkybje());
				vos[i].setCjkbbje(item.getCjkbbje());
				vos[i].setGroupcjkbbje(item.getGroupcjkbbje());
				vos[i].setGlobalcjkbbje(item.getGlobalcjkbbje());
				//因为是clone，所以这里不再设置回去
				if(vos[i].getShrq() == null){//生效可以去生效日期
					vos[i].setShrq(parentVO.getJsrq() == null ? null : new UFDateTime(parentVO.getJsrq().getMillis()));
				}
			}
			return vos;
		}
	}

	/**
	 * @param contrasts
	 * @param jkdCollection
	 * @return 处理预算回冲借款的情况
	 */
	public static JKBXHeaderVO[] prepareItemToHeaderForJkContrast(Collection<BxcontrastVO> contrasts,
			Collection<JKBXHeaderVO> jkdCollection, Collection<BXBusItemVO> jkBusItemCollection) {
		List<JKBXHeaderVO> headers = new ArrayList<JKBXHeaderVO>();
		Map<String, SuperVO> jkdMap = VOUtils.changeCollectionToMap(jkdCollection, new String[] { JKBXHeaderVO.PK_JKBX });
		Map<String, SuperVO> busItemMap = VOUtils.changeCollectionToMap(jkBusItemCollection,
				new String[] { BXBusItemVO.PK_BUSITEM });
		
		for(BxcontrastVO vo:contrasts){
			JKBXHeaderVO head = (JKBXHeaderVO) jkdMap.get(vo.getPk_jkd()).clone();
			
			if(head.getQcbz()!=null && head.getQcbz().booleanValue()){ //期初借款单不占用执行数，不需要进行回冲
				continue;
			}
			BXBusItemVO busItem = (BXBusItemVO)busItemMap.get(vo.getPk_busitem()).clone();
			
			String[] attributeNames = busItem.getAttributeNames();
			for (String attr : attributeNames) {
				if(attr.equals("cashproj") || attr.equals("jkbxr") || attr.equals("cashitem")){
					continue;
				}
				head.setAttributeValue(attr, busItem.getAttributeValue(attr));
			}
			
			head.setYbje(vo.getCjkybje());
			head.setBbje(vo.getCjkbbje());
			head.setGroupbbje(vo.getGroupcjkbbje());
			head.setGlobalbbje(vo.getGlobalcjkbbje());
			
			// 设置预算金额
			head.setPreItemJe(new UFDouble[] { vo.getGlobalfybbje(), vo.getGroupfybbje(), vo.getFybbje(),
					vo.getFyybje()});
			headers.add(head);
		}
		
		return headers.toArray(new JKBXHeaderVO[]{});
	}
	
	public static boolean isSimpleEquals(Object obj,Object obj1){
		if(obj==null && obj1==null)
			return true;
		else if(obj1==null || obj==null)
			return false;
		else
			return obj1.equals(obj);
		
	}

}
