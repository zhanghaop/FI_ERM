package nc.ui.er.djlx;

import nc.ui.er.component.ExButtonObject;
import nc.ui.er.djlx.listener.AddButtonStatListener;
import nc.ui.er.djlx.listener.BrowseStatListener;
import nc.ui.er.djlx.listener.CancelButtonActionListener;
import nc.ui.er.djlx.listener.DjlxBillCardHeadEditListener;
import nc.ui.er.djlx.listener.DjlxTreeSelectionListener;
import nc.ui.er.djlx.listener.EditButtonActionListener;
import nc.ui.er.djlx.listener.EdittingStatListener;
import nc.ui.er.djlx.listener.RefreshButtonActionListener;
import nc.ui.er.djlx.listener.SaveButtonActionListener;
import nc.ui.er.djlx.listener.SelectedDetailDataStatListener;
import nc.ui.er.plugin.DefaultListenerController;
import nc.ui.er.plugin.IInfoInitializer;
import nc.ui.er.plugin.IListenerController;
import nc.ui.ml.NCLangRes;

public class DjlxListenerInitializer implements IInfoInitializer {
	public void initTreeListeners(IListenerController lc){
		DjlxTreeSelectionListener ls = new DjlxTreeSelectionListener();
		ls.setMainFrame(lc.getMainFrame());
		lc.addTreeSelectionListener(ls);
	}
	/* ���� Javadoc��
	 * @see nc.ui.fi.arap.plugin.IInitInfo#initBillCardListeners(nc.ui.fi.arap.plugin.IListenerController)
	 */
	public void initBillCardListeners(IListenerController lc){
		DjlxBillCardHeadEditListener chel = new DjlxBillCardHeadEditListener();
		chel.setMainFrame(lc.getMainFrame());
		lc.addBillCardEdit2Listener(chel);
		lc.addBillCardEditListener(chel);
	}
	/* ���� Javadoc��
	 * @see nc.ui.fi.arap.plugin.IInitInfo#initBillListListeners(nc.ui.fi.arap.plugin.IListenerController)
	 */
	public void initBillListListeners(IListenerController lc){
		
	}
	/* ���� Javadoc��
	 * @see nc.ui.fi.arap.plugin.IInitInfo#initBtns(nc.ui.fi.arap.plugin.IListenerController)
	 */
	public void initBtns(IListenerController lc){
		DefaultListenerController dlc = (DefaultListenerController)lc;
			/** ϵͳ��ť */
//			 ExButtonObject btn = new ExButtonObject(NCLangRes.getInstance()
//					.getStrByID("common", "UC001-0000002")/* @res "����" */, NCLangRes
//					.getInstance().getStrByID("common", "UC001-0000002")/* @res "����" */,
//					5, "����", "sysinit_add"); /*-=notranslate=-*/
//			 btn.setMainFrame(dlc.getMainFrame());
//			 btn.setBtnActLisener(new AddButtonActionListener());
//			 btn.setBtnStatLisener(new AddButtonStatListener());
//			 dlc.getBtnList().add(btn);
//			 dlc.getBtnMap().put(btn.getBtnid(),btn);
//			 ExButtonObject btn= new ExButtonObject(NCLangRes.getInstance()
//					.getStrByID("common", "UC001-0000039")/* @res "ɾ��" */, NCLangRes
//					.getInstance().getStrByID("common", "UC001-0000039")/* @res "ɾ��" */,
//					5, "ɾ��","sysinit_del"); /*-=notranslate=-*/
//			 btn.setMainFrame(dlc.getMainFrame());
//			 btn.setBtnActLisener(new DelButtonActionListener());
//			 btn.setBtnStatLisener(new SelectedSysDataStatListener());
//			 dlc.getBtnList().add(btn);
//			 dlc.getBtnMap().put(btn.getBtnid(),btn);
			 ExButtonObject btn = new ExButtonObject(NCLangRes.getInstance()
					.getStrByID("common", "UC001-0000045")/* @res "�޸�" */, NCLangRes
					.getInstance().getStrByID("common", "UC001-0000045")/* @res "�޸�" */,
					5, "�޸�","sysinit_edit"); /*-=notranslate=-*/
			 btn.setMainFrame(dlc.getMainFrame());
			 btn.setBtnActLisener(new EditButtonActionListener());
			 btn.setBtnStatLisener(new SelectedDetailDataStatListener());
			 dlc.getBtnList().add(btn);
			 dlc.getBtnMap().put(btn.getBtnid(),btn);
			 btn = new ExButtonObject(NCLangRes.getInstance()
					.getStrByID("common", "UC001-0000001")/* @res "����" */, NCLangRes
					.getInstance().getStrByID("common", "UC001-0000001")/* @res "����" */,
					5, "����","sysinit_save"); /*-=notranslate=-*/
			 btn.setMainFrame(dlc.getMainFrame());
			 btn.setBtnActLisener(new SaveButtonActionListener());
			 btn.setBtnStatLisener(new EdittingStatListener());
			 dlc.getBtnList().add(btn);
			 dlc.getBtnMap().put(btn.getBtnid(),btn);
			 btn = new ExButtonObject(
					NCLangRes.getInstance()
							.getStrByID("20060101", "UC001-0000008")/* @res "ȡ��" */,
					NCLangRes.getInstance()
							.getStrByID("20060101", "UC001-0000008")/* @res "ȡ��" */,
					5, "ȡ��","sysinit_cancel"); /*-=notranslate=-*/
			 btn.setMainFrame(dlc.getMainFrame());		 
			 btn.setBtnActLisener(new CancelButtonActionListener());
			 btn.setBtnStatLisener(new EdittingStatListener());
			 dlc.getBtnList().add(btn);
			 dlc.getBtnMap().put(btn.getBtnid(),btn);
			 btn = new ExButtonObject(NCLangRes.getInstance()
					.getStrByID("common", "UC001-0000009")/* @res "ˢ��" */, NCLangRes
					.getInstance().getStrByID("common", "UC001-0000009")/* @res "ˢ��" */,
					5, "ˢ��","sysinit_refresh"); /*-=notranslate=-*/
			 btn.setMainFrame(dlc.getMainFrame());
			 btn.setBtnActLisener(new RefreshButtonActionListener());
			 btn.setBtnStatLisener(new BrowseStatListener());
			 dlc.getBtnList().add(btn);
			 dlc.getBtnMap().put(btn.getBtnid(),btn);
			 btn= new ExButtonObject(
					NCLangRes.getInstance()
							.getStrByID("20060101", "UPT20060101-000002")/* @res "����" */,
					NCLangRes.getInstance()
							.getStrByID("20060101", "UPP20060101-000005")/*
																			 * @res
																			 * "���Ŷ��ڵ������ͽ��з���"
																			 */,
					5, "����","sysinit_distribute"); /*-=notranslate=-*/
			 btn.setMainFrame(dlc.getMainFrame());
			 
			 //FIXME
			 btn.setBtnActLisener(null);
//			 btn.setBtnActLisener(new DistributeButtonActionListener());
			 btn.setBtnStatLisener(new AddButtonStatListener());
			 dlc.getBtnList().add(btn);
			 dlc.getBtnMap().put(btn.getBtnid(),btn);

		}
}
