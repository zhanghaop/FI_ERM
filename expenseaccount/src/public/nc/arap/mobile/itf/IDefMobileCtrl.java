package nc.arap.mobile.itf;

import java.util.Map;

import nc.vo.pub.BusinessException;
/**
 * �Զ��彻�����͵��ݵ���ɾ�Ĳ�
 */
public interface IDefMobileCtrl {
	/**
	 * ��ȡ��ͬ���ڵ��µĵ��ݽڵ㣬��nc�Ĺ���Ȩ����
	 * 
	 */
	public String getBXbilltype(String userid,String flag) throws BusinessException;
	/**
	 * ���浥��
	 * 
	 */
	public String saveJkbx(Map<String,Object> map, String djlxbm,String userid) throws BusinessException;
	/**
	 * �ύ����
	 * @param userid
	 * @param pk_jkbx
	 * @param djlxbm
	 * @return
	 * @throws BusinessException
	 */
	public String commitDefJkbx(String userid,String pk_jkbx,String djlxbm,String djdl) throws BusinessException;
	/**
	 * ���ݵ������ͱ����ȡ��ӦDSL�ļ�
	 */
	public String getDslFile(String userid,String djlxbm,String nodecode, String flag) throws BusinessException;
	/**
	 * �õ�����
	 * 
	 */
	public String getRefList(String userid,String query,String pk_org, String reftype,String filterCondition)
				throws BusinessException;

	/**
	 * ���ݵ������ͱ����ȡ��ӦDSL�ļ�
	 */
	public String getItemDslFile(String userid,String djlxbm,String nodecode,String tablecode, String flag) throws BusinessException;
	/**
	 * �����Զ���ģ��ĵ���
	 */
	public String addDefJkbx(String bxdcxt,String djlxbm,String userid) throws BusinessException;
	/**
	 * tsУ��
	 */
	public String validateTs(String userid,String djlxbm,String nodecode,String tsflag) throws BusinessException;
	
	/**
	 * ����pk��ѯ����
	 */
	public String getJkbxCard(String pk_jkbx,String userid,String djlxbm,String djlxmc,String getbillflag) throws BusinessException;
	
	/**
	 * ��ѯ���ݸ�������
	 */
	public String getAttachFile(String pk_jkbx,String userid) throws BusinessException;
	/**
	 * �༭���¼�
	 */
	public String doAfterEdit(String editinfo,String userid) throws BusinessException;
	/**
	 * ɾ����
	 */
	public String delLine(String userid,String ctx,String itemno,String djlxbm) throws BusinessException;
	/**
	 * ���ݱ�ͷ��ñ���Ĭ��ֵ
	 * @param userid �û�pk
	 * @param djlxbm �������ͱ���
	 * @param head ��ͷ��Ϣ��json��ʽ
	 * @param tablecode ��ǰҳǩ����
	 * @param itemnum �ڼ��б���
	 * @param classname  ��������Ӧ��vo��class����
	 * @return
	 * @throws BusinessException
	 */
	public String getItemInfo(String userid,String djlxbm,String head,String tablecode,String itemnum,String classname) throws BusinessException;
}
