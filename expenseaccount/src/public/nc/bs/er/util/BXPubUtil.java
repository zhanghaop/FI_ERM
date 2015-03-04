package nc.bs.er.util;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.fi.pub.Currency;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;


public class BXPubUtil {
	
	/**
	 * 自动冲借款
	 * 
	 * @param bxVo 报销单VO
	 * @param isCancel 是否取消
	 * @throws BusinessException
	 */
	public static void autoContrast(JKBXVO bxVo , boolean isCancel) throws BusinessException {
		if (bxVo == null || bxVo instanceof JKVO) {
			return;
		}
		
		if(!isCancel){
			StringBuffer sqlBuf = new StringBuffer();
			
			//查出符合条件的借款单，单据日期按从前到后
			sqlBuf.append(" where zb.yjye>0 and zb.dr=0 and zb.djzt=3 ");
			sqlBuf.append(" and zb.bzbm='" + bxVo.getParentVO().getBzbm() + "'");
			sqlBuf.append(" and zb.jkbxr='" + bxVo.getParentVO().getJkbxr() + "'");
			sqlBuf.append(" and zb.djrq<='" + bxVo.getParentVO().getDjrq() + "'");
			sqlBuf.append(" order by zb.djrq ");
			
			List<JKBXVO> result = ((IBXBillPrivate) NCLocator.getInstance().lookup(IBXBillPrivate.class.getName()))
					.queryVOsByWhereSql(sqlBuf.toString(), BXConstans.JK_DJDL);
			
			if(result != null && result.size() > 0){
				fillContrastVo(bxVo, result);//补充冲销信息
			}
		}else{
			bxVo.setContrastVO(null);
		}
		
		fillBxVoContrastJe(bxVo);//补充报销单中冲销信息金额
	}
	
	/**
	 * 补充报销单中的冲销金额（冲销金额、还款金额、支付金额）
	 * @param bxVo
	 * @throws BusinessException
	 */
	private static void fillBxVoContrastJe(JKBXVO bxVo) throws BusinessException {
		if (bxVo == null) {
			return;
		}
		
		JKBXHeaderVO head = bxVo.getParentVO();
		if(bxVo.getContrastVO() == null || bxVo.getContrastVO().length == 0){
			// 取消借款单的冲销
			bxVo.getParentVO().setCjkybje(UFDouble.ZERO_DBL);//冲借款金额
			bxVo.getParentVO().setCjkbbje(UFDouble.ZERO_DBL);
			bxVo.getParentVO().setGroupcjkbbje(UFDouble.ZERO_DBL);
			bxVo.getParentVO().setGlobalcjkbbje(UFDouble.ZERO_DBL);
			bxVo.getParentVO().setHkybje(UFDouble.ZERO_DBL);//还款金额
			bxVo.getParentVO().setHkbbje(UFDouble.ZERO_DBL);
			bxVo.getParentVO().setGrouphkbbje(UFDouble.ZERO_DBL);
			bxVo.getParentVO().setGlobalhkbbje(UFDouble.ZERO_DBL);
			bxVo.getParentVO().setZfybje(bxVo.getParentVO().getYbje());//支付金额
			bxVo.getParentVO().setZfbbje(bxVo.getParentVO().getBbje());
			bxVo.getParentVO().setGroupzfbbje(bxVo.getParentVO().getGroupbbje());
			bxVo.getParentVO().setGlobalzfbbje(bxVo.getParentVO().getGlobalbbje());
			
			if(bxVo.getChildrenVO() != null){
				for(BXBusItemVO itemVo : bxVo.getChildrenVO()){
					itemVo.setCjkybje(UFDouble.ZERO_DBL);//冲借款金额
					itemVo.setCjkbbje(UFDouble.ZERO_DBL);
					itemVo.setGroupcjkbbje(UFDouble.ZERO_DBL);
					itemVo.setGlobalcjkbbje(UFDouble.ZERO_DBL);
					itemVo.setHkybje(UFDouble.ZERO_DBL);//还款金额
					itemVo.setHkbbje(UFDouble.ZERO_DBL);
					itemVo.setGrouphkbbje(UFDouble.ZERO_DBL);
					itemVo.setGlobalhkbbje(UFDouble.ZERO_DBL);
					itemVo.setZfybje(itemVo.getYbje());//支付金额
					itemVo.setZfbbje(itemVo.getBbje());
					itemVo.setGroupzfbbje(itemVo.getGroupbbje());
					itemVo.setGlobalzfbbje(itemVo.getGlobalbbje());
				}
			}
		}else{
			UFDouble cjkybje = UFDouble.ZERO_DBL;
			for (BxcontrastVO contrast : bxVo.getContrastVO()) {
				cjkybje = cjkybje.add(contrast.getCjkybje());
			}
			
			//冲借款金额
			setHeadJe(head, cjkybje, new String[] { JKBXHeaderVO.CJKYBJE, JKBXHeaderVO.CJKBBJE,
				JKBXHeaderVO.GROUPCJKBBJE, JKBXHeaderVO.GLOBALCJKBBJE });
			
			//支付金额
			setHeadJe(head, head.getYbje().sub(cjkybje), new String[] { JKBXHeaderVO.ZFYBJE, JKBXHeaderVO.ZFBBJE,
				JKBXHeaderVO.GROUPZFBBJE, JKBXHeaderVO.GLOBALZFBBJE });
			
			//表体金额设置
			caculateBodyCjkje(bxVo);
		}
	}

