package nc.vo.erm.termendtransact;

/**
 * 月末处理提示信息类。
 * 创建日期：(2001-10-24 20:21:56)
 * @author：wyan
 */
public class TermEndMsg {

	public static String getm_sCheckBeg(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030201","UPP2006030201-000281")/*@res "正在进行月末检查，请稍候......"*/;
	}
	public static String getm_sCheckEnd(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030201","UPP2006030201-000282")/*@res "月末检查完成！"*/;
	}
	public static String getm_sCheckFail(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030201","UPP2006030201-000283")/*@res "月末检查失败！"*/;
	}
	public static String getm_sReckoningSuc(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030201","UPP2006030201-000284")/*@res "月末结账成功！"*/;
	}
	public static String getm_sReckoningFail(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030201","UPP2006030201-000285")/*@res "月末结账失败！"*/;
	}
	public static String getm_sCancelReckoningSuc(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030201","UPP2006030201-000286")/*@res "取消结账成功！"*/;
	}
	public static String getm_sCancelReckoningFail(){
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030201","UPP2006030201-000287")/*@res "取消结账失败！"*/;
	}

/**
 * TermEndMsg 构造子注解。
 */
public TermEndMsg() {
	super();
}
}