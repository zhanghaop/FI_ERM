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
	 * 经过ErVOUtils.prepareBxvoItemToHeaderClone(vo)处理的表头VO
	 */
	private JKBXHeaderVO parentVO = null;
	
	/**
	 * 回写金额
	 */
	private UFDouble amount = null;
	
	/**
	 * 费用原币金额，冲借款使用
	 */
	private UFDouble fyyb_data = null;

	/**
	 * 回写方向，默认正向
	 */
	private int direction = Direction_positive;
	
	/**
	 * 回写数据类型 ，默认执行数
	 */
	private String datatype = DataType_exe;
	
	/**
	 * 不传入参数mtAppTradeType，方法getMtAppTradeType()将查询数据库
	 *
	 * @param parentVO
	 * @author: wangyhh@ufida.com.cn
	 */
	public JKBXMtappCtrlBusiVO(JKBXHeaderVO parentVO) {
		super();

		if (parentVO == null) {
			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0104")/*@res "参数不能为null"*/);
		}

		this.parentVO = parentVO;
	}

	public JKBXMtappCtrlBusiVO(JKBXHeaderVO parentVO,String mtAppTradeType) {
		super();

		if (parentVO == null || mtAppTradeType == null) {
			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0104")/*@res "参数不能为null"*/);
		}

		this.parentVO = parentVO;
	}

	@SuppressWarnings("unused")
	private JKBXMtappCtrlBusiVO() {
		//parentVO不能为空
		super();
	}

	@Override
	public String getAttributeValue(String... attrs) {
		
		// 从属性中过滤有效的目标字段，从主表或者业务行获得
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
		// 冲借款下游单据为报销单行
		return parentVO.getBx_busitemPK();
	}

	@Override
	public String getSrcBusidetailPK() {
		// 冲借款上游单据为借款单行
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
		// 报销单、冲借款，可按超申请处理
		return BXConstans.BX_DJDL.equals(getpk_djdl())||!StringUtil.isEmpty(getSrcBusidetailPK());
	}

	@Override
	public String getMatterAppDetailPK() {
		return parentVO.getPk_mtapp_detail();
	}
	
	/**
	 * 是否上游申请单分摊
	 * 
	 * @return
	 */
	public boolean getIsmashare(){
		return parentVO.getIsmashare() == null? false:parentVO.getIsmashare().booleanValue();
	}
	
}