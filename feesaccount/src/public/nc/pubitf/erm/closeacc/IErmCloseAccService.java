package nc.pubitf.erm.closeacc;

import nc.bs.bd.service.ValueObjWithErrLog;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.vo.erm.annotation.CloseAccBiz;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;


/**
 * ������ĩ���˷���
 * 
 * @author wangled
 */
@Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAcc, description = "����"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
public interface IErmCloseAccService {
	/**
	 * ����
	 * @param closeAccBookVO
	 * @throws BusinessException
	 */
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAcc, description = "����"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
	public ValueObjWithErrLog closeAcc_RequiresNew(CloseAccBookVO closeAccBookVO)throws BusinessException ;
    
    
	/**
	 * �����������
	 * @param closeAccBookVO
	 * @throws BusinessException
	 */
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAcc, description = "��������"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
	public ValueObjWithErrLog[] batchcloseAcc(CloseAccBookVO[] closeAccBookVO)throws BusinessException ;
    
    
	/**
 	* ������
 	* @param closeAccBookVO
 	* @throws BusinessException
 	*/
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.UnCloseAcc, description = "ȡ������"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
	public CloseAccBookVO uncloseAcc_RequiresNew(CloseAccBookVO closeAccBookVO)throws BusinessException;
    
    
	/**
 	* ������������
 	* @param closeAccBookVO
 	* @throws BusinessException
 	*/
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.UnCloseAcc, description = "����ȡ������"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
	public ValueObjWithErrLog[] batchuncloseAcc(CloseAccBookVO[] closeAccBookVO)throws BusinessException;
    
}
