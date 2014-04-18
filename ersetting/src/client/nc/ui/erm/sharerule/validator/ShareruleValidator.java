package nc.ui.erm.sharerule.validator;

import java.util.HashMap;
import java.util.Map;

import nc.bs.erm.sharerule.ShareruleConst;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IAttribute;
import nc.md.model.IBean;
import nc.md.model.MetaDataException;
import nc.ui.pub.bill.BillModel;
import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.erm.sharerule.ShareruleDataVO;
import nc.vo.erm.sharerule.ShareruleObjVO;
import nc.vo.erm.sharerule.ShareruleVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.lang.UFDouble;

public class ShareruleValidator implements Validator {

    private static final long serialVersionUID = -6633281373009116708L;

    public static final String MD_ID_SHARERULE = "cf21a746-807a-4cf8-911f-f397529ee06e";

    @Override
    public ValidationFailure validate(Object obj) {
        if (!(obj instanceof AggshareruleVO))
            return null;
        ValidationFailure failure = null;
        AggshareruleVO aggshareruleVO = (AggshareruleVO)obj;
        ShareruleVO shareruleVO = (ShareruleVO)aggshareruleVO.getParentVO();

        //��̯��ʽ
        int ruleType = shareruleVO.getRule_type();
        //��̯����
//        String[] shareObjNameArray = parseShareObjName(aggshareruleVO);
//        String[] shareObjCodeArray = parseShareObjCode(aggshareruleVO, shareObjNameArray);

        String[] shareObjCodeArray = parseShareObjCode(aggshareruleVO);
        String[] shareObjNameArray = parseShareObjNameByCode(aggshareruleVO, shareObjCodeArray);
        
        //��¼����Ϣ
        Map<String, String> rowInfoMap = new HashMap<String, String>();
        //ѭ��У��ÿһ��
        ShareruleDataVO[] shareDataArray =
            (ShareruleDataVO[])aggshareruleVO.getTableVO("rule_data");
        for (ShareruleDataVO shareruleData : shareDataArray) {
            if (shareruleData.getStatus() == BillModel.DELETE)
                continue;
            failure = singleRowValidate(shareObjCodeArray, shareObjNameArray,
                    shareruleData, ruleType, rowInfoMap);
            if (failure != null)
                break;
        }
        return failure;
    }
    
    private String[] parseShareObjCode(AggshareruleVO aggshareruleVO) {
        ShareruleVO shareruleVO = (ShareruleVO)aggshareruleVO.getParentVO();
        String[] codeArray = null;
        if (!StringUtil.isEmpty(shareruleVO.getRuleobj_name())) {
            codeArray = shareruleVO.getRuleobj_name().split(",");
        }
        else {
            ShareruleObjVO[] objVO =
                (ShareruleObjVO[])aggshareruleVO.getTableVO(aggshareruleVO.getTableCodes()[0]);
            if (objVO != null) {
                codeArray = new String[objVO.length];
                for (int nPos = 0; nPos < objVO.length; nPos++) {
                    codeArray[nPos] = objVO[nPos].getFieldcode();
                }
            }
        }

        return codeArray;
    }

    private String[] parseShareObjNameByCode(AggshareruleVO aggshareruleVO,
            String[] shareObjCodeArray) {
        String[] shareObjNameArray = null;

        ShareruleObjVO[] objVO =
            (ShareruleObjVO[])aggshareruleVO.getTableVO(aggshareruleVO.getTableCodes()[0]);
        if (shareObjCodeArray == null) {
            if (objVO != null) {
                shareObjNameArray = new String[objVO.length];
                for (int nPos = 0; nPos < objVO.length; nPos++) {
                    shareObjNameArray[nPos] = objVO[nPos].getFieldcode();
                }
            }
        }
        
        if (shareObjCodeArray != null) {
            try {
                //ͨ��ObjCode������Ӧ��ObjName
                shareObjNameArray = new String[shareObjCodeArray.length];
                IBean bean = MDBaseQueryFacade.getInstance().getBeanByID(MD_ID_SHARERULE);
                IAttribute att;
                for (int i = 0; i < shareObjCodeArray.length; i++) {
                    att = bean.getAttributeByName(shareObjCodeArray[i]);
                    if (att != null) {
                        shareObjNameArray[i] = att.getDisplayName();
                    } else if (objVO != null) {
                        shareObjNameArray[i] = objVO[i].getFieldname();
                    }
                }
            } catch (MetaDataException e) {
                Logger.error(e.getMessage(), e);
            }
        }

        return shareObjNameArray;
    }

