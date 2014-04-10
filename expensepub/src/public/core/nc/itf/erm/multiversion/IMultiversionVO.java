package nc.itf.erm.multiversion;

/**
 * ��汾VO�ӿ�
 * @author chendya
 *
 */
public interface IMultiversionVO {

	/**
	 * ���طǶ�汾�ֶκͶ�汾�ֶζ���map
	 * 
	 * @return
	 */
	public java.util.Map<String, String> getFieldMap();

	/**
	 * ���ض�汾VO�������
	 * 
	 * @return
	 */
	public Class<?> getVersionVOClass();

	/**
	 * ���طǶ�汾VO�������
	 * 
	 * @return
	 */
	public Class<?> getVOClass();

	/**
	 * ���ض�汾���ݿ�����Ͷ�汾���ݿ����
	 * 
	 * @return
	 */
	public String[] getTableNames();

	/**
	 * ����Oid��Ӧ�ֶ�
	 * 
	 * @return
	 */
	public String getOidField();

	/**
	 * ����Vid��Ӧ�ֶ�
	 * 
	 * @return
	 */
	public String getVidField();

	/**
	 * ���ض�汾��ʼ�����ֶ�
	 * 
	 * @return
	 */
	public String getVstartdateField();

}
