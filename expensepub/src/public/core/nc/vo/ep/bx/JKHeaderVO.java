package nc.vo.ep.bx;

/**
 * ����ͷVO����֧�ָ���vo classname ��ѯԪ����ʵ��������չ��
 * @author chendya
 *
 */
public class JKHeaderVO extends JKBXHeaderVO {
	
	private static final long serialVersionUID = -6568273138082575715L;

	public JKHeaderVO(){
		super();
	}

	@Override
	public String getTableName() {
		if(isInit())
			return "er_jkbx_init";
		return "er_jkzb";
	}
}
