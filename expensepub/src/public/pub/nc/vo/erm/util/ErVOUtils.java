package nc.vo.erm.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.StringUtils;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;
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
			return new JKBXHeaderVO[]{parentVO};
		} else {
			JKBXHeaderVO[] vos=new JKBXHeaderVO[childrenVO.length];
			for (int i = 0; i < vos.length; i++) {
				vos[i]=(JKBXHeaderVO) parentVO.clone();
				BXBusItemVO item = childrenVO[i];
				String[] attributeNames = item.getAttributeNames();
				for (String attr : attributeNames) {
					if(attr.equals("jobid") || attr.equals("cashproj") || attr.equals("jkbxr") || attr.equals("cashitem")){
						continue;
					}
					vos[i].setAttributeValue(attr, item.getAttributeValue(attr));
				}
				//事项审批 关联表 arap_item_clb
				vos[i].setPk_jkbx(parentVO.getPk_jkbx());
			}
			return vos;
		}
	}

	
	/**
	 * @param contrasts
	 * @param jkds
	 * @return  处理预算回冲借款的情况
	 */
	public static JKBXHeaderVO[] prepareBxvoItemToHeaderForJkContrast(Collection<BxcontrastVO> contrasts, List<JKBXHeaderVO> jkds) {
		List<JKBXHeaderVO> headers=new ArrayList<JKBXHeaderVO>();
		Map<String, SuperVO> jkdMap = VOUtils.changeCollectionToMap(jkds,new String[]{JKBXHeaderVO.PK_JKBX});
		for(BxcontrastVO vo:contrasts){
			JKBXHeaderVO head = (JKBXHeaderVO) (jkdMap.get(vo.getPk_jkd()).clone());
			
			if(head.getQcbz()!=null && head.getQcbz().booleanValue()){ //期初借款单不占用执行数，不需要进行回冲
				continue;
			}
//begin--modifiedd by chendya v6.0冲借款修改了控制规则为减少，不再传反向金额，预算会自动控制			
//			head.setYbje(vo.getYbje().multiply(-1));
//			head.setBbje(vo.getBbje().multiply(-1));
//--end			
			head.setYbje(vo.getYbje());
			head.setBbje(vo.getBbje());
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