    private ValidationFailure singleRowValidate(String[] shareObjCodeArray,
            String[] shareObjNameArray, ShareruleDataVO shareruleData,
            int ruleType, Map<String, String> rowInfoMap) {

        //��̯��ʽУ��
        ValidationFailure failureShareruleType =
            shareRuleTypeValidate(ruleType, shareruleData);
        //��̯����У��
        ValidationFailure failureObj = shareRuleObjValidate(shareObjCodeArray,
                shareObjNameArray, shareruleData, rowInfoMap);

        return failureObj != null ? failureObj : failureShareruleType;
    }

    private static final UFDouble ONE_HUNDRED = new UFDouble(100);

    private static final String ERROR_EMPTY = "empty";

    private ValidationFailure shareRuleObjValidate(String[] shareObjCodeArray,
            String[] shareObjNameArray, ShareruleDataVO shareruleData,
            Map<String, String> rowInfoMap) {
        
        String sSplit = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0040");/*@res "��"*/
        StringBuilder rowInfoSb = new StringBuilder();
        //У��
        StringBuilder errorMsgSb = new StringBuilder();
        boolean emptyShareRuleObject = true;
        for (int nPos = 0; nPos < shareObjCodeArray.length; nPos++) {
            Object obj = shareruleData.getAttributeValue(shareObjCodeArray[nPos]);
            if (obj == null) {
                if (errorMsgSb.length() > 0) {
                    errorMsgSb.append(sSplit);
                }
                errorMsgSb.append(shareObjNameArray[nPos]);
            }
            else {
                rowInfoSb.append(obj.toString());
                emptyShareRuleObject = false;
            }
        }
        ValidationFailure failure = null;
        if (errorMsgSb.length() > 0) {
            String sMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201100_0",null,"0201100-0041",null,new String[]{ errorMsgSb.toString() });
            failure = new ValidationFailure(sMsg);
        }
        if (emptyShareRuleObject)
            failure.setErrorcode(ERROR_EMPTY);

        //��¼����Ϣ
        if (failure == null && rowInfoSb.length() > 0) {
            failure = sameRowvalidate(rowInfoMap, rowInfoSb.toString());
            if (shareruleData.getPk_cshare_detail() == null) {
                rowInfoMap.put(Long.toBinaryString(System.currentTimeMillis()), rowInfoSb.toString());
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    Logger.error(e.getMessage(), e);
                }
            } else {
                rowInfoMap.put(shareruleData.getPk_cshare_detail(), rowInfoSb.toString());
            }
        }
        return failure;
    }

    private ValidationFailure shareRuleTypeValidate(int ruleType,
            ShareruleDataVO shareruleData) {
        ValidationFailure failure = null;
        if (ruleType == ShareruleConst.SRuletype_Ratio) {
            if (shareruleData.getShare_ratio() == null) {
                failure = new ValidationFailure(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0023")/*@res "��̯��������Ϊ�գ�"*/);
                failure.setErrorcode(ERROR_EMPTY);
            }
            else if (UFDouble.ZERO_DBL.compareTo(shareruleData.getShare_ratio()) >= 0 ||
                    ONE_HUNDRED.compareTo(shareruleData.getShare_ratio()) < 0)
                failure = new ValidationFailure(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0024")/*@res "��̯�����������0��С�ڵ���100��"*/);
        }
        if (ruleType == ShareruleConst.SRuletype_Money) {
            if (shareruleData.getAssume_amount() == null) {
                failure = new ValidationFailure(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0025")/*@res "�е�����Ϊ�գ�"*/);
                failure.setErrorcode(ERROR_EMPTY);
            } else if (shareruleData.getAssume_amount().compareTo(UFDouble.ZERO_DBL) <= 0)
                failure = new ValidationFailure(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0026")/*@res "�е����������0��"*/);
        }
        return failure;
    }

    /**
     * �ظ���У��
     * @return
     */
    private ValidationFailure sameRowvalidate(Map<String, String> rowInfoMap,
            String rowInfo) {
        ValidationFailure failure = null;
        if (rowInfoMap.containsValue(rowInfo))
            failure = new ValidationFailure(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0027")/*@res "���ܴ����ظ��У����޸ģ�"*/);
        return failure;
    }

}