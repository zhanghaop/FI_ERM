package nc.vo.arap.bx.util;

public class ActionUtils {

	/**
	 * @param djzt 单据状态
	 * @param operation 执行动作 
	 * @param statusAllowed 允许动作执行的状态值
	 * @return 空表示验证通过, 否则返回错误提示信息
	 */
	public static String checkBillStatus(int djzt, int operation, int[] statusAllowed) {

		String strMessage = null;

		for (int i = 0; i < statusAllowed.length; i++) {
			if(statusAllowed[i]==djzt)
				return null;
		}

		String operationName = getOperationName(operation);

		switch (djzt) {
			case 0: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "单据未确认"*/;
				break;
			}
			case -99: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "单据未确认"*/;
				break;
			}
			case 1: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000263")/*@res "单据未审核"*/;
				break;
			}
			case 2: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000264")/*@res "单据已经审核"*/;
				break;
			}
			case 3: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000265")/*@res "单据已经签字"*/;
				break;
			}
			case 4:{
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000955");/*@res 单据已作废*/
				break;
			}
			default: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000266")/*@res "单据状态不明"*/;
				break;
			}
		}

		return strMessage+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000267")/*@res ",不能"*/+operationName;
	}


	/**
	 * @param djzt 单据状态
	 * @param operation 执行动作 , @see {@link MessageVO}
	 * @param statusAllowed 允许动作执行的状态值
	 * @param statusNotAllowed 不允许动作执行的状态值
	 * @return 空表示验证通过, 否则返回错误提示信息
	 */
	public static String checkBillStatus(int djzt, int operation, int[] statusAllowed,int[] statusNotAllowed) {

		String strMessage = null;

		String operationName = getOperationName(operation);

		switch (djzt) {
			case 0: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "单据未确认"*/;
				break;
			}
			case -99: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "单据未确认"*/;
				break;
			}
			case 1: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000263")/*@res "单据未审核"*/;
				break;
			}
			case 2: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000264")/*@res "单据已经审核"*/;
				break;
			}
			case 3: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000265")/*@res "单据已经签字"*/;
				break;
			}
			default: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000266")/*@res "单据状态不明"*/;
				break;
			}
		}

		for (int i = 0; i < statusAllowed.length; i++) {
			if(statusAllowed[i]==djzt)
				return null;
		}

		for (int i = 0; i < statusNotAllowed.length; i++) {
			if(statusNotAllowed[i]==djzt)
				return strMessage+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000267")/*@res ",不能"*/+operationName;
		}

		return "";
	}

	public static final int ADD = 0;

	public static final int AUDIT = 1;

	public static final int UNAUDIT = 2;

	public static final int SETTLE = 3;

	public static final int UNSETTLE = 4;

	public static final int EDIT = 5;

	public static final int DELETE = 6;

	public static final int CONTRAST = 7;
	
	public static final int COMMIT = 8;//提交
    
    public static final int RECALL = 9;//收回
    
    public static final int CLOSE = 10;//关闭
    
    public static final int OPEN = 11;//重启
    
    public static final int EXPAMORTIZE = 12;//摊销
    
    public static final int CONFIRM = 13;//确认
    public static final int UNCONFIRM = 14;//反确认
    
    public static final int INVALID = 15;//作废
    
    
	public static String getOperationName(int oper){
		String name = "";
		switch (oper) {
		case ADD:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000061")/*@res "保存"*/;
			break;
		case AUDIT:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000194")/*@res "审批"*/;
			break;
		case UNAUDIT:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000304")/*@res "取消审批"*/;
			break;
		case SETTLE:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000305")/*@res "签字"*/;
			break;
		case UNSETTLE:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000306")/*@res "反签字"*/;
			break;
		case EDIT:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000055")/*@res "修改"*/;
			break;
		case DELETE:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000307")/*@res "删除"*/;
			break;
		case CONTRAST:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000075")/*@res "批量冲借款"*/;
			break;
        case COMMIT:
            name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0065")/*@res "提交"*/;
            break;
        case RECALL:
            name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0066")/*@res "收回"*/;
            break;
        case CLOSE:
            name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0067")/*@res "关闭"*/;
            break;
        case OPEN:
            name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0068")/*@res "重启"*/;
            break;
        case EXPAMORTIZE:
        	name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0067")/*@res "摊销"*/;
			break;
        case CONFIRM:
        	name =nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0001")/*@res "确认"*/;
        	break;
        case UNCONFIRM:
        	name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0008")/*@res "取消确认"*/;
        	break;
        case INVALID:
        	name= "作废";
        	break;
		default:
			break;
		}

		return name;
	}
}