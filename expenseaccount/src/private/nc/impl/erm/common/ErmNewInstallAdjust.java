package nc.impl.erm.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.arap.bx.ErContrastUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.mw.sqltrans.TempTable;
import nc.bs.sm.accountmanage.AbstractUpdateAccount;
import nc.erm.pub.conversion.ErmBillCostConver;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.itf.erm.service.IErmGroupPredataService;
import nc.itf.fip.initdata.IFipInitDataService;
import nc.itf.org.IGroupQryService;
import nc.itf.tb.control.IRuleUpgradeToV63;
import nc.itf.uap.bbd.func.IFuncRegisterQueryService;
import nc.itf.uap.pf.IWorkflowUpgrade;
import nc.itf.uap.template.ISystemTemplateAssignService;
import nc.jdbc.framework.ConnectionFactory;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountManageService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.erm.expenseaccount.ExpenseBalVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.GroupVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pftemplate.SystemplateBaseVO;
import nc.vo.pub.pftemplate.SystemplateVO;
import nc.vo.sm.funcreg.FuncRegisterVO;
import nc.vo.sm.funcreg.MenuItemVO;
import nc.vo.sm.funcreg.ParamRegVO;
import nc.vo.trade.summarize.Hashlize;
import nc.vo.trade.summarize.IHashKey;

/**
 * 费用管理产品63版本，升级时的代码调整 *
 */
public class ErmNewInstallAdjust extends AbstractUpdateAccount {
	private BaseDAO dao = null;

	private BaseDAO getBaseDAO() {
		if (dao == null) {
			dao = new BaseDAO();
		}
		return dao;
	}

