package nc.vo.erm.matterappctrl;

import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * װ������װ�����뵥������Ϣ�����ӿ��ƻ�д����ʵ������Ҫʹ�õ��������ҵ����Ϣ���԰�װ
 * 
 * @author lvhj
 *
 */
public class MtappCtrlBusiVO implements IMtappCtrlBusiVO {

	/**
	 * �Ƿ�����
	 */
	private boolean isExceed = false;
	/**
	 * �����Ƿ���Գ�����
	 */
	private boolean isExceedEnable = false;
	/**
	 * ҵ��ϵͳ��װ�Ļ�д����
	 */
	private IMtappCtrlBusiVO busivo;
		
	/**
	 * ҵ�񵥾�ռԤռ��
	 */
	private UFDouble pre_data = UFDouble.ZERO_DBL;

	/**
	 * ҵ�񵥾�ռԤռ��
	 */
	private UFDouble exe_data = UFDouble.ZERO_DBL;
	
	/**
	 * ���Կ���ά��
	 */
	private String unAdjustKey;
	/**
	 * ���п���ά��
	 */
	private String allFieldKey;
//	
//	/**
//	 * ҵ�񵥾ݽ��
//	 */
//	private UFDouble amount = UFDouble.ZERO_DBL;
	
	public MtappCtrlBusiVO(IMtappCtrlBusiVO busivo) {
		this.busivo = busivo;
		// ���õ�ǰִ������
//		this.amount = busivo.getAmount();
		if(DataType_pre.equals(getDataType())){
			pre_data = getAmount();
		}else{
			exe_data = getAmount();
		}
		this.isExceedEnable = busivo.isExceedEnable();
	}
	
	@Override
	public String getAttributeValue(String... attrs) {
		return busivo.getAttributeValue(attrs);
	}

	@Override
	public String getBusiPK() {
		return busivo.getBusiPK();
	}

	@Override
	public String getDetailBusiPK() {
		return busivo.getDetailBusiPK();
	}

	@Override
	public String getBusiSys() {
		return busivo.getBusiSys();
	}

	@Override
	public String getpk_djdl() {
		String pk_djdl = busivo.getpk_djdl();
		if(StringUtil.isEmptyWithTrim(pk_djdl)){
			return getBillType();
		}
		return pk_djdl;
	}

	@Override
	public String getBillType() {
		return busivo.getBillType();
	}

	@Override
	public String getTradeType() {
		return busivo.getTradeType();
	}

	@Override
	public String getCurrency() {
		return busivo.getCurrency();
	}

	@Override
	public UFDouble[] getCurrInfo() {
		return busivo.getCurrInfo();
	}

	@Override
	public UFDate getBillDate() {
		return busivo.getBillDate();
	}

	@Override
	public String getPk_org() {
		return busivo.getPk_org();
	}

	@Override
	public String getPk_group() {
		return busivo.getPk_group();
	}

	@Override
	public String getMatterAppPK() {
		return busivo.getMatterAppPK();
	}

	@Override
	public String getForwardBusidetailPK() {
		return busivo.getForwardBusidetailPK();
	}

	@Override
	public String getSrcBusidetailPK() {
		return busivo.getSrcBusidetailPK();
	}
	
	@Override
	public int getDirection() {
		return busivo.getDirection();
	}

	@Override
	public String getDataType() {
		return busivo.getDataType();
	}

	@Override
	public UFDouble getAmount() {
		UFDouble busi_amount = busivo.getAmount();
		if(Direction_negative == getDirection()){
			busi_amount = busi_amount.multiply(new UFDouble(-1));
		}
		return busi_amount;
	}
	

	public UFDouble getExeData() {
		return exe_data;
	}

	public UFDouble getPreData() {
		return pre_data;
	}

	public void setPre_data(UFDouble pre_data) {
		this.pre_data = pre_data;
	}

	public void setExe_data(UFDouble exe_data) {
		this.exe_data = exe_data;
	}
	
	public boolean isExceedEnable() {
		return this.isExceedEnable;
	}

	public String getMatterAppDetailPK() {
		return busivo.getMatterAppDetailPK();
	}

	public boolean isExceed() {
		return isExceed;
	}

	public void setExceed(boolean isExceed) {
		this.isExceed = isExceed;
	}

	public String getUnAdjustKey() {
		return unAdjustKey;
	}

	public void setUnAdjustKey(String unAdjustKey) {
		this.unAdjustKey = unAdjustKey;
	}

	public String getAllFieldKey() {
		return allFieldKey;
	}

	public void setAllFieldKey(String allFieldKey) {
		this.allFieldKey = allFieldKey;
	}
	
	public void setExceedEnable(boolean isExceedEnable){
		this.isExceedEnable = isExceedEnable;
	}
	
}
