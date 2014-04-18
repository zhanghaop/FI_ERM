package nc.bs.erm.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.server.util.NewObjectService;
import nc.itf.erm.service.IErmEJBService;
import nc.vo.erm.service.ServiceVO;
import nc.vo.fipub.service.ExceptionUtil;
import nc.vo.fipub.service.ObjectUtils;
import nc.vo.pub.BusinessException;

/**
 *
 * nc.bs.erm.service.ErmEJBServiceImpl
 */
public class ErmEJBServiceImpl implements IErmEJBService {
	public ErmEJBServiceImpl() {

	}

	private Object invokeInstance(String modulename, ServiceVO scd) throws BusinessException {

		try {
			String classname = scd.getClassname();
			String methodname = scd.getMethodname();
			Object bo = null;

			try{
				bo = NCLocator.getInstance().lookup(classname);
			}catch (Exception e) {
			}
			if(bo==null){
				try{
					bo=NewObjectService.newInstance(modulename, classname);
				}catch (Exception e) {
				}
			}
			if(bo==null && classname.equals("nc.bs.uap.lock.PKLock")){
				bo=nc.bs.uap.lock.PKLock.getInstance();
			}
			if(bo==null){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0085")/*@res "调用异常，查找接口失败："*/+classname);
			}

			Method method = null;

			method = getMethods(scd, methodname, bo.getClass().getDeclaredMethods(), method);

			if (method == null) {
				method = getMethods(scd, methodname, bo.getClass().getMethods(), null);
			}
			if (method == null) {
				throw new BusinessException(new NoSuchMethodException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2004", "UPP2004-000262")/*
																																						 * @res
																																						 * "程序错误：方法没有定义,请检查。（备注：一个远程外观类中的方法如果同名，其参数个数不能相同"
																																						 */
						+ classname + "'s " + methodname));
			}

			return method.invoke(bo, scd.getParam());
		} catch (BusinessException ite) {
			throw ite;
		} catch (InvocationTargetException te) {
			Throwable ee = te.getTargetException();
			BusinessException bbee = ExceptionUtil.getRealBusiException(ee);
			if (bbee != null) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(bbee);;
				throw bbee;
			} else {
				nc.bs.logging.Log.getInstance(this.getClass()).error(ee);;
				throw new BusinessException(ee.getMessage(), ee);
			}

		} catch (Exception ie) {
			BusinessException bbee = ExceptionUtil.getRealBusiException(ie);
			if (bbee != null) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(bbee);;
				throw bbee;
			} else {
				nc.bs.logging.Log.getInstance(this.getClass()).error(ie);;
				throw new BusinessException(ie.getMessage(), ie);
			}
		}

	}

	private Method getMethods(ServiceVO scd, String strMethodName, Method[] methods, Method method) {
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];
			if ((m.getName().equals(strMethodName)) && (m.getParameterTypes().length == scd.getParamtype().length)) {

				Class[] temp1 = m.getParameterTypes();
				Class[] temp2 = scd.getParamtype();

				boolean ifsame = true;

				for (int j = 0; j < temp1.length; j++) {

					if (temp1[j].getName().equalsIgnoreCase("int")) {
						temp1[j] = Integer.class;
					} else if (temp1[j].getName().equalsIgnoreCase("float")) {
						temp1[j] = Float.class;
					} else if (temp1[j].getName().equalsIgnoreCase("double")) {
						temp1[j] = Double.class;
					} else if (temp1[j].getName().equalsIgnoreCase("char")) {
						temp1[j] = Character.class;
					} else if (temp1[j].getName().equalsIgnoreCase("boolean")) {
						temp1[j] = Boolean.class;
					} else if (temp1[j].getName().equalsIgnoreCase("short")) {
						temp1[j] = Short.class;
					} else if (temp1[j].getName().equalsIgnoreCase("byte")) {
						temp1[j] = Byte.class;
					} else if (temp1[j].getName().equalsIgnoreCase("long")) {
						temp1[j] = Long.class;
					}

					if (!temp1[j].equals(temp2[j])) {
						ifsame = false;
						break;
					}
				}

				if (ifsame) {
					method = m;
					break;
				}
			}
		}
		return method;
	}

	public java.lang.Object[] callEJBService(String modulename, ServiceVO[] scds) throws BusinessException {

		if (scds == null || scds.length == 0)
			return null;

		Object[] ret = new Object[scds.length];
		try {
			for (int i = 0, j = scds.length; i < j; i++) {
				ret[i] = invokeInstance(modulename, scds[i]);
			}

			ObjectUtils.objectReference(ret);

		} catch (BusinessException ite) {
			throw ite;
		} catch (Exception ie) {
			throw new BusinessException(ie.getMessage(), ie);
		}
		return ret;

	}

	public Object[] callEJBService_RequiresNew(String modulename, ServiceVO[] arg0) throws BusinessException {
		return callEJBService(modulename, arg0);
	}

	public Map<String,Object> callBatchEJBService(ServiceVO[] scds) throws BusinessException {
		if (scds == null || scds.length == 0)
			return null;

		Map<String,Object> ret = new HashMap<String,Object>();

		try {
			for (int i = 0, j = scds.length; i < j; i++) {
				ret.put(scds[i].getCode(), invokeInstance(scds[i].getModuleName(), scds[i]));
			}
			ObjectUtils.objectReference(ret);
		} catch (BusinessException ite) {
			throw ite;
		} catch (Exception ie) {
			throw new BusinessException(ie.getMessage(), ie);
		}
		return ret;
	}

}