	private int batchUpdate(String sql, List<SQLParameter> parameterList) throws DAOException {
		PersistenceManager manager = null;
		int value;
		try {
			manager = PersistenceManager.getInstance();
			manager.setMaxRows(100000);
			manager.setAddTimeStamp(true);
			JdbcSession session = manager.getJdbcSession();

			session.addBatch(sql, parameterList.toArray(new SQLParameter[parameterList.size()]));
			value = session.executeBatch();
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return value;
	}

	private IBXBillPrivate getQueryService() {
		return NCLocator.getInstance().lookup(IBXBillPrivate.class);
	}

	/**
	 * 新安装账套中数据库结构升级之后，进行数据升级之前调用 处理63对于61中数据表变更的新增字段默认值设置
	 */
	private void doBeforeUpdateData() throws DAOException, BusinessException, Exception {
		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.1升级到6.3开始更新信息开始" + getClass().getName() + "**********");
		Logger.debug("*************************************");

		// 一、删除多余的参数设置 “FICOMMON02 单据列表每页显示记录数”
		getBaseDAO().executeUpdate("delete from pub_sysinit where initcode = 'FICOMMON02'");
		getBaseDAO().executeUpdate("delete from pub_sysinittemp where initcode = 'FICOMMON02'");

		// 二、删除61系统预置菜单，备份61非系统预置菜单项
		do61MenuDelete();

		// 三、删除61原有的应用资产
		getBaseDAO().executeUpdate("delete from aam_appasset where pk_module = '2011' and assetlayer = 0");
		
		//四、删除借款预警中的模板
		getBaseDAO().executeUpdate("delete from pub_print_cell where ctemplateid in " +
				"(select ctemplateid from pub_print_template where pub_print_template.vtemplatecode = 'ermprealarm' ) ");
		
		//四、删除预算控制策略
		getBaseDAO().executeUpdate("delete from ntb_id_ctrltactics  where sys_code = 'erm' ");

		Logger.debug("*************************************");
		Logger.debug("******** 报销管理模块升级6.1升级到6.3开始更新信息结束" + getClass().getName() + "**********");
		Logger.debug("*************************************");
	}

	/**
	 * 更新借款报销单中表头关联到表体的字段默认值设置（例如项目，项目任务，核算要素等）
	 * 
	 * @param jkList
	 * @param bxList
	 * @throws Exception
	 */
	private void updateJkBxInfo(Collection<JKBXVO> jkList, Collection<JKBXVO> bxList) throws Exception {
		String updateBxPayOrgSql = "update er_bxzb set pk_payorg = isnull(pk_payorg,pk_org), pk_payorg_v = isnull(pk_payorg_v,pk_org_v)";
		getBaseDAO().executeUpdate(updateBxPayOrgSql);
		Logger.debug(updateBxPayOrgSql);

		String updateJkPayOrgSql = "update er_jkzb set pk_payorg = isnull(pk_payorg,pk_org), pk_payorg_v = isnull(pk_payorg_v,pk_org_v)";
		getBaseDAO().executeUpdate(updateJkPayOrgSql);
		Logger.debug(updateJkPayOrgSql);

		StringBuffer updateBusitemSql = new StringBuffer();
		updateBusitemSql.append("update er_busitem set pk_resacostcenter = ?, pk_checkele = ?,").append(
				"pk_pcorg = ? , pk_pcorg_v = ?,jobid = ? ,projecttask = ? where pk_jkbx = ?");

		List<JKBXHeaderVO> jkbxList = new ArrayList<JKBXHeaderVO>();
		if (jkList != null) {
			for (JKBXVO jkVo : jkList) {
				jkbxList.add(jkVo.getParentVO());
			}
		}

		if (bxList != null) {
			for (JKBXVO bxVo : bxList) {
				jkbxList.add(bxVo.getParentVO());
			}
		}

		List<SQLParameter> parameterList = new ArrayList<SQLParameter>();
		for (JKBXHeaderVO head : jkbxList) {
			SQLParameter param = new SQLParameter();
			param.addParam(head.getPk_resacostcenter());
			param.addParam(head.getPk_checkele());
			param.addParam(head.getPk_payorg());
			param.addParam(head.getPk_payorg_v());

			param.addParam(head.getJobid());
			param.addParam(head.getProjecttask());
			param.addParam(head.getPk_jkbx());
			parameterList.add(param);
		}
		batchUpdate(updateBusitemSql.toString(), parameterList);
		Logger.debug(updateBusitemSql.toString());
	}

	/**
	 * 更新冲销信息（63中冲借款按照借款单业务行进行冲销，与61中整单冲销不同）
	 * 
	 * @param jkVoCollection
	 *            借款VO集合
	 * @param bxVoCollection
	 *            报销VO集合
	 * @param contrastVoList
	 *            冲借款VO集合
	 * @throws Exception
	 */
	private void updateContrastInfo(Collection<JKBXVO> jkVoCollection, Collection<JKBXVO> bxVoCollection,
			List<BxcontrastVO> contrastVoList) throws Exception {
		if (jkVoCollection == null || jkVoCollection.size() == 0 || contrastVoList == null
				|| contrastVoList.size() == 0) {
			return;
		}

		// <借款单pk：冲销行集合>
		Map<String, List<BxcontrastVO>> contrastMap = new HashMap<String, List<BxcontrastVO>>();

		List<BXBusItemVO> resultBusItemList = new ArrayList<BXBusItemVO>();
		List<BxcontrastVO> resultContrastList = new ArrayList<BxcontrastVO>();

		for (BxcontrastVO bxcontrastVO : contrastVoList) {
			List<BxcontrastVO> list = contrastMap.get(bxcontrastVO.getPk_jkd());
			if (list == null) {
				list = new ArrayList<BxcontrastVO>();
				contrastMap.put(bxcontrastVO.getPk_jkd(), list);
			}
			list.add(bxcontrastVO);
		}

		for (JKBXVO jkVo : jkVoCollection) {
			JKVO jkVoClone = (JKVO) jkVo.clone();

			JKBXHeaderVO headVo = jkVoClone.getParentVO();
			if (contrastMap.get(headVo.getPk_jkbx()) == null) {// 无冲销行则不需要处理
				continue;
			}

			BXBusItemVO[] busItems = jkVoClone.getChildrenVO();
			List<BxcontrastVO> contrastList = contrastMap.get(headVo.getPk_jkbx());

			int contrastIndex = 0;// 冲销行索引
			for (BXBusItemVO busItem : busItems) {
				busItem.setYjye(busItem.getYbje());
				busItem.setYbye(busItem.getYbje());
				busItem.setBbye(busItem.getBbje());
				busItem.setGroupbbye(busItem.getGroupbbje());
				busItem.setGlobalbbye(busItem.getGlobalbbje());

				resultBusItemList.add(busItem);

				if (contrastIndex >= contrastList.size()) {
					continue;
				}
				// 处理冲销行
				contrastIndex = dealContrastVos(busItem, contrastList, resultContrastList, headVo.getPk_payorg(),
						contrastIndex);
			}
		}

		// 设置费用原币金额
		setFyJeAndBbJe(resultContrastList, bxVoCollection);

		getBaseDAO().updateVOArray(
				resultBusItemList.toArray(new BXBusItemVO[] {}),
				new String[] { BXBusItemVO.YBYE, BXBusItemVO.YJYE, BXBusItemVO.BBYE, BXBusItemVO.GROUPBBYE,
						BXBusItemVO.GLOBALBBYE });

		// 删除所有冲销行
		getBaseDAO().deleteVOArray(contrastVoList.toArray(new BxcontrastVO[] {}));
		getBaseDAO().insertVOArray(resultContrastList.toArray(new BxcontrastVO[] {}));
	}

	private void setFyJeAndBbJe(List<BxcontrastVO> resultContrastList, Collection<JKBXVO> bxVos) {
		Map<String, List<BxcontrastVO>> bxContrastMap = new HashMap<String, List<BxcontrastVO>>();
		for (BxcontrastVO contrastVo : resultContrastList) {
			List<BxcontrastVO> list = bxContrastMap.get(contrastVo.getPk_jkd());
			if (list == null) {
				list = new ArrayList<BxcontrastVO>();
				bxContrastMap.put(contrastVo.getPk_bxd(), list);
			}
			list.add(contrastVo);
		}

		for (JKBXVO bxvo : bxVos) {
			JKBXHeaderVO parentVO = bxvo.getParentVO();
			UFDouble hkybJe = parentVO.getHkybje();
			List<BxcontrastVO> contrasVos = bxContrastMap.get(parentVO.getPk_jkbx());

			if (contrasVos == null || contrasVos.size() == 0) {
				continue;
			}
			for (BxcontrastVO contrastVo : contrasVos) {
				if (hkybJe == null || hkybJe.compareTo(UFDouble.ZERO_DBL) == 0) {
					contrastVo.setFyybje(contrastVo.getYbje());
				} else {
					UFDouble fyybje = UFDouble.ZERO_DBL;

					if (contrastVo.getYbje().compareTo(hkybJe) >= 0) {
						fyybje = contrastVo.getYbje().sub(hkybJe);
						hkybJe = UFDouble.ZERO_DBL;
					} else {
						fyybje = UFDouble.ZERO_DBL;
						hkybJe = hkybJe.sub(contrastVo.getYbje());
					}
					contrastVo.setFybbje(fyybje);
				}
			}

			// 设置本币金额
			ErContrastUtil.resetCjkJeToBB(parentVO.getBbhl(), parentVO.getGlobalbbhl(), parentVO.getGroupbbhl(),
					parentVO.getBzbm(), parentVO.getDjrq(), parentVO.getPk_org(), parentVO.getPk_group(), contrasVos);
		}
	}

	private int dealContrastVos(BXBusItemVO busItem, List<BxcontrastVO> contrastList,
			List<BxcontrastVO> resultContrastList, String payOrg, int contrastIndex) {
		int result = 0;
		for (int i = contrastIndex; i < contrastList.size(); i++) {
			BxcontrastVO bxcontrastVO = contrastList.get(i);

			if (busItem.getYjye().compareTo(bxcontrastVO.getYbje()) >= 0) {// 业务行金额大于冲销行金额
				busItem.setYjye(busItem.getYjye().sub(bxcontrastVO.getYbje()));// 预计余额设置
				if (bxcontrastVO.getSxbz() != null && bxcontrastVO.getSxbz() == BXStatusConst.SXBZ_VALID) {
					busItem.setYbye(busItem.getYjye());// 原币余额
					busItem.setBbye(busItem.getBbye().sub(bxcontrastVO.getBbje()));
					UFDouble groupbbye = busItem.getGroupbbye() == null ? UFDouble.ZERO_DBL : busItem.getGroupbbye();
					UFDouble globalbbye = busItem.getGlobalbbye() == null ? UFDouble.ZERO_DBL : busItem.getGlobalbbye();
					busItem.setGroupbbye(groupbbye.sub(bxcontrastVO.getGroupbbje()));
					busItem.setGlobalbbye(globalbbye.sub(bxcontrastVO.getGlobalbbje()));
				}

				// TODO 差一些字段未设置
				BxcontrastVO newContrastVo = (BxcontrastVO) bxcontrastVO.clone();
				newContrastVo.setPk_busitem(busItem.getPk_busitem());// 借款行
				newContrastVo.setPk_payorg(payOrg);// 支付单位
				newContrastVo.setPk_bxcontrast(null);
				resultContrastList.add(newContrastVo);
				result = i + 1;// 冲销行的索引
			} else {
				BxcontrastVO newContrastVo = (BxcontrastVO) bxcontrastVO.clone();
				busItem.setYjye(UFDouble.ZERO_DBL);
				if (bxcontrastVO.getSxbz() != null && bxcontrastVO.getSxbz() == BXStatusConst.SXBZ_VALID) {
					busItem.setYbye(UFDouble.ZERO_DBL);// 原币余额
					busItem.setBbye(UFDouble.ZERO_DBL);
					busItem.setGroupbbye(UFDouble.ZERO_DBL);
					busItem.setGlobalbbye(UFDouble.ZERO_DBL);
				}
				// 重置冲销行的金额
				bxcontrastVO.setYbje(bxcontrastVO.getYbje().sub(busItem.getYbje()));

				newContrastVo.setYbje(busItem.getYbje());// 金额
				newContrastVo.setCjkybje(busItem.getYbje());// 冲借款金额

				// TODO 差一些字段未设置
				newContrastVo.setPk_busitem(busItem.getPk_busitem());// 借款行
				newContrastVo.setPk_payorg(payOrg);// 支付单位
				newContrastVo.setPk_bxcontrast(null);
				resultContrastList.add(newContrastVo);
				result = i;// 冲销行的索引
			}

			if (busItem.getYjye().equals(UFDouble.ZERO_DBL)) {
				break;
			}
		}
		return result;
	}

	@Override
	public void doBeforeUpdateData(String oldVersion, String newVersion) throws Exception {

		if (is61UpdateTo63(oldVersion, newVersion)) {
			// 61升级到63时进行升级处理
			doBeforeUpdateData();
		}
	}

	private boolean is61UpdateTo63(String oldVersion, String newVersion) {
		return oldVersion != null && oldVersion.startsWith("6.1") && newVersion != null && newVersion.startsWith("6.3");
	}

	@Override
	public void doBeforeUpdateDB(String oldVersion, String newVersion) throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * 63对比61新增的功能节点
	 * 
	 * 费用申请控制规则设置 20110MCS 分摊规则设置-组织 20110SR 分摊规则设置-集团 20110SRG 分摊结转单据对应设置-集团
	 * 20110BCL 分摊结转单据对应设置-组织 20110ABC 费用申请单 201102611 费用申请单管理 20110MTAMN 费用结转
	 * 201105CSMG 待摊费用摊销 201105EXPMG 结账 20110EOCA 部门费用汇总表 2011050402 成本中心汇总表
	 * 2011050403 费用单位汇总表 2011050404 部门费用明细账 2011050502 成本中心费用明细账 2011050503
	 * 单位费用明细账 2011050504 部门费用申请执行情况查询 2011050701 申请人费用申请执行情况查询 2011050702
	 * 成本中心费用请执行情况查询 2011050703
	 */

	@Override
	public void doAfterUpdateData(String oldVersion, String newVersion) throws Exception {
		if (!is61UpdateTo63(oldVersion, newVersion)) {
			// 非61升级到63不处理
			return;
		}

		// 一、将新增的节点默认模板分配，分配到集团
		doAssignTemplate();

		// 二、菜单变更
		doUpdateMenu();

		// 三、删除临时表
		doDropTempTable();

		// 四、处理用户自定义交易类型功能节点
		dealUserDefFun();

		// 五、会计平台新增预置脚本升级
		updateFipInfo();

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
			initDataToGroup(pkgroups);
		}

		// 七、常用单据。支付单位默认值设置，默认与借款报销单位相同。
		String updateInitSql = "update er_jkbx_init set pk_payorg = pk_org, pk_payorg_v = pk_org_v";
		getBaseDAO().executeUpdate(updateInitSql);
		Logger.debug(updateInitSql);

		// 八、借款报销单。支付单位默认值设置，默认与借款报销单位相同；业务行新增维度默认值设置，默认与主表对应字段值相同
		List<JKBXVO> jkVoCollection = getQueryService().queryVOsByWhereSql(" where dr = 0 ", BXConstans.JK_DJDL);
		List<JKBXVO> bxVoCollection = getQueryService().queryVOsByWhereSql(" where dr = 0 ", BXConstans.BX_DJDL);

		updateJkBxInfo(jkVoCollection, bxVoCollection);

		// 九、冲销行。原针对借款单整单的冲销改造为按照借款单的业务行冲销，按借款单业务行pk顺序抵消。
		if (jkVoCollection != null && bxVoCollection != null) {
			List<BxcontrastVO> contrastVoList = new ArrayList<BxcontrastVO>();
			for (JKBXVO bxVo : bxVoCollection) {// 获取所有冲销行
				BxcontrastVO[] contrasts = bxVo.getContrastVO();
				if (contrasts != null && contrasts.length > 0) {
					for (BxcontrastVO bxcontrastVO : contrasts) {
						contrastVoList.add(bxcontrastVO);
					}
				}
			}

			// 更新冲销信息
			updateContrastInfo(jkVoCollection, bxVoCollection, contrastVoList);
		}

		// 十、借款报销单表体行交通工具字段脚本升级
		updateVehicleField();

		// 十一、升级之前的费用账同步到费用账表
		insertVOsExpAccVO();

		// 十二、预算脚本升级
		updateBudgetData();

		// 十三、参数升级
		updateInitParm();
		
		// 十四、参数删除
		deleteInitParm();
		
		// 十五、业务插件删除
        deleteBusinessPlugin();
        
        // 十六、"委托办理"动作升级
        updateBillAction();
	}
	
