package nc.ui.er.djlx.listener;

import nc.bs.logging.Log;
import nc.ui.er.plugin.IButtonStatListener;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.pub.BusinessException;


public class AddButtonStatListener extends BrowseStatListener implements IButtonStatListener {

	public boolean isBtnVisible() {
		// TODO 自动生成方法存根
		if(isSysCorp()){
			return true;
		}
		return false;
	}

	public boolean isSubBtn() {
		// TODO 自动生成方法存根
		return false;
	}

	public String getParentBtnid() {
		// TODO 自动生成方法存根
		return null;
	}
	public boolean isBtnEnable(){
		BillTypeVO vo = null;
		try {
			vo = (BillTypeVO)getMainFrame().getDataModel().getCurrData();
		} catch (BusinessException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(),e);
		}
		if(vo!=null && (vo.getDjdl().equals("ts") || "DR".equals(vo.getDjlxbm()))){
			return false;
		}else{
			return super.isBtnEnable();
		}
		
	}

}
