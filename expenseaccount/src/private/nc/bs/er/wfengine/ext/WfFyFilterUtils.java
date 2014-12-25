package nc.bs.er.wfengine.ext;

import java.util.HashSet;

import nc.bs.framework.common.NCLocator;
import nc.itf.pmbd.pub.IProjectQueryService;
import nc.itf.pmpub.prv.IProjectQuery;
import nc.util.fi.pub.SqlUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.pub.BusinessException;
import nc.vo.uap.pf.PFBusinessException;

public class WfFyFilterUtils {

	/**
	 *��ѯ��Ŀ������
	 * 
	 * @param projectPKs
	 * @return
	 * @throws BusinessException
	 */
	public String[] getDutierByProjectPks(String[] projectPKs) throws BusinessException {
		if (projectPKs == null || projectPKs.length < 1) {
			return null;
		}
		// �Ƿ�װ��Ŀ����(ʹ����Ŀ����ͻ�����������Ŀ���ö�����һ���������⴦��)
		HashSet<String> psnIDs = new HashSet<String>();
		boolean isInstallPM = BXUtil.isProductTbbInstalled(BXConstans.PIM_FUNCODE);
		if (isInstallPM) {
			// // ��ѯ��Ŀ��������Ŀʹ��IProjectQueryService�ӿ�
			IProjectQueryService service = NCLocator.getInstance().lookup(IProjectQueryService.class);
			nc.vo.pim.project.ProjectHeadVO[] projecters = (nc.vo.pim.project.ProjectHeadVO[]) service
					.queryProjectHeadVOsByPK(projectPKs);
			if (projecters == null || projecters.length == 0) {
				return null;
			}
			for (int i = 0; i < projecters.length; i++) {
				if (projecters[i].getPk_dutier() == null) {
					throw new PFBusinessException("δ�ҵ���Ŀ������");
				} else {
					psnIDs.add(projecters[i].getPk_dutier());
				}
			}
		} else {
			String condition = SqlUtils.getInStr(nc.vo.pmpub.project.ProjectHeadVO.PK_PROJECT, projectPKs);

			// ��ѯȫ����Ŀ�ĸ�����
			IProjectQuery service = NCLocator.getInstance().lookup(IProjectQuery.class);
			nc.vo.pmpub.project.ProjectHeadVO[] projecters = (nc.vo.pmpub.project.ProjectHeadVO[]) service
					.queryProjectHeadVOsByCondition(condition);
			if (projecters == null || projecters.length == 0) {
				return null;
			}
			for (int i = 0; i < projecters.length; i++) {
				if (projecters[i].getPk_dutier() == null) {
					throw new PFBusinessException("δ�ҵ���Ŀ������");
				} else {
					psnIDs.add(projecters[i].getPk_dutier());
				}
			}

		}

		return psnIDs.toArray(new String[0]);
	}

}