	@SuppressWarnings("unchecked")
	private void updateBillAction() throws BusinessException {
		String[] delActionType = new String[]{"TRANSFERFTS"};
		String[] addActionType = new String[]{"TRANSFERFTS"};
		String sql = "select distinct pk_billtypecode from bd_billtype where systemcode='erm' and ncbrcode='bx' and istransaction='Y'";
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
	
	private void deleteBusinessPlugin() throws DAOException {
	    String sqlER1 = "delete from PUB_EVENTLISTENER where IMPLCLASSNAME = 'nc.vo.erm.closeacc.ErmGLCloseAccListener' and owner = '2011'";

	    Logger.debug("费用-总账关账校验业务插件删除：" + sqlER1);
	    getBaseDAO().executeUpdate(sqlER1);
	}

	private void updateInitParm() throws DAOException {
		String sqlER1 = "update pub_sysinit set value =  "
				+ "(case when value = '借款报销人' then '2'  when value='录入人' then '1' else value end)  " /*-=notranslate=-*/
				+ " where INITCODE = 'ER1'";

		Logger.debug("费用参数升级：" + sqlER1);
		getBaseDAO().executeUpdate(sqlER1);

		String sqlER8 = "update pub_sysinit set value = (case when value = '报销人单位' then '1'  " /*-=notranslate=-*/
				+ "when value='费用承担单位' then '2' when value='报销单位' then '3' else value end) " /*-=notranslate=-*/
				+ " where INITCODE = 'ER8'";
		Logger.debug("费用参数升级：" + sqlER8);
		getBaseDAO().executeUpdate(sqlER8);

		String sqlERY = "update pub_sysinit set value = (case when value = '费用承担单位' then '1'  " /*-=notranslate=-*/
				+ "when value='借款报销单位' then '2' when value='借款报销人单位' then '3' when value='支付单位' then '4' " /*-=notranslate=-*/
				+ "else value end)  where INITCODE = 'ERY'";
		Logger.debug("费用参数升级：" + sqlERY);
		getBaseDAO().executeUpdate(sqlERY);
		
		String sqlER9 = "update pub_sysinit set value = (case when value = '不检查' then '1'  " /*-=notranslate=-*/
			+ "when value='检查但不控制' then '2' when value='检查并且控制' then '3' else value end) " /*-=notranslate=-*/
			+ " where INITCODE = 'ER9'";
		Logger.debug("费用参数升级：" + sqlER9);
		getBaseDAO().executeUpdate(sqlER9);
		
	}
	
	private void deleteInitParm() throws DAOException {
	    String sqlEr10 = "DELETE FROM PUB_SYSINITTEMP WHERE PK_SYSINITTEMP = '0001Z362002560632514'"; 
	    Logger.debug("费用参数删除：" + sqlEr10);
	    getBaseDAO().executeUpdate(sqlEr10);
	}

	private void updateBudgetData() throws BusinessException {
		boolean isInstallTBB = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (isInstallTBB) {
			Map<String, String> ermAttToNew = new HashMap<String, String>();
			ermAttToNew.put("zb.pk_pcorg", "pk_pcorg");
			ermAttToNew.put("zb.pk_resacostcenter", "pk_resacostcenter");
			ermAttToNew.put("zb.deptid", "operator_dept");
			ermAttToNew.put("zb.fydeptid", "assume_dept");
			ermAttToNew.put("zb.fydwbm", "assume_org");
			ermAttToNew.put("zb.dwbm", "operator_org");
			ermAttToNew.put("zb.hbbm", "pk_supplier");
			ermAttToNew.put("zb.jobid", "pk_project");
			ermAttToNew.put("zb.customer", "pk_customer");
			ermAttToNew.put("fb.szxmid", "pk_iobsclass");
			ermAttToNew.put("zb.bzbm", "pk_currtype");
			ermAttToNew.put("zb.pk_org", "pk_org");
			ermAttToNew.put("zb.cashproj", "cashproj");
			IRuleUpgradeToV63 upgrader = NCLocator.getInstance().lookup(IRuleUpgradeToV63.class);
			upgrader.upgradeRulesForAttChanged(ermAttToNew, "erm");
		}
	}

	private void insertVOsExpAccVO() throws MetaDataException, BusinessException {
		// 先删除后插入
		getBaseDAO().executeUpdate("delete from " + ExpenseAccountVO.getDefaultTableName());
		getBaseDAO().executeUpdate("delete from " + ExpenseBalVO.getDefaultTableName());

		@SuppressWarnings("unchecked")
		List<JKBXVO> vos = (List<JKBXVO>) MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(
				BXVO.class, "dr = 0", false);
		ExpenseAccountVO[] expaccvo = ErmBillCostConver.getExpAccVOS(vos.toArray(new JKBXVO[0]));
		NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).insertVOs(expaccvo);
	}

