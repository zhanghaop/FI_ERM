package nc.bs.erm.expamortize.control;

import java.io.Serializable;

import nc.bs.erm.expamortize.ExpAmoritizeConst;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.expamortize.ExpamtDetailVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * 摊销预算控制vo
 * 
 * @author chenshuaia
 *
 */
public class ExpamortizeYsControlVO implements IFYControl,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ExpamtinfoVO parentvo;
	private ExpamtDetailVO detailvo;
	
	public ExpamortizeYsControlVO(ExpamtinfoVO parentvo, ExpamtDetailVO detailvo) {
		this.parentvo = (ExpamtinfoVO) parentvo.clone();
		this.detailvo = (ExpamtDetailVO) detailvo.clone();
	}

	@Override
	public boolean isYSControlAble() {
		// 摊销完成，不进行预算控制
		if(parentvo.getBillstatus().equals(ExpAmoritizeConst.Billstatus_Amted)){
			return false;
		}
		return true;
	}

	@Override
	public boolean isJKControlAble() {
		return false;
	}

	@Override
	public boolean isSSControlAble() {
		return false;
	}

	@Override
	public String getPk_item() {
		return null;
	}

	@Override
	public Object getItemValue(String key) {
		if(StringUtil.isEmptyWithTrim(key)){
			return null;
		}
		String[] tokens = StringUtil.split(key, ".");
		
		if(tokens.length == 1){
			return parentvo.getAttributeValue(tokens[0]);
		}
		if("at".equals(tokens[0]) || "proc".equals(tokens[0]) || "proc1".equals(tokens[0])){
			return parentvo.getAttributeValue(tokens[1]);
		}else{
			return detailvo.getAttributeValue(tokens[1]);
		}
	}

	@Override
	public UFDouble[] getItemJe() {
		return new UFDouble[]{detailvo.getCurr_globalamount(),detailvo.getCurr_groupamount(),
				detailvo.getCurr_orgamount(),detailvo.getCurr_amount()};
	}

	@Override
	public String getBzbm() {
		return detailvo.getBzbm();
	}

	@Override
	public UFDouble[] getItemHl() {
		return new UFDouble[]{detailvo.getGlobalbbhl(),detailvo.getGroupbbhl(),detailvo.getBbhl()};
	}

	@Override
	public String getOperationUser() {
		return parentvo.getCreator();
	}

	@Override
	public UFDate getOperationDate() {
		return new UFDate();
	}

	@Override
	public String getFydwbm() {
		return detailvo.getAssume_org();
	}

	@Override
	public String getPk_group() {
		return parentvo.getPk_group();
	}

	@Override
	public String getDwbm() {
		return parentvo.getPk_org();
	}

	@Override
	public UFDate getDjrq() {
		return parentvo.getAmortize_date();
	}

	@Override
	public String getDjlxbm() {
		return ExpAmoritizeConst.Expamoritize_BILLTYPE;
	}

	@Override
	public String getPk() {
		return parentvo.getPk_expamtinfo();
	}

	@Override
	public String getDjdl() {
		return ExpAmoritizeConst.Expamoritize_DJDL;
	}

	@Override
	public String getDdlx() {
		return ExpAmoritizeConst.Expamoritize_BILLTYPE;
	}

	@Override
	public Integer getFx() {
		return 1;
	}

	@Override
	public boolean isSaveControl() {
		return false;
	}

	@Override
	public String getJsfs() {
		return null;
	}

	@Override
	public Integer getDjzt() {
		return parentvo.getBillstatus();
	}

	@Override
	public String getJkbxr() {
		return parentvo.getCreator();
	}

	@Override
	public String getOperator() {
		return parentvo.getCreator();
	}

	@Override
	public String getPk_org() {
		return parentvo.getPk_org();
	}

	@Override
	public String getParentBillType() {
		return ExpAmoritizeConst.Expamoritize_BILLTYPE;
	}

	@Override
	public String getPk_payorg() {
		return null;
	}

	@Override
	public UFDouble[] getPreItemJe() {
		return getItemJe();
	}
	
	@Override
	public String getWorkFlowBillPk() {
		return getPk();
	}

	@Override
	public String getWorkFolwBillType() {
		return getDjlxbm();
	}
}
