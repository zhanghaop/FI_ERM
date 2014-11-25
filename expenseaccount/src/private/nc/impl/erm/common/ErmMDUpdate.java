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
	private String[] bmfpath = {
			"/modules/erm/METADATA/accruedexpense.bmf",
			"/modules/erm/METADATA/reimdimension.bmf",
			"/modules/erm/METADATA/reimrule.bmf", "/modules/erm/METADATA/ermcostaccount.bmf",
			"/modules/erm/METADATA/ermcostaccountbal.bmf", "/modules/erm/METADATA/matterappbill.bmf",
			"/modules/erm/METADATA/matterappbill_intermonth_ext.bmf", 
			"/modules/erm/METADATA/expenseaccount.bmf", "/modules/erm/METADATA/loanmanage.bmf" ,
			"/modules/erm/METADATA/costsharebill.bmf", "/modules/erm/METADATA/expamortizeinfo.bmf",
			"/modules/erm/METADATA/bxbillmanage.bmf","/modules/erm/METADATA/ermbilltype.bmf" };

	private String[] bpfpath = {
			"/modules/erm/METADATA/accruedoperate.bpf"
	};

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
	}

	private void delVOChange_b() throws DAOException {
		String sql = "delete from pub_vochange_b where ts not like '2014-11%' and  pk_vochange in (select pk_vochange from pub_vochange where src_billtype='261X' and pk_group='~' and dest_billtype in ('263X','264X'))";
		getBaseDAO().executeUpdate(sql);
		Logger.debug(sql);
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
