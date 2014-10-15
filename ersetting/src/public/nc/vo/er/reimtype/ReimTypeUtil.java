package nc.vo.er.reimtype;

import nc.vo.arap.workflow.config.ConfigAgent;
import nc.vo.ep.bx.ReimRuleDefVO;

public class ReimTypeUtil {
	
	private ReimTypeUtil(){
		
	}

	public static ReimRuleDefVO getReimRuleDefvo(String djlxbm) {
		final ConfigAgent config = (ConfigAgent) ConfigAgent.getInstance();
		ReimRuleDefVO reimRuleDefVO = (ReimRuleDefVO) config.getCommonVO(ReimRuleDefVO.key,djlxbm);
		if (reimRuleDefVO == null) {
			//单个交易类型的读取不到则读全局的
			reimRuleDefVO = (ReimRuleDefVO) config.getCommonVO(ReimRuleDefVO.key, "");
			if(reimRuleDefVO==null){
				//全局的也没有配置，则返回空
				return null;
			}
			ReimRuleDefVO vo = (ReimRuleDefVO) reimRuleDefVO.clone();
			vo.setId(djlxbm);
			config.getCache().putCommonVO(ReimRuleDefVO.key, vo);
		}
		
		return reimRuleDefVO;
	}
}
