package nc.impl.erm.common;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.logging.Logger;
import nc.bs.sm.accountmanage.AbstractPatchInstall;
import nc.bs.sm.accountmanage.PatchInstallContext;
import nc.md.persist.designer.service.IPublishService;
import nc.vo.pub.BusinessException;

/**
 * 
 * <p>
 * 631-63EHP2升级 补丁盘元数据升级，注册在setup.ini中
 * </p>
 * 
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li> <br>
 * <br>
 * 
 * @see
 * @author chenshuaia
 * @version V6.3
 * @since V6.3
 */
public class ErmMDUpdate extends AbstractPatchInstall {
	private IPublishService service;

	private BaseDAO dao = null;

	// 元数据如果相互关联，需注意先后顺序
	private String[] bmfpath = { "/modules/erm/METADATA/accruedexpense.bmf", "/modules/erm/METADATA/reimdimension.bmf",
			"/modules/erm/METADATA/reimrule.bmf", "/modules/erm/METADATA/ermcostaccount.bmf",
			"/modules/erm/METADATA/ermcostaccountbal.bmf", "/modules/erm/METADATA/matterappbill.bmf",
			"/modules/erm/METADATA/matterappbill_intermonth_ext.bmf", "/modules/erm/METADATA/expenseaccount.bmf",
			"/modules/erm/METADATA/loanmanage.bmf", "/modules/erm/METADATA/costsharebill.bmf",
			"/modules/erm/METADATA/expamortizeinfo.bmf", "/modules/erm/METADATA/bxbillmanage.bmf",
			"/modules/erm/METADATA/ermbilltype.bmf" };

	private String[] bpfpath = { "/modules/erm/METADATA/accruedoperate.bpf" };

	/**
	 * 发布元数据
	 * 
	 * @param context
	 * @throws BusinessException
	 */
	@Override
	public void pulishMetaData(PatchInstallContext context) throws BusinessException {
		String nchome = RuntimeEnv.getInstance().getNCHome();
		for (String path : this.bmfpath) {
			StringBuilder destPath = new StringBuilder(nchome);
			destPath.append(path);
			try {
				Logger.error("发布元数据：" + destPath.toString());
				getPublishService().publishMetaDataForBMF(destPath.toString());
			} catch (Exception ex) {
				Logger.error(ex.getMessage(), ex);
				throw new BusinessException(ex.getMessage(), ex);
			}
		}
		for (String path : this.bpfpath) {
			StringBuilder destPath = new StringBuilder(nchome);
			destPath.append(path);
			try {
				Logger.error("发布元数据操作：" + destPath.toString());
				getPublishService().publishMetaDataForBPF(destPath.toString());
			} catch (Exception ex) {
				Logger.error(ex.getMessage(), ex);
				throw new BusinessException(ex.getMessage(), ex);
			}
		}

		// pub_vochange_b表维护,申请单到借款报销单的vo对照
		updateVOChange_b();
		updateFct();
	}

