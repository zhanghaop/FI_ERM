package nc.ui.er.djlx.listener;

import nc.bs.logging.Log;
import nc.ui.er.plugin.IButtonStatListener;
import nc.ui.er.pub.BillWorkPageConst;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.pub.BusinessException;

/**ѡ��ϵͳԤ�����ݻ��߱�ϵͳ����Ϊ�����޸ĵ�����ʱ����ʹ��*/
public class SelectedSysDataStatListener extends BaseListener implements IButtonStatListener {
	public boolean isBtnEnable() {
		// TODO �Զ����ɷ������
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
		// TODO �Զ����ɷ������
		return isSysCorp();
//		return true;
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
