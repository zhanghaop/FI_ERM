package nc.ui.erm.matterapp.common;

import java.util.ArrayList;
import java.util.List;

import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

public class MatterAppClientChecker {
	/**
	 * ǰ̨У��
	 * 
	 * @param aggVo
	 * @throws BusinessException
	 */
	public void checkClientSave(AggMatterAppVO aggVo, AggMatterAppVO oldAggVo, BillCardPanel panel)
			throws BusinessException {
		// ����Ϣ���в��䣬����У�����
		AggMatterAppVO newAggVo = fillUpChildren(aggVo, oldAggVo);
		
		checkExistRows(newAggVo);
		// ���У��
		UFDouble oriAmount = newAggVo.getParentVO().getOrig_amount();
		if (oriAmount == null || oriAmount.compareTo(UFDouble.ZERO_DBL) <= 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0021")/* @res "��¼����ȷ�Ľ��" */);
		}

		for (MtAppDetailVO detail : newAggVo.getChildrenVO()) {
			UFDouble detailAmount = detail.getOrig_amount();
			if (detail.getStatus() != VOStatus.DELETED
					&& (detailAmount == null || detailAmount.compareTo(UFDouble.ZERO_DBL) <= 0)) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0084")/*
										 * @res "���뵥��ϸ���ܰ������С�ڵ���0���У�"
										 */);
			}
		}

		// �Զ��ر�����У��
		UFDate autoCloseDate = newAggVo.getParentVO().getAutoclosedate();

		if (autoCloseDate != null && autoCloseDate.asBegin().compareTo(ErUiUtil.getSysdate().asBegin()) < 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0023")/* @res "�Զ��ر����ڲ������ڵ�ǰ����֮ǰ" */);
		}

		// �����ظ���У��
		checkRepeatRow(newAggVo, panel);
	}
	
	
	/**
	 * ����Ƿ�¼��ҵ����
	 * @param newAggVo
	 * @throws ValidationException 
	 */
	private void checkExistRows(AggMatterAppVO newAggVo) throws BusinessException {
		boolean isExistRows = false;
		if (newAggVo.getChildrenVO() != null && newAggVo.getChildrenVO().length != 0) {
			for (MtAppDetailVO detail : newAggVo.getChildrenVO()) {
				if (detail.getStatus() != VOStatus.DELETED) {
					isExistRows = true;
					break;
				}
			}
		}

		if (!isExistRows) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
					"0201212-0097")/*
									 * @res "���������뵥ҵ���У�"
									 */);
		}

	}
	
	/**
	 * ����children
	 * 
	 * @param vo
	 * @param oldvo
	 */
	private AggMatterAppVO fillUpChildren(AggMatterAppVO vo, AggMatterAppVO oldvo) {
		AggMatterAppVO result = new AggMatterAppVO();

		if (oldvo == null) {
			return (AggMatterAppVO) vo.clone();
		}

		List<MtAppDetailVO> resultChildren = new ArrayList<MtAppDetailVO>();
		List<String> pkList = new ArrayList<String>();

		MtAppDetailVO[] changedChildren = vo.getChildrenVO();
		MtAppDetailVO[] oldChildren = oldvo.getChildrenVO();
		
		if (changedChildren != null) {
			for (int i = 0; i < changedChildren.length; i++) {
				if (changedChildren[i].getPk_mtapp_detail() != null) {
					pkList.add(changedChildren[i].getPk_mtapp_detail());
				}
				resultChildren.add((MtAppDetailVO) changedChildren[i].clone());
			}
		}
		
		if(oldChildren != null){
			for (int i = 0; i < oldChildren.length; i++) {
				if (!pkList.contains(oldChildren[i].getPk_mtapp_detail())) {
					oldChildren[i].setStatus(VOStatus.UNCHANGED);
					resultChildren.add((MtAppDetailVO) oldChildren[i].clone());
				}
			}
		}

		result.setChildrenVO(resultChildren.toArray(new MtAppDetailVO[] {}));
		result.setParentVO((MatterAppVO) vo.getParentVO().clone());

		return result;
	}

	/**
	 * У���ظ���
	 * 
	 * @param aggVo
	 * @param panel
	 * @throws ValidationException
	 */
	private void checkRepeatRow(AggMatterAppVO aggVo, BillCardPanel panel) throws ValidationException {
		MtAppDetailVO[] childrenVo = aggVo.getChildrenVO();
		if (childrenVo != null && childrenVo.length > 0) {// ���ܳ����ظ���
			String defaultTabcode = aggVo.getTableCodes()[0];
			List<String> controlKeys = new ArrayList<String>();
			StringBuffer controlKey = null;
			String[] attributeNames = childrenVo[0].getAttributeNames();
			for (int i = 0; i < childrenVo.length; i++) {
				if (childrenVo[i].getStatus() != VOStatus.DELETED) {
					controlKey = new StringBuffer();
					for (int j = 0; j < attributeNames.length; j++) {
						BillItem bodyItem = panel.getBodyItem(defaultTabcode,attributeNames[j]);
						if (bodyItem == null || !bodyItem.isShow()) {// ����ʾ�Ĳ�У��
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

					if (!controlKeys.contains(controlKey.toString())) {
						controlKeys.add(controlKey.toString());
					} else {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
								"0201212-0024")/* @res "�������뵥��ϸ�����ظ��У�" */);
					}
				}
			}
		}
	}

	private List<String> getNotRepeatFields() {
		return AggMatterAppVO.getApplyOrgBodyIterms();
	}
}