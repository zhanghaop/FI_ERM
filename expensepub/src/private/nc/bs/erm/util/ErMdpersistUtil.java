package nc.bs.erm.util;

import nc.md.MDBaseQueryFacade;
import nc.md.data.access.NCObject;
import nc.md.model.IBean;
import nc.md.model.MetaDataException;
import nc.md.util.MDUtil;

/**
 *
 *
 *
 * @author lvhj
 *
 */
public class ErMdpersistUtil {

	public static NCObject getNCObject(Object billVo) throws MetaDataException {
		NCObject ncObj = NCObject.newInstance(billVo);
		if (ncObj == null)
			throw new MetaDataException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0019")/*@res "Ҫ�����SuperVOû������Ԫ���ݣ��޷����г־û�,SuperVo:"*/
					+ billVo.getClass().getName());
		if (!MDUtil.isEntityType(ncObj.getRelatedBean()))
			throw new MetaDataException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0020")/*@res "Ҫ�����SuperVO��Ӧ��Ԫ���ݲ���ʵ�����ͣ�����ģ�ͣ�beanName:"*/
					+ ncObj.getRelatedBean().getName());
		return ncObj;
	}

	public static NCObject[] getNCObject(Object[] billVos) throws MetaDataException {
		if (billVos == null || billVos.length == 0)
			return null;
		NCObject[] ncObjs = new NCObject[billVos.length];
		IBean bean = MDBaseQueryFacade.getInstance().getBeanByFullClassName(
				billVos[0].getClass().getName());
		if (!MDUtil.isEntityType(bean))
			throw new MetaDataException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0020")/*@res "Ҫ�����SuperVO��Ӧ��Ԫ���ݲ���ʵ�����ͣ�����ģ�ͣ�beanName:"*/ + bean.getFullName());
		for (int i = 0; i < billVos.length; i++) {
			ncObjs[i] = NCObject.newInstance(bean, billVos[i]);
		}
		return ncObjs;
	}

}