	/**
	 * 借款报销单表体交通工具字段脚本升级
	 * 
	 * @throws DAOException
	 */
	private void updateVehicleField() throws DAOException {
		// 更新借款单表体行defitem4字段（差旅费借款单）
		String jksql = "update er_busitem  set defitem4 = case defitem4 " + "when '其他' then '0' " /*-=notranslate=-*/
				+ "when '城铁\\地铁' then '1' " + "when '商务车' then '2' " + "when '公交车' then '3' " + "when '出租车' then '4' " /*-=notranslate=-*/
				+ "when '长途汽车' then '5' " + "when '火车' then '6' " + "when '飞机' then '7'" + "else defitem4 " + "end " /*-=notranslate=-*/
				+ "where pk_jkbx in ( select pk_jkbx from er_jkzb where djlxbm in ('2631') )"; /*-=notranslate=-*/
		getBaseDAO().executeUpdate(jksql);
		// 更新报销单表体行defitem5字段（差旅费报销单、交通费报销单）
		String bxsql = "update er_busitem  set defitem5 = case defitem5 " + "when '其他' then '0' " /*-=notranslate=-*/
				+ "when '城铁\\地铁' then '1' " + "when '商务车' then '2' " + "when '公交车' then '3' " + "when '出租车' then '4' " /*-=notranslate=-*/
				+ "when '长途汽车' then '5' " + "when '火车' then '6' " + "when '飞机' then '7'" + "else defitem5 " + "end " /*-=notranslate=-*/
				+ "where pk_jkbx in ( select pk_jkbx from er_bxzb where djlxbm in ('2641','2642') )"; /*-=notranslate=-*/
		getBaseDAO().executeUpdate(bxsql);
	}

