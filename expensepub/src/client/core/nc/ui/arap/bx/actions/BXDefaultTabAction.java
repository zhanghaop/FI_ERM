package nc.ui.arap.bx.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.ui.arap.bx.BXBillCardPanel;
import nc.ui.arap.bx.BXBillListPanel;
import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.arap.bx.BxParam;
import nc.ui.arap.bx.VOCache;
import nc.ui.arap.bx.listeners.BxCardHeadEditListener;
import nc.ui.arap.bx.remote.PsnVoCall;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXQueryUtil;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.arap.bx.util.BXVOUtils;
import nc.vo.arap.bx.util.BodyEditVO;
import nc.vo.arap.bx.util.BxUIControlUtil;
import nc.vo.arap.bx.util.Page;
import nc.vo.bd.ref.IFilterStrategy;
import nc.vo.ep.bx.BDInfo;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.BxAggregatedVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.MessageVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.er.pub.QryCondArrayVO;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.er.util.SqlUtils_Pub;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.billrule.BillRuleCheckVO;
import nc.vo.fipub.billrule.BillRuleItemVO;
import nc.vo.fipub.rulecontrol.RuleDataCacheEx;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.query.ConditionVO;

/**
 * @author twei
 *
 * nc.ui.arap.bx.actions.BXDefaultAction
 *
 * 借款报销默认Action
 * 提供一些默认的读取函数
 *
 */
public class BXDefaultTabAction extends BxAbstractTabAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
	}
	public void BXDefaultTabAction() {

	}

	protected BXBillMainPanel getMainPanel() {
		return (BXBillMainPanel) getParent();
	}

	@Override
	public BillCardPanel getCardPanel() {
		return getMainPanel().getBillCardPanel();
	}

	protected BillCardPanel getBillCardPanel() {
		return getCardPanel();
	}

	protected BillListPanel getListPanel() {
		return getMainPanel().getBillListPanel();
	}

	protected BillListPanel getBillListPanel() {
		return getListPanel();
	}

	protected BXBillListPanel getBxBillListPanel() {
		return (BXBillListPanel) getListPanel();
	}

	protected BXBillCardPanel getBxBillCardPanel() {
		return (BXBillCardPanel) getCardPanel();
	}

	protected VOCache getVoCache() {
		return getMainPanel().getCache();
	}

	protected BxParam getBxParam() {
		return getMainPanel().getBxParam();
	}

	protected void showWarningMsg(String msg){
		getMainPanel().showWarningMessage(msg);
	}

	protected void showErrorMsg(String msg){
		getMainPanel().showErrorMessage(msg);
	}

	/**
	 * @return　选中的vo数组
	 */
	protected JKBXVO[] getSelBxvos() {
		return ((BXBillMainPanel)getActionRunntimeV0()).getSelBxvos();
	}

	/**
	 * @return　是否卡片界面
	 */
	protected boolean isCard() {
		return getActionRunntimeV0().getCurrWorkPage() == BillWorkPageConst.CARDPAGE;
	}

	/**
	 * @return　选中的vo数组（clone vo)
	 */
	protected JKBXVO[] getSelBxvosClone() {
		List<JKBXVO> list = new ArrayList<JKBXVO>();
		if (getActionRunntimeV0().getCurrWorkPage() == BillWorkPageConst.LISTPAGE) {
			list = getVoCache().getSelectedVOsClone();
		} else if (getActionRunntimeV0().getCurrWorkPage() == BillWorkPageConst.CARDPAGE) {

			JKBXVO vo = getVoCache().getCurrentVO();

			if (vo != null) {
				list.add((JKBXVO) vo.clone());
			}
		}
		return list.toArray(new JKBXVO[list.size()]);
	}

	protected String getNodeCode() {
		return getMainPanel().getNodeCode();
	}

	protected void setHeadValue(String key, Object value) {
		if(getMainPanel().getBillCardPanel().getHeadItem(key)!=null){
			getMainPanel().getBillCardPanel().getHeadItem(key).setValue(value);
		}
	}

	protected static void setHeadValue(BillCardPanel card,String key, Object value) {
		if(card.getHeadItem(key)!=null){
			card.getHeadItem(key).setValue(value);
		}
	}

	protected static Object getHeadValue(BillCardPanel card,String key) {
		return card.getHeadItem(key).getValueObject();
	}

	protected void setHeadValues(String[] key, Object[] value) {
		for (int i = 0; i < value.length; i++) {
			getMainPanel().getBillCardPanel().getHeadItem(key[i]).setValue(value[i]);
		}
	}

	protected Object getHeadValue(String key) {
		BillItem headItem = getCardPanel().getHeadItem(key);
		if(headItem==null){
			headItem=getCardPanel().getTailItem(key);
		}
		if(headItem==null){
			return null;
		}
		return headItem.getValueObject();
	}

	protected void setHeadEditable(String[] fields, boolean status) throws BusinessException {
		for (int i = 0; i < fields.length; i++) {
			BillItem item = getBillCardPanel().getHeadItem(fields[i]);
			item.setEnabled(status);
		}
	}

	protected JKBXVO getBillValueVO() throws ValidationException {
		return getMainPanel().getBillValueVO();
	}

	/**
	 * @param bxvo
	 * @throws BusinessException
	 *
	 * 获取传入Vo的借款冲销信息
	 */
	protected JKBXVO retrieveBxcontrastVO(JKBXVO bxvo) throws BusinessException{
		try{
			Collection<BxcontrastVO> contrasts = getIBXBillPrivate().queryContrasts(bxvo.getParentVO());
			BxcontrastVO[] contrast = contrasts.toArray(new BxcontrastVO[]{});
			bxvo.setContrastVO(contrast);
			return bxvo;
		}catch (Exception e) {
			throw new BusinessException(e.getMessage(),e);
		}
	}


	protected JKBXVO getCurrentSelectedVO() {

		JKBXVO zbvo = null;

		if (getActionRunntimeV0().getCurrWorkPage() == BillWorkPageConst.LISTPAGE) {

			String djoid;

			int row = getMainPanel().getBillListPanel().getHeadTable().getSelectedRow();

			int rowcount_h = getMainPanel().getBillListPanel().getHeadBillModel().getRowCount();

			if (row >= rowcount_h)
				row = rowcount_h - 1;

			if (row >= 0 && rowcount_h > 0) {

				djoid = getMainPanel().getBillListPanel().getHeadBillModel().getValueAt(row, JKBXHeaderVO.PK_JKBX).toString();

				zbvo = getVoCache().getVOByPk(djoid);

				getVoCache().setCurrentDjpk(djoid);
			}

		} else if (getActionRunntimeV0().getCurrWorkPage() == BillWorkPageConst.CARDPAGE) {

			zbvo = getVoCache().getCurrentVO();

		}

		return zbvo;
	}

	/**
	 * @param pk_corp
	 * @param djlxbm
	 * @return 查询常用单据
	 * @throws BusinessException
	 */
	protected List<JKBXHeaderVO> getInitBillHeader(String pk_group,String pk_corp,UFBoolean isGroup,String djlxbm) throws BusinessException {

		DjCondVO condVO = initCond(pk_group,pk_corp,isGroup, djlxbm);

		List<JKBXHeaderVO> vos = getIBXBillPrivate().queryHeaders(0, 1, condVO);
		return vos;
	}