	private static void fillContrastVo(JKBXVO bxVo, List<JKBXVO> result) {
		if(bxVo == null || result == null || result.size() == 0){
			return;
		}
		
		List<BxcontrastVO> list = new ArrayList<BxcontrastVO>();

		//封装冲销信息
		UFDouble ybje = bxVo.getParentVO().getYbje();
		
		for(JKBXVO tempJkVo : result){
			JKBXHeaderVO jkParentVO = tempJkVo.getParentVO();
			BXBusItemVO[] jkBusitemVos = tempJkVo.getChildrenVO();
			
			for(BXBusItemVO busitemVo : jkBusitemVos){
				if(ybje.compareTo(UFDouble.ZERO_DBL) <= 0){
					break;
				}
				
				if(busitemVo.getYjye().compareTo(UFDouble.ZERO_DBL) > 0){
					BxcontrastVO bxcontrastVO = new BxcontrastVO();
					
					bxcontrastVO.setCxrq(bxVo.getParentVO().getDjrq());
					bxcontrastVO.setDeptid(jkParentVO.getDeptid());
					bxcontrastVO.setDjlxbm(jkParentVO.getDjlxbm());
					bxcontrastVO.setJkbxr(jkParentVO.getJkbxr());
					bxcontrastVO.setJobid(busitemVo.getJobid());
					bxcontrastVO.setPk_payorg(jkParentVO.getPk_payorg());
					bxcontrastVO.setPk_bxd(bxVo.getParentVO().getPk_jkbx());
					bxcontrastVO.setPk_org(jkParentVO.getPk_org());
					bxcontrastVO.setPk_jkd(jkParentVO.getPk_jkbx());
					bxcontrastVO.setJkdjbh(jkParentVO.getDjbh());
					bxcontrastVO.setPk_busitem(busitemVo.getPk_busitem());
					bxcontrastVO.setSzxmid(busitemVo.getSzxmid());
					bxcontrastVO.setSxbz(BXStatusConst.SXBZ_NO);
					bxcontrastVO.setSxrq(null);
					bxcontrastVO.setHkybje(UFDouble.ZERO_DBL);//报销金额大于等于冲销金额，不会出现还款金额
					bxcontrastVO.setBxdjbh(bxVo.getParentVO().getDjbh());
					
					if(ybje.compareTo(busitemVo.getYjye()) >= 0){
						bxcontrastVO.setFyybje(busitemVo.getYjye());
						bxcontrastVO.setCjkybje(busitemVo.getYjye());
						bxcontrastVO.setYbje(busitemVo.getYjye());
						ybje = ybje.sub(busitemVo.getYjye());
					}else{
						bxcontrastVO.setFyybje(busitemVo.getYjye().sub(ybje));
						bxcontrastVO.setCjkybje(busitemVo.getYjye().sub(ybje));
						bxcontrastVO.setYbje(busitemVo.getYjye().sub(ybje));
						ybje = UFDouble.ZERO_DBL;
					}
					
					list.add(bxcontrastVO);
				}
			}
			
			if(ybje.compareTo(UFDouble.ZERO_DBL) <= 0){
				break;
			}
		}
		bxVo.setContrastVO(list.toArray(new BxcontrastVO[]{}));
	}
	
