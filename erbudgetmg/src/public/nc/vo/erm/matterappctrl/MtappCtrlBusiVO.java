package nc.vo.erm.matterappctrl;

import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * 装饰外界包装的申请单控制信息，增加控制回写服务实现中需要使用的其他相关业务信息属性包装
 * 
 * @author lvhj
 *
 */
public class MtappCtrlBusiVO implements IMtappCtrlBusiVO {

	/**
	 * 是否超申请
	 */
	private boolean isExceed = false;
	/**
	 * 配置是否可以超申请
	 */
	private boolean isExceedEnable = false;
	/**
	 * 业务系统包装的回写数据
	 */
	private IMtappCtrlBusiVO busivo;
		
	/**
	 * 业务单据占预占数
	 */
	private UFDouble pre_data = UFDouble.ZERO_DBL;

	/**
	 * 业务单据占预占数
	 */
	private UFDouble exe_data = UFDouble.ZERO_DBL;
	
	/**
	 * 刚性控制维度
	 */
	private String unAdjustKey;
	/**
	 * 所有控制维度
	 */
	private String allFieldKey;
//	
//	/**
//	 * 业务单据金额
//	 */
//	private UFDouble amount = UFDouble.ZERO_DBL;
	
	public MtappCtrlBusiVO(IMtappCtrlBusiVO busivo) {
		this.busivo = busivo;
		// 设置当前执行数据
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