//	/**
//	 * @param pk_corp
//	 * @param djlxbm
//	 * @return 查询常用单据
//	 * @throws BusinessException
//	 */
//	protected List<BXVO> getInitBill(String pk_corp, String djlxbm) throws BusinessException {
//
//		DjCondVO condVO = initCond(pk_corp, djlxbm);
//
//		List<BXVO> vos = getIBXBillPrivate().queryVOs(0, 1, condVO);
//		return vos;
//	}

	private DjCondVO initCond(String pk_group,String pk_org,UFBoolean isGroup, String djlxbm) {

		if (djlxbm == null)
			djlxbm = getVoCache().getCurrentDjlxbm();

		DjCondVO condVO = new DjCondVO();
		condVO.isInit = true;
		//集团级以及业务单元级参数不同 集团 group+isinitgroup(Y)+djlxbm/org+isinitgroup(N)+djlxbm
		if(isGroup.booleanValue()){
			condVO.defWhereSQL = " zb.djlxbm='" + djlxbm + "' and zb.dr=0 and zb.isinitgroup='" + isGroup + "'";
			condVO.pk_group = new String[]{pk_group};
		}else{
			condVO.defWhereSQL = " zb.djlxbm='" + djlxbm + "' and zb.dr=0 and zb.isinitgroup='" + isGroup + "'";
			condVO.pk_org=new String[]{pk_org};
		}


		condVO.isCHz = false;
		condVO.operator = getBxParam().getPk_user();
//		condVO.pk_group = new String[] { BXUiUtil.getPK_group().toString() };
		return condVO;

	}

	protected boolean isCanAddRow(String tableCode) {
		Boolean isCanAddRow = getBusTypeVO().getIsTableAddRow().get(tableCode);
		if (isCanAddRow == null)
			isCanAddRow = true;
		return isCanAddRow;
	}

	protected BusiTypeVO getBusTypeVO() {
		return getMainPanel().getBusTypeVO();
	}

	protected BusiTypeVO getBusTypeVO(String djlxbm, String djdl) {
		return BXUtil.getBusTypeVO(djlxbm, djdl);
	}

	protected JKBXVO retrieveChidren(JKBXVO zbvo) throws BusinessException {
		try {
			JKBXVO bxvo = getIBXBillPrivate().retriveItems(zbvo.getParentVO());
			return bxvo;
		} catch (Exception e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}

	protected List<JKBXHeaderVO> queryHeadersByPage(Page queryPage, DjCondVO condVO) throws BusinessException {
		List<JKBXHeaderVO> bills = getIBXBillPrivate().queryHeaders(queryPage.getThisPageFirstElementNumber(), queryPage.getPageSize(), condVO);
		if(bills.size()<queryPage.getPageSize()){
			queryPage.setThisPageNumber(queryPage.getLastPageNumber());
		}
		return bills;
	}

	/**
	 * @return　返回默认的DjCondVO
	 */
	protected DjCondVO getCondVO() {

		BXBillMainPanel mainPanel = getMainPanel();
		QueryConditionDLG qryDlg = mainPanel.getQryDlg();

		QryCondArrayVO[] vos = BXQueryUtil.getValueCondVO(getBxParam().getIsQc());

		DjCondVO cur_Djcondvo = new nc.vo.ep.dj.DjCondVO();
		cur_Djcondvo.m_NorCondVos = vos;
		ConditionVO[] logicalConditionVOs = qryDlg.getLogicalConditionVOs();

		String whereSQL = qryDlg.getWhereSQL();

		String djlxbms="";
		if(logicalConditionVOs!=null){
			for (int i = 0; i < logicalConditionVOs.length; i++) {
				if(logicalConditionVOs[i].getFieldCode().equals(BXQueryUtil.PZZT)){
					String value = logicalConditionVOs[i].getValue();
					Integer[] Ivalues = BXQueryUtil.splitQueryConditons(value);
					cur_Djcondvo.VoucherFlags =  Ivalues;
				}
				if(logicalConditionVOs[i].getFieldCode().equals(BXQueryUtil.XSPZ)){
					String value = logicalConditionVOs[i].getValue();
					cur_Djcondvo.isLinkPz = new UFBoolean(value).booleanValue();
				}
				if(logicalConditionVOs[i].getFieldCode().equals(BXQueryUtil.APPEND)){
					String value = logicalConditionVOs[i].getValue();
					cur_Djcondvo.isAppend = new UFBoolean(value).booleanValue();
				}
				if(logicalConditionVOs[i].getFieldCode().equals(BXQueryUtil.DJLXBM)){
					djlxbms = logicalConditionVOs[i].getValue();
				}
			}
		}

		String djlxbmStr ="";
		if(!StringUtils.isNullWithTrim(djlxbms)){
			if(djlxbms.indexOf("(")==-1){
				djlxbmStr=" zb.djlxbm='"+djlxbms+"'";
			}else{
				djlxbmStr=" zb.djlxbm in "+djlxbms+"";
			}
		}else{
			if(mainPanel.getCache().getDjlxVOS().length==1){
				djlxbmStr=" zb.djlxbm='"+mainPanel.getCache().getCurrentDjlxbm()+"'";
			}
		}
		if(djlxbmStr.length()!=0){
			if(whereSQL==null){
				whereSQL=djlxbmStr;
			}else{
				whereSQL=whereSQL+" and "+djlxbmStr;
			}
		}


		String user = getBxParam().getPk_user();
		String jkbxr="";

		//FIXME 默认组织错误
	    if(BXUiUtil.getValue(PsnVoCall.PSN_PK_ + user + BXUiUtil.getBXDefaultOrgUnit())!=null){
	    	jkbxr= (String) BXUiUtil.getValue(PsnVoCall.PSN_PK_ + user + BXUiUtil.getBXDefaultOrgUnit());
	    }

	    //判断是集团节点
		String funcode=getMainPanel().getFuncCode();
		UFBoolean isGroup= BXUiUtil.isGroup(funcode);
		if(isGroup.booleanValue()){
			whereSQL = whereSQL + " and isinitgroup='Y'";
		}

		cur_Djcondvo.defWhereSQL = whereSQL;
		cur_Djcondvo.isCHz = false;
		cur_Djcondvo.psndoc = jkbxr;
		cur_Djcondvo.operator = user;
		cur_Djcondvo.isInit = getBxParam().isInit();
		cur_Djcondvo.nodecode=getMainPanel().getBxParam().getNodeOpenType()==BxParam.NodeOpenType_LR_PUB_Approve?BXConstans.BXMNG_NODECODE:getNodeCode();
		cur_Djcondvo.djdl=BXQueryUtil.getDjdlFromBm(djlxbms,getVoCache().getDjlxVOS());

		return cur_Djcondvo;
	}

	/**
	 * 使用从界面取来的汇率值进行计算
	 * modified by zx
	 * @param hl
	 */
	protected void resetYFB_HL(UFDouble hl) {

		resetYFB_HL(hl,JKBXHeaderVO.YBJE, JKBXHeaderVO.BBJE);

	}

	protected void resetYFB_HL(UFDouble hl,String ybjeField, String bbjeField) {

		if (getHeadValue(JKBXHeaderVO.BZBM) == null)
			return;

		try {
			UFDouble[] yfbs = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, getHeadValue(JKBXHeaderVO.BZBM).toString(), getHeadValue(ybjeField) == null ? null : new UFDouble(getHeadValue(ybjeField).toString()), null , getHeadValue(bbjeField) == null ? null
					: new UFDouble(getHeadValue(bbjeField).toString()), null,hl, BXUiUtil.getSysdate());

			if (yfbs[0] != null) {
				setHeadValue(ybjeField, yfbs[0]);
				setHeadValue(JKBXHeaderVO.TOTAL, yfbs[0]);
			}
			if (yfbs[2] != null) {
				setHeadValue(bbjeField, yfbs[2]);
			}
			if (yfbs[4] != null) {
				setHeadValue(JKBXHeaderVO.BBHL, yfbs[4]);
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}

	protected List<JKBXVO> combineMsgs(MessageVO[] msgs, MessageVO[] msgReturn, List<JKBXVO> resultVos) {

		if(msgReturn!=null){
			for (int i = 0; i < msgReturn.length; i++) {
				if (msgReturn[i].isSuccess()) {

					resultVos.add(msgReturn[i].getBxvo());

				} else {
					for (int j = 0; j < msgs.length; j++) {
						if (msgs[j].getBxvo().getParentVO().getPrimaryKey().equals(msgReturn[i].getBxvo().getParentVO().getPrimaryKey())) {
							msgs[j] = msgReturn[i];
						}
					}
				}
			}
		}
		return resultVos;
	}

	/**
	 * @param resultVos
	 *
	 * 更新缓存的vo和界面　
	 */
	protected void updateVoAndView(JKBXVO[] resultVos) {

		if (resultVos == null || resultVos.length == 0)
			return;

		getVoCache().putVOArray(resultVos);

		getMainPanel().updateView();
	}

	protected IBXBillPublic getIBXBillPublic()throws ComponentException{
	      return NCLocator.getInstance().lookup(IBXBillPublic.class);
	}

	protected IArapCommonPrivate getICommonPrivate()throws ComponentException{
	      return NCLocator.getInstance().lookup(IArapCommonPrivate.class);
	}

	protected IBXBillPrivate getIBXBillPrivate()throws ComponentException{
	      return NCLocator.getInstance().lookup(IBXBillPrivate.class);
	}

	protected String getTempPk() {
		//设置临时主键
		int temppkIndex = getMainPanel().getTemppkIndex();
		String tempfbpk = BXConstans.TEMP_FB_PK;
		getMainPanel().setTemppkIndex(temppkIndex+1);

		String temppk = tempfbpk+temppkIndex;
		return temppk;
	}
	/**
	 * //设置报销人的信息
	 * @param needSetAgentInfo
	 * @param org
	 * @throws BusinessException
	 */
	protected void setPsnInfoByUserId(boolean needSetAgentInfo, String org) throws BusinessException {
		String userid = BXUiUtil.getPk_user();
		String pk_group =BXUiUtil.getPK_group();
		//获取客户端缓存
		if(WorkbenchEnvironment.getInstance().getClientCache(PsnVoCall.PSN_PK_ + userid + pk_group)!=null){
			String jkbxr = (String) WorkbenchEnvironment.getInstance().getClientCache(PsnVoCall.PSN_PK_ + userid + pk_group);
			String deptid = (String) WorkbenchEnvironment.getInstance().getClientCache(PsnVoCall.DEPT_PK_ + userid +pk_group);
			String fiorg = (String) WorkbenchEnvironment.getInstance().getClientCache(PsnVoCall.FIORG_PK_ + userid +pk_group);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.JKBXR, jkbxr);
			BillItem item = getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER);
			if (item != null && item.isShow()) {
				getBillCardPanel().setHeadItem(JKBXHeaderVO.RECEIVER, jkbxr);
			}
			getBillCardPanel().setHeadItem(JKBXHeaderVO.DEPTID, deptid);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.FYDEPTID, deptid);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.DWBM,fiorg);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.FYDWBM, fiorg);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.PK_PCORG, fiorg);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.PK_FIORG, fiorg );
			getBillCardPanel().setHeadItem(JKBXHeaderVO.PK_ORG, fiorg);


		}else{
			String[] queryPsnidAndDeptid = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPsnidAndDeptid(userid, org);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.JKBXR, queryPsnidAndDeptid[0]);
			BillItem item = getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER);
			if (item != null && item.isShow()) {
				getBillCardPanel().setHeadItem(JKBXHeaderVO.RECEIVER, queryPsnidAndDeptid[0]);
			}
			getBillCardPanel().setHeadItem(JKBXHeaderVO.DEPTID, queryPsnidAndDeptid[1]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.FYDEPTID, queryPsnidAndDeptid[1]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.DWBM, queryPsnidAndDeptid[2]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.FYDWBM, queryPsnidAndDeptid[2]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.PK_PCORG, queryPsnidAndDeptid[2]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.PK_FIORG, queryPsnidAndDeptid[2] );
			getBillCardPanel().setHeadItem(JKBXHeaderVO.PK_ORG, queryPsnidAndDeptid[2]);
		}

		// 设置授权代理人
		if(needSetAgentInfo){
			BxCardHeadEditListener.initSqdlr(getMainPanel(),getCardPanel().getHeadItem(JKBXHeaderVO.JKBXR),getVoCache().getCurrentDjlxbm(),getCardPanel().getHeadItem(JKBXHeaderVO.DWBM));
		}

	}

	/**
	 * 根据表头total字段的值设置其他金额字段的值，若是借款单，因为没有total字段，所以取ybje字段的值
	 * @param panel
	 * @throws BusinessException
	 */
	protected void setHeadYFB() throws BusinessException{
		UFDouble total =  new UFDouble(0);
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE)!=null){
			total =getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).getValueObject()==null?UFDouble.ZERO_DBL: new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).getValueObject().toString());
		}
		String bzbm = "null";
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject()!=null){
			bzbm = getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject().toString();
		}
		UFDouble hl = null;
		UFDouble globalhl = null;
		UFDouble grouphl = null;
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject()!=null){
			hl=new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject().toString());
		}
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject()!=null){
			globalhl=new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject().toString());
		}
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject()!=null){
			grouphl=new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject().toString());
		}
		if(getPk_org()!=null){
			UFDouble[] je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, total, null, null, null,hl, BXUiUtil.getSysdate());
			getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, je[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.BBJE, je[2]);


			/**
			 * 计算全局集团本位币
			 * @param   amout: 原币金额  localAmount: 本币金额 currtype: 币种  data:日期 pk_org：组织
			 * @return 全局或者集团的本币
			 *
			 */

			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2],
					bzbm, BXUiUtil.getSysdate(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject()
							.toString(), getBillCardPanel().getHeadItem(
							JKBXHeaderVO.PK_GROUP).getValueObject().toString(),
					globalhl, grouphl);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBJE, money[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBJE, money[1]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBHL, money[2]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBHL, money[3]);
		}

		resetCjkjeAndYe(total, bzbm, hl, globalhl, grouphl);

	}

	/**
	 * 根据表头total字段的值设置其他金额字段的值，若是借款单，因为没有total字段，所以取ybje字段的值
	 * @param panel
	 * @throws BusinessException
	 */
	protected void setHeadGlobalYFB() throws BusinessException{
		UFDouble globalhl = null;
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject()!=null){
			globalhl=new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject().toString());
		}
		UFDouble total =getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).getValueObject()==null?UFDouble.ZERO_DBL: new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).getValueObject().toString());
		getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBJE, new UFDouble(total.multiply(globalhl)));

