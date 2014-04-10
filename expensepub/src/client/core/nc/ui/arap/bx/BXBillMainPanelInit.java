package nc.ui.arap.bx;

import nc.ui.er.util.BXUiUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.bill.BillRendererVO;


/**
 * @author twei
 *
 * nc.ui.arap.bx.BXBillMainPanelInit
 * 
 * ������ʼ����(���õ���)���
 */
public class BXBillMainPanelInit extends BXBillMainPanelMN {

	private static final long serialVersionUID = 5450143952480541834L;

	public BXBillMainPanelInit() {
		getBxParam().setInit(true);
		initialize();
	}
	
	protected void loadBillListTemplate() {
										
		//FIXME Ŀǰͨ�����ַ����жϽڵ�
		if (getFuncCode().equals(BXConstans.BXINIT_NODECODE_G)){
			
			getBillListPanel().loadTemplet(getNodeCode(), null, getBxParam().getPk_user(), BXUiUtil.getPK_group(), "ERINITG");

		} else{
			
			getBillListPanel().loadTemplet(getNodeCode(), null, getBxParam().getPk_user(), BXUiUtil.getPK_group(), "ERINIT");
		}
		getTempletEventAgent().initBillListPane(getBillListPanel());
	}
	
	@SuppressWarnings("deprecation")
	public nc.ui.pub.bill.BillListPanel getBillListPanel() {
		if (listPanel == null) {
			try {
				listPanel = new BXBillListPanel();
				listPanel.setName("LIST");

				loadBillListTemplate();
				
				BillRendererVO voCell = new BillRendererVO();
				voCell.setShowThMark(true);
				voCell.setShowZeroLikeNull(true);
				listPanel.getChildListPanel().setShowFlags(voCell);
				listPanel.getParentListPanel().setShowFlags(voCell);	
				listPanel.getHeadItem(JKBXHeaderVO.SELECTED).setEnabled(true);
				//6.0�Ķ�����ҪΪtrue����afteredit���¼�������
				listPanel.setEnabled(true);
				setJeFieldDigits(JKBXHeaderVO.YBJE, JKBXHeaderVO.BBJE);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}

		return listPanel;
	}
	
}
