package nc.bs.erm.service;

/**
 * 服务的代理服务。 创建日期：(2004-9-6 15:14:43)
 *
 * @author：Zhu Qi
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.server.util.NewObjectService;
import nc.itf.erm.service.IErmServiceProxy;
import nc.vo.erm.service.ServiceVO;
import nc.vo.fipub.service.ExceptionUtil;
import nc.vo.fipub.service.ObjectUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;

/**
 *
 * nc.bs.erm.service.ErmServiceProxyImpl
 */
public class ErmServiceProxyImpl implements IErmServiceProxy {
	public ErmServiceProxyImpl() {

	}

	/**
	 * 服务代理调用服务的入口方法。 创建日期：(2004-9-6 15:18:43)
	 *
	 * @return java.lang.Object[]--返回每个服务的结果。
	 * @param scds
	 *            nc.servlet.call.ServletCallDiscription[] ---需要调用的服务序列。
	 * @exception java.rmi.RemoteException
	 *                异常说明。
	 */
	public Object[] call(ServiceVO[] scds) throws BusinessException {
		if (scds == null || scds.length == 0)
			return null;

		Object[] ret = new Object[scds.length];
		try {
			for (int i = 0, j = scds.length; i < j; i++) {
				ret[i] = invokeobject(scds[i]);
			}

			ObjectUtils.objectReference(ret);

		} catch (BusinessException ite) {
			throw ite;
		} catch (Exception ie) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(ie);;
			throw new BusinessRuntimeException(ie.getMessage(),ie);
		}
		return ret;

	}

	private Object invokeobject(ServiceVO scd) throws BusinessException {
		try {
			String classname = scd.getClassname();
			String methodname = scd.getMethodname();
			Object bo = NCLocator.getInstance().lookup(classname);
			Method[] methods = bo.getClass().getDeclaredMethods();
			Method method = null;
			for (int i = 0; i < methods.length; i++) {
				Method m = methods[i];
				if ((m.getName().equals(methodname))&& (m.getParameterTypes().length == scd.getParamtype().length)) {
					method = m;
					break;
				}
			}
			if (method == null) {
				throw new BusinessException(new NoSuchMethodException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0086")/*@res "程序错误：方法没有定义,请检查:"*/+ classname + "'s " + methodname));
			}

			return method.invoke(bo, scd.getParam());
		} catch (BusinessException ite) {
			throw ite;
		}catch(InvocationTargetException te){
			Throwable ee = te.getTargetException();
			BusinessException bbee = ExceptionUtil.getRealBusiException(ee);
			if(bbee!=null){
				nc.bs.logging.Log.getInstance(this.getClass()).error(bbee);;
				throw bbee;
			}else{
				nc.bs.logging.Log.getInstance(this.getClass()).error(ee);;
				throw new BusinessException(ee.getMessage(),ee);
			}

		}catch (Exception ie) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(ie);;
			throw new BusinessRuntimeException(ie.getMessage(),ie);
		}
	}

		private Object invokeInstance(String modulename,ServiceVO scd) throws BusinessException {

			try {
				String classname = scd.getClassname();
				String methodname = scd.getMethodname();
				 Object bo = NewObjectService.newInstance(modulename,classname);

				Method[] methods = bo.getClass().getDeclaredMethods();
				Method method = null;
				for (int i = 0; i < methods.length; i++) {
					Method m = methods[i];
					if ((m.getName().equals(methodname))&& (m.getParameterTypes().length == scd.getParamtype().length)) {
						method = m;
						break;
					}
				}
				if (method == null) {
					throw new BusinessException(new NoSuchMethodException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0086")/*@res "程序错误：方法没有定义,请检查:"*/+ classname + "'s " + methodname));
				}

				return method.invoke(bo, scd.getParam());
			} catch (BusinessException ite) {
				throw ite;
			}catch(InvocationTargetException te){
				Throwable ee = te.getTargetException();
				BusinessException bbee = ExceptionUtil.getRealBusiException(ee);
				if(bbee!=null){
					nc.bs.logging.Log.getInstance(this.getClass()).error(ee);;
					throw bbee;
				}else{
					nc.bs.logging.Log.getInstance(this.getClass()).error(ee);;
					throw new BusinessException(ee.getMessage(),ee);
				}

			} catch (Exception ie) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(ie);;
				throw new BusinessRuntimeException(ie.getMessage(),ie);
			}

		}
		public  java.lang.Object[] callService(String modulename,ServiceVO[] scds) throws BusinessException {

			if (scds == null || scds.length == 0)
				return null;

			Object[] ret = new Object[scds.length];
			try {
				for (int i = 0, j = scds.length; i < j; i++) {
					ret[i] = invokeInstance(modulename,scds[i]);
				}
				ObjectUtils.objectReference(ret);
			} catch (BusinessException ite) {
				throw ite;
			} catch (Exception ie) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(ie);;
				throw new BusinessRuntimeException(ie.getMessage(),ie);
			}
			return ret;

		}


}