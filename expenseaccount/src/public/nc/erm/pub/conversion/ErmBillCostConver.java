package nc.erm.pub.conversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bd.accperiod.InvalidAccperiodExcetion;
import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.expamortize.ExpAmoritizeConst;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDate;

/**
 * ҵ��VOת��Ϊ������ת����<br>
 * ע���:
 * <li>����֯ȡ���óе���λ
 * @author luolch
 *
 */
public class ErmBillCostConver {

    public static ExpenseAccountVO[] getExpAccVO(final AggCostShareVO vo) throws BusinessException {
        CostShareVO parentVO = (CostShareVO) vo.getParentVO();
        CircularlyAccessibleValueObject[] childsVOS = vo.getChildrenVO();
        List<ExpenseAccountVO> expList = new ArrayList<ExpenseAccountVO>();
        if ((childsVOS!=null) && (childsVOS.length!=0)) {
        	// �Ƿ��Ƿ��õ������Ľ�ת
        	boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(parentVO.getPk_group(), parentVO.getDjlxbm(), ErmDjlxConst.BXTYPE_ADJUST);
            ExpenseAccountVO[] exvos = new ExpenseAccountVO[childsVOS.length];
            String[] attrNames = new ExpenseAccountVO().getAttributeNames();
            for (int i = 0; i < childsVOS.length; i++) {
                exvos[i]  = new ExpenseAccountVO();
                for (int j = 0; j < attrNames.length; j++) {
                    String csvoField = ErmBillFieldContrastCache.getSrcField(ErmBillFieldContrastCache.FieldContrast_SCENE_ExpenseAccount,
                            IErmCostShareConst.COSTSHARE_BILLTYPE,
                            attrNames[j]);
                    if (csvoField!=null) {
                        String[] tokens = StringUtil.split(csvoField, ".");
                        Object value = null;
                        if("zb".equals(tokens[0])){
                            value =  parentVO.getAttributeValue(tokens[1]);
                        }else{
                            value =  childsVOS[i].getAttributeValue(csvoField);
                        }
                        exvos[i].setAttributeValue(attrNames[j], value);
                    }
                }

                exvos[i].setAssume_org((String) childsVOS[i].getAttributeValue(CShareDetailVO.ASSUME_ORG));
                exvos[i].setPk_org((String) childsVOS[i].getAttributeValue(CShareDetailVO.ASSUME_ORG));
                if(isAdjust){
                	// ���õ����������̯��ϸ��Ԥ��ռ�����ڲ�Ϊ����������÷����ʵ�����ΪԤ��ռ������
                	UFDate ysdate = (UFDate) childsVOS[i].getAttributeValue(CShareDetailVO.YSDATE);
                	if(ysdate != null){
                		exvos[i].setBilldate(ysdate);
                	}
                }
                
                setExpAccperid(exvos[i], exvos[i].getPk_org(), exvos[i].getBilldate());
                
                exvos[i].setBx_tradetype(parentVO.getDjlxbm());//��������������
                expList.add(exvos[i]);
            }
        }
        return expList.toArray(new ExpenseAccountVO[0]);
    }

