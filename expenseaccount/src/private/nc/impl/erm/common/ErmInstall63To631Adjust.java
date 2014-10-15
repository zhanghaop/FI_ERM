package nc.impl.erm.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.sm.accountmanage.AbstractUpdateAccount;
import nc.itf.bd.config.mode.IBDMode;
import nc.itf.fip.initdata.IFipInitDataService;
import nc.itf.fipub.report.IReportInitialize;
import nc.itf.org.IGroupQryService;
import nc.itf.tb.control.ICtrlSchemeUpgrade;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pubitf.resa.IResaConstants;
import nc.pubitf.resa.controlarea.IControlAreaPubService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.iufo.freereport.FreeReportVO;
import nc.vo.org.GroupVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.resa.controlarea.ControlAreaVO;

/**
 * 费用管理产品63升级631版本升级，升级时的代码调整 *
 */
public class ErmInstall63To631Adjust extends AbstractUpdateAccount {
	
	private BaseDAO dao = null;

	private BaseDAO getBaseDAO() {
		if (dao == null) {
			dao = new BaseDAO();
		}
		return dao;
	}

	@Override
	public void doBeforeUpdateData(String oldVersion, String newVersion) throws Exception {
		if (!is63UpdateTo631(oldVersion, newVersion)) {
			return;
		}
	}


	@Override
	public void doBeforeUpdateDB(String oldVersion, String newVersion) throws Exception {
		if (!is63UpdateTo631(oldVersion, newVersion)) {
			// 非61升级到63不处理
			return;
		}
	}

	@Override
	public void doAfterUpdateData(String oldVersion, String newVersion) throws Exception {
		if (!is63UpdateTo631(oldVersion, newVersion)) {
			// 非63升级到631不处理
			return;
		}
		
		Logger.debug("*************************************");
		Logger.debug("******** 费用管理模块升级6.3升级到6.31开始更新信息开始" + getClass().getName() + "**********");
		Logger.debug("*************************************");

		// 菜单升级
		doUpdateMenu();
		
		// 收款人升级
		doUpdateReceiver();
		
		// 对公支付升级
		doUpdateIscusupplier();
		
		// 借款报销单表头费用申请单相关信息srcbilltype、srctype升级
		doUpdateMaInfoForHead();
		
		// 借款报销单上申请单外键升级
		doUpdatePk_item();
		
		//申请单升级
		doUpdateMaInfo();
		
		//升级交易类型
		doUpdateDjlx();
		
		//费用申请单控制维度、控制对象管控模式升级
		doUpdateModeSelected();
		
		//会计平台升级
		updateFipInfo();
		
		//维度对照升级
		updateFieldContrast();
		
		//预算重新取数
		doRestartBudget();
		
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.3升级到6.31开始更新信息结束" + getClass().getName() + "**********");
		Logger.debug("*************************************");
	}
	
	public void doRestartBudget() throws BusinessException {
		// 是否安装预算
		boolean isInstallTBB = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (!isInstallTBB) {
			return;
		}
		ICtrlSchemeUpgrade upgrader = NCLocator.getInstance().lookup(ICtrlSchemeUpgrade.class);
		upgrader.upgradeCtrlSchemeDataBySysId(new String[]{"erm"});
	}

	private void doUpdateModeSelected() throws DAOException {
		String sql = "update bd_mode_selected set managemode = "+IBDMode.SCOPE_GROUP_ORG+", uniquescope = "+IBDMode.SCOPE_GROUP_ORG+", visiblescope = "+IBDMode.SCOPE_GROUP_ORG+" where mdclassid in ('7b51cdf3-576c-47bb-99f3-8623bb092531','e779296b-cd8e-4efa-935d-e87ee2432071')";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
	}

