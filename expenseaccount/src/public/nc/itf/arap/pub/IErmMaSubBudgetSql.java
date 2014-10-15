package nc.itf.arap.pub;

import nc.vo.pub.BusinessException;
import nc.vo.tb.obj.NtbParamVO;

/**
 * ���뵥���ε���sql��ѯ
 * 
 * @author chenshuaia
 * 
 */
public interface IErmMaSubBudgetSql {
	/**
	 * ��ȡ���ε�����Чҵ����pks<br>
	 * Ҫʵ�ֹ����ǣ� ��detailPks�и���ntbParam ���˳�����������ҵ����pk���ϣ�
	 * ���磺<br>
	 * detailPks �а��� 3�����ݣ������Ǳ���̬��һ������Ч̬��
	 * ntbParam ���Ĳ���δȡִ������
	 * �򷵻ظ�һ����Ч̬��pk����
	 * @param detailPks
	 *            ���е���Чҵ����pk������״̬�ĵ���ҵ����pk��
	 * @param ntbParam
	 *            Ԥ�����
	 * @return
	 * @throws BusinessException
	 */
	public String[] getMaBudgetSubBillEffectDetailPks(String[] detailPks, final NtbParamVO ntbParam)
			throws BusinessException;
}
