package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.erm.ext.common.ErmConstExt;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.arap.pub.IErmBillUIPublic;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.arap.bx.BXQryTplUtil;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.remote.RoleVoCall;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.billpub.view.MatterSourceRefDlg;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.IBillItem;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.uif2.actions.AddAction;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppConvResVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.querytemplate.TemplateInfo;

public class AddFromMtappAction  extends AddAction{
	private static final long serialVersionUID = 1L;
	private BillForm editor;
	private QueryConditionDLG queryDialog;
	private MatterSourceRefDlg maSourceDlg;
	
	public AddFromMtappAction(){
		setCode("AddFromMtapp");
	    setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0142")/*@res "费用申请单"*/);
	    this.putValue(Action.ACCELERATOR_KEY, null);// 将此按钮的快捷键置为空，防止和父类的快捷键重复
	    this.putValue(Action.SHORT_DESCRIPTION, null);
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		checkAddFromMtapp();
		
		if (UIDialog.ID_OK == getQryDlg().showModal()) {
			UIRefPane orgRefPane = BXQryTplUtil.getRefPaneByFieldCode(getQryDlg(), MatterAppVO.PK_ORG);
			String pk_org = ((String[]) orgRefPane.getValueObj())[0];
			String djlxbm = ((ErmBillBillManageModel)getModel()).getCurrentBillTypeCode();
			String pk_psndoc = BXUiUtil.getPk_psndoc();
			if(ErmConstExt.Pay_JK_Tradetype.equals(djlxbm)){
				// 一般市场费用支付单的借款单不做人员过滤
				pk_psndoc = null;
			}
			String rolerSql = null;
			if (WorkbenchEnvironment.getInstance().getClientCache(RoleVoCall.PK_ROLE_IN_SQL_BUSI + BXUiUtil.getPK_group()) != null) {
				rolerSql = (String) WorkbenchEnvironment.getInstance().getClientCache(RoleVoCall.PK_ROLE_IN_SQL_BUSI + BXUiUtil.getPK_group());
			}
			// 根据人员过滤，普通借款及报销只能拉本人做的申请单
			AggMatterAppVO[] aggMattervos = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class)
					.queryBillFromMtappByPsn(getQryDlg().getWhereSQL(), djlxbm, pk_org,pk_psndoc,rolerSql);
		
			
			
			getMaSourceDlg().setAggMtappVOS(aggMattervos);
			
			if (getMaSourceDlg().showModal() == UIDialog.ID_OK) {
				AggMatterAppVO retvo = getMaSourceDlg().getRetvo();
				if ( retvo != null && retvo.getChildrenVO() != null && retvo.getChildrenVO().length > 0) {
//					String pk_group = BXUiUtil.getPK_group();

					MatterAppConvResVO resVO = convertAggMattappVO(pk_org,  retvo);
					((JKBXVO) resVO.getBusiobj()).setMaheadvo(retvo.getParentVO());
//					
//					JKBXHeaderVO parentVO = vo.getParentVO();
//					vo.getParentVO().setBzbm(pk_currtype);
//					vo.getParentVO().setPk_org(pk_org);
//					vo.getParentVO().setPk_group(pk_group);
//					if(retvo.getParentVO().getIscostshare() == null || !retvo.getParentVO().getIscostshare().booleanValue()){
//						// 申请单非分摊情况时，不向分摊页签转换数据
//						parentVO.setIscostshare(UFBoolean.FALSE);
//						vo.setcShareDetailVo(null);
//					}
//					
					((ErmBillBillForm) getEditor()).setResVO(resVO);
					super.doAction(e);

				}
			}
		}

	}
	
	private void checkAddFromMtapp() throws BusinessException{
		//校验当前用户
		checkUser();
		//校验封存
		checkBillType();
		//校验表体模板
		checkBodyTemplate();
		
	}
	private void checkUser() throws BusinessException {
		String pkPsn = BXUiUtil.getPk_psndoc();
		if (pkPsn == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"2011ermpub0316_0", "02011ermpub0316-0000")/*
																 * * @res*
																 * "当前用户未关联人员，请联系管理人员为此用户指定身份"
																 */);
		}
	}
	
	
	private void checkBodyTemplate() throws BusinessException {
		BillTabVO[] billTabVOs = getEditor().getBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
		boolean isEist = true;
		if (billTabVOs != null && billTabVOs.length > 0) {
			if (billTabVOs[0].getMetadatapath() != null
					&& !billTabVOs[0].getMetadatapath().equals(BXConstans.ER_BUSITEM)
					&& !billTabVOs[0].getMetadatapath().equals(BXConstans.JK_BUSITEM)) {
				isEist = false;
			}
		}else{
			isEist = false;
		}
		
		if(!isEist){
			
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0171")/**
					 * @res
					 *      * "当前单据模板没有表体业务信息，不允许拉单！"
					 */);
		}
	}
	private void checkBillType() throws BusinessException {
		String selectBillTypeCode = ((ErmBillBillManageModel) getModel()).getSelectBillTypeCode();
		DjLXVO currentDjlx = ((ErmBillBillManageModel) getModel()).getCurrentDjlx(selectBillTypeCode);
		if (currentDjlx.getFcbz().booleanValue()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000171")/**
			 * @res
			 *      * "该节点单据类型已被封存，不可操作节点！"
			 */
			);
		}
	}
	
	/**
	 * 申请单拉单转换为下游单据
	 * 
	 * @param pk_org
	 * @param pk_group
	 * @param retvo
	 * @return
	 * @throws BusinessException
	 */
	protected MatterAppConvResVO convertAggMattappVO(String pk_org, AggMatterAppVO retvo) throws BusinessException {
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel) getModel()).getCurrentDjLXVO();

		IErmBillUIPublic service = NCLocator.getInstance().lookup(IErmBillUIPublic.class);
		return service.setBillVOtoUIByMtappVO(pk_org, retvo, currentDjLXVO, getModel().getContext().getNodeCode());
	}
	
	protected MatterSourceRefDlg getMaSourceDlg() {
		if(maSourceDlg == null){
			maSourceDlg = new MatterSourceRefDlg(getModel().getContext());
		}
		return maSourceDlg;
	}

	private QueryConditionDLG getQryDlg() {
		if (queryDialog == null) {
			TemplateInfo tempinfo = new TemplateInfo();
			tempinfo.setPk_Org(getModel().getContext().getPk_group());
			tempinfo.setFunNode(ErmMatterAppConst.MAPP_NODECODE_MN );
			tempinfo.setUserid(getModel().getContext().getPk_loginUser());
			tempinfo.setNodekey("mtTOjkbxQry");
			/*
			* @res "查询条件"
			*/
			queryDialog = new QueryConditionDLG(getEditor(), null,
					tempinfo,
					nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0002782"));
			queryDialog.registerCriteriaEditorListener(new MaQueryCriteriaChangedListener(getModel()));
		}
		return queryDialog;
	}
	
	public BillForm getEditor() {
		return editor;
	}
	public void setEditor(BillForm editor) {
		this.editor = editor;
	}
	
	@Override
	protected boolean isActionEnable() {
		// 费用调整单情况拉单按钮不可用
		DjLXVO currentDjlx = ((ErmBillBillManageModel) getModel()).getCurrentDjLXVO();
		boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjlx, ErmDjlxConst.BXTYPE_ADJUST);
		
		return !isAdjust && super.isActionEnable();
	}
}
