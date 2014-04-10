package nc.vo.arap.workflow.config;

import nc.vo.arap.workflow.config.parser.BusiTypeXmlParser;
import nc.vo.arap.workflow.config.parser.XmlParser;
import nc.vo.ep.bx.BusiTypeVO;

/**
 * @author twei
 *
 * nc.vo.arap.workflow.config.BXDefaultXmlConfigurationProvider
 * 
 * 借款报销业务参数默认的配置Provider
 */
public class BXDefaultXmlConfigurationProvider extends AbstractXmlConfigurationProvider {

	public String[] getFileNames() {
		return new String[]{"busitype.xml"};
	}

	public XmlParser[] getParsers() {
		return new XmlParser[]{new BusiTypeXmlParser()};
	}

	public String getDefault_package() {
		return "";
	}

	public String getDir() {
		return "jkbxconfig";
	}

	public String getSign() {
		return BusiTypeVO.key;
	}

	@Override
	public Class getConfigVoClass() {
		return BusiTypeVO.class;
	}
}
