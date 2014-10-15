package nc.impl.arap.bx;

import java.util.Arrays;
import java.util.Collection;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.callouter.FipCallFacade;
import nc.bs.er.djlx.DjLXDMO;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.bs.pf.functioncopy.IPFFunctionCopyPlugin;
import nc.bs.pub.SystemException;
import nc.itf.er.pub.IArapBillTypePublic;
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
 * 借款报销单据类型拷贝 -- 实现功能复制插件类接口
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
//			Collection<DjLXVO> collection = baseDAO.retrieveByClause(DjLXVO.class, "dwbm ='0001' and djlxbm ='" + srcBm + "'");
			DjLXVO[] vos = NCLocator.getInstance().lookup(IArapBillTypePublic.class).queryByWhereStr("dwbm ='0001' and djlxbm ='" + srcBm + "'");
			Collection<DjLXVO> collection = Arrays.asList(vos);
			if(collection==null || collection.size()==0){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000252")/*@res "原始单据类型不存在,拷贝失败!"*/);
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
			Logger.debug("没有找到会计平台提供的增加单据类型接口，应该是会计平台的问题，主流程不受影响");
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
				/* @res "单据{0}被引用,不能删除。" */
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
//		BaseDAO dao = new BaseDAO();
		try {
//            Collection cl = dao.retrieveByClause(DjLXVO.class, "dwbm <>'0001' and djlxbm ='" + djlxbm + "'");
			DjLXVO[] vos = NCLocator.getInstance().lookup(IArapBillTypePublic.class).queryByWhereStr("dwbm <>'0001' and djlxbm ='" + djlxbm + "'");
			Collection<DjLXVO> cl = Arrays.asList(vos);
			if (cl.size() > 0) {
				return true;
			} else {
				return false;
			}

		} catch (DAOException e) {
			throw new BusinessRuntimeException(e.getMessage(),e);
		} catch (BusinessException e) {
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
			throw new BusinessRuntimeException(e.getMessage());
		} catch (NamingException e) {
			throw new BusinessRuntimeException(e.getMessage());
		}
		if (!bool) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("20060101", "UPP20060101-000052"));
		}
		return true;

	}
}