package nc.ui.arap.bx.remote;

import java.util.List;
import java.util.Map;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.arap.bx.BXBillMainPanel;
import nc.vo.arap.service.ServiceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
/**
 * 客户端对VO查询的远程调用
 * @author chendya
 *
 */
public abstract class AbstractVORemoteCall extends AbstractCall {

	public AbstractVORemoteCall(BXBillMainPanel panel) {
		super(panel);
	}
	
	/**
	 * 返回此远程调用后得到的结果集在客户端的缓存(WorkbenchEnvironment)中的标识
	 * @return
	 */
	protected abstract String getCacheKey();

	/**
	 * 返回方法的参数和参数值
	 * @return
	 */
	protected abstract String getWhereCondition();
	
	/**
	 * 返回要查询的VO字段
	 * @return
	 */
	protected abstract String[] getQryField();

	/**
	 * 返回要查询的VO类
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
	 * 构造 类是 field in ('values1','value2','...')字符串儿
	 * @param values
	 * @param withBrackets 是否带小括号
	 * @return
	 */
	public static String createInSql(Object[] values,boolean withBrackets){
		StringBuffer buf = new StringBuffer();
		//是否为字符串类型
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
