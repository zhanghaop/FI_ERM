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
 * ���������ù������
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
	 * @see  ���Žڵ��½��ʾ����������Ϣ
	 *       ҵ��Ԫ���ڵ��½Ĭ����ʾ����������Ϣ
	 *       ѡ��ҵ��Ԫ��ʾ��Ӧҵ��Ԫ������Ϣ
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
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000052")/*@res "����������"*/;
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
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0030")/*@res "�����޸�������λ���õĽ��������ݣ�"*/);
			}
		}
		if(getModel().getStatus()==CommonModel.STATUS_MOD && getModel().getSelectedvo()!=null){
			if(!isGroup()){
				if(getModel().getSelectedvo().getAttributeValue("pk_org")==null){
					throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0030")/*@res "�����޸�������λ���õĽ��������ݣ�"*/);
				}
			}
		}

		super.updateStatus();
	}

	@Override
	protected void doBeforeDel() {

		if(getModel().getSelectedvo().getAttributeValue("pk_group") ==null){
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0031")/*@res "����ɾ���������õĽ��������ݣ�"*/);
		}
		if(getModel().getSelectedvo().getAttributeValue("pk_org")==null && getModel().getSelectedvo().getAttributeValue("pk_group")==null ){
//			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000421")/*@res "����ɾ��������λ���õĽ��������ݣ�"*/);
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0032")/*@res "����ɾ��������λ���õĽ��������ݣ�"*/);
		}
		if(!isGroup()){
			if(getModel().getSelectedvo().getAttributeValue("pk_org")==null){
//			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000421")/*@res "����ɾ��������λ���õĽ��������ݣ�"*/);
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0031")/*@res "����ɾ���������õĽ��������ݣ�"*/);
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