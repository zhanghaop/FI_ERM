package nc.itf.erm.prv;


import nc.vo.er.paramsetting.ParamVO;
import nc.vo.pub.BusinessException;

/**
 * �����������������Ľӿ�

 *
 */
public interface IErmParamService {

	/**
	 * ����pkȡ�ò���VO
	 * @param pk_param   ����pk
	 * @return   ����VO
	 * @throws BusinessException
	 */
	ParamVO queryParamByParamPk(String pk_param) throws BusinessException;
	
	/**
	 * ����ҵ����֯pkȡ�ø�ҵ����֯�µ�����VO
	 * @param pk_org  ҵ����֯pk
	 * @return
	 * @throws BusinessException
	 */
	ParamVO[] queryAllParamsByPk_org(String pk_org) throws BusinessException;
	
	/**
	 * ����ҵ����֯pk�����������ϵͳ���뷵�ؽ��
	 * @param pk_org
	 * @param sParamCode
	 * @param iSysCode
	 * @return
	 * @throws BusinessException
	 */
	ParamVO queryParamByCode(String pk_org, String sParamCode, Integer iSysCode) throws BusinessException;
	
	/**
	 * ����ҵ����֯pk�����������ϵͳ���뷵�ؽ��
	 * @param pk_org
	 * @param sParamCode
	 * @param iSysCode
	 * @return
	 * @throws BusinessException
	 */
	ParamVO[] queryParamsByCode(String pk_org, String sParamCode, Integer iSysCode) throws BusinessException;	
	
	/**
	 * ���²���VO
	 * @param paramVos
	 * @throws BusinessException
	 */
	void updateParams(ParamVO[] paramVos) throws BusinessException;
	
	/**
	 * �������VO
	 * @param paramVos
	 * @throws BusinessException
	 */
	void insertParams(ParamVO[] paramVos) throws BusinessException;
}
