package nc.impl.arap.bx;

import java.util.ArrayList;
import java.util.List;

import nc.bs.arap.bx.ProxyUserManageDAO;
import nc.bs.logging.Logger;
import nc.itf.arap.prv.IproxyUserBillPrivate;
import nc.jdbc.framework.exception.DbException;
import nc.vo.ep.bx.SqdlrVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;

public class ProxyUserManageImp implements IproxyUserBillPrivate {

	private ProxyUserManageDAO dao = null;

	public SqdlrVO[] getProxyUser(String user) throws BusinessException {
		String condition = " pk_user='"+user+"' and type=1";
		List<SqdlrVO> ls = null;
		try {
			ls = getDao().getResultVo(SqdlrVO.class, condition);
		} catch (DbException e) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000253")/*@res "查询异常"*/);
		}
		return ls.toArray(new SqdlrVO[ls.size()]);
	}

	public boolean saveProxyUser(SqdlrVO[] sqdlrVOs) throws BusinessException {
		boolean bl = false;
		List<SqdlrVO> lsvo = new ArrayList<SqdlrVO>();
		List<SqdlrVO> luvo = new ArrayList<SqdlrVO>();

		for(int i=0;i<sqdlrVOs.length;i++){
			if(sqdlrVOs[i].getStatus() == VOStatus.NEW)
				lsvo.add(sqdlrVOs[i]);
			if(sqdlrVOs[i].getStatus() == VOStatus.UPDATED)
				luvo.add(sqdlrVOs[i]);
		}
		SqdlrVO[] svo = lsvo.toArray(new SqdlrVO[lsvo.size()]);
		SqdlrVO[] uvo = luvo.toArray(new SqdlrVO[luvo.size()]);
		try {
			if(svo.length>0)
				getDao().saveData(svo);
			if(uvo.length>0)
				getDao().executeUpdate(uvo);
			bl = true;
		} catch (DbException e) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000254")/*@res "保存数据异常"*/);
		}

		return bl;
	}

	public ProxyUserManageDAO getDao() {
		if(dao == null)
			dao = new ProxyUserManageDAO();
		return dao;
	}

	public boolean delProxyuser(List<SqdlrVO> pusers) throws BusinessException {
		return getDao().deleteData(pusers.toArray(new SqdlrVO[pusers.size()]));
	}



}