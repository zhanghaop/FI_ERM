package nc.ui.arap.bx.btnstatus;

import nc.ui.arap.engine.ExtButtonObject;
import nc.ui.arap.engine.IActionRuntime;
import nc.ui.arap.engine.IButtonStatus;
import nc.ui.er.pub.BillWorkPageConst;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.pf.IPfRetCheckInfo;

public class BxApproveBtnStatusListener extends BXDefaultBtnStatusListener
		implements IButtonStatus {

	@Override
	public void updateButtonStatus(ExtButtonObject bo, IActionRuntime runtime) {

		setActionRunntimeV0(runtime);

		if (bo == null || runtime == null) {
			return;
		}

		dealForLinkMode(bo, runtime);

		// ��˰�ť�Ƿ����
		boolean isApproveBtnEnable = true;

		// ����˰�ť�Ƿ����
		boolean isUnApproveBtnEnable = true;

		// �������
		if (getActionRunntimeV0().getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_BROWSE) {
			if (isList()) {
				// �б����
				JKBXVO[] vos = getSelBxvos();
				if (vos == null) {
					isApproveBtnEnable = false;
					isUnApproveBtnEnable = false;
				}
				//���ڶ�ѡ�Ļ� �Ĵ��� 
				Boolean[] isUnApproveBtnEnables=new Boolean[vos.length];
				Boolean[] isApproveBtnEnables=new Boolean[vos.length];
				for(int i=0 ;i<vos.length;i++) {
					// �Ϳ�Ƭ������ͬ����
					isApproveBtnEnable = isApproveBtnEnable(vos[i]);
					isUnApproveBtnEnable = isUnApproveBtnEnable(vos[i]);
					//�����Ƿ�ȡ��������ť������
					isUnApproveBtnEnables[i]=isUnApproveBtnEnable;
					isApproveBtnEnables[i]=isApproveBtnEnable;
					
				}
				for(int j=0;j<isUnApproveBtnEnables.length;j++){
					if(isUnApproveBtnEnables[j]==true){
						isUnApproveBtnEnable=true;
						break;
					}
					else{
						isUnApproveBtnEnable=false;
					}
				}
				for(int j=0;j<isApproveBtnEnables.length;j++){
					if(isApproveBtnEnables[j]==true){
						isApproveBtnEnable=true;
						break;
					}
					else{
						isApproveBtnEnable=false;
					}
				}				
			} else {
				// ��Ƭ����
				isApproveBtnEnable = isApproveBtnEnable(getCurrentSelectedVO());
				isUnApproveBtnEnable = isUnApproveBtnEnable(getCurrentSelectedVO());
			}
		}
		if (bo.getBtninfo().getBtncode().equals("Approve")) {
			bo.setEnabled(isApproveBtnEnable);
		}
		if (bo.getBtninfo().getBtncode().equals("UnApprove")) {
			bo.setEnabled(isUnApproveBtnEnable);
		}
	}

	/**
	 * ������ť�Ƿ����
	 * 
	 * @param vo
	 * @return
	 */
	private boolean isApproveBtnEnable(JKBXVO vo) {
		//�����У� ��˰�ť����
		if(vo==null){
			return true;
		}
		if(vo.getParentVO().getSpzt()!=null&&vo.getParentVO().getSpzt()==IPfRetCheckInfo.GOINGON){
			return true;
		}
		// �����Ѿ����ͨ���ˣ���˰�ť������
		if (vo.getParentVO().getDjzt() > BXStatusConst.DJZT_Saved) {
			return false;
		}
		return true;
	}

	/**
	 * ����˰�ť�Ƿ����
	 * 
	 * @param vo
	 * @return
	 */
	private boolean isUnApproveBtnEnable(JKBXVO vo) {
		
		if(vo==null){
			return true;
		}

		//�����У� ����˰�ť����
		if(vo.getParentVO().getSpzt()!=null&&vo.getParentVO().getSpzt()==IPfRetCheckInfo.GOINGON){
			return true;
		}
		
		//������Ϊ��ˣ�����˲�����
		if (vo.getParentVO().getDjzt() < BXStatusConst.DJZT_Verified) {
			return false;
		}
		
		// �����Ѿ����ͨ���ˣ�����˰�ť����
		if (vo.getParentVO().getDjzt() > BXStatusConst.DJZT_Saved) {
			return true;
		}
		return true;
	}
}
