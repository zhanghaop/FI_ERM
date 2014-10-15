package nc.vo.arap.workflow.config;

import nc.vo.arap.workflow.config.parser.BilltypeRuleXmlParser;
import nc.vo.arap.workflow.config.parser.XmlParser;
import nc.vo.ep.bx.BilltypeRuleVO;

/**
 * @author twei
 *
 * 费用产品中单据类型过滤默认的配置Provider
 */
public class BilltypeRuleXmlConfigurationProvider extends AbstractXmlConfigurationProvider {

	public String[] getFileNames() {
		return new String[]{"billtyperule.xml"};
	}

	public XmlParser[] getParsers() {
		return new XmlParser[]{new BilltypeRuleXmlParser()};
	}

	public String getDefault_package() {
		return "";
	}

	public String getDir() {
		return "jkbxconfig";
	}

	public String getSign() {
		return BilltypeRuleVO.key;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getConfigVoClass() {
		return BilltypeRuleVO.class;
	}
}
