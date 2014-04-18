package nc.vo.ep.bx;


/** 
 *  常用单据主表vo
 * <b>Date:</b>2013-2-20<br>
 * @author：wangyhh@ufida.com.cn
 * @version $Revision$
 */ 
public class InitHeaderVO extends JKBXHeaderVO {

	private static final long serialVersionUID = 1L;

	public InitHeaderVO(){
		super();
	}

	@Override
	public String getTableName() {
		return "er_jkbx_init";
	}

}
