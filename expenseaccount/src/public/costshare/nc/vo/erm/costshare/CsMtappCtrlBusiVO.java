package nc.vo.erm.costshare;

import java.io.Serializable;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * 结转单回写申请单，回写结构
 * 
 * @author lvhj
 *
 */
public class CsMtappCtrlBusiVO implements IMtappCtrlBusiVO,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 只处理报销单事前分摊情况，所以表头字段都按报销单走
	 */
	private JKBXHeaderVO parentVO = null;
	
	private CShareDetailVO detailVO = null;
	

	/**
	 * 回写方向，默认正向
	 */
	private int direction = Direction_positive;
	
	/**
	 * 回写数据类型 ，默认执行数
	 */
	private String datatype = DataType_exe;
	
	public CsMtappCtrlBusiVO(CostShareVO csparentVO,CShareDetailVO detailVO) {
		super();

		if (csparentVO == null) {
			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0104")/*@res "参数不能为null"*/);
		}

		this.parentVO = csparentVO.getBxheadvo();
		this.detailVO = detailVO;
	}

	@Override
	public String getAttributeValue(String... attrs) {
		
		// 从属性中过滤有效的目标字段，优先去分摊明细字段，没有情况再取表头字段
		String attr = null;
		for (int i = 0; i < attrs.length; i++) {
			String[] split = StringUtil.split(attrs[i], ".");
			if(split.length ==1){
				attr = attrs[i];
			}else if(BXConstans.COSTSHAREDETAIL.equals(split[0])){
				attr = attrs[i];
				break;
			}
		}
		
		if(StringUtil.isEmptyWithTrim(attr)){
			return null;
		}
		String[] tokens = StringUtil.split(attr, ".");
		// 表头对照字段值，从报销单表头上获得;表体字段从分摊明细中获得
		if(tokens.length == 1){
			return (String) parentVO.getAttributeValue(tokens[0]);
		}else{
			return (String) detailVO.getAttributeValue(tokens[1]);
		}
	}

	@Override
	public String getBusiPK() {
		// 按报销单进行回写申请单
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
		// 回写主组织为分摊费用承担组织
		return detailVO.getAssume_org();
	}

	@Override
	public String getPk_group() {
		return parentVO.getPk_group();
	}

	@Override
	public String getMatterAppPK() {
		return detailVO.getPk_item();
	}
	
	@Override
	public String getDetailBusiPK() {
		return detailVO.getPk_cshare_detail();
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
		return detailVO.getAssume_amount();
	}

	@Override
	public String getForwardBusidetailPK() {
		return null;
	}

	@Override
	public String getSrcBusidetailPK() {
		return null;
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
		return detailVO.getAssume_amount();
	}
	
	@Override
	public boolean isExceedEnable() {
		// 可超申请
		return true;
	}

	@Override
	public String getMatterAppDetailPK() {
		return detailVO.getPk_mtapp_detail();
	}
	
	
}