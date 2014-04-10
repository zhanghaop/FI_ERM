package nc.ui.er.reimrule.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.IActionCode;
import nc.itf.er.reimtype.IReimTypeService;
import nc.ui.er.reimrule.CopyDialog;
import nc.ui.er.reimrule.ReimRuleUtil;
import nc.ui.er.reimrule.view.DimensTable;
import nc.ui.er.reimrule.view.RuleTable;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;


/**
 * 按钮编辑的动作
 * 
 * @author shiwla
 *
 */
public class CopyAction extends NCAction {

	private static final long serialVersionUID = -2886050746728516317L;

	private AbstractUIAppModel model = null;
	private BatchBillTable editor = null;
	
	private CopyDialog dialog = null;
	
	public CopyAction() {
		ActionInitializer.initializeAction(this, IActionCode.COPY);
		setBtnName("快速复制");
	}
	
	public CopyDialog getCopyDialog() {
		if(dialog == null)
			dialog = new CopyDialog(getEditor());
		return dialog;
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		if(getEditor() instanceof RuleTable){
			doRuleCopy();
		}
		else if(getEditor() instanceof DimensTable){
			doDimCopy();
		}
	}
	
	private void doRuleCopy() {
		String olddjlx = null;
		if (getModel().getSelectedData() != null) {
			olddjlx = ((DjLXVO) getModel().getSelectedData()).getDjlxbm();
		}
		if(olddjlx == null)
			return;
		if (getCopyDialog().showModal() == UIDialog.ID_OK) {
			String neworg = getCopyDialog().getCorpRef().getRefPK();
			String newdjlx = getCopyDialog().getDjlxRef().getRefCode();
			int ret = MessageDialog.showYesNoDlg(getEditor(), null, nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("ersetting_0", "02011001-0021")/*
																			 * @res
																			 * "您确定要把当前组织和交易类型设置的标准复制到指定组织和交易类型吗？这样会丢失目的组织和交易类型原有的报销标准数据！"
																			 */);
			if (ret != UIDialog.ID_YES) {
				return;
			}
			//若指定公司为空，则默认为集团级标准
			if(neworg == null)
				neworg = ReimRulerVO.PKORG;
			String oldorg = getModel().getContext().getPk_org();
			String currentBodyTableCode = getEditor().getBillCardPanel()
					.getCurrentBodyTableCode();
			ReimRulerVO[] reimRuleVos = (ReimRulerVO[]) getEditor().getBillCardPanel()
					.getBillData().getBodyValueVOs(currentBodyTableCode,
							ReimRulerVO.class.getName());
			if (reimRuleVos == null || reimRuleVos.length == 0) {
				showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000493")/*
															 * @res
															 * "选中的报销标准中没有具体的值,复制已取消!"
															 */);
				return;
			}
			int count = 0;
			if (newdjlx.equals(olddjlx) && oldorg.equals(neworg)){
				showErrorMessage("同公司同交易类型不进行复制,复制已取消!");
				return;
			}
			try {
				//首先复制标准维度
				List<SuperVO> reimDimVos = ReimRuleUtil.getDataMapDim().get(olddjlx);
				for(SuperVO dimvo:reimDimVos){
					((ReimRuleDimVO)dimvo).setPk_billtype(newdjlx);
					((ReimRuleDimVO)dimvo).setPk_org(neworg);
				}
				List<ReimRuleDimVO> resultVos = NCLocator.getInstance().lookup(
						IReimTypeService.class).saveReimDim(newdjlx, getModel().getContext().getPk_group(),neworg,
								reimDimVos.toArray(new ReimRuleDimVO[0]));
				if (neworg.equals(oldorg)) { // 如果是同公司的复制，需要同时更新datamapdim
					List<SuperVO> list = new ArrayList<SuperVO>();
					list.addAll(resultVos);
					ReimRuleUtil.putDim(newdjlx, list);
				}
				for (ReimRulerVO vo : reimRuleVos) {
					vo.setPk_billtype(newdjlx);
					vo.setPk_group(getModel().getContext().getPk_group());
					vo.setPk_org(neworg);
					vo.setPriority(count);
					count++;
				}
				List<ReimRulerVO> returnVos = NCLocator.getInstance()
						.lookup(IReimTypeService.class).saveReimRule(
								newdjlx,getModel().getContext().getPk_group(),neworg,reimRuleVos); // 直接进行保存的动作
				if (neworg.equals(oldorg)) { // 如果是同公司的复制，需要同时更新datamaprule
					List<SuperVO> list = new ArrayList<SuperVO>();
					list.addAll(returnVos);
					ReimRuleUtil.putRule(newdjlx, list);
				}
			} catch (BusinessException e) {
				showErrorMessage(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011",
								"UPP2011-000494")/* @res "复制失败：" */
						+ e.getMessage());
			}
			showWarningMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011", "UPP2011-000495",null,new String[]{String.valueOf(count)})/* @res "复制成功,i条记录已复制!" */);
		}
	}
	
	private void doDimCopy() {
		String str = ((DimensTable)getEditor()).getOrgPanel().getRefPane().getUITextField().getValue().toString();
		if(str == null)
			return;
		String[] strarray = str.split(";");
		if(strarray.length<2)
			return;
		String olddjlx = strarray[0];
		if(olddjlx == null)
			return;
		if (getCopyDialog().showModal() == UIDialog.ID_OK) {
			String neworg = getCopyDialog().getCorpRef().getRefPK();
			String newdjlx = getCopyDialog().getDjlxRef().getRefCode();
			int ret = MessageDialog.showYesNoDlg(getEditor(), null, nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("ersetting_0", "02011001-0021")/*
																			 * @res
																			 * "您确定要把当前组织和交易类型设置的标准复制到指定组织和交易类型吗？这样会丢失目的组织和交易类型原有的报销标准数据！"
																			 */);
			if (ret != UIDialog.ID_YES) {
				return;
			}
			//若指定公司为空，则默认为集团级标准
			if(neworg == null)
				neworg="~";
			String oldorg = strarray[1];
			String currentBodyTableCode = getEditor().getBillCardPanel()
					.getCurrentBodyTableCode();
			ReimRuleDimVO[] reimDimVos = (ReimRuleDimVO[]) getEditor().getBillCardPanel()
					.getBillData().getBodyValueVOs(currentBodyTableCode,
							ReimRuleDimVO.class.getName());
			if (reimDimVos == null || reimDimVos.length == 0) {
				showErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000493")/*
															 * @res
															 * "选中的报销标准中没有具体的值,复制已取消!"
															 */);
				return;
			}
			int count = reimDimVos.length;
			if (newdjlx.equals(olddjlx) && oldorg.equals(neworg)){
				showErrorMessage("同公司同交易类型不进行复制,复制已取消!");
				return;
			}
			try {
				// 检查维度值，并加入单据类型、组织与对应项
				ReimRuleUtil.checkReimDims(reimDimVos,newdjlx,getModel().getContext().getPk_group(),neworg);
				List<ReimRuleDimVO> resultVos = NCLocator.getInstance().lookup(
						IReimTypeService.class).saveReimDim(newdjlx, getModel().getContext().getPk_group(),neworg,
								reimDimVos);
				if (neworg.equals(oldorg)) { // 如果是同公司的复制，需要同时更新datamapdim
					List<SuperVO> list = new ArrayList<SuperVO>();
					list.addAll(resultVos);
					ReimRuleUtil.putDim(newdjlx, list);
				}
	
			} catch (BusinessException e) {
				showErrorMessage(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("2011",
								"UPP2011-000494")/* @res "复制失败：" */
						+ e.getMessage());
			}
			showWarningMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011", "UPP2011-000495",null,new String[]{String.valueOf(count)})/* @res "复制成功,i条记录已复制!" */);
		}
	}

	@Override
	protected boolean isActionEnable() {
		return true;
	}
	
	public AbstractUIAppModel getModel() {
		return model;
	}
 
	public void setModel(AbstractUIAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public void showErrorMessage(String err) {
	        //显示错误对话框
	        MessageDialog.showErrorDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000019")/* @res "错误" */, err);
	}
	 
	 public void showWarningMessage(String msg) {
	        MessageDialog.showWarningDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/* @res "警告" */, msg);
	 }
	 
	 public BatchBillTable getEditor() {
			return editor;
		}

		public void setEditor(BatchBillTable editor) {
			this.editor = editor;
		}
}
