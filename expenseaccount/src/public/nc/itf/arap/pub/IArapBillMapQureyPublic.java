package nc.itf.arap.pub;


import java.util.List;
import java.util.Map;
import nc.vo.ep.bx.BillTypeMapVO;
import nc.vo.pub.BusinessException;

/**
 * �������Ͷ��չ�ϵ��ѯ�ӿ�<p>
 * 
 * 1��ԭ������������queryBillMap(String sourcebilltype)��queryBillMaps�����޸ĺ�ɴ�0-*����˾���룬
   ����������ԭ�õ��ÿ��Բ��޸ģ�������0����˾������Ϊ���ŵĶ��չ�ϵ��
   queryBillMap(String sourcebilltype, String targetparenttype)�޸ĺ󣬱��봫����ֻ�ܴ���һ����˾
   ���룬����nullʱ����Ϊ���ŵĶ��չ�ϵ��ԭ�õ��ñ��봫�빫˾�����null��<p>
   2���¼�����������������������������ԭ�����������ƣ�ֻ����ԭ�з������¼ӹ�˾������������ذ���˾key��Ӧ��
   Map�����ڲ�๫˾��Ӧ�Ķ��չ�ϵ��������˾ȡ��Ӧԭ���չ�ϵ <p>
   3����������ʹ�ü�IArapBillMapQureyPublic�ӿ�ע�͡�
 
 * @author tanfh
 *
 */
public interface IArapBillMapQureyPublic {
	
	/**
	 * ������Դ���ݱ���õ���ض��չ�ϵ�б�,
	 * ���Ϊnull,��������
	 * @param sourcebilltype ��Դ���ݱ���
	 * @param pk_corps ��˾���룬Ϊ��ʱ�����ؼ��ŵ�����
	 * @return
	 */
	public List<BillTypeMapVO> queryBillMap(String sourcebilltype, String[] pk_corps) throws BusinessException;
	
	/**
	 * ���ݶ����Դ���ݱ���õ���ض��չ�ϵ�б�
	 * ���Ϊnull,��������
	 * @param sourcebilltype ��Դ���ݱ��� 
	 * @param pk_corps ��˾���룬Ϊ��ʱ�����ؼ��ŵ�����
	 * @return
	 */
	public Map<String,List<BillTypeMapVO>> queryBillMaps(String[] sourcebilltypes, String[] pk_corps) throws BusinessException;
	
	/**
	 * ������Դ���ݱ����Ŀ�굥������Ψһȷ��һ�����չ�ϵ
	 * @param sourcebilltype ��Դ���ݱ���
	 * @param targetparenttype Ŀ�굥������
	 * @param pk_corps ��˾���룬Ϊ��ʱ�����ؼ��ŵ�����; Ҫ���ض����˾�� ��queryCorpsBillMap
	 * @see IArapBillMapQureyPublic#queryCorpsBillMap
	 * @return
	 */
	public BillTypeMapVO queryBillMap(String sourcebilltype, String targetparenttype, String pk_corp) throws BusinessException;
	
	/**
	 * ������Դ���ݱ���õ���ض��չ�ϵ�б�,
	 * ���Ϊnull,��������
	 * @param sourcebilltype ��Դ���ݱ���
	 * @param pk_corps ��˾���룬�������м�¼
	 * @return ����˾����ĸ�����Դ���ݱ���õ���ض��չ�ϵ�б�
	 */
	public Map<String, List<BillTypeMapVO>> queryCorpsBillMap(String sourcebilltype, String[] pk_corps) throws BusinessException;
	
	/**
	 * ���ݶ����Դ���ݱ���õ���ض��չ�ϵ�б�
	 * ���Ϊnull,��������
	 * @param sourcebilltype ��Դ���ݱ���
	 * @param pk_corps ��˾���룬Ϊ��ʱ���������м�¼
	 * @return ����˾����ĸ��ݶ����Դ���ݱ���õ���ض��չ�ϵ�б�
	 */
	public Map<String, Map<String,List<BillTypeMapVO>>> queryCorpsBillMaps(String[] sourcebilltypes, String[] pk_corps) throws BusinessException;
	
	/**
	 * ������Դ���ݱ����Ŀ�굥������Ψһȷ��һ�����չ�ϵ
	 * @param sourcebilltype ��Դ���ݱ���
	 * @param targetparenttype Ŀ�굥������
	 * @param pk_corps ��˾���룬Ϊ��ʱ���������м�¼
	 * @return ����˾����ĸ�����Դ���ݱ����Ŀ�굥������Ψһȷ��һ�����չ�ϵ
	 */
	public Map<String, BillTypeMapVO> queryCorpsBillMap(String sourcebilltype, String targetparenttype, String[] pk_corps) throws BusinessException;
	

}
