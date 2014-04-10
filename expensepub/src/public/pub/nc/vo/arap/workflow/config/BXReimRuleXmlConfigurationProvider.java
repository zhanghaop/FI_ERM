package nc.vo.arap.workflow.config;

import nc.vo.arap.workflow.config.parser.ReimRuleXmlParser;
import nc.vo.arap.workflow.config.parser.XmlParser;
import nc.vo.ep.bx.ReimRuleDefVO;

/**
 * @author twei
 *
 * nc.vo.arap.workflow.config.BXDefaultXmlConfigurationProvider
 * 
 * 借款报销业务参数默认的配置Provider
 */
public class BXReimRuleXmlConfigurationProvider extends AbstractXmlConfigurationProvider {

	public String[] getFileNames() {
		return new String[]{"reimrule.xml"};
	}

	public XmlParser[] getParsers() {
		return new XmlParser[]{new ReimRuleXmlParser()};
	}

	public String getDefault_package() {
		return "";
	}

	public String getDir() {
		return "jkbxconfig";
	}

	public String getSign() {
		return ReimRuleDefVO.key;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getConfigVoClass() {
		return ReimRuleDefVO.class;
	}
}
