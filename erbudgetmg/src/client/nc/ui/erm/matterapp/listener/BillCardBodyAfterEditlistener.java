package nc.ui.erm.matterapp.listener;

import javax.swing.Action;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.ERMDealForJeAndRatioUtil;
import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.erm.matterapp.common.ErmForMatterAppUtil;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.matterapp.view.AbstractMappBillForm;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.uitheme.ui.ThemeResourceCenter;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.uap.rbac.constant.INCSystemUserConst;

import org.apache.commons.lang.ArrayUtils;

/**
 * ��Ƭ����༭��listener
 * @author chenshuaia
 *
 */
public class BillCardBodyAfterEditlistener implements BillEditListener {
	private static final long serialVersionUID = 1L;
	
	private NCAction closeRowAction;
	
	private MAppModel model;

	private MatterAppMNBillForm billForm;
	
	protected IExceptionHandler exceptionHandler;

	@Override
	public void afterEdit(BillEditEvent eve) {
		BillItem bodyItem = billForm.getBillCardPanel().getBodyItem(eve.getTableCode(),eve.getKey());
		
		if(bodyItem==null)
			return;
		String key = bodyItem.getKey();
		if(ErmMatterAppConst.MatterApp_MDCODE_DETAIL.endsWith(eve.getTableCode())){
			// ��ѡ���Զ����и�����ʵ��
			int[] changRow = null;
			if (ArrayUtils.indexOf(AggMatterAppVO.getBodyMultiSelectedItems(), eve.getKey(), 0) >= 0) {
				changRow = ERMDealForJeAndRatioUtil.pasteLineForMultiSelected(eve.getRow(), MtAppDetailVO.ORIG_AMOUNT,
						MatterAppVO.ORIG_AMOUNT, MtAppDetailVO.SHARE_RATIO, eve.getKey(), getBillForm()
								.getBillCardPanel(), MtAppDetailVO.PK_MTAPP_DETAIL);

				// ���ݶ�ѡ�仯���У�ѡ����صĲ����ֶΣ����������
				if (changRow != null && (changRow.length > 1 || eve.getKey().equals(MtAppDetailVO.ASSUME_ORG))) {
					//�������
					ErmForMatterAppUtil.resetOtherJe(changRow, getBillForm().getBillCardPanel());
					
					try {
						//����������
						if(eve.getKey().equals(MtAppDetailVO.ASSUME_ORG)){
							resetBodyRate((String)eve.getOldValue(), changRow);
						}
						
						// ���㱾�ҵ��������
						afterEditAmount(changRow);
						MatterAppUiUtil.setBodyShareRatio(billForm.getBillCardPanel());//��������
					} catch (BusinessException e) {
						ExceptionHandler.consume(e);
					}
				}
				
				//���������༭��
				afterEditBdDoc(eve, changRow);
				
				// �����仯�������ص���
				ErmForMatterAppUtil.resetFieldValue(changRow, getBillForm().getBillCardPanel(), eve.getKey(), eve
						.getOldValue());
			}
			
			if (key.equals(MtAppDetailVO.ORIG_AMOUNT) || isAmoutField(bodyItem)) {// ���仯
				try {
					MatterAppUiUtil.setHeadAmountByBodyAmounts(billForm.getBillCardPanel());//��������ӽ�������ͷ
					// ���㱾�ҵ��������
					MatterAppUiUtil.setBodyShareRatio(billForm.getBillCardPanel());
					afterEditAmount(eve.getRow());
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
				}
				// �������
			} else if (key.equals(MtAppDetailVO.SHARE_RATIO)) {
				// ���������¼�����
				ErmForMatterAppUtil.resetJeByRatio(eve.getRow(), billForm.getBillCardPanel(), false);
				try {
					// ���㱾�ҵ��������
					afterEditAmount(eve.getRow());
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
				}
			} else if(key.equals(MtAppDetailVO.ORG_CURRINFO)
					|| key.equals(MtAppDetailVO.GROUP_CURRINFO)
					|| key.equals(MtAppDetailVO.GLOBAL_CURRINFO)){
				// ��������óе���λ����
				String assume_org = billForm.getBodyItemStrValue(eve.getRow(), MtAppDetailVO.ASSUME_ORG);
				String pk_currtype = billForm.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);
				
				boolean isEnable = false;
				if(assume_org != null){
					if(MatterAppVO.ORG_CURRINFO.equals(key)){
						isEnable = MatterAppUiUtil.getOrgRateEnableStatus(assume_org, pk_currtype);
					}else if(MatterAppVO.GROUP_CURRINFO.equals(key)){
						isEnable = MatterAppUiUtil.getGroupRateEnableStatus(assume_org, pk_currtype);
					}else if(MatterAppVO.GLOBAL_CURRINFO.equals(key)){
						isEnable = MatterAppUiUtil.getGlobalRateEnableStatus(assume_org, pk_currtype);
					}
				}
				
				if(!isEnable){//���ɱ༭���룬�����ǵ������
					billForm.setBodyValue(eve.getOldValue(), eve.getRow(), key);
				}
				billForm.resetCardBodyAmount(eve.getRow());
			}
		}
		
