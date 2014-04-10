package nc.vo.er.pub;

import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;


/**
 * @author twei
 *
 * nc.vo.itf.IitemControl
 * 
 * VO ��Ҫʵ�ֵĽӿ�, ���ڷ��ÿ��� (��������,Ԥ��,������).
 */
public interface IFYControl {
	
	
	public boolean isYSControlAble();
	public boolean isJKControlAble();
	public boolean isSSControlAble();
	
	/**
	 * @return ��������������
	 */
	public String getPk_item();
	
	/**
	 * @return ��������Vo������ֵ
	 */
	public Object getItemValue(String key);
	
	/**
	 * @return ���ƽ��[glbbb,groupbb,orgbb,yb], ������
	 */
	public UFDouble[] getItemJe();
	
	/**
	 * @return �������������
	 */
	public String getBzbm();
	
	/**
	 * @return ������������� UFdouble[glbbbhl,groupbbhl,bb_hl]
	 */
	public UFDouble[] getItemHl();
	
	
	
	/**
	 * 
	 * 	if(head.getDjzt().intValue()==DJZBVOConsts.m_intDJStatus_Signature){
			items[k].setCloser(head.getYhqrr());
			items[k].setClosedate(head.getYhqrrq());
		}else if(head.getDjzt().intValue()==DJZBVOConsts.m_intDJStatus_Verified){
			items[k].setCloser(head.getShr());
			items[k].setClosedate(head.getShrq());
		}else if(head.getDjzt().intValue()==DJZBVOConsts.m_intDJStatus_Saved){
			items[k].setCloser(head.getLrr());
			items[k].setClosedate(head.getDjrq());
		}		
	 * @return
	 */
	public String getOperationUser();
	public UFDate getOperationDate();

	/**
	 * @return ���óе���˾pk
	 */
	public String getFydwbm();
	/**
	 * ����¼�빫˾ 	 
	 */
	public String getPk_group();
	
	/**
	 * �������� 	 
	 */
	public String getDwbm();
	
	/**
	 * @return �������� 
	 */
	public UFDate getDjrq();
	
	/**
	 * @return �������ͱ���
	 */
	public String getDjlxbm();
	
	/**
	 * @return ����
	 */
	public String getPk();

	/**
	 * @return ���ݴ���
	 */
	public String getDjdl();

	/**
	 * @return �ϲ���Դ
	 */
	public String getDdlx();

	/**
	 * @return ����
	 */
	public Integer getFx();

	/**
	 * @return �Ƿ񱣴����
	 */
	public boolean isSaveControl();
	
	/**
	 * @return ���㷽ʽ
	 */
	public String getJsfs();

	/**
	 * 
	 * ���ض�Ӧ�ĵ���״̬
	 */
	public Integer getDjzt();

	/**
	 * 
	 * ���ض�Ӧ�ı�����
	 */
	public String getJkbxr() ;
	/**
	 * 
	 * ���ض�Ӧ��¼����
	 */
	public String getOperator() ;
	public String getPk_org();
	
}
