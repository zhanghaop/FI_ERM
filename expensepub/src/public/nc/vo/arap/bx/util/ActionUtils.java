package nc.vo.arap.bx.util;

public class ActionUtils {

	/**
	 * @param djzt ����״̬
	 * @param operation ִ�ж��� 
	 * @param statusAllowed ������ִ�е�״ֵ̬
	 * @return �ձ�ʾ��֤ͨ��, ���򷵻ش�����ʾ��Ϣ
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
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "����δȷ��"*/;
				break;
			}
			case -99: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "����δȷ��"*/;
				break;
			}
			case 1: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000263")/*@res "����δ���"*/;
				break;
			}
			case 2: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000264")/*@res "�����Ѿ����"*/;
				break;
			}
			case 3: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000265")/*@res "�����Ѿ�ǩ��"*/;
				break;
			}
			default: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000266")/*@res "����״̬����"*/;
				break;
			}
		}

		return strMessage+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000267")/*@res ",����"*/+operationName;
	}


	/**
	 * @param djzt ����״̬
	 * @param operation ִ�ж��� , @see {@link MessageVO}
	 * @param statusAllowed ������ִ�е�״ֵ̬
	 * @param statusNotAllowed ��������ִ�е�״ֵ̬
	 * @return �ձ�ʾ��֤ͨ��, ���򷵻ش�����ʾ��Ϣ
	 */
	public static String checkBillStatus(int djzt, int operation, int[] statusAllowed,int[] statusNotAllowed) {

		String strMessage = null;

		String operationName = getOperationName(operation);

		switch (djzt) {
			case 0: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "����δȷ��"*/;
				break;
			}
			case -99: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000262")/*@res "����δȷ��"*/;
				break;
			}
			case 1: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000263")/*@res "����δ���"*/;
				break;
			}
			case 2: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000264")/*@res "�����Ѿ����"*/;
				break;
			}
			case 3: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000265")/*@res "�����Ѿ�ǩ��"*/;
				break;
			}
			default: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000266")/*@res "����״̬����"*/;
				break;
			}
		}

		for (int i = 0; i < statusAllowed.length; i++) {
			if(statusAllowed[i]==djzt)
				return null;
		}

		for (int i = 0; i < statusNotAllowed.length; i++) {
			if(statusNotAllowed[i]==djzt)
				return strMessage+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000267")/*@res ",����"*/+operationName;
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
	
	public static final int COMMIT = 8;//�ύ
    
    public static final int RECALL = 9;//�ջ�
    
    public static final int CLOSE = 10;//�ر�
    
    public static final int OPEN = 11;//����
    
    public static final int EXPAMORTIZE = 12;//̯��
    
    public static final int CONFIRM = 13;//ȷ��
    public static final int UNCONFIRM = 14;//��ȷ��
    
    
	public static String getOperationName(int oper){
		String name = "";
		switch (oper) {
		case ADD:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000061")/*@res "����"*/;
			break;
		case AUDIT:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000194")/*@res "����"*/;
			break;
		case UNAUDIT:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000304")/*@res "ȡ������"*/;
			break;
		case SETTLE:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000305")/*@res "ǩ��"*/;
			break;
		case UNSETTLE:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000306")/*@res "��ǩ��"*/;
			break;
		case EDIT:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000055")/*@res "�޸�"*/;
			break;
		case DELETE:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000307")/*@res "ɾ��"*/;
			break;
		case CONTRAST:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000075")/*@res "��������"*/;
			break;
        case COMMIT:
            name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0065")/*@res "�ύ"*/;
            break;
        case RECALL:
            name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0066")/*@res "�ջ�"*/;
            break;
        case CLOSE:
            name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0067")/*@res "�ر�"*/;
            break;
        case OPEN:
            name = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0068")/*@res "����"*/;
            break;
        case EXPAMORTIZE:
        	name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0067")/*@res "̯��"*/;
			break;
        case CONFIRM:
        	name =nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0001")/*@res "ȷ��"*/;
        	break;
        case UNCONFIRM:
        	name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0008")/*@res "ȡ��ȷ��"*/;
        	break;
		default:
			break;
		}

		return name;
	}
}