package nc.vo.erm.costshare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.trade.pub.IExAggVO;

/**
 * 
 * 单子表/单表头/单表体聚合VO
 *
 * 创建日期:
 * @author 
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.erm.costshare.CostShareVO")
public class  AggCostShareVO extends HYBillVO implements IExAggVO{
	
	private AggCostShareVO oldvo;
	
	private JKBXVO bxvo;
	
	/**
	 * 结转单在申请单上的执行记录
	 */
	private MtapppfVO[] maPfVos;
	
	public static String[] getBodyMultiSelectedItems(){
		return new String[] { CShareDetailVO.ASSUME_ORG, CShareDetailVO.ASSUME_DEPT, CShareDetailVO.PK_PCORG,
				CShareDetailVO.PK_RESACOSTCENTER, CShareDetailVO.PK_IOBSCLASS, CShareDetailVO.JOBID,
				CShareDetailVO.PROJECTTASK, CShareDetailVO.PK_CHECKELE, CShareDetailVO.CUSTOMER, CShareDetailVO.HBBM ,CShareDetailVO.PK_BRAND,CShareDetailVO.PK_PROLINE,
				CShareDetailVO.DEFITEM1,CShareDetailVO.DEFITEM2,CShareDetailVO.DEFITEM3,CShareDetailVO.DEFITEM4,CShareDetailVO.DEFITEM5,CShareDetailVO.DEFITEM6,CShareDetailVO.DEFITEM7,CShareDetailVO.DEFITEM8,CShareDetailVO.DEFITEM9,CShareDetailVO.DEFITEM10,
				CShareDetailVO.DEFITEM11,CShareDetailVO.DEFITEM12,CShareDetailVO.DEFITEM13,CShareDetailVO.DEFITEM14,CShareDetailVO.DEFITEM15,CShareDetailVO.DEFITEM16,CShareDetailVO.DEFITEM17,CShareDetailVO.DEFITEM18,CShareDetailVO.DEFITEM19,CShareDetailVO.DEFITEM20,
				CShareDetailVO.DEFITEM21,CShareDetailVO.DEFITEM22,CShareDetailVO.DEFITEM23,CShareDetailVO.DEFITEM24,CShareDetailVO.DEFITEM25,CShareDetailVO.DEFITEM26,CShareDetailVO.DEFITEM27,CShareDetailVO.DEFITEM28,CShareDetailVO.DEFITEM29,CShareDetailVO.DEFITEM30};
	}
	
	@Override
	public CircularlyAccessibleValueObject[] getChildrenVO() {
		return getTableVO(getTableCodes()[0]);
	}
	
	@Override
	public void setChildrenVO(CircularlyAccessibleValueObject[] children) {
		super.setChildrenVO(children);
		setTableVO(getTableCodes()[0], children);
	}
	

	//用于装载多子表数据的HashMap
	@SuppressWarnings("rawtypes")
	private HashMap hmChildVOs = new HashMap();
	
	/**
	 * 获取全部子表页签信息，包括动态扩展的子表
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String[] getAllTabcodes(){
		return (String[]) hmChildVOs.keySet().toArray(new String[hmChildVOs.keySet().size()]);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CircularlyAccessibleValueObject[] getAllChildrenVO() {
		@SuppressWarnings("rawtypes")
		ArrayList al = new ArrayList();
		String[] allTabcodes = getAllTabcodes();
		for(int i = 0; i < allTabcodes.length; i++){
			CircularlyAccessibleValueObject[] cvos
			        = getTableVO(getTableCodes()[i]);
			if(cvos != null)
				al.addAll(Arrays.asList(cvos));
		}
		
		return (SuperVO[]) al.toArray(new SuperVO[0]);
	}

	@Override
	public SuperVO[] getChildVOsByParentId(String tableCode, String parentid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDefaultTableCode() {
		return getTableCodes()[0];
	}

	@SuppressWarnings("rawtypes")
	@Override
	public HashMap getHmEditingVOs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentId(SuperVO item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getTableCodes() {
		return new String[]{
	 		   		"csharedetail"
	    };
	}

	@Override
	public String[] getTableNames() {
		return new String[] { "费用分摊明细" };
	}

	@Override
	public CircularlyAccessibleValueObject[] getTableVO(String tableCode) {
		return (CircularlyAccessibleValueObject[])hmChildVOs.get(tableCode);
	}

	@Override
	public void setParentId(SuperVO item, String id) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setTableVO(String tableCode,
			CircularlyAccessibleValueObject[] values) {
		hmChildVOs.put(tableCode,values);
	}
	
	
	/**
	 * 生成凭证的key值
	 * 
	 * 主键+利润中心+日期(日期是按基准时区输出)
	 * 
	 * @return
	 */
	public String getVoucherKey(){
		CostShareVO vo = (CostShareVO) getParentVO();
		CircularlyAccessibleValueObject[] childrenVOs = getChildrenVO();
		String pk_pcorg = null;
		if(childrenVOs != null && childrenVOs.length >0){
			CShareDetailVO childrenVO = (CShareDetailVO) childrenVOs[0];
			pk_pcorg = childrenVO.getPk_pcorg();
		}
		return  vo.getPrimaryKey() + pk_pcorg+vo.getBilldate().toStdString();
	}

	public AggCostShareVO getOldvo() {
		return oldvo;
	}

	public void setOldvo(AggCostShareVO oldvo) {
		this.oldvo = oldvo;
	}

	public JKBXVO getBxvo() {
		return bxvo;
	}

	public void setBxvo(JKBXVO bxvo) {
		this.bxvo = bxvo;
	}

	public MtapppfVO[] getMaPfVos() {
		return maPfVos;
	}

	public void setMaPfVos(MtapppfVO[] maPfVos) {
		this.maPfVos = maPfVos;
	}
}
