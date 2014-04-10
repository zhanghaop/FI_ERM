/*
 * 创建日期 2005-11-8
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
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
 * TODO 要更改此生成的类型注释的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
public class TermEndPrivateImpl implements ITermEndPrivate {

	/**
	 * 
	 */
	public TermEndPrivateImpl() {
		super();
		// TODO 自动生成构造函数存根
	}

    /* （非 Javadoc）
     * @see nc.itf.arap.prv.ITermEndPrivate#getNoVouchiDocs(java.util.Hashtable, int, java.util.Hashtable, java.util.Vector)
     */
    public Vector<Vector<String>> getNoVouchiDocs(Hashtable hash_bill, int sysBz,
    		Hashtable<String,String> hash_bill_type, Vector<Vector<String>> vetResult,String pk_corp)
            throws BusinessException {
        // TODO 自动生成方法存根
        return new ReckoningBO().getNoVouchiDocs(hash_bill,sysBz,hash_bill_type,vetResult,pk_corp);
    }

    /* （非 Javadoc）
     * @see nc.itf.arap.prv.ITermEndPrivate#onReckoningCheck(nc.vo.arap.termendtransact.FilterCondVO, nc.vo.arap.agiotage.AgiotageVO)
     */
    public RemoteTransferVO onReckoningCheck(FilterCondVO voCond,
            AgiotageVO voCurrency) throws BusinessException {
        // TODO 自动生成方法存根
        return new ReckoningBO().onReckoningCheck(voCond,voCurrency);
    }
    
	public void termEndOperation(String prodId, TermEndVO endVO,boolean isCancel) throws BusinessException {
		new ReckoningBO().termEndOperation(prodId , endVO,isCancel);
	}

}
