package nc.ui.erm.matterapp.listener;

import nc.itf.fi.pub.Currency;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

/**
 * ���ʾ��ȼ�����Ƭ����������
 * @author wangled
 *
 */
public class CardBodyRateDecimalListener implements IBillModelDecimalListener2{
	public static final String RATE_TYPE_LOCAL = "local";
	public static final String RATE_TYPE_GROUP = "group";
	public static final String RATE_TYPE_GLOBAL = "global";
	
	private String orgField;
	private String[] target;
	private String rateType;
	private BillModel model; 
	
	public CardBodyRateDecimalListener(BillModel model, String orgField,
			String[] target, String rateType) {
		this.model = model;
		this.orgField = orgField;
		this.target = target;
		this.rateType = rateType;
		this.model.addDecimalListener(this);
	}

	public String[] getTarget() {
		return this.target;
	}

	public int getDecimalFromSource(int row, Object value) {
		int hlPrecision = 8;
		String pk_org = (String)value;
		
		String pk_currtype = (String)model.getValueAt(row, MtAppDetailVO.PK_CURRTYPE + IBillItem.ID_SUFFIX);
		try {
			if (RATE_TYPE_LOCAL.equals(rateType)) {
				hlPrecision = Currency.getRateDigit(pk_org, pk_currtype, Currency.getOrgLocalCurrPK(pk_org));
			} else if (RATE_TYPE_GROUP.equals(rateType)) {
				hlPrecision = Currency.getGroupRateDigit(pk_org, ErUiUtil.getPK_group(), pk_currtype);
			} else if (RATE_TYPE_GLOBAL.equals(rateType)) {
				// ȫ�ֻ��ʾ���
				hlPrecision = Currency.getGlobalRateDigit(pk_org, pk_currtype);
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		
		return hlPrecision;
	
	}

	public String getSource() {
		return orgField;
	}

	public boolean isTarget(BillItem item) {
		boolean result = false;
		for (int i = 0; i < target.length; i++) {
			if(target[i].equals(item.getKey())){
				result = true;
				break;
			}
		}
		return result;
	}
}