	/**
	 * 处理维度对照
	 * @throws BusinessException 
	 */
	private void updateFieldContrast() throws BusinessException {
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
		// 申请单分摊维度、产品线、品牌，查询条件
		String delsqlwhere = " ER_FIELDCONTRAST.APP_SCENE = "+ErmBillFieldContrastCache.FieldContrast_SCENE_SHARERULEField+" and pk_group" +
		" <> 'GLOBLE00000000000000' and (ER_FIELDCONTRAST.SRC_BILLTYPE = '"+ErmBillConst.MatterApp_BILLTYPE+"' " +
		"OR des_fieldcode in ('rule_data.pk_proline','rule_data.pk_brand'))";
		
		String sqlwhere = " ER_FIELDCONTRAST.APP_SCENE = "+ErmBillFieldContrastCache.FieldContrast_SCENE_SHARERULEField+" and pk_group" +
		" = 'GLOBLE00000000000000' and (ER_FIELDCONTRAST.SRC_BILLTYPE = '"+ErmBillConst.MatterApp_BILLTYPE+"' " +
		"OR des_fieldcode in ('rule_data.pk_proline','rule_data.pk_brand'))";
		
		//1、先删除原来v63版本集团的数据
		getBaseDAO().deleteByClause(FieldcontrastVO.class, delsqlwhere);
		
		//2、查询费用申请单261X维度对照分摊、生产线及品牌全局预置数据
		@SuppressWarnings("unchecked")
		Collection<FieldcontrastVO> c = getBaseDAO().retrieveByClause(FieldcontrastVO.class, sqlwhere);
		
		List<FieldcontrastVO> fieldvoList = new ArrayList<FieldcontrastVO>();
		for (FieldcontrastVO fieldcontrastVO : c) {
			for (int j = 0; j < groupPks.length; j++) {
				FieldcontrastVO vo = (FieldcontrastVO) fieldcontrastVO.clone();
				vo.setPrimaryKey(null);
				String groupPk = groupPks[j];
				vo.setPk_group(groupPk);
				vo.setPk_org(groupPk);
				String des_billtype = vo.getDes_billtype();
				if (des_billtype == null) {
					vo.setDes_billtype("~");
				}else{
					vo.setDes_billtypepk(PfDataCache.getBillTypeInfo(groupPk,
							des_billtype) == null ? null : PfDataCache
									.getBillTypeInfo(groupPk, des_billtype)
									.getPrimaryKey());
				}
				String src_billtype = vo.getSrc_billtype();
				vo.setSrc_billtypepk(PfDataCache.getBillTypeInfo(groupPk,
						src_billtype) == null ? null : PfDataCache
						.getBillTypeInfo(groupPk, src_billtype)
						.getPrimaryKey());
				vo.setStatus(VOStatus.NEW);
				fieldvoList.add(vo);
			}
		}
		// 3、 批量保存预置数据
		if (!fieldvoList.isEmpty()) {
			getBaseDAO().insertVOArray(fieldvoList.toArray(new FieldcontrastVO[0]));
		}
		
	}

	/**
	 * 报销单，对公支付升级：如果报销单表头有供应商/客户，则对公支付为Y
	 * @throws DAOException 
	 */
	private void doUpdateIscusupplier() throws DAOException {
		String bxsql = "update er_bxzb set iscusupplier='Y' where receiver in (null,'~') and ((hbbm is not null and hbbm!='~') or (customer is not null and customer!='~'))";
		getBaseDAO().executeUpdate(bxsql);
		Logger.debug(bxsql);
	}

	/**
	 * 借款报销单表头表体pk_item为空时，设置为"~"
	 * @throws DAOException 
	 */
	private void doUpdatePk_item() throws DAOException {
		//借款单表头
		String jksql = "update er_jkzb set pk_item='~' where pk_item is null";
		getBaseDAO().executeUpdate(jksql);
		Logger.debug(jksql);
		
		//报销单表头
		String bxsql = "update er_bxzb set pk_item='~' where pk_item is null";
		getBaseDAO().executeUpdate(bxsql);
		Logger.debug(bxsql);
		
		// 表体
		String bussql = "update er_busitem set pk_item='~' where pk_item is null";
		getBaseDAO().executeUpdate(bussql);
		Logger.debug(bussql);
		
	}

	private void doUpdateDjlx() throws DAOException {
		//交易类型修改
		String updateDjlx = " update er_djlx set matype = (case when matype is null then  1 else matype end) ,bx_percentage = 100 where djdl = 'ma' ";
		getBaseDAO().executeUpdate(updateDjlx);
		Logger.debug(updateDjlx);
	}

	/**
	 * 申请单升级
	 * 
	 * @throws DAOException
	 */
	private void doUpdateMaInfo() throws DAOException {
		// 申请单表体费用承担单位,费用承担部门
		String assume_orgsql = " update er_mtapp_detail set assume_org = case when assume_org is null or assume_org = '~' then "
				+ " (select ma.pk_org from er_mtapp_bill ma where er_mtapp_detail.pk_mtapp_bill = ma.pk_mtapp_bill) else assume_org end ,"
				+ " assume_dept = case when assume_dept is null or assume_dept = '~' then "
				+ " (select ma.assume_dept from er_mtapp_bill ma where er_mtapp_detail.pk_mtapp_bill = ma.pk_mtapp_bill) else assume_dept end;";
		getBaseDAO().executeUpdate(assume_orgsql);
		Logger.debug(assume_orgsql);
	}

