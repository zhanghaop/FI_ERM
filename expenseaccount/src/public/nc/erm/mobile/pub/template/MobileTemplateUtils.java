package nc.erm.mobile.pub.template;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.pub.BusinessException;

public class MobileTemplateUtils {
	private static class ListResultSetProcessor implements ResultSetProcessor
    {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Object handleResultSet(ResultSet rs) throws SQLException {
			List<String> topPks = new ArrayList<String>();
			while(rs.next()){
				topPks.add(rs.getString(1));
			}
	        return topPks;
	     }
    }
	private static BaseDAO basedao;
	private static BaseDAO getBasedao() {
		if (basedao == null) {
			basedao = new BaseDAO();
		}
		return basedao;
	}
	
	private static <T> boolean isEmpty(Collection<T> c) {
		return c == null || c.size() == 0;
	}
	
	@SuppressWarnings("unchecked")
	public static String getTemplatePK(String djlxbm) throws BusinessException {
		String pk_corp = InvocationInfoProxy.getInstance().getGroupId();
		ListResultSetProcessor processor = new ListResultSetProcessor();
		String sql = "select PK_BILLTEMPLET from PUB_BILLTEMPLET where PK_BILLTYPECODE = '" + djlxbm 
			+ "' and pk_corp = '" + pk_corp + "' and BILL_TEMPLETCAPTION LIKE '" + djlxbm + "_M%'";
		List<String> templetpks = (List<String>)getBasedao().executeQuery(sql, processor);
		if (isEmpty(templetpks)){
			return null;
		}else{
			return templetpks.get(0);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static String getTemplateTS(String djlxbm) throws BusinessException {
		String pk_corp = InvocationInfoProxy.getInstance().getGroupId();
		ListResultSetProcessor processor = new ListResultSetProcessor();
		String sql = "select ts from PUB_BILLTEMPLET where PK_BILLTYPECODE = '" + djlxbm 
			+ "' and pk_corp = '" + pk_corp + "' and BILL_TEMPLETCAPTION LIKE '" + djlxbm + "_M%'";
		List<String> templetpks = (List<String>)getBasedao().executeQuery(sql, processor);
		if (isEmpty(templetpks)){
			return null;
		}else{
			return templetpks.get(0);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static String getTemplatePK_W(String djlxbm) throws BusinessException {
		String pk_corp = InvocationInfoProxy.getInstance().getGroupId();
		ListResultSetProcessor processor = new ListResultSetProcessor();
		String xtdjlx = "2641,2642,2643,2644,2645,2646";
		if(!xtdjlx.contains(djlxbm)){
			//�Զ��彻�����ͣ����Ȳ��ҵ�ǰ���ŵ�ǰ�������͵�ģ�壬��������Ϊdjlxbm_W
			String sql = "select PK_BILLTEMPLET from PUB_BILLTEMPLET where pk_corp = '" 
			+ pk_corp + "' and BILL_TEMPLETNAME = '" + djlxbm + "_W'";
			List<String> ts = (List<String>)getBasedao().executeQuery(sql, processor);
			if (isEmpty(ts)){
				//Ϊ�������ϵͳԤ�õĲ��÷ѱ��������ƶ�ģ��
				sql = "select PK_BILLTEMPLET from PUB_BILLTEMPLET where PK_BILLTYPECODE = '2641_W' and pk_corp = '@@@@'";
				ts = (List<String>)getBasedao().executeQuery(sql, processor);
			}
			if (isEmpty(ts)){
				return null; 
			} 
			return ts.get(0);
		}else{
			//Ԥ�Ƶ������ͣ����Ȳ��ҵ�ǰ���ŵ�ǰ�������͵�ģ�壬��������Ϊdjlxbm_W
			String sql = "select PK_BILLTEMPLET from PUB_BILLTEMPLET where PK_BILLTYPECODE = '" + djlxbm 
			+ "_W' and pk_corp = '" + pk_corp + "' and BILL_TEMPLETNAME = '" + djlxbm + "_W'";
			List<String> ts = (List<String>)getBasedao().executeQuery(sql, processor);
			if (isEmpty(ts)){
				//Ϊ�������ϵͳԤ�õĵ�ǰ�������͵�
				sql = "select PK_BILLTEMPLET from PUB_BILLTEMPLET where PK_BILLTYPECODE = '" + djlxbm 
				+ "_W' and pk_corp = '@@@@'";
				ts = (List<String>)getBasedao().executeQuery(sql, processor);
				if (isEmpty(ts)){
					//��Ϊ�������ϵͳԤ�õĲ��÷ѱ�������ģ��ģ��
					sql = "select PK_BILLTEMPLET from PUB_BILLTEMPLET where PK_BILLTYPECODE = '2641_W' and pk_corp = '@@@@'";
					ts = (List<String>)getBasedao().executeQuery(sql, processor);
				}
			}
			if (isEmpty(ts)){
				return null; 
			} 
			return ts.get(0);
		}
	}

}
