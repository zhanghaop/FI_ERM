package nc.ui.erm.sharerule.validator;

import nc.bs.erm.sharerule.ShareruleConst;
import nc.bs.erm.sharerule.SharerulePubUtil;
import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.erm.sharerule.ShareruleVO;
import nc.vo.pub.lang.UFDouble;

public class RatioValidator implements Validator {

    private static final long serialVersionUID = -4357063994975188182L;

    @Override
    public ValidationFailure validate(Object obj) {
        if (!(obj instanceof AggshareruleVO))
            return null;
        ValidationFailure failure = null;
        AggshareruleVO aggshareruleVO = (AggshareruleVO)obj;

        ShareruleVO parentvo = (ShareruleVO) aggshareruleVO.getParentVO();
        if (parentvo.getRule_type().equals(ShareruleConst.SRuletype_Ratio)) {
            UFDouble totalRatio = SharerulePubUtil.getTotalRatio(aggshareruleVO);
            if ((totalRatio.compareTo(new UFDouble(100)) != 0)) {
                failure = new ValidationFailure(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0021")/*@res "表体分摊比例合计不为100%"*/);
            }
        }
        return failure;
    }

}