    public static ExpenseAccountVO[] getExpAccVO(final AggExpamtinfoVO vo) throws BusinessException {
        ExpamtinfoVO parentVO = (ExpamtinfoVO) vo.getParentVO();
        CircularlyAccessibleValueObject[] childsVOS = vo.getChildrenVO();
        List<ExpenseAccountVO> expList = new ArrayList<ExpenseAccountVO>();
        if ((childsVOS!=null) && (childsVOS.length!=0)) {
            ExpenseAccountVO[] exvos = new ExpenseAccountVO[childsVOS.length];
            String[] attrNames = new ExpenseAccountVO().getAttributeNames();
            for (int i = 0; i < childsVOS.length; i++) {
                exvos[i]  = new ExpenseAccountVO();
                for (int j = 0; j < attrNames.length; j++) {
                    String csvoField = ErmBillFieldContrastCache.getSrcField(ErmBillFieldContrastCache.FieldContrast_SCENE_ExpenseAccount,
                            ExpAmoritizeConst.Expamoritize_BILLTYPE,
                            attrNames[j]);
                    if (csvoField!=null) {
                        String[] tokens = StringUtil.split(csvoField, ".");
                        Object value = null;
                        if("zb".equals(tokens[0])){
                            value =  parentVO.getAttributeValue(tokens[1]);
                        }else{
                            value =  childsVOS[i].getAttributeValue(csvoField);
                        }
                        exvos[i].setAttributeValue(attrNames[j], value);
                    }
                }
                
                setExpAccperid(exvos[i], exvos[i].getPk_org(), parentVO.getAmortize_date());

                exvos[i].setSrc_billno(parentVO.getBx_billno());
                exvos[i].setSrc_billtype(ExpAmoritizeConst.Expamoritize_BILLTYPE);
                exvos[i].setSrc_tradetype(ExpAmoritizeConst.Expamoritize_BILLTYPE);
                exvos[i].setBillstatus(BXStatusConst.DJZT_Sign);
                
                exvos[i].setBx_tradetype(parentVO.getBx_pk_billtype());
                expList.add(exvos[i]);
            }
        }
        return expList.toArray(new ExpenseAccountVO[0]);
    }

    public static ExpenseAccountVO[] getExpAccVO(final JKBXVO vo) throws BusinessException {
        if ((vo == null) || (vo.getParentVO() == null)) {
        	// ����Ԥ��ı����������ɷ�����
            return new ExpenseAccountVO[]{};
        }
        List<ExpenseAccountVO> expaccVOList = new ArrayList<ExpenseAccountVO>();
        BXBusItemVO[] childrenVO = vo.getChildrenVO();
        JKBXHeaderVO parentVO = (JKBXHeaderVO) vo.getParentVO().clone();
        String[] attrNames = new ExpenseAccountVO().getAttributeNames();
        if ((childrenVO == null) || (childrenVO.length == 0)) {
//            //ת��Ϊ����
//            ExpenseAccountVO expaccVO  = new ExpenseAccountVO();
//            for (int i = 0; i < attrNames.length; i++) {
//                String bxvoField = ErmBillFieldContrastCache.getSrcField(
//                        ErmBillFieldContrastCache.FieldContrast_SCENE_ExpenseAccount, BXConstans.BX_DJLXBM,
//                        attrNames[i]);
//                if (bxvoField!=null) {
//                    expaccVO.setAttributeValue(attrNames[i], parentVO.getAttributeValue(bxvoField));
//                }
//            }
//
//            expaccVO.setPk_group(parentVO.getPk_group());
//            expaccVO.setPk_org(parentVO.getPk_org());
//            
//            setExpAccperid(expaccVO, parentVO.getFydwbm(), parentVO.getDjrq());
//
//            expaccVO.setSrc_billtype(parentVO.getParentBillType());
//            expaccVO.setSrc_tradetype(parentVO.getDjlxbm());
//            return new ExpenseAccountVO[]{expaccVO};
        	// EHP2������ҵ����Ϊ���������ת�����ɱ���������
        	return new ExpenseAccountVO[0];
        } else {
            ExpenseAccountVO expaccVO;
            Map<String, String> fbField = new HashMap<String, String>();
            for (int i = 0; i < childrenVO.length; i++) {
                BXBusItemVO item = childrenVO[i];
                String[] attributeNames = item.getAttributeNames();
                for (String attr : attributeNames) {
                    //��Щ���Ա����У���������ͷ������
                    if (attr.equals(JKBXHeaderVO.CASHPROJ) || attr.equals(JKBXHeaderVO.JKBXR)
                            || attr.equals(JKBXHeaderVO.CASHITEM)) {
                        continue;
                    }
//                    if (attr.indexOf("defitem") >= 0) {
//                        String att1 = attr.substring(7);
//                        parentVO.setAttributeValue("zyx" + att1, item.getAttributeValue(attr));
//                    } else {
//                        parentVO.setAttributeValue(attr, item.getAttributeValue(attr));
//                    }
                    fbField.put(attr, null);
                }

                //�������� ������ arap_item_clb
                parentVO.setPk_jkbx(parentVO.getPk_jkbx());
                expaccVO  = new ExpenseAccountVO();
//                for (int j = 0; j < attrNames.length; j++) {
//                    String bxvoField = ErmBillFieldContrastCache.getSrcField(ErmBillFieldContrastCache.FieldContrast_SCENE_ExpenseAccount,
//                            BXConstans.BX_DJLXBM,
//                            attrNames[j]);
//                    if (bxvoField!=null) {
//                        expaccVO.setAttributeValue(attrNames[j], parentVO.getAttributeValue(bxvoField));
//                    }
//                }
                
                for (int j = 0; j < attrNames.length; j++) {
                    String bxvoField = ErmBillFieldContrastCache.getSrcField(ErmBillFieldContrastCache.FieldContrast_SCENE_ExpenseAccount,
                            BXConstans.BX_DJLXBM,
                            attrNames[j]);
                    if (bxvoField!=null) {
                        String[] tokens = StringUtil.split(bxvoField, ".");
                        Object value = null;
                        //��Щ���Ա����У���������ͷ������
                        if ("fb".equals(tokens[0])) {
                            value =  item.getAttributeValue(tokens[1]);
                        } else if (fbField.containsKey(bxvoField)) {
                            value =  item.getAttributeValue(bxvoField);
                        } else if ("zb".equals(tokens[0])) {
                            value =  parentVO.getAttributeValue(tokens[1]);
                        } else {
                            value =  parentVO.getAttributeValue(bxvoField);
                        }
                        
                        expaccVO.setAttributeValue(attrNames[j], value);
                    }
                }
                expaccVO.setPk_group(parentVO.getPk_group());
                expaccVO.setPk_org(parentVO.getFydwbm());//����������֯ȡ���óе���λ
                //����ڼ�����
                setExpAccperid(expaccVO, parentVO.getFydwbm(), parentVO.getDjrq());

                expaccVO.setSrc_billtype(parentVO.getParentBillType());
                expaccVO.setSrc_tradetype(parentVO.getDjlxbm());
                expaccVO.setBx_fiorg(parentVO.getPk_org());
                expaccVO.setBx_tradetype(parentVO.getDjlxbm());
                expaccVOList.add(expaccVO);
            }
            return expaccVOList.toArray(new ExpenseAccountVO[0]);
        }
    }
    
