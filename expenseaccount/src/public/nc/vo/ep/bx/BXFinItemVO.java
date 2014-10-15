package nc.vo.ep.bx;

/**
 * 暂时保留，6.0升级到6.1升级代码类专用，其它地方不要引用
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
