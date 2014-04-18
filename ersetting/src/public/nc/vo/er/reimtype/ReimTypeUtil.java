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
			//�����������͵Ķ�ȡ�������ȫ�ֵ�
			reimRuleDefVO = (ReimRuleDefVO) config.getCommonVO(ReimRuleDefVO.key, "");
			if(reimRuleDefVO==null){
				//ȫ�ֵ�Ҳû�����ã��򷵻ؿ�
				return null;
			}
			ReimRuleDefVO vo = (ReimRuleDefVO) reimRuleDefVO.clone();
			vo.setId(djlxbm);
			config.getCache().putCommonVO(ReimRuleDefVO.key, vo);
		}
		
		return reimRuleDefVO;
	}
}
