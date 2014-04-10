package nc.vo.erm.matterappctrl;

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
	 * ҵ�񵥾ݽ��
	 */
	private UFDouble amount = UFDouble.ZERO_DBL;
	
	public MtappCtrlBusiVO(IMtappCtrlBusiVO busivo) {
		this.busivo = busivo;
		// ���õ�ǰִ������
		this.amount = busivo.getAmount();
		if(DataType_pre.equals(getDataType())){
			pre_data = amount;
		}else{
			exe_data = amount;
		}
	}
	
	@Override
	public String getAttributeValue(String attr) {
		return busivo.getAttributeValue(attr);
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
		return busivo.getpk_djdl();
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
		return busivo.getAmount();
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
	
	@Override
	public boolean isExceedEnable() {
		return busivo.isExceedEnable();
	}
}
