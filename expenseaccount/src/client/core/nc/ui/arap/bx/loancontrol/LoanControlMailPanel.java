package nc.ui.arap.bx.loancontrol;

import nc.ui.arap.bx.ButtonUtil;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.common.CommonCard;
import nc.ui.erm.common.CommonList;
import nc.ui.erm.common.CommonModel;
import nc.ui.erm.common.CommonUI;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.components.CommonConfirmDialogUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.ref.IFilterStrategy;
import nc.vo.ep.bx.LoanControlVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;

/**
 * @author twei
 *
 * nc.ui.arap.bx.loancontrol.LoanControlMailPanel
 *
 * 借款控制设置管理界面
 *
 * @see CommonUI
 */
public class LoanControlMailPanel extends CommonUI{

	private static final long serialVersionUID = -594066044695195934L;

	private LoanControlList listPanel ;

	private LoanControlCard cardPanel ;

	public LoanControlMailPanel() {
		super();
	}

	@Override
	public Class getVoClass() {
		return LoanControlVO.class;
	}

	/**
	 * @see  集团节点登陆显示集团设置信息
	 *       业务单元级节点登陆默认显示集团设置信息
	 *       选择业务单元显示对应业务单元设置信息
	 * @author liansg
	 */
	@Override
	public String getWhereStr() {
		String refPK = getListPanel().getRefOrg().getRefPK();
		if(isGroup())
			return " pk_group='" + BXUiUtil.getPK_group() + "' and (pk_org='~' or pk_org is null)";
		else{

			if(refPK!=null){
				return " (pk_org='" + refPK + "' and pk_group='" + BXUiUtil.getPK_group()
				 + "') or ( pk_org='~' " + "and pk_group='" + BXUiUtil.getPK_group() +"') order by pk_org";
			}else{
				if(BXUiUtil.getBXDefaultOrgUnit()!=null){
					return " (pk_org='" + BXUiUtil.getBXDefaultOrgUnit() + "' and pk_group='" + BXUiUtil.getPK_group()
						 + "') or ( pk_org='~'" + "and pk_group='" + BXUiUtil.getPK_group() +"') order by pk_org";
				} else{
					return " pk_org='~' " + "and pk_group='" + BXUiUtil.getPK_group() +"'";
				}
			}

		}
	}

	protected boolean isGroup() {
		return getModuleCode().equals(BXConstans.LOANCTRL_CODE);
	}

	@Override
	public String getTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000052")/*@res "借款控制设置"*/;
	}

	@Override
	public CommonCard getCardPanel() {
		if(cardPanel==null){
			cardPanel=new LoanControlCard();
		}
		return cardPanel;
	}

	@Override
	public CommonList getListPanel() {
		if(listPanel==null){
			listPanel=new LoanControlList();
			listPanel.getRefOrg().getRefModel().setFilterPks(BXUiUtil.getPermissionOrgs(BXConstans.LOANCTRL_ORG),IFilterStrategy.INSECTION);
		}
		return listPanel;
	}

	@Override
	public void updateStatus() {
		if(getModel().getStatus()==CommonModel.STATUS_MOD && getModel().getSelectedvo()!=null){
			if(getModel().getSelectedvo().getAttributeValue("pk_org")==null && getModel().getSelectedvo().getAttributeValue("pk_group")==null ){
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0030")/*@res "不能修改其他单位设置的借款控制内容！"*/);
			}
		}
		if(getModel().getStatus()==CommonModel.STATUS_MOD && getModel().getSelectedvo()!=null){
			if(!isGroup()){
				if(getModel().getSelectedvo().getAttributeValue("pk_org")==null){
					throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0030")/*@res "不能修改其他单位设置的借款控制内容！"*/);
				}
			}
		}

		super.updateStatus();
	}

	@Override
	protected void doBeforeDel() {

		if(getModel().getSelectedvo().getAttributeValue("pk_group") ==null){
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0031")/*@res "不能删除集团设置的借款控制内容！"*/);
		}
		if(getModel().getSelectedvo().getAttributeValue("pk_org")==null && getModel().getSelectedvo().getAttributeValue("pk_group")==null ){
//			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000421")/*@res "不能删除其他单位设置的借款控制内容！"*/);
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0032")/*@res "不能删除其他单位设置的借款控制内容！"*/);
		}
		if(!isGroup()){
			if(getModel().getSelectedvo().getAttributeValue("pk_org")==null){
//			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000421")/*@res "不能删除其他单位设置的借款控制内容！"*/);
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0031")/*@res "不能删除集团设置的借款控制内容！"*/);
			}
		}
	}
	
	@Override
	public boolean onClosing() {
		int currStatus =  getCardPanel().getParentUI().getModel().getStatus();
		if (CommonModel.STATUS_ADD == currStatus || CommonModel.STATUS_MOD  == currStatus) {
			return doClosing();
		} else {
			return true;
		}
	}
	
	private boolean doClosing() {

		int i = CommonConfirmDialogUtils.showConfirmSaveDialog(getParent());
		switch (i) {
		case UIDialog.ID_YES: {
			try {
				saveData();
			} catch (BusinessException e) {
				ErUiUtil.showUif2DetailMessage(this, ButtonUtil.getButtonHintMsg(-1,btnSave), e);
				return false;
			}
			return true;
		}
		case UIDialog.ID_CANCEL: {
			return false;
		}
		case UIDialog.ID_NO:
		default:
			return true;
		}

	}
}