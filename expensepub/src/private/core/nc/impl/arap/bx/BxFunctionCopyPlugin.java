package nc.impl.arap.bx;

import java.util.Collection;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.callouter.FipCallFacade;
import nc.bs.er.djlx.DjLXDMO;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.bs.pf.functioncopy.IPFFunctionCopyPlugin;
import nc.bs.pub.SystemException;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.billtype.BilltypeVO;

/**
 * @author twei
 *
 * nc.impl.arap.bx.BxFunctionCopyPlugin
 *
 * �����������Ϳ��� -- ʵ�ֹ��ܸ��Ʋ����ӿ�
 *
 * @see IPFFunctionCopyPlugin
 */
public class BxFunctionCopyPlugin implements IPFFunctionCopyPlugin {

	@SuppressWarnings("unchecked")
	public void excuteCopyBill(BilltypeVO srcBilltypeVO, BilltypeVO newBilltypeVO) {

		BaseDAO baseDAO = new BaseDAO();
		String destBm = newBilltypeVO.getPk_billtypecode();
		String srcBm = srcBilltypeVO.getPk_billtypecode();
		String djmc = newBilltypeVO.getBilltypename();

		DjLXVO djlx=null;

		try {
			Collection<DjLXVO> collection = baseDAO.retrieveByClause(DjLXVO.class, "dwbm ='0001' and djlxbm ='" + srcBm + "'");

			if(collection==null || collection.size()==0){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000252")/*@res "ԭʼ�������Ͳ�����,����ʧ��!"*/);
			}

			djlx=collection.iterator().next();
			djlx.setDjlxoid(null);
			djlx.setDjlxjc(djmc);
			djlx.setDjlxmc(djmc);
			djlx.setDjlxbm(destBm);

		}catch (BusinessException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		}


		try {
			checkUnique(djlx);

			baseDAO.insertVO(djlx);

			new FipCallFacade().copybill(destBm, srcBm, djmc, "", null);

		} catch (ComponentException e) {
			Logger.debug("û���ҵ����ƽ̨�ṩ�����ӵ������ͽӿڣ�Ӧ���ǻ��ƽ̨�����⣬�����̲���Ӱ��");
		} catch (BusinessException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
	}

	public void excuteDeleteBill(BilltypeVO btvo, String parentBilltypecode) {
		String pk_billtypecode = btvo.getPk_billtypecode();

		try {
			if (isRefered(pk_billtypecode)) {
				String djlxjc = btvo.getBilltypename();
				/* @res "����{0}������,����ɾ����" */
				throw new nc.vo.pub.BusinessException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060101", "UPP20060101-000000", null, new String[] { djlxjc }));
			} else {
				BaseDAO dao = new BaseDAO();
				dao.deleteByClause(DjLXVO.class, " djlxbm ='" + pk_billtypecode + "'");
				new FipCallFacade().deleteBill(pk_billtypecode);
			}
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
	}

	private boolean isRefered(String djlxbm) {
		BaseDAO dao = new BaseDAO();
		try {
			Collection cl = dao.retrieveByClause(DjLXVO.class, "dwbm <>'0001' and djlxbm ='" + djlxbm + "'");
			if (cl.size() > 0) {
				return true;
			} else {
				return false;
			}

		} catch (DAOException e) {
			throw new BusinessRuntimeException(e.getMessage(),e);
		}
	}

	private boolean checkUnique(DjLXVO vo) throws BusinessException {
		boolean bool = true;
		DjLXDMO dmo;
		try {
			dmo = new DjLXDMO();
			bool = dmo.checkUnique(vo);
		} catch (SystemException e) {
			Log.getInstance(this.getClass()).equals(e);
			throw new BusinessRuntimeException(e.getMessage());
		} catch (NamingException e) {
			Log.getInstance(this.getClass()).equals(e);
			throw new BusinessRuntimeException(e.getMessage());
		}
		if (!bool) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("20060101", "UPP20060101-000052"));
		}
		return true;

	}
}