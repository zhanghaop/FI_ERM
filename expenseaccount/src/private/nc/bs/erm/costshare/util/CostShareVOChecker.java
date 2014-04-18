package nc.bs.erm.costshare.util;

import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.utils.crosscheckrule.FipubCrossCheckRuleChecker;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.transaction.DataValidateException;
import nc.vo.cmp.util.StringUtils;
import nc.vo.ep.bx.MessageVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * 费用结转单vo校验类
 *
 * @author lvhj
 *
 */
public class CostShareVOChecker {
	
	private List<String> notRepeatFields;

	/**
	 * 保存校验
	 *
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkSave(AggCostShareVO vo) throws BusinessException{
		prepare(vo);
		//暂存时不进行校验
		if(!((CostShareVO)vo.getParentVO()).getBillstatus().equals(BXStatusConst.DJZT_TempSaved)){
			checkHeader(vo);
			checkChildren(vo);
			checkIsCloseAcc(vo);
			checkFc(vo.getParentVO());
			
			//交叉校验
			new FipubCrossCheckRuleChecker()
					.check(((CostShareVO)vo.getParentVO()).getPk_org(), ((CostShareVO)vo.getParentVO()).getPk_tradetype(), vo);
		}
	}
	
	private void checkFc(CircularlyAccessibleValueObject parentVO) throws BusinessException {
		String pkTradetype = ((CostShareVO)parentVO).getPk_tradetype();
		DjLXVO djlxVO = ErmDjlxCache.getInstance().getDjlxVO(((CostShareVO)parentVO).getPk_group(), pkTradetype);
		if(djlxVO.getFcbz().booleanValue()){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("expensepub_0", "02011002-0192"));/**
			 * @res*
			 *      "对应结转单已封存，请修改或解封对应结转单！"
			 */
		}
	}

	/**
	 * 后台检查报销管理模块是否关账
	 * 
	 * @param aggVo 单据VO
	 * @throws BusinessException
	 */
	public static void checkIsCloseAcc(AggCostShareVO aggVo) throws BusinessException {
		CostShareVO head = (CostShareVO)aggVo.getParentVO();
		String moduleCode = BXConstans.ERM_MODULEID;
		String pk_org = head.getPk_org();
		UFDate date = head.getBilldate();
		if (ErUtil.isOrgCloseAcc(moduleCode, pk_org, date)) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0146")/*
									 * @res "已经关帐，不能进行该操作！"
									 */);
		}
	}

	private void checkHeader(AggCostShareVO vo) throws ValidationException{
		CostShareVO header = (CostShareVO)vo.getParentVO();
		// 费用调整单不控制合计金额为0、负数
		boolean isAdjust = false ;
		try {
			isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(header.getPk_group(), header.getDjlxbm(), ErmDjlxConst.BXTYPE_ADJUST);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		if(header.getYbje().compareTo(UFDouble.ZERO_DBL) <= 0 && !isAdjust){
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0070")/*@res "单据金额应大于0！"*/);
		}
	}

	/**
	 * 为费用结转单设置一些初始值
	 * 例如金额为null的字段设置为0、
	 * @param vo
	 */
	private void prepare(AggCostShareVO vo) {
		prepareForNullJe(vo);
	}

	/**
	 * 设置为null金额字段为0
	 * @param vo
	 */
	private void prepareForNullJe(AggCostShareVO vo) {
		CircularlyAccessibleValueObject parentVO = vo.getParentVO();

		String[] jeField = new String[] {
 				"total","ybje","bbje"};
		String[] bodyJeField = new String[] {
 				"assume_amount", "bbje"};
		for(String field:jeField){
			if(parentVO.getAttributeValue(field)==null){
				parentVO.setAttributeValue(field, UFDouble.ZERO_DBL);
			}
		}

		for(String field:bodyJeField){
			CircularlyAccessibleValueObject[] childrenVO = vo.getChildrenVO();
			if(childrenVO!=null){
				for(CircularlyAccessibleValueObject item:childrenVO){
					if(item.getAttributeValue(field)==null){
						item.setAttributeValue(field, UFDouble.ZERO_DBL);
					}
				}
			}
		}
	}

	/**
	 * 费用结转单表体验证
	 * @param vo
	 * @throws ValidationException
	 */
	private void checkChildren(AggCostShareVO vo) throws ValidationException{
		CShareDetailVO[] cShareVos = (CShareDetailVO[])vo.getChildrenVO();

		if (cShareVos == null || cShareVos.length <= 0){
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0071")/*@res "分摊明细信息不能为空！"*/);
		}

		CostShareVO parentVO = (CostShareVO)vo.getParentVO();

		// 费用调整单不控制金额为0、负数
		boolean isAdjust = false ;
		try {
			isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(parentVO.getPk_group(), parentVO.getDjlxbm(), ErmDjlxConst.BXTYPE_ADJUST);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		
		UFDouble total = parentVO.getYbje();

		//合计总金额
		UFDouble amount = UFDouble.ZERO_DBL;
		List<String> controlKeys = new ArrayList<String>();
		StringBuffer controlKey = null;
		String[] attributeNames =  cShareVos[0].getAttributeNames();

		for (int i = 0; i < cShareVos.length; i++) {
			if(cShareVos[i].getStatus() != VOStatus.DELETED){
				UFDouble shareAmount = cShareVos[i].getAssume_amount();
				UFDouble share_ratio = cShareVos[i].getShare_ratio();
				if(shareAmount == null){
					shareAmount = UFDouble.ZERO_DBL;
				}

				if(!isAdjust &&shareAmount.compareTo(UFDouble.ZERO_DBL) <= 0){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0072")/*@res "分摊明细信息不能包括金额小于等于0的行！"*/);
				}
				
				if(!isAdjust &&  share_ratio.compareTo(UFDouble.ZERO_DBL)< 0 ){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0087")/*@res "分摊明细信息不能包括分比例小于0的行！"*/);
				}

				amount = amount.add(cShareVos[i].getAssume_amount());

				controlKey = new StringBuffer();

				for (int j = 0; j < attributeNames.length; j++) {
					String attr = attributeNames[j];
					if(getNotRepeatFields().contains(attr)||
							attr.startsWith(BXConstans.BODY_USERDEF_PREFIX)
							||(isAdjust&&CShareDetailVO.YSDATE.equals(attr))){
						// 费用调整单情况，不可重复字段包括预算占用日期
						controlKey.append(cShareVos[i].getAttributeValue(attr));
					}else{
						continue;
					}
				}

				if(!controlKeys.contains(controlKey.toString())){
					controlKeys.add(controlKey.toString());
				}else{
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0073")/*@res "分摊明细信息存在重复行！"*/);
				}
			}
		}

		if (total.toDouble().compareTo(amount.toDouble()) != 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0074")/*@res "表头“合计金额”与表体分摊明细页签总金额不一致！"*/);
		}
	}
	
	/**
	 * 删除校验
	 *
	 * @param vos
	 */
	public void checkDelete(AggCostShareVO[] vos) throws BusinessException{
		if(vos == null){
			return;
		}

		for (int i = 0; i < vos.length; i++) {
			AggCostShareVO aggCostShareVO = vos[i];
			CostShareVO head = (CostShareVO)aggCostShareVO.getParentVO();
			String msg = checkBillStatus(head.getBillstatus(), ActionUtils.DELETE, new int[]{
				BXStatusConst.DJZT_Saved,
				BXStatusConst.DJZT_TempSaved
							});

			if(head.getEffectstate()!=null && head.getEffectstate().equals(IErmCostShareConst.CostShare_Bill_Effectstate_Y)){
				msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0075")/*@res "单据已经生效，不能删除！"*/;
			}

			if (!StringUtils.isNullWithTrim(msg)) {
				throw new DataValidateException(msg);
			}
			
			checkIsCloseAcc(vos[i]);
		}

	}

	/**
	 * 生效校验
	 *
	 * @param vos
	 */
	public void checkApprove(AggCostShareVO[] vos,UFDate buDate) throws DataValidateException{
		if(vos == null){
			return;
		}

		for (int i = 0; i < vos.length; i++) {
			AggCostShareVO aggCostShareVO = vos[i];
			CostShareVO head = (CostShareVO)aggCostShareVO.getParentVO();
			String msg = checkBillStatus(head.getBillstatus(), ActionUtils.SETTLE, new int[] {BXStatusConst.DJZT_Saved});
			if (StringUtils.isNullWithTrim(msg)) {
				msg="";
			}else {
				msg+="\n";
			}
			if (head.getBilldate().after(buDate)) {
				msg+=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0111");/*"确认日期不能为制单日期之前!"*/;
			}
			if (!StringUtils.isNullWithTrim(msg)) {
				throw new DataValidateException(msg);
			}
		}
	}
	/**
	 * 取消生效校验
	 *
	 * @param vos
	 */
	public void checkunApprove(AggCostShareVO[] vos) throws DataValidateException, ValidationException{
		if(vos == null){
			return;
		}

		for (int i = 0; i < vos.length; i++) {
			AggCostShareVO aggCostShareVO = vos[i];
			CostShareVO head = (CostShareVO)aggCostShareVO.getParentVO();
			String msg = checkBillStatus(head.getBillstatus(), ActionUtils.UNSETTLE, new int[] {BXStatusConst.DJZT_Sign});
			if (!StringUtils.isNullWithTrim(msg)) {
				throw new DataValidateException(msg);
			}
		}
	}

	/**
	 * @param djzt 单据状态
	 * @param operation 执行动作 , @see {@link MessageVO}
	 * @param statusAllowed 允许动作执行的状态值
	 * @return 空表示验证通过, 否则返回错误提示信息
	 */
	public static String checkBillStatus(int djzt, int operation, int[] statusAllowed) {

		String strMessage = null;

		for (int i = 0; i < statusAllowed.length; i++) {
			if(statusAllowed[i]==djzt)
				return null;
		}

		String operationName = null;

		switch (operation) {
			case ActionUtils.SETTLE:
				operationName=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0076")/*@res "确认生效"*//*@res "确认生效"*/;
				break;
			case ActionUtils.UNSETTLE:
				operationName=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0077")/*@res "反确认生效"*//*@res "反确认生效"*/;
				break;
			case ActionUtils.DELETE:
				operationName=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000307")/*@res "删除"*/;
				break;
			default:
				break;
		}

		switch (djzt) {
			case BXStatusConst.DJZT_TempSaved: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0126")/*@res "费用结转单据为暂存"*/;
				break;
			}
			case BXStatusConst.DJZT_Saved: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0078")/*@res "费用结转单据未生效"*/;
				break;
			}
			case BXStatusConst.DJZT_Sign: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0079")/*@res "费用结转单据已经生效"*/;
				break;
			}
			default: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000266")/*@res "单据状态不明"*/;
				break;
			}
		}

		return strMessage+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000267")/*@res ",不能"*/+operationName;
	}

	public List<String> getNotRepeatFields() {
		if(notRepeatFields == null){
			notRepeatFields = new ArrayList<String>();
			notRepeatFields.add(CShareDetailVO.ASSUME_ORG);
			notRepeatFields.add(CShareDetailVO.ASSUME_DEPT);
			notRepeatFields.add(CShareDetailVO.PK_IOBSCLASS);
			notRepeatFields.add(CShareDetailVO.PK_PCORG);
			notRepeatFields.add(CShareDetailVO.PK_RESACOSTCENTER);
			notRepeatFields.add(CShareDetailVO.JOBID);
			notRepeatFields.add(CShareDetailVO.PROJECTTASK);
			notRepeatFields.add(CShareDetailVO.PK_CHECKELE);
			notRepeatFields.add(CShareDetailVO.CUSTOMER);
			notRepeatFields.add(CShareDetailVO.HBBM);
			notRepeatFields.add(CShareDetailVO.PK_BRAND);
			notRepeatFields.add(CShareDetailVO.PK_PROLINE);
		}
		return notRepeatFields;
	}
	
}