//		UFDouble total =  new UFDouble(0);
//		if(getBillCardPanel().getHeadItem(BXHeaderVO.TOTAL)!=null){
//			total =getBillCardPanel().getHeadItem(BXHeaderVO.TOTAL).getValueObject()==null?UFDouble.ZERO_DBL: new UFDouble(getBillCardPanel().getHeadItem(BXHeaderVO.TOTAL).getValueObject().toString());
//		}else if(getBillCardPanel().getHeadItem(BXHeaderVO.YBJE)!=null){
//			total =getBillCardPanel().getHeadItem(BXHeaderVO.YBJE).getValueObject()==null?UFDouble.ZERO_DBL: new UFDouble(getBillCardPanel().getHeadItem(BXHeaderVO.YBJE).getValueObject().toString());
//		}
//		String bzbm = "null";
//		if(getBillCardPanel().getHeadItem(BXHeaderVO.BZBM).getValueObject()!=null){
//			bzbm = getBillCardPanel().getHeadItem(BXHeaderVO.BZBM).getValueObject().toString();
//		}
//		UFDouble hl = null;
//		if(getBillCardPanel().getHeadItem(BXHeaderVO.GLOBALBBHL).getValueObject()!=null){
//			hl=new UFDouble(getBillCardPanel().getHeadItem(BXHeaderVO.GLOBALBBHL).getValueObject().toString());
//		}
//		if(getPk_org()!=null){
//			UFDouble[] je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, total, null, null, null,hl, BXUiUtil.getSysdate());
//			getBillCardPanel().setHeadItem(BXHeaderVO.YBJE, je[0]);
//			getBillCardPanel().setHeadItem(BXHeaderVO.BBJE, je[2]);
//
//
//			/**
//			 * 计算全局集团本位币
//			 * @param   amout: 原币金额  localAmount: 本币金额 currtype: 币种  data:日期 pk_org：组织
//			 * @return 全局或者集团的本币
//			 *
//			 */
//
//			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(), getBillCardPanel().getHeadItem(BXHeaderVO.PK_ORG).getValueObject().toString());
//			getBillCardPanel().setHeadItem(BXHeaderVO.GROUPBBJE, money[0]);
//			getBillCardPanel().setHeadItem(BXHeaderVO.GLOBALBBJE, money[1]);
//			getBillCardPanel().setHeadItem(BXHeaderVO.GROUPBBHL, money[2]);
//			getBillCardPanel().setHeadItem(BXHeaderVO.GLOBALBBHL, money[3]);
//		}

	}

	private void resetCjkjeAndYe(UFDouble total, String bzbm, UFDouble hl, UFDouble globalhl, UFDouble grouphl) throws BusinessException {
		UFDouble[] je;
		/**
		 * 重新设置冲借款金额，还款金额和支付金额字段.
		 */
		ContrastAction action = new ContrastAction();
		action.setActionRunntimeV0(this.getActionRunntimeV0());
		UFDouble cjkybje = new UFDouble(0);
		BillItem item = getBxBillCardPanel().getHeadItem(JKBXHeaderVO.CJKYBJE);
		if(item!=null&&item.getValueObject()!=null){
			cjkybje = new UFDouble(item.getValueObject().toString());
		}
		if(getBxBillCardPanel().getContrasts()!=null && getBxBillCardPanel().getContrasts().size()>0){
			je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, cjkybje, null, null, null,hl, BXUiUtil.getSysdate());
			/**
			 * 计算全局集团本位币
			 * @param   amout: 原币金额  localAmount: 本币金额 currtype: 币种  data:日期 pk_org：组织
			 * @return 全局或者集团的本币
			 *
			 */
			getBillCardPanel().setHeadItem(JKBXHeaderVO.CJKYBJE, je[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.CJKBBJE, je[2]);
			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2],
					bzbm, BXUiUtil.getSysdate(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject()
							.toString(), getBillCardPanel().getHeadItem(
							JKBXHeaderVO.PK_GROUP).getValueObject().toString(),
					globalhl, grouphl);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPCJKBBJE, money[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALCJKBBJE, money[1]);
		}
		boolean isJK = getBillCardPanel().getBillData().getHeadItem(JKBXHeaderVO.DJDL).getValueObject().equals(JKBXHeaderVO.JK);
		if(!isJK){
			BxAggregatedVO vo =  new BxAggregatedVO(getMainPanel().getCache().getCurrentDjdl());
			getBxBillCardPanel().getBillValueVOExtended(vo);
			JKBXHeaderVO jkHead = VOFactory.createVO(vo.getParentVO(),vo.getChildrenVO()).getParentVO();
			if(cjkybje.doubleValue()>=total.doubleValue()){
				setJe(jkHead, cjkybje.sub(total),new String[]{JKBXHeaderVO.HKYBJE,JKBXHeaderVO.HKBBJE,JKBXHeaderVO.GROUPHKBBJE,JKBXHeaderVO.GLOBALHKBBJE}, globalhl, grouphl);
				setHeadValues(new String[]{JKBXHeaderVO.ZFYBJE,JKBXHeaderVO.ZFBBJE}, new Object[]{null,null});
			}else if(cjkybje.doubleValue()<total.doubleValue()){
				setJe(jkHead, total.sub(cjkybje),new String[]{JKBXHeaderVO.ZFYBJE,JKBXHeaderVO.ZFBBJE,JKBXHeaderVO.GROUPZFBBJE,JKBXHeaderVO.GLOBALZFBBJE}, globalhl, grouphl);
				setHeadValues(new String[]{JKBXHeaderVO.HKYBJE,JKBXHeaderVO.HKBBJE}, new Object[]{null,null});
			}
		}
		getBillCardPanel().setHeadItem(JKBXHeaderVO.YBYE, getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).getValueObject());
		getBillCardPanel().setHeadItem(JKBXHeaderVO.BBYE, getBillCardPanel().getHeadItem(JKBXHeaderVO.BBJE).getValueObject());
	}

	public void setJe(JKBXHeaderVO jkHead, UFDouble cjkybje,String[] yfbKeys,UFDouble globalhl, UFDouble grouphl) throws BusinessException {
		try {

			UFDouble[] yfbs;
			setHeadValue(yfbKeys[0], cjkybje);
			yfbs = Currency.computeYFB(getPk_org(), Currency.Change_YBJE, jkHead.getBzbm(), cjkybje, null, null, null, jkHead.getBbhl(), jkHead.getDjrq());
			UFDouble[] money = Currency.computeGroupGlobalAmount(cjkybje,
					yfbs[2], jkHead.getBzbm(), BXUiUtil.getSysdate(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG)
							.getValueObject().toString(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject()
							.toString(), globalhl, grouphl);
			setHeadValue(yfbKeys[1], yfbs[2]);
			setHeadValue(yfbKeys[2], money[0]);
			setHeadValue(yfbKeys[3], money[1]);

		} catch (BusinessException e) {
			//设置本币错误.
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000009")/*@res "设置本币错误!"*/);
		}
	}

	/**
	 * 根据币种或汇率的变化，重新计算报销单表体财务页签的本币字段的值
	 * @author zhangxiao1
	 */
	protected void resetBodyFinYFB(){
		BillModel billModel = getBillCardPanel().getBillModel(BXConstans.BUS_PAGE);
		if(billModel!=null && billModel.getBodyItems()!=null){
			BXBusItemVO[] bf = (BXBusItemVO[])billModel.getBodyValueVOs(BXBusItemVO.class.getName());
			int length = bf.length;
//			取得表头币种编码和汇率值，根据汇率值换算本币的值，若币种与本位币相同，则忽略界面中自定的汇率
			String bzbm = "null";
			if(getHeadValue(JKBXHeaderVO.BZBM)!=null){
				bzbm = getHeadValue(JKBXHeaderVO.BZBM).toString();
			}
			for(int i=0;i<length;i++){
				transFinYbjeToBbje(i,bzbm);
			}
		}
	}
	/**
	 * 当表体中的原币金额，冲借款金额，还款金额，支付金额四个值中的某个值发生变化时
	 * 调用该方法重新计算其他的值
	 * @param key 发生变化的值
	 * @param row 表体行号
	 * @author zhangxiao1
	 */
	protected void modifyFinValues(String key,int row){
		BillCardPanel panel = getMainPanel().getBillCardPanel();
		UFDouble ybje =  panel.getBodyValueAt(row, BXBusItemVO.YBJE)==null? new UFDouble(0):(UFDouble)panel.getBodyValueAt(row, "ybje");
		UFDouble cjkybje = panel.getBodyValueAt(row, BXBusItemVO.CJKYBJE)==null? new UFDouble(0):(UFDouble)panel.getBodyValueAt(row, "cjkybje");
		UFDouble zfybje = panel.getBodyValueAt(row, BXBusItemVO.ZFYBJE)==null? new UFDouble(0):(UFDouble)panel.getBodyValueAt(row, "zfybje");
		UFDouble hkybje = panel.getBodyValueAt(row, BXBusItemVO.HKYBJE)==null? new UFDouble(0):(UFDouble)panel.getBodyValueAt(row, "hkybje");

//		如果原币金额或冲借款金额发生变化
		if(key.equals(BXBusItemVO.YBJE)||key.equals(BXBusItemVO.CJKYBJE)){
			if(ybje.getDouble()>cjkybje.getDouble()){//如果原币金额大于冲借款金额
				panel.setBodyValueAt(ybje.sub(cjkybje), row, BXBusItemVO.ZFYBJE);//支付金额=原币金额-冲借款金额
				panel.setBodyValueAt("0", row, BXBusItemVO.HKYBJE);
				panel.setBodyValueAt(cjkybje, row, BXBusItemVO.CJKYBJE);
			}else{
				panel.setBodyValueAt(cjkybje.sub(ybje), row, BXBusItemVO.HKYBJE);//还款金额=冲借款金额-原币金额
				panel.setBodyValueAt("0", row, BXBusItemVO.ZFYBJE);
				panel.setBodyValueAt(cjkybje, row, BXBusItemVO.CJKYBJE);
			}
		}else if(key.equals(BXBusItemVO.ZFYBJE)){//如果是支付金额发生变化
			if(zfybje.toDouble()>ybje.toDouble()){//支付金额不能大于原币金额，否则将支付金额的值置为原币金额的值
				zfybje = ybje;
				panel.setBodyValueAt(zfybje, row, BXBusItemVO.ZFYBJE);
			}
			panel.setBodyValueAt(ybje.sub(zfybje), row, BXBusItemVO.CJKYBJE);//冲借款金额=原币金额-支付金额
			panel.setBodyValueAt("0", row, BXBusItemVO.HKYBJE);
		}else if(key.equals(BXBusItemVO.HKYBJE)){//如果是还款金额发生变化
			panel.setBodyValueAt(ybje.add(hkybje), row, BXBusItemVO.CJKYBJE);//冲借款金额=原币金额+还款金额
			panel.setBodyValueAt("0", row, BXBusItemVO.ZFYBJE);
		}
		panel.setBodyValueAt(ybje, row, "ybye");//原币余额=原币金额

		String bzbm = "null";
		if(getHeadValue(JKBXHeaderVO.BZBM)!=null){
			bzbm = getHeadValue(JKBXHeaderVO.BZBM).toString();
		}
		transFinYbjeToBbje(row,bzbm);
	}
	/**
	 * 表体财务页签，根据原币金额换算本币金额
	 * @param row 表体行号
	 * @param bzbm 币种编码
	 * @author zhangxiao1
	 */
	protected void transFinYbjeToBbje(int row,String bzbm){
		BillCardPanel panel = getMainPanel().getBillCardPanel();
		UFDouble ybje = (UFDouble)panel.getBillModel(BXConstans.BUS_PAGE).getValueAt(row, BXBusItemVO.YBJE);
		UFDouble cjkybje = (UFDouble)panel.getBillModel(BXConstans.BUS_PAGE).getValueAt(row, BXBusItemVO.CJKYBJE);
		UFDouble hkybje = (UFDouble)panel.getBillModel(BXConstans.BUS_PAGE).getValueAt(row, BXBusItemVO.HKYBJE);
		UFDouble zfybje = (UFDouble)panel.getBillModel(BXConstans.BUS_PAGE).getValueAt(row, BXBusItemVO.ZFYBJE);
		UFDouble hl = null;
		UFDouble globalhl = null;
		UFDouble grouphl = null;
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject()!=null){
			hl=new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject().toString());
		}
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject()!=null){
			globalhl=new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject().toString());
		}
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject()!=null){
			grouphl=new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject().toString());
		}
		try {
			UFDouble[] bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, ybje, null, null, null,hl, BXUiUtil.getSysdate());
			panel.getBillModel(BXConstans.BUS_PAGE).setValueAt(bbje[2], row, JKBXHeaderVO.BBJE);
			panel.getBillModel(BXConstans.BUS_PAGE).setValueAt(bbje[2], row, JKBXHeaderVO.BBYE);
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, cjkybje, null, null, null,hl, BXUiUtil.getSysdate());
			panel.getBillModel(BXConstans.BUS_PAGE).setValueAt(bbje[2], row, JKBXHeaderVO.CJKBBJE);
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, hkybje, null, null, null,hl, BXUiUtil.getSysdate());
			panel.getBillModel(BXConstans.BUS_PAGE).setValueAt(bbje[2], row, JKBXHeaderVO.HKBBJE);
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, zfybje, null, null, null,hl, BXUiUtil.getSysdate());
			panel.getBillModel(BXConstans.BUS_PAGE).setValueAt(bbje[2], row, JKBXHeaderVO.ZFBBJE);

			/**
			 * 计算全局集团本位币
			 * @param   amout: 原币金额  localAmount: 本币金额 currtype: 币种  data:日期 pk_org：组织
			 * @return 全局或者集团的本币  money
			 *
			 */
			UFDouble[] je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, ybje, null, null, null,hl, BXUiUtil.getSysdate());
			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2],
					bzbm, BXUiUtil.getSysdate(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject()
							.toString(), getBillCardPanel().getHeadItem(
							JKBXHeaderVO.PK_GROUP).getValueObject().toString(),
					globalhl, grouphl);
			panel.getBillModel(BXConstans.BUS_PAGE).setValueAt(money[0], row, JKBXHeaderVO.GROUPBBJE);
			panel.getBillModel(BXConstans.BUS_PAGE).setValueAt(money[1], row, JKBXHeaderVO.GLOBALBBJE);

		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}

	
	protected void finBodyYbjeEdit() {
		UFDouble newYbje = new UFDouble(0);

		BillModel billModel = getBillCardPanel().getBillModel(BXConstans.BUS_PAGE);
		BXBusItemVO[] items = (BXBusItemVO[])billModel.getBodyValueVOs(BXBusItemVO.class.getName());
		int length = items.length;

		for(int i=0;i<length;i++){
			newYbje = newYbje.add(items[i].getYbje());
		}
		getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, newYbje);

		setHeadYfbByHead();
	}

	protected void setHeadYfbByHead() {

		Object valueObject = getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).getValueObject();

		if(valueObject==null || valueObject.toString().trim().length()==0)
			return;

		UFDouble newYbje=new UFDouble(valueObject.toString());

		try {
			String bzbm = "null";
			if(getHeadValue(JKBXHeaderVO.BZBM)!=null){
				bzbm = getHeadValue(JKBXHeaderVO.BZBM).toString();
			}
			UFDouble hl = null;
			UFDouble globalhl = null;
			UFDouble grouphl = null;
			if(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject()!=null){
				hl=new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject().toString());
			}
			if(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject()!=null){
				hl=new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject().toString());
			}
			if(getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject()!=null){
				globalhl=new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject().toString());
			}
			if(getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject()!=null){
				grouphl=new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject().toString());
			}

			UFDouble[] je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, newYbje, null, null, null,hl, BXUiUtil.getSysdate());
			getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, je[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.BBJE, je[2]);

			//需要传入汇率，集团，全局
			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2],
					bzbm, BXUiUtil.getSysdate(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject()
							.toString(), getBillCardPanel().getHeadItem(
							JKBXHeaderVO.PK_GROUP).getValueObject().toString(),
					globalhl, grouphl);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBJE, money[0]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBJE, money[1]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GROUPBBHL, money[2]);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.GLOBALBBHL, money[3]);

			resetCjkjeAndYe(je[0], bzbm, hl ,globalhl ,grouphl);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}


	}

	//报销规则
	protected void doReimRuleAction() {

		Map<String, SuperVO> expenseType = getMainPanel().getExpenseMap();
		Map<String, SuperVO> reimtypeMap = getMainPanel().getReimtypeMap();

		JKBXVO bxvo=null;
		try {
			bxvo = getBillValueVO();

		} catch (ValidationException e) {
			ExceptionHandler.consume(e);
		}

		String reimrule = BxUIControlUtil.doHeadReimAction(bxvo,getMainPanel().getReimRuleDataMap(), expenseType, reimtypeMap);
		if(getBillCardPanel().getHeadItem(BXConstans.REIMRULE)!=null){
			getBillCardPanel().setHeadItem(BXConstans.REIMRULE,reimrule.toString());
		}

		HashMap<String, String> bodyReimRuleMap = getBodyReimRuleMap();
		List<BodyEditVO> result = BxUIControlUtil.doBodyReimAction(bxvo,getMainPanel().getReimRuleDataMap(),bodyReimRuleMap);
		for(BodyEditVO vo:result){
			getBillCardPanel().setBodyValueAt(vo.getValue(), vo.getRow(), vo.getItemkey(),vo.getTablecode());
		}
	}

	protected void doBodyReimAction(){

		JKBXVO bxvo=null;
		try {
			bxvo = getBillValueVO();
		} catch (ValidationException e) {
			ExceptionHandler.consume(e);
		}

		HashMap<String, String> bodyReimRuleMap = getBodyReimRuleMap();
		List<BodyEditVO> result = BxUIControlUtil.doBodyReimAction(bxvo,getMainPanel().getReimRuleDataMap(),bodyReimRuleMap);
		for(BodyEditVO vo:result){
			getBillCardPanel().setBodyValueAt(vo.getValue(), vo.getRow(), vo.getItemkey(),vo.getTablecode());
		}
	}

    protected HashMap<String, String> getBodyReimRuleMap()
    {
    	HashMap<String, String> hashMap = new HashMap<String, String>();
        if(getBillCardPanel().getBillData().getBillTempletVO() == null || getBillCardPanel().getBillData().getBillTempletVO().getChildrenVO() == null) {
			return hashMap;
		}
        BillTempletBodyVO tbodyvos[] = (BillTempletBodyVO[])getBillCardPanel().getBillData().getBillTempletVO().getChildrenVO();
        BillTempletBodyVO abilltempletbodyvo[] = tbodyvos;
        int i = 0;
        for(int j = abilltempletbodyvo.length; i < j; i++)
        {
            BillTempletBodyVO bodyvo = abilltempletbodyvo[i];
            String userdefine1 = bodyvo.getUserdefine1();
			if(userdefine1 != null && userdefine1.startsWith("getReimvalue")){
				String expenseName = userdefine1.substring(userdefine1.indexOf("(")+1,userdefine1.indexOf(")"));
				Collection<SuperVO> values = getMainPanel().getExpenseMap().values();
				for(SuperVO vo:values){
					if(("\""+vo.getAttributeValue(ExpenseTypeVO.CODE)+"\"").equals(expenseName)){
						userdefine1=vo.getPrimaryKey();
						hashMap.put(bodyvo.getTable_code()+ReimRuleVO.REMRULE_SPLITER+bodyvo.getItemkey(), userdefine1);
					}
				}
            }
        }

        return hashMap;
    }

    protected String getUserdefine(int pos, String key,int def) {
		if(getBillCardPanel().getBillData().getBillTempletVO()==null || getBillCardPanel().getBillData().getBillTempletVO().getChildrenVO()==null){
			return null;
		}
		nc.vo.pub.bill.BillTempletBodyVO[] tbodyvos = (nc.vo.pub.bill.BillTempletBodyVO[]) getBillCardPanel().getBillData().getBillTempletVO().getChildrenVO();
		for(BillTempletBodyVO bodyvo:tbodyvos){
			if(bodyvo.getPos()==pos && bodyvo.getItemkey().equals(key)){
				if(def==1)
					return bodyvo.getUserdefine1();
				else if(def==2)
					return bodyvo.getUserdefine2();
				else if(def==3)
					return bodyvo.getUserdefine3();
			}
		}
		return null;
	}


    protected void doFormulaAction(String formula, String skey, int srow, String stable, Object svalue) {
		if(formula==null)
			return;
		try{
			/**
			 toHead(headKey,sum(%row%,%key%,%table%))
			 --- 将 sum(%row%,%key%,%table%) 公式的值赋值给表头headKey;

			 toBody(%row%,%key%,%table%,sum(%row%,%key%,%table%))
			 --- 将 sum(%row%,%key%,%table%) 公式的值赋值给表体 table页签的key字段的row行上

			 个性化公式：支持表头，表体，表体各页签之间进行数据的传递

			 sum() 合计
			 min() 最大值
			 max() 最小值
			 %key%   默认是触发公式的字段， 可以直接指定
			 %row%   默认是触发公式的行，可以指定， 表头直接用-1, 所有行都进行赋值和取值则用: %all%
			 %table% 默认是触发公式的页签， 可以直接指定

			 公式定义在单据模板表体自定义2上，在字段编辑时执行

			 * */
			formula = formula.replace('(', '#');
			formula = formula.replace(')', '#');
			formula = formula.replace(',', '#');
			formula=formula.trim();

			if(formula.startsWith("toHead")){
				String[] values = formula.split("#");
				String headKey=values[1];
				String func=values[2];
				String prow=values[3];
				String pkey=values[4];
				String ptab=values[5];

				String key=pkey.equals("%key%")?skey:pkey;
				String table=ptab.equals("%table%")?stable:ptab;

				Object resultvalue = getResultValue(func,svalue, prow, key, table);

				if(resultvalue!=null){
					setHeadValue(headKey, resultvalue);
				}
			}
			if(formula.startsWith("toBody")){
				String[] values = formula.split("#");
				String bodyRow=values[1];
				String bodyKey=values[2];
				String bodyTab=values[3];
				String func=values[4];
				String prow=values[5];
				String pkey=values[6];
				String ptab=values[7];

				String key=pkey.equals("%key%")?skey:pkey;
				String table=ptab.equals("%table%")?stable:ptab;

				Object resultvalue = getResultValue(func,svalue, prow, key, table);

				BillItem item = getBillCardPanel().getBodyItem(bodyTab,bodyKey);
		        BillModel bm = getBillCardPanel().getBillModel(bodyTab);

				if(resultvalue!=null){
					bodyKey=bodyKey.equals("%key%")?skey:bodyKey;
					bodyTab=bodyTab.equals("%table%")?stable:bodyTab;

					if(bodyRow.equals("%all%")){
						int rowCount = getBillCardPanel().getRowCount(bodyTab);
						for (int i = 0; i < rowCount; i++) {
							getBillCardPanel().setBodyValueAt(resultvalue, i, bodyKey,bodyTab);

							if (bm != null)
					            bm.execFormulas(i, item.getEditFormulas());
						}
					}else if(bodyRow.equals("%row%")){
						getBillCardPanel().setBodyValueAt(resultvalue, srow, bodyKey,bodyTab);

				        if (bm != null)
				            bm.execFormulas(srow, item.getEditFormulas());
					}else{
						getBillCardPanel().setBodyValueAt(resultvalue, Integer.parseInt(bodyRow), bodyKey,bodyTab);


				        if (bm != null)
				            bm.execFormulas(Integer.parseInt(bodyRow), item.getEditFormulas());
					}


				}
			}
		}catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	private Object getResultValue(String func,Object svalue, String prow, String key, String table) {
		int row=0;
		Object[] value=null;
		if(prow.equals(-1)){ //head
			value=new Object[]{getHeadValue(key)};
		}else if(prow.equals("%all%")){
			BillModel billModel = getBillCardPanel().getBillModel(table);
			int rowCount = getBillCardPanel().getRowCount(table);
			List<Object> arrayValue=new ArrayList<Object>();
			for (int i = 0; i < rowCount; i++) {
				Object valueAt = billModel.getValueAt(i, key);
				if(valueAt!=null && !valueAt.equals("")){
					arrayValue.add(valueAt);
				}
			}
			value=arrayValue.toArray(new Object[]{});
		}else if(prow.equals("%row%")){
			value=new Object[]{svalue};
		}else{
			row=Integer.parseInt(prow);
			value=new Object[]{getBillCardPanel().getBillModel(table).getValueAt(row, key)};
		}
		if(value==null)
			return null;
		if(value.length<=1)
			return value[0];
		if(func.equals("sum")){
			Object revalue=null;
			for(Object sv:value){
				if(sv==null)
					continue;
				if (sv instanceof UFDouble ) {
					if(revalue==null)
						revalue=sv;
					else
						revalue=((UFDouble)revalue).add((UFDouble) sv);
				}
				if (sv instanceof Integer ) {
					if(revalue==null)
						revalue=sv;
					else
						revalue=new Integer(((Integer)revalue).intValue()+(Integer.parseInt(sv.toString())));
				}
			}
			return revalue;
		}
		if(func.equals("avg")){
			Object revalue=null;
			for(Object sv:value){
				if(sv==null)
					continue;
				if (sv instanceof UFDouble ) {
					if(revalue==null)
						revalue=sv;
					else
						revalue=((UFDouble)revalue).add((UFDouble) sv);
				}
				if (sv instanceof Integer ) {
					if(revalue==null)
						revalue=sv;
					else
						revalue=new Integer(((Integer)revalue).intValue()+(Integer.parseInt(sv.toString())));
				}
			}
			if(revalue==null)
				return null;
			if(revalue instanceof UFDouble)
				return ((UFDouble)revalue).div(value.length);
			if(revalue instanceof Integer)
				return ((Integer)revalue)/(value.length);
		}
		if(func.equals("min")){
			Object revalue=value[0];
			for(Object sv:value){
				if(sv==null)
					continue;
				if (sv instanceof UFDouble ) {
					UFDouble new_name = (UFDouble) sv;
					if(new_name.compareTo(revalue)<0)
						revalue=new_name;
				}
				if (sv instanceof Integer ) {
					Integer new_name = (Integer) sv;
					if(new_name.compareTo((Integer)revalue)<0)
						revalue=new_name;
				}
				if (sv instanceof UFDate ) {
					UFDate new_name = (UFDate) sv;
					//FIXME
//					if(new_name.compareTo(revalue)<0)
					if(new_name.compareTo((UFDate)revalue)<0)
						revalue=new_name;
				}
				if (sv instanceof String ) {
					String new_name = (String) sv;
					if(new_name.compareTo((String)revalue)<0)
						revalue=new_name;
				}
			}
			return revalue;
		}
		if(func.equals("max")){
			Object revalue=value[0];
			for(Object sv:value){
				if(sv==null)
					continue;
				if (sv instanceof UFDouble ) {
					UFDouble new_name = (UFDouble) sv;
					if(new_name.compareTo(revalue)>0)
						revalue=new_name;
				}
				if (sv instanceof Integer ) {
					Integer new_name = (Integer) sv;
					if(new_name.compareTo((Integer)revalue)>0)
						revalue=new_name;
				}
				if (sv instanceof UFDate ) {
					UFDate new_name = (UFDate) sv;
//					if(new_name.compareTo(revalue)>0)
					if(new_name.compareTo((UFDate)revalue)>0)
						revalue=new_name;
				}
				if (sv instanceof String ) {
					String new_name = (String) sv;
					if(new_name.compareTo((String)revalue)>0)
						revalue=new_name;
				}
			}
			return revalue;
		}

		return value;
	}

	/**
	 * 期初本币可编辑 此处插入方法说明。
	 *
	 * @throws BusinessException
	 */
	private void setBBeditable() throws BusinessException {
		nc.ui.pub.bill.BillItem jfbbje = getBillCardPanel().getHeadItem(JKBXHeaderVO.BBJE);
		jfbbje.setEnabled(true);
	}

	private void setDefaultValue(String strDjdl, String strDjlxbm)
			throws BusinessException {
		if (getBxParam().getIsQc()) {
			// FIXME 期初组织本币可编辑，集团，全局本币随汇率改变
			setBBeditable();
			getBillCardPanel().setHeadItem(JKBXHeaderVO.QCBZ, UFBoolean.TRUE);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.APPROVER, getBxParam().getPk_user());
			getBillCardPanel().setHeadItem(JKBXHeaderVO.OPERATOR, getBxParam().getPk_user());
			getBillCardPanel().setHeadItem(JKBXHeaderVO.JSR, getBxParam().getPk_user());
			getBillCardPanel().setHeadItem(JKBXHeaderVO.DJZT, BXStatusConst.DJZT_Sign);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.SXBZ, BXStatusConst.SXBZ_VALID);

		} else {
			getBillCardPanel().setHeadItem(JKBXHeaderVO.QCBZ, UFBoolean.FALSE);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.DJRQ, getBxParam().getBusiDate());
