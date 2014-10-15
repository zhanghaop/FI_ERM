package nc.vo.arap.workflow.config.parser;

import java.util.ArrayList;
import java.util.List;

import nc.vo.arap.workflow.config.Configuration;
import nc.vo.arap.workflow.config.ConfigurationException;
import nc.vo.ep.bx.ReimRuleDef;
import nc.vo.ep.bx.ReimRuleDefVO;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author twei
 *
 * nc.vo.arap.workflow.config.parser.BusiTypeXmlParser
 */
public class ReimRuleXmlParser implements XmlParser {
	
	public void parse(Element element, Configuration config) throws ParserException, ConfigurationException {
		if(element.getNodeName().equals("billtype")){
			loadBusitype( element, config);
		}
	}

	private void loadBusitype(Element element, Configuration config) throws ConfigurationException {
		
		ReimRuleDefVO reimRuleDefVO=new ReimRuleDefVO();
		
		String code=element.getAttribute("code");

		reimRuleDefVO.setId(code);
		reimRuleDefVO.setReimRuleDefList(getItems(element));
		
		
		config.buildCommonVO(ReimRuleDefVO.key, reimRuleDefVO);
	}

	private List<ReimRuleDef> getItems(Element element) {
		NodeList actionNodes=element.getElementsByTagName("items");
		List<ReimRuleDef> list=new ArrayList<ReimRuleDef>();
		if (actionNodes.getLength() > 0) {
            Element actionElement = (Element) actionNodes.item(0);            
            NodeList actionList=actionElement.getElementsByTagName("billitem");
            
            for (int i = 0; i < actionList.getLength(); i++) {
    			Node childNode=actionList.item(i);
    			if (childNode instanceof Element) {
    					Element child = (Element) childNode;
    					String itemkey = child.getAttribute("itemkey");
    					String itemvalue = child.getAttribute("itemvalue");
    					String reftype = child.getAttribute("reftype");
    					String datatype = child.getAttribute("datatype");
    					String showname = child.getAttribute("showname");
    					ReimRuleDef vo=new ReimRuleDef();
    					vo.setItemkey(itemkey);
    					vo.setItemvalue(itemvalue);
    					vo.setReftype(reftype);
    					vo.setDatatype(datatype);
    					vo.setShowname(showname);
						list.add(vo);
    			}
    		}
		}
		return list;
	}


	public Element assemble(Document doc,Object vo) {
		return null;
	}

	public Element findElement(Element root, Element element) {
		return null;
	}

	public void parseParent(Element element, Configuration config, String oldnamespace) throws ParserException, ConfigurationException {
	}

}