	/**
	 * 将子表er_busitem中的srcbilltype、srctype字段同步到表头er_bxzb的srcbilltype、srctype字段
	 * @throws DAOException 
	 * 
	 */
	public void doUpdateMaInfoForHead() throws DAOException {
		String jksql = "update er_jkzb set er_jkzb.srctype=(select max(er_busitem.srctype) from er_busitem where er_jkzb.pk_jkbx=er_busitem.pk_jkbx),"+
										"er_jkzb.srcbilltype=(select max(er_busitem.srcbilltype) from er_busitem where er_jkzb.pk_jkbx=er_busitem.pk_jkbx)";
		getBaseDAO().executeUpdate(jksql);
		Logger.debug(jksql);

		String bxsql = "update er_bxzb set er_bxzb.srctype=(select max(er_busitem.srctype) from er_busitem where er_bxzb.pk_jkbx=er_busitem.pk_jkbx),"+
										"er_bxzb.srcbilltype=(select max(er_busitem.srcbilltype) from er_busitem where er_bxzb.pk_jkbx=er_busitem.pk_jkbx)";
		getBaseDAO().executeUpdate(bxsql);
		Logger.debug(bxsql);
	}

	/**
	 * 63升级631，收款人默认为借款报销人
	 * 
	 * @throws DAOException
	 */
	private void doUpdateReceiver() throws DAOException {
		// 借款单
		String jksql = "update er_jkzb set receiver = jkbxr where  receiver in ('~','') or receiver is null";
		getBaseDAO().executeUpdate(jksql);
		Logger.debug(jksql);
	}

	private void doMenuDeleteFor631() throws DAOException {
		// 删除报表“成本中心费用申请执行情况查询”
		String sql = "select id from iufo_freereport where id = '1001Z3100000000415O5'";
		@SuppressWarnings("unchecked")
		List<FreeReportVO> resultList = (List<FreeReportVO>) getBaseDAO().executeQuery(sql,
				new BeanListProcessor(FreeReportVO.class));
		if (resultList == null || resultList.isEmpty()) {
			return;
		}
		try {
			NCLocator.getInstance().lookup(IReportInitialize.class).deleteInitializedReport("erm", "2011050703");
		} catch (BusinessException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	private void doUpdateMenu() throws DAOException {
		// 631 删除的菜单
		doMenuDeleteFor631();
	}
	
	private boolean is63UpdateTo631(String oldVersion, String newVersion) {
		Logger.debug("旧版本：" + oldVersion);
		Logger.debug("新版本：" + newVersion);
		return oldVersion != null && oldVersion.trim().startsWith("6.3") && newVersion != null
				&& newVersion.startsWith("6.31");
	}
	
	
	/**
	 * 会计平台新增预置脚本升级
	 * 
	 * @throws BusinessException
	 */
	private void updateFipInfo() throws BusinessException {
		copyFipTemplete();//费用申请单转换规则的升级
		NCLocator.getInstance().lookup(IFipInitDataService.class).initSrcData(BXConstans.ERM_PRODUCT_CODE_Lower, null);//单据影响因素的升级
	}
	
	 /**
     * 复制模板
     * @author: zhangyfh@ufida.com.cn
	 * @throws BusinessException 
     */
	@SuppressWarnings("unchecked")
	private void copyFipTemplete() throws BusinessException {
		String queryAllGroup = "select pk_group from org_group";
		List<String> controlAreas = new ArrayList<String>();
		List<String> groups = (List<String>) new BaseDAO().executeQuery(queryAllGroup, new ColumnListProcessor());
		
		if(groups == null){
			return;
		}
		
		for (String pk_group : groups) {
			// 获取集团下的管控范围
			ControlAreaVO[] controlAreaVOs = NCLocator.getInstance().lookup(IControlAreaPubService.class)
					.queryControlAreaByGroupPK(pk_group);
			if (controlAreaVOs != null && controlAreaVOs.length > 0) {
				for (int i = 0; i < controlAreaVOs.length; i++) {
					controlAreas.add(controlAreaVOs[i].getPk_controlarea());
				}
			}
		}
		
		// 为该集团下的所有管控范围 预置数据
		if (controlAreas != null && controlAreas.size() > 0) {
			NCLocator
					.getInstance()
					.lookup(IFipInitDataService.class)
					.initDesData(IResaConstants.RESA_BILLTYPE, groups.toArray(new String[0]),
							controlAreas.toArray(new String[0]));
		}
	}
}
