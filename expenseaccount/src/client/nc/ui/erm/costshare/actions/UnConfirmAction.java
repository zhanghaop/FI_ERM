package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.uif2.IActionCode;
import nc.uap.rbac.core.dataperm.DataPermissionFacade;
import nc.ui.erm.costshare.ui.CostShareModelService;
import nc.ui.erm.costshare.ui.CsBillManageModel;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

/**
 * @author luolch
 *
 * 查询活动
 *
 */
@SuppressWarnings("serial")
public class UnConfirmAction extends NCAction {
	private CostShareModelService modelService;
	private BillManageModel model;
	private boolean isCard;
	
	// 以下实现数据权限需要
	private String mdOperateCode = null; // 元数据操作编码
	private String operateCode = null; // 资源对象操作编码，以上两者注入其一，都不注入，则不进行数据权限控制。
	private String resourceCode = null; // 业务实体资源编码
	
	public UnConfirmAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.UNAPPROVE);
	}
	
	public void doAction(ActionEvent e) throws Exception {
		if (!checkDataPermission()) {
			throw new BusinessException(IShowMsgConstant.getDataPermissionInfo());
		}
		Object[] aggCs = ((CsBillManageModel)getModel()).getSelectedOperaDatas();
		//卡片处理方式：不弹出TextArea大文本框，显示UE规范错误
		if(isCard){
			AggCostShareVO aggVo = (AggCostShareVO)getModel().getSelectedData();
			MessageVO[] loginfo = getModelService().unConfirm(new AggCostShareVO[]{aggVo});
			if(loginfo!=null){
				((CsBillManageModel)getModel()).directlyUpdate(loginfo[0].getSuccessVO());
				//ShowStatusBarMsgUtil.showStatusBarMsg(loginfo[0].toString(), getModel().getContext());
				nc.ui.erm.util.ErUiUtil.showBatchResults(getModel().getContext(), loginfo);
			}
		}
		else {
			//没有选择复选框，默认认为光标选中框
			if(aggCs == null){
				aggCs = new AggCostShareVO[]{(AggCostShareVO) getModel().getSelectedData()};
			}
			MessageVO[] mglog = new MessageVO[aggCs.length];
			for (int i = 0; i < aggCs.length; i++) {
				AggCostShareVO aggVo = (AggCostShareVO)aggCs[i];
				CostShareVO csvo = (CostShareVO)aggVo.getParentVO();
				if (csvo.getSrc_type()==IErmCostShareConst.CostShare_Bill_SCRTYPE_BX) {
					mglog[i] = new MessageVO(aggVo,ActionUtils.UNAUDIT);
					mglog[i].setSuccess(false);
					mglog[i].setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012V575_0","0upp2012V575-0117"));
					/*"报销单生成不允许操作!"*/
				}else {
					try {
						mglog[i] = getModelService().unConfirm(new AggCostShareVO[]{aggVo})[0];
					} catch (Exception e1) {
						mglog[i] = new MessageVO(aggVo,ActionUtils.UNAUDIT);
						mglog[i].setSuccess(false);
						mglog[i].setErrorMessage(e1.getMessage());
						nc.bs.logging.Logger.error(e1.getMessage(), e1);
					}
				}
			}
			//更新表格数据
			for (int i = 0; i < mglog.length; i++) {
				if(mglog[i].isSuccess())
					getModel().directlyUpdate(mglog[i].getSuccessVO());
			}
			nc.ui.erm.util.ErUiUtil.showBatchResults(getModel().getContext(), mglog);
		}
	}

	@Override
	protected boolean isActionEnable() {
		Object[] datas = getModel().getSelectedOperaDatas();
		boolean isEnable = false;
		if (datas!=null) {
			for (int i = 0; i < datas.length; i++) {
				 CostShareVO vo = ((CostShareVO)((AggCostShareVO)datas[i]).getParentVO());
				if (vo.getSrc_type()==IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL&&vo.getEffectstate() == IErmCostShareConst.CostShare_Bill_Effectstate_Y) {
					isEnable = true;
					break;
				}
			}
		}
		return model.getUiState()== UIState.NOT_EDIT&&isEnable;
	}
	
	protected boolean checkDataPermission()
	{
		if(StringUtil.isEmptyWithTrim(getOperateCode()) && StringUtil.isEmptyWithTrim(getMdOperateCode()) || StringUtil.isEmptyWithTrim(getResourceCode()))
			return true;
		
		LoginContext context = getModel().getContext();
		String userId = context.getPk_loginUser();
		String pkgroup = context.getPk_group();
		Object data = getModel().getSelectedData();
		boolean hasp = true;
		if(!StringUtil.isEmptyWithTrim(getMdOperateCode()))
			hasp = DataPermissionFacade.isUserHasPermissionByMetaDataOperation(userId, getResourceCode(), getMdOperateCode(), pkgroup,data);
		else
			hasp = DataPermissionFacade.isUserHasPermission(userId, getResourceCode(), getOperateCode(), pkgroup, data);
		return hasp;
	}
	
	public void setModelService(CostShareModelService modelService) {
		this.modelService = modelService;
	}
	
	public CostShareModelService getModelService() {
		return modelService;
	}
	
	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}
	
	public BillManageModel getModel() {
		return model;
	}
	
	public void setCard(boolean isCard) {
		this.isCard = isCard;
	}
	
	public boolean isCard() {
		return isCard;
	}

	public String getMdOperateCode() {
		return mdOperateCode;
	}

	public void setMdOperateCode(String mdOperateCode) {
		this.mdOperateCode = mdOperateCode;
	}

	public String getOperateCode() {
		return operateCode;
	}

	public void setOperateCode(String operateCode) {
		this.operateCode = operateCode;
	}

	public String getResourceCode() {
		return resourceCode;
	}

	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}
}