	/**
	 * 折算表头金额字段
	 * 
	 * @param head
	 * @param ybje
	 * @param yfbKeys
	 * @throws BusinessException
	 */
	public static void setHeadJe(JKBXHeaderVO head, UFDouble ybje, String[] yfbKeys) throws BusinessException {
		try {
			head.setAttributeValue(yfbKeys[0], ybje);
			UFDouble[] yfbs = Currency.computeYFB(head.getPk_org(), Currency.Change_YBJE, head.getBzbm(), ybje, null,
					null, null, head.getBbhl(), head.getDjrq());
			UFDouble[] money = Currency.computeGroupGlobalAmount(ybje, yfbs[2], head.getBzbm(), head.getDjrq(),
					head.getPk_org(), head.getPk_group(), head.getGlobalbbhl(), head.getGroupbbhl());

			head.setAttributeValue(yfbKeys[1], yfbs[2]);
			head.setAttributeValue(yfbKeys[2], money[0]);
			head.setAttributeValue(yfbKeys[3], money[1]);

		} catch (BusinessException e) {
			// 设置本币错误.
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000009")/*
																														 * @
																														 * res
																														 * "设置本币错误!"
																														 */);
		}
	}
	
	/**
	 * 折算表体冲借款金额(同时计算支付或还款金额)
	 * 
	 * @param bxvo
	 */
	public static void caculateBodyCjkje(JKBXVO bxvo) {
		JKBXHeaderVO head = bxvo.getParentVO();
		BXBusItemVO[] childrenVO = bxvo.getChildrenVO();
		if (childrenVO == null || childrenVO.length == 0)
			return;

		List<BXBusItemVO> noNullChildrenVoList = new ArrayList<BXBusItemVO>();

		for (int i = 0; i < childrenVO.length; i++) {
			if (!isJeNullRow(childrenVO[i])) {
				noNullChildrenVoList.add(childrenVO[i]);
			}
		}

		UFDouble cjkje = head.getCjkybje();//总冲借款金额，进行分配
		for (int i = 0; i < noNullChildrenVoList.size(); i++) {
			BXBusItemVO child = noNullChildrenVoList.get(i);

			if (cjkje != null) {
				// 还有没分配完的冲借款金额
				UFDouble ybje = child.getYbje();
				// 当前行是最后一行
				if (i == noNullChildrenVoList.size() - 1) {
					child.setAttributeValue(BXBusItemVO.CJKYBJE, cjkje);
				}else if (cjkje.compareTo(ybje) > 0) {
					// 如果剩余的冲借款金额大于原币金额，则该项的冲借款金额值与原币金额相同
					child.setAttributeValue(BXBusItemVO.CJKYBJE, ybje);
					cjkje = cjkje.sub(ybje);
				} else {
					// 如果剩余的冲借款金额不大于原币金额，则该项的冲借款金额值设为剩余的冲借款金额
					child.setAttributeValue(BXBusItemVO.CJKYBJE, cjkje);
					cjkje = null;
				}
			} else {
				// 冲借款金额都分配完了，剩余项都用0填补
				child.setAttributeValue(BXBusItemVO.CJKYBJE, UFDouble.ZERO_DBL);
			}
			caculateZfHkJe(child);
			transYbjeToBbje(head, child);
		}
	}
	
	private static boolean isJeNullRow(BXBusItemVO vo) {
		// 判断是否是金额为空的行
		UFDouble ybje = vo.getYbje() == null ? UFDouble.ZERO_DBL : vo.getYbje();
		UFDouble cjkJe = vo.getCjkybje() == null ? UFDouble.ZERO_DBL : vo.getCjkybje();

		if (UFDouble.ZERO_DBL.equals(ybje) && UFDouble.ZERO_DBL.equals(cjkJe)) {
			// 原币金额为空则表示为空行
			return true;
		}

		return false;
	}
	
	/**
	 * 根据冲借款金额的变化，修改表体中数据的其他金额数值
	 * 
	 * @author 
	 */
	private static void caculateZfHkJe(BXBusItemVO vo) {
		UFDouble ybje = vo.getYbje();
		UFDouble cjkybje = vo.getCjkybje();
		if (ybje.getDouble() > cjkybje.getDouble()) {// 如果原币金额大于冲借款金额
			vo.setAttributeValue(BXBusItemVO.ZFYBJE, ybje.sub(cjkybje));// 支付金额=原币金额-冲借款金额
			vo.setAttributeValue(BXBusItemVO.HKYBJE, UFDouble.ZERO_DBL);
		} else {
			vo.setAttributeValue(BXBusItemVO.HKYBJE, cjkybje.sub(ybje));// 还款金额=冲借款金额-原币金额
			vo.setAttributeValue(BXBusItemVO.ZFYBJE, UFDouble.ZERO_DBL);
		}
	}
	
	private static void transYbjeToBbje(JKBXHeaderVO head, BXBusItemVO itemVO) {
		String pk_corp = head.getPk_org();
		String bzbm = head.getBzbm();
		UFDouble hl = head.getBbhl();
		UFDouble ybje = itemVO.getYbje();
		UFDouble cjkybje = itemVO.getCjkybje();
		UFDouble hkybje = itemVO.getHkybje();
		UFDouble zfybje = itemVO.getZfybje();
		try {
			UFDouble[] bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm, ybje, null, null, null, hl, head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.BBJE, bbje[2]);
			itemVO.setAttributeValue(JKBXHeaderVO.BBYE, bbje[2]);
			bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm, cjkybje, null, null, null, hl, head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.CJKBBJE, bbje[2]);
			bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm, hkybje, null, null, null, hl, head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.HKBBJE, bbje[2]);
			bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm, zfybje, null, null, null, hl, head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.ZFBBJE, bbje[2]);

			// 折算冲借款集团、全局本币金额
			caculateGroupAndGlobalBbje(head, itemVO, BXBusItemVO.CJKYBJE, BXBusItemVO.CJKYBJE, BXBusItemVO.GROUPCJKBBJE, BXBusItemVO.GLOBALCJKBBJE);

			// 折算支付集团、全局本币金额
			caculateGroupAndGlobalBbje(head, itemVO, BXBusItemVO.ZFYBJE, BXBusItemVO.ZFYBJE, BXBusItemVO.GROUPZFBBJE, BXBusItemVO.GLOBALZFBBJE);

			// 折算还款集团、全局本币金额
			caculateGroupAndGlobalBbje(head, itemVO, BXBusItemVO.HKYBJE, BXBusItemVO.HKYBJE, BXBusItemVO.GROUPHKBBJE, BXBusItemVO.GLOBALHKBBJE);

		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 折算集团和全局本币金额
	 * 
	 * @param ybje
	 * @param bbje
	 * @param pk_currtype
	 * @param date
	 * @param pk_org
	 * @param pk_group
	 * @param globalrate
	 * @param grouprate
	 * @throws BusinessException
	 */
	private static void caculateGroupAndGlobalBbje(JKBXHeaderVO head, BXBusItemVO itemVO, String ybjeField, String bbjeField, String groupbbjeField, String globalbbjeField) throws BusinessException {
		UFDouble[] moneys = Currency.computeGroupGlobalAmount((UFDouble) itemVO.getAttributeValue(ybjeField), (UFDouble) itemVO.getAttributeValue(bbjeField), head.getBzbm(), head.getDjrq(),
				head.getPk_org(), head.getPk_group(), head.getGlobalbbhl(), head.getGroupbbhl());
		// 集团
		itemVO.setAttributeValue(groupbbjeField, moneys[0]);
		// 全局
		itemVO.setAttributeValue(globalbbjeField, moneys[1]);
	}
}
