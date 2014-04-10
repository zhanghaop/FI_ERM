/*
 * �������� 2005-9-14
 *
 */
package nc.vo.er.util;
import java.lang.reflect.Array;
import java.util.Collection;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

public class ArapCommonTool {

	public static final UFDouble ZERO=new UFDouble("0");
	public static String fb_temp_pk="ARAP_FB_OID_";

	public static boolean isZero(UFDouble u){
		if(null==u){
			return false;
		}
		if(u.compareTo(ZERO)==0){
			return true;
		}
		return false;
	}
	public static boolean isLargeZero(UFDouble u){
		if(null==u){
			return false;
		}
		if(u.compareTo(ZERO)>0){
			return true;
		}
		return false;
	}
	public static boolean isLessZero(UFDouble u){
		if(null==u){
			return false;
		}
		if(u.compareTo(ZERO)<0){
			return true;
		}
		return false;
	}

	public static boolean isNotZero(UFDouble u){
		return isNotEqual(u, getZero());
	}

	/**
	 * @param u
	 * @param t
	 * @return u is greater than t
	 */
	public static boolean isLarge(UFDouble u,UFDouble t){
		if(null==u || null ==t){
			return false;
		}
		if(u.compareTo(t)>0){
			return true;
		}
		return false;
	}

	/**
	 * @param u
	 * @param t
	 * @return u is greater than t or equal to t
	 */
	public static boolean isLargeEqual(UFDouble u,UFDouble t){
		if(null==u || null ==t){
			return false;
		}
		if(u.compareTo(t)>=0){
			return true;
		}
		return false;
	}

	public static boolean isLess(UFDouble u,UFDouble t){
		if(null==u || null ==t){
			return false;
		}
		if(u.compareTo(t)<0){
			return true;
		}
		return false;
	}

	public static boolean isLessEqual(UFDouble u,UFDouble t){
		if(null==u || null ==t){
			return false;
		}
		if(u.compareTo(t)<=0){
			return true;
		}
		return false;
	}

	public static boolean isEqual(UFDouble u,UFDouble t){
		if(null==u || null ==t){
			return false;
		}
		if(u.compareTo(t)==0){
			return true;
		}
		return false;
	}

	public static boolean isNotEqual(UFDouble u,UFDouble t){
		if(null==u || null ==t){
			return false;
		}
		if(u.compareTo(t)!=0){
			return true;
		}
		return false;
	}

	public static UFDouble getZero(){
		return  new UFDouble("0");
	}

	public static String getMsgByClbz(Integer clbz){

		String msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000058")/*@res "��������"*/;
		switch (clbz) {
			case -2:
				msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000059")/*@res "�����ۿ�"*/;
				break;
			case -1:
				msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000060")/*@res "����ֺ���"*/;
				break;
			case 0:
				msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000061")/*@res "ͬ���ֺ���"*/;
				break;
			case 1:
				msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000062")/*@res "�������"*/;
				break;
			case 2:
				msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000063")/*@res "�����Գ�"*/;
				break;
			case 3:
				msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000064")/*@res "���˷���"*/;
				break;
			case 4:
				msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000065")/*@res "�����ջ�"*/;
				break;
			case 5:
				msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000066")/*@res "����ת��"*/;
				break;
			case 6:
				msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000067")/*@res "����ת��"*/;
				break;
			case 10:
				msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000068")/*@res "�����"*/;
				break;
			case 11:
				msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000069")/*@res "��������"*/;
				break;
			case 12:
				msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000070")/*@res "��ʵ�ֻ������"*/;
				break;
			case 13:
				msg=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000071")/*@res "�������س�"*/;
				break;
			case 10000:
				msg="";
				break;
			default:
				break;
		}
		return msg;
	}
	public static String getClbzMessage(int clbz){

		String msgByClbz = getMsgByClbz(clbz);

		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004","UPP2004-000072")/*@res "�����Ѵ���-"*/+msgByClbz;

	}
	@SuppressWarnings("unchecked")
	public static Object changeCollection2Array(Collection cl, Class sClass) {
		if (cl.isEmpty()) {
			return null;
		}
		Object[] o = (Object[]) Array.newInstance(sClass, cl.size());
		o = cl.toArray(o);
		return o;

	}
	public static UFDouble getLogicZero(String pk_currency) throws BusinessException{
		/**
		 * rocking,����������������ڱȽ��е�"0",Ŀǰ���ܶ�ط�����getZero()ʹ��
		 */
		return  getZero();
//		int digit=Currency.getCurrDigit(pk_currency);
//		if(digit==0){
//			return new UFDouble("0");
//		}
//		StringBuffer str=new StringBuffer("");
//		for(int i=0;i<digit;i++ ){
//			if(str.length()==0)
//				str.append("0.");
//			else
//				str.append("0");
//		}
//		str.append("1");
//		return new UFDouble(str.toString());
	}
}