package nc.ui.er.djlx.listener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import nc.bs.logging.Log;
import nc.ui.er.component.ExTreeNode;
import nc.ui.er.plugin.IFrameListener;
import nc.ui.er.plugin.IMainFrame;
import nc.ui.pub.beans.MessageDialog;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.pub.BusinessException;

public class DjlxTreeSelectionListener implements TreeSelectionListener,IFrameListener {

	private IMainFrame m_mf;
	public void valueChanged(TreeSelectionEvent e){
		// TODO 自动生成方法存根
		ExTreeNode node =(ExTreeNode) getMf().getUITree().getLastSelectedPathComponent();
		if(node==null){
			return;
		}
		BillTypeVO vo = (BillTypeVO)node.getExObject();
		
		try {
//			getMf().getDataModel().getData(vo.getDjlxoid());
			getMf().getDataModel().setData(getMf().getDataModel().getData(vo.getDjlxoid()));
		} catch (BusinessException e1) {
			// TODO 自动生成 catch 块
			Log.getInstance(this.getClass()).error(e1.getMessage(),e1);
			MessageDialog.showErrorDlg(null,"",e1.getMessage());
		}

	}

	public void setMainFrame(IMainFrame mf) {
		// TODO 自动生成方法存根
		m_mf = mf;
		
	}

	public IMainFrame getMf() {
		return m_mf;
	}

}