		// �¼�ת�����ҷ����¼� 
		billForm.getEventTransformer().afterEdit(eve);
	}
	
	//�л�����������
	private void afterEditBdDoc(BillEditEvent eve, int[] changRow) {
		BillItem bodyItem = billForm.getBillCardPanel().getBodyItem(eve.getTableCode(),eve.getKey());
		if (bodyItem.getKey().equals(MtAppDetailVO.ASSUME_DEPT)) {//���óе�����
			for (int row : changRow) {
				ErmForMatterAppUtil.setCostCenter(row, billForm.getBillCardPanel());
			}
		}
	}

	private void afterEditAmount(int... changRow) throws BusinessException {
		billForm.resetHeadAmounts();
		MAppModel model = (MAppModel)getModel();
		DjLXVO djlxVo = model.getTradeTypeVo(model.getDjlxbm());
		
		for (int i = 0; i < changRow.length; i++) {
			billForm.resetCardBodyAmount(changRow[i]);
			billForm.resetBodyMaxAmount(changRow[i], djlxVo);
		}
		
		MatterAppUiUtil.fillLastRowAmount(billForm.getBillCardPanel());
	}

	private void resetBodyRate(String oldPk_org, int... changRow) throws BusinessException {
		for (int i = 0; i < changRow.length; i++) {
			String assume_org = billForm.getBodyItemStrValue(changRow[i], MtAppDetailVO.ASSUME_ORG);
			if (oldPk_org == null || assume_org == null || !assume_org.equals(oldPk_org)) {
				billForm.resetCardBodyRate(changRow[i]);
			}
		}
	}
	
	@Override
	public void bodyRowChange(BillEditEvent e) {
		updateLineCloseActionStatus();
		// �¼�ת�����ҷ����¼� 
		billForm.getEventTransformer().bodyRowChange(e);
	}
	
	/**
	 * ���·��������йرհ�ť״̬
	 */
	private void updateLineCloseActionStatus() {
		
		if(getModel().getUiState() != UIState.NOT_EDIT){
			return;
		}
		
		//����ͼ��
		int[] rows = billForm.getBillCardPanel().getBodyPanel().getTable().getSelectedRows();
		if (rows == null || rows.length <= 0) {
			return;
		}
		
		AggMatterAppVO matterAppVo = (AggMatterAppVO)getModel().getSelectedData();
		
		if(matterAppVo == null){
			getCloseRowAction().setEnabled(false);
			return;
		}
		
		MtAppDetailVO children = (MtAppDetailVO)billForm.getBillCardPanel().getBillModel().getBodyValueRowVO(
				rows[0], MtAppDetailVO.class.getName());
		if(children == null){
			return;
		}

		// ֻ����Ч�ĵ���,����������ſ���
		if (matterAppVo.getParentVO().getEffectstatus() < ErmMatterAppConst.EFFECTSTATUS_VALID
				|| ((AbstractMappBillForm) billForm).getLink_type() != -1
				|| (children.getCloseman() != null &&  children.getCloseman().equals(INCSystemUserConst.NC_USER_PK))) {
			getCloseRowAction().setEnabled(false);
		}else{
			getCloseRowAction().setEnabled(true);
		}
		
		if (children.getClose_status() == null || children.getClose_status() == ErmMatterAppConst.CLOSESTATUS_N) {
			getCloseRowAction().putValue(Action.SHORT_DESCRIPTION, ErmActionConst.getCloseLineName());
			getCloseRowAction().putValue(Action.SMALL_ICON,
					ThemeResourceCenter.getInstance().getImage(ErmMatterAppConst.TOOLBARICONS_CLOSE_PNG));
		} else {
			getCloseRowAction().putValue(Action.SHORT_DESCRIPTION, ErmActionConst.getOpenLineName());
			getCloseRowAction().putValue(Action.SMALL_ICON,
					ThemeResourceCenter.getInstance().getImage(ErmMatterAppConst.TOOLBARICONS_OPEN_PNG));
		}
	}
	
	/**
	 * �ж��Զ������Ƿ����ù�ʽ�ı����ֶ�
	 * @param bodyItem
	 * @return
	 */
	private boolean isAmoutField(BillItem bodyItem) {
		String[] editFormulas = bodyItem.getEditFormulas();
		if(editFormulas==null){
			return false;
		}
		for(String formula:editFormulas){
			if(formula.indexOf(MtAppDetailVO.ORIG_AMOUNT)!=-1){
				return true;
			}
		}
		return false;
	}

	public NCAction getCloseRowAction() {
		return closeRowAction;
	}

	public void setCloseRowAction(NCAction closeRowAction) {
		this.closeRowAction = closeRowAction;
	}

	public MAppModel getModel() {
		return model;
	}

	public void setModel(MAppModel model) {
		this.model = model;
	}

	public MatterAppMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(MatterAppMNBillForm billForm) {
		this.billForm = billForm;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
}
