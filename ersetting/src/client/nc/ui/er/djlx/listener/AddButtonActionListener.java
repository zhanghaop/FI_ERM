package nc.ui.er.djlx.listener;


import nc.bs.logging.Log;
import nc.ui.er.component.ExTreeNode;
import nc.ui.er.djlx.IDjlxModel;
import nc.ui.er.plugin.IButtonActionListener;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.ml.NCLangRes;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/** 增加按钮的处理方法 */
public class AddButtonActionListener extends BaseListener implements IButtonActionListener {

	public boolean actionPerformed() throws BusinessException {
		// TODO 自动生成方法存根
		getMainFrame().showHintMessage(NCLangRes.getInstance().getStrByID("common","UCH028"));
		/**
		 * 从界面中选出用户所选中的单据大类
		 */
		ExTreeNode selectedNode =(ExTreeNode) getUITree().getLastSelectedPathComponent();
		// 首先要从树中检验一下，看用户是否选择了某种单据类型
		if (selectedNode == null) {
			showErrorMessage(NCLangRes.getInstance().getStrByID("20060101","UPP20060101-000034")/*
																								 * @res
																								 * "您必须选择一种单据大类，然后完成增加工作！"
																								 */);
			getMainFrame().showHintMessage("");
			return false;
		}
		getMainFrame().setWorkstat(BillWorkPageConst.WORKSTAT_NEW);
		getBillCardPanel().setEnabled(true);
		setCardEnable(true);
		getUITree().setEnabled(false);
		makeItemNull();
		return true;
	}
	private void makeItemNull() {
		try{
			getBillCardPanel().getHeadItem("djlxoid").setValue(null);
			getBillCardPanel().getHeadItem("djlxjc").setValue(null);
			getBillCardPanel().getHeadItem("djlxmc").setValue(null);
			getBillCardPanel().getHeadItem("djlxjc_remark").setValue(null);
			getBillCardPanel().getHeadItem("djlxmc_remark").setValue(null);
			getBillCardPanel().getHeadItem("djlxbm").setValue(((IDjlxModel)getDataModel()).getbm());
			getBillCardPanel().getHeadItem("fcbz").setValue(UFBoolean.FALSE);
			getBillCardPanel().getHeadItem("isbankrecive").setValue(UFBoolean.FALSE);
			getBillCardPanel().getHeadItem("defcurrency").setValue(null);
			getBillCardPanel().getHeadItem("mjbz").setValue(UFBoolean.TRUE);
			getBillCardPanel().getHeadItem("djmboid").setValue(null);
			//FIXME 增加为集团
//			getBillCardPanel().getHeadItem("dwbm").setValue(BXUiUtil.getDefaultOrgUnit());
			getBillCardPanel().getHeadItem("dwbm").setValue(ErUiUtil.getPK_group());
			getBillCardPanel().getHeadItem("dr").setValue(0);
//			String djdl = (String)getBillCardPanel().getHeadItem("djdl").getValueObject();

		}catch(Exception e){
			Log.getInstance(this.getClass()).error(e.getMessage(),e);
	 		showErrorMessage(e.getMessage());
		}
	}
}
