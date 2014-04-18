package nc.impl.erm.common;

import java.lang.reflect.Array;
import java.util.Collection;
import nc.bs.dao.BaseDAO;
import nc.itf.erm.prv.IErmParamService;
import nc.vo.er.paramsetting.ParamVO;
import nc.vo.pub.BusinessException;

public class ErmParamServiceImpl implements IErmParamService {

	/**
	 * ����ҵ����֯pkȡ�ø�ҵ����֯�µ�����VO
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
	 * ����ҵ����֯pk�����������ϵͳ���뷵�ؽ��
	 */
	public ParamVO queryParamByCode(String pkOrg, String sParamCode,
			Integer iSysCode) throws BusinessException {
		
    	String strConditionNames = "";
    	String strAndOr = "and ";

    	/**
    	 * ���ݲ���ƴ����
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
    	
    	//ȥ����ǰ���Ǹ�"and"
    	if(strConditionNames.length()>0){
    		strConditionNames = strConditionNames.substring(3);
    	}
    	
    	//ȡ�ý����
    	Collection cl =dao.retrieveByClause(ParamVO.class,strConditionNames);
    	if(cl.isEmpty()){
    		return null;
    	}

    	//ת�������鷵�ؽ��
    	ParamVO[] o = (ParamVO[])Array.newInstance(ParamVO.class, cl.size());
    	o = (ParamVO[])cl.toArray(o);		
    	if(o==null || o.length <1){ 	
    		return null;
    	}
    	
    	return o[0];		
	}
	
	/**
	 * ����ҵ����֯pk�����������ϵͳ���뷵�ؽ��
	 */
	public ParamVO[] queryParamsByCode(String pkOrg, String sParamCode,
			Integer iSysCode) throws BusinessException {
		
    	String strConditionNames = "";
    	String strAndOr = "and ";

    	/**
    	 * ���ݲ���ƴ����
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
    	
    	//ȥ����ǰ���Ǹ�"and"
    	if(strConditionNames.length()>0){
    		strConditionNames = strConditionNames.substring(3);
    	}
    	
    	//ȡ�ý����
    	Collection cl =dao.retrieveByClause(ParamVO.class,strConditionNames);
    	if(cl.isEmpty()){
    		return null;
    	}

    	//ת�������鷵�ؽ��
    	ParamVO[] o = (ParamVO[])Array.newInstance(ParamVO.class, cl.size());
    	o = (ParamVO[])cl.toArray(o);		
    	if(o==null || o.length <1){ 	
    		return null;
    	}
    	
    	return o;		
	}


	/**
	 * ���ݲ�������ȡ�ò���VO
	 */
	public ParamVO queryParamByParamPk(String pkParam) throws BusinessException {
		BaseDAO dao = new BaseDAO();
    	return (ParamVO)dao.retrieveByPK(ParamVO.class,pkParam);
	}

	/**
	 * ���²���VO
	 */
	public void updateParams(ParamVO[] paramVos) throws BusinessException {
    	BaseDAO dao = new BaseDAO();
    	dao.updateVOArray(paramVos);
	}
	
	/**
	 * �������VO
	 */
	public void insertParams(ParamVO[] paramVos) throws BusinessException {
    	BaseDAO dao = new BaseDAO();
    	dao.insertVOArray(paramVos);
	}

}