	@SuppressWarnings("unchecked")
	private void dealUserDefFun() throws Exception {
		// 1.先查出所有自定义功能节点
		Collection<ParamRegVO> c = getBaseDAO()
				.retrieveByClause(
						ParamRegVO.class,
						"PARENTID IN ( SELECT cfunid FROM SM_FUNCREGISTER WHERE OWN_MODULE = '2011') and PARAMNAME = 'transtype' and PARAMVALUE not in ('2611','2631','2632','2641','2642','2643','2644','2645','2646','2647')",
						"PARENTID");

		if (c == null || c.size() == 0) {
			return;
		}

		// 2.新增参数注册
		List<ParamRegVO> newParamRegVOList = new ArrayList<ParamRegVO>();
		List<String> pk_funRegister = new ArrayList<String>();
		for (ParamRegVO ParamRegVO : c) {
			pk_funRegister.add(ParamRegVO.getParentid());
			ParamRegVO newParamRegVO = new ParamRegVO();
			newParamRegVO.setParentid(ParamRegVO.getParentid());
			if (ParamRegVO.getParamvalue().startsWith("264X")) {
				newParamRegVO.setParamname("BeanConfigFilePath");
				newParamRegVO.setParamvalue("nc/ui/erm/billinput/billinput_bxconfig.xml");
			} else if (ParamRegVO.getParamvalue().startsWith("263X")) {
				newParamRegVO.setParamname("BeanConfigFilePath");
				newParamRegVO.setParamvalue("nc/ui/erm/billinput/billinput_jkconfig.xml");
			}
			newParamRegVOList.add(newParamRegVO);
		}

		// 先删后插
		getBaseDAO().deleteByClause(ParamRegVO.class,
				SqlUtils.getInStr("PARENTID", pk_funRegister, true) + " and PARAMNAME = 'BeanConfigFilePath'");
		getBaseDAO().insertVOArray(newParamRegVOList.toArray(new ParamRegVO[0]));
		// 3.更新功能节点注册的设置
		String condition = SqlUtils.getInStr(FuncRegisterVO.CFUNID, pk_funRegister.toArray(new String[0]), false);
		Collection<FuncRegisterVO> f = getBaseDAO().retrieveByClause(FuncRegisterVO.class, condition);
		List<FuncRegisterVO> newFuncRegisterList = new ArrayList<FuncRegisterVO>();
		for (FuncRegisterVO funRegisterVO : f) {
			newFuncRegisterList.add(funRegisterVO);
			funRegisterVO.setClass_name("nc.ui.erm.view.ErmToftPanel");
		}
		getBaseDAO().updateVOArray(newFuncRegisterList.toArray(new FuncRegisterVO[newFuncRegisterList.size()]),
				new String[] { FuncRegisterVO.CLASS_NAME });

		// 4.更新功能节点模板分配
		// String sql1 =
		// "update  pub_systemplate_base set nodekey='bill' where funnode in " +
		// "(select funcode from SM_FUNCREGISTER where "+condition+") and tempstyle='0' "
		// +
		// "and nodekey is not null";
		//
		// getBaseDAO().executeUpdate(sql1);

		// 5.更新模板的业务页签元数据路径
		String sql2 = "update PUB_BILLTEMPLET_T set Metadataclass='erm.er_busitem', Metadatapath='er_busitem'   where  PK_BILLTEMPLET  in ( select PK_BILLTEMPLET from PUB_BILLTEMPLET where METADATACLASS='erm.bxzb') and Metadataclass='erm.busitem'";
		getBaseDAO().executeUpdate(sql2);
		
		// 6.更新模板的冲销页签元数据路径
		String sql3 = "update PUB_BILLTEMPLET_T set Metadataclass='erm.contrast', Metadatapath='er_bxcontrast'   where  PK_BILLTEMPLET  in ( select PK_BILLTEMPLET from PUB_BILLTEMPLET where METADATACLASS='erm.bxzb') and Metadataclass='erm.contrast'";
		getBaseDAO().executeUpdate(sql3);
		
		// 7.将模板的选中设置为‘N’
		String sql4 = "update pub_billtemplet_b set listshowflag='N' where  PK_BILLTEMPLET  in ( select PK_BILLTEMPLET from PUB_BILLTEMPLET where METADATACLASS in ('erm.bxzb','erm.jkzb') and PK_BILLTYPECODE<>'CJK63') and itemkey='selected'";
		getBaseDAO().executeUpdate(sql4);
		
		// 8.设置模板合计行为‘1’
		String sql5= "update pub_billtemplet_b set totalflag='1' where  PK_BILLTEMPLET  in ( select PK_BILLTEMPLET from PUB_BILLTEMPLET where METADATACLASS in ('erm.bxzb','erm.jkzb') and PK_BILLTYPECODE<>'CJK63') and  itemkey in ('ybje', 'bbje', 'groupbbje', 'groupzfbbje', 'globalbbje','globalzfbbje')  and pos = '1'";
		getBaseDAO().executeUpdate(sql5);
	}

