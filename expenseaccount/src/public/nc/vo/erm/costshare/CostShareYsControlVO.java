package nc.vo.erm.costshare;

import nc.bs.erm.costshare.IErmCostShareConst;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.er.pub.IFYControl;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * 费用结转单预算控制包装vo
 * 
 * @author lvhj
 *
 */
public class CostShareYsControlVO  implements IFYControl,java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 预算是否保存控制，需调用set方法注入
	 */
	private boolean isYsSaveControl = false;
	
	protected CostShareVO parentvo;
	
	protected CShareDetailVO detailvo;
	
	public CostShareYsControlVO(CostShareVO parentvo,CShareDetailVO detailvo){
		this.parentvo = (CostShareVO) parentvo.clone();
		this.detailvo = (CShareDetailVO) detailvo.clone();
	}
	
	public boolean isYsSaveControl() {
		return isYsSaveControl;
	}

	public void setYsSaveControl(boolean isYsSaveControl) {
		this.isYsSaveControl = isYsSaveControl;
	}
	@Override
	public boolean isYSControlAble() {
		// 非暂存状态，可进行预算控制
		if(parentvo.getBillstatus().equals(BXStatusConst.DJZT_TempSaved)){
			return false;
		}
		return true;
	}

	@Override
	public boolean isJKControlAble() {
		// 不支持借款控制
		return false;
	}

	@Override
	public String getPk_item() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getItemValue(String key) {
		if(StringUtil.isEmptyWithTrim(key)){
			return null;
		}
		if(BXConstans.EFFECTDATE.equals(key)||BXConstans.APPROVEDATE.equals(key)){
			key = CostShareVO.APPROVEDATE;
		}
		String[] tokens = StringUtil.split(key, ".");
		if(tokens.length == 1){
			return parentvo.getAttributeValue(tokens[0]);
		}
		if("cs".equals(tokens[0])){
			return parentvo.getAttributeValue(tokens[1]);
		}else{
			return detailvo.getAttributeValue(tokens[1]);
		}

	}

	@Override
	public UFDouble[] getItemJe() {
		return new UFDouble[]{detailvo.getGlobalbbje(),detailvo.getGroupbbje(),detailvo.getBbje(),detailvo.getAssume_amount()};
	}
	
	@Override
	public UFDouble[] getPreItemJe() {
		return getItemJe();
	}

	@Override
	public String getBzbm() {
		return parentvo.getBzbm();
	}

	@Override
	public UFDouble[] getItemHl() {
		return new UFDouble[]{detailvo.getGlobalbbhl(),detailvo.getGroupbbhl(),detailvo.getBbhl()};
	}

	@Override
	public String getOperationUser() {
		Integer billstatus = parentvo.getBillstatus();
		if(billstatus == null){
			return null;
		}else if(billstatus.intValue() == BXStatusConst.DJZT_Saved){
			return parentvo.getBillmaker();
		}else if(billstatus.intValue() == BXStatusConst.DJZT_Sign){
			return parentvo.getApprover();
		}
		return null;
	}

	@Override
	public UFDate getOperationDate() {
		Integer billstatus = parentvo.getBillstatus();
		if(billstatus == null){
			return null;
		}else if(billstatus.intValue() == BXStatusConst.DJZT_Saved){
			return getDjrq();
		}else if(billstatus.intValue() == BXStatusConst.DJZT_Sign){
			return getShrq();
		}
		return null;
	}

	public UFDate getShrq(){
		return parentvo.getApprovedate();
	}
	@Override
	public String getFydwbm() {
		return detailvo.getAssume_org();
	}
	@Override
	public String getDwbm() {
		return parentvo.getDwbm();
	}

	@Override
	public UFDate getDjrq() {
		return parentvo.getBilldate();
	}

	@Override
	public String getDjlxbm() {
		return parentvo.getPk_tradetype();
	}

	@Override
	public String getPk() {
		return parentvo.getPk_costshare();
	}

	@Override
	public String getDjdl() {
		return IErmCostShareConst.COSTSHARE_DJDL;
	}

	@Override
	public String getDdlx() {
		return parentvo.getPk_billtype();
	}

	@Override
	public Integer getFx() {
		return 1;
	}

	@Override
	public boolean isSaveControl() {
		return isYsSaveControl();
	}

	@Override
	public String getJsfs() {
		return parentvo.getJsfs();
	}

	@Override
	public Integer getDjzt() {
		return parentvo.getBillstatus();
	}

	@Override
	public String getJkbxr() {
		return parentvo.getOperator();
	}

	@Override
	public String getPk_group() {
		return parentvo.getPk_group();
	}

	@Override
	public String getOperator() {
		return parentvo.getBillmaker();
	}

	@Override
	public String getPk_org() {
		return detailvo.getAssume_org();
	}

	@Override
	public String getParentBillType() {
		return IErmCostShareConst.COSTSHARE_BILLTYPE;
	}

	@Override
	public String getPk_payorg() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getWorkFlowBillPk() {
		return parentvo.getSrc_id();
	}

	@Override
	public String getWorkFolwBillType() {
		return parentvo.getDjlxbm();
	}
}
