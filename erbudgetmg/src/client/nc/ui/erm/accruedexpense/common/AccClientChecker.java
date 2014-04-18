package nc.ui.erm.accruedexpense.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.erm.accruedexpense.check.AccruedBillVOChecker;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDouble;

public class AccClientChecker {
	/**
	 * 前台校验
	 * 
	 * @param aggVo
	 * @throws BusinessException
	 */
	public void checkClientSave(AggAccruedBillVO aggVo, AggAccruedBillVO oldAggVo, BillCardPanel panel)
			throws BusinessException {
		// 将信息进行补充，便于校验操作
		AggAccruedBillVO newAggVo = fillUpChildren(aggVo, oldAggVo);
		// 客户端校验
		new AccruedBillVOChecker().checkClientSave(newAggVo);

		// 表体重复行校验
		checkRepeatRow(newAggVo, panel);
		
		//表0字段校验
		checkZeroFieldRow(panel);
	}
	
	/**
	 * 校验为0的表体字段
	 * 
	 * @param panel
	 * @throws NullFieldException
	 */
	private void checkZeroFieldRow(BillCardPanel panel) throws NullFieldException {
		BillItem item = panel.getBodyItem(ErmAccruedBillConst.Accrued_MDCODE_DETAIL, AccruedDetailVO.ORG_CURRINFO);
		BillModel billModel = panel.getBillModel(ErmAccruedBillConst.Accrued_MDCODE_DETAIL);

		if (item == null || billModel == null || billModel.getRowCount() == 0) {
			return;
		}

		StringBuffer message = null;
		for (int i = 0; i < billModel.getRowCount(); i++) {
			StringBuffer rowmessage = new StringBuffer(" ");
			String rowText = NCLangRes.getInstance().getString("_Bill", null, "UPP_Bill-000547", null,
					new String[] { String.valueOf(i + 1) })/*
															 * @res"第{0}行:"
															 */;
			rowmessage.append(rowText);

			UFDouble aValue = (UFDouble) billModel.getValueAt(i, AccruedDetailVO.ORG_CURRINFO);
			StringBuffer errormessage = null;
			if (aValue == null || aValue.compareTo(UFDouble.ZERO_DBL) == 0) {

				errormessage = new StringBuffer();
				String dunhao = nc.ui.ml.NCLangRes.getInstance().getString("_bill", "、", "0_bill0035");// 顿号
				errormessage.append("[");
				errormessage.append(item.getName());
				errormessage.append("]");
				errormessage.append(dunhao);

				billModel.cellShowWarning(i, item.getKey());
			}
			if (errormessage != null) {

				errormessage.deleteCharAt(errormessage.length() - 1);
				rowmessage.append(errormessage);

				if (message == null)
					message = new StringBuffer(rowmessage);
				else
					message.append(rowmessage);
			}
		}

		if (message != null) {
			throw new NullFieldException(message.toString());
		}
	}

	/**
	 * 补充children
	 * 
	 * @param vo
	 * @param oldvo
	 */
	private AggAccruedBillVO fillUpChildren(AggAccruedBillVO vo, AggAccruedBillVO oldvo) {
		AggAccruedBillVO result = new AggAccruedBillVO();

		if (oldvo == null) {
			return (AggAccruedBillVO) vo.clone();
		}

		List<AccruedDetailVO> resultChildren = new ArrayList<AccruedDetailVO>();
		List<String> pkList = new ArrayList<String>();

		AccruedDetailVO[] changedChildren = vo.getChildrenVO();
		AccruedDetailVO[] oldChildren = oldvo.getChildrenVO();

		if (changedChildren != null) {
			for (int i = 0; i < changedChildren.length; i++) {
				if (changedChildren[i].getPk_accrued_detail() != null) {
					pkList.add(changedChildren[i].getPk_accrued_detail());
				}
				resultChildren.add((AccruedDetailVO) changedChildren[i].clone());
			}
		}

		if (oldChildren != null) {
			for (int i = 0; i < oldChildren.length; i++) {
				if (!pkList.contains(oldChildren[i].getPk_accrued_detail())) {
					oldChildren[i].setStatus(VOStatus.UNCHANGED);
					resultChildren.add((AccruedDetailVO) oldChildren[i].clone());
				}
			}
		}

		result.setChildrenVO(resultChildren.toArray(new AccruedDetailVO[] {}));
		result.setParentVO((AccruedVO) vo.getParentVO().clone());

		return result;
	}

	/**
	 * 校验重复行
	 * 
	 * @param aggVo
	 * @param panel
	 * @throws ValidationException
	 */
	private void checkRepeatRow(AggAccruedBillVO aggVo, BillCardPanel panel) throws ValidationException {
		AccruedDetailVO[] childrenVo = aggVo.getChildrenVO();
		if (childrenVo != null && childrenVo.length > 0) {// 不能出现重复行
			String defaultTabcode = aggVo.getTableCodes()[0];
			// List<String> controlKeys = new ArrayList<String>();
			Map<String, List<Integer>> controlRowsMap = new HashMap<String, List<Integer>>();
			StringBuffer controlKey = null;
			String[] attributeNames = childrenVo[0].getAttributeNames();
			for (int i = 0; i < childrenVo.length; i++) {
				if (childrenVo[i].getStatus() != VOStatus.DELETED) {
					controlKey = new StringBuffer();
					for (int j = 0; j < attributeNames.length; j++) {
						BillItem bodyItem = panel.getBodyItem(defaultTabcode, attributeNames[j]);
						if (bodyItem == null || !bodyItem.isShow()) {// 不显示的不校验
							continue;
						}

						if (getNotRepeatFields().contains(attributeNames[j])) {
							controlKey.append(childrenVo[i].getAttributeValue(attributeNames[j]));
						} else if (attributeNames[j].startsWith(BXConstans.BODY_USERDEF_PREFIX)) {
							if (bodyItem != null && bodyItem.getComponent() instanceof UIRefPane
									&& ((UIRefPane) bodyItem.getComponent()).getRefModel() != null) {
								controlKey.append(childrenVo[i].getAttributeValue(attributeNames[j]));
							}
						} else {
							continue;
						}
					}

					String key = controlKey.toString();
					List<Integer> list = controlRowsMap.get(key);
					if (list == null) {
						list = new ArrayList<Integer>();
						controlRowsMap.put(key, list);
					}
					// 行号从1开始
					list.add(i + 1);
				}
			}
			StringBuffer errorMesg = new StringBuffer();
			for (Entry<String, List<Integer>> controlRows : controlRowsMap.entrySet()) {
				List<Integer> list = controlRows.getValue();
				int listsize = list.size();
				if (listsize > 1) {
					// 存在重复行
					for (int i = 0; i < listsize; i++) {
						errorMesg.append("[" + list.get(i) + "]");
						if (i < listsize - 1) {
							errorMesg.append("、");
						}
					}
					errorMesg.append("，");
				}
			}

			if (errorMesg.length() > 0) {
				// 存在重复行，抛出异常

				throw new ValidationException("预提单存在重复行 "+errorMesg.toString()+"请修改");
//				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
//						"0201212-0024", null, new String[] { errorMesg.toString() })/*

			}
		}
	}

	private List<String> getNotRepeatFields() {
		return AggAccruedBillVO.getNotRepeatFields();
	}
}
