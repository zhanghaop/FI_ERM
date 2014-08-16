package nc.impl.erm.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.sm.accountmanage.AbstractUpdateAccount;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.itf.er.reimtype.IReimTypeService;
import nc.itf.erm.prv.IArapCommonPrivate;
import nc.itf.fip.initdata.IFipInitDataService;
import nc.itf.org.IGroupQryService;
import nc.itf.org.IOrgUnitQryService;
import nc.itf.uap.pf.IWorkflowUpgrade;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IAttribute;
import nc.md.model.IBean;
import nc.md.model.MetaDataException;
import nc.md.model.impl.Attribute;
import nc.ui.bd.ref.RefPubUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.bd.ref.RefInfoVO;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.ep.bx.ReimRuleDef;
import nc.vo.ep.bx.ReimRuleDefVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.er.reimtype.ReimTypeHeaderVO;
import nc.vo.er.reimtype.ReimTypeUtil;
import nc.vo.erm.billcontrast.BillcontrastVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.fip.operatinglogs.OperatingLogVO;
import nc.vo.fip.relation.FipRelationVO;
import nc.vo.org.GroupVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.pftemplate.SystemplateVO;

/**
 * 费用管理产品631升级63EHP2版本升级，升级时的代码调整 *
 */
public class ErmInstall631ToEhp2Adjust extends AbstractUpdateAccount {

	private BaseDAO dao = null;
	/*报销单元数据ID*/
	private static String bx_beanid = "d9b9f860-4dc7-47fa-a7d5-7a5d91f39290";
	
	/*借款单元数据ID*/
	private static String jk_beanid = "e0499b58-c604-48a6-825b-9a7e4d6dacca";
	
	@Override
	public void doBeforeUpdateData(String oldVersion, String newVersion) throws Exception {
		if (!is631UpdateTo63Ehp2(oldVersion, newVersion)) {
			return;
		}
		// 更新报销各个交易类型的报销类型为普通报销
		doUpdateDjlx();
	}

	private void doUpdateDjlx() throws DAOException {
		// 交易类型修改,更新报销单各个交易类型的报销类型为普通报销
		String updateDjlx = " update er_djlx set bxtype = 1  where djdl = 'bx' and bxtype is null ";
		getBaseDAO().executeUpdate(updateDjlx);
		Logger.debug(updateDjlx);
		
		// 交易类型修改,手工结算设置默认值为是
		String updateDjlx2 = " update er_djlx set manualsettle = 'Y' where djdl in ('jk','bx') and manualsettle is null;";
		getBaseDAO().executeUpdate(updateDjlx2);
		Logger.debug(updateDjlx2);
	}

	@Override
	public void doBeforeUpdateDB(String oldVersion, String newVersion) throws Exception {
		if (!is631UpdateTo63Ehp2(oldVersion, newVersion)) {
			// 非61升级到63不处理
			return;
		}
	}

	@Override
	public void doAfterUpdateData(String oldVersion, String newVersion) throws Exception {
		if (!is631UpdateTo63Ehp2(oldVersion, newVersion)) {
			// 非63升级到631不处理
			return;
		}

		Logger.debug("*************************************");
		Logger.debug("******** 费用管理模块升级6.31升级到6.3EHP2开始更新信息开始" + getClass().getName() + "**********");
		Logger.debug("*************************************");
		
		// 申请单升级
		doUpdateMaInfo();
		
		//申请单查询节点模板升级
		updateMaQueryNodeTemplate();
		
		//月末凭证处理模板升级
		updateMonthEndNodeTemplate();
		
		//报销单升级
		doUpdateBxInfo();
		
		//升级借款单和报销单的凭证标志vouchertag
		doUpdateJKBXVT();
		
		// 菜单变更
		doUpdateMenu();
		
		//会计平台升级
		updateFipInfo();
		
		//删除多余查询方案
		clearQueryScheme();
		
		// 单据对照升级
		updateBillContrast();
		
		//报销标准升级
		doUpdateReimRule();
		
		// 六、费用产品集团业务初始化预置数据
		String[] pkgroups = null;
		IGroupQryService ip = NCLocator.getInstance().lookup(IGroupQryService.class);
		GroupVO[] groupVOs = ip.queryAllGroupVOs();
		if (groupVOs != null) {
			pkgroups = new String[groupVOs.length];
			for (int i = 0; i < pkgroups.length; i++) {
				pkgroups[i] = groupVOs[i].getPk_group();
			}
		}

		if (pkgroups != null) {// 复制预制数据到集团
			insertDjlx(pkgroups);//交易类型
		}
		
		// 查询对象升级
		updateQueryObj();
		
		//更新编码规则属性
		updateBillCode();
		
		// 借款单"委托办理"动作升级
        updateJKBillAction();
		
        // 借款单支持委托付款，升级bd_billtype表633
        updateBillTypeForwardBill();
        
        // 635SSC适配升级
        updateSSC();
		
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.31升级到6.3EHP2开始更新信息结束" + getClass().getName() + "**********");
		Logger.debug("*************************************");
	}
	
