package nc.bs.er.control;

import java.rmi.RemoteException;
import java.util.List;

import nc.bs.er.ntbcontrol.ErmNtbSqlFactory;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.tb.control.IAccessableBusiVO;
import nc.itf.tb.control.IBusiSysExecDataProvider;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tb.obj.NtbParamVO;

/**
 * 获取预算数的提供类（注册在IBusiSysExecDataProvider nc.vo.er.ntb.ErmBusiSysReg.getExecDataProvider())
 * @modify liansg 获取执行数服务的提供类 nc.bs.er.control.ErmNtbProvider
 * @modify chenshuaia 获取执行数服务的提供类 nc.bs.er.control.ErmNtbProvider 2013
 */
public class ErmNtbProvider implements IBusiSysExecDataProvider {
	private IUAPQueryBS dao = NCLocator.getInstance().lookup(IUAPQueryBS.class);
	
	@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "预算取执行数接口" /*-=notranslate=-*/,type=BusinessType.CORE)
	public UFDouble[] getExecData(NtbParamVO param) throws BusinessException {
		return getData(param);
	}
	
	@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "预算取执行数接口" /*-=notranslate=-*/,type=BusinessType.CORE)
	public UFDouble[][] getExecDataBatch(NtbParamVO[] param) throws BusinessException {
		UFDouble[][] values = new UFDouble[param.length][4];
		try {
			for (int i = 0; i < param.length; i++) {
				values[i] = getExecData(param[i]);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
		return values;
	}
	
	@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "预算取预占数接口" /*-=notranslate=-*/,type=BusinessType.CORE)
	public UFDouble[] getReadyData(NtbParamVO param) throws BusinessException {
		return getData(param);
	}
	
	@Business(business=ErmBusinessDef.TBB_CTROL,subBusiness="", description = "预算取预占数接口" /*-=notranslate=-*/,type=BusinessType.CORE)
	public UFDouble[][] getReadyDataBatch(NtbParamVO[] param) throws BusinessException {
		UFDouble[][] values = new UFDouble[param.length][4];
		try {
			for (int i = 0; i < param.length; i++) {
				values[i] = getReadyData(param[i]);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		return values;
	}

	public int getCtlPoint(String pk_corp) throws RemoteException {
		return -1;
	}

	public IAccessableBusiVO[] getCvtProvider(IAccessableBusiVO[] runvos) throws RemoteException {

		return null;
	}

	public UFDouble[] getPointData(NtbParamVO param) throws BusinessException {

		return null;
	}

	/**
	 * 返回时点数
	 */
	public UFDouble[][] getPointDataBatch(NtbParamVO[] param) throws BusinessException {

		return null;
	}

	public void createBillType(NtbParamVO[] param) throws BusinessException {
	}
	
	protected UFDouble[] getData(NtbParamVO param) throws BusinessException {
		UFDouble[] returnData = new UFDouble[]{UFDouble.ZERO_DBL,UFDouble.ZERO_DBL,UFDouble.ZERO_DBL,UFDouble.ZERO_DBL};

		String[] sqls = ErmNtbSqlFactory.getInstance(param).getSqls();
		
		if(sqls != null && sqls.length > 0){
			for (int i = 0; i < sqls.length; i++) {
				if(sqls[i] == null || sqls[i].trim().length() == 0){
					continue;
				}
				@SuppressWarnings("unchecked")
				List<Object[]> list = (List<Object[]>) dao.executeQuery(sqls[i], new ArrayListProcessor());
				if (list != null && list.size() == 1) {
					Object[] obj = list.get(0);
					returnData[0] = (obj[0] == null ? UFDouble.ZERO_DBL : new UFDouble(obj[0].toString())).add(returnData[0]);
					returnData[1] = (obj[1] == null ? UFDouble.ZERO_DBL : new UFDouble(obj[1].toString())).add(returnData[1]);
					returnData[2] = (obj[2] == null ? UFDouble.ZERO_DBL : new UFDouble(obj[2].toString())).add(returnData[2]);
					returnData[3] = (obj[3] == null ? UFDouble.ZERO_DBL : new UFDouble(obj[3].toString())).add(returnData[3]);
				}
			}
		}
		return returnData;
	}

}
