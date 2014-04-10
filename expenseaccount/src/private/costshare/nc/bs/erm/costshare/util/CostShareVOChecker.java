package nc.bs.erm.costshare.util;

import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.util.ErUtil;
import nc.utils.crosscheckrule.FipubCrossCheckRuleChecker;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.transaction.DataValidateException;
import nc.vo.cmp.util.StringUtils;
import nc.vo.ep.bx.MessageVO;
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
 * ���ý�ת��voУ����
 *
 * @author lvhj
 *
 */
public class CostShareVOChecker {
	
	private List<String> notRepeatFields;

	/**
	 * ����У��
	 *
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkSave(AggCostShareVO vo) throws BusinessException{
		prepare(vo);
		//�ݴ�ʱ������У��
		if(!((CostShareVO)vo.getParentVO()).getBillstatus().equals(BXStatusConst.DJZT_TempSaved)){
			checkHeader(vo);
			checkChildren(vo);
			checkIsCloseAcc(vo);
			
			//����У��
			new FipubCrossCheckRuleChecker()
					.check(((CostShareVO)vo.getParentVO()).getPk_org(), ((CostShareVO)vo.getParentVO()).getPk_tradetype(), vo);
		}
	}
	
	/**
	 * ��̨��鱨������ģ���Ƿ����
	 * 
	 * @param aggVo ����VO
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
									 * @res "�Ѿ����ʣ����ܽ��иò�����"
									 */);
		}
	}

	private void checkHeader(AggCostShareVO vo) throws ValidationException{
		CostShareVO header = (CostShareVO)vo.getParentVO();

		if(header.getYbje().compareTo(UFDouble.ZERO_DBL) <= 0){
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0070")/*@res "���ݽ��Ӧ����0��"*/);
		}
	}

	/**
	 * Ϊ���ý�ת������һЩ��ʼֵ
	 * ������Ϊnull���ֶ�����Ϊ0��
	 * @param vo
	 */
	private void prepare(AggCostShareVO vo) {
		prepareForNullJe(vo);
	}

	/**
	 * ����Ϊnull����ֶ�Ϊ0
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
	 * ���ý�ת��������֤
	 * @param vo
	 * @throws ValidationException
	 */
	private void checkChildren(AggCostShareVO vo) throws ValidationException{
		CShareDetailVO[] cShareVos = (CShareDetailVO[])vo.getChildrenVO();

		if (cShareVos == null || cShareVos.length <= 0){
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0071")/*@res "��̯��ϸ��Ϣ����Ϊ�գ�"*/);
		}

		CostShareVO parentVO = (CostShareVO)vo.getParentVO();

		UFDouble total = parentVO.getYbje();

		//�ϼ��ܽ��
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

				if(shareAmount.compareTo(UFDouble.ZERO_DBL) <= 0){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0072")/*@res "��̯��ϸ��Ϣ���ܰ������С�ڵ���0���У�"*/);
				}
				
				if(share_ratio.compareTo(UFDouble.ZERO_DBL)< 0){
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0087")/*@res "��̯��ϸ��Ϣ���ܰ����ֱ���С��0���У�"*/);
				}

				amount = amount.add(cShareVos[i].getAssume_amount());

				controlKey = new StringBuffer();

				for (int j = 0; j < attributeNames.length; j++) {
					if(getNotRepeatFields().contains(attributeNames[j])||
							attributeNames[j].startsWith(BXConstans.BODY_USERDEF_PREFIX)){
						controlKey.append(cShareVos[i].getAttributeValue(attributeNames[j]));
					}else{
						continue;
					}
				}

				if(!controlKeys.contains(controlKey.toString())){
					controlKeys.add(controlKey.toString());
				}else{
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0073")/*@res "��̯��ϸ��Ϣ�����ظ��У�"*/);
				}
			}
		}

		if (total.toDouble().compareTo(amount.toDouble()) != 0) {
			throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0074")/*@res "��ͷ���ϼƽ�������̯��ϸҳǩ�ܽ�һ�£�"*/);
		}
	}
	
	/**
	 * ɾ��У��
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
				msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0075")/*@res "�����Ѿ���Ч������ɾ����"*/;
			}

			if (!StringUtils.isNullWithTrim(msg)) {
				throw new DataValidateException(msg);
			}
			
			checkIsCloseAcc(vos[i]);
		}

	}

	/**
	 * ��ЧУ��
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
				msg+=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0111");/*"ȷ�����ڲ���Ϊ�Ƶ�����֮ǰ!"*/;
			}
			if (!StringUtils.isNullWithTrim(msg)) {
				throw new DataValidateException(msg);
			}
		}
	}
	/**
	 * ȡ����ЧУ��
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
	 * @param djzt ����״̬
	 * @param operation ִ�ж��� , @see {@link MessageVO}
	 * @param statusAllowed ������ִ�е�״ֵ̬
	 * @return �ձ�ʾ��֤ͨ��, ���򷵻ش�����ʾ��Ϣ
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
				operationName=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0076")/*@res "ȷ����Ч"*//*@res "ȷ����Ч"*/;
				break;
			case ActionUtils.UNSETTLE:
				operationName=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0077")/*@res "��ȷ����Ч"*//*@res "��ȷ����Ч"*/;
				break;
			case ActionUtils.DELETE:
				operationName=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000307")/*@res "ɾ��"*/;
				break;
			default:
				break;
		}

		switch (djzt) {
			case BXStatusConst.DJZT_TempSaved: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0126")/*@res "���ý�ת����Ϊ�ݴ�"*/;
				break;
			}
			case BXStatusConst.DJZT_Saved: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0078")/*@res "���ý�ת����δ��Ч"*/;
				break;
			}
			case BXStatusConst.DJZT_Sign: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0","0upp2012V575-0079")/*@res "���ý�ת�����Ѿ���Ч"*/;
				break;
			}
			default: {
				strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000266")/*@res "����״̬����"*/;
				break;
			}
		}

		return strMessage+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000267")/*@res ",����"*/+operationName;
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