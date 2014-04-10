package nc.bs.erm.termendtransact;

/**
 * ��ĩ����BO�ࡣ
 * �������ڣ�(2001-8-21 10:06:57)
 * ����޸����ڣ�(2001-8-21 10:06:57)
 * @author��wyan
 */

import java.util.Hashtable;
import java.util.Vector;
import nc.bs.er.callouter.FipCallFacade;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.termendtransact.AgiotageVO;
import nc.vo.erm.termendtransact.FilterCondVO;
import nc.vo.erm.termendtransact.RemoteTransferVO;
import nc.vo.erm.termendtransact.ReportVO;
import nc.vo.erm.termendtransact.RetBillVo;
import nc.vo.erm.termendtransact.TermEndVO;
import nc.vo.pub.BusinessException;

public class ReckoningBO {
/**
 * ReckoningBO ������ע�⡣
 */
public ReckoningBO() {
	super();
}

/**
 * �ӻ��ƽ̨��ý�ֹ������û�����ɻ��ƾ֤�ĵ�����Ϣ��
 * �������ڣ�(2002-6-6 14:38:55)
 * @return java.util.Vector
 * @param vo nc.vo.arap.termendtransact.FilterCondVO
 */
private RetBillVo[] getNotCreateVoucherDoc(FilterCondVO vo) throws BusinessException {

    RetBillVo[] voNoCreateVouDocs = null;
    try {
        String dwbm = vo.getDwbm();
        String year = vo.getYear();
        String month = vo.getQj();
        voNoCreateVouDocs = new FipCallFacade().getPeriodNotCompleteBill(year+month, dwbm, "EC");
    } catch (Exception e) {
    	ExceptionHandler.handleException(this.getClass(),e);
    }

    return voNoCreateVouDocs;
}
/**
 * ����:�õ�û��ƾ֤�ĵ�����Ϣ
 * ���ߣ�����
 * ����ʱ�䣺(2004-6-16 12:56:15)
 * ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ���
 * ע�⣺�ִ�Bug
 *
 *
 * @return java.util.Vector
 * @param hash_bill java.util.Hashtable
 * @param sysBz int
 * @param hash_bill_type java.util.Hashtable
 */
public Vector<Vector<String>> getNoVouchiDocs(Hashtable hash_bill, int sysBz, Hashtable<String,String> hash_bill_type,Vector<Vector<String>> vetResult,String pk_corp) throws BusinessException{

	try{
	 ReckoningDMO dmo = new ReckoningDMO();
	 String tabname = dmo.createHeaderTempTable(hash_bill);

	String sql = "select zb.djlxbm,zb.djbh from arap_djzb zb inner join "+tabname
	+" tmp on zb.djlxbm=tmp.djlxbm and zb.djbh=tmp.djbh where zb.pzglh="+sysBz+" and zb.dr=0 and zb.dwbm='"+pk_corp+"' ";


	vetResult = dmo.queryNoVouchBills(sql,hash_bill_type,vetResult);
	}
	catch (Exception ex) {
		ExceptionHandler.handleException(this.getClass(),ex);
    }
	return vetResult;
}
/**
 * ��Ҫ���ܣ�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-9-21 15:51:09)
 * ����޸����ڣ�(2001-9-21 15:51:09)
 * @author��wyan
 * @return java.util.Vector
 * @param vRep nc.vo.arap.termendtransact.ReportVO
 */
public Vector<Object> getRowTitle(ReportVO vRep) throws BusinessException{

	Vector<Object> vData = new Vector<Object>();
	try{
		vData.add(new Integer(vRep.getBh()));
		vData.add(vRep.getInfo());
		vData.add(vRep.getCount());
	}
	catch (Exception e) {
		ExceptionHandler.handleException(this.getClass(),e);
	}
	return vData;
}
/**
 * ��Ҫ���ܣ���ĩ���
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-8-16 17:58:10)
 * ����޸����ڣ�(2001-8-16 17:58:10)
 * @author��wyan
 */
public RemoteTransferVO onReckoningCheck(FilterCondVO voCond, AgiotageVO voCurrency)
    throws BusinessException {

    RemoteTransferVO voRemote = new RemoteTransferVO();
    ReportVO repVO = new ReportVO();
    Vector<Vector<String>> vCheckData = new Vector<Vector<String>>();
    Vector<Vector<String>> vResult = new Vector<Vector<String>>();
    boolean bState = true; /*���˱�־��true-��ʾ���Խ���     false-��־�����Խ���*/
    try {
        ReckoningDMO dmo = new ReckoningDMO();
        int i = 1; /*��Ϊ�û�����ѡ�����飬�������Ϊ��̬*/
        /*���µ����Ƿ�ȫ����Ч*/
        if (voCond.getMode1() != null) {
            vCheckData = dmo.onReckoningCheckStep1(voCond);
            vResult = vCheckData;
            i++;
            if ((voCond.getMode1().equals("control")) && (vCheckData.size() > 1)) /*��ȥ������*/
                bState = false;

        }
        /*�����ո���Ƿ�ȫ������*/
        if (voCond.getMode2() != null) {
            vCheckData = dmo.onReckoningCheckStep2(voCond);
            Vector<String> vTem = new Vector<String>();
            vTem.addElement(new Integer(i).toString());
            if (voCond.getSfbz().equals("Ys"))
                vTem.addElement(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("200604","UPP200604-000042")/*@res "����δ�������տ"*/);
            if (voCond.getSfbz().equals("Yf"))
                vTem.addElement(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("200604","UPP200604-000043")/*@res "����δ�����ĸ��"*/);
            if (voCond.getSfbz().equals("Bzzx"))
                vTem.addElement(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("200604","UPP200604-000044")/*@res "����δ�������ո��"*/);
            ;
            //vTem.addElement(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("200604","UPP200604-000045")/*@res "��"*/ + vCheckData.size() + nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("200604","UPP200604-000046")/*@res "��"*/);
            vTem.addElement(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("200604","UPP200604-000045",null,new String[]{String.valueOf(vCheckData.size())}))/*@res "��{0}��"*/;
            vResult.addElement(vTem);
            if (vCheckData.size() != 0) {
                for (int j = 0; j < vCheckData.size(); j++) {
                    vResult.addElement(vCheckData.elementAt(j));
                }
            }
            i++;
            if ((voCond.getMode2().equals("control")) && (vCheckData.size() != 0))
                bState = false;
        }
        int num = vResult.size();
        /*���µ����Ƿ�ȫ�����ɻ��ƾ֤*/
        if (voCond.getMode3() != null) {
            Vector<String> vTem = new Vector<String>();
            int iSum = 0;
            RetBillVo[] voNotCreateVouDocs = getNotCreateVoucherDoc(voCond);
            if (voNotCreateVouDocs == null)
                voNotCreateVouDocs = new RetBillVo[0];
            vTem.addElement(new Integer(i).toString());
            vTem.addElement(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("200604","UPP200604-000047")/*@res "����δ���ɻ��ƾ֤����"*/);
            vTem.addElement(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("200604","UC000-0002282")/*@res "����"*/);
            vResult.addElement(vTem);
            int sysBz = -1;
            if (voCond.getSfbz().equals("Ys"))
                sysBz = 0;
            else if (voCond.getSfbz().equals("Yf"))
                sysBz = 1;
            else if (voCond.getSfbz().equals("Bzzx"))
                sysBz = 2;

            if (voNotCreateVouDocs.length != 0) {
                /*����hash���浥�����ͣ�������жϾ����жϷ��ص������Ƿ���*/
                Hashtable<String, String> hash = new Hashtable<String, String>();
                /*���浥������,�貹��*/
                Vector<Vector<String>> vBillType = dmo.queryBillType(voCond.getDwbm());
                Vector<String> vCurBillType = null;
                for (int m = 0; m < vBillType.size(); m++) {
                    vCurBillType = vBillType.get(m);
                    hash.put(vCurBillType.get(0), vCurBillType.get(1));
                }
                /**/
                Hashtable<String, RetBillVo> hash_bill = new Hashtable<String, RetBillVo>();
                for (int j = 0; j < voNotCreateVouDocs.length; j++) {
                    //Vector vDocInfo = new Vector();
                    /*�������ͱ���(D1��)*/
                    String BillTypeNum = ((RetBillVo) voNotCreateVouDocs[j]).getBillType();

                    if (hash.get(BillTypeNum) == null) {
                        continue;
                    } else {
                        /*���ݱ��*/
                        String BillNum = ((RetBillVo) voNotCreateVouDocs[j]).getBillNo();
                        hash_bill.put(BillTypeNum + "_" + BillNum, voNotCreateVouDocs[j]);
                    }

                    String name = "";
					if(hash.get(BillTypeNum)!=null){
						name=hash.get(BillTypeNum);
					}else{
						name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-v56-000085")/*@res "���ݴ���"*/;
					}
					name = BillTypeNum.equals("D9")? nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-v56-000086")/*@res "������"*/: name;
					name = BillTypeNum.equals("DG")? nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-v56-000087")/*@res "���˼��ᵥ"*/: name;
					name = BillTypeNum.equals("DJ") ? nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-v56-000088")/*@res "Ӧ�ջ����ջص�"*/ : name;
					name = BillTypeNum.equals("DL") ? nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-v56-000089")/*@res "Ӧ�ջ����ջغ�����"*/ : name;


                	String BillNum = ((RetBillVo) voNotCreateVouDocs[j]).getBillNo();
                	Vector<String> v = new Vector<String>();
        			v.addElement("");
        			v.addElement(name+ BillNum);
        			v.addElement("");
        			vResult.addElement(v);
                }
            }
            vResult.elementAt(vResult.size() - iSum - 1).setElementAt(
                nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("200604","UPP200604-000045",null,new String[]{String.valueOf(vResult.size() - num - 1)})/*@res "��{0}��"*/ ,2);
            i++;
            if ((voCond.getMode3().equals("control")) && (vResult.size() - num - 1!= 0))
                bState = false;
        }
        /*���µ����Ƿ�ȫ��������������*/
        if (voCond.getMode4() != null) {
            repVO = dmo.onReckoningCheckStep3(voCond, voCurrency);
            Vector<String> vTem = new Vector<String>();
            vTem.addElement(new Integer(i).toString());
            vTem.addElement(repVO.getInfo());
            vTem.addElement("");
            vResult.addElement(vTem);
            i++;
            if ((voCond.getMode4().equals("control")) && (!repVO.getState()))
                bState = false;
        }
        //getDJZB().remove();
        voRemote.setTranData1(vResult); /*���˼������*/
        voRemote.setReckoningState(bState); /*����*/

    } catch (Exception e) {
    	ExceptionHandler.handleException(this.getClass(),e);
    }

    return voRemote;

}

	/**
	 * ��ĩ�����̨����
	 * @param prodId ϵͳ����
	 * @param endVO  ����VO
	 * @param isCancel  �Ƿ������
	 * @throws BusinessException
	 */
	public void termEndOperation(String prodId,TermEndVO endvo,boolean isCancel) throws BusinessException {


		//��ϵͳ֪ͨ�ӿ�
		String funnode=null;
		if(prodId.equals("AR")){
			funnode="200604";
		}else if(prodId.equals("AP")){
			funnode="200804";
		}else{
			funnode="201004";
		}

//		if(isCancel){
//			new BDOperateServ().beforeOperate(funnode,IBDOperate.BUSOPERATION_CANCELMONTHEND,null, null, endvo);
//		}else{
//			new BDOperateServ().beforeOperate(funnode,IBDOperate.BUSOPERATION_MONTHEND,null, null, endvo);
//		}
	}
}