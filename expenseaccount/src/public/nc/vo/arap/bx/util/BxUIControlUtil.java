package nc.vo.arap.bx.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.bd.pub.IBDMetaDataIDConst;
import nc.itf.fi.pub.Currency;
import nc.itf.org.IDeptQryService;
import nc.pubitf.bd.accessor.GeneralAccessorFactory;
import nc.pubitf.bd.accessor.IGeneralAccessor;
import nc.vo.bd.accessor.IBDData;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

public class BxUIControlUtil {
	/**
	 * ����������ӱ�ı���
	 * �뵥��ģ���ҳǩ�����Ӧ
	 */
	public static String[] tableCodes =  new String[]{"er_busitem",
		   		"costsharedetail","er_bxcontrast","er_tbbdetail",
		   		"accrued_verify","jk_contrast","jk_busitem"};
	/**
	 * ��yer���ã�������������Ȩ����
	 * 
	 * @param jkbxr
	 * @param rolersql
	 * @param billtype
	 * @param user
	 * @param date
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public static String getAgentWhereString(String jkbxr, String rolersql, String billtype, String user, String date,
			String pk_org) throws BusinessException {
		return ErUtil.getAgentWhereString(jkbxr, rolersql, billtype, user, date, pk_org);
	}

	/**
	 * @param bxvo
	 * @param contrastData
	 * @return
	 * @throws BusinessException
	 * 
	 *             ������������ ����������VO
	 * 
	 *             ���أ������ı�����
	 * 
	 */
	public JKBXVO doContrast(JKBXVO bxvo, List<BxcontrastVO> contrastsData) throws BusinessException {
		JKBXHeaderVO head = bxvo.getParentVO();
		UFDouble zero = UFDouble.ZERO_DBL;
		if (contrastsData == null || contrastsData.size() == 0) {
			// ȡ�����ĳ���
			head.setCjkybje(zero);
			head.setCjkbbje(zero);
			head.setHkybje(zero);
			head.setHkbbje(zero);
			head.setZfybje(head.getYbje());
			head.setZfbbje(head.getBbje());
		} else {
			UFDouble cjkybje = zero;
			for (BxcontrastVO contrast : contrastsData) {
				cjkybje = cjkybje.add(contrast.getCjkybje());
			}

			// �������
			setJeMul(contrastsData, head, new String[] { JKBXHeaderVO.CJKYBJE, JKBXHeaderVO.CJKBBJE,
					JKBXHeaderVO.GROUPCJKBBJE, JKBXHeaderVO.GLOBALCJKBBJE });
			
			//��ͷ����,֧������, ȡ������, ����ȡ������
			if (cjkybje.doubleValue() > head.getYbje().doubleValue()) {
				// ������>���������л���
				setHeadJe(head, cjkybje.sub(head.getYbje()), new String[] { JKBXHeaderVO.HKYBJE, JKBXHeaderVO.HKBBJE,
						JKBXHeaderVO.GROUPHKBBJE, JKBXHeaderVO.GLOBALHKBBJE });
				head.setZfybje(zero);
				head.setZfbbje(zero);
				head.setGroupzfbbje(zero);
				head.setGlobalzfbbje(zero);
			} else if (cjkybje.doubleValue() < head.getYbje().doubleValue()) {
				// ������<����������֧��
				setHeadJe(head, head.getYbje().sub(cjkybje), new String[] { JKBXHeaderVO.ZFYBJE, JKBXHeaderVO.ZFBBJE,
						JKBXHeaderVO.GROUPZFBBJE, JKBXHeaderVO.GLOBALZFBBJE });
				head.setHkybje(zero);
				head.setHkbbje(zero);
				head.setGrouphkbbje(zero);
				head.setGlobalhkbbje(zero);
			} else if (cjkybje.doubleValue() == head.getYbje().doubleValue()) {
				// ������==����������޻������֧��
				setHeadJe(head, head.getYbje().sub(cjkybje), new String[] { JKBXHeaderVO.ZFYBJE, JKBXHeaderVO.ZFBBJE,
						JKBXHeaderVO.GROUPZFBBJE, JKBXHeaderVO.GLOBALZFBBJE });
				head.setHkybje(zero);
				head.setHkbbje(zero);
				head.setGrouphkbbje(zero);
				head.setGlobalhkbbje(zero);

				setHeadJe(head, cjkybje.sub(head.getYbje()), new String[] { JKBXHeaderVO.HKYBJE, JKBXHeaderVO.HKBBJE,
						JKBXHeaderVO.GROUPHKBBJE, JKBXHeaderVO.GLOBALHKBBJE });
				head.setZfybje(zero);
				head.setZfbbje(zero);
				head.setGroupzfbbje(zero);
				head.setGlobalzfbbje(zero);
			}
		}

		// ������������
		caculateBodyCjkje(bxvo);
		//�������ҳǩ�л����� add by chenshuaia
		caculateContrastHkJe(bxvo, contrastsData);
		return bxvo;
	}

