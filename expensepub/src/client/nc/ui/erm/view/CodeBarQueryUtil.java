package nc.ui.erm.view;

import java.util.List;

import nc.bs.erm.accruedexpense.common.AccruedBillQueryCondition;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.matterapp.common.MatterAppQueryCondition;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.erm.accruedexpense.IErmAccruedBillQueryPrivate;
import nc.itf.erm.matterapp.IErmMatterAppBillQueryPrivate;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.accruedexpense.model.AccManageAppModel;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

public class CodeBarQueryUtil {
	// 根据输入的条码查询数据，查询出数据时返回true，未查询到数据返回false
	public static AggregatedValueObject doBarCodeQuery(String value, BillManageModel model) throws BusinessException {
		if (value == null || value.trim().length() == 0) {
			return null;
		}

		// 共享服务- 编码数据格式为 单据类型+单据PK
		String billType = null;
		String billPk = null;
		if (value.length() == 24) {
			billType = value.substring(0, 4);
			billPk = value.substring(4);
		} else {
			return null;
		}

		return doBillManageQuery(billType, billPk, model);
	}

	public static AggregatedValueObject doBillManageQuery(String billType, String billPk, BillManageModel model) throws BusinessException {
		if (billType == null || billPk == null || model == null) {
			return null;
		}

		String nodeCode = model.getContext().getNodeCode();
		if (billType.equals(BXConstans.BX_DJLXBM) || billType.equals(BXConstans.JK_DJLXBM)) {
			StringBuffer sqlBuf = new StringBuffer();
			if (BXConstans.BXBILL_QUERY.equals(nodeCode)) {
				sqlBuf.append(" where zb.pk_jkbx='" + billPk + "'");
				sqlBuf.append(" and zb.pk_group='" + BXUiUtil.getPK_group() + "'");
				sqlBuf.append(" and QCBZ='N' and DR ='0' ");
			} else if (BXConstans.BXMNG_NODECODE.equals(nodeCode)) {// 单据管理
				sqlBuf.append(" where zb.pk_jkbx='" + billPk + "'");
				sqlBuf.append(" and zb.pk_group='" + BXUiUtil.getPK_group() + "'");
				sqlBuf.append(" and QCBZ='N' and DR ='0' ");
				sqlBuf.append(" and dr=0 AND (APPROVER= '" + model.getContext().getPk_loginUser() + "'");
				sqlBuf.append(" or (pk_jkbx IN(SELECT BILLID FROM PUB_WORKFLOWNOTE WF WHERE WF.CHECKMAN='" + model.getContext().getPk_loginUser() + "'");
				sqlBuf.append(" AND ISNULL(WF.DR,0) = 0 AND WF.ACTIONTYPE <> 'MAKEBILL'))) ");

			} else if (BXConstans.BXLR_QCCODE.equals(nodeCode)) {
				// 期初单据录入节点
				sqlBuf.append(" where zb.pk_jkbx='" + billPk + "'");
				sqlBuf.append(" and zb.pk_group='" + BXUiUtil.getPK_group() + "'");
				sqlBuf.append(" and QCBZ='Y' and DR <>'1'");
			} else {
				// 录入节点
				sqlBuf.append(" where zb.pk_jkbx='" + billPk + "'");
				sqlBuf.append(" and zb.pk_group='" + BXUiUtil.getPK_group() + "'");
				sqlBuf.append(" and QCBZ='N' and DR ='0' ");

				String pk_psn = ErUiUtil.getPk_psndoc();
				String whereStr = " AND ( JKBXR='" + pk_psn + "' OR creator='" + model.getContext().getPk_loginUser() + "' )";
				sqlBuf.append(whereStr);

				sqlBuf.append(" and djlxbm='" + ((ErmBillBillManageModel) model).getCurrentBillTypeCode() + "'");
			}

			List<JKBXVO> result = ((IBXBillPrivate) NCLocator.getInstance().lookup(IBXBillPrivate.class.getName())).queryVOsByWhereSql(sqlBuf.toString(), billType);
			if (result != null && result.size() > 0) {
				return result.get(0);
			}

		} else if (billType.equals(ErmAccruedBillConst.AccruedBill_Billtype)) {// 预提单
			AccruedBillQueryCondition condvo = new AccruedBillQueryCondition();

			String condition = " " + AccruedVO.getDefaultTableName() + "." + AccruedVO.PK_ACCRUED_BILL + "='" + billPk + "' ";
			condvo.setWhereSql(condition);
			condvo.setPk_tradetype(((AccManageAppModel) model).getCurrentTradeTypeCode());
			condvo.setNodeCode(model.getContext().getNodeCode());
			condvo.setPk_group(model.getContext().getPk_group());
			condvo.setPk_user(model.getContext().getPk_loginUser());
			String[] pks = NCLocator.getInstance().lookup(IErmAccruedBillQueryPrivate.class).queryBillPksByWhere(condvo);

			if (pks != null) {
				AggAccruedBillVO[] result = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class).queryBillByPks(pks, false);

				if (result != null && result.length > 0) {
					return result[0];
				}
			}
		} else if (billType.equals(ErmMatterAppConst.MatterApp_BILLTYPE)) {// 申请单
			MatterAppQueryCondition condVo = new MatterAppQueryCondition();
			String condition = " " + MatterAppVO.getDefaultTableName() + "." + MatterAppVO.PK_MTAPP_BILL + "='" + billPk + "' ";
			condVo.setWhereSql(condition);
			condVo.setPk_tradetype(((MAppModel) model).getDjlxbm());
			condVo.setNodeCode(model.getContext().getNodeCode());
			condVo.setPk_group(model.getContext().getPk_group());
			condVo.setPk_user(model.getContext().getPk_loginUser());

			String[] pks = NCLocator.getInstance().lookup(IErmMatterAppBillQueryPrivate.class).queryBillPksByWhere(condVo);
			if (pks != null) {
				AggMatterAppVO[] result = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class).queryBillByPKs(pks, false);
				if (result != null && result.length > 0) {
					return result[0];
				}
			}
		}
		return null;
	}
}
