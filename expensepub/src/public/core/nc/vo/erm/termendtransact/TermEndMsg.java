package nc.vo.erm.termendtransact;

/**
 * ��ĩ������ʾ��Ϣ�ࡣ
 * �������ڣ�(2001-10-24 20:21:56)
 * @author��wyan
 */
public class TermEndMsg {

	public static String getm_sCheckBeg(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030201","UPP2006030201-000281")/*@res "���ڽ�����ĩ��飬���Ժ�......"*/;
	}
	public static String getm_sCheckEnd(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030201","UPP2006030201-000282")/*@res "��ĩ�����ɣ�"*/;
	}
	public static String getm_sCheckFail(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030201","UPP2006030201-000283")/*@res "��ĩ���ʧ�ܣ�"*/;
	}
	public static String getm_sReckoningSuc(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030201","UPP2006030201-000284")/*@res "��ĩ���˳ɹ���"*/;
	}
	public static String getm_sReckoningFail(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030201","UPP2006030201-000285")/*@res "��ĩ����ʧ�ܣ�"*/;
	}
	public static String getm_sCancelReckoningSuc(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030201","UPP2006030201-000286")/*@res "ȡ�����˳ɹ���"*/;
	}
	public static String getm_sCancelReckoningFail(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030201","UPP2006030201-000287")/*@res "ȡ������ʧ�ܣ�"*/;
	}

/**
 * TermEndMsg ������ע�⡣
 */
public TermEndMsg() {
	super();
}
}