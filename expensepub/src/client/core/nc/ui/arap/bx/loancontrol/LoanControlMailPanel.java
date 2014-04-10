package nc.ui.arap.bx.loancontrol;

import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.common.CommonCard;
import nc.ui.erm.common.CommonList;
import nc.ui.erm.common.CommonModel;
import nc.ui.erm.common.CommonUI;
import nc.ui.pub.ClientEnvironment;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.LoanControlVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;

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
}