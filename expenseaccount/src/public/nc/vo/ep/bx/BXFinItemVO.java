package nc.vo.ep.bx;

/**
 * ��ʱ������6.0������6.1����������ר�ã������ط���Ҫ����
 * @author chendya
 *
 */

public class BXFinItemVO extends BXBusItemVO {

	private static final long serialVersionUID = -6775639943189464634L;

	public java.lang.String getTableName() {
		return getDefaultTableName();
	}
	
	public static java.lang.String getDefaultTableName() {
		return "er_finitem";
	}
}
