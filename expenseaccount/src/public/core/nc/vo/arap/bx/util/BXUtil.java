package nc.vo.arap.bx.util;

import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.ErmBillTypeUtil;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.ep.bx.ReimRuleDef;
import nc.vo.ep.bx.ReimRuleDefVO;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.er.reimtype.ReimTypeUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;

public class BXUtil {

	/**
	 * ��������ģ��API(��Ԥ�����)
	 * 
	 * @param pk_group
	 *            ����
	 * @param funcode
	 *            ���ܽڵ� ������Դ��dap_dapsystem
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isProductInstalled(String strCorpPK, String pro) {
		return ErUtil.isProductInstalled(strCorpPK, pro);
	}

	/**
	 * ��������ģ��API(Ԥ�����)
	 * 
	 * @param pk_group
	 *            ����
	 * @param funcode
	 *            ���ܽڵ� ������Դ��dap_dapsystem
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isProductTbbInstalled(String pro) {
		return ErUtil.isProductTbbInstalled(pro);

	}

	public static String getBusiPk_corp(JKBXHeaderVO head, String key) {
		String pk_corp = "";
		BusiTypeVO busTypeVO = getBusTypeVO(head.getDjlxbm());
		List<String> costentity_billitems = busTypeVO.getCostentity_billitems();
		List<String> payentity_billitems = busTypeVO.getPayentity_billitems();
		List<String> useentity_billitems = busTypeVO.getUseentity_billitems();
		if (costentity_billitems.contains(key)) {
			pk_corp = head.getFydwbm();
		} else if (payentity_billitems.contains(key)) {
			pk_corp = head.getPk_org();
		} else if (useentity_billitems.contains(key)) {
			pk_corp = head.getDwbm();
		} else {
			pk_corp = head.getPk_group();// ȡ�õ�ǰ��¼��˾
		}
		return pk_corp;
	}

	public static BusiTypeVO getBusTypeVO(String djlxbm) {
		return ErmBillTypeUtil.getBusTypeVO(djlxbm);
	}

	/**
	 * @return ȡҵ������VO (busitype.xml��������)
	 * @see BusiTypeVO
	 */
	public static BusiTypeVO getBusTypeVO(String djlxbm, String djdl) {
		return ErmBillTypeUtil.getBusTypeVO(djlxbm, djdl);
	}

	public static ReimRuleDefVO getReimRuleDefvo(String djlxbm) {
		return ReimTypeUtil.getReimRuleDefvo(djlxbm);
	}

	public static List<List<String>> getReimRuleFields(String djlxbm) {
		ReimRuleDefVO reimRuleDefvo = getReimRuleDefvo(djlxbm);
		List<List<String>> fields = new ArrayList<List<String>>();
		List<String> headFields = new ArrayList<String>();
		List<String> bodyFields = new ArrayList<String>();

		headFields.add(JKBXHeaderVO.JKBXR);
		headFields.add(JKBXHeaderVO.DEPTID);
		headFields.add(JKBXHeaderVO.BZBM);
		bodyFields.add(BXBusItemVO.PK_REIMTYPE);

		if (reimRuleDefvo != null) {
			List<ReimRuleDef> reimRuleDefList = reimRuleDefvo.getReimRuleDefList();
			if (reimRuleDefList != null) {
				for (ReimRuleDef reimrule : reimRuleDefList) {
					String item = reimrule.getItemvalue();

					int i = item.indexOf(ReimRuleVO.REMRULE_SPLITER);
					if (i == -1)
						continue;

					if (item.startsWith(ReimRuleVO.Reim_body_key)) {
						bodyFields.add(item.substring(i + 1));
					} else if (item.startsWith(ReimRuleVO.Reim_head_key)) {
						headFields.add(item.substring(i + 1));
					} else {
						headFields.add(item.substring(0, i));
					}
				}
			}
		}

		fields.add(headFields);
		fields.add(bodyFields);
		return fields;
	}
	
