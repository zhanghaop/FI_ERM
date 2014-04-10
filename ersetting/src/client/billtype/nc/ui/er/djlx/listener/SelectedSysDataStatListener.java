package nc.ui.er.djlx.listener;

import nc.bs.logging.Log;
import nc.ui.er.plugin.IButtonStatListener;
import nc.ui.er.pub.BillWorkPageConst;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.pub.BusinessException;

/**选中系统预制数据或者被系统设置为不可修改的数据时不可使用*/
public class SelectedSysDataStatListener extends BaseListener implements IButtonStatListener {
	public boolean isBtnEnable() {
		// TODO 自动生成方法存根
		if(getWorkStat()==BillWorkPageConst.WORKSTAT_BROWSE){
			BillTypeVO vo = null;
			try {
				vo = (BillTypeVO)getMainFrame().getDataModel().getCurrData();
			} catch (BusinessException e) {
				Log.getInstance(this.getClass()).error(e.getMessage(),e);
			}
			if(isSelectedDetail(vo) && !isSysButton(vo)){
				return true;
			}
		}
		return false;
	}

	public boolean isBtnVisible() {
		// TODO 自动生成方法存根
		return isSysCorp();
//		return true;
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
