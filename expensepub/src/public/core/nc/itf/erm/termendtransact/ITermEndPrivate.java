
package nc.itf.erm.termendtransact;

/**
 * <p>
 *   ��ĩ����
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
     *   ���� �õ�û��ƾ֤�ĵ�����Ϣ
     * </p>
     * 
     * @param hash_bill ����
     * @param sysBz ϵͳ����
     * @param hash_bill_type ��������
     * @param vetResult ����
     * @return ����
     * @throws BusinessException
     */
    public Vector<Vector<String>> getNoVouchiDocs(Hashtable hash_bill, int sysBz, Hashtable<String,String> hash_bill_type,Vector<Vector<String>> vetResult,String pk_corp)throws BusinessException;
    
    /**
     * <p>
     *   ���� ��ĩ���
     * </p>
     * <p>
       * @throws BusinessException
     */
    public RemoteTransferVO onReckoningCheck(FilterCondVO voCond, AgiotageVO voCurrency)throws BusinessException;

	/**
	 * ��ĩ�����̨����
	 * @param prodId ϵͳ���� 
	 * @param endVO  ����VO	
	 * @param isCancel  �Ƿ������
	 * @throws BusinessException
	 */
	public void termEndOperation(String prodId, TermEndVO endVO, boolean isCancel) throws BusinessException;

}