	// 635借款报销单加入单据类型字段
	private void updateSSC() throws DAOException {
		//单据类型的升级
		String updateBillTypeSql = " update er_bxzb set PK_BILLTYPE = '264X' ";
		getBaseDAO().executeUpdate(updateBillTypeSql);
		Logger.debug(updateBillTypeSql);

		String updateBillTypeSql2 = " update er_jkzb set pk_billtype = '263X' ";
		getBaseDAO().executeUpdate(updateBillTypeSql2);
		Logger.debug(updateBillTypeSql2);
	}
	
	//633加入借款单支持委托付款
	private void updateBillTypeForwardBill() throws DAOException {
		String updateSql = "update bd_billtype set forwardbilltype='36J1' where pk_billtypecode like '263%'";
		getBaseDAO().executeUpdate(updateSql);
		Logger.debug(updateSql);
	}

	@SuppressWarnings("unchecked")
	private void updateJKBillAction() throws BusinessException {
		String[] delActionType = new String[]{"TRANSFERFTS"};
		String[] addActionType = new String[]{"TRANSFERFTS"};
		String sql = "select distinct pk_billtypecode from bd_billtype where systemcode='erm' and ncbrcode='jk' and istransaction='Y'";
		List<String> result = (List<String>) new BaseDAO().executeQuery(sql.toString(), new BaseProcessor() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object processResultSet(ResultSet rs) throws SQLException {
				List<String> lstResult = new ArrayList<String>();
				while (rs.next()) {
					lstResult.add(rs.getString(1));
				}
				return lstResult;
			}

		});
		if(result == null || result.size() < 0){
			return;
		}
		IWorkflowUpgrade workflowUpgrade = NCLocator.getInstance().lookup(IWorkflowUpgrade.class);
		for (String billorTranstype : result) {
			workflowUpgrade.updateBillactionByGlobal(billorTranstype, delActionType, addActionType);
		}
	}
	
	
	/**
	 * 更新编码规则
	 * @throws DAOException 
	 */
	private void updateBillCode() throws DAOException {
		//修改费用承担单位为业务单元属性，报销人单位为行政组织，做盘脚本没有执行，这里强制执行
		String sql1 = " update pub_bcr_candiattr set ENTITYMETAID = '985be8a4-3a36-4778-8afe-2d8ed3902659' where "
				+ " pk_nbcr in ('2011Z3M000000000263X','2011Z3M000000000264X','1001Z31000000000DHFC') and CANDIDATE = 'fydwbm' ";
		getBaseDAO().executeUpdate(sql1);

		String sql2 = " update pub_bcr_candiattr set ENTITYMETAID = 'a0ec952c-e4e5-416a-b3e0-d402725f76be' where "
				+ " pk_nbcr in ('2011Z3M000000000263X','2011Z3M000000000264X','1001Z31000000000DHFC') and CANDIDATE = 'dwbm' ";
		getBaseDAO().executeUpdate(sql2);

		String sql3 = " update pub_bcr_candiattr set ENTITYMETAID = 'a0ec952c-e4e5-416a-b3e0-d402725f76be' where "
				+ " pk_nbcr in ('1001Z31000000000E6ON') and CANDIDATE = 'apply_org'";
		getBaseDAO().executeUpdate(sql3);
	}

	private void clearQueryScheme() throws DAOException {
		String deleteSql = " delete from PUB_QUERYSCHEME where funcode like '2011%' and isprepared = 'Y' "
				+ " and funcode not in (select funcode from SM_FUNCREGISTER)";
		getBaseDAO().executeUpdate(deleteSql);
	}

	//只处理生效生成凭证的单据:根据凭证生成环节设置vouchertag
	@SuppressWarnings("unchecked")
	private void doUpdateJKBXVT() throws Exception {
		Set<String> pksets = new LinkedHashSet<String>();
		//处理报销单
		String bxzbsql = "  QCBZ='N' and DR ='0' and djdl='bx' and djzt='3' and sxbz='1'";
		Collection<BXHeaderVO> bxhead = dao.retrieveByClause(BXHeaderVO.class, bxzbsql);
		for(BXHeaderVO headerVO :bxhead){
			headerVO.setVouchertag(BXStatusConst.SXFlag);
			pksets.add(headerVO.getPk_jkbx());
		}
		getBaseDAO().updateVOArray(bxhead.toArray(new BXHeaderVO[0]), new String[] {JKBXHeaderVO.VOUCHERTAG});
		
		//处理借款单
		String jkzbsql = "  QCBZ='N' and DR ='0' and djdl='jk' and djzt='3' and sxbz='1'";
		Collection<JKHeaderVO> jkhead = dao.retrieveByClause(JKHeaderVO.class, jkzbsql);
		for(JKHeaderVO headerVO :jkhead){
			headerVO.setVouchertag(BXStatusConst.SXFlag);
			pksets.add(headerVO.getPk_jkbx());
		}
		getBaseDAO().updateVOArray(jkhead.toArray(new JKHeaderVO[0]), new String[] {JKBXHeaderVO.VOUCHERTAG});
		
		//处理平台日志:
		String wherepart =  SqlUtils.getInStr("src_relationid", pksets.toArray(new String[]{}), false);
		List<OperatingLogVO> oplogs = (List<OperatingLogVO>) dao.retrieveByClause(OperatingLogVO.class, wherepart);
		if (oplogs != null && oplogs.size() > 0) {
			for (OperatingLogVO log : oplogs) {
				log.setSrc_relationid(log.getSrc_relationid()+"_"+BXStatusConst.SXFlag);
			}
			getBaseDAO().updateVOArray(oplogs.toArray(new OperatingLogVO[0]), new String[] {OperatingLogVO.SRC_RELATIONID});
		}
		//处理关联关系
		List<FipRelationVO> fipRelations = (List<FipRelationVO>) dao.retrieveByClause(FipRelationVO.class, wherepart);
		if (fipRelations != null && fipRelations.size() > 0) {
			for (FipRelationVO fip : fipRelations) {
				fip.setSrc_relationid(fip.getSrc_relationid()+"_"+BXStatusConst.SXFlag);
			}
			getBaseDAO().updateVOArray(fipRelations.toArray(new FipRelationVO[0]), new String[] {FipRelationVO.SRC_RELATIONID});
		}
	}

	private void updateQueryObj() throws DAOException {

        String sql = " update fipub_queryobj set dr = 1 where PK_QUERYOBJ in ('ERM0Z3LOANDET2010010', " + 
                "'ERM0Z3LOANDET2010011', " + 
                "'ERM0Z3LOANDET2010014', " +
                "'ERM0Z3LOANDET2000008', " +
                "'ERM0Z3PEDET201000001') ";
        getBaseDAO().executeUpdate(sql);

        sql = "update fipub_queryobj set tallyfieldname = 'er_expenseaccount.bx_jsfs' where pk_queryobj = 'ERM0Z3LOANDET2010022'";
        getBaseDAO().executeUpdate(sql);

        sql = "update fipub_queryobj set tallyfieldname = 'er_expenseaccount.pk_project' where pk_queryobj = 'ERM0Z3LOANDET2010012'";
        getBaseDAO().executeUpdate(sql);

        sql = "update fipub_queryobj set tallyfieldname = 'er_expenseaccount.assume_dept' where pk_queryobj = 'ERM0Z3LOANDET2010018'";
        getBaseDAO().executeUpdate(sql);

        sql = "update fipub_queryobj set tallyfieldname = 'er_expenseaccount.pk_iobsclass' where pk_queryobj = 'ERM0Z3LOANDET2010019'";
        getBaseDAO().executeUpdate(sql);

        sql = "update fipub_queryobj set tallyfieldname = 'er_expenseaccount.assume_org' where pk_queryobj = 'ERM0Z3LOANDET2010020'";
        getBaseDAO().executeUpdate(sql);

        sql = "update fipub_queryobj set tallyfieldname = 'er_expenseaccount.bx_jkbxr' where pk_queryobj = 'ERM0Z3PEDET201000021'";
        getBaseDAO().executeUpdate(sql);

        sql = "update fipub_queryobj set tallyfieldname = 'er_expenseaccount.bx_deptid' where pk_queryobj = 'ERM0Z3LOANDET2000010'";
        getBaseDAO().executeUpdate(sql);

        sql = "update fipub_reportinitialize_b set resid = 'qryobj_billmaker' where PK_REPORTINITIALIZE_B = '1001Z31000000000K1II'";
        getBaseDAO().executeUpdate(sql);

        sql = "update fipub_reportinitialize_b set tallyfieldname = 'er_expenseaccount.bx_jkbxr' where pk_reportinitialize_b in ('1001Z31000000000SH0Y', '1001Z31000000000SFXI')";
        getBaseDAO().executeUpdate(sql);
        
	}
	
	@SuppressWarnings("unchecked")
	private void updateMonthEndNodeTemplate() throws BusinessException {
		//查询月末处理
		StringBuffer sbf = new StringBuffer(100);
		sbf.append(" funnode='"+BXConstans.MONTHEND_DEAL+"'");
		sbf.append(" and (nodekey like '263X%' or nodekey like '264X%')");
		// 先删后插
		getBaseDAO().deleteByClause(SystemplateVO.class, sbf.toString());
		
		//查询单据查询节点的自定义交易类模板
		StringBuffer sbf2 = new StringBuffer(100);
		sbf2.append(" funnode='"+BXConstans.BXBILL_QUERY+"'");
		sbf2.append(" and (nodekey like '263X%' or nodekey like '264X%')");
		List<SystemplateVO> sysTemplatevos = (List<SystemplateVO>) getBaseDAO().retrieveByClause(SystemplateVO.class,sbf2.toString());

		//将模板插入到月末凭证处理节点
		List<SystemplateVO> systemplateVOs = new ArrayList<SystemplateVO>();
		if(sysTemplatevos==null || sysTemplatevos.size()==0){
			return;
		}
		for (SystemplateVO vo : sysTemplatevos) {
			SystemplateVO systemplateVO = (SystemplateVO) vo.clone();
			systemplateVO.setPrimaryKey(null);
			systemplateVO.setFunnode(BXConstans.MONTHEND_DEAL);
			systemplateVOs.add(systemplateVO);
		}
		if (!systemplateVOs.isEmpty()) {
			getBaseDAO().insertVOList(systemplateVOs);
		}
	}

	private void updateBillContrast() throws BusinessException {
		//查询所有集团
		String[] groupPks = null;
		IGroupQryService ip = NCLocator.getInstance().lookup(IGroupQryService.class);
		GroupVO[] groupVOs = ip.queryAllGroupVOs();
		if (groupVOs != null) {
			groupPks = new String[groupVOs.length];
			for (int i = 0; i < groupPks.length; i++) {
				groupPks[i] = groupVOs[i].getPk_group();
			}
		}else{
			return;
		}
		//1、先删除各个集团的264a对照数据
		getBaseDAO().deleteByClause(BillcontrastVO.class, 
				"pk_org <> 'GLOBLE00000000000000' and src_tradetype='264a'");
		
		// 2、查询预置的264a单据对照数据，供copy
		@SuppressWarnings("unchecked")
		Collection<BillcontrastVO> c = getBaseDAO().retrieveByClause(BillcontrastVO.class, 
				"pk_org = 'GLOBLE00000000000000' and src_tradetype='264a'");
		List<BillcontrastVO> voList = new ArrayList<BillcontrastVO>();
		for (BillcontrastVO bvo : c) {
			for (int j = 0; j < groupPks.length; j++) {
				BillcontrastVO vo = (BillcontrastVO) bvo.clone();
				vo.setPrimaryKey(null);
				String groupPk = groupPks[j];
				vo.setPk_group(groupPk);
				vo.setPk_org(groupPk);
				String des_tradetype = vo.getDes_tradetype();
				vo.setDes_tradetypeid(PfDataCache.getBillTypeInfo(groupPk,
						des_tradetype) == null ? "~" : PfDataCache
								.getBillTypeInfo(groupPk, des_tradetype)
								.getPrimaryKey());
				String src_tradetype = vo.getSrc_tradetype();
				vo.setSrc_tradetypeid(PfDataCache.getBillTypeInfo(groupPk,
						src_tradetype) == null ?  "~" : PfDataCache
						.getBillTypeInfo(groupPk, src_tradetype)
						.getPrimaryKey());
				vo.setStatus(VOStatus.NEW);
				voList.add(vo);
			}
		}
		// 3、 批量保存预置数据
		if (!voList.isEmpty()) {
			getBaseDAO().insertVOArray(voList.toArray(new BillcontrastVO[0]));
		}
		
	}

	public void doUpdateMenu() throws DAOException {
		@SuppressWarnings("rawtypes")
		List templist = (List) getBaseDAO().executeQuery(
				"select menuitemcode from sm_menuitemreg " + "where (menuitemcode = '201160') "
						+ "and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y')",
				new ColumnListProcessor());
		if (templist.isEmpty()) {
			return;
		}
		// 201160存在（报表，原63为201106）表示预制数据已经插入到数据库中
		
		//更新菜单编码 
		//		201101 -->201110
		//		....
		//		201106 -->201160
		String sql = " update sm_menuitemreg set menuitemcode = replace(menuitemcode,'201101','201110') "
				+ " where menuitemcode like '201101%'  "
				+ " and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y') ";
		getBaseDAO().executeUpdate(sql);

		sql = " update sm_menuitemreg set menuitemcode = replace(menuitemcode,'201102','201120') "
				+ " where menuitemcode like '201102%'  "
				+ " and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y') ";
		getBaseDAO().executeUpdate(sql);

		sql = " update sm_menuitemreg set menuitemcode = replace(menuitemcode,'201103','201130') "
				+ " where menuitemcode like '201103%'  "
				+ " and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y') ";
		getBaseDAO().executeUpdate(sql);

		sql = " update sm_menuitemreg set menuitemcode = replace(menuitemcode,'201104','201140') "
				+ " where menuitemcode like '201104%'  "
				+ " and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y') ";
		getBaseDAO().executeUpdate(sql);

		sql = " update sm_menuitemreg set menuitemcode = replace(menuitemcode,'201105','201150') "
				+ " where menuitemcode like '201105%'  "
				+ " and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y') ";
		getBaseDAO().executeUpdate(sql);

		sql = " update sm_menuitemreg set menuitemcode = replace(menuitemcode,'201106','201160') "
				+ " where menuitemcode like '201106%'  "
				+ " and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y') ";
		getBaseDAO().executeUpdate(sql);

        sql = " update sm_menuitemreg set menuitemcode = '20110029' where pk_menuitem = '1001Z310000000019KFP' ";
        getBaseDAO().executeUpdate(sql);
	}

	/**
	 * 报销单升级
	 */
	public void doUpdateBxInfo() throws DAOException{
		// 报销单表头增加收款对象
		String sql = "update er_bxzb set paytarget = case when iscusupplier is null or " +
				"iscusupplier = 'N' then 0 else case when hbbm is null or hbbm = '~' " +
				"then 2 else 1 end  end " ;
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
		
		// 常用单据表头增加收款对象
		String sql1 = "update er_jkbx_init set paytarget = case when iscusupplier " +
				"is null or iscusupplier = 'N' then 0 else case when hbbm is null or hbbm = '~' " +
				"then 2 else 1 end  end " ;
		getBaseDAO().executeUpdate(sql1);
		Logger.debug(sql1);
		
		
		//报销单表体增加字段，从表头带下来
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update er_busitem set paytarget=(select paytarget from er_bxzb where er_busitem.pk_jkbx = er_bxzb.pk_jkbx)");
		sqlBuffer.append(",receiver=( select receiver from er_bxzb where er_busitem.pk_jkbx = er_bxzb.pk_jkbx)");
		sqlBuffer.append(",skyhzh=( select skyhzh from er_bxzb where er_busitem.pk_jkbx = er_bxzb.pk_jkbx)");
		sqlBuffer.append(",hbbm=( select hbbm from er_bxzb where er_busitem.pk_jkbx = er_bxzb.pk_jkbx)");
		sqlBuffer.append(",customer=( select customer from er_bxzb where er_busitem.pk_jkbx = er_bxzb.pk_jkbx)");
		sqlBuffer.append(",custaccount=( select custaccount from er_bxzb where er_busitem.pk_jkbx = er_bxzb.pk_jkbx)");
		sqlBuffer.append(",freecust=( select freecust from er_bxzb where er_busitem.pk_jkbx = er_bxzb.pk_jkbx)");
		sqlBuffer.append(",dwbm=( select dwbm from er_bxzb where er_busitem.pk_jkbx = er_bxzb.pk_jkbx)");
		sqlBuffer.append(",deptid=( select deptid from er_bxzb where er_busitem.pk_jkbx = er_bxzb.pk_jkbx)");
		sqlBuffer.append(",jkbxr=( select jkbxr from er_bxzb where er_busitem.pk_jkbx = er_bxzb.pk_jkbx)");
		getBaseDAO().executeUpdate(sqlBuffer.toString());
		Logger.debug(sqlBuffer.toString());
	}

	/**
	 * 申请单升级
	 * 
	 * @throws DAOException
	 */
	private void doUpdateMaInfo() throws DAOException {
		// 申请单表头增加申请单位 isnull( apply_org,'~')='~'
		String sql = "update ER_MTAPP_BILL set apply_org = case when apply_org is null or apply_org = '~' then pk_org else apply_org end ";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);

		// 单据状态 已提交修改为已保存
		String sqlBillstatus = " update er_mtapp_bill set billstatus = 1 where billstatus = 2 ";
		getBaseDAO().executeUpdate(sqlBillstatus);
		Logger.debug(sqlBillstatus);

		String sqlBillstatus1 = " update er_mtapp_detail set billstatus = 1 where billstatus = 2 ";
		getBaseDAO().executeUpdate(sqlBillstatus1);
		Logger.debug(sqlBillstatus1);
	}
	
	/**
	 * 
	 * @throws DAOException
	 */
	public void updateMaQueryNodeTemplate() throws DAOException {
		// 先删后插
		String deleteSqlWhere = " funnode='" + ErmBillConst.MatterApp_QY_FUNCODE + "' and nodekey != '2611' and nodekey like '261%' ";
		getBaseDAO().deleteByClause(SystemplateVO.class, deleteSqlWhere);

		String sqlWhere = " funnode='" + ErmBillConst.MatterApp_FUNCODE + "' and nodekey != '2611' and nodekey like '261%' ";
		@SuppressWarnings("unchecked")
		List<SystemplateVO> sysTemplatevos = (List<SystemplateVO>) getBaseDAO().retrieveByClause(SystemplateVO.class,
				sqlWhere);

		List<SystemplateVO> systemplateVOs = new ArrayList<SystemplateVO>();
		for (SystemplateVO vo : sysTemplatevos) {
			SystemplateVO systemplateVO = (SystemplateVO) vo.clone();
			systemplateVO.setPrimaryKey(null);
			systemplateVO.setFunnode(ErmBillConst.MatterApp_QY_FUNCODE);
			systemplateVOs.add(systemplateVO);
		}

		if (!systemplateVOs.isEmpty()) {
			getBaseDAO().insertVOList(systemplateVOs);
		}
	}
	
	private void insertDjlx(String[] pk_groups) throws BusinessException {
		// 先删除，后插入，防止升级过程出错重复插入
		String delSql = "delete from er_djlx where (djlxbm = '2621' or djlxbm = '264a' or djlxbm = '265a' or djlxbm = '264c')  and "
				+ SqlUtils.getInStr("pk_group", pk_groups, true);
		getBaseDAO().executeUpdate(delSql);

		DjLXVO[] sourcevos = NCLocator.getInstance().lookup(IArapBillTypePublic.class)
				.queryByWhereStr(" pk_group='@@@@'" + " and (djdl in ('ac') or djlxbm = '264a' or djlxbm = '265a' or djlxbm = '264c') ");

		List<DjLXVO> result = new ArrayList<DjLXVO>();
		// 增加对应集团的交易类型
		for (int i = 0; i < pk_groups.length; i++) {
			// 63升级程序没有处理，这里加入
			boolean isErmInstall = ErUtil.isProductInstalled(pk_groups[i], BXConstans.ERM_MODULEID);
			if (isErmInstall) {
				for (DjLXVO sysvo : sourcevos) {
					DjLXVO djlx = (DjLXVO) sysvo.clone();
					djlx.setPk_group(pk_groups[i]);
					djlx.setPrimaryKey(null);
					result.add(djlx);
				}
			}
		}

		getBaseDAO().insertVOArray(result.toArray(new DjLXVO[] {}));
	}
	
	/**
	 * 会计平台新增预置脚本升级
	 * 
	 * @throws BusinessException
	 */
	private void updateFipInfo() throws BusinessException {
		String updateSql = " update fip_factor set entityid = '985be8a4-3a36-4778-8afe-2d8ed3902659' " +
				"where displayname = '费用承担单位' and pk_group != 'GLOBLE00000000000000' "/*-=notranslate=-*/;
		getBaseDAO().executeUpdate(updateSql);
		
		NCLocator.getInstance().lookup(IFipInitDataService.class).initSrcData(BXConstans.ERM_PRODUCT_CODE_Lower, null);// 单据影响因素的升级
	}

	private boolean is631UpdateTo63Ehp2(String oldVersion, String newVersion) {
		Logger.debug("旧版本：" + oldVersion);
		Logger.debug("新版本：" + newVersion);
		return oldVersion != null && oldVersion.trim().startsWith("6.31") && newVersion != null
				&& newVersion.startsWith("6.3") && (newVersion.endsWith("EHP2") || newVersion.startsWith("6.35"));
	}
	
	private BaseDAO getBaseDAO() {
		if (dao == null) {
			dao = new BaseDAO();
		}
		return dao;
	}
	
	/**
	 * 报销标准升级
	 */
	public void doUpdateReimRule() throws DAOException{
		//给现有集团和组织都设置默认维度
//		doSetGroupOrgReimDimension();//界面处理
		//读配置文件配置组织级
		doSetXmlToDimension();
		//将旧标准迁移到新的表中
		doTransferOldReimRule();
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
	public void doSetGroupOrgReimDimension() throws DAOException{
		//给现有集团和组织都设置默认维度
		ListResultSetProcessor processor = new ListResultSetProcessor();
		List<String> billtypes = (List<String>) getBaseDAO().executeQuery("select distinct djlxbm from er_djlx where djdl in('jk','bx') and (BXTYPE is null or bxtype!=2)", processor);
		List<ReimRuleDimVO> vodims;
		try {
			vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
					.queryReimDim("2631", "GLOBLE00000000000000", "~");
			GroupVO[] groups = NCLocator.getInstance().lookup(IGroupQryService.class).
								queryAllGroupVOs();
			if(groups==null || groups.length==0)
				return;
			ReimRuleDimVO[] arraydims = vodims.toArray(new ReimRuleDimVO[0]);
			//设置集团级标准
			for(GroupVO groupVO:groups){
				String pk = groupVO.getPk_group();
				for(String billtype:billtypes){
					for(ReimRuleDimVO dimvo:arraydims){
						dimvo.setPk_billtype(billtype);
						dimvo.setPk_group(pk);
					}
					//保存标准
					NCLocator.getInstance().lookup(IReimTypeService.class)
					.saveReimDim(billtype,pk, "~",arraydims);
				}
			}
			//设置组织级标准
			for(GroupVO groupVO:groups){
				String pk = groupVO.getPk_group();
				OrgVO[] orgvos = NCLocator.getInstance().lookup(IOrgUnitQryService.class)
								.queryAllOrgVOsByGroupID(pk,false,false);
				if(orgvos==null || orgvos.length==0)
					continue;
				//若该集团下有组织，则设置默认值
				for(OrgVO orgvo:orgvos){
					for(String billtype:billtypes){
						for(ReimRuleDimVO dimvo:arraydims){
							dimvo.setPk_billtype(billtype);
							dimvo.setPk_group(pk);
							dimvo.setPk_org(orgvo.getPk_org());
						}
						//保存标准
						NCLocator.getInstance().lookup(IReimTypeService.class)
						.saveReimDim(billtype,pk, orgvo.getPk_org(),arraydims);
					}
				}
			}
		}catch (BusinessException ex) {
			ExceptionHandler.consume(ex);
		}
	}
	
	public void doSetXmlToDimension(){
		GroupVO[] groups;
		try {
			groups = NCLocator.getInstance().lookup(IGroupQryService.class).queryAllGroupVOs();
			if(groups==null || groups.length==0)
				return;
			//读配置文件配置组织级
			for(GroupVO groupVO:groups){
				String pk_group = groupVO.getPk_group();
//				OrgVO[] orgvos = NCLocator.getInstance().lookup(IOrgUnitQryService.class)
//								.queryAllOrgVOsByGroupID(pk_group,false,false);
//				if(orgvos==null || orgvos.length==0)
//					continue;
//				//若该集团下有组织，则设置默认值
//				for(OrgVO orgvo:orgvos){
//					String pk_org = orgvo.getPk_org();
					setXmlToDimension(pk_group);
//				}
			}
		}catch (BusinessException ex) {
			ExceptionHandler.consume(ex);
		}
	}
	
	private class ListResultSetProcessor implements ResultSetProcessor {
		private static final long serialVersionUID = 1L;
		@Override
		public Object handleResultSet(ResultSet rs) throws SQLException {
			List<String> topPks = new ArrayList<String>();
			while (rs.next()) {
				topPks.add(rs.getString(1));
			}
			return topPks;
		}
	}
	 
	@SuppressWarnings("unchecked")
	private void setXmlToDimension(String pk_group) throws DAOException{
		//读配置文件写入报销标准维度
		int orders;
		ListResultSetProcessor processor = new ListResultSetProcessor();
		List<String> billtypes = (List<String>) getBaseDAO().executeQuery("select distinct djlxbm from er_djlx where djdl in('jk','bx') and (BXTYPE is null or bxtype!=2)", processor);
		//需要插入的维度数据
		List<ReimRuleDimVO> reimDimVOs = new ArrayList<ReimRuleDimVO>();
		for(String djlxbm:billtypes){
			orders=8;
			//返回所选交易类型配置的自定义字段
			ReimRuleDefVO reimRuleDefvo = ReimTypeUtil.getReimRuleDefvo(djlxbm);
			if (reimRuleDefvo == null){
				//该交易类型没有自定义配置字段
				continue;
			}
			List<ReimRuleDef> reimRuleDefList = reimRuleDefvo.getReimRuleDefList();
			if (reimRuleDefList != null) {
				for (ReimRuleDef def : reimRuleDefList) {
					ReimRuleDimVO dimvo = new ReimRuleDimVO();
					dimvo.setControlflag(UFBoolean.FALSE);
					dimvo.setOrders(orders);
					orders++;
					dimvo.setShowflag(UFBoolean.FALSE);
					dimvo.setPk_group(pk_group);
					dimvo.setPk_org(ReimRulerVO.PKORG);
					dimvo.setPk_billtype(djlxbm);
					dimvo.setDatatype("BS000010000100001001");
					String reftype = def.getReftype();
					if(reftype!=null && !reftype.equals("")){
						RefInfoVO refvo = RefPubUtil.getRefinfoVO(reftype);
						if (refvo != null) {
							String beanName = refvo.getMetadataTypeName();
							IBean entitybean;
							try {
								entitybean = MDBaseQueryFacade.getInstance().getBeanByName("uap", beanName);
								dimvo.setDatatype(entitybean.getID());
								dimvo.setDatatypename(entitybean.getDisplayName());
								dimvo.setBeanname(entitybean.getName());
								if(entitybean.getRefModelName()!=null && entitybean.getRefModelName().contains(";")){
									dimvo.setReferential(reftype);
									dimvo.setDatatypename(reftype);
								}
							} catch (MetaDataException e) {
								
							}
						} 
					}
					dimvo.setCorrespondingitem(def.getItemkey());
					dimvo.setDisplayname(def.getShowname());
					String itemvalue = def.getItemvalue();
					if(itemvalue!=null){
						itemvalue = itemvalue.replaceAll("@", ".");
						if(def.getItemvalue().startsWith(ReimRulerVO.Reim_head_key))
							itemvalue = itemvalue.substring(itemvalue.indexOf(".")+1);
						if(def.getItemvalue().startsWith(ReimRulerVO.Reim_body_key)){
							itemvalue = itemvalue.substring(itemvalue.indexOf(".")+1);
							try {
								if(djlxbm.startsWith("263")){
									List<IAttribute> attrs = getEntityBean("jk").getAttributes();
									for(IAttribute attr:attrs){
										if(attr.getName().equals("jk_contrast") || attr.getName().equals("jk_busitem")){
											IBean entitybean1 = MDBaseQueryFacade.getInstance().getBeanByID(((Attribute)attr).getDataTypeID());
											List<IAttribute> attrs1 = entitybean1.getAttributes();
											for(IAttribute attr1:attrs1){
												if(attr1.getName().equals(itemvalue)){
													itemvalue = attr.getName()+"."+itemvalue;
													break;
												}
											}
											if(itemvalue.contains("."))
												break;
										}
									}
								}
								else{
									List<IAttribute> attrs = getEntityBean("bx").getAttributes();
									for(IAttribute attr:attrs){
										if(attr.getName().equals("er_busitem") || attr.getName().equals("costsharedetail")
												|| attr.getName().equals("er_bxcontrast") || attr.getName().equals("er_tbbdetail")
												|| attr.getName().equals("accrued_verify")){
											IBean entitybean1 = MDBaseQueryFacade.getInstance().getBeanByID(((Attribute)attr).getDataTypeID());
											List<IAttribute> attrs1 = entitybean1.getAttributes();
											for(IAttribute attr1:attrs1){
												if(attr1.getName().equals(itemvalue)){
													itemvalue = attr.getName()+"."+itemvalue;
													break;
												}
											}
											if(itemvalue.contains("."))
												break;
										}
									}				
								}
							} catch (MetaDataException e) {
							}
						}
						dimvo.setBillrefcode(itemvalue);
					}//if(itemvalue!=null){
					reimDimVOs.add(dimvo);
				}//for
			}//if (reimRuleDefList != null)
		}//for
		if(reimDimVOs.size()>0){
			getBaseDAO().deleteVOArray(reimDimVOs.toArray(new ReimRuleDimVO[0]));
			getBaseDAO().insertVOArray(reimDimVOs.toArray(new ReimRuleDimVO[0]));
		}
	}
	
	IBean jkentitybean;
	IBean bxentitybean;
	private IBean getEntityBean(String djlx) throws MetaDataException{
		if(djlx.equals("jk")){
			if(jkentitybean==null){
				jkentitybean = MDBaseQueryFacade.getInstance().getBeanByID(jk_beanid);
			}
			return jkentitybean;
		}
		else{
			if(bxentitybean==null){
				bxentitybean = MDBaseQueryFacade.getInstance().getBeanByID(bx_beanid);
			}
			return bxentitybean;
		}
	}
	Collection<SuperVO> expenseType;
	private Collection<SuperVO> getExpenseType() throws BusinessException{
		if(expenseType==null){
			expenseType = NCLocator.getInstance().lookup(IArapCommonPrivate.class).getVOs(ExpenseTypeVO.class, "", false);
		}
		return expenseType;
	}
	Collection<SuperVO> reimType;
	private Collection<SuperVO> getReimType() throws BusinessException{
		if(reimType==null){
			reimType = NCLocator.getInstance().lookup(IArapCommonPrivate.class).getVOs(ReimTypeHeaderVO.class, "", false);
		}
		return reimType;
	}
	
	public void doTransferOldReimRule(){
		try {
			//需要插入的标准数据
			List<ReimRulerVO> reimRuleVOs = new ArrayList<ReimRulerVO>();
			List<ReimRuleVO> rulevos = NCLocator.getInstance().lookup(IReimTypeService.class)
					.queryReimRule(null,null);
			for(ReimRuleVO vo:rulevos){
				ReimRulerVO rvo = new ReimRulerVO();
				rvo.setPk_expensetype(vo.getPk_expensetype());
				if(getExpenseType()!=null){
					Iterator<SuperVO> itExpense = getExpenseType().iterator();
					while (itExpense.hasNext()) {
						ExpenseTypeVO voexpense = (ExpenseTypeVO) itExpense.next();
						if (voexpense.getPk_expensetype().equals(vo.getPk_expensetype())){
							rvo.setPk_expensetype_name(voexpense.getName());
						}
					}
				}
				rvo.setAmount(vo.getAmount());
				rvo.setAmount_name(vo.getAmount().toString());
				rvo.setPk_currtype(vo.getPk_currtype());
				rvo.setPk_org(vo.getPk_org());
				GroupVO gvo = NCLocator.getInstance().lookup(IOrgUnitQryService.class)
					.getGroupVOByOrgID(vo.getPk_org());
				rvo.setPk_group(gvo.getPk_group());
				rvo.setPk_billtype(vo.getPk_billtype());
				rvo.setPriority(vo.getPriority());
				if(vo.getPk_reimtype()!=null){
					rvo.setPk_reimtype(vo.getPk_reimtype());
					if(getReimType()!=null){
						Iterator<SuperVO> itExpense = getReimType().iterator();
						while (itExpense.hasNext()) {
							ReimTypeHeaderVO voexpense = (ReimTypeHeaderVO) itExpense.next();
							if (voexpense.getPk_reimtype().equals(vo.getPk_reimtype())){
								rvo.setPk_reimtype_name(voexpense.getName());
							}
						}
					}
				}
				if(vo.getPk_deptid()!=null)
					rvo.setPk_deptid(vo.getPk_deptid());
				if(vo.getMemo()!=null){
					rvo.setMemo(vo.getMemo());
					rvo.setMemo_name(vo.getMemo());
				}
				for(int i=1;i<=10;i++){
					if(vo.getAttributeValue("def"+i)!=null)
						rvo.setAttributeValue("def"+i, vo.getAttributeValue("def"+i));
					if(vo.getAttributeValue("def"+i+"_name")!=null)
						rvo.setAttributeValue("def"+i+"_name", vo.getAttributeValue("def"+i+"_name"));
				}
				reimRuleVOs.add(rvo);
			}
			if(reimRuleVOs.size()>0){
				getBaseDAO().deleteVOArray(reimRuleVOs.toArray(new ReimRulerVO[0]));
				getBaseDAO().insertVOArray(reimRuleVOs.toArray(new ReimRulerVO[0]));
				//增加默认配置
				String[] sqls = {"truncate table ER_REIMRULE",
						"update er_reimruler set showitem='报销单业务行.自定义项10',showitem_name='er_busitem.defitem10' ,controlitem='报销单业务行.自定义项7',controlitem_name='er_busitem.defitem7' ,controlflag='1' where pk_billtype ='2641' and PK_EXPENSETYPE_NAME ='交通费'",
						"update er_reimruler set showitem='报销单业务行.自定义项11',showitem_name='er_busitem.defitem11' ,  controlitem='报销单业务行.自定义项8',controlitem_name='er_busitem.defitem8' ,  controlflag='1' ,  controlformula='defitem11*defitem9' where pk_billtype ='2641' and PK_EXPENSETYPE_NAME ='出差补贴'",
						"update er_reimruler set showitem='报销单业务行.自定义项7',showitem_name='er_busitem.defitem7' ,  controlflag='1' where pk_billtype ='2643' and PK_EXPENSETYPE_NAME ='出差通讯费'"};
				for(String sql:sqls){
					getBaseDAO().executeUpdate(sql);
				}
			}
		}catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}
}
