package nc.itf.cmp.pj;

import java.util.List;

import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.pub.BusinessException;
/**
 * ����Ʊ�����á���������ӿ�
 * @author zhaozh on 2009-1-7
 *
 */
public interface INoteOuter {
	/**
	 * @param adoptList ��Ҫ���õĽ�����Ϣ����vo
	 * @throws BusinessException
	 */
	public void autoAdopt(List<SettlementBodyVO> adoptList) throws BusinessException;
	/**
	 * 
	 * @param bxList ��Ҫ�����Ľ�����Ϣ����vo
	 * @throws BusinessException
	 */
	public void autoBx(List<SettlementBodyVO> bxList) throws BusinessException;
	/**
	 * ����ȡ����������������飬ֱ��ȡ��Ʊ�ݵı���״̬
	 * @param bxList ����Ҫȡ�������ı���vo(��Ʊ�����͡�Ʊ�ݺ�)
	 * @throws BusinessException
	 */
	public void cancleBX(List<SettlementBodyVO> bxList) throws BusinessException;
}
