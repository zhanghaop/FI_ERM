package nc.itf.erm.fieldcontrast;

import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.pub.BusinessException;

/**
 * �ֶζ���ʵ��Ĳ�ѯ�ӿ�
 * 
 * @author lvhj
 *
 */
public interface IFieldContrastQryService {
	
	/**
	 * ��ѯ�ֶζ�����Ϣ
	 * 
	 * @param pk_org
	 * @param app_scene
	 * @param src_billtype
	 * @return
	 * @throws BusinessException
	 */
	public FieldcontrastVO[] qryVOs(String pk_org,int app_scene,String src_billtype) throws BusinessException;
	
	
	/**
	 * ��ѯ����֯Ԥ������
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public FieldcontrastVO[] qryPredataVOs() throws BusinessException;
	

}