	private void updateVOChange_b() throws BusinessException {
		String delsql = "delete from pub_vochange_b where  pk_vochange in (select pk_vochange from pub_vochange where src_billtype='261X' and pk_group='~' and dest_billtype in ('263X','264X'))";
		executeSql(getBaseDAO(), delsql);
		Logger.debug(delsql);
		
		String[] insertsqls = new String[]{
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('bzbm', 0, '1001Z31000000000UWO1', '1001Z310000000000TBM', 'pk_currtype', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jk_busitem.srctype', 0, '1001Z31000000000UWO1', '1001Z310000000000TBN', 'pk_billtype', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('srctype', 0, '1001Z31000000000UWO1', '1001Z310000000000TBO', 'pk_billtype', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jk_busitem.pk_proline', 0, '1001Z31000000000UWO1', '1001Z310000000000TBP', 'mtapp_detail.pk_proline', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('pk_proline', 0, '1001Z31000000000UWO1', '1001Z310000000000TBQ', 'mtapp_detail.pk_proline', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jk_busitem.pk_resacostcenter', 0, '1001Z31000000000UWO1', '1001Z310000000000TBR', 'mtapp_detail.pk_resacostcenter', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('fydeptid', 0, '1001Z31000000000UWO1', '1001Z310000000000TBS', 'mtapp_detail.assume_dept', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('fydwbm', 0, '1001Z31000000000UWO1', '1001Z310000000000TBT', 'mtapp_detail.assume_org', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('hbbm', 0, '1001Z31000000000UWO1', '1001Z310000000000TBU', 'mtapp_detail.pk_supplier', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jk_busitem.pk_checkele', 0, '1001Z31000000000UWO1', '1001Z310000000000TBV', 'mtapp_detail.pk_checkele', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jk_busitem.amount', 0, '1001Z31000000000UWO1', '1001Z310000000000TBW', 'mtapp_detail.usable_amout', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jk_busitem.ybje', 0, '1001Z31000000000UWO1', '1001Z310000000000TBX', 'mtapp_detail.usable_amout', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('customer', 0, '1001Z31000000000UWO1', '1001Z310000000000TBY', 'mtapp_detail.pk_customer', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jk_busitem.pk_pcorg', 0, '1001Z31000000000UWO1', '1001Z310000000000TBZ', 'mtapp_detail.pk_pcorg', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jk_busitem.pk_brand', 0, '1001Z31000000000UWO1', '1001Z310000000000TC0', 'mtapp_detail.pk_brand', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('pk_brand', 0, '1001Z31000000000UWO1', '1001Z310000000000TC1', 'mtapp_detail.pk_brand', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jk_busitem.szxmid', 0, '1001Z31000000000UWO1', '1001Z310000000000TC2', 'mtapp_detail.pk_iobsclass', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jk_busitem.jobid', 0, '1001Z31000000000UWO1', '1001Z310000000000TC3', 'mtapp_detail.pk_project', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jk_busitem.projecttask', 0, '1001Z31000000000UWO1', '1001Z310000000000TC4', 'mtapp_detail.pk_wbs', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jk_busitem.pk_mtapp_detail', 0, '1001Z31000000000UWO1', '1001Z310000000000TC5', 'mtapp_detail.pk_mtapp_detail', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('center_dept', 0, '1001Z31000000000UWO1', '1001Z310000000000TC6', 'center_dept', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jk_busitem.srcbilltype', 0, '1001Z31000000000UWO1', '1001Z310000000000TC7', 'pk_tradetype', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('srcbilltype', 0, '1001Z31000000000UWO1', '1001Z310000000000TC8', 'pk_tradetype', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('deptid', 0, '1001Z31000000000UWO1', '1001Z310000000000TC9', 'apply_dept', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jkbxr', 0, '1001Z31000000000UWO1', '1001Z310000000000TCA', 'billmaker', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('receiver', 0, '1001Z31000000000UWO1', '1001Z310000000000TCB', 'billmaker', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('zy', 0, '1001Z31000000000UWO1', '1001Z310000000000TCC', 'reason', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('ismashare', 0, '1001Z31000000000UWO1', '1001Z310000000000TCD', 'iscostshare', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('dwbm', 0, '1001Z31000000000UWO1', '1001Z310000000000TCE', 'apply_org', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('pk_org', 0, '1001Z31000000000UWO1', '1001Z310000000000TCF', 'pk_org', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('pk_payorg', 0, '1001Z31000000000UWO1', '1001Z310000000000TCG', 'pk_org', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('pk_item', 0, '1001Z31000000000UWO1', '1001Z310000000000TCH', 'pk_mtapp_bill', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jk_busitem.pk_item', 0, '1001Z31000000000UWO1', '1001Z310000000000TCI', 'pk_mtapp_bill', 2, '2014-11-20 18:28:59')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.hbbm', 0, '1001Z31000000000UWO3', '1001Z310000000000TDL', 'mtapp_detail.pk_supplier', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.assume_amount', 0, '1001Z31000000000UWO3', '1001Z310000000000TDM', 'mtapp_detail.usable_amout', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.bzbm', 0, '1001Z31000000000UWO3', '1001Z310000000000TDN', 'pk_currtype', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.pk_org', 0, '1001Z31000000000UWO3', '1001Z310000000000TDO', 'pk_org', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.pk_group', 0, '1001Z31000000000UWO3', '1001Z310000000000TDP', 'pk_group', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.pk_item', 0, '1001Z31000000000UWO3', '1001Z310000000000TDQ', 'pk_mtapp_bill', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.pk_mtapp_detail', 0, '1001Z31000000000UWO3', '1001Z310000000000TDR', 'mtapp_detail.pk_mtapp_detail', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.pk_proline', 0, '1001Z31000000000UWO3', '1001Z310000000000TDS', 'mtapp_detail.pk_proline', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.pk_brand', 0, '1001Z31000000000UWO3', '1001Z310000000000TDT', 'mtapp_detail.pk_brand', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('pk_payorg', 0, '1001Z31000000000UWO3', '1001Z310000000000TDU', 'pk_org', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('pk_item', 0, '1001Z31000000000UWO3', '1001Z310000000000TDV', 'pk_mtapp_bill', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.customer', 0, '1001Z31000000000UWO3', '1001Z310000000000TDK', 'mtapp_detail.pk_customer', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('iscostshare', 0, '1001Z31000000000UWO3', '1001Z310000000000TCJ', 'Y', 1, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('pk_org', 0, '1001Z31000000000UWO3', '1001Z310000000000TCK', 'pk_org', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('pk_pcorg', 0, '1001Z31000000000UWO3', '1001Z310000000000TCL', 'mtapp_detail.pk_pcorg', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('fydwbm', 0, '1001Z31000000000UWO3', '1001Z31000000000CR4R', 'mtapp_detail.assume_org', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('dwbm', 0, '1001Z31000000000UWO3', '1001Z310000000000TCN', 'apply_org', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('deptid', 0, '1001Z31000000000UWO3', '1001Z310000000000TCO', 'apply_dept', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('receiver', 0, '1001Z31000000000UWO3', '1001Z310000000000TCP', 'billmaker', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('jkbxr', 0, '1001Z31000000000UWO3', '1001Z310000000000TCQ', 'billmaker', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('szxmid', 0, '1001Z31000000000UWO3', '1001Z310000000000TCR', 'mtapp_detail.pk_iobsclass', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('fydeptid', 0, '1001Z31000000000UWO3', '1001Z310000000000TCS', 'mtapp_detail.assume_dept', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('bzbm', 0, '1001Z31000000000UWO3', '1001Z310000000000TCT', 'pk_currtype', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('zy', 0, '1001Z31000000000UWO3', '1001Z310000000000TCU', 'reason', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('hbbm', 0, '1001Z31000000000UWO3', '1001Z310000000000TCV', 'mtapp_detail.pk_supplier', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('customer', 0, '1001Z31000000000UWO3', '1001Z310000000000TCW', 'mtapp_detail.pk_customer', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.amount', 0, '1001Z31000000000UWO3', '1001Z310000000000TCX', 'mtapp_detail.usable_amout', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.szxmid', 0, '1001Z31000000000UWO3', '1001Z310000000000TCY', 'mtapp_detail.pk_iobsclass', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.ybje', 0, '1001Z31000000000UWO3', '1001Z310000000000TCZ', 'mtapp_detail.usable_amout', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.pk_pcorg', 0, '1001Z31000000000UWO3', '1001Z310000000000TD0', 'mtapp_detail.pk_pcorg', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.pk_checkele', 0, '1001Z31000000000UWO3', '1001Z310000000000TD1', 'mtapp_detail.pk_checkele', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.jobid', 0, '1001Z31000000000UWO3', '1001Z310000000000TD2', 'mtapp_detail.pk_project', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.projecttask', 0, '1001Z31000000000UWO3', '1001Z310000000000TD3', 'mtapp_detail.pk_wbs', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.pk_resacostcenter', 0, '1001Z31000000000UWO3', '1001Z310000000000TD4', 'mtapp_detail.pk_resacostcenter', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.pk_item', 0, '1001Z31000000000UWO3', '1001Z310000000000TD5', 'pk_mtapp_bill', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.srcbilltype', 0, '1001Z31000000000UWO3', '1001Z310000000000TD6', 'pk_tradetype', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.srctype', 0, '1001Z31000000000UWO3', '1001Z310000000000TD7', 'pk_billtype', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.pk_mtapp_detail', 0, '1001Z31000000000UWO3', '1001Z310000000000TD8', 'mtapp_detail.pk_mtapp_detail', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.pk_proline', 0, '1001Z31000000000UWO3', '1001Z310000000000TD9', 'mtapp_detail.pk_proline', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.pk_brand', 0, '1001Z31000000000UWO3', '1001Z310000000000TDA', 'mtapp_detail.pk_brand', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('er_busitem.receiver', 0, '1001Z31000000000UWO3', '1001Z310000000000TDB', 'billmaker', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.assume_org', 0, '1001Z31000000000UWO3', '1001Z310000000000TDC', 'mtapp_detail.assume_org', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.assume_dept', 0, '1001Z31000000000UWO3', '1001Z310000000000TDD', 'mtapp_detail.assume_dept', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.pk_pcorg', 0, '1001Z31000000000UWO3', '1001Z310000000000TDE', 'mtapp_detail.pk_pcorg', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.pk_resacostcenter', 0, '1001Z31000000000UWO3', '1001Z310000000000TDF', 'mtapp_detail.pk_resacostcenter', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.pk_iobsclass', 0, '1001Z31000000000UWO3', '1001Z310000000000TDG', 'mtapp_detail.pk_iobsclass', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.jobid', 0, '1001Z31000000000UWO3', '1001Z310000000000TDH', 'mtapp_detail.pk_project', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.projecttask', 0, '1001Z31000000000UWO3', '1001Z310000000000TDI', 'mtapp_detail.pk_wbs', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('costsharedetail.pk_checkele', 0, '1001Z31000000000UWO3', '1001Z310000000000TDJ', 'mtapp_detail.pk_checkele', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('center_dept', 0, '1001Z31000000000UWO3', '1001Z310000000000TDW', 'center_dept', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('srcbilltype', 0, '1001Z31000000000UWO3', '1001Z310000000000TDX', 'pk_tradetype', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('srctype', 0, '1001Z31000000000UWO3', '1001Z310000000000TDY', 'pk_billtype', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('ismashare', 0, '1001Z31000000000UWO3', '1001Z310000000000TDZ', 'iscostshare', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('pk_proline', 0, '1001Z31000000000UWO3', '1001Z310000000000TE0', 'mtapp_detail.pk_proline', 2, '2014-11-20 18:29:35')",
				"insert into PUB_VOCHANGE_B (DEST_ATTR, DR, PK_VOCHANGE, PK_VOCHANGE_B, RULEDATA, RULETYPE, TS) values ('pk_brand', 0, '1001Z31000000000UWO3', '1001Z310000000000TE1', 'mtapp_detail.pk_brand', 2, '2014-11-20 18:29:35')"

		};
		for (String sql : insertsqls) {
			executeSql(getBaseDAO(), sql);
		}
	}

	/**
	 * 已安装收付款合同的情况下才执行。
	 * 
	 * @throws BusinessException
	 * @throws Exception
	 */
	private void updateFct() throws BusinessException {
		try {
			BaseDAO dao = new BaseDAO();
			dao.executeUpdate("update fct_ar set dr=dr");
		} catch (Exception e) {
			return;
		}
		String[] sqls = new String[] {
				"delete from md_property where id in ('32901b16-b984-43fb-9d9b-148ecabf5be9','78d1da6b-0af5-4ddb-b629-cf8df7e42b04')",
				"delete from md_db_relation where id in ('130c3ecb-5647-43da-bc5c-2571b0ed1a62','b5d73606-f408-411c-8e0a-c6aeb04c472d')",
				"insert into md_db_relation (asstype, createtime, creator, description, displayname, dr, endcardinality, endfieldid, endtableid, help, id, isforeignkey, modifier, modifytime, name, resid, startattrid, startcardinality, startfieldid, starttableid, ts, versiontype) values (3, null, null, null, 'jk_busitem_ct_ap', null, null, 'fct_ap@@PK@@', 'fct_ap', null, '130c3ecb-5647-43da-bc5c-2571b0ed1a62', 'N', null, null, 'jk_busitem_ct_ap', null, '32901b16-b984-43fb-9d9b-148ecabf5be9', null, 'er_busitem@@@fctno', 'er_busitem', '2014-11-15 10:00:11', 0)",
				"insert into md_db_relation (asstype, createtime, creator, description, displayname, dr, endcardinality, endfieldid, endtableid, help, id, isforeignkey, modifier, modifytime, name, resid, startattrid, startcardinality, startfieldid, starttableid, ts, versiontype) values (3, null, null, null, 'er_busitem_fct_ap', null, null, 'fct_ap@@PK@@', 'fct_ap', null, 'b5d73606-f408-411c-8e0a-c6aeb04c472d', 'N', null, null, 'er_busitem_fct_ap', null, '78d1da6b-0af5-4ddb-b629-cf8df7e42b04', null, 'er_busitem@@@fctno', 'er_busitem', '2014-11-15 10:00:10', 0)",
				"insert into md_property (accessorclassname, accesspower, accesspowergroup, attrlength, attrmaxvalue, attrminvalue, attrsequence, calculation, classid, createindustry, createtime, creator, customattr, datatype, datatypestyle, defaultvalue, description, displayname, dr, dynamicattr, dynamictable, fixedlength, help, hided, id, industry, isactive, isauthen, modifier, modifytime, name, notserialize, nullable, precise, readonly, refmodelname, resid, ts, versiontype, visibility) values (null, 'N', null, 20, null, null, 91, 'N', '6953ec6a-329c-4c09-b950-25b4af68e5c5', '0', null, null, 'N', 'af6a8e77-4fa3-4316-9d8d-12d24a9ff338', 305, null, null, '合同号', null, 'N', null, 'N', null, 'N', '32901b16-b984-43fb-9d9b-148ecabf5be9', '0', 'Y', null, null, null, 'fctno', 'N', 'Y', 0, 'N', '付款合同', '2UC000-000234', '2014-11-15 10:00:11', 0, 0)",
				"insert into md_property (accessorclassname, accesspower, accesspowergroup, attrlength, attrmaxvalue, attrminvalue, attrsequence, calculation, classid, createindustry, createtime, creator, customattr, datatype, datatypestyle, defaultvalue, description, displayname, dr, dynamicattr, dynamictable, fixedlength, help, hided, id, industry, isactive, isauthen, modifier, modifytime, name, notserialize, nullable, precise, readonly, refmodelname, resid, ts, versiontype, visibility) values (null, 'N', null, 20, null, null, 95, 'N', 'ece96dd8-bdf8-4db3-a112-9d2f636d388f', '0', null, null, 'N', 'af6a8e77-4fa3-4316-9d8d-12d24a9ff338', 305, null, null, '合同号', null, 'N', null, 'N', null, 'N', '78d1da6b-0af5-4ddb-b629-cf8df7e42b04', '0', 'Y', null, null, null, 'fctno', 'N', 'Y', 0, 'N', '付款合同', '2UC000-000234', '2014-11-15 10:00:09', 0, 0)", };
		for (String sql : sqls) {
			executeSql(dao, sql);
		}
	}

	private void executeSql(BaseDAO dao, String sql) throws BusinessException {
		try {
			dao.executeUpdate(sql);
		} catch (Exception e) {
			throw new BusinessException("费用升级出错：" + sql);
		}
	}

	private BaseDAO getBaseDAO() {
		if (dao == null) {
			dao = new BaseDAO();
		}
		return dao;
	}

	public IPublishService getPublishService() {
		if (this.service == null) {
			this.service = NCLocator.getInstance().lookup(IPublishService.class);
		}
		return this.service;
	}
}
