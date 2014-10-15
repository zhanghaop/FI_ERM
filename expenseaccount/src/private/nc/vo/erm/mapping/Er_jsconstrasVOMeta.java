package nc.vo.erm.mapping;

import nc.vo.ep.bx.JsConstrasVO;


public class Er_jsconstrasVOMeta extends ArapBaseMappingMeta {

	public Er_jsconstrasVOMeta() {
		super();
		init();
	}
	
	@Override
	public Class<?> getMetaClass() {
		return JsConstrasVO.class;
	}
	
	private static final long serialVersionUID = -1L;

	private void init() {
		
		String[] strings = new String[] { "pk_jsconstras",/* 0 */
				"pk_jsd",/* 1 */
				"pk_bxd",/* 2 */
				"jsh",/* 3 */
				"ts",/* 4 */
				"dr",/* 5 */
				"billflag"/*6*/
				};
		
		setTabName("er_jsconstras");// ����
		setCols(strings);// ���ݿ��ֶ�����
		setAttributes(strings);// vo�ֶ�����
		setPk("pk_jsconstras");// ��������
		
		
		setDataTypes(new int[] { TYPE_STRING,/* 0 */
		TYPE_STRING,/* 1 */
		TYPE_STRING,/* 2 */
		TYPE_STRING,/* 3 */
		TYPE_DATETIME,/* 4 */
		TYPE_INT, /* 5 */
		TYPE_INT /* 6 */
		});// ��������
	}
}