    /**
     * Ԥ�ᵥת������
     * @param vos
     * @return
     * @throws BusinessException
     */
    public static ExpenseAccountVO[] getExpAccVOS(final AggAccruedBillVO[] vos) throws BusinessException {
        List<ExpenseAccountVO> expaccVOList = new ArrayList<ExpenseAccountVO>();
        for (int i = 0; i < vos.length; i++) {
            ExpenseAccountVO[] expAccVOs = getExpAccVO(vos[i]);
            if(expAccVOs.length >0){
            	expaccVOList.addAll(Arrays.asList(expAccVOs));
            }
        }
        return expaccVOList.toArray(new ExpenseAccountVO[0]);
    }
    
	/**
	 * Ԥ�ᵥת������
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public static ExpenseAccountVO[] getExpAccVO(final AggAccruedBillVO vo) throws BusinessException {
		if ((vo == null) || (vo.getParentVO() == null)) {
			return new ExpenseAccountVO[] {};
		}

		List<ExpenseAccountVO> expaccVOList = new ArrayList<ExpenseAccountVO>();
		AccruedDetailVO[] childrenVO = vo.getChildrenVO();

		AccruedVO parentVO = (AccruedVO) vo.getParentVO().clone();
		String[] attrNames = new ExpenseAccountVO().getAttributeNames();
		if ((childrenVO != null) && (childrenVO.length >= 0)) {

			ExpenseAccountVO expaccVO;
			boolean isRedBack = false;
			for (int i = 0; i < childrenVO.length; i++) {
				AccruedDetailVO detailItem = childrenVO[i];
				if(detailItem.getSrctype() != null && detailItem.getSrctype().length() > 1){
					isRedBack = true;
				}

				expaccVO = new ExpenseAccountVO();
				for (int j = 0; j < attrNames.length; j++) {
					String fieldName = ErmBillFieldContrastCache.getSrcField(
							ErmBillFieldContrastCache.FieldContrast_SCENE_ExpenseAccount,
							ErmBillConst.AccruedBill_Billtype, attrNames[j]);
					if (fieldName != null) {
						String[] tokens = StringUtil.split(fieldName, ".");
						Object value = null;
						// ��Щ���Ա����У���������ͷ������
						if ("zb".equals(tokens[0])) {
							value = parentVO.getAttributeValue(tokens[1]);
						} else {
							value = detailItem.getAttributeValue(fieldName);
						}

						expaccVO.setAttributeValue(attrNames[j], value);
					}
				}
				
				if(isRedBack){
					expaccVO.setBillstatus(BXStatusConst.DJZT_Sign);
				}
				
				//������Ա��Ϣ
				expaccVO.setBx_dwbm(parentVO.getOperator_org());
				expaccVO.setBx_deptid(parentVO.getOperator_dept());
				expaccVO.setBx_jkbxr(parentVO.getOperator());
				expaccVO.setBx_group(parentVO.getPk_group());
				expaccVO.setBx_org(parentVO.getPk_org());
				
				expaccVO.setPk_group(parentVO.getPk_group());
				expaccVO.setPk_org(detailItem.getAssume_org());// ����������֯ȡ���óе���λ
				expaccVO.setAssume_org(detailItem.getAssume_org());
				// ����ڼ�����
				setExpAccperid(expaccVO, detailItem.getAssume_org(), parentVO.getBilldate());

				expaccVO.setSrc_billtype(parentVO.getPk_billtype());
				expaccVO.setSrc_tradetype(parentVO.getPk_tradetype());
				expaccVO.setBx_fiorg(parentVO.getPk_org());
				expaccVOList.add(expaccVO);
			}
		}

		return expaccVOList.toArray(new ExpenseAccountVO[0]);
	}
    
    /**
     * ���û���ڼ�
     * @param expaccVO
     * @param pk_org
     * @param date
     * @throws InvalidAccperiodExcetion
     */
	private static void setExpAccperid(ExpenseAccountVO expaccVO, String pk_org, UFDate date)
			throws InvalidAccperiodExcetion {
		AccperiodmonthVO  accperiod = ErAccperiodUtil.getAccperiodmonthByUFDate(pk_org, date);
		
		if(accperiod != null){
			expaccVO.setAccperiod(accperiod.getYearmth());
			String[] yearMonth = StringUtil.split(accperiod.getYearmth(), "-");
			expaccVO.setAccyear(yearMonth[0]);
			expaccVO.setAccmonth(yearMonth[1]);
		}
	}

