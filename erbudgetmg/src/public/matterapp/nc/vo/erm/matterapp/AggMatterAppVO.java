package nc.vo.erm.matterapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.trade.pub.IExAggVO;

/**
 * 
 * 单子表/单表头/单表体聚合VO
 * 
 * 创建日期:
 * 
 * @author
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.erm.matterapp.MatterAppVO")
public class AggMatterAppVO extends HYBillVO implements Cloneable, IExAggVO {
	private static String[] headYbAmounts;

	private static String[] headOrgAmounts;

	private static String[] headGroupAmounts;

	private static String[] headGlobalAmounts;

	private static String[] bodyYbAmounts;

	private static String[] bodyOrgAmounts;

	private static String[] bodyGroupAmounts;

	private static String[] bodyGlobalAmounts;

	private static String[] headAmounts;

	private static String[] bodyAmounts;

	private static List<String> applyOrgHeadIterms;

	private static List<String> applyOrgBodyIterms;

	static {

		headAmounts = new String[] { MatterAppVO.ORIG_AMOUNT, MatterAppVO.REST_AMOUNT, MatterAppVO.EXE_AMOUNT,
				MatterAppVO.ORG_AMOUNT, MatterAppVO.ORG_EXE_AMOUNT, MatterAppVO.ORG_REST_AMOUNT,
				MatterAppVO.GROUP_AMOUNT, MatterAppVO.GROUP_REST_AMOUNT, MatterAppVO.GROUP_EXE_AMOUNT,
				MatterAppVO.GLOBAL_AMOUNT, MatterAppVO.GLOBAL_REST_AMOUNT, MatterAppVO.GLOBAL_EXE_AMOUNT };

		bodyAmounts = new String[] { MtAppDetailVO.ORIG_AMOUNT, MtAppDetailVO.REST_AMOUNT, MtAppDetailVO.EXE_AMOUNT,
				MtAppDetailVO.ORG_AMOUNT, MtAppDetailVO.ORG_REST_AMOUNT, MtAppDetailVO.ORG_EXE_AMOUNT,
				MtAppDetailVO.GROUP_AMOUNT, MtAppDetailVO.GROUP_EXE_AMOUNT, MtAppDetailVO.GROUP_REST_AMOUNT,
				MtAppDetailVO.GLOBAL_AMOUNT, MtAppDetailVO.GLOBAL_EXE_AMOUNT, MtAppDetailVO.GLOBAL_REST_AMOUNT ,MtAppDetailVO.USABLE_AMOUT };

		headYbAmounts = new String[] { MatterAppVO.ORIG_AMOUNT, MatterAppVO.REST_AMOUNT, MatterAppVO.EXE_AMOUNT,
				MatterAppVO.PRE_AMOUNT };

		headOrgAmounts = new String[] { MatterAppVO.ORG_AMOUNT, MatterAppVO.ORG_EXE_AMOUNT,
				MatterAppVO.ORG_REST_AMOUNT, MatterAppVO.ORG_PRE_AMOUNT };

		headGroupAmounts = new String[] { MatterAppVO.GROUP_AMOUNT, MatterAppVO.GROUP_REST_AMOUNT,
				MatterAppVO.GROUP_EXE_AMOUNT, MatterAppVO.GROUP_PRE_AMOUNT };

		headGlobalAmounts = new String[] { MatterAppVO.GLOBAL_AMOUNT, MatterAppVO.GLOBAL_REST_AMOUNT,
				MatterAppVO.GLOBAL_EXE_AMOUNT, MatterAppVO.GLOBAL_PRE_AMOUNT };

		bodyYbAmounts = new String[] { MtAppDetailVO.ORIG_AMOUNT, MtAppDetailVO.REST_AMOUNT, MtAppDetailVO.EXE_AMOUNT,
				MtAppDetailVO.PRE_AMOUNT, MtAppDetailVO.USABLE_AMOUT };

		bodyOrgAmounts = new String[] { MtAppDetailVO.ORG_AMOUNT, MtAppDetailVO.ORG_REST_AMOUNT,
				MtAppDetailVO.ORG_EXE_AMOUNT, MtAppDetailVO.ORG_PRE_AMOUNT };

		bodyGroupAmounts = new String[] { MtAppDetailVO.GROUP_AMOUNT, MtAppDetailVO.GROUP_EXE_AMOUNT,
				MtAppDetailVO.GROUP_REST_AMOUNT, MtAppDetailVO.GROUP_PRE_AMOUNT };

		bodyGlobalAmounts = new String[] { MtAppDetailVO.GLOBAL_AMOUNT, MtAppDetailVO.GLOBAL_EXE_AMOUNT,
				MtAppDetailVO.GLOBAL_REST_AMOUNT, MtAppDetailVO.GLOBAL_PRE_AMOUNT };

		applyOrgHeadIterms = new ArrayList<String>();
		applyOrgHeadIterms.add(MatterAppVO.APPLY_DEPT);
		applyOrgHeadIterms.add(MatterAppVO.BILLMAKER);
		applyOrgHeadIterms.add(MatterAppVO.REASON);
		applyOrgHeadIterms.add(MatterAppVO.PK_CUSTOMER);
		applyOrgHeadIterms.add(MatterAppVO.ASSUME_DEPT);

		applyOrgBodyIterms = new ArrayList<String>();
		applyOrgBodyIterms.add(MtAppDetailVO.PK_PCORG);
		applyOrgBodyIterms.add(MtAppDetailVO.PK_RESACOSTCENTER);
		applyOrgBodyIterms.add(MtAppDetailVO.PK_SALESMAN);
		applyOrgBodyIterms.add(MtAppDetailVO.PK_IOBSCLASS);
		applyOrgBodyIterms.add(MtAppDetailVO.PK_PROJECT);
		applyOrgBodyIterms.add(MtAppDetailVO.PK_WBS);

		applyOrgBodyIterms.add(MtAppDetailVO.PK_CUSTOMER);
		applyOrgBodyIterms.add(MtAppDetailVO.REASON);
		applyOrgBodyIterms.add(MtAppDetailVO.PK_CHECKELE);
		applyOrgBodyIterms.add(MtAppDetailVO.PK_SUPPLIER);
	}

	@Override
	public Object clone() {
		AggMatterAppVO aggVo = new AggMatterAppVO();

		if (getParentVO() != null) {
			aggVo.setParentVO((CircularlyAccessibleValueObject) getParentVO().clone());
		}

		for (int i = 0; i < getTableCodes().length; i++) {
			CircularlyAccessibleValueObject[] cvos = getTableVO(getTableCodes()[i]);
			if (cvos != null) {
				CircularlyAccessibleValueObject[] clonevos = new CircularlyAccessibleValueObject[cvos.length];
				for (int j = 0; j < cvos.length; j++) {
					if (cvos[j] != null) {
						clonevos[j] = (CircularlyAccessibleValueObject) cvos[j].clone();
					}
				}
				aggVo.setTableVO(getTableCodes()[i], clonevos);
			}
		}

		return aggVo;
	}

	
	@Override
	public void setChildrenVO(CircularlyAccessibleValueObject[] children) {
		setTableVO(getTableCodes()[0], children);
	}
	
	
	/**
	 * 返回ParentVo,避免强制转型
	 */
	public MatterAppVO getParentVO() {
		return (MatterAppVO) super.getParentVO();
	}

	public MtAppDetailVO[] getChildrenVO() {
		CircularlyAccessibleValueObject[] tableVO = getTableVO(getTableCodes()[0]);
		if(tableVO == null || tableVO.length == 0){
			return null;
		}
		MtAppDetailVO[] childs = new MtAppDetailVO[tableVO.length];
		System.arraycopy(tableVO, 0, childs, 0, tableVO.length);
		return childs;
	}

	public static String[] getHeadYbAmounts() {
		return headYbAmounts;
	}

	public static String[] getHeadGroupAmounts() {
		return headGroupAmounts;
	}

	public static String[] getHeadGlobalAmounts() {
		return headGlobalAmounts;
	}

	public static String[] getHeadOrgAmounts() {
		return headOrgAmounts;
	}

	public static String[] getBodyYbAmounts() {
		return bodyYbAmounts;
	}

	public static String[] getBodyOrgAmounts() {
		return bodyOrgAmounts;
	}

	public static String[] getBodyGroupAmounts() {
		return bodyGroupAmounts;
	}

	public static String[] getBodyGlobalAmounts() {
		return bodyGlobalAmounts;
	}

	public static String[] getHeadAmounts() {
		return headAmounts;
	}

	public static String[] getBodyAmounts() {
		return bodyAmounts;
	}

	public static List<String> getApplyOrgHeadIterms() {
		return applyOrgHeadIterms;
	}

	public static List<String> getApplyOrgBodyIterms() {
		return applyOrgBodyIterms;
	}
	
	//用于装载多子表数据的HashMap
	@SuppressWarnings("rawtypes")
	private HashMap hmChildVOs = new HashMap();
	
	
	/**
	 * 返回多个子表的编码
	 * 必须与单据模版的页签编码对应
	 * 创建日期：
	 * @return String[]
	 */
	public String[] getTableCodes(){
		          
		return new String[]{
		 		 		   		"mtapp_detail"
		   		    };
		          
	}
	
	
	/**
	 * 返回多个子表的中文名称
	 * 创建日期：
	 * @return String[]
	 */
	public String[] getTableNames(){
		
		return new String[] { NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0094")/*@res "费用申请单明细"*/ };
	}
	
	
	/**
	 * 取得所有子表的所有VO对象
	 * 创建日期：
	 * @return CircularlyAccessibleValueObject[]
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CircularlyAccessibleValueObject[] getAllChildrenVO(){
		
		ArrayList al = new ArrayList();
		for(int i = 0; i < getTableCodes().length; i++){
			CircularlyAccessibleValueObject[] cvos
			        = getTableVO(getTableCodes()[i]);
			if(cvos != null)
				al.addAll(Arrays.asList(cvos));
		}
		
		return (SuperVO[]) al.toArray(new SuperVO[0]);
	}
	
	
	/**
	 * 返回每个子表的VO数组
	 * 创建日期：
	 * @return CircularlyAccessibleValueObject[]
	 */
	public CircularlyAccessibleValueObject[] getTableVO(String tableCode){
		
		return (CircularlyAccessibleValueObject[])
		            hmChildVOs.get(tableCode);
	}
	
	
	/**
	 * 
	 * 创建日期：
	 * @param SuperVO item
	 * @param String id
	 */
	public void setParentId(SuperVO item,String id){}
	
	/**
	 * 为特定子表设置VO数据
	 * 创建日期：
	 * @param String tableCode
	 * @para CircularlyAccessibleValueObject[] vos
	 */
	@SuppressWarnings("unchecked")
	public void setTableVO(String tableCode,CircularlyAccessibleValueObject[] vos){
		
		hmChildVOs.put(tableCode,vos);
	}
	
	/**
	 * 缺省的页签编码
	 * 创建日期：
	 * @return String 
	 */
	public String getDefaultTableCode(){
		
		return getTableCodes()[0];
	}
	
	/**
	 * 
	 * 创建日期：
	 * @param String tableCode
	 * @param String parentId
	 * @return SuperVO[]
	 */
	public SuperVO[] getChildVOsByParentId(String tableCode,String parentId){
		
		return null;
	}
	
	
	/**
	 * 
	 * 创建日期：
	 * @return HashMap
	 */
	@SuppressWarnings("rawtypes")
	public HashMap getHmEditingVOs() throws Exception{
		
		return null;
	}
	
	/**
	 * 
	 * 创建日期:
	 * @param SuperVO item
	 * @return String
	 */
	public String getParentId(SuperVO item){
		
		return null;
	}
	
}
