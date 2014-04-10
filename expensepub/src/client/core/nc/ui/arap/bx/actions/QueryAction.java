package nc.ui.arap.bx.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.arap.bx.BxParam;
import nc.ui.arap.bx.IPageObserver;
import nc.ui.arap.bx.IPageSubject;
import nc.ui.arap.bx.page.BXPageUtil;
import nc.ui.arap.bx.remote.PsnVoCall;
import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.query.QueryConditionClient;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXQueryUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.er.pub.QryCondArrayVO;
import nc.vo.er.util.StringUtils;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.query.ConditionVO;

/**
 * @author twei
 *
 * 查询动作
 * 
 * nc.ui.arap.bx.actions.QueryAction
 */
public class QueryAction extends BXDefaultAction implements IPageSubject/*added by chendya 支持列表界面分页功能的观察者模式接口*/{
	
	boolean isinitialize;
	
	private void initialize(){
		addObserver(((BXBillMainPanel)getActionRunntimeV0()).getPageBarPanel());
	}
	
	public boolean isIsinitialize() {
		return isinitialize;
	}

	public void setIsinitialize(boolean isinitialize) {
		this.isinitialize = isinitialize;
	}

	public List<IPageObserver> getObserverList() {
		return observerList;
	}

	public void query() throws Exception{
		
		if(!isinitialize){
			initialize();
			setIsinitialize(true);
		}
		
		BXBillMainPanel mainPanel = getMainPanel();
				
		mainPanel.getQryDlg().showModal();
		
		if(mainPanel.getQryDlg().getResult() != QueryConditionClient.ID_OK)
			return;//没有选择“确定”

		//查询动作，刷新标记为false
		doQuery(false);

	}
	
	public void goToPageQuery() throws BusinessException{
		//查询动作，刷新标记为false
		doQuery(false);
	}
	
	/**
	* 刷新的时候调用
	 */
	public void refresh() throws Exception {
		
		doQuery(true);
	}
	
	/**
	 * 处理分页查询后的结果
	 * @author chendya
	 * @param bills
	 * @param pageUtil
	 */
	private List<JKBXHeaderVO> dealPageResult(List<JKBXHeaderVO> bills,BXPageUtil pageUtil){
		List<JKBXHeaderVO> billList = new ArrayList<JKBXHeaderVO>();
		if(bills==null){
			return billList;
		}
		if(bills.size()>pageUtil.getPerPageSize()){
			//针对单据管理，单据查询节点既查借款单又查报销单导致查询结果比指定的页数多
			final Integer size = pageUtil.getPerPageSize();
			for (int i = 0; i < size; i++) {
				billList.add(bills.get(i));
			}
		}else{
			return bills;
		}
		return billList;
	}
	
	/**
	 * 展示数据，同时通知分页观察者更新视图
	 * @author chendya
	 * @param bills
	 * @param isAppend
	 * @throws BusinessException
	 */
	protected void display(List<JKBXHeaderVO> bills,boolean isAppend,BXPageUtil pageUtil) throws BusinessException{
		bills = dealPageResult(bills,pageUtil);
		//常用单据实现集团，组织同时显示一列中
		for(JKBXHeaderVO head:bills){
			if(head.getIsinitgroup().booleanValue()){
				head.setSetorg(head.getPk_group());
			}else{
				head.setSetorg(head.getPk_org());				
			}
		}
		//当卡片查询的时候一条记录显示当前记录，多条切换到列表显示
		if(isCard()){
			if(isAppend){
				getMainPanel().appendListVO(bills); //追加查询结果
				changeToList();
				getMainPanel().updateView();
			}else{
				changeToList();
				getMainPanel().setListVO(bills);  //设置查询结果
			}
		}else{
			if(isAppend){
				getMainPanel().appendListVO(bills); //追加查询结果
				getMainPanel().updateView();
			}else{
				getMainPanel().setListVO(bills);  //设置查询结果
			}
		}
		//通知观察者更新视图
		setValueChanged(true);
		notifyObservers(pageUtil);
	}

