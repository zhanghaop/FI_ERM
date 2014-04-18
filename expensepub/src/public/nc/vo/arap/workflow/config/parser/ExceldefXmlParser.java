package nc.vo.arap.workflow.config.parser;

import nc.bs.erm.plugin.ArapSheetBodyVO;
import nc.bs.erm.plugin.ArapSheetItemVO;
import nc.bs.erm.plugin.ArapSheetVO;
import nc.vo.arap.workflow.config.Configuration;
import nc.vo.arap.workflow.config.ConfigurationException;
import nc.vo.pub.lang.UFBoolean;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ExceldefXmlParser implements XmlParser {
	
	public Element assemble(Document doc, Object vo) {
		// TODO Auto-generated method stub
		return null;
	}

	public Element findElement(Element root, Element element) {
		// TODO Auto-generated method stub
		return null;
	}

	public void parse(Element element, Configuration config) throws ParserException, ConfigurationException {
		if(element.getNodeName().equals("billtype")){
			loadExcelDef( element, config);
		}
	}

	private void loadExcelDef(Element element, Configuration config) throws ConfigurationException {
		
		ArapSheetVO arapSheetVO=new ArapSheetVO();
		
		String name=element.getAttribute("name");
		arapSheetVO.setName(name);
		
		arapSheetVO.setBodys(loadSheetBodys(element));
		
		config.buildCommonVO(ArapSheetVO.key, arapSheetVO);
	}

	private ArapSheetBodyVO[] loadSheetBodys(Element element) {
		NodeList actionNodes=element.getElementsByTagName("sheet");
		ArapSheetBodyVO[] vos=new ArapSheetBodyVO[actionNodes.getLength()];
		
		
		if (actionNodes.getLength() > 0) {
			for (int i = 0; i < actionNodes.getLength(); i++) {
				Element actionElement = (Element) actionNodes.item(i);      
				NodeList actionList=actionElement.getElementsByTagName("item");
				ArapSheetBodyVO vo=new ArapSheetBodyVO();
				
				vo.setName(actionElement.getAttribute("name"));
				
				ArapSheetItemVO[] items= new ArapSheetItemVO[actionList.getLength()];
				for (int j = 0; j < actionList.getLength(); j++) {
	    			Node childNode=actionList.item(j);
	    			if (childNode instanceof Element) {
	    				Element child = (Element) childNode;
	    				items[j]=new ArapSheetItemVO();
	    				items[j].setItemkey(child.getAttribute("itemkey"));
	    				items[j].setName(child.getAttribute("name"));
	    				items[j].setRefType(child.getAttribute("reftype"));
	    				items[j].setDownRef(UFBoolean.valueOf(child.getAttribute("downRef").equalsIgnoreCase("Y")));
	    			}
	    		}
				
				vo.setItems(items);
				
				vos[i]=vo;
				
			}

		}
		return vos;
	}

	public void parseParent(Element element, Configuration config, String oldnamespace) throws ParserException, ConfigurationException {
		// TODO Auto-generated method stub

	}

}
