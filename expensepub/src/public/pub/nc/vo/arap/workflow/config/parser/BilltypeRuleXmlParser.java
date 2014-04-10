package nc.vo.arap.workflow.config.parser;

import java.util.ArrayList;
import java.util.List;

import nc.vo.arap.workflow.config.Configuration;
import nc.vo.arap.workflow.config.ConfigurationException;
import nc.vo.ep.bx.BilltypeRuleVO;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author twei
 *
 * BilltypeRule½âÎö
 */
public class BilltypeRuleXmlParser implements XmlParser {
	
	public void parse(Element element, Configuration config) throws ParserException, ConfigurationException {
		if(element.getNodeName().equals("billtype")){
			loadBusitype( element, config);
		}
	}

	private void loadBusitype(Element element, Configuration config) throws ConfigurationException {
		
		BilltypeRuleVO rulevo=new BilltypeRuleVO();
		
		String code=element.getAttribute("code");

		rulevo.setId(code);
		rulevo.setParentitems(getParentItems(element));
		rulevo.setExcludeitems(getExcludeItems(element));
		rulevo.setItems(getCtrlItems(element));
		
		config.buildCommonVO(BilltypeRuleVO.key, rulevo);
	}


	private List<String> getParentItems(Element element) {

		return getItems(element, "parentitems");
	}
	private List<String> getExcludeItems(Element element) {
		
		return getItems(element, "excludeitems");
	}
	private List<String> getCtrlItems(Element element) {
		
		return getItems(element, "items");
	}
	
	private List<String> getItems(Element element, String head) {
		NodeList actionNodes=element.getElementsByTagName(head);
		List<String> list=new ArrayList<String>();
		if (actionNodes.getLength() > 0) {
            Element actionElement = (Element) actionNodes.item(0);            
            NodeList actionList=actionElement.getElementsByTagName("billitem");
            
            for (int i = 0; i < actionList.getLength(); i++) {
    			Node childNode=actionList.item(i);
    			if (childNode instanceof Element) {
    					Element child = (Element) childNode;
    					list.add(child.getAttribute("itemkey"));
    			}
    		}
		}
		return list;
	}


	public Element assemble(Document doc,Object vo) {
		
		BilltypeRuleVO rulevo=(BilltypeRuleVO) vo;
		Element root=doc.createElement("billtype");
		root.setAttribute("code", rulevo.getId());
		
		return root;
	}

	public Element findElement(Element root, Element element) {
		NodeList nodes = root.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node childNode = nodes.item(i);

			if (childNode instanceof Element) {
				Element child = (Element) childNode;

				if(child.getAttribute("code").equals(element.getAttribute("code")))
				{
					return child;
				}
			}
		}
		
		return null;
	}


	public void parseParent(Element element, Configuration config, String oldnamespace) throws ParserException, ConfigurationException {
		// TODO Auto-generated method stub

	}

}
