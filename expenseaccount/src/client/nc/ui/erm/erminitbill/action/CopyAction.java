package nc.ui.erm.erminitbill.action;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.remote.QcDateCall;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.billpub.view.eventhandler.HeadFieldHandleUtil;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;

public class CopyAction extends NCAction{
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private ErmBillBillForm editor;

	public CopyAction() {
		setCode("Copy");
		setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0053")/*@res "复制"*/);

	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object[] selectedOperaDatas = getModel().getSelectedOperaDatas();
		
		if(selectedOperaDatas.length!=1){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000902")/** @res* "请选择一个单据进行复制"*/);
		}
		//期初关闭后是不可以拷贝的
		JKBXVO jkbxVO=(JKBXVO) getModel().getSelectedData();
		boolean closeflag = getEditor().getHelper().checkQCClose(jkbxVO.getParentVO().getPk_org());
		if(closeflag){
			return;
		}
		
		DjLXVO currentDjlx = ((ErmBillBillManageModel)getModel()).getCurrentDjlx(jkbxVO.getParentVO().getDjlxbm());
		if (currentDjlx.getFcbz() != null
				&& currentDjlx.getFcbz()
				.booleanValue()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000171")/** @res* "该节点单据类型已被封存，不可操作节点！"*/);
		}
		
		beforeAction();
		
		//拷贝所选的借款报销VO
		JKBXVO copyJkbxVO=(JKBXVO)jkbxVO.clone();
		JKBXHeaderVO header = copyJkbxVO.getParentVO();

		
		String[] fieldNotCopy = JKBXHeaderVO.getFieldNotCopy();
		for (int i = 0; i < fieldNotCopy.length; i++) {
			header.setAttributeValue(fieldNotCopy[i], null);
		}
		
		if(currentDjlx.getDjlxbm().equals(BXConstans.BILLTYPECODE_RETURNBILL)){
			copyJkbxVO.setChildrenVO(null);
		}
		
		//初始化
		initVO(copyJkbxVO);
		
		// 表体pk清空
		if (copyJkbxVO.getBxBusItemVOS() != null) {
			for (BXBusItemVO itm : copyJkbxVO.getBxBusItemVOS()) {
				itm.setPk_jkbx(null);
				itm.setPk_busitem(null);
			}
		}

		if (copyJkbxVO.getcShareDetailVo() != null) {
			for (CShareDetailVO itm : copyJkbxVO.getcShareDetailVo()) {
				itm.setPk_jkbx(null);
				itm.setPk_cshare_detail(null);
				itm.setPk_costshare(null);
				itm.setYsdate(copyJkbxVO.getParentVO().getDjrq());
			}
		}

		// 清空核销明细
		copyJkbxVO.setAccruedVerifyVO(null);

		//设置vo到界面
		getEditor().setValue(copyJkbxVO);
		getEditor().getHelper().resetRowState(getEditor());
		
		getEditor().setExpamtEnable();
		
		//设置最迟还款日
		Object billDate = editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		int days = SysInit.getParaInt(header.getPk_org(),BXParamConstant.PARAM_ER_RETURN_DAYS);
		if (billDate != null && billDate.toString().length() > 0) {
			UFDate billUfDate = (UFDate) billDate;
			// 审核、签字、最迟还款日期随单据日期变化
			UFDate zhrq = billUfDate.getDateAfter(days);
			editor.getBillCardPanel().setHeadItem(JKBXHeaderVO.ZHRQ, zhrq);
		}
		//不是期初单据
		if(!((ErmBillBillManageModel)getModel()).isInit()){
			HeadFieldHandleUtil.initSqdlr(getEditor(),getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.JKBXR),((ErmBillBillManageModel)getModel()).getCurrentBillTypeCode(),getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM));
		}
		
		//复制单据时重新设置汇率信息
		getEditor().getHelper().setCurrencyInfo(header.getPk_org());
		// 计算表体相关金额字段数值
		getEditor().getEventHandle().resetBodyFinYFB();
		// 根据表头total字段的值设置其他金额字段的值
		getEditor().getEventHandle().getEventHandleUtil().setHeadBbje();

		//设置分摊页签中的汇率值和本币金额
		getEditor().getEventHandle().resetBodyCShare(null);
		
		
		//设置本币汇率是否能编辑
		if(getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject()!=null && !getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject().equals(Currency.getOrgLocalCurrPK(header.getPk_org())))
			getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setEnabled(true);
		else
			getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setEnabled(false);
		
		
		//单据日期编辑后事件 ,重新设置多版本业务单元
		getEditor().getHelper().setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.PK_ORG_V,
				JKBXHeaderVO.FYDWBM_V, JKBXHeaderVO.DWBM_V,JKBXHeaderVO.PK_PCORG_V }, new String[] { JKBXHeaderVO.PK_ORG,
				JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,JKBXHeaderVO.PK_PCORG});
		
		getEditor().getBillCardPanel().getHeadItem("zy").getValueObject();
		
		//过滤相应的字段
		getEditor().getHelper().getAfterEditUtil().initPayentityItems(false);
		getEditor().getHelper().getAfterEditUtil().initCostentityItems(false);
		getEditor().getHelper().getAfterEditUtil().initUseEntityItems(false);
		getEditor().getHelper().getAfterEditUtil().initPayorgentityItems(false);
		
		//复制时，要对界面上的参照字段设置过滤
		((ErmBillBillForm) getEditor()).filterHeadItem();
		
		((BillForm) getEditor()).getBillCardPanel().getBillData().setBillstatus(VOStatus.NEW);
		
		//设置焦点
		getEditor().getBillCardPanel().transferFocusTo(0);
	}

	/**
	 * 清空拉单过来字段值
	 * @param itm
	 */
	@SuppressWarnings("unused")
	private void setNullForMtappField(BXBusItemVO itm){
		itm.setPk_item(null);
		itm.setSrcbilltype(null);
		itm.setSrctype(null);
	}
	
	/**
	 * 复制时，不需要OnAdd方法，所以要将对应的监听去掉
	 * @see
	 * @since V6.0
	 */
	private void beforeAction() throws BusinessException{
//		if(getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ITEM)!=null){
//			getEditor().getBillCardPanel().setHeadItem(JKBXHeaderVO.PK_ITEM,null);
//		}
		getModel().removeAppEventListener(getEditor());
		getModel().setUiState(UIState.ADD);
		getEditor().showMeUp();
		getEditor().setEditable(true);
		getEditor().getBillCardPanel().addNew();
		if (getEditor().isRequestFocus()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					getEditor().getBillCardPanel().requestFocusInWindow();
					getEditor().getBillCardPanel()
							.transferFocusToFirstEditItem();
				}
			});
		}
		getModel().addAppEventListener(getEditor());
	}

	@Override
	protected boolean isActionEnable()
	{
		JKBXVO jkbxVO=(JKBXVO) getModel().getSelectedData();
		if(jkbxVO ==null ){
			return false;
		}
		return true;
	}

	private void initVO(JKBXVO bxvo) throws BusinessException {
		JKBXHeaderVO header=bxvo.getParentVO();

		String cuserid = WorkbenchEnvironment.getInstance().getLoginUser().getCuserid();
		if (!header.getQcbz().booleanValue()) {
			header.setQcbz(BXConstans.UFBOOLEAN_FALSE);
			header.setSxbz(Integer.valueOf(BXStatusConst.SXBZ_NO));
			header.setShrq(null);
			header.setApprover(null);
			header.setJsrq(null);
			header.setJsr(null);
			header.setOperator(cuserid);

			header.setCreator(cuserid);
			header.setCreationtime(WorkbenchEnvironment.getServerTime());

			header.setDjrq(WorkbenchEnvironment.getInstance().getBusiDate());
			header.setDjzt(Integer.valueOf(BXStatusConst.DJZT_Saved));
		} else {
			UFDate startDate = null;
			String pk_org = bxvo.getParentVO().getPk_org();
			// 获得个性化中默认主组织<1>
			final String key = QcDateCall.QcDate_Date_PK_+bxvo.getParentVO().getPk_org();
			if (WorkbenchEnvironment.getInstance().getClientCache(key) != null) {
				startDate = (UFDate) WorkbenchEnvironment.getInstance().getClientCache(key);
			} else {
				if (pk_org != null && pk_org.length() > 0) {
					pk_org = BXUiUtil.getBXDefaultOrgUnit();
				} else {
					// 取当前登录人所属组织
					pk_org = ErUiUtil.getPsnPk_org(BXUiUtil.getPk_psndoc());
				}
				if (pk_org != null && pk_org.length() > 0) {
					try {
						startDate = BXUiUtil.getStartDate(pk_org);
					} catch (BusinessException e) {
						ExceptionHandler.consume(e);
					}
				}
			}
			if(startDate!=null){
				UFDateTime sysDateTime = BXUiUtil.getSysdatetime();
				UFDateTime startDateTime = new UFDateTime(startDate,sysDateTime.getUFTime());
				header.setShrq(startDateTime.getDateTimeBefore(1));
				header.setJsrq(startDateTime.getDate().getDateBefore(1));
				header.setDjrq(startDateTime.getDate().getDateBefore(1));
			}
			header.setQcbz(BXConstans.UFBOOLEAN_TRUE);
			header.setSxbz(Integer.valueOf(BXStatusConst.SXBZ_VALID));
			header.setApprover(cuserid);
			header.setJsr(cuserid);
			header.setOperator(cuserid);
			header.setDjzt(Integer.valueOf(BXStatusConst.DJZT_Sign));
		}
		ErVOUtils.clearContrastInfo(bxvo);
	}


	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public ErmBillBillForm getEditor() {
		return editor;
	}

	public void setEditor(ErmBillBillForm editor) {
		this.editor = editor;
	}

}