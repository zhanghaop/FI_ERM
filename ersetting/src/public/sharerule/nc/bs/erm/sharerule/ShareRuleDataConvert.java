package nc.bs.erm.sharerule;

import java.math.BigDecimal;

import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IAttribute;
import nc.md.model.IBean;
import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.erm.sharerule.ShareConvRuleVO;
import nc.vo.erm.sharerule.ShareruleDataVO;
import nc.vo.erm.sharerule.ShareruleObjVO;
import nc.vo.erm.sharerule.ShareruleVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;
/**
 * 分摊规则转换
 * @author luolch
 *
 */
public class ShareRuleDataConvert {

    /**
     * 分摊规则转换
     * @param scvo 转换数据vo
     * @return
     * @throws Exception
     */
    public static SuperVO[] getDataConvertVOS(final ShareConvRuleVO scvo) throws Exception{
        ShareruleVO sr= (ShareruleVO) scvo.getAggSRule().getParentVO();
        ShareruleObjVO[] so = (ShareruleObjVO[]) scvo.getAggSRule().getTableVO(scvo.getAggSRule().getTableCodes()[0]);
        ShareruleDataVO[] sd = (ShareruleDataVO[]) scvo.getAggSRule().getTableVO(scvo.getAggSRule().getTableCodes()[1]);
        SuperVO[] superVOArr = new SuperVO[sd.length];
        //平均分摊金额
        UFDouble divMoney = scvo.getShareMoney().div(sd.length);
        int nPower = scvo.getShareMoney().getPower();
        divMoney = divMoney.setScale(nPower, BigDecimal.ROUND_HALF_UP);

        //总金额
        UFDouble sumMoney =scvo.getShareMoney();

        // 字段对照查询对象
        FieldcontrastVO qryVO = new FieldcontrastVO();
        qryVO.setApp_scene(ErmBillFieldContrastCache.FieldContrast_SCENE_SHARERULEField);
        qryVO.setSrc_billtype(scvo.getBillType());
        qryVO.setSrc_busitype(scvo.getBusitype());
        qryVO.setPk_group(scvo.getPk_group());
        qryVO.setPk_org(scvo.getPk_org());

        for (int nsdPos = 0; nsdPos < sd.length; nsdPos++) {
            superVOArr[nsdPos] = (SuperVO) scvo.getSharevo().clone();
            for (int nsoPos = 0; nsoPos < so.length; nsoPos++) {
                //获得分摊对象对照字段
                String objectField = getSrcFieldCode(qryVO, so[nsoPos].getFieldcode());
                //根据分摊对象设置值
                superVOArr[nsdPos].setAttributeValue(objectField,
                        sd[nsdPos].getAttributeValue(so[nsoPos].getFieldcode()));
            }
            //获得金额对照字段
            qryVO.setDes_fieldcode("rule_data." + ShareruleDataVO.ASSUME_AMOUNT);
            String moneyField = ErmBillFieldContrastCache.getSrcField(qryVO);
            if (moneyField == null) {
                throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0032")/*@res "维度对照表中缺少初始化配置：分摊数据.承担金额！"*/);
            }
            moneyField = moneyField.split("\\.")[1];
            //设置业务行需要分摊金额
            UFDouble shareMoney = UFDouble.ZERO_DBL;
            //平均分摊
            if (ShareruleConst.SRuletype_Average == sr.getRule_type()) {
                shareMoney = divMoney;
            }//按金额分摊
            else if (ShareruleConst.SRuletype_Money == sr.getRule_type()) {
                shareMoney=sd[nsdPos].getAssume_amount();
            }//按比例分摊
            else if (ShareruleConst.SRuletype_Ratio  ==sr.getRule_type()) {
                shareMoney= scvo.getShareMoney().multiply(sd[nsdPos].getShare_ratio().div(100));
            }
            if (ShareruleConst.SRuletype_Money  !=sr.getRule_type()) {
                //从总金额减掉
                UFDouble tmpSumMoney = sumMoney;
                sumMoney = sumMoney.sub(shareMoney);
                //效验金额，不够补齐
                
                if (sumMoney.compareTo(UFDouble.ZERO_DBL) <= 0) {
                    sumMoney = UFDouble.ZERO_DBL;
                    shareMoney = tmpSumMoney;
                } 
                
                //如果是最后一个并且金额加上
                if ((nsdPos == sd.length - 1)
                        && (UFDouble.ZERO_DBL.compareTo(sumMoney) != 0)) {
                    shareMoney = shareMoney.add(sumMoney);
                }
            }
            superVOArr[nsdPos].setAttributeValue(moneyField, shareMoney);
        }
        return superVOArr;
    }
    
    public static String getSrcFieldCode(FieldcontrastVO qryVO, String shareObjFieldCode) throws BusinessException {
        qryVO.setDes_fieldcode("rule_data." + shareObjFieldCode);
        String objectField = ErmBillFieldContrastCache.getSrcField(qryVO);
        if (objectField == null) {
            IBean bean = MDBaseQueryFacade.getInstance().getBeanByID("cf21a746-807a-4cf8-911f-f397529ee06e");//元数据：费用分摊规则数据
            IAttribute att = bean.getAttributeByName(qryVO.getDes_fieldcode());
            String fieldName = null;
            if (att == null) {
                if ((qryVO.getDes_fieldcode() != null) && (qryVO.getDes_fieldcode().indexOf(".") > 0)) {
                    fieldName = qryVO.getDes_fieldcode().split("\\.")[1];
                    att = bean.getAttributeByName(fieldName);
                    fieldName = att.getDisplayName();
                }
            } else {
                fieldName = att.getDisplayName();
            }

            throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201100_0",null,"0201100-0030",null,new String[]{bean.getDisplayName() + "." + fieldName }));
            /*@res "请在维度对照中配置字段{0}的对照关系"*/
        }

        objectField = objectField.split("\\.")[1];
        return objectField; 
    }

}