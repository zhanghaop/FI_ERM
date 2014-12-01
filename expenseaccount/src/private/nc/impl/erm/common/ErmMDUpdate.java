package nc.impl.erm.common;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
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

		// pub_vochange_b表维护
		delVOChange_b();
		updateFct();
	}

	private void delVOChange_b() throws DAOException {
		String sql = "delete from pub_vochange_b where ts not like '2014-11%' and  pk_vochange in (select pk_vochange from pub_vochange where src_billtype='261X' and pk_group='~' and dest_billtype in ('263X','264X'))";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
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
