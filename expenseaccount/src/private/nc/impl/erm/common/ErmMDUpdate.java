package nc.impl.erm.common;

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
 * 631-63EHP2���� ������Ԫ����������ע����setup.ini��
 * </p>
 * 
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li> <br>
 * <br>
 * 
 * @see
 * @author chenshuaia
 * @version V6.3
 * @since V6.3
 */
public class ErmMDUpdate extends AbstractPatchInstall {
	private IPublishService service;

	// Ԫ��������໥��������ע���Ⱥ�˳��
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
	 * ����Ԫ����
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
				Logger.error("����Ԫ���ݣ�" + destPath.toString());
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
				Logger.error("����Ԫ���ݲ�����" + destPath.toString());
				getPublishService().publishMetaDataForBPF(destPath.toString());
			} catch (Exception ex) {
				Logger.error(ex.getMessage(), ex);
				throw new BusinessException(ex.getMessage(), ex);
			}
		}
	}

	public IPublishService getPublishService() {
		if (this.service == null) {
			this.service = NCLocator.getInstance().lookup(IPublishService.class);
		}
		return this.service;
	}
}
