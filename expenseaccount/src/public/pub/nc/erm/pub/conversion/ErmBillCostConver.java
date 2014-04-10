package nc.erm.pub.conversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.expamortize.ExpAmoritizeConst;
import nc.pubitf.accperiod.AccountCalendar;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * 业务VO转换为费用帐转换器<br>
 * 注意点:
 * <li>主组织取费用承担单位
 * @author luolch
 *
 */
public class ErmBillCostConver {

    public static ExpenseAccountVO[] getExpAccVO(final AggCostShareVO vo) throws BusinessException {
        CostShareVO parentVO = (CostShareVO) vo.getParentVO();
        CircularlyAccessibleValueObject[] childsVOS = vo.getChildrenVO();
        List<ExpenseAccountVO> expList = new ArrayList<ExpenseAccountVO>();
        if ((childsVOS!=null) && (childsVOS.length!=0)) {
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

                exvos[i].setAssume_org((String) childsVOS[i]
                                                          .getAttributeValue(CShareDetailVO.ASSUME_ORG));
                exvos[i].setPk_org((String) childsVOS[i]
                                                      .getAttributeValue(CShareDetailVO.ASSUME_ORG));
                AccountCalendar newAC = AccountCalendar.getInstance();
                newAC.setDate(parentVO.getBilldate());
                exvos[i].setAccperiod(newAC.getMonthVO().getYearmth());
                String[] yearMonth = StringUtil.split(newAC.getMonthVO().getYearmth(), "-");
                exvos[i].setAccyear(yearMonth[0]);
                exvos[i].setAccmonth(yearMonth[1]);
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
                AccountCalendar newAC = AccountCalendar.getInstance();
                newAC.setDate(parentVO.getAmortize_date());
                exvos[i].setAccperiod(newAC.getMonthVO().getYearmth());
                String[] yearMonth = StringUtil.split(newAC.getMonthVO().getYearmth(), "-");
                exvos[i].setAccyear(yearMonth[0]);
                exvos[i].setSrc_billno(parentVO.getBx_billno());
                exvos[i].setAccmonth(yearMonth[1]);
                exvos[i].setSrc_billtype(ExpAmoritizeConst.Expamoritize_BILLTYPE);
                exvos[i].setSrc_tradetype(ExpAmoritizeConst.Expamoritize_BILLTYPE);
                exvos[i].setBillstatus(BXStatusConst.DJZT_Sign);
                expList.add(exvos[i]);
            }
        }
        return expList.toArray(new ExpenseAccountVO[0]);
    }

    public static ExpenseAccountVO[] getExpAccVO(final JKBXVO vo) throws BusinessException {
        if ((vo == null) || (vo.getParentVO() == null)) {
            return new ExpenseAccountVO[]{};
        }
        List<ExpenseAccountVO> expaccVOList = new ArrayList<ExpenseAccountVO>();
        BXBusItemVO[] childrenVO = vo.getChildrenVO();
        JKBXHeaderVO parentVO = (JKBXHeaderVO) vo.getParentVO().clone();
        String[] attrNames = new ExpenseAccountVO().getAttributeNames();
        if ((childrenVO == null) || (childrenVO.length == 0)) {
            //转换为费用
            ExpenseAccountVO expaccVO  = new ExpenseAccountVO();
            for (int i = 0; i < attrNames.length; i++) {
                String bxvoField = ErmBillFieldContrastCache.getSrcField(
                        ErmBillFieldContrastCache.FieldContrast_SCENE_ExpenseAccount, BXConstans.BX_DJLXBM,
                        attrNames[i]);
                if (bxvoField!=null) {
                    expaccVO.setAttributeValue(attrNames[i], parentVO.getAttributeValue(bxvoField));
                }
            }

            expaccVO.setPk_group(parentVO.getPk_group());
            expaccVO.setPk_org(parentVO.getPk_org());
            AccountCalendar newAC = AccountCalendar.getInstance();
            newAC.setDate(parentVO.getDjrq());
            expaccVO.setAccperiod(newAC.getMonthVO().getYearmth());
            String[] yearMonth = StringUtil.split(newAC.getMonthVO().getYearmth(), "-");
            expaccVO.setAccyear(yearMonth[0]);
            expaccVO.setAccmonth(yearMonth[1]);
            expaccVO.setSrc_billtype(parentVO.getParentBillType());
            expaccVO.setSrc_tradetype(parentVO.getDjlxbm());
            return new ExpenseAccountVO[]{expaccVO};
        } else {
            ExpenseAccountVO expaccVO;
            for (int i = 0; i < childrenVO.length; i++) {
                BXBusItemVO item = childrenVO[i];
                String[] attributeNames = item.getAttributeNames();
                for (String attr : attributeNames) {
                    //这些属性表体有，但仅按表头属性走
                    if (attr.equals(JKBXHeaderVO.CASHPROJ) || attr.equals(JKBXHeaderVO.JKBXR)
                            || attr.equals(JKBXHeaderVO.CASHITEM)) {
                        continue;
                    }
                    parentVO.setAttributeValue(attr, item.getAttributeValue(attr));
                }

                //事项审批 关联表 arap_item_clb
                parentVO.setPk_jkbx(parentVO.getPk_jkbx());
                expaccVO  = new ExpenseAccountVO();
                for (int j = 0; j < attrNames.length; j++) {
                    String bxvoField = ErmBillFieldContrastCache.getSrcField(ErmBillFieldContrastCache.FieldContrast_SCENE_ExpenseAccount,
                            BXConstans.BX_DJLXBM,
                            attrNames[j]);
                    if (bxvoField!=null) {
                        expaccVO.setAttributeValue(attrNames[j], parentVO.getAttributeValue(bxvoField));
                    }
                }

                expaccVO.setPk_group(parentVO.getPk_group());
                expaccVO.setPk_org(parentVO.getFydwbm());//费用帐主组织取费用承担单位
                AccountCalendar newAC = AccountCalendar.getInstance();
                newAC.setDate(parentVO.getDjrq());
                expaccVO.setAccperiod(newAC.getMonthVO().getYearmth());
                String[] yearMonth = StringUtil.split(newAC.getMonthVO().getYearmth(), "-");
                expaccVO.setAccyear(yearMonth[0]);
                expaccVO.setAccmonth(yearMonth[1]);
                expaccVO.setSrc_billtype(parentVO.getParentBillType());
                expaccVO.setSrc_tradetype(parentVO.getDjlxbm());
                expaccVO.setBx_fiorg(parentVO.getPk_org());
                expaccVOList.add(expaccVO);
            }
            return expaccVOList.toArray(new ExpenseAccountVO[0]);
        }
    }

    public static ExpenseAccountVO[] getExpAccVOS(final JKBXVO[] vos) throws BusinessException {
        List<ExpenseAccountVO> expaccVOList = new ArrayList<ExpenseAccountVO>();
        for (int i = 0; i < vos.length; i++) {
            expaccVOList.addAll(Arrays.asList(getExpAccVO(vos[i])));
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
