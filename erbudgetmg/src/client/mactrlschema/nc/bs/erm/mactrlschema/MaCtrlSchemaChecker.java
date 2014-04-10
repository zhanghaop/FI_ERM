package nc.bs.erm.mactrlschema;

import java.util.List;

import nc.bs.erm.eventlistener.EventListenerUtil;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.util.SqlUtils_Pub;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;

import org.apache.commons.lang.ArrayUtils;

public class MaCtrlSchemaChecker {

	/***
	 * ��鶯����ҵ������� eg:�������޸ġ�ɾ��ʱ������֯�µĴ���������뵥�ѹ���ҵ�񵥾��򲻿ɽ��в���
	 * 
	 * @param treemodel
	 * @throws BusinessException
	 */
	public static void checkOperation(HierachicalDataAppModel treemodel) throws BusinessException {

		String mtappTradeTypePk = ((BilltypeVO) treemodel.getSelectedData()).getPk_billtypecode();
		String pk_org = treemodel.getContext().getPk_org();
		// ���ݽ������Ͳ�ѯ�������뵥
		String[] mtAppPks = null;
		String whereSql = " pk_tradetype = '" + mtappTradeTypePk + "' and pk_org = '" + pk_org
				+ "'  and CLOSE_STATUS = " + ErmMatterAppConst.CLOSESTATUS_N;// δ�رյ����뵥
		AggMatterAppVO[] aggMatterAppVOs = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class)
				.queryBillByWhere(whereSql);
		if (ArrayUtils.isEmpty(aggMatterAppVOs)) {
			// �ù���û�д����κη������뵥
			return;
		}

		mtAppPks = VOUtils.getAttributeValues(aggMatterAppVOs, MatterAppVO.PK_MTAPP_BILL);

		// ����ҵ�񵥾ݲ����Ը���
		if (EventListenerUtil.isExistBill(mtAppPks)) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0",
					"0upp2012V575-0123")/* @res "δ�رյķ������뵥�ѹ���ҵ�񵥾ݣ�������������ά��" */);
		}
	}
	
	/***
	 * �����ƶ�������в���������ҵ�������
	 * eg:�������޸ġ�ɾ���Ŀ��ƶ����������ڸ���֯�µĴ���������뵥�ѹ���ҵ�񵥾�(JK��BX)�򲻿ɽ��в���
	 * @param treemodel
	 * @throws BusinessException
	 */
	public static void checkCtrlBillOperation(HierachicalDataAppModel treemodel,List<String> ctrlBillList)throws BusinessException {
	
		if(ctrlBillList.isEmpty() || ctrlBillList.get(0) == null){
			return;
		}
		String pk_org = treemodel.getContext().getPk_org();
		String wheresql = " where " + SqlUtils_Pub.getInStr(JKBXHeaderVO.DJLXBM, ctrlBillList.toArray(new String[]{})) + " and pk_org='" + pk_org + "'";
		
		List<JKBXHeaderVO> bxHeaderVos = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryHeadersByWhereSql(wheresql, BXConstans.BX_DJDL);
		List<JKBXHeaderVO> jkHeaderVos = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryHeadersByWhereSql(wheresql, BXConstans.JK_DJDL);
		if (!jkHeaderVos.isEmpty()) {
			bxHeaderVos.addAll(jkHeaderVos);
		}
		if (bxHeaderVos.isEmpty()) {
			return;
		}
		String mtappTradeTypePk = ((BilltypeVO) treemodel.getSelectedData()).getPk_billtypecode();
		StringBuffer err = new StringBuffer();
		for (JKBXHeaderVO jkbxvo : bxHeaderVos) {
			String pk_item = jkbxvo.getPk_item();
			if (pk_item != null) {
				AggMatterAppVO aggvo = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class).queryBillByPK(pk_item);
				// ��װ��������
				if (aggvo != null && aggvo.getParentVO().getPk_tradetype().equals(mtappTradeTypePk)) {
					BilltypeVO billtypevo = PfDataCache.getBillTypeInfo(jkbxvo.getDjlxbm());
					if (!err.toString().contains(billtypevo.getBilltypenameOfCurrLang())) {
						err.append("��" + billtypevo.getBilltypenameOfCurrLang() + "��");
					}
				}
			}
		}
		
		if (err.length()>0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0",
					"0upp2012V575-0127", null, new String[]{err.toString()} )
										/*
										 * @res
										 * "���ƶ���{0}�ѹ������뵥������ɾ�����޸ġ�"
										 */);
		}
	}

}
