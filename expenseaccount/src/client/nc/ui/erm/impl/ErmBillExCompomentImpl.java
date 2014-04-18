package nc.ui.erm.impl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;

import nc.bs.framework.codesync.client.NCClassLoader;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.cmp.pub.IInitDataUsingDataManager;
import nc.itf.cmp.pub.ITabExComponent;
import nc.ui.uif2.IFunNodeClosingListener;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.factory.UIF2BeanFactory;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.cmp.settlement.NodeType;
import nc.vo.uif2.LoginContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ErmBillExCompomentImpl implements ITabExComponent , IInitDataUsingDataManager{

	private BeanFactory factory;

	public ErmBillExCompomentImpl() {
		try {
			Resource resource = new ClassPathResource("nc/ui/erm/billmanage/billmanage_config.xml", NCClassLoader.getNCClassLoader());
			factory = new DefaultListableBeanFactory();
			XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader((BeanDefinitionRegistry) factory);
			reader.loadBeanDefinitions(resource);
		} catch (Exception e) {
			factory = new UIF2BeanFactory();
			((UIF2BeanFactory)factory).loadBeanConfig("nc/ui/erm/billmanage/billmanage_config.xml");
		}
	}


	public List<Action> getActions() {
		List<Action> actions = new ArrayList<Action>();;
		actions.add((Action)factory.getBean("cardRefreshAction"));
		return actions;
	}


	public IFunNodeClosingListener getCloseListener() {

		return null;
	}

	public JComponent getExComponent() {
		BillForm bean = (BillForm)factory.getBean("editor");
		return bean;
	}

	@Override
	public void setLoginContext(LoginContext context) {
		LoginContext ownContext = (LoginContext)factory.getBean("context");
		ownContext.setNodeCode(BXConstans.BXMNG_NODECODE);
		ownContext.setPk_loginUser(WorkbenchEnvironment.getInstance().getLoginUser().getPrimaryKey());
		ownContext.setEntranceUI(context.getEntranceUI());
		ownContext.setFuncInfo(context.getFuncInfo()); 
		if(WorkbenchEnvironment.getInstance().getGroupVO()!=null)
		{
			ownContext.setPk_group(WorkbenchEnvironment.getInstance().getGroupVO().getPk_group());
		}
	}


	@Override
	public void setNodeType(NodeType nodeType) {

	}

	public List<Action> getActions(String[] actionnames) {
		return getActions();
	}

	@Override
	public List<Action> getActions(String nodetype) {
		return getActions();
	}


	@Override
	public List<Action> getEditActions(String nodetype) {
		return getActions();
	}


	@Override
	public void initDataUsingDataManager() {
		try {
			// 避免系统没有mrg而产生的异常
			IAppModelDataManager m = (IAppModelDataManager) factory.getBean(MODELDATAMANAGER);
			m.initModel();
		} catch (BeansException be) {
			Logger.error(be.getMessage(), be);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}

		((BillForm)getExComponent()).initUI();
	}

}