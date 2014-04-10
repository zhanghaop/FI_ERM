/*
 * �������� 2005-11-8
 *
 * TODO Ҫ���Ĵ����ɵ��ļ���ģ�壬��ת��
 * ���� �� ��ѡ�� �� Java �� ������ʽ �� ����ģ��
 */
package nc.impl.erm.termendtransact;

import java.util.Hashtable;
import java.util.Vector;

import nc.bs.erm.termendtransact.ReckoningBO;
import nc.itf.erm.termendtransact.ITermEndPrivate;
import nc.vo.erm.termendtransact.AgiotageVO;
import nc.vo.erm.termendtransact.FilterCondVO;
import nc.vo.erm.termendtransact.RemoteTransferVO;
import nc.vo.erm.termendtransact.TermEndVO;
import nc.vo.pub.BusinessException;

/**
 * @author xuhb
 *
 * TODO Ҫ���Ĵ����ɵ�����ע�͵�ģ�壬��ת��
 * ���� �� ��ѡ�� �� Java �� ������ʽ �� ����ģ��
 */
public class TermEndPrivateImpl implements ITermEndPrivate {

	/**
	 * 
	 */
	public TermEndPrivateImpl() {
		super();
		// TODO �Զ����ɹ��캯�����
	}

    /* ���� Javadoc��
     * @see nc.itf.arap.prv.ITermEndPrivate#getNoVouchiDocs(java.util.Hashtable, int, java.util.Hashtable, java.util.Vector)
     */
    public Vector<Vector<String>> getNoVouchiDocs(Hashtable hash_bill, int sysBz,
    		Hashtable<String,String> hash_bill_type, Vector<Vector<String>> vetResult,String pk_corp)
            throws BusinessException {
        // TODO �Զ����ɷ������
        return new ReckoningBO().getNoVouchiDocs(hash_bill,sysBz,hash_bill_type,vetResult,pk_corp);
    }

    /* ���� Javadoc��
     * @see nc.itf.arap.prv.ITermEndPrivate#onReckoningCheck(nc.vo.arap.termendtransact.FilterCondVO, nc.vo.arap.agiotage.AgiotageVO)
     */
    public RemoteTransferVO onReckoningCheck(FilterCondVO voCond,
            AgiotageVO voCurrency) throws BusinessException {
        // TODO �Զ����ɷ������
        return new ReckoningBO().onReckoningCheck(voCond,voCurrency);
    }
    
	public void termEndOperation(String prodId, TermEndVO endVO,boolean isCancel) throws BusinessException {
		new ReckoningBO().termEndOperation(prodId , endVO,isCancel);
	}

}
