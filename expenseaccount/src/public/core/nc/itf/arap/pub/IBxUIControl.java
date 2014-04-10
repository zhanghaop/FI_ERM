package nc.itf.arap.pub;

import java.util.List;

import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * @author twei
 * 
 * nc.itf.arap.pub.IBxUIControl
 */
public interface IBxUIControl {
	
	/**
	 * @param head
	 * @return
	 * @throws BusinessException
	 * 
	 * ������������
	 * ����2���������ڣ���ǰ̨ȡ��½���ڴ���
	 * ����3�������ѯ��������˵ĵ��ݹ��ܣ�Ĭ�ϴ���գ��о����ѯ����ʱ���Դ���
	 * 
	 * ���أ��ܹ����г��������Ľ�
	 */
	public List<JKBXHeaderVO> getJKD(JKBXVO bxvo,UFDate cxrq,String queryStr) throws BusinessException ;
	
	/**
	 * ��������ͷ����
	 */
	public BXBusItemVO[] queryByPk(String pk_jk,String pk_Bx) throws BusinessException ;
	
}