	private void doQuery(boolean isrefresh) throws BusinessException {
		BXBillMainPanel mainPanel = getMainPanel();
		
		if(isCard()){
			if(isrefresh){
				if(getCurrentSelectedVO()!=null && getCurrentSelectedVO().getParentVO().getPrimaryKey()!=null){
					List<JKBXVO> values = getIBXBillPrivate().queryVOsByPrimaryKeys(new String[]{getCurrentSelectedVO().getParentVO().getPrimaryKey()}, getCurrentSelectedVO().getParentVO().getDjdl());
					if(values!=null && values.size()!=0){
						getVoCache().addVO(values.get(0));
						mainPanel.updateView();						
					}
					return ;
				}else{
					return;
				}
			}
		}		
		
		DjCondVO condVO = getCondVO();
		
//		int size = getIBXBillPrivate().querySize(condVO);
//		
//		((BXBillMainPanel)getActionRunntimeV0()).getPageBarPanel().setTotalRowCount(size);
//		
//		if(condVO.isAppend){ //是否追加显示查询结果
//			getVoCache().setPage(new PageUtil(size+getVoCache().getVoCache().size(),getVoCache().getPage().getThisPageNumber(),getVoCache().getMaxRecords()));
//		}else{
//			getVoCache().setPage(new PageUtil(size,Page.STARTPAGE,getVoCache().getMaxRecords()));
//		}
//		
//		getVoCache().setQueryPage(new PageUtil(size,Page.STARTPAGE,getVoCache().getMaxRecords()*4));
//		List<BXHeaderVO> bills = queryHeadersByPage(getVoCache().getQueryPage(),condVO);
		
		doPageQuery(condVO);
		
	}
	
	
//begin--added by chendya 支持分页功能添加观察者模式	
	
	List<IPageObserver> observerList = new ArrayList<IPageObserver>();
	
	boolean valueChanged = false;
	
	public void setValueChanged(boolean flag){
		valueChanged = flag;
	}
	
	public boolean isValueChanged() {
		return valueChanged;
	}
	
	@Override
	public synchronized void addObserver(IPageObserver o) {
		if (o == null) {
			throw new NullPointerException();
		}
		if (!observerList.contains(o)) {
			observerList.add(o);
		}
	}
	
	@Override
	public synchronized void deleteObserver(IPageObserver o) {
		observerList.remove(o);
	}
	
	@Override
	public void notifyObservers(Object object) {
		synchronized (this) {
			if (!isValueChanged()) {
				return;
			}
			setValueChanged(false);
			for (Iterator<IPageObserver> iterator = observerList.iterator(); iterator.hasNext();) {
				IPageObserver o = iterator.next();
				o.update(this, object);
			}
		}
	}
	
	public Integer getListSize(){
		return getMainPanel().getBillListPanel().getHeadBillModel().getRowCount();
	}
//--end	
	
	
	/**
	 * 供查询方案快速查询调用
	 * @param whereSql
	 * @throws BusinessException
	 */
	public void doQuickQuery(String whereSql) throws BusinessException{
		
		DjCondVO condVO = getQuickQueryCondVO(whereSql);
		
//		int size = getIBXBillPrivate().querySize(condVO);
//		
//		if(condVO.isAppend){ //是否追加显示查询结果
//			getVoCache().setPage(new PageUtil(size+getVoCache().getVoCache().size(),getVoCache().getPage().getThisPageNumber(),getVoCache().getMaxRecords()));
//		}else{
//			getVoCache().setPage(new PageUtil(size,Page.STARTPAGE,getVoCache().getMaxRecords()));
//		}
//		
//		getVoCache().setQueryPage(new PageUtil(size,Page.STARTPAGE,getVoCache().getMaxRecords()*4));
//		List<BXHeaderVO> bills = queryHeadersByPage(getVoCache().getQueryPage(),condVO);
//		
//		display(bills, condVO.isAppend);
		
		doPageQuery(condVO);
	}
	
	/**
	 * 类似Uif2分页查询实现
	 * @author chendya
	 * @param condVO 查询条件
	 */
	private void doPageQuery(DjCondVO condVO) throws BusinessException{
		
		//先查询出总行数
		int totalRowCount = getIBXBillPrivate().querySize(condVO);
		
		BXPageUtil pageUtil = new BXPageUtil(getMainPanel());
		
		//设置总行数
		pageUtil.setTotalRowCount(totalRowCount);
		
		List<JKBXHeaderVO> bills = queryHeadersByPage(pageUtil,condVO);
		
		display(bills,condVO.isAppend,pageUtil);
	}
	