	/**
	 * ����������
	 * 
	 * @param bxvo
	 * @param contrastsData
	 * @author chenshuaia
	 */
	private void caculateContrastHkJe(JKBXVO bxvo, List<BxcontrastVO> contrastsData) {
		if (contrastsData == null || contrastsData.size() == 0) {
			return;
		}
		UFDouble hkybje = bxvo.getParentVO().getHkybje();
		UFDouble sumHkJe = UFDouble.ZERO_DBL;
		for (BxcontrastVO contrastVo : contrastsData) {
			if (contrastVo.getHkybje() != null) {
				sumHkJe = sumHkJe.add(contrastVo.getHkybje());
			}
		}

		if (hkybje.compareTo(sumHkJe) != 0) {
			UFDouble diffJe = hkybje.sub(sumHkJe);// ������һ���������һ��
			UFDouble diffJeAbs = diffJe.abs();
			boolean isMore = diffJe.compareTo(UFDouble.ZERO_DBL) > 0;

			for (int i = contrastsData.size() - 1; i >= 0; i--) {
				BxcontrastVO contrastVo = contrastsData.get(i);
				UFDouble bodyHkJe = contrastVo.getHkybje() == null ? UFDouble.ZERO_DBL : contrastVo.getHkybje();
				UFDouble bodyfyYbJe = contrastVo.getFyybje() == null ? UFDouble.ZERO_DBL : contrastVo.getFyybje();

				if (isMore) {
					if (bodyfyYbJe.compareTo(UFDouble.ZERO_DBL) > 0) {
						if (diffJeAbs.compareTo(bodyfyYbJe) > 0) {
							contrastVo.setFyybje(UFDouble.ZERO_DBL);
							contrastVo.setHkybje(contrastVo.getCjkybje());
							diffJeAbs = diffJeAbs.sub(bodyfyYbJe);
							continue;
						} else {
							contrastVo.setFyybje(bodyfyYbJe.sub(diffJeAbs));
							contrastVo.setHkybje(bodyHkJe.add(diffJeAbs));
							break;
						}
					}
				} else {
					if (bodyHkJe.compareTo(UFDouble.ZERO_DBL) > 0) {
						if (diffJeAbs.compareTo(bodyHkJe) > 0) {
							contrastVo.setHkybje(UFDouble.ZERO_DBL);
							contrastVo.setFyybje(contrastVo.getCjkybje());
							diffJeAbs = diffJeAbs.sub(bodyHkJe);
							continue;
						} else {
							contrastVo.setFyybje(bodyfyYbJe.add(diffJeAbs));
							contrastVo.setHkybje(bodyHkJe.sub(diffJeAbs));
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * ������������(ͬʱ����֧���򻹿���)
	 * 
	 * @author chendya
	 * @param bxvo
	 */
	public void caculateBodyCjkje(JKBXVO bxvo) {
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
					modifyValues(child);
					transYbjeToBbje(head, child);
					return;
				}
				if (cjkje.compareTo(ybje) > 0) {
					// ���ʣ��ĳ��������ԭ�ҽ������ĳ�����ֵ��ԭ�ҽ����ͬ
					child.setAttributeValue(BXBusItemVO.CJKYBJE, ybje);
					modifyValues(child);
					transYbjeToBbje(head, child);
					cjkje = cjkje.sub(ybje);
				} else {
					// ���ʣ��ĳ��������ԭ�ҽ������ĳ�����ֵ��Ϊʣ��ĳ�����
					child.setAttributeValue(BXBusItemVO.CJKYBJE, cjkje);
					modifyValues(child);
					transYbjeToBbje(head, child);
					cjkje = null;
				}
			} else {
				// ������������ˣ�ʣ�����0�
				child.setAttributeValue(BXBusItemVO.CJKYBJE, new UFDouble(0));
				modifyValues(child);
				transYbjeToBbje(head, child);
			}
		}
	}

	@SuppressWarnings("unused")
	private BXBusItemVO[] filterJeNullRow(BXBusItemVO[] chilerenVO) {
		// ���˵�û�н�����
		List<BXBusItemVO> voList = new ArrayList<BXBusItemVO>();
		for (BXBusItemVO vo : chilerenVO) {
			UFDouble ybje = vo.getYbje();
			if (ybje == null) {
				ybje = UFDouble.ZERO_DBL;
			}
			if (UFDouble.ZERO_DBL.equals(ybje)) {
				// û��ԭ�ҽ����в�����
				continue;
			}
			voList.add(vo);
		}
		return voList.toArray(new BXBusItemVO[0]);
	}

	private boolean isJeNullRow(BXBusItemVO vo) {
		// �ж��Ƿ��ǽ��Ϊ�յ���
		UFDouble ybje = vo.getYbje() == null ? UFDouble.ZERO_DBL : vo.getYbje();
		UFDouble cjkJe = vo.getCjkybje() == null ? UFDouble.ZERO_DBL : vo.getCjkybje();

		if (UFDouble.ZERO_DBL.equals(ybje) && UFDouble.ZERO_DBL.equals(cjkJe)) {
			// ԭ�ҽ��Ϊ�����ʾΪ����
			return true;
		}

		return false;
	}

	private void transYbjeToBbje(JKBXHeaderVO head, BXBusItemVO itemVO) {
		String pk_corp = head.getPk_org();
		String bzbm = head.getBzbm();
		UFDouble hl = head.getBbhl();
		UFDouble ybje = itemVO.getYbje();
		UFDouble cjkybje = itemVO.getCjkybje();
		UFDouble hkybje = itemVO.getHkybje();
		UFDouble zfybje = itemVO.getZfybje();
		try {
			UFDouble[] bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm, ybje, null, null, null, hl,
					head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.BBJE, bbje[2]);
			itemVO.setAttributeValue(JKBXHeaderVO.BBYE, bbje[2]);
			bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm, cjkybje, null, null, null, hl,
					head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.CJKBBJE, bbje[2]);
			bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm, hkybje, null, null, null, hl,
					head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.HKBBJE, bbje[2]);
			bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm, zfybje, null, null, null, hl,
					head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.ZFBBJE, bbje[2]);

			// �������š�ȫ�ֱ��ҽ��
			caculateGroupAndGlobalBbje(head, itemVO, BXBusItemVO.CJKYBJE, BXBusItemVO.CJKYBJE,
					BXBusItemVO.GROUPCJKBBJE, BXBusItemVO.GLOBALCJKBBJE);

			// ����֧�����š�ȫ�ֱ��ҽ��
			caculateGroupAndGlobalBbje(head, itemVO, BXBusItemVO.ZFYBJE, BXBusItemVO.ZFYBJE, BXBusItemVO.GROUPZFBBJE,
					BXBusItemVO.GLOBALZFBBJE);

			// ���㻹��š�ȫ�ֱ��ҽ��
			caculateGroupAndGlobalBbje(head, itemVO, BXBusItemVO.HKYBJE, BXBusItemVO.HKYBJE, BXBusItemVO.GROUPHKBBJE,
					BXBusItemVO.GLOBALHKBBJE);

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
	private static void caculateGroupAndGlobalBbje(JKBXHeaderVO head, BXBusItemVO itemVO, String ybjeField,
			String bbjeField, String groupbbjeField, String globalbbjeField) throws BusinessException {
		UFDouble[] moneys = Currency.computeGroupGlobalAmount((UFDouble) itemVO.getAttributeValue(ybjeField),
				(UFDouble) itemVO.getAttributeValue(bbjeField), head.getBzbm(), head.getDjrq(), head.getPk_org(),
				head.getPk_group(), head.getGlobalbbhl(), head.getGroupbbhl());
		// ����
		itemVO.setAttributeValue(groupbbjeField, moneys[0]);
		// ȫ��
		itemVO.setAttributeValue(globalbbjeField, moneys[1]);
	}

	/**
	 * ���ݳ�����ı仯���޸ı��������ݵ����������ֵ
	 * 
	 * @author zhangxiao1
	 */
	private void modifyValues(BXBusItemVO vo) {
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

	/**
	 * ����ҳǩ�ĳ�����Ҽ���<br>
	 * ��ͷ������ҵļ���
	 * @param contrastsData
	 * @param yfbKeys
	 * @throws BusinessException
	 */
	private void setJeMul(List<BxcontrastVO> contrastsData, JKBXHeaderVO head, String[] yfbKeys)
			throws BusinessException {
		try {
			UFDouble[] yfbs = null;
			for (Iterator<BxcontrastVO> iter = contrastsData.iterator(); iter.hasNext();) {
				BxcontrastVO vo = iter.next();
				UFDouble cjkybje = vo.getCjkybje();
				UFDouble[] values = Currency.computeYFB(head.getPk_org(), Currency.Change_YBJE, head.getBzbm(),
						cjkybje, null, null, null, head.getBbhl(), head.getDjrq());

				vo.setCjkbbje(values[2]);
				vo.setBbje(values[2]);

				UFDouble[] money = Currency
						.computeGroupGlobalAmount(cjkybje, values[2], head.getBzbm(), head.getDjrq(), head.getPk_org(),
								head.getPk_group(), head.getGlobalbbhl(), head.getGroupbbhl());

				vo.setGroupcjkbbje(money[0]);
				vo.setGlobalcjkbbje(money[1]);
				vo.setGroupbbje(money[0]);
				vo.setGlobalbbje(money[1]);

				if (yfbs == null) {
					yfbs = values;
				} else {
					for (int i = 0; i < 3; i++) {
						yfbs[i] = yfbs[i].add(values[i]);
					}
				}
			}
			UFDouble[] money2 = Currency.computeGroupGlobalAmount(yfbs[0], yfbs[2], head.getBzbm(), head.getDjrq(),
					head.getPk_org(), head.getPk_group(), head.getGlobalbbhl(), head.getGroupbbhl());

			head.setAttributeValue(yfbKeys[0], yfbs[0]);
			head.setAttributeValue(yfbKeys[1], yfbs[2]);
			head.setAttributeValue(yfbKeys[2], money2[0]);
			head.setAttributeValue(yfbKeys[3], money2[1]);

		} catch (BusinessException e) {
			// ���ñ��Ҵ���.
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000009")/*
																														 * @
																														 * res
																														 * "���ñ��Ҵ���!"
																														 */);
		}
	}

	/**
	 * �����ͷ����ֶ�
	 * 
	 * @param head
	 * @param ybje
	 * @param yfbKeys
	 * @throws BusinessException
	 */
	private void setHeadJe(JKBXHeaderVO head, UFDouble ybje, String[] yfbKeys) throws BusinessException {
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
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000009")/*
																														 * @
																														 * res
																														 * "���ñ��Ҵ���!"
																														 */);
		}
	}

	/**
	 * ���س��õ���VO
	 * 
	 * @author chendya
	 * @param pk_org
	 * @param pk_group
	 * @param djlxbm
	 * @param includeGroup
	 * @return
	 * @throws BusinessException
	 */
	public static List<JKBXVO> getInitBill(String pk_org, String pk_group, String djlxbm, boolean includeGroup)
			throws BusinessException {

		DjCondVO condVO = new DjCondVO();

		condVO.isInit = true;
		condVO.defWhereSQL = " zb.djlxbm='" + djlxbm + "' and zb.dr=0 and ((isinitgroup='N' and pk_org='" + pk_org
				+ "') or isinitgroup='Y') ";
		condVO.isCHz = false;
		condVO.pk_group = new String[] { pk_group };
		// ��֯�ͼ��ŵĺϲ���ѯ������Զ�̵��ô�������ѯ������,���ȷ�����֯����VO
		List<JKBXVO> all = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryVOs(0, -99, condVO);

		if (all == null) {
			return new ArrayList<JKBXVO>();
		}
		List<JKBXVO> orgVOList = new ArrayList<JKBXVO>();
		List<JKBXVO> groupVOList = new ArrayList<JKBXVO>();
		for (Iterator<JKBXVO> iterator = all.iterator(); iterator.hasNext();) {
			JKBXVO vo = iterator.next();
			if (UFBoolean.TRUE.equals(vo.getParentVO().getIsinitgroup())) {
				groupVOList.add(vo);
			} else {
				orgVOList.add(vo);
			}
		}
		// ���ȷ�����֯���ĳ��õ���
		return orgVOList.size() > 0 ? orgVOList : groupVOList;
	}

	public static BusiTypeVO getBusTypeVO(String djdl, String djlxbm) {
		return BXUtil.getBusTypeVO(djlxbm, djdl);
	}

	public static boolean doSimpleNoEquals(String reftype, String pk_corp, Object ruleKey, Object voKey) {
		IGeneralAccessor acc = null;

		if (reftype.equals("��������")) {/* -=notranslate=- */
			acc = GeneralAccessorFactory.getAccessor(IBDMetaDataIDConst.AREACLASS);
		}
		if (reftype.equals("����")) {/* -=notranslate=- */
			acc = GeneralAccessorFactory.getAccessor(IBDMetaDataIDConst.DEPT);
		}

		if (acc != null) {
			while (voKey != null && voKey.toString().trim().length() != 0 && doSimpleNoEquals(voKey, ruleKey)) {
				IBDData doc = acc.getDocByPk(voKey.toString());
				if (doc == null)
					return doSimpleNoEquals(voKey, ruleKey);
				voKey = doc.getParentPk();
			}
			return doSimpleNoEquals(voKey, ruleKey);
		}

		return doSimpleNoEquals(ruleKey, voKey);
	}

	public static boolean doSimpleNoEquals(Object ruleKey, Object voKey) {
		if (ruleKey == null)
			return false;
		else if (voKey == null)
			return true;
		else
			return !VOUtils.simpleEquals(ruleKey.toString(), voKey.toString());
	}

	/**
	 * ִ�б��屨����׼
	 * 
	 * @param bxvo
	 *            ����VO
	 * @param reimRuleDataMap
	 *            ������׼Map
	 * @param bodyReimRuleMap
	 *            ���屨����׼Map
	 * @return
	 */
	public static List<BodyEditVO> doBodyReimAction(JKBXVO bxvo, Map<String, List<SuperVO>> reimRuleDataMap,
			BillTempletBodyVO[] billtempletbodyvos, Map<String, List<SuperVO>> reimDimDataMap) {
		List<BodyEditVO> result = new ArrayList<BodyEditVO>();
		if (bxvo == null || bxvo.getParentVO() == null || bxvo.getParentVO().getDjlxbm() == null
				|| billtempletbodyvos==null || billtempletbodyvos.length==0
				|| reimDimDataMap==null || reimRuleDataMap==null)
			return result;

		String djlxbm = bxvo.getParentVO().getDjlxbm();

		//����δ���ñ�����׼ά�Ȼ�û�б�׼����ֱ�ӷ���
		List<SuperVO> matchReimDim = reimDimDataMap.get(djlxbm);
		if(matchReimDim==null || reimRuleDataMap.get(djlxbm)==null)
			return result;
		
		//������׼ά���У����ݶ�Ӧ�� Ϊ�ӱ�����Ҫ�ó����������бȽ�
		//���籨�����;Ͷ�Ӧ�ӱ�����ԣ���Ҫ�����ӱ���dims�д洢<itemkey,billrefcode,������������>
		List<String> dims = new ArrayList<String>();
		String centcontrolitem = null;
		for(SuperVO vo:reimDimDataMap.get(djlxbm))
		{
			ReimRuleDimVO dimvo = (ReimRuleDimVO)vo;
			if(dimvo.getControlflag().booleanValue())
				centcontrolitem=dimvo.getCorrespondingitem();
			for(String tablecode:tableCodes)
				if(dimvo.getBillrefcode()!=null && dimvo.getBillrefcode().startsWith(tablecode))
				{
					dims.add(dimvo.getCorrespondingitem()+","+dimvo.getBillrefcode().substring(dimvo.getBillrefcode().indexOf(".")+1)
							+","+dimvo.getDatatypename());
				}
		}
		if(centcontrolitem==null)
			return result;
		//ѡ���뵥�ݶ�Ӧ�ı�����׼��ֻƥ���ͷ
		List<ReimRulerVO> matchReimRule = getMatchReimRuleByHead(bxvo, reimRuleDataMap.get(djlxbm),matchReimDim);
		List<String> matchedpks = new ArrayList<String>();
		for(ReimRulerVO mvo:matchReimRule){
			matchedpks.add(mvo.getAttributeValue(centcontrolitem).toString());
		}
		//�����б�׼����ʾ����ȡ�������޸ı�ͷʱ��Ҫ���� pk showitem
		List<String> pks = new ArrayList<String>();
		for(SuperVO vo:reimRuleDataMap.get(djlxbm)){
			if(!matchedpks.contains(vo.getAttributeValue(centcontrolitem).toString()) 
					&& !pks.contains(vo.getAttributeValue(centcontrolitem).toString())){
				pks.add(vo.getAttributeValue(centcontrolitem).toString());
				ReimRulerVO newvo = new ReimRulerVO();
				newvo.setAttributeValue(centcontrolitem, vo.getAttributeValue(centcontrolitem));
				newvo.setShowitem(((ReimRulerVO)vo).getShowitem());
				newvo.setShowitem_name(((ReimRulerVO)vo).getShowitem_name());
				newvo.setAmount(new UFDouble(0));
				matchReimRule.add(newvo);
			}
		}
		// ��������ı�����׼���������ģ����Ӧ����ʾ��׼�����������
		result = addBodyEdit(bxvo, billtempletbodyvos, matchReimRule, dims, centcontrolitem);
		return result;
	}

	/**
	 *  ���屨����׼Map<tablecode@itemkey,centcontrolPK��Ĭ��Ϊ��������pk��>ѭ��
	 * @param bodyReimRuleMap
	 * @return
	 */
	public static List<BodyEditVO> addBodyEdit(JKBXVO bxvo,
			BillTempletBodyVO[] billtempletbodyvos,List<ReimRulerVO> matchReimRule,
			List<String> dims,String centcontrolitem) {
		
		List<BodyEditVO> result = new ArrayList<BodyEditVO>();//���ؽ��
		
		//��һ��������ÿ��ҳǩ��ÿһ��item���鿴��Ԫ�������ԣ���ȡ��ʾ���������
		HashMap<String, List<String>> bodyReimRuleMap = new HashMap<String, List<String>>();

		for (BillTempletBodyVO bodyvo : billtempletbodyvos) {
			// ����û��Ԫ���ݣ�ֱ������
			if (bodyvo.getMetadataproperty() == null) {
				continue;
			} else {
				for (ReimRulerVO rule : matchReimRule) {
					// ��ȡvo�ĵ�����ʾ�����������Ŀ�����PK
					String showitem = rule.getShowitem_name();
					String controlitem = rule.getControlitem_name();
					// 0������ 1��ʾ 2����
					Integer controlflag = rule.getControlflag();
					String centcontrolPK = rule.getAttributeValue(centcontrolitem).toString();

					// �鿴ҳǩ��Ԫ��������
					String metadata = bodyvo.getMetadataproperty();
					metadata = metadata.substring(metadata.indexOf(".") + 1);

					// �����׼����ʾ����ͬ�������<ҳǩitem,���Ŀ�����pk>
					String reimRuleMapKey = bodyvo.getTable_code() + "." + bodyvo.getItemkey();

					if (metadata.equals(showitem)) {
						if (bodyReimRuleMap.get(reimRuleMapKey) == null) {
							List<String> centControlPKs = new ArrayList<String>();
							centControlPKs.add(centcontrolPK);
							bodyReimRuleMap.put(reimRuleMapKey, centControlPKs);
						} else {
							bodyReimRuleMap.get(reimRuleMapKey).add(centcontrolPK);
						}
					}
					// �����׼�Ŀ�������ͬ�������<ҳǩitem,���Ŀ�����pk;����ģʽ>
					if (metadata.equals(controlitem)) {
						if (bodyReimRuleMap.get(reimRuleMapKey) == null) {
							List<String> centControlPKs = new ArrayList<String>();
							centControlPKs.add(centcontrolPK + ";" + controlflag);
							bodyReimRuleMap.put(reimRuleMapKey, centControlPKs);
						} else {
							bodyReimRuleMap.get(reimRuleMapKey).add(centcontrolPK + ";" + controlflag);
						}
					}
				}
			}
		}
		
		// key : tableCode-itemkey-row
		Map<String, BodyEditVO> bodyEditVoMap = new HashMap<String, BodyEditVO>();
		
		for (String key : bodyReimRuleMap.keySet()) {
			List<String> centcontrolKeys = bodyReimRuleMap.get(key);// ��������key
			String[] keys = key.split(ReimRulerVO.REMRULE_SPLITER);
			String tableCode = keys[0];
			String itemkey = keys[1];
			
			CircularlyAccessibleValueObject[] bodyValueVOs = bxvo.getTableVO(tableCode);

			if (bodyValueVOs != null) {
				int row = -1;
				
				// �Զ�ӦtableCode�µ����������н��б���
				for (CircularlyAccessibleValueObject body : bodyValueVOs) {
					row++;
					String bodyEditVoMapKey = tableCode + "-" + itemkey + "-" + row;
					for(String centcontrolKey : centcontrolKeys){	
						String[] pks = null;
						String pk = centcontrolKey;
						if(centcontrolKey.contains(";")){
							pks= centcontrolKey.split(";");
							pk = pks[0];
						}else{
							if (bodyEditVoMap.get(bodyEditVoMapKey) == null || bodyEditVoMap.get(bodyEditVoMapKey).getValue() == null
									|| bodyEditVoMap.get(bodyEditVoMapKey).getValue().equals(UFDouble.ZERO_DBL)) {
								BodyEditVO bodyEditVO2 = new BodyEditVO();
								bodyEditVO2.setValue(UFDouble.ZERO_DBL);
								bodyEditVO2.setRow(row);
								bodyEditVO2.setItemkey(itemkey);
								bodyEditVO2.setTablecode(tableCode);

								bodyEditVoMap.put(bodyEditVoMapKey, bodyEditVO2);
							}
						}
	
						for (ReimRulerVO rule : matchReimRule) {
							// �жϺ��Ŀ����Ĭ��Ϊ�������ͣ��Ƿ���ͬ
							if (doSimpleNoEquals(rule.getAttributeValue(centcontrolitem), pk)) {
								continue;
							}
							// �鿴ά������������ж�Ӧ�ֶ�ֵ�Ƿ���ͬ
							boolean match = true;
							List<BodyEditVO> dimlist = new ArrayList<BodyEditVO>();
							for(String str:dims){
								String[] itemvalues = str.split(",");
								//dims�д����[PK_REIMTYPE, pk_reimtype, ��������]�����һλ����������
								if(itemvalues.length > 3 && itemvalues[2]!=null){
									if (doSimpleNoEquals(itemvalues[2], bxvo.getParentVO().getPk_group(),
											rule.getAttributeValue(itemvalues[0]), body.getAttributeValue(itemvalues[1]))) {
										match = false;
										break;
									}
								}
								else{
									if (doSimpleNoEquals(rule.getAttributeValue(itemvalues[0]), body.getAttributeValue(itemvalues[1]))) {
										match = false;
										break;
									}
								}
								BodyEditVO bvo = new BodyEditVO();
								bvo.setTablecode(tableCode);
								bvo.setItemkey(itemvalues[1]);
								dimlist.add(bvo);
							}
							if (match) {
								//pksΪ��˵���������ֺţ�Ϊ��ʾ��
								if (pks == null) {
									BodyEditVO bodyEditVO = new BodyEditVO();
									bodyEditVO.setValue(rule.getAmount());
									bodyEditVO.setRow(row);
									bodyEditVO.setItemkey(itemkey);
									bodyEditVO.setTablecode(tableCode);
									
									bodyEditVoMap.put(bodyEditVoMapKey, bodyEditVO);
								} else {
									ControlBodyEditVO bodyEditVO = new ControlBodyEditVO();
									bodyEditVO.setValue(rule.getAmount());
									bodyEditVO.setRow(row);
									bodyEditVO.setItemkey(itemkey);
									bodyEditVO.setTablecode(tableCode);
									bodyEditVO.setDimlist(dimlist);
									if (rule.getControlformula() != null)
										bodyEditVO.setFormulaRule(rule.getControlformula());
									if (pks.length > 1) {
										try {
											bodyEditVO.setTip(Integer.valueOf(pks[1]));
										} catch (Exception e) {

										}
									}
									bodyEditVoMap.put(bodyEditVoMapKey, bodyEditVO);
								}
							}
						}
					}
				}
			}
		}
		
		result.addAll(bodyEditVoMap.values());
		return result;
	}
	/**
	 * ��ͷ������׼
	 * 
	 * @param bxvo
	 *            ����VO
	 * @param reimRuleDataMap
	 *            ������׼Map
	 * @param reimRuleDataMap
	 *            ��׼ά��Map
	 * @param expenseType
	 *            ��������
	 * @param reimtypeMap
	 *            ��������
	 * @return
	 */
	public static List<String> doHeadReimAction(JKBXVO bxvo, Map<String, List<SuperVO>> reimRuleDataMap,
			Map<String, List<SuperVO>> reimDimDataMap) {//,Map<String, SuperVO> expenseType, Map<String, SuperVO> reimtypeMap
		List<String> reimrule = new ArrayList<String>();

		if (bxvo == null || bxvo.getParentVO() == null || bxvo.getParentVO().getDjlxbm() == null
				|| reimRuleDataMap==null || reimDimDataMap==null)
			return reimrule;

		String djlxbm = bxvo.getParentVO().getDjlxbm();
		List<SuperVO> matchReimDim = reimDimDataMap.get(djlxbm);
		if(matchReimDim==null)
			return reimrule;
		// ���ݵ������ͻ�ȡ�Զ��屨����׼
		List<ReimRulerVO> matchReimRule = getMatchReimRuleByHead(bxvo, reimRuleDataMap.get(djlxbm),matchReimDim);
		//ѡ�����ñ�׼ά��ʱ���õ���Ҫչʾ����
		List<String> dims = new ArrayList<String>();
		for(SuperVO vo:matchReimDim)
		{
			ReimRuleDimVO dimvo = (ReimRuleDimVO)vo;
			if(dimvo.getShowflag().booleanValue())
			{
				dims.add(dimvo.getCorrespondingitem());
			}
		}
		for (ReimRulerVO rule : matchReimRule) {
			StringBuffer reim = new StringBuffer("");
			for (String str : dims)
			{
				if(str.equalsIgnoreCase("amount"))
					continue;
				if(rule.getAttributeValue(str+"_name")!=null)
					reim.append(rule.getAttributeValue(str+"_name")+":");
			}
			reim.append(rule.getAmount());
			reimrule.add(reim.toString());
		}
		return reimrule;
	}

	public static List<ReimRulerVO> getMatchReimRuleByHead(JKBXVO bxvo,
			List<? extends SuperVO> dataList,List<? extends SuperVO> matchReimDim) {
		List<ReimRulerVO> matchedRule = new ArrayList<ReimRulerVO>();

		if (dataList == null || dataList.size() == 0 || bxvo == null || bxvo.getParentVO() == null || matchReimDim==null)
			return matchedRule;

		ReimRulerVO[] ruleVOs = dataList.toArray(new ReimRulerVO[] {});
		Arrays.sort(ruleVOs);
		// �������湹��ı�����׼
		HashSet<String> ruleset = new HashSet<String>();

		for (ReimRulerVO rule : ruleVOs) {
			StringBuffer bodykey = new StringBuffer();
			boolean match = true;
			for (SuperVO vo : matchReimDim) {
				ReimRuleDimVO dim = (ReimRuleDimVO)vo;
				if (rule.getAttributeValue(dim.getCorrespondingitem()) != null) {
					bodykey.append(rule.getAttributeValue(dim.getCorrespondingitem()));
				}
				//�������û�����õ��ݶ�Ӧ����Ӧ��׼��Ϊ�գ������Ƚϣ�Ĭ��Ϊ�˱�׼����
				if(dim.getBillrefcode()==null || rule.getAttributeValue(dim.getCorrespondingitem())==null)
					continue;
				String itemvalue = dim.getBillrefcode();
				Object headvalue = "";
				if (itemvalue.contains(".")) {
					//����"."��˵������������������Ҳ�������ӱ�
					//����������ֻ֧�ֱ����ˡ��տ��ˡ��������š����óе�����
					String[] keys = itemvalue.split(ReimRulerVO.REMRULE_SPLITER);
					if (keys[0].equals(ReimRulerVO.Reim_jkbxr_key)
							|| itemvalue.startsWith(ReimRulerVO.Reim_receiver_key)) {
						headvalue = getPsnDef1((String) bxvo.getParentVO().getAttributeValue(keys[0]), keys[1]);
					} else if (keys[0].equals(ReimRulerVO.Reim_deptid_key)
							|| itemvalue.startsWith(ReimRulerVO.Reim_fydeptid_key)) {
						try {
							headvalue = getDeptDef1((String) bxvo.getParentVO().getAttributeValue(keys[0]),keys[1]);
						} catch (BusinessException e) {
							ExceptionHandler.consume(e);
						}
					}
				} else {
					//������"."��ֱ���뵥�ݵ�ParentVO���бȽ�
					//������Ҫ���⴦��������û�еĻ�Ҫһֱ������
					if(dim.getCorrespondingitem().equalsIgnoreCase(ReimRulerVO.PK_DEPTID))
					{
						String deptid = bxvo.getParentVO().getDeptid();
						while (deptid != null && doSimpleNoEquals(rule.getPk_deptid(), deptid)) {
							//������Ų�һ����һֱ���ϲ��ң�ֱ���ҵ���ͬ�Ĳ���Ϊֹ
							deptid = getFatherDept(deptid);
						}

						if (doSimpleNoEquals(rule.getPk_deptid(), deptid)) {
							match = false;
							break;
						}
					}
					headvalue=bxvo.getParentVO().getAttributeValue(dim.getBillrefcode());
				}
				if(headvalue==null || headvalue.equals(""))
					continue;
				// ����ͷ������һ���������׼��ͬ������������ʾ������
				if (doSimpleNoEquals(dim.getDatatypename(), bxvo.getParentVO().getPk_group(),
						rule.getAttributeValue(dim.getCorrespondingitem()), headvalue)) {
					match = false;
					break;
				}
			}

			if (match == false)
				continue;

			int scale = 2;
			if (bxvo.getParentVO().getBzbm() != null) {
				try {
					scale = Currency.getCurrDigit(bxvo.getParentVO().getBzbm());
				} catch (Exception e) {
					ExceptionHandler.consume(e);
				}
				rule.setAmount(rule.getAmount().setScale(scale, UFDouble.ROUND_HALF_UP));
			}

			if (!ruleset.contains(bodykey.toString()) && bodyRuleMatch(bxvo, matchReimDim, rule)) {
				matchedRule.add(rule);
				ruleset.add(bodykey.toString());
			}
		}
		return matchedRule;
	}

	private static String getFatherDept(String dept) {
		if (dept == null || dept.trim().length() == 0)
			return null;

		DeptVO deptdocVOs = null;
		try {
			deptdocVOs = NCLocator.getInstance().lookup(IDeptQryService.class).queryDeptVOByID(dept);
		} catch (BusinessException e) {
			nc.bs.logging.Log.getInstance("ermExceptionLog").error(e);
		}

		if (deptdocVOs == null)
			return null;

		return deptdocVOs.getPk_fatherorg();
	}

	private static String getDeptDef1(String dept, String keyString) throws BusinessException {
		if (dept == null)
			return null;

		DeptVO deptdocVO = new DeptVO();
		deptdocVO.setPk_dept(dept);
		DeptVO deptdocVOs = NCLocator.getInstance().lookup(IDeptQryService.class).queryDeptVOByID(dept);

		if (deptdocVOs == null)
			return null;

		try {
			if (deptdocVOs.getDef1() != null) {
				return (String) deptdocVOs.getAttributeValue(keyString);
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
		return getDeptDef1(deptdocVOs.getPk_fatherorg(), keyString);
	}

	private static String getPsnDef1(String pk_psndoc, String keyString) {
		if (pk_psndoc == null)
			return null;

		PsndocVO[] psnDoc = null;
		try {
			psnDoc = NCLocator.getInstance().lookup(IPsndocQueryService.class)
					.queryPsndocVOsByCondition("pk_psndoc='" + pk_psndoc + "'");
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		if (psnDoc == null)
			return null;

		return (String) psnDoc[0].getAttributeValue(keyString);
	}
	
	/**
	 * �жϵ�����¼�Ƿ���ϱ�׼
	 * 
	 * @param jkvo
	 * @param reimDim
	 * @param rule
	 * @param bxVO
	 * 2014.10.8
	 * @return
	 */
	private static boolean oneBodyRuleMatch(List<? extends SuperVO> reimDim, ReimRulerVO rule, BXBusItemVO bvo) {
		boolean match = true;

		if (reimDim == null || rule == null || bvo == null) {
			return false;
		}
		for (SuperVO vo : reimDim) {
			ReimRuleDimVO dimVO = (ReimRuleDimVO) vo;
			if (dimVO.getBillrefcode() == null || rule.getAttributeValue(dimVO.getCorrespondingitem()) == null) {
				continue;
			} else {
				String itemValue = dimVO.getBillrefcode();
				if (itemValue.contains(".")) {
					String[] keys = itemValue.split(ReimRulerVO.REMRULE_SPLITER);
					Object bodyVule = bvo.getAttributeValue(keys[1]);
					if (doSimpleNoEquals(rule.getAttributeValue(dimVO.getCorrespondingitem()), bodyVule)) {
						match = false;
						break;
					}
				}
			}
		}
		return match;
	}
	
	/**
	 * �жϱ����Ƿ���ϱ�׼
	 * 
	 * @param jkvo
	 * @param reimDim
	 * @param rule
	 * 2014.10.8
	 * @return
	 */
	private static boolean bodyRuleMatch(JKBXVO jkvo, List<? extends SuperVO> reimDim, ReimRulerVO rule) {

		boolean ruleMatch = false;
		if (jkvo == null || reimDim == null || rule == null) {
			return false;
		}

		BXBusItemVO[] bvo = jkvo.getChildrenVO();

		if (bvo != null && bvo.length > 0) {
			for (BXBusItemVO vo : bvo) {
				if (oneBodyRuleMatch(reimDim, rule, vo)) {
					ruleMatch = true;
					break;
				}
			}
		}
		return ruleMatch;
	}

}