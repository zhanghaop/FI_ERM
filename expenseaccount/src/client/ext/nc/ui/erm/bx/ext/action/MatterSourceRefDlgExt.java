package nc.ui.erm.bx.ext.action;

import java.util.Arrays;
import java.util.Map;

import nc.ui.erm.billpub.view.MatterSourceRefDlg;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillListPanel;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.uif2.LoginContext;

/**
 * �����������ڣ���չӦ��
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 *
 */
public class MatterSourceRefDlgExt  extends MatterSourceRefDlg{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MatterSourceRefDlgExt(LoginContext loginContext) {
		super(loginContext);
	}
	
	private BillListPanel extpanel;
	private Map<String, AggMatterAppVO> appvoMap = null;
	
	/* (non-Javadoc)
	 * @see nc.ui.erm.billpub.view.MatterSourceRefDlg#getBillListPanel()
	 */
	public BillListPanel getBillListPanel() {
		if(extpanel == null){
			extpanel = super.getBillListPanel();
			extpanel.setChildMultiSelect(false);
			extpanel.addHeadEditListener(new BillEditListener() {
				
				@Override
				public void bodyRowChange(BillEditEvent e) {
					// ѡ���ͷһ�к󣬽���������ݴ��������������ñ���ȫѡ��
					String pk_mtapp_bill = (String)extpanel.getHeadBillModel().getValueAt(e.getRow(), MatterAppVO.PK_MTAPP_BILL);
					extpanel.setBodyValueVO(appvoMap.get(pk_mtapp_bill).getChildrenVO());
					// ���ع�ʽ����ʾ��������
					extpanel.getBodyBillModel().loadLoadRelationItemValue();
					extpanel.getBodyBillModel().execLoadFormula();
					getBillListPanel().updateUI();
					// ����ȷ�ϰ�ť����
					setSureBtnEnable(true);
				}
				
				@Override
				public void afterEdit(BillEditEvent e) {
					
				}
			});
		}
		
		return extpanel;
	}
	
	/* (non-Javadoc)
	 * @see nc.ui.erm.billpub.view.MatterSourceRefDlg#setAggMtappVOS(nc.vo.erm.matterapp.AggMatterAppVO[])
	 */
	public void setAggMtappVOS(AggMatterAppVO[] aggMtappvos) {
		super.setAggMtappVOS(aggMtappvos);
		// ��ϣ��aggvo����
		if(aggMtappvos != null && aggMtappvos.length >0){
			appvoMap = VOUtils.changeCollection2Map(Arrays.asList(aggMtappvos));
		}
	}
	
	/* (non-Javadoc)
	 * @see nc.ui.erm.billpub.view.MatterSourceRefDlg#getRetvo()
	 */
	public AggMatterAppVO getRetvo() {
		// ֱ�ӷ��ص�ǰ���뵥�����ۺ�vo
		AggMatterAppVO retvo = null;
		int headRow = getBillListPanel().getHeadTable().getSelectedRow();
		if (headRow > -1) {
			String pk_mtapp_bill = (String) getBillListPanel().getHeadBillModel().getValueAt(headRow,MatterAppVO.PK_MTAPP_BILL);
			retvo = appvoMap.get(pk_mtapp_bill);
		}
		return retvo;
	}
}

