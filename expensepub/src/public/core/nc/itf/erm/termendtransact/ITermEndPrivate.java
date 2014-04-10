
package nc.itf.erm.termendtransact;

/**
 * <p>
 *   期末处理
 * </p>
 * <p>
 */

import java.util.Hashtable;
import java.util.Vector;
import nc.vo.erm.termendtransact.AgiotageVO;
import nc.vo.erm.termendtransact.FilterCondVO;
import nc.vo.erm.termendtransact.RemoteTransferVO;
import nc.vo.erm.termendtransact.TermEndVO;
import nc.vo.pub.BusinessException;

public interface ITermEndPrivate{
    /**
     * <p>
     *   功能 得到没做凭证的单据信息
     * </p>
     * 
     * @param hash_bill 单据
     * @param sysBz 系统币种
     * @param hash_bill_type 单据类型
     * @param vetResult 单据
     * @return 单据
     * @throws BusinessException
     */
    public Vector<Vector<String>> getNoVouchiDocs(Hashtable hash_bill, int sysBz, Hashtable<String,String> hash_bill_type,Vector<Vector<String>> vetResult,String pk_corp)throws BusinessException;
    
    /**
     * <p>
     *   功能 月末检查
     * </p>
     * <p>
       * @throws BusinessException
     */
    public RemoteTransferVO onReckoningCheck(FilterCondVO voCond, AgiotageVO voCurrency)throws BusinessException;

	/**
	 * 月末处理后台操作
	 * @param prodId 系统编码 
	 * @param endVO  处理VO	
	 * @param isCancel  是否反向操作
	 * @throws BusinessException
	 */
	public void termEndOperation(String prodId, TermEndVO endVO, boolean isCancel) throws BusinessException;

}
