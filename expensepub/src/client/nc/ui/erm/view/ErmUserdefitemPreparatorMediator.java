package nc.ui.erm.view;

import java.util.List;

import nc.ui.pub.bill.IBillData;
import nc.ui.pub.bill.IBillListData;
import nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare;
import nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare;
import nc.ui.uif2.userdefitem.QueryParam;
import nc.ui.uif2.userdefitem.UserDefItemContainer;

/**
 * ǰ̨�û��Զ�������������չ
 * 
 * @author lvhj
 *
 */
@SuppressWarnings("restriction")
public class ErmUserdefitemPreparatorMediator {
	
	/**
	 * ����չ���û��Զ���������
	 */
	private UserDefItemContainer userDefitemContainer;
	/**
	 * ����չ���û��Զ����������еĹ���
	 */
	private List<QueryParam> params;
	
	/**
	 * ����չ���б��Զ��������
	 */
	private CompositeBillListDataPrepare listPreparator;
	/**
	 * ����չ�Ŀ�Ƭ�Զ��������
	 */
	private CompositeBillDataPrepare cardPreparator;
	/**
	 * ����չ���б��Զ���������е�ҵ������
	 */
	private List<IBillListData> extendlistPreparators;
	
	/**
	 * ����չ����Ƭ�Զ���������е�ҵ������
	 */
	private List<IBillData> extendcardPreparators;
	
	
	/**
	 * ��ʼ������
	 */
	public void preparedata(){
		if(this.userDefitemContainer != null && this.params != null){
			// ��չ�����еĹ���
			List<QueryParam> containerParams = this.userDefitemContainer.getParams();
			if(containerParams == null){
				this.userDefitemContainer.setParams(this.params);
			}else{
				containerParams.addAll(this.params);
			}
		}
		
		if(this.listPreparator != null && this.extendlistPreparators != null){
			// ��չ�б��Զ��������
			List<IBillListData> billListDataPrepares = this.listPreparator.getBillListDataPrepares();
			if(billListDataPrepares == null){
				this.listPreparator.setBillListDataPrepares(this.extendlistPreparators);
			}else{
				billListDataPrepares.addAll(this.extendlistPreparators);
			}
		}

		if(this.cardPreparator != null && this.extendcardPreparators != null){
			// ��չ��Ƭ�Զ��������
			List<IBillData> billDataPrepares = this.cardPreparator.getBillDataPrepares();
			if(billDataPrepares == null){
				this.cardPreparator.setBillDataPrepares(this.extendcardPreparators);
			}else{
				billDataPrepares.addAll(this.extendcardPreparators);
			}
		}
		
	}

	public UserDefItemContainer getUserDefitemContainer() {
		return userDefitemContainer;
	}

	public void setUserDefitemContainer(UserDefItemContainer userDefitemContainer) {
		this.userDefitemContainer = userDefitemContainer;
	}

	public List<QueryParam> getParams() {
		return params;
	}

	public void setParams(List<QueryParam> params) {
		this.params = params;
	}

	public CompositeBillListDataPrepare getListPreparator() {
		return listPreparator;
	}

	public void setListPreparator(CompositeBillListDataPrepare listPreparator) {
		this.listPreparator = listPreparator;
	}

	public CompositeBillDataPrepare getCardPreparator() {
		return cardPreparator;
	}

	public void setCardPreparator(CompositeBillDataPrepare cardPreparator) {
		this.cardPreparator = cardPreparator;
	}

	public List<IBillData> getExtendcardPreparators() {
		return extendcardPreparators;
	}

	public void setExtendcardPreparators(List<IBillData> extendcardPreparators) {
		this.extendcardPreparators = extendcardPreparators;
	}

	public List<IBillListData> getExtendlistPreparators() {
		return extendlistPreparators;
	}

	public void setExtendlistPreparators(List<IBillListData> extendlistPreparators) {
		this.extendlistPreparators = extendlistPreparators;
	}


}
