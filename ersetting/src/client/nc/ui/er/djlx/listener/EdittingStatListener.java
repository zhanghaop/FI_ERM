package nc.ui.er.djlx.listener;

import nc.ui.er.plugin.IButtonStatListener;
import nc.ui.er.pub.BillWorkPageConst;

public class EdittingStatListener extends BaseListener implements IButtonStatListener {
	public boolean isBtnEnable() {
		// TODO 自动生成方法存根
		if(getWorkStat()==BillWorkPageConst.WORKSTAT_EDIT||
				getWorkStat()==BillWorkPageConst.WORKSTAT_NEW){
			return true;
		}
		return false;
	}

	public boolean isBtnVisible() {
		// TODO 自动生成方法存根
		return true;
	}

	public boolean isSubBtn() {
		// TODO 自动生成方法存根
		return false;
	}

	public String getParentBtnid() {
		// TODO 自动生成方法存根
		return null;
	}

}
