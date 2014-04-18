package nc.vo.arap.workflow.config;

import nc.bs.erm.plugin.ArapSheetVO;
import nc.vo.arap.workflow.config.parser.ExceldefXmlParser;
import nc.vo.arap.workflow.config.parser.XmlParser;

/**
 * @author twei
 *
 * nc.vo.arap.workflow.config.ExcelDefaultXmlConfigurationProvider
 * 
 * excel sheet def ≈‰÷√Provider
 */
public class ExcelDefaultXmlConfigurationProvider extends AbstractXmlConfigurationProvider {

	public String[] getFileNames() {
		return new String[]{"sheetdef.xml"};
	}

	public XmlParser[] getParsers() {
		return new XmlParser[]{new ExceldefXmlParser()};
	}

	public String getDefault_package() {
		return "";
	}

	public String getDir() {
		return "excelconfig";
	}

	public String getSign() {
		return ArapSheetVO.key;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getConfigVoClass() {
		return ArapSheetVO.class;
	}

}
