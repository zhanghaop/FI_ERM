package nc.ui.erm.view;

import java.util.List;

import nc.ui.pub.bill.IBillData;
import nc.ui.pub.bill.IBillListData;
import nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare;
import nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare;
import nc.ui.uif2.userdefitem.QueryParam;
import nc.ui.uif2.userdefitem.UserDefItemContainer;

/**
 * 前台用户自定义属性容器扩展
 * 
 * @author lvhj
 *
 */
@SuppressWarnings("restriction")
public class ErmUserdefitemPreparatorMediator {
	
	/**
	 * 待扩展的用户自定义项容器
	 */
	private UserDefItemContainer userDefitemContainer;
	/**
	 * 待扩展到用户自定义项容器中的规则
	 */
	private List<QueryParam> params;
	
	/**
	 * 待扩展的列表自定义项处理器
	 */
	private CompositeBillListDataPrepare listPreparator;
	/**
	 * 待扩展的卡片自定义项处理器
	 */
	private CompositeBillDataPrepare cardPreparator;
	/**
	 * 待扩展到列表自定义项处理器中的业务处理器
	 */
	private List<IBillListData> extendlistPreparators;
	
	/**
	 * 待扩展到卡片自定义项处理器中的业务处理器
	 */
	private List<IBillData> extendcardPreparators;
	
	
	/**
	 * 初始化方法
	 */
	public void preparedata(){
		if(this.userDefitemContainer != null && this.params != null){
			// 扩展容器中的规则
			List<QueryParam> containerParams = this.userDefitemContainer.getParams();
			if(containerParams == null){
				this.userDefitemContainer.setParams(this.params);
			}else{
				containerParams.addAll(this.params);
			}
		}
		
		if(this.listPreparator != null && this.extendlistPreparators != null){
			// 扩展列表自定义项处理器
			List<IBillListData> billListDataPrepares = this.listPreparator.getBillListDataPrepares();
			if(billListDataPrepares == null){
				this.listPreparator.setBillListDataPrepares(this.extendlistPreparators);
			}else{
				billListDataPrepares.addAll(this.extendlistPreparators);
			}
		}

		if(this.cardPreparator != null && this.extendcardPreparators != null){
			// 扩展卡片自定义项处理器
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
