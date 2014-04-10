package nc.bs.er.control;

import java.rmi.RemoteException;

import nc.bs.logging.Logger;
import nc.itf.tb.control.IAccessableBusiVO;
import nc.itf.tb.control.IBusiSysExecDataProvider;
import nc.vo.erm.control.FuncResultVO;
import nc.vo.erm.control.ResultVOChanger;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.tb.obj.NtbParamVO;

/**
 * @modify liansg
 * ��ȡִ����������ṩ��
 * nc.bs.er.control.ErmNtbProvider
 */
public class ErmNtbProvider implements IBusiSysExecDataProvider

{

	public int getCtlPoint(String pk_corp) throws RemoteException {
//		try {
//	        String paramValue = SysInit.getParaString(pk_corp,BXConstans.BUGET_CTRL_TIME);
//	        if (paramValue.trim().equals("1")){
//	            return 1;
//	        } else if (paramValue.trim().equals("0")){
//	            return 0;
//	        }
//	    } catch (Exception e) {
//	        Log.getInstance(this.getClass()).error(e.getMessage());
//	        throw new RemoteException(e.getMessage());
//	    }
	    return -1;
	}

	
	public UFDouble[] getExecData(NtbParamVO param) throws BusinessException {
		
		return getExecDataBatch(new NtbParamVO[]{param})[0];
	}

	/**	 
	 * Ԥ��ȡҵ��ϵͳִ������Ҫ���õķ���
	 */	
	public UFDouble[][] getExecDataBatch(NtbParamVO[] param) throws BusinessException {

		UFDouble[][] values = new UFDouble[param.length][4];
		
		try {
			
			FuncResultVO[] resultVOs = new QueryFuncBO().queryFuncs(param, null);						
			if(param.length != resultVOs.length){
				throw new Exception(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-000405")/*@res "����.���ؽ���ļ�¼�����Ͳ������������."*/);
			}
//			for(int i=0;i<values.length;i++){
//				//����ȫ�ֱ���
//				if(param[i].getCurr_type()==0){					
//					values[i]=new UFDouble[]{**,UFDouble.ZERO_DBL,UFDouble.ZERO_DBL,UFDouble.ZERO_DBL};
//				}else if(param[i].getCurr_type()==1){
//					values[i]=new UFDouble[]{UFDouble.ZERO_DBL,**,UFDouble.ZERO_DBL,UFDouble.ZERO_DBL};
//				}else if(param[i].getCurr_type()==2){
//					values[i]=new UFDouble[]{UFDouble.ZERO_DBL,UFDouble.ZERO_DBL,**,UFDouble.ZERO_DBL};
//				}else if(param[i].getCurr_type()==3){
//					String currency = param[i].getPk_currency();
//					
//					values[i]=new UFDouble[]{UFDouble.ZERO_DBL,UFDouble.ZERO_DBL,UFDouble.ZERO_DBL,UFDouble.ZERO_DBL};	
//				}
//				
//			}
			values = ResultVOChanger.getUFDoubleArray(resultVOs);
		} catch (Exception e) {
			Logger.error(e.getMessage(),e);
			throw new BusinessException(e.getMessage());
		}
		return values;
		
	}

	public IAccessableBusiVO[] getCvtProvider(IAccessableBusiVO[] runvos) throws RemoteException {

		return null;
	}

	public UFDouble[] getPointData(NtbParamVO param) throws BusinessException {

		return null;
	}
	/**	 
	 * ����ʱ����
	 */	
	public UFDouble[][] getPointDataBatch(NtbParamVO[] param) throws BusinessException {

		return null;
	}

	public UFDouble[] getReadyData(NtbParamVO param) throws BusinessException {
		return getReadyDataBatch(new NtbParamVO[]{param})[0];
	}
	
	/**	 
	 * ����Ԥռ��
	 */
	public UFDouble[][] getReadyDataBatch(NtbParamVO[] param) throws BusinessException {

		UFDouble[][] values = new UFDouble[param.length][4];
		try {
			//FIXME
			
			FuncResultVO[] resultVOs = new QueryFuncBO().queryFuncs(param, null);	
			if(resultVOs == null)
				return new UFDouble[][] {{UFDouble.ZERO_DBL,UFDouble.ZERO_DBL,UFDouble.ZERO_DBL,UFDouble.ZERO_DBL}};
			if(param.length != resultVOs.length){
				throw new Exception(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-000405")/*@res "����.���ؽ���ļ�¼�����Ͳ������������."*/);
			}

			values = ResultVOChanger.getUFDoubleArray(resultVOs);
		} catch (Exception e) {
			Logger.error(e.getMessage(),e);
			throw new BusinessException(e.getMessage());
		}
		return values;

	}

	public void createBillType(NtbParamVO[] param)
			throws BusinessException {
	}

}