	/**
	 * ���ɱ�����
	 * 
	 * @param jkbxvo
	 */
	public static void generateJKBXRow(JKBXVO jkbxvo) {
		if (jkbxvo == null || jkbxvo.getParentVO() == null) {
			return;
		}
		final JKBXHeaderVO headVo = jkbxvo.getParentVO();
		final BXBusItemVO[] busitemVos = jkbxvo.getChildrenVO();

		List<BXBusItemVO> unDeleteItems = new ArrayList<BXBusItemVO>();
		if (busitemVos != null) {
			for (BXBusItemVO item : busitemVos) {
				if (item.getStatus() != VOStatus.DELETED) {
					unDeleteItems.add(item);
				}
			}
		}

		BXBusItemVO tempBusitemVo = null;
		if (unDeleteItems.size() == 0) {// ������ޱ��壬������һ������
			tempBusitemVo = new BXBusItemVO();

			String[] names = tempBusitemVo.getAttributeNames();
			for (String name : names) {
				tempBusitemVo.setAttributeValue(name, headVo.getAttributeValue(name));
			}

			tempBusitemVo.setYbje(headVo.getYbje());
			tempBusitemVo.setAmount(headVo.getYbje());
			tempBusitemVo.setPk_busitem(headVo.getPk_item());
			if (jkbxvo.getMt_aggvos() != null && jkbxvo.getMt_aggvos().length > 0) {
				tempBusitemVo.setSrcbilltype(jkbxvo.getMt_aggvos()[0].getParentVO().getPk_tradetype());
				tempBusitemVo.setSrctype(jkbxvo.getMt_aggvos()[0].getParentVO().getPk_billtype());
			}
			if (headVo instanceof JKHeaderVO) {
				tempBusitemVo.setTablecode(BXConstans.BUS_PAGE_JK);
			} else {
				tempBusitemVo.setTablecode(BXConstans.BUS_PAGE);
			}
			tempBusitemVo.setStatus(VOStatus.NEW);
		} else if (unDeleteItems.size() == 1) {// ��һ�б��壬�򽫱�ͷ������õ�����,�����ý���
			tempBusitemVo = busitemVos[0];
			tempBusitemVo.setYbje(headVo.getYbje());
			tempBusitemVo.setAmount(headVo.getTotal());
			tempBusitemVo.setBbje(headVo.getBbje());
			tempBusitemVo.setHkybje(headVo.getHkybje());
			tempBusitemVo.setHkbbje(headVo.getHkbbje());
			tempBusitemVo.setZfybje(headVo.getZfybje());
			tempBusitemVo.setZfbbje(headVo.getZfbbje());
			tempBusitemVo.setCjkybje(headVo.getCjkybje());
			tempBusitemVo.setCjkbbje(headVo.getCjkbbje());
			tempBusitemVo.setGroupbbye(headVo.getGroupbbje());// ����
			tempBusitemVo.setGrouphkbbje(headVo.getGrouphkbbje());
			tempBusitemVo.setGroupzfbbje(headVo.getGroupzfbbje());
			tempBusitemVo.setGroupcjkbbje(headVo.getGroupcjkbbje());
			tempBusitemVo.setGlobalbbje(headVo.getGlobalbbje());// ȫ��
			tempBusitemVo.setGlobalhkbbje(headVo.getGlobalhkbbje());
			tempBusitemVo.setGlobalzfbbje(headVo.getGlobalzfbbje());
			tempBusitemVo.setGlobalcjkbbje(headVo.getGlobalcjkbbje());
			tempBusitemVo.setStatus(VOStatus.UPDATED);
		} else {
			return;
		}

		if (busitemVos != null && tempBusitemVo.getStatus() == VOStatus.NEW) {
			List<BXBusItemVO> childrenVos = new ArrayList<BXBusItemVO>();
			if (busitemVos != null) {
				for (BXBusItemVO item : busitemVos) {
					childrenVos.add(item);
				}
			}
			childrenVos.add(tempBusitemVo);
			jkbxvo.setChildrenVO(childrenVos.toArray(new BXBusItemVO[] {}));
		} else {
			jkbxvo.setChildrenVO(new BXBusItemVO[] { tempBusitemVo });
		}
	}
}
