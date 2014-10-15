package nc.vo.erm.jkbx.ext;

import java.util.ArrayList;
import java.util.List;

/**
 * 报销单超申请回写预算时，使用的常量管理
 * 
 * @author lvhj
 *
 */
public class BXMaYsConst {

	public static final List<String> bxYsFields = new ArrayList<String>();
	static {
		bxYsFields.add("billdate");
		bxYsFields.add("approvedate");
		bxYsFields.add("effectdate");
		bxYsFields.add("pk_billtype");
		bxYsFields.add("operator_org");
		bxYsFields.add("operator_dept");
	}
}
