package nc.itf.erm.prv;


import nc.vo.er.paramsetting.ParamVO;
import nc.vo.pub.BusinessException;

/**
 * 报销管理参数表操作的接口

 *
 */
public interface IErmParamService {

	/**
	 * 根据pk取得参数VO
	 * @param pk_param   参数pk
	 * @return   参数VO
	 * @throws BusinessException
	 */
	ParamVO queryParamByParamPk(String pk_param) throws BusinessException;
	
	/**
	 * 根据业务组织pk取得该业务组织下的所有VO
	 * @param pk_org  业务组织pk
	 * @return
	 * @throws BusinessException
	 */
	ParamVO[] queryAllParamsByPk_org(String pk_org) throws BusinessException;
	
	/**
	 * 根据业务组织pk、参数编码和系统编码返回结果
	 * @param pk_org
	 * @param sParamCode
	 * @param iSysCode
	 * @return
	 * @throws BusinessException
	 */
	ParamVO queryParamByCode(String pk_org, String sParamCode, Integer iSysCode) throws BusinessException;
	
	/**
	 * 根据业务组织pk、参数编码和系统编码返回结果
	 * @param pk_org
	 * @param sParamCode
	 * @param iSysCode
	 * @return
	 * @throws BusinessException
	 */
	ParamVO[] queryParamsByCode(String pk_org, String sParamCode, Integer iSysCode) throws BusinessException;	
	
	/**
	 * 更新参数VO
	 * @param paramVos
	 * @throws BusinessException
	 */
	void updateParams(ParamVO[] paramVos) throws BusinessException;
	
	/**
	 * 插入参数VO
	 * @param paramVos
	 * @throws BusinessException
	 */
	void insertParams(ParamVO[] paramVos) throws BusinessException;
}
