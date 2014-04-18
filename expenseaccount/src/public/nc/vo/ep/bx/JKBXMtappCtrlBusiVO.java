package nc.vo.ep.bx;

import java.io.Serializable;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

public class JKBXMtappCtrlBusiVO implements IMtappCtrlBusiVO,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ����ErVOUtils.prepareBxvoItemToHeaderClone(vo)����ı�ͷVO
	 */
	private JKBXHeaderVO parentVO = null;
	
	/**
	 * ��д���
	 */
	private UFDouble amount = null;
	
	/**
	 * ����ԭ�ҽ�����ʹ��
	 */
	private UFDouble fyyb_data = null;

	/**
	 * ��д����Ĭ������
	 */
	private int direction = Direction_positive;
	
	/**
	 * ��д�������� ��Ĭ��ִ����
	 */
	private String datatype = DataType_exe;
	
	/**
	 * ���������mtAppTradeType������getMtAppTradeType()����ѯ���ݿ�
	 *
	 * @param parentVO
	 * @author: wangyhh@ufida.com.cn
	 */
	public JKBXMtappCtrlBusiVO(JKBXHeaderVO parentVO) {
		super();

		if (parentVO == null) {
			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0104")/*@res "��������Ϊnull"*/);
		}

		this.parentVO = parentVO;
	}

	public JKBXMtappCtrlBusiVO(JKBXHeaderVO parentVO,String mtAppTradeType) {
		super();

		if (parentVO == null || mtAppTradeType == null) {
			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0104")/*@res "��������Ϊnull"*/);
		}

		this.parentVO = parentVO;
	}

	@SuppressWarnings("unused")
	private JKBXMtappCtrlBusiVO() {
		//parentVO����Ϊ��
		super();
	}

	@Override
	public String getAttributeValue(String... attrs) {
		
		// �������й�����Ч��Ŀ���ֶΣ����������ҵ���л��
		String key = null;
		for (int i = 0; i < attrs.length; i++) {
			String[] split = StringUtil.split(attrs[i], ".");
			if(split.length ==1){
				key = split[0];
			}else if(BXConstans.ER_BUSITEM.equals(split[0])||BXConstans.JK_BUSITEM.equals(split[0])){
				key = split[1];
				break;
			}
		}
		if(key == null){
			return null;
		}
		return (String) parentVO.getAttributeValue(key);
	}

	@Override
	public String getBusiPK() {
		return parentVO.getPk_jkbx();
	}

	@Override
	public String getBusiSys() {
		return BXConstans.ERM_PRODUCT_CODE_Lower;
	}

	@Override
	public String getpk_djdl() {
		return parentVO.getDjdl();
	}

	@Override
	public String getBillType() {
		return parentVO.getParentBillType();
	}

	@Override
	public String getTradeType() {
		return parentVO.getDjlxbm();
	}

	@Override
	public String getCurrency() {
		return parentVO.getBzbm();
	}

	@Override
	public String getPk_org() {
		return parentVO.getFydwbm();
	}

	@Override
	public String getPk_group() {
		return parentVO.getPk_group();
	}

	@Override
	public String getMatterAppPK() {
		return parentVO.getPk_item();
	}
	
	@Override
	public String getDetailBusiPK() {
		return parentVO.getPk_busitem();
	}

	@Override
	public UFDate getBillDate() {
		return parentVO.getDjrq();
	}

	@Override
	public UFDouble[] getCurrInfo() {
		return new UFDouble[]{parentVO.getBbhl(),parentVO.getGroupbbhl(),parentVO.getGlobalbbhl()};
	}

	public UFDouble getFyyb_data() {
		if(fyyb_data == null){
			fyyb_data = parentVO.getCjkybje();
		}
		return fyyb_data;
	}

	public void setFyyb_data(UFDouble fyyb_data) {
		this.fyyb_data = fyyb_data;
	}

	@Override
	public String getForwardBusidetailPK() {
		// �������ε���Ϊ��������
		return parentVO.getBx_busitemPK();
	}

	@Override
	public String getSrcBusidetailPK() {
		// �������ε���Ϊ����
		return parentVO.getJk_busitemPK();
	}

	@Override
	public int getDirection() {
		return direction;
	}

	@Override
	public String getDataType() {
		return datatype;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	@Override
	public UFDouble getAmount() {
		if(amount == null){
			amount = parentVO.getYbje();
		}
		return amount;
	}

	public void setAmount(UFDouble amount) {
		this.amount = amount;
	}
	
	@Override
	public boolean isExceedEnable() {
		// ������������ɰ������봦��
		return BXConstans.BX_DJDL.equals(getpk_djdl())||!StringUtil.isEmpty(getSrcBusidetailPK());
	}

	@Override
	public String getMatterAppDetailPK() {
		return parentVO.getPk_mtapp_detail();
	}
	
	/**
	 * �Ƿ��������뵥��̯
	 * 
	 * @return
	 */
	public boolean getIsmashare(){
		return parentVO.getIsmashare() == null? false:parentVO.getIsmashare().booleanValue();
	}
	
}