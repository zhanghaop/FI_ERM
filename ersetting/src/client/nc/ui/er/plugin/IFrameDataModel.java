package nc.ui.er.plugin;

import nc.ui.pub.beans.ValueChangedListener;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

/**����ģ�ͣ�����MainFrame�����ݹ���*/
public interface IFrameDataModel {
/**������ݱ仯������*/
public abstract void addDataChangeListener(ValueChangedListener vl);
/**�õ�ѡ�е�����*/
public abstract AggregatedValueObject[] getSelectedDatas() throws BusinessException;
/**�õ���ǰ����*/
public abstract AggregatedValueObject getCurrData()throws BusinessException;
/**��ʼ������*/
public abstract void initData()throws BusinessException;
/**������������ݵ�������,����������Ѿ�����������������ݣ�����׷��
 * �˷����ᴥ�����ݸı��¼�
 * */
public abstract void setData(AggregatedValueObject vo)throws BusinessException;
/**������棬���ҽ����ݸ��µ�������
 * �˷����ᴥ�����ݸı��¼�
 * */
public abstract void setDatas(AggregatedValueObject[] vo)throws BusinessException;

/**ȡ��������������
 * �˷����ᴥ�����ݸı��¼�
 * */
public abstract AggregatedValueObject[] getAllData()throws BusinessException;

/**ɾ�����ݱ仯������*/
public void removeDataChangeListener(ValueChangedListener vl)throws BusinessException;

public AggregatedValueObject getData(Object key)throws BusinessException;

/**�����ݴ����ݻ�����ɾ��
 * �˷����ᴥ�����ݸı��¼�
 * */
public abstract boolean removeData(AggregatedValueObject vo)throws BusinessException;
}
