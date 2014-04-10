package nc.impl.erm.common;

import java.lang.reflect.Array;
import java.util.Collection;
import nc.bs.dao.BaseDAO;
import nc.itf.erm.prv.IErmParamService;
import nc.vo.er.paramsetting.ParamVO;
import nc.vo.pub.BusinessException;

public class ErmParamServiceImpl implements IErmParamService {

	/**
	 * 根据业务组织pk取得该业务组织下的所有VO
	 */
	public ParamVO[] queryAllParamsByPk_org(String pkOrg)
			throws BusinessException {
    	BaseDAO dao = new BaseDAO();
    	Collection<ParamVO> cl = dao.retrieveByCorp(ParamVO.class,pkOrg);
    	
    	if(cl.isEmpty()){
    		return null;
    	}

    	ParamVO[] o = (ParamVO[])Array.newInstance(ParamVO.class, cl.size());
    	o = (ParamVO[])cl.toArray(o);
		
		return o;
	}

	/**
	 * 根据业务组织pk、参数编码和系统编码返回结果
	 */
	public ParamVO queryParamByCode(String pkOrg, String sParamCode,
			Integer iSysCode) throws BusinessException {
		
    	String strConditionNames = "";
    	String strAndOr = "and ";

    	/**
    	 * 根据参数拼条件
    	 */
    	if (pkOrg != null) {
    		strConditionNames += strAndOr + "pk_org='"+pkOrg+"' ";
    	}
    	if (sParamCode != null) {
    		strConditionNames += strAndOr + "param_code='"+sParamCode+"' ";
    	}
    	if (iSysCode != null) {
    		strConditionNames += strAndOr + "sysCode="+iSysCode;
    	}
    	
    	BaseDAO dao = new BaseDAO();
    	
    	//去掉最前面那个"and"
    	if(strConditionNames.length()>0){
    		strConditionNames = strConditionNames.substring(3);
    	}
    	
    	//取得结果集
    	Collection cl =dao.retrieveByClause(ParamVO.class,strConditionNames);
    	if(cl.isEmpty()){
    		return null;
    	}

    	//转化成数组返回结果
    	ParamVO[] o = (ParamVO[])Array.newInstance(ParamVO.class, cl.size());
    	o = (ParamVO[])cl.toArray(o);		
    	if(o==null || o.length <1){ 	
    		return null;
    	}
    	
    	return o[0];		
	}
	
	/**
	 * 根据业务组织pk、参数编码和系统编码返回结果
	 */
	public ParamVO[] queryParamsByCode(String pkOrg, String sParamCode,
			Integer iSysCode) throws BusinessException {
		
    	String strConditionNames = "";
    	String strAndOr = "and ";

    	/**
    	 * 根据参数拼条件
    	 */
    	if (pkOrg != null) {
    		strConditionNames += strAndOr + "pk_org='"+pkOrg+"' ";
    	}
    	if (sParamCode != null) {
    		strConditionNames += strAndOr + "param_code='"+sParamCode+"' ";
    	}
    	if (iSysCode != null) {
    		strConditionNames += strAndOr + "sysCode="+iSysCode;
    	}
    	
    	BaseDAO dao = new BaseDAO();
    	
    	//去掉最前面那个"and"
    	if(strConditionNames.length()>0){
    		strConditionNames = strConditionNames.substring(3);
    	}
    	
    	//取得结果集
    	Collection cl =dao.retrieveByClause(ParamVO.class,strConditionNames);
    	if(cl.isEmpty()){
    		return null;
    	}

    	//转化成数组返回结果
    	ParamVO[] o = (ParamVO[])Array.newInstance(ParamVO.class, cl.size());
    	o = (ParamVO[])cl.toArray(o);		
    	if(o==null || o.length <1){ 	
    		return null;
    	}
    	
    	return o;		
	}


	/**
	 * 根据参数主键取得参数VO
	 */
	public ParamVO queryParamByParamPk(String pkParam) throws BusinessException {
		BaseDAO dao = new BaseDAO();
    	return (ParamVO)dao.retrieveByPK(ParamVO.class,pkParam);
	}

	/**
	 * 更新参数VO
	 */
	public void updateParams(ParamVO[] paramVos) throws BusinessException {
    	BaseDAO dao = new BaseDAO();
    	dao.updateVOArray(paramVos);
	}
	
	/**
	 * 插入参数VO
	 */
	public void insertParams(ParamVO[] paramVos) throws BusinessException {
    	BaseDAO dao = new BaseDAO();
    	dao.insertVOArray(paramVos);
	}

}
