package nc.vo.arap.workflow.config.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.vo.arap.workflow.config.Configuration;
import nc.vo.arap.workflow.config.ConfigurationException;
import nc.vo.ep.bx.BusiTypeVO;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author twei
 *
 * nc.vo.arap.workflow.config.parser.BusiTypeXmlParser
 */
public class BusiTypeXmlParser implements XmlParser {
	
	public void parse(Element element, Configuration config) throws ParserException, ConfigurationException {
		if(element.getNodeName().equals("billtype")){
			loadBusitype( element, config);
		}
	}

	private void loadBusitype(Element element, Configuration config) throws ConfigurationException {
		
		BusiTypeVO busiTypeVO=new BusiTypeVO();
		
		String code=element.getAttribute("code");
		String rule=element.getAttribute("rule");
		String limit=element.getAttribute("limit");

		busiTypeVO.setId(code);
		busiTypeVO.setRule(rule);
		busiTypeVO.setLimit(limit);
		busiTypeVO.setCostentity_billitems(getCostentity_billitems(element));
		busiTypeVO.setPayentity_billitems(getPayentity_billitems(element));
		busiTypeVO.setPayorgentity_billitems(getPayorgentity_billitems(element));
		busiTypeVO.setUseentity_billitems(getUseentity_billitems(element));
		busiTypeVO.setInterfaces(getInterfaces(element));
		busiTypeVO.setPower_items(getPower_items(element));
		
		getTablecodes(busiTypeVO,element);
		
		config.buildCommonVO(BusiTypeVO.key, busiTypeVO);
	}




	private void getTablecodes(BusiTypeVO busiTypeVO,Element element) {
		
		NodeList actionNodes=element.getElementsByTagName("tablecodes");
		Map<String, Boolean> mapAddRow=new HashMap<String, Boolean>();
		Map<String, Boolean> mapTemplet=new HashMap<String, Boolean>();
		if (actionNodes.getLength() > 0) {
            Element actionElement = (Element) actionNodes.item(0);            
            NodeList actionList=actionElement.getElementsByTagName("tablecode");
            
            for (int i = 0; i < actionList.getLength(); i++) {
    			Node childNode=actionList.item(i);
    			if (childNode instanceof Element) {
    					Element child = (Element) childNode;
    					mapAddRow.put(child.getAttribute("code"), child.getAttribute("canAddRow").equalsIgnoreCase("Y"));
    					mapTemplet.put(child.getAttribute("code"), child.getAttribute("templetflag").equalsIgnoreCase("Y"));
    			}
    		}
		}
		busiTypeVO.setIsTableAddRow(mapAddRow);
		busiTypeVO.setIsTableTemplet(mapTemplet);
	}

	private List<String> getPower_items(Element element) {

		return getItems(element, "power_items");
	}

	private Map<String, String> getInterfaces(Element element) {
		NodeList actionNodes=element.getElementsByTagName("interfaces");
		Map<String, String> map=new HashMap<String, String>();
		
		if (actionNodes.getLength() > 0) {
            Element actionElement = (Element) actionNodes.item(0);            
            NodeList actionList=actionElement.getElementsByTagName("interface");
            
            for (int i = 0; i < actionList.getLength(); i++) {
    			Node childNode=actionList.item(i);
    			if (childNode instanceof Element) {
    					Element child = (Element) childNode;
    					map.put(child.getAttribute("name"),child.getAttribute("impl"));
    			}
    		}
		}
		return map;
	}

	private List<String> getCostentity_billitems(Element element) {

		return getItems(element, "costentity_billitems");
	}

	private List<String> getPayentity_billitems(Element element) {

		return getItems(element, "payentity_billitems");
	}
	private List<String> getPayorgentity_billitems(Element element) {

		return getItems(element, "payorgentity_billitems");
	}
	private List<String> getUseentity_billitems(Element element) {

		return getItems(element, "useentity_billitems");
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
		
		BusiTypeVO busiTypeVO=(BusiTypeVO) vo;
		Element root=doc.createElement("billtype");
		root.setAttribute("code", busiTypeVO.getId());
		
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
