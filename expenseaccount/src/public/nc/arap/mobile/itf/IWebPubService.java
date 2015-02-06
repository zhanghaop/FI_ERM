package nc.arap.mobile.itf;


import java.util.ArrayList;
import java.util.HashMap;
import nc.bs.dao.DAOException;

import org.codehaus.jettison.json.JSONArray;

/**
 * 
 * @author gaotn
 *
 */

public interface IWebPubService {
	
	public HashMap<String,String> getClassnameMap(String headvoclassname) throws Exception;
	
	public HashMap<String,String> getMainJobDept(String pk_psndoc) throws Exception;
	
	public HashMap<String,String> executeQuery(String sql) throws Exception;
	
	public Object getRelationItemValue(String editItemClassname, String editItemId, String relationitem, Object itemvalue) throws Exception;
	
	/*
	 * ��װ��ͨ����json����
	 */
	public ArrayList<HashMap<String,String>> getCommonRefJSON(String content,String reftype,String filterCondition,String pk_group,String pk_org,String pk_user) throws Exception;	
	/*
	 * ��װ���β���json����
	 */
	public ArrayList<HashMap<String,String>> getfirstLevelTreeRefJSON(String reftype,String filterCondition,String pk_group,String pk_org,String pk_user) throws Exception;
	public ArrayList<HashMap<String,String>> getnextLevelTreeRefJSON(String reftype,String fatherField,String filterCondition,String pk_group,String pk_org,String pk_user) throws Exception;
	/*
	 * ��װ�����ݲ���json����
	 */
	public ArrayList<HashMap<String,String>> getBlobRefJSON(String keyword,String condition,String filterCondition,String reftype,String pk_group,String pk_org,String pk_user) throws Exception;
	/*
	 * ��װ�����ݲ��շ�����Ϣjson����
	 */
	public ArrayList<HashMap<String,String>> getBlobRefClassJSON(String reftype,String pk_group,String pk_org,String pk_user) throws Exception;
	/*
	 * �ͷŶ�̬��
	 */
	public void releaseLock() throws Exception;
	/*
	 * ���ӽ�������
	 */
	public void saveTranstype(String transtype,String name) throws DAOException;
}
