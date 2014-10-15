package nc.itf.erm.mactrlschema;

import java.util.List;
import java.util.Map;

import nc.vo.erm.mactrlschema.MtappCtrlbillVO;
import nc.vo.pub.BusinessException;

/**
 * �����������Ƶ��ݶ����������
 * @author chenshuaia
 *
 */
public interface IErmMappCtrlBillQuery {
	/**
	 * ������֯pk�뽻�����Ͳ�ѯ�������������п��ƶ���Ĳ�ѯ
	 * @param pk_org ��֯pk
	 * @param trade_type �������ͣ�����2641�ȣ�
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlbillVO[] queryCtrlBillVos(String pk_org, String trade_type) throws BusinessException;

	/**
	 * ������֯pk�뽻�����Ͳ�ѯ�������������п��ƶ���Ĳ�ѯ
	 * 
	 * @param paramList  List< {pk_org ��֯pk,trade_type �������ͣ�����2641�ȣ�}>
	 * @return  map<pk_org+pk_tradeType,List<���ƶ��������ͱ���>>
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	public Map<String,List<String>> queryCtrlBillVos(List<String[]> paramList) throws BusinessException;
	
	
	/**
	 * ������֯pk�뽻�����Ͳ�ѯ���������뵥�п��ƶ��󼰿���ά��
	 * 
	 * @param paramList ����Ϊ��
	 * @param pk_group ����Ϊ��
	 * @return map[0]�ǿ��ƶ���map[1]Ϊ����ά�ȣ�map��keyΪvo.getPk_org() + vo.getPk_tradetype()
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public Map[] queryCtrlShema(List<String[]> paramList,String pk_group) throws BusinessException;
}
