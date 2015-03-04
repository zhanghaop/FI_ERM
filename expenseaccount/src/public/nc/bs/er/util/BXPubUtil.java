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
	 * �Զ�����
	 * 
	 * @param bxVo ������VO
	 * @param isCancel �Ƿ�ȡ��
	 * @throws BusinessException
	 */
	public static void autoContrast(JKBXVO bxVo , boolean isCancel) throws BusinessException {
		if (bxVo == null || bxVo instanceof JKVO) {
			return;
		}
		
		if(!isCancel){
			StringBuffer sqlBuf = new StringBuffer();
			
			//������������Ľ����������ڰ���ǰ����
			sqlBuf.append(" where zb.yjye>0 and zb.dr=0 and zb.djzt=3 ");
			sqlBuf.append(" and zb.bzbm='" + bxVo.getParentVO().getBzbm() + "'");
			sqlBuf.append(" and zb.jkbxr='" + bxVo.getParentVO().getJkbxr() + "'");
			sqlBuf.append(" and zb.djrq<='" + bxVo.getParentVO().getDjrq() + "'");
			sqlBuf.append(" order by zb.djrq ");
			
			List<JKBXVO> result = ((IBXBillPrivate) NCLocator.getInstance().lookup(IBXBillPrivate.class.getName()))
					.queryVOsByWhereSql(sqlBuf.toString(), BXConstans.JK_DJDL);
			
			if(result != null && result.size() > 0){
				fillContrastVo(bxVo, result);//���������Ϣ
			}
		}else{
			bxVo.setContrastVO(null);
		}
		
		fillBxVoContrastJe(bxVo);//���䱨�����г�����Ϣ���
	}
	
	/**
	 * ���䱨�����еĳ����������������֧����
	 * @param bxVo
	 * @throws BusinessException
	 */
	private static void fillBxVoContrastJe(JKBXVO bxVo) throws BusinessException {
		if (bxVo == null) {
			return;
		}
		
		JKBXHeaderVO head = bxVo.getParentVO();
		if(bxVo.getContrastVO() == null || bxVo.getContrastVO().length == 0){
			// ȡ�����ĳ���
			bxVo.getParentVO().setCjkybje(UFDouble.ZERO_DBL);//������
			bxVo.getParentVO().setCjkbbje(UFDouble.ZERO_DBL);
			bxVo.getParentVO().setGroupcjkbbje(UFDouble.ZERO_DBL);
			bxVo.getParentVO().setGlobalcjkbbje(UFDouble.ZERO_DBL);
			bxVo.getParentVO().setHkybje(UFDouble.ZERO_DBL);//������
			bxVo.getParentVO().setHkbbje(UFDouble.ZERO_DBL);
			bxVo.getParentVO().setGrouphkbbje(UFDouble.ZERO_DBL);
			bxVo.getParentVO().setGlobalhkbbje(UFDouble.ZERO_DBL);
			bxVo.getParentVO().setZfybje(bxVo.getParentVO().getYbje());//֧�����
			bxVo.getParentVO().setZfbbje(bxVo.getParentVO().getBbje());
			bxVo.getParentVO().setGroupzfbbje(bxVo.getParentVO().getGroupbbje());
			bxVo.getParentVO().setGlobalzfbbje(bxVo.getParentVO().getGlobalbbje());
			
			if(bxVo.getChildrenVO() != null){
				for(BXBusItemVO itemVo : bxVo.getChildrenVO()){
					itemVo.setCjkybje(UFDouble.ZERO_DBL);//������
					itemVo.setCjkbbje(UFDouble.ZERO_DBL);
					itemVo.setGroupcjkbbje(UFDouble.ZERO_DBL);
					itemVo.setGlobalcjkbbje(UFDouble.ZERO_DBL);
					itemVo.setHkybje(UFDouble.ZERO_DBL);//������
					itemVo.setHkbbje(UFDouble.ZERO_DBL);
					itemVo.setGrouphkbbje(UFDouble.ZERO_DBL);
					itemVo.setGlobalhkbbje(UFDouble.ZERO_DBL);
					itemVo.setZfybje(itemVo.getYbje());//֧�����
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
			
			//������
			setHeadJe(head, cjkybje, new String[] { JKBXHeaderVO.CJKYBJE, JKBXHeaderVO.CJKBBJE,
				JKBXHeaderVO.GROUPCJKBBJE, JKBXHeaderVO.GLOBALCJKBBJE });
			
			//֧�����
			setHeadJe(head, head.getYbje().sub(cjkybje), new String[] { JKBXHeaderVO.ZFYBJE, JKBXHeaderVO.ZFBBJE,
				JKBXHeaderVO.GROUPZFBBJE, JKBXHeaderVO.GLOBALZFBBJE });
			
			//����������
			caculateBodyCjkje(bxVo);
		}
	}

	private static void fillContrastVo(JKBXVO bxVo, List<JKBXVO> result) {
		if(bxVo == null || result == null || result.size() == 0){
			return;
		}
		
		List<BxcontrastVO> list = new ArrayList<BxcontrastVO>();

		//��װ������Ϣ
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
					bxcontrastVO.setHkybje(UFDouble.ZERO_DBL);//���������ڵ��ڳ�����������ֻ�����
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
	 * �����ͷ����ֶ�
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
			// ���ñ��Ҵ���.
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000009")/*
																														 * @
																														 * res
																														 * "���ñ��Ҵ���!"
																														 */);
		}
	}
	
	/**
	 * ������������(ͬʱ����֧���򻹿���)
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

		UFDouble cjkje = head.getCjkybje();//�ܳ�������з���
		for (int i = 0; i < noNullChildrenVoList.size(); i++) {
			BXBusItemVO child = noNullChildrenVoList.get(i);

			if (cjkje != null) {
				// ����û������ĳ�����
				UFDouble ybje = child.getYbje();
				// ��ǰ�������һ��
				if (i == noNullChildrenVoList.size() - 1) {
					child.setAttributeValue(BXBusItemVO.CJKYBJE, cjkje);
				}else if (cjkje.compareTo(ybje) > 0) {
					// ���ʣ��ĳ��������ԭ�ҽ������ĳ�����ֵ��ԭ�ҽ����ͬ
					child.setAttributeValue(BXBusItemVO.CJKYBJE, ybje);
					cjkje = cjkje.sub(ybje);
				} else {
					// ���ʣ��ĳ��������ԭ�ҽ������ĳ�����ֵ��Ϊʣ��ĳ�����
					child.setAttributeValue(BXBusItemVO.CJKYBJE, cjkje);
					cjkje = null;
				}
			} else {
				// ������������ˣ�ʣ�����0�
				child.setAttributeValue(BXBusItemVO.CJKYBJE, UFDouble.ZERO_DBL);
			}
			caculateZfHkJe(child);
			transYbjeToBbje(head, child);
		}
	}
	
	private static boolean isJeNullRow(BXBusItemVO vo) {
		// �ж��Ƿ��ǽ��Ϊ�յ���
		UFDouble ybje = vo.getYbje() == null ? UFDouble.ZERO_DBL : vo.getYbje();
		UFDouble cjkJe = vo.getCjkybje() == null ? UFDouble.ZERO_DBL : vo.getCjkybje();

		if (UFDouble.ZERO_DBL.equals(ybje) && UFDouble.ZERO_DBL.equals(cjkJe)) {
			// ԭ�ҽ��Ϊ�����ʾΪ����
			return true;
		}

		return false;
	}
	
	/**
	 * ���ݳ�����ı仯���޸ı��������ݵ����������ֵ
	 * 
	 * @author 
	 */
	private static void caculateZfHkJe(BXBusItemVO vo) {
		UFDouble ybje = vo.getYbje();
		UFDouble cjkybje = vo.getCjkybje();
		if (ybje.getDouble() > cjkybje.getDouble()) {// ���ԭ�ҽ����ڳ�����
			vo.setAttributeValue(BXBusItemVO.ZFYBJE, ybje.sub(cjkybje));// ֧�����=ԭ�ҽ��-������
			vo.setAttributeValue(BXBusItemVO.HKYBJE, UFDouble.ZERO_DBL);
		} else {
			vo.setAttributeValue(BXBusItemVO.HKYBJE, cjkybje.sub(ybje));// ������=������-ԭ�ҽ��
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

			// �������š�ȫ�ֱ��ҽ��
			caculateGroupAndGlobalBbje(head, itemVO, BXBusItemVO.CJKYBJE, BXBusItemVO.CJKYBJE, BXBusItemVO.GROUPCJKBBJE, BXBusItemVO.GLOBALCJKBBJE);

			// ����֧�����š�ȫ�ֱ��ҽ��
			caculateGroupAndGlobalBbje(head, itemVO, BXBusItemVO.ZFYBJE, BXBusItemVO.ZFYBJE, BXBusItemVO.GROUPZFBBJE, BXBusItemVO.GLOBALZFBBJE);

			// ���㻹��š�ȫ�ֱ��ҽ��
			caculateGroupAndGlobalBbje(head, itemVO, BXBusItemVO.HKYBJE, BXBusItemVO.HKYBJE, BXBusItemVO.GROUPHKBBJE, BXBusItemVO.GLOBALHKBBJE);

		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * ���㼯�ź�ȫ�ֱ��ҽ��
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
		// ����
		itemVO.setAttributeValue(groupbbjeField, moneys[0]);
		// ȫ��
		itemVO.setAttributeValue(globalbbjeField, moneys[1]);
	}
}
