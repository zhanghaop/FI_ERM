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

		// 审核按钮是否可用
		boolean isApproveBtnEnable = true;

		// 反审核按钮是否可用
		boolean isUnApproveBtnEnable = true;

		// 浏览界面
		if (getActionRunntimeV0().getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_BROWSE) {
			if (isList()) {
				// 列表界面
				JKBXVO[] vos = getSelBxvos();
				if (vos == null) {
					isApproveBtnEnable = false;
					isUnApproveBtnEnable = false;
				}
				//对于多选的话 的处理 
				Boolean[] isUnApproveBtnEnables=new Boolean[vos.length];
				Boolean[] isApproveBtnEnables=new Boolean[vos.length];
				for(int i=0 ;i<vos.length;i++) {
					// 和卡片界面相同处理
					isApproveBtnEnable = isApproveBtnEnable(vos[i]);
					isUnApproveBtnEnable = isUnApproveBtnEnable(vos[i]);
					//保存是否取消审批按钮的数据
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
				// 卡片界面
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
	 * 审批按钮是否可用
	 * 
	 * @param vo
	 * @return
	 */
	private boolean isApproveBtnEnable(JKBXVO vo) {
		//审批中， 审核按钮可用
		if(vo==null){
			return true;
		}
		if(vo.getParentVO().getSpzt()!=null&&vo.getParentVO().getSpzt()==IPfRetCheckInfo.GOINGON){
			return true;
		}
		// 单据已经审核通过了，审核按钮不可用
		if (vo.getParentVO().getDjzt() > BXStatusConst.DJZT_Saved) {
			return false;
		}
		return true;
	}

	/**
	 * 反审核按钮是否可用
	 * 
	 * @param vo
	 * @return
	 */
	private boolean isUnApproveBtnEnable(JKBXVO vo) {
		
		if(vo==null){
			return true;
		}

		//审批中， 反审核按钮可用
		if(vo.getParentVO().getSpzt()!=null&&vo.getParentVO().getSpzt()==IPfRetCheckInfo.GOINGON){
			return true;
		}
		
		//单据尚为审核，反审核不可用
		if (vo.getParentVO().getDjzt() < BXStatusConst.DJZT_Verified) {
			return false;
		}
		
		// 单据已经审核通过了，反审核按钮可用
		if (vo.getParentVO().getDjzt() > BXStatusConst.DJZT_Saved) {
			return true;
		}
		return true;
	}
}