//begin--modified by chendya@ufida.com.cn 审核人、审核日期，结算人，结算日期，支付人，支付日期调整到表尾
//			getBillCardPanel().setHeadItem(BXHeaderVO.SHRQ, "");
//			getBillCardPanel().setHeadItem(BXHeaderVO.JSRQ, "");
//			getBillCardPanel().setHeadItem(BXHeaderVO.APPROVER, "");

			getBillCardPanel().setTailItem(JKBXHeaderVO.SHRQ, "");
			getBillCardPanel().setTailItem(JKBXHeaderVO.JSRQ, "");
			getBillCardPanel().setTailItem(JKBXHeaderVO.APPROVER, "");
//--end
			getBillCardPanel().setHeadItem(JKBXHeaderVO.OPERATOR, getBxParam().getPk_user());
			getBillCardPanel().setHeadItem(JKBXHeaderVO.DJZT, BXStatusConst.DJZT_Saved);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.SXBZ, BXStatusConst.SXBZ_NO);
		}
		getBillCardPanel().setHeadItem(JKBXHeaderVO.DJDL, strDjdl);
		getBillCardPanel().setHeadItem(JKBXHeaderVO.DJLXBM, strDjlxbm);
		getBillCardPanel().setHeadItem(JKBXHeaderVO.DJBH, "");
		getBillCardPanel().setHeadItem(JKBXHeaderVO.DR, new Integer(0));
		getBillCardPanel().setHeadItem(JKBXHeaderVO.PK_GROUP, BXUiUtil.getPK_group());
	}

	protected void setDefaultWithOrg(String strDjdl,String strDjlxbm,String org,boolean isnew) throws BusinessException {

		BxCardHeadEditListener listener = new BxCardHeadEditListener();
		listener.setActionRunntimeV0(this.getActionRunntimeV0());

		Object dwvalue = getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM).getValueObject();
		Object fydwvalue = getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM).getValueObject();
		Object orgvalue = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
		// 设置默认值
		if(dwvalue==null)
			getBillCardPanel().setHeadItem(JKBXHeaderVO.DWBM, org);
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_PCORG).getValueObject()==null)
			getBillCardPanel().setHeadItem(JKBXHeaderVO.PK_PCORG, org);
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_FIORG).getValueObject()==null)
			getBillCardPanel().setHeadItem(JKBXHeaderVO.PK_FIORG, org );
		if(fydwvalue==null)
			getBillCardPanel().setHeadItem(JKBXHeaderVO.FYDWBM, org);
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject()==null)
			getBillCardPanel().setHeadItem(JKBXHeaderVO.PK_ORG, org);

		if(isnew){
			//设置报销人的信息
			setPsnInfoByUserId(!getBxParam().getIsQc(),org);
			//自动带出收款银行账号
			listener.editSkyhzh(true,org);
		}

		org =(String) (getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject()==null?"":getBillCardPanel().getHeadItem("pk_org").getValueObject());

		DjLXVO djlxvo = getVoCache().getCurrentDjlx();
		if(djlxvo.getIsloadtemplate()!=null && djlxvo.getIsloadtemplate().booleanValue()){
			//初始化常用单据
			if(!getBxParam().isInit()){
				List<JKBXVO> initBill = BxUIControlUtil.getInitBill(org,BXUiUtil.getPK_group(),getVoCache().getCurrentDjlxbm(),isnew);
				if(initBill!=null && initBill.size()>0){
					JKBXVO bxvo = initBill.get(0);
					String[] fieldNotCopy = JKBXHeaderVO.getFieldNotInit();
					for (int i = 0; i < fieldNotCopy.length; i++) {
						bxvo.getParentVO().setAttributeValue(fieldNotCopy[i], getHeadValue(fieldNotCopy[i]));
					}
					bxvo.setChildrenVO(null);
					getCardPanel().setBillValueVO(bxvo);
				}
			}
		}
		//重新获得界面上的各个组织
		org =(String) (getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject()==null?"":getBillCardPanel().getHeadItem("pk_org").getValueObject());

		Object dwvalue2 = getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM).getValueObject();
		Object fydwvalue2 = getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM).getValueObject();
		Object orgvalue2 = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
		//对不同栏的编辑状态进行重置
		if(!BXVOUtils.simpleEquals(orgvalue2, orgvalue)){
			listener.initPayentityItems(!isnew);
		}
		if(!BXVOUtils.simpleEquals(fydwvalue2, fydwvalue)){
			listener.initCostentityItems(!isnew);
		}
		if(!BXVOUtils.simpleEquals(dwvalue2, dwvalue)){
			listener.initUseEntityItems(!isnew);
		}

		if(org==null || org.trim().length()==0)
			return;

		if (getBxParam().getIsQc()) {
//			//获得模块的启用日期
			UFDate startDate = BXUiUtil.getStartDate(org);

			if(startDate==null ){
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0001")/*@res "该组织模块启用日期为空"*/);

			}

			UFDateTime sysDateTime = getBxParam().getSysDateTime();
			UFDateTime startDateTime = new UFDateTime(startDate,sysDateTime.getUFTime());

			getBillCardPanel().setHeadItem(JKBXHeaderVO.QCBZ, UFBoolean.TRUE);
			getBillCardPanel().setHeadItem(JKBXHeaderVO.DJRQ, startDate.getDateBefore(1));
//begin--modified by chendya@ufida.com.cn 审核人、审核日期，结算人，结算日期，支付人，支付日期调整到表尾
//			getBillCardPanel().setHeadItem(BXHeaderVO.SHRQ, startDateTime.getDateTimeBefore(1));
//			getBillCardPanel().setHeadItem(BXHeaderVO.JSRQ, startDate.getDateBefore(1));

			getBillCardPanel().setTailItem(JKBXHeaderVO.SHRQ, startDateTime.getDateTimeBefore(1));
			getBillCardPanel().setTailItem(JKBXHeaderVO.JSRQ, startDate.getDateBefore(1));
//--end
		}

		try{//设置最迟还款日
			setZhrq(org);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}

		VOCache voCache = getVoCache();
		String defcurrency = voCache.getCurrentDjlx().getDefcurrency();
		if(defcurrency==null || defcurrency.trim().length()==0){
			defcurrency=Currency.getOrgLocalCurrPK(org);
		}

		//设置币种
		BillItem headItemBZBM = getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM);
		try {
			if (null != headItemBZBM ) {
				String pk_currtype = (String) headItemBZBM.getValueObject();
				if (pk_currtype == null || pk_currtype.trim().length() == 0){//如果表头币种字段为空
						((UIRefPane) (headItemBZBM.getComponent())).setPK(defcurrency);
						//6.0汇率采用新的取法
						UFDouble hl = Currency.getRate(getPk_org(), defcurrency,Currency.getOrgLocalCurrPK(org), BXUiUtil.getSysdate());
						BXUiUtil.resetDecimal(getBillCardPanel(),org,defcurrency);//根据表头币种设置精度
						getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setValue(hl);
						resetYFB_HL(hl);
				}else{//如果表头币种字段非空
					BXUiUtil.resetDecimal(getBillCardPanel(),org,null);//根据表头币种设置精度
					UFDouble hl = Currency.getRate(getPk_org(), defcurrency,Currency.getOrgLocalCurrPK(org), BXUiUtil.getSysdate());
					getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setValue(hl);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
		//设置本币汇率是否能编辑
		if(getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject()!=null && !getBillCardPanel().getHeadItem(JKBXHeaderVO.BZBM).getValueObject().equals(Currency.getOrgLocalCurrPK(org)))
			getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setEnabled(true);
		else
			getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).setEnabled(false);

		if(isnew){
			//报销标准 6.0
			List<ReimRuleVO> vos=NCLocator.getInstance().lookup(nc.itf.arap.prv.IBXBillPrivate.class).queryReimRule(null, org);
			((BXBillMainPanel) getParent()).setReimRuleDataMap(VOUtils.changeCollectionToMapList(vos, "pk_billtype"));
		}
		//执行上述过程之后，重新设置默认值
		setDefaultValue(strDjdl, strDjlxbm);
	}

	protected void setZhrq(String org) throws BusinessException {
		if(org==null)
			return;
		try{
			if(!getBxParam().isInit()){
				//设置最迟还款日
				if(getBillCardPanel().getHeadItem(JKBXHeaderVO.ZHRQ)!=null){
					GregorianCalendar rq = new GregorianCalendar();
					Object valueRq = getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
					String djrq = valueRq==null?new UFDate().toString():valueRq.toString();
					int year = new Integer(djrq.substring(0, 4));
					int month = new Integer(djrq.substring(5,7));
					int date = new Integer(djrq.substring(8,10));
					month--;
					rq.set(year, month, date);
					int time = SysInit.getParaInt(org, BXParamConstant.PARAM_ER_RETURN_DAYS);

					rq.add(GregorianCalendar.DATE, time);
					month = rq.get(GregorianCalendar.MONTH)+1;
					String zhrq = rq.get(GregorianCalendar.YEAR)+"-"+month+"-"+rq.get(GregorianCalendar.DATE);
					getBillCardPanel().setHeadItem(JKBXHeaderVO.ZHRQ, zhrq);
				}
			}
		}catch(Exception e){
			ExceptionHandler.consume(e);
		}
	}

}