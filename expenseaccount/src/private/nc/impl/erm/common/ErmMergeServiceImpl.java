package nc.impl.erm.common;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.itf.erm.service.IErmMergeService;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;

public class ErmMergeServiceImpl implements IErmMergeService {

	@Override
	public void mergeSupplier(String targetSup, String sourceSup) throws BusinessException {
		SQLParameter par = new SQLParameter();
		par.addParam(targetSup);
		par.addParam(sourceSup);
		PersistenceManager pm = null;

		try {
			pm = PersistenceManager.getInstance(getds());
			JdbcSession session = pm.getJdbcSession();
			// ��Ϊ������sqlparameter���á�������ʽ�����Ч�ʣ�Ҳ���Բ���addBatch(sql)
			String sqlBx = "update er_bxzb set hbbm =?  where hbbm =? ";// ������
			session.addBatch(sqlBx, par);
			String sqlJk = "update er_jkzb set hbbm =?  where hbbm =? ";
			session.addBatch(sqlJk, par);

			String sqlMa = "update er_mtapp_bill set pk_supplier =? where pk_supplier =? ";// ���뵥
			String sqlMaDetail = "update er_mtapp_detail set pk_supplier =? where pk_supplier =? ";
			session.addBatch(sqlMa, par);
			session.addBatch(sqlMaDetail, par);

			String sqlCs = "update er_costshare set hbbm =? where hbbm =? ";// ��ת��
			String sqlCsDetail = "update er_cshare_detail set hbbm =? where hbbm =? ";
			session.addBatch(sqlCs, par);
			session.addBatch(sqlCsDetail, par);

			String sqlAT = "update er_expamtdetail set hbbm =? where hbbm =? ";// ̯����Ϣ
			session.addBatch(sqlAT, par);

			session.executeBatch();
		} catch (DbException e) {
			ExceptionHandler.consume(e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		} finally {
			pm.release();
		}
	}

	@Override
	public void mergeCustomer(String targetCus, String sourceCus) throws BusinessException {
		SQLParameter par = new SQLParameter();
		par.addParam(targetCus);
		par.addParam(sourceCus);
		PersistenceManager pm = null;

		try {
			pm = PersistenceManager.getInstance(getds());
			JdbcSession session = pm.getJdbcSession();
			// ��Ϊ������sqlparameter���á�������ʽ�����Ч�ʣ�Ҳ���Բ���addBatch(sql)
			String sqlBx = "update er_bxzb set customer =?  where customer =? ";
			session.addBatch(sqlBx, par);
			String sqlJk = "update er_jkzb set customer =?  where customer =? ";
			session.addBatch(sqlJk, par);

			String sqlMa = "update er_mtapp_bill set pk_customer =? where pk_customer =? ";// ���뵥
			String sqlMaDetail = "update er_mtapp_detail set pk_customer =? where pk_customer =? ";
			session.addBatch(sqlMa, par);
			session.addBatch(sqlMaDetail, par);

			String sqlCs = "update er_costshare set customer =? where hbbm =? ";// ��ת��
			String sqlCsDetail = "update er_cshare_detail set customer =? where customer =? ";
			session.addBatch(sqlCs, par);
			session.addBatch(sqlCsDetail, par);

			String sqlAT = "update er_expamtdetail set customer =? where customer =? ";// ̯����Ϣ
			session.addBatch(sqlAT, par);

			session.executeBatch();
		} catch (DbException e) {
			ExceptionHandler.consume(e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		} finally {
			pm.release();
		}
	}

	private String getds() {
		return InvocationInfoProxy.getInstance().getUserDataSource();
	}
}
