package nc.ui.er.djlx.listener;

import nc.ui.er.plugin.IButtonStatListener;
import nc.ui.er.pub.BillWorkPageConst;

public class EdittingStatListener extends BaseListener implements IButtonStatListener {
	public boolean isBtnEnable() {
		// TODO �Զ����ɷ������
		if(getWorkStat()==BillWorkPageConst.WORKSTAT_EDIT||
				getWorkStat()==BillWorkPageConst.WORKSTAT_NEW){
			return true;
		}
		return false;
	}

	public boolean isBtnVisible() {
		// TODO �Զ����ɷ������
		return true;
	}

	public boolean isSubBtn() {
		// TODO �Զ����ɷ������
		return false;
	}

	public String getParentBtnid() {
		// TODO �Զ����ɷ������
		return null;
	}

}
