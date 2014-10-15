package nc.bs.erm.costshare;

import nc.bs.dao.BaseDAO;
import nc.bs.erm.util.ErMdpersistUtil;
import nc.md.data.access.NCObject;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;

/**
 * ���ý�ת���־û�
 * 
 * @author lvhj
 * 
 */
public class ErmCostShareDAO {

	private IMDPersistenceService service;

	private IMDPersistenceService getService() {
		if (service == null) {
			service = MDPersistenceService.lookupPersistenceService();
		}
		return service;
	}

	private BaseDAO basedao;

	private BaseDAO getBaseDAO() {
		if (basedao == null) {
			basedao = new BaseDAO();
		}
		return basedao;
	}

	public AggCostShareVO insertVO(AggCostShareVO vo) throws BusinessException {
		vo.getParentVO().setStatus(VOStatus.NEW);
//		String pk = 
			getService().saveBill(vo);
//		return getQryService().queryBillOfVOByPK(AggCostShareVO.class, pk, false);
		return vo;
	}

	public void deleteVOs(AggCostShareVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		for (int i = 0; i < vos.length; i++) {
			vos[i].getParentVO().setStatus(VOStatus.DELETED);
		}
		NCObject[] ncobjs = ErMdpersistUtil.getNCObject(vos);
		getService().deleteBillFromDB(ncobjs);
	}

	/**
	 * ��vo����
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO updateVO(AggCostShareVO vo) throws BusinessException {
		vo.getParentVO().setStatus(VOStatus.UPDATED);
//		String pk = 
			getService().saveBillWithRealDelete(vo);
//		return getQryService().queryBillOfVOByPK(AggCostShareVO.class, pk, false);
		return vo;
	}

	/**
	 * ���ֶθ�������
	 * 
	 * @param parentvos
	 * @param fields
	 * @throws BusinessException
	 */
	public void updateParentVOs(CostShareVO[] parentvos,String[] fields) throws BusinessException {
		// ���ݿ��ֶθ���
		getBaseDAO().updateVOArray(parentvos,fields);
	}
	
	/**
	 * ���ֶθ����ӱ�
	 * 
	 * @param parentvos
	 * @param fields
	 * @throws BusinessException
	 */
	public void updateChildrenVOs(CShareDetailVO[] childrenVO,String[] fields) throws BusinessException {
		// ���ݿ��ֶθ���
		getBaseDAO().updateVOArray(childrenVO,fields);
	}

}
