package nc.ui.erm.billinit.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.ml.NCLangRes;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.actions.RefreshSingleAction;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

public class RefreshSingleInitAction extends RefreshSingleAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object obj=model.getSelectedData();
		if(obj!=null){
			 if(obj instanceof JKBXVO){ 
				 JKBXHeaderVO parentvo = ((JKBXVO) obj).getParentVO();
				if(parentvo != null)
				{
					List<JKBXVO> ncobjs = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryVOsByPrimaryKeysForNewNode(new String[]{parentvo.getPrimaryKey()},parentvo.getDjdl(),parentvo.isInit(),((ErmBillBillManageModel)getModel()).getDjCondVO());
					if (ncobjs != null && ncobjs.get(0) != null) {
						model.directlyUpdate(ncobjs.get(0));
					}
					else {
						// 数据已经被删除
						throw new BusinessException(NCLangRes.getInstance().getStrByID("uif2", "RefreshSingleAction-000000")/*数据已经被删除，请返回列表界面！*/);
					}
				}
			}
			
			ShowStatusBarMsgUtil.showStatusBarMsg(NCLangRes.getInstance().getStrByID("common", "UCH007")/*"刷新成功！"*/,
					model.getContext());
		}
	}
}
