package nc.ui.arap.bx.actions;

import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.fi.pub.Currency;
import nc.ui.arap.bx.BxParam;
import nc.ui.arap.bx.listeners.BxCardHeadEditListener;
import nc.ui.arap.bx.remote.QcDateCall;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;

/**
 * <p>
 * 		 单据拷贝动作
 *      nc.ui.arap.bx.actions.CopyAction
 * </p>
 *
 * @see
 * @author  twei
 * @modify  liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-1-21 下午02:56:12
 */
public class CopyAction extends AddAction {

	public void copy() throws Exception {
		
		if(getMainPanel().getCurrWorkPage()==BillWorkPageConst.LISTPAGE){
			CardAction action = new CardAction();
			action.setActionRunntimeV0(getMainPanel());
			action.changeTab();
		}

		BillCardPanel billCardPanelDj = this.getBillCardPanel();

		billCardPanelDj.transferFocusTo(0);

		JKBXVO bxVO = getVoCache().getCurrentVOClone();

		JKBXHeaderVO header = bxVO.getParentVO();

		if(getVoCache().getDjlxVO(header.getDjlxbm()).getFcbz()!=null && getVoCache().getDjlxVO(header.getDjlxbm()).getFcbz().booleanValue()){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000352")/*@res "此单据类型已经封存，不能进行复制操作"*/);
		}

		String[] fieldNotCopy = JKBXHeaderVO.getFieldNotCopy(); //取不需要拷贝的属性

		for (int i = 0; i < fieldNotCopy.length; i++) {
			header.setAttributeValue(fieldNotCopy[i], null);
		}

		if(bxVO.getBxBusItemVOS()!=null){
			for(BXBusItemVO itm:bxVO.getBxBusItemVOS()){
				itm.setPk_jkbx(null);
			}
		}

		//初始化报销vo
		initBXVO(bxVO);

		//设置vo到界面
		getCardPanel().setBillValueVO(bxVO);
		
		//设置新增状态
		getMainPanel().setCurrentPageStatus(BillWorkPageConst.WORKSTAT_NEW);

		//设置最迟还款日
		setZhrq(header.getPk_org());
		
		//设置授权代理
		if(!getBxParam().getIsQc()){
			BxCardHeadEditListener.initSqdlr(getMainPanel(),getCardPanel().getHeadItem(JKBXHeaderVO.JKBXR),getVoCache().getCurrentDjlxbm(),getCardPanel().getHeadItem(JKBXHeaderVO.DWBM));
		}

		//设置焦点
		getBillCardPanel().transferFocusTo(0);


		//设置本币汇率是否能编辑
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject()!=null && !getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject().equals(Currency.getOrgLocalCurrPK(header.getPk_org())))
			getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setEnabled(true);
		else
			getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setEnabled(false);

		BxCardHeadEditListener bxCardHeadEditListener = new BxCardHeadEditListener();
		bxCardHeadEditListener.setActionRunntimeV0(this.getActionRunntimeV0());

		//进行报销标准的处理
		doReimRuleAction();
		
		//单据日期编辑后事件 ,重新设置多版本业务单元
		setHeadOrgMultiVersion(new String[] { JKBXHeaderVO.PK_ORG_V,
				JKBXHeaderVO.FYDWBM_V, JKBXHeaderVO.DWBM_V,JKBXHeaderVO.PK_PCORG_V }, new String[] { JKBXHeaderVO.PK_ORG,
				JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,JKBXHeaderVO.PK_PCORG});
	}

	private void initBXVO(JKBXVO bxvo) {
		JKBXHeaderVO header=bxvo.getParentVO();

		BxParam djSettingParam = getBxParam();
		if (!djSettingParam.getIsQc()) {
			header.setQcbz(BXConstans.UFBOOLEAN_FALSE);
			header.setSxbz(new Integer(BXStatusConst.SXBZ_NO));
			header.setShrq(null);
			header.setApprover(null);
			header.setJsrq(null);
			header.setJsr(null);
			header.setOperator(djSettingParam.getPk_user());
			
			header.setCreator(djSettingParam.getPk_user());
			header.setCreationtime(djSettingParam.getSysDateTime());
			
			header.setDjrq(djSettingParam.getBusiDate());
			header.setDjzt(new Integer(BXStatusConst.DJZT_Saved));
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
					pk_org = BXUiUtil.getPsnPk_org(BXUiUtil.getPk_psndoc());
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
			header.setSxbz(new Integer(BXStatusConst.SXBZ_VALID));
			header.setApprover(djSettingParam.getPk_user());
			header.setJsr(djSettingParam.getPk_user());
			header.setOperator(djSettingParam.getPk_user());
			header.setDjzt(new Integer(BXStatusConst.DJZT_Sign));
		}
		ErVOUtils.clearContrastInfo(bxvo);
	}

}