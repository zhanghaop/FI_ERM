package nc.bs.erm.imag;


import nc.bs.dao.BaseDAO;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.BusinessException;
import ssc.itf.service.ISSCService;

public class BxSSCServiceImpl implements ISSCService {

	@Override
	public String setState(String pk, String barcode, String billtype, String state) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		if (BXConstans.BX_DJLXBM.equals(billtype)) {// ±¨Ïúµ¥
			
			String sql = "update " + BXConstans.BX_TABLENAME + " set image_status = " + state
					+ " where dr = 0 and pk_jkbx = " + pk;
			dao.executeUpdate(sql);
		}
		return state;
	}

}
