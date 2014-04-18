package nc.itf.erm.mactrlschema;

import java.util.List;
import java.util.Map;

import nc.vo.erm.mactrlschema.MtappCtrlfieldVO;
import nc.vo.pub.BusinessException;

/**
 * ������������ά�Ȳ�������
 * @author chenshuaia
 *
 */
public interface IErmMappCtrlFieldQuery {
	/**
	 * ������֯pk�뽻�����Ͳ�ѯ�������������п����ֶ�
	 * @param pk_org ��֯pk
	 * @param trade_type �������ͣ�����2641�ȣ�
	 * @return
	 * @throws BusinessException
	 */
	public MtappCtrlfieldVO[] queryCtrlFieldVos(String pk_org, String trade_type) throws BusinessException;
	
	/**
	 * ������֯pk�뽻�����Ͳ�ѯ�����������п����ֶ�
	 * 
	 * @param paramList  List< {pk_org ��֯pk,trade_type �������ͣ�����2641�ȣ�}>
	 * @return  map<pk_org+pk_tradeType,List<MtappCtrlfieldVO>>
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	public Map<String,List<MtappCtrlfieldVO>> queryCtrlFieldVos(List<String[]> paramList) throws BusinessException;
	
	/**
	 * 
	 * @param pk_org ������֯
	 * @param trade_type ����������ؽ������Ͳ���
	 * @return Map<String,String[]> key = ��������,value ���Ƶ��ֶ�
	 * @throws BusinessException
	 */
	public Map<String,List<String>> queryCtrlFields(String pk_org,String[] trade_type) throws BusinessException;
	
	/**
	 * ����pk_org���������ͱ������飬��ѯȫ������ά��vo
	 * 
	 * @param pk_org ������֯
	 * @param trade_type ����������ؽ������Ͳ���
	 * @return Map<String,MtappCtrlfieldVO[]> key = ��������,value ���Ƶ�VO
	 * @throws BusinessException
	 */
	public Map<String,List<MtappCtrlfieldVO>> queryFieldVOs(String pk_org,String[] trade_type) throws BusinessException;
}
