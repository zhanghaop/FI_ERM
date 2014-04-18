package nc.pubitf.erm.closeacc;

import nc.bs.bd.service.ValueObjWithErrLog;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.vo.erm.annotation.CloseAccBiz;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;


/**
 * 费用期末结账服务
 * 
 * @author wangled
 */
@Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAcc, description = "结账"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
public interface IErmCloseAccService {
	/**
	 * 结账
	 * @param closeAccBookVO
	 * @throws BusinessException
	 */
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAcc, description = "结账"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
	public ValueObjWithErrLog closeAcc_RequiresNew(CloseAccBookVO closeAccBookVO)throws BusinessException ;
    
    
	/**
	 * 批量处理结账
	 * @param closeAccBookVO
	 * @throws BusinessException
	 */
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.CloseAcc, description = "批量结账"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
	public ValueObjWithErrLog[] batchcloseAcc(CloseAccBookVO[] closeAccBookVO)throws BusinessException ;
    
    
	/**
 	* 反结账
 	* @param closeAccBookVO
 	* @throws BusinessException
 	*/
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.UnCloseAcc, description = "取消结账"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
	public CloseAccBookVO uncloseAcc_RequiresNew(CloseAccBookVO closeAccBookVO)throws BusinessException;
    
    
	/**
 	* 批量处理反结账
 	* @param closeAccBookVO
 	* @throws BusinessException
 	*/
    @Business(business = ErmBusinessDef.CloseAcc, subBusiness = CloseAccBiz.UnCloseAcc, description = "批量取消结账"/* -=notranslate=- */, type = BusinessType.DOMAIN_INT)
	public ValueObjWithErrLog[] batchuncloseAcc(CloseAccBookVO[] closeAccBookVO)throws BusinessException;
    
}