	private void doDropTempTable() {
		dropTables(new String[] { "TMP_ERM_EXPBALANCE1", "TMP_ERM_EXPDETAIL1" });
	}

	private void dropTables(String[] tempTables) {
		if (tempTables != null && tempTables.length > 0) {
			Connection con = null;
			try {
				con = ConnectionFactory.getConnection();
				TempTable tt = new TempTable();
				for (String tablename : tempTables) {
					try {
						tt.dropTempTable(con, tablename);
					} catch (SQLException e) {
						Logger.error("", e);
					}
				}
			} catch (SQLException e) {
				Logger.error("", e);
			} finally {
				try {
					if (con != null)
						con.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 删除61系统预置菜单，备份61非系统预置菜单项 附：恢复原61数据的sql（回滚sql）------ update sm_menuitemreg
	 * set menuitemcode =
	 * substring(sm_menuitemreg.menuitemcode,6,len(sm_menuitemreg.menuitemcode)
	 * -5), pk_menuitem =replace(pk_menuitem,'@@','Z3')where (menuitemcode like
	 * '@@@@_2011%') and pk_menu in (select pk_menu from sm_menuregister where
	 * isdefault='Y')
	 * 
	 * @throws DAOException
	 */
	private void do61MenuDelete() throws DAOException {
		@SuppressWarnings("rawtypes")
		List c = (List) getBaseDAO().executeQuery(
				"select menuitemcode from sm_menuitemreg " + "where (menuitemcode = '201111') "
						+ "and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y')",
				new ColumnListProcessor());
		if (c.isEmpty()) {
			// 防止升级过程出错，原更新的不回滚，用61的菜单项201111判断是否菜单已经升级过
			return;
		}
		// 更新费用菜单项的编码为 @@@@_菜单项目编码,PK更新为原菜单项目编码避免63数据新增时pk冲突，数据备份
		String updatesql = "update sm_menuitemreg set menuitemcode = '@@@@_'||menuitemcode ,pk_menuitem = replace(pk_menuitem,'Z3','@@') "
				+ "where (menuitemcode like '2011%') and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y')";
		getBaseDAO().executeUpdate(updatesql);

	}

	/**
	 * 
	 * 61预置的系统级节点
	 * 
	 * 2011 报销管理 201100 初始设置 20110002 常用单据设置-集团 20110004 常用单据设置-组织 20110006
	 * 借款控制设置-集团 20110008 借款控制设置-组织 20110010 授权代理设置 20110012 个人授权设置-集团 20110014
	 * 费用类型设置 20110016 报销类型设置 20110018 报销标准设置 20110024 账表初始化 201101 期初单据 201102
	 * 单据处理 20110202 单据管理 20110204 单据查询 201103 单据录入 20110301 差旅费借款单 20110302
	 * 会议费借款单 20110303 差旅费报销单 20110304 交通费报销单 20110305 通讯费报销单 20110306 礼品费报销单
	 * 20110307 招待费报销单 20110308 会议费报销单 20110309 还款单 201106 综合处理 20110602 期末处理
	 * 2011060202 关账 2011060206 批量关账 201109 查询 20110903 借款明细查询 2011090301
	 * 借款人借款明细账 20110904 借款余额查询 2011090401 借款人借款余额表 20110905 费用明细查询 2011090501
	 * 报销人费用明细账 20110906 费用汇总查询 2011090601 报销人费用汇总表 20110907 借款账龄查询 2011090701
	 * 借款人借款账龄分析 2011090701 借款人借款账龄查询 201111 报表 20111101 借款账龄分析表 2011110101
	 * 借款人借款账龄分析
	 * 
	 * 63菜单项目分类 与 61的编码对应关系
	 * 
	 * 2011 费用管理 201100 初始设置 201100 初始设置 201101 费用申请 20110102 费用申请单录入 201102
	 * 报销管理 2011 报销管理 20110202 单据录入 201103 单据录入 201103 费用核算 201104 期末处理 20110602
	 * 期末处理 201105 查询 201109 查询 20110501 借款余额查询 20110904 借款余额查询 20110502 借款明细查询
	 * 20110903 借款明细查询 20110503 借款账龄分析查询 20110907 借款账龄查询 20110504 费用汇总查询
	 * 20110906 费用汇总查询 20110505 费用明细查询 20110905 费用明细查询 20110506 费用趋势图分析 20110507
	 * 费用申请单执行情况查询 201106 报表 201111 报表 20110601 借款账龄分析表 20111101 借款账龄分析表 201102
	 * 单据处理 201106 综合处理
	 * 
	 * @throws DAOException
	 * 
	 * 
	 */

	private void doUpdateMenu() throws DAOException {
		@SuppressWarnings("rawtypes")
		List templist = (List) getBaseDAO().executeQuery(
				"select menuitemcode from sm_menuitemreg " + "where (menuitemcode = '@@@@_201111') "
						+ "and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y')",
				new ColumnListProcessor());
		if (templist.isEmpty()) {
			// 防止升级过程出错，原更新的不回滚，用61的菜单项201111判断是否菜单已经升级过
			return;
		}
		// 61原系统级菜单项
		String[] menuItems61 = new String[] { "2011", "201100", "20110002", "20110004", "20110006", "20110008",
				"20110010", "20110012", "20110014", "20110016", "20110018", "20110024", "201101", "201102", "20110202",
				"20110204", "201103", "20110301", "20110302", "20110303", "20110304", "20110305", "20110306",
				"20110307", "20110308", "20110309", "201106", "20110602", "2011060202", "2011060206", "201109",
				"20110903", "2011090301", "20110904", "2011090401", "20110905", "2011090501", "20110906", "2011090601",
				"20110907", "2011090701", "2011090701", "201111", "20111101", "2011110101" };
		List<String> menuItem61List = Arrays.asList(menuItems61);
		// 获得61菜单项目分类对应63的
		Map<String, String> codemap = new HashMap<String, String>();
		codemap.put("201100", "201100");
		codemap.put("2011", "2011"); // 报销管理-费用管理
		codemap.put("201103", "20110202");
		codemap.put("20110602", "201104");
		codemap.put("201109", "201105");
		codemap.put("20110904", "20110501");
		codemap.put("20110903", "20110502");
		codemap.put("20110907", "20110503");
		codemap.put("20110906", "20110504");
		codemap.put("20110905", "20110505");
		codemap.put("201111", "201106");
		codemap.put("20111101", "20110601");
		codemap.put("201102", "201102");// 单据处理-报销管理
		codemap.put("201106", "201102");// 综合处理-报销管理

		// 1、查询61的菜单,按菜单项目编号排序
		@SuppressWarnings("unchecked")
		Collection<MenuItemVO> c = getBaseDAO()
				.retrieveByClause(
						MenuItemVO.class,
						"(menuitemcode like '@@@@_2011%') and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y')",
						"menuitemcode");

		// 2、遍历61菜单项，对于自定义菜单进行重新编码
		Map<String, Integer> newchildCount = new HashMap<String, Integer>();// 父节点的新孩子个数
		List<String> errCodes = new ArrayList<String>(); // 不能删除的错误菜单项
		List<MenuItemVO> newMenuItems = new ArrayList<MenuItemVO>();// 待新增的菜单项
		for (MenuItemVO menuItemVO : c) {
			String code = menuItemVO.getMenuitemcode().split("_")[1];
			if (menuItem61List.contains(code)) {
				// 系统预置的菜单编码，跳过
				continue;
			}
			// 获得自定义菜单项目的父编码，截去后两位
			String parentcode = code.substring(0, code.length() - 2);
			String parent63 = codemap.get(parentcode);
			if (parent63 == null) {
				// 标记备份菜单项目待删除
				errCodes.add(menuItemVO.getMenuitemcode());
				ExceptionHandler.error("61升级到63，菜单升级错误，菜单编码--------" /*-=notranslate=-*/ + code);
				continue;
			}

			// 设置新的编号,从a0开始
			Integer count = newchildCount.get(parent63);
			if (count == null) {
				count = 1;
				menuItemVO.setMenuitemcode(parent63 + "a0");
			} else {
				menuItemVO.setMenuitemcode(parent63 + Integer.toHexString((Integer.parseInt("a0", 16) + count)));
				count = count + 1;
			}
			newchildCount.put(parent63, count);

			// 标记待新增的菜单项目VO
			menuItemVO.setPk_menuitem(null);
			menuItemVO.setResid("D" + menuItemVO.getMenuitemcode());
			newMenuItems.add(menuItemVO);

			// 如果菜单项目是菜单分类，则也加入到codemap中待用
			if (menuItemVO.getIsmenutype().booleanValue()) {
				codemap.put(code, menuItemVO.getMenuitemcode());
			}
		}
		// 3、删除备份的61菜单数据，错误的菜单项目备份不删除
		String delSql = "(menuitemcode like '@@@@_2011%') and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y')";
		if (!errCodes.isEmpty()) {
			delSql += " and menuitemcode not in (" + StringUtil.toString(errCodes, ",", "'", "'") + ")";
		}
		getBaseDAO().deleteByClause(MenuItemVO.class, delSql);
		// 4、新增新的菜单项目
		if (!newMenuItems.isEmpty()) {
			getBaseDAO().insertVOArray(newMenuItems.toArray(new MenuItemVO[0]));
		}
	}

	private void doAssignTemplate() throws BusinessException, DAOException {
		// 查询费用管理下的全部功能节点
		IFuncRegisterQueryService funcService = NCLocator.getInstance().lookup(IFuncRegisterQueryService.class);
		FuncRegisterVO[] vos = funcService.queryFunctionWhere(" own_module =  '2011' and isenable = 'Y' ");
		Map<String, FuncRegisterVO> funcMap = new HashMap<String, FuncRegisterVO>();
		for (int i = 0; i < vos.length; i++) {
			funcMap.put(vos[i].getFuncode(), vos[i]);
		}
		// 查询节点下的默认模板，且按照功能节点进行分组
		String funcodeInSql = SqlUtils.getInStr("funnode", funcMap.keySet().toArray(new String[0]), false);
		@SuppressWarnings("unchecked")
		Collection<SystemplateBaseVO> c = getBaseDAO().retrieveByClause(SystemplateBaseVO.class, funcodeInSql);
		@SuppressWarnings("unchecked")
		HashMap<String, List<SystemplateBaseVO>> tempMap = Hashlize.hashlizeObjects(
				c.toArray(new SystemplateBaseVO[0]), new IHashKey() {

					@Override
					public String getKey(Object arg0) {
						return ((SystemplateBaseVO) arg0).getFunnode();
					}
				});
		// 更新非系统预置的模板分配的templateflag标记位，防止增补集团时丢失
		
		String selectSql = " " + funcodeInSql + " and  templateflag = 'Y' "
				+ " and pk_systemplate not in (select pub_systemplate.pk_systemplate "
				+ " from pub_systemplate ,pub_systemplate_base where "
				+ " pub_systemplate.funnode = pub_systemplate_base.funnode and "
				+ " isnull(pub_systemplate.nodekey,'~') = isnull(pub_systemplate_base.nodekey,'~') "
				+ " and pub_systemplate.templateid = pub_systemplate_base.templateid "
				+ " and pub_systemplate.moduleid = '2011')";

		@SuppressWarnings("unchecked")
        Collection<SystemplateVO> templateVos = getBaseDAO().retrieveByClause(SystemplateVO.class, selectSql);

		if (templateVos != null) {
			for (SystemplateVO vo : templateVos) {
				vo.setTemplateflag(UFBoolean.FALSE);
			}

			getBaseDAO().updateVOArray(templateVos.toArray(new SystemplateVO[] {}));
		}

		// 将各个节点的模板分配到增补到集团
		for (Entry<String, FuncRegisterVO> funcs : funcMap.entrySet()) {
			String funcode = funcs.getKey();
			FuncRegisterVO frvo = funcs.getValue();
			List<SystemplateBaseVO> templist = tempMap.get(funcode);
			if (frvo == null || templist == null || templist.isEmpty()) {
				continue;
			}
			ISystemTemplateAssignService systemService = NCLocator.getInstance().lookup(
					ISystemTemplateAssignService.class);
			systemService.doAssign4Group(frvo, templist.toArray(new SystemplateBaseVO[0]));
		}

		if (templateVos != null) {
			for (SystemplateVO vo : templateVos) {
				vo.setTemplateflag(UFBoolean.TRUE);
			}

			getBaseDAO().updateVOArray(templateVos.toArray(new SystemplateVO[] {}));
		}
	}

	/**
	 * 会计平台新增预置脚本升级
	 * 
	 * @throws BusinessException
	 */
	private void updateFipInfo() throws BusinessException {
		NCLocator.getInstance().lookup(IFipInitDataService.class).initData(BXConstans.ERM_PRODUCT_CODE);
	}

	private void insertDjlx(String[] pk_groups) throws BusinessException {
		// 先删除，后插入，防止升级过程出错重复插入

		String delSql = "delete from er_djlx where djdl in ('at','ma','cs')  and "
				+ SqlUtils.getInStr("pk_group", pk_groups, true);
		getBaseDAO().executeUpdate(delSql);

		DjLXVO[] sourcevos = NCLocator.getInstance().lookup(IArapBillTypePublic.class)
				.queryByWhereStr(" pk_group='@@@@'" + " and djdl in ('at','ma','cs') ");

		List<DjLXVO> result = new ArrayList<DjLXVO>();
		// 增加对应集团的交易类型
		for (int i = 0; i < pk_groups.length; i++) {
			//63升级程序没有处理，这里加入
			boolean isErmInstall = ErUtil.isProductInstalled(pk_groups[i], BXConstans.ERM_MODULEID);
			if(isErmInstall){
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

	private void initDataToGroup(String[] pk_groups) throws BusinessException {
		insertDjlx(pk_groups);// 单据类型
		// 维度对照、单据对照
		NCLocator.getInstance().lookup(IErmGroupPredataService.class).initGroupData(pk_groups);
		
		// 处理单据项目成本中心、项目的升级
		String updateFipCenterSql = "update FIP_BILLFACTOR set ENTITY_ATTR = '$er_busitem.pk_resacostcenter'  where pk_systypecode = 'erm' "
				+ " and (PK_BILLTYPE like '264X%' or PK_BILLTYPE like '263X%') and pk_group != 'GLOBLE00000000000000' and ENTITY_ATTR = '@pk_resacostcenter'";
		getBaseDAO().executeUpdate(updateFipCenterSql);

		String updateFipJobSql = "update FIP_BILLFACTOR set ENTITY_ATTR = '$er_busitem.jobid'  where pk_systypecode = 'erm' "
				+ " and (PK_BILLTYPE like '264X%' or PK_BILLTYPE like '263X%') and pk_group != 'GLOBLE00000000000000' and ENTITY_ATTR = '@jobid'";
		getBaseDAO().executeUpdate(updateFipJobSql);

		String updateFipSzxmSql = "update FIP_BILLFACTOR set ENTITY_ATTR = '$er_busitem.szxmid'  where pk_systypecode = 'erm' "
				+ " and (PK_BILLTYPE like '264X%' or PK_BILLTYPE like '263X%') and pk_group != 'GLOBLE00000000000000' and ENTITY_ATTR = '#szxmid'";
		getBaseDAO().executeUpdate(updateFipSzxmSql);
	}
}
