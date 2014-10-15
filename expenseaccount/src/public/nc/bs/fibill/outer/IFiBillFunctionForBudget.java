/**
 * 
 */
package nc.bs.fibill.outer;

import java.util.ArrayList;

import nc.vo.cmp.func.QueryVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * ���񵥾��漰Ԥ�����ع��ܽӿڣ������ڲ�ʹ��
 * @author jianghao
 * @version V5.5
 * @since V5.5
 * 2008-9-2
 */
public interface IFiBillFunctionForBudget {
	/** ԭ�ҽ�� �������� */
	public final String YBJE_ALIAS = "ybje";
	/** ���ҽ�� �������� */
	public final String FBJE_ALIAS = "fbje";
	/** ���ҽ�� �������� */
	public final String BBJE_ALIAS = "bbje";

	/**
	 * ����������ϵͳ�жϹ�˾��Ԥ����ʽ�ƻ����ƻ��ڡ����ά�����ڲ����Ƿ����޸�
	 * @param pk_corp ��˾pk
	 * @return ����Ϊ2��boolean���飬�ֱ����Ԥ����ʽ�ƻ����ƻ��ڡ����ά�����ڲ����Ƿ����޸ġ����޸ģ�true
	 * @throws BusinessException
	 */
	public boolean[] canUpdateBudgetAndBalMaintParam(String pk_corp) throws BusinessException;


	/**
	 * ִ�������ѯ���������ط��ϣ�Ԥ�㣩�����ĵ�����Ϣ��
	 * 
	 * ʵ���߼��ο���
	 * 
	 * @param qvos ����vo���飺QueryVO����m_SourceArr��������{@link nc.vo.ntb.outer.NtbParamVO}����
	 * @param selectFlds ��ѯ�ֶ����ƣ�ArrayList��ÿ��String�����Ӧ����qvosÿ��Ԫ��Ҫ��ѯ���ֶ�
	 * @param amountFldAlias ����������ƣ���ֵ��Դ�ڽӿڳ���YBJE_ALIAS��FBJE_ALIAS��BBJE_ALIAS
	 * @return ��ѯֵ��ArrayListÿ��Ԫ�طֱ��Ӧ����qvosÿ��Ԫ�صĲ�ѯ�����
	 * ����selectFlds�е��ֶ����ƺ�amountFldAlias�еĽ����������Ϊattributename������vo��ֵ
	 * @throws BusinessException
	 * 
	 */
	public ArrayList<CircularlyAccessibleValueObject[]> queryBudgetExecBillInfo(QueryVO[] qvos, ArrayList<String[]> selectFlds, String[] amountFldAlias) throws BusinessException;

}
