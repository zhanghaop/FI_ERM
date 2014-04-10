package nc.ui.arap.bx.remote;

import java.util.List;
import java.util.Map;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.arap.bx.BXBillMainPanel;
import nc.vo.arap.service.ServiceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
/**
 * �ͻ��˶�VO��ѯ��Զ�̵���
 * @author chendya
 *
 */
public abstract class AbstractVORemoteCall extends AbstractCall {

	public AbstractVORemoteCall(BXBillMainPanel panel) {
		super(panel);
	}
	
	/**
	 * ���ش�Զ�̵��ú�õ��Ľ�����ڿͻ��˵Ļ���(WorkbenchEnvironment)�еı�ʶ
	 * @return
	 */
	protected abstract String getCacheKey();

	/**
	 * ���ط����Ĳ����Ͳ���ֵ
	 * @return
	 */
	protected abstract String getWhereCondition();
	
	/**
	 * ����Ҫ��ѯ��VO�ֶ�
	 * @return
	 */
	protected abstract String[] getQryField();

	/**
	 * ����Ҫ��ѯ��VO��
	 * @return
	 */
	protected abstract Class<?> getVoClass();

	@Override
	public ServiceVO getServcallVO() {
		ServiceVO callvo = new ServiceVO();
		callvo.setClassname("nc.itf.arap.prv.IBXBillPrivate");
		callvo.setMethodname("getVORemoteCall");
		callvo.setParamtype(new Class[] { Class.class, String.class,String[].class });
		callvo.setParam(new Object[] { getVoClass(), getWhereCondition(), getQryField()});
		return callvo;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void handleResult(Map<String, Object> values) throws BusinessException {
		java.util.List<SuperVO> list = (List<SuperVO>) values.get(callvo.getCode());
		if (list == null) {
			return;
		}
		WorkbenchEnvironment.getInstance().putClientCache(getCacheKey(), list);
	}
	
	/**
	 * ���� ���� field in ('values1','value2','...')�ַ�����
	 * @param values
	 * @param withBrackets �Ƿ��С����
	 * @return
	 */
	public static String createInSql(Object[] values,boolean withBrackets){
		StringBuffer buf = new StringBuffer();
		//�Ƿ�Ϊ�ַ�������
		boolean isStringType = values instanceof String[]; 
		if(withBrackets){
			buf.append(" (");
		}
		for (int i = 0; i < values.length; i++) {
			if(i==0){
				if(isStringType){
					buf.append("'"+values[i]+"'");
				}else{
					buf.append(values[i]);
				}
			}else{
				if(isStringType){
					buf.append(",").append("'"+values[i]+"'");
				}else{
					buf.append(",").append(values[i]);
				}
			}
		}
		if(withBrackets){
			buf.append(") ");
		}
		return buf.toString();
	}

}
