package nc.bs.arap.bx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.cmp.settlement.ICmpSettlementPubQueryService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.cmp.dap.MsgAggregatedStruct;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;

public class FipUtil {
	
	public AggregatedValueObject addOtherInfo(JKBXVO bxvo) throws BusinessException {
		if(bxvo==null)
			return null;
		List<JKBXVO> bxvolist=new ArrayList<JKBXVO>();
		bxvolist.add(bxvo);
		addSettleMentInfo(bxvolist);
		return bxvo;
	}
	

	private Map<String, SuperVO> getJkdMap(List<JKBXVO> bxvolist, boolean withContrastInfo) throws BusinessException {
		if(withContrastInfo){
			Map<String, SuperVO> jkdMapnew=new HashMap<String, SuperVO>();
			for(JKBXVO bxvo:bxvolist){
				Map<String, JKBXHeaderVO> jkdMap = bxvo.getJkdMap();
				if(jkdMap!=null && jkdMap.size()!=0){
					jkdMapnew.putAll(jkdMap);
				}
			}
			return jkdMapnew;
		}
		
		
		List<String> jkdKeys=new ArrayList<String>();
		for(JKBXVO bxvo:bxvolist){
			BxcontrastVO[] contrastVO = bxvo.getContrastVO();
			if(contrastVO!=null && contrastVO.length!=0){
				for(BxcontrastVO vo:contrastVO){
					jkdKeys.add(vo.getPk_jkd());
				}
			}
		}
		List<JKBXHeaderVO> jkds = new BXZbBO().queryHeadersByPrimaryKeys(jkdKeys.toArray(new String[]{}), BXConstans.JK_DJDL);
		Map<String, SuperVO> jkdMap = VOUtils.changeCollectionToMap(jkds,JKBXHeaderVO.PK_JKBX);
		return jkdMap;
	}
	
	public void addSettleMentInfo(List<JKBXVO> bxvos) throws BusinessException {
		for(JKBXVO bxvo:bxvos){
			Map<String, BXBusItemVO> map = new HashMap<String, BXBusItemVO>();
			for(BXBusItemVO finitem:bxvo.getChildrenVO()){
				map.put(finitem.getPrimaryKey(), finitem);
			}
			List<BXBusItemVO> djzbitemsList = new ArrayList<BXBusItemVO>();
			List<String> pklist = new ArrayList<String>();
			BXBusItemVO tempdjzbitem = null;
			List<SettlementBodyVO> list = null;
			Map<String, List<SettlementBodyVO>> detailMap = bxvo.getSettlementMap();
//			Map<String, List<SettlementBodyVO>> detailMap = null;
			JKBXHeaderVO head = bxvo.getParentVO();
			if(head.getPk_jkbx()!=null){

				//判断cmp是否启用
				boolean iscmpused = BXUtil.isProductInstalled(head.getPk_group(),BXConstans.TM_CMP_FUNCODE);
				if(iscmpused){
					SettlementAggVO[] aggVO = NCLocator.getInstance().lookup(ICmpSettlementPubQueryService.class)
					.queryBillsBySourceBillID(new String[] { head.getPk_jkbx() }); 
//added by chendya@ufida.com.cn 加非空校验
					if(aggVO==null||aggVO.length==0){
						return;
					}
//--end	
					detailMap = new HashMap<String, List<SettlementBodyVO>>();
					for(SettlementAggVO settleVO:aggVO){
						CircularlyAccessibleValueObject[] childrenVO = settleVO.getChildrenVO();
						for(CircularlyAccessibleValueObject vo:childrenVO){
							//取得单据对应的pk
							String pkBilldetail = ((SettlementBodyVO)vo).getPk_billdetail();
							//同一张单据对应不同的结算信息
							List<SettlementBodyVO> lists=null;
							if(detailMap.containsKey(pkBilldetail)){
								lists=detailMap.get(pkBilldetail);
								lists.add((SettlementBodyVO) vo);
							}else{
								lists=new ArrayList<SettlementBodyVO>();
								lists.add((SettlementBodyVO) vo);
								
							}
							detailMap.put(pkBilldetail,lists);
						}
					}		
						
					bxvo.setSettlementMap(detailMap);
				}

			}
			if(detailMap==null)
				continue;
			
			Set<String> pksSet = detailMap.keySet();
			for (Iterator iter = pksSet.iterator(); iter.hasNext();) {
				String pk = (String) iter.next();
				list = detailMap.get(pk);
				for (SettlementBodyVO settlementBodyVO : list) {
					
					
					
					if (map.get(pk)==null) { //没有对应的结算信息
						
					}else{
						tempdjzbitem = (BXBusItemVO) map.get(pk).clone();
						tempdjzbitem = (BXBusItemVO) map.get(pk).clone();
						tempdjzbitem.setHkybje(settlementBodyVO.getReceive());
						tempdjzbitem.setHkbbje(settlementBodyVO.getReceivelocal());
						tempdjzbitem.setZfybje(settlementBodyVO.getPay());
						tempdjzbitem.setZfbbje(settlementBodyVO.getPaylocal());
						tempdjzbitem.setSettleBodyVO(settlementBodyVO);
					}
					
					if(pklist.contains(pk)){ //第二次取出这个pk，表示在结算信息中进行了拆行.
						tempdjzbitem.setYbje(new UFDouble(0));
						tempdjzbitem.setBbje(new UFDouble(0));
					}
					
					djzbitemsList.add(tempdjzbitem);
					pklist.add(pk);
				}
			}
			
			for(BXBusItemVO finitem:bxvo.getChildrenVO()){
				if(!pklist.contains(finitem.getPrimaryKey())){ //没有对应的结算信息
					SettlementBodyVO settlementBodyVO2 = new SettlementBodyVO();
					settlementBodyVO2.setReceive(finitem.getHkybje());
					settlementBodyVO2.setReceivelocal(finitem.getHkbbje());
					settlementBodyVO2.setPay(finitem.getZfybje());
					settlementBodyVO2.setPaylocal(finitem.getZfbbje());
					settlementBodyVO2.setPk_account(bxvo.getParentVO().getFkyhzh());
					settlementBodyVO2.setPk_balatype(bxvo.getParentVO().getJsfs());
					finitem.setSettleBodyVO(settlementBodyVO2);
					djzbitemsList.add(finitem);
				}
			}
			bxvo.setChildrenVO(djzbitemsList.toArray(new BXBusItemVO[]{}));
		}
	}
}