    public static ExpenseAccountVO[] getExpAccVOS(final JKBXVO[] vos) throws BusinessException {
        List<ExpenseAccountVO> expaccVOList = new ArrayList<ExpenseAccountVO>();
        for (int i = 0; i < vos.length; i++) {
            ExpenseAccountVO[] expAccVOs = getExpAccVO(vos[i]);
            if(expAccVOs.length >0){
            	expaccVOList.addAll(Arrays.asList(expAccVOs));
            }
        }
        return expaccVOList.toArray(new ExpenseAccountVO[0]);
    }

    public static ExpenseAccountVO[] getExpAccVOS(final AggCostShareVO[] vos) throws BusinessException {
        List<ExpenseAccountVO> expaccVOList = new ArrayList<ExpenseAccountVO>();
        for (int i = 0; i < vos.length; i++) {
            expaccVOList.addAll(Arrays.asList(getExpAccVO(vos[i])));
        }
        return expaccVOList.toArray(new ExpenseAccountVO[0]);
    }

    public static ExpenseAccountVO[] getExpAccVOS(final AggExpamtinfoVO[] vos) throws BusinessException {
        List<ExpenseAccountVO> expaccVOList = new ArrayList<ExpenseAccountVO>();
        for (int i = 0; i < vos.length; i++) {
            expaccVOList.addAll(Arrays.asList(getExpAccVO(vos[i])));
        }
        return expaccVOList.toArray(new ExpenseAccountVO[0]);
    }
}