	/**
	 * @author chendya 支持查询方案快速查询
	 * @param whereSql
	 * @return
	 */
	protected DjCondVO getQuickQueryCondVO(String whereSql){
		QryCondArrayVO[] vos = BXQueryUtil.getValueCondVO(getBxParam().getIsQc());

		DjCondVO cur_Djcondvo = new nc.vo.ep.dj.DjCondVO();
		cur_Djcondvo.m_NorCondVos = vos;
		ConditionVO[] logicalConditionVOs = getMainPanel().getQryDlg().getLogicalConditionVOs();

		List<ConditionVO> condVOList = new ArrayList<ConditionVO>();
		condVOList.addAll(Arrays.asList(getMainPanel().getQryDlg().getQryCondEditor().getLogicalConditionVOs()));
		condVOList.addAll(Arrays.asList( getMainPanel().getQryDlg().getQryCondEditor().getGeneralCondtionVOs()));
		String whereSQL = whereSql;

		// 处理对应的zb形式，以后看看有没有其他的处理方式
		if (whereSQL != null) {
			if (whereSQL.indexOf(JKBXHeaderVO.PK_GROUP) > -1) {
				whereSQL = whereSQL.substring(0, whereSQL.indexOf(JKBXHeaderVO.PK_GROUP))
						+ " zb."+JKBXHeaderVO.PK_GROUP + " "
						+ whereSQL.substring(whereSQL.indexOf(JKBXHeaderVO.PK_GROUP) - 2
								+ ("zb."+JKBXHeaderVO.PK_GROUP).length()) + " ";
			}
			//借款报销单位
			if ( whereSQL.indexOf(JKBXHeaderVO.PK_ORG) > -1) {
				whereSQL = whereSQL.substring(0, whereSQL.indexOf(JKBXHeaderVO.PK_ORG))
				+ " zb."+JKBXHeaderVO.PK_ORG + " "
				+ whereSQL.substring(whereSQL.indexOf(JKBXHeaderVO.PK_ORG) - 2 + ("zb."+JKBXHeaderVO.PK_ORG).length()) + " ";
			}
			if (whereSQL.indexOf(JKBXHeaderVO.PK_JKBX) > -1) {
				whereSQL = whereSQL.substring(0, whereSQL.indexOf("pk_jkbx")) + " zb."+JKBXHeaderVO.PK_JKBX + " "
						+ whereSQL.substring(whereSQL.indexOf("pk_jkbx") - 2 + ("zb."+JKBXHeaderVO.PK_JKBX).length()) + " ";
			}
		}
		
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
			if(getMainPanel().getCache().getDjlxVOS().length==1){
				djlxbmStr=" zb.djlxbm='"+getMainPanel().getCache().getCurrentDjlxbm()+"'";
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
	    if(BXUiUtil.getValue(PsnVoCall.PSN_PK_ + user + BXUiUtil.getPK_group())!=null){
	    	jkbxr= (String) BXUiUtil.getValue(PsnVoCall.PSN_PK_ + user + BXUiUtil.getPK_group());
	    }
		
	    //判断是集团节点
		String funcode=getMainPanel().getFuncCode();
		UFBoolean isGroup= BXUiUtil.isGroup(funcode);
		if(isGroup.booleanValue()){
			if(whereSQL==null){
				whereSQL = " isnull(zb.dr,0)=0 ";
			}
			whereSQL = whereSQL + " and isinitgroup='Y'";
		}
	    
		cur_Djcondvo.defWhereSQL = whereSQL;
		cur_Djcondvo.isCHz = false;
		cur_Djcondvo.psndoc = jkbxr;
		cur_Djcondvo.operator = user;
		cur_Djcondvo.isInit = getBxParam().isInit();
		cur_Djcondvo.nodecode=getMainPanel().getBxParam().getNodeOpenType()==BxParam.NodeOpenType_LR_PUB_Approve?BXConstans.BXMNG_NODECODE:getNodeCode();
		cur_Djcondvo.djdl=BXQueryUtil.getDjdlFromBm(djlxbms,getVoCache().getDjlxVOS());

//		 added by chendya 追加数据权限查询
		 String dataPowerSql = null;
		 try {
		 dataPowerSql = getDataPowerSql(getMainPanel().getQryDlg());
		 } catch (BusinessException e) {
		 getMainPanel().handleException(e);
		 }
		 cur_Djcondvo.setDataPowerSql(dataPowerSql);
//		 --end
		
		return cur_Djcondvo;
	}
	
	public void changeToList(){
		try {
			CardAction action = new CardAction();
			action.setActionRunntimeV0(this.getActionRunntimeV0());
			action.changeTab(BillWorkPageConst.LISTPAGE, true, false,null);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}


}
