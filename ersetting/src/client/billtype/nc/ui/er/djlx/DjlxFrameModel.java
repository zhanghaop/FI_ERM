package nc.ui.er.djlx;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.event.EventListenerList;

import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Log;
import nc.impl.er.proxy.ProxyDjlx;
import nc.ui.er.plugin.IFrameDataModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

public class DjlxFrameModel implements IFrameDataModel, IDjlxModel {
	/**���ݱ仯����������*/
	private EventListenerList m_DataChangeListenerList = null;
	private Hashtable<String,BillTypeVO> m_billtypeCache = null; //�������ͻ���
	private Hashtable<String,BillTypeVO> m_djdlCache = null;//���ݴ��໺��
//	private BillTypeTrans m_DisplayTrans = null;//��ʾ��Ϣ������
//	 ����һ���ڵ�(�͵����������Ӧ�������Ե����ĸ��ģ�
	private String[][] m_djdlinfo = null;
	private BillTypeVO[] m_vos = null;
	private BillTypeVO m_currvo = null;
	
	private BMMachine m_bmmachine = null;
	
	private BMMachine getBmmachine() {
		if(m_bmmachine==null){
			m_bmmachine = new BMMachine();
		}
		return m_bmmachine;
	}
	/* ���� Javadoc��
	 * @see nc.ui.arap.djlx.iDjlxModel#addDataChangeListener(nc.ui.pub.beans.ValueChangedListener)
	 */
	public void addDataChangeListener(ValueChangedListener vl) {
		if(vl!=null){
			getDataChangeListenerList().add(ValueChangedListener.class,vl);
		}
	}
	/* ���� Javadoc��
	 * @see nc.ui.arap.djlx.iDjlxModel#removeDataChangeListener(nc.ui.pub.beans.ValueChangedListener)
	 */
	public void removeDataChangeListener(ValueChangedListener vl) {
		// TODO �Զ����ɷ������
		if(vl!=null){
			getDataChangeListenerList().remove(ValueChangedListener.class,vl);
		}
	}
	
	/* ���� Javadoc��
	 * @see nc.ui.arap.djlx.iDjlxModel#getDjdlCache()
	 */
	public Hashtable<String,BillTypeVO> getDjdlCache() {
		if(m_djdlCache==null){
			m_djdlCache = new Hashtable<String,BillTypeVO>();
			int k = getDjdlCode().length;
			for (int i = 0; i < k; i++) {
				BillTypeVO vo = new BillTypeVO();
				DjLXVO djlx = new DjLXVO();
				djlx.setDjdl(getDjdlCode()[i]);
				djlx.setDjlxoid(getDjdlCode()[i]);
//				djlx.setDjlxbm(getDjdlName()[i]);
				djlx.setDjlxmc(getDjdlName()[i]);
//				djlx.setDjlxjc(getDjdlName()[i]);
				djlx.setMjbz(UFBoolean.FALSE);
				vo.setParentVO(djlx);	
				getDjdlCache().put(getDjdlCode()[i],vo);
			}
		}
		return m_djdlCache;
	}

	public String[][] getDjdlinfo() {
		if(m_djdlinfo==null){
			m_djdlinfo = new String[2][];
			m_djdlinfo[1]= new String[]{BXConstans.JK_NAME,BXConstans.BX_NAME};

			m_djdlinfo[0]= new String[]{BXConstans.JK_DJDL,BXConstans.BX_DJDL };
		}
		return m_djdlinfo;
	}

//	private BillTypeTrans getDisplayTrans() {
//		if(m_DisplayTrans==null){
//			m_DisplayTrans = new BillTypeTrans();
//		}
//		return m_DisplayTrans;
//	}
	/* ���� Javadoc��
	 * @see nc.ui.arap.djlx.iDjlxModel#getDjlxCache()
	 */
	protected Hashtable<String,BillTypeVO> getDjlxCache(){
		if(m_billtypeCache==null){
			m_billtypeCache = new Hashtable<String,BillTypeVO>();
			int k = getDjdlCode().length;
			for (int i = 0; i < k; i++) {
				getDjlxCache().put(getDjdlCode()[i],getDjdlCache().get(getDjdlCode()[i]));
			}
		}
		return m_billtypeCache;
	}
	private void initCache(BillTypeVO[] vos) throws BusinessException{
		try{
			m_billtypeCache = null;
			
//			getDisplayTrans().fillDisplayinfo(vos);
			if(vos!=null){				
				int iLeg = vos.length;
				for(int i=0;i<iLeg;i++){
					getDjlxCache().put(vos[i].getParentVO().getPrimaryKey().trim(),vos[i]);
				}
			}
			getBmmachine().setBilltypes(vos);
			getBmmachine().init();
		}catch (ComponentException e) {
			Log.getInstance(this.getClass()).error(e.getMessage(),e);
		} 		
	}
	/* ���� Javadoc��
	 * @see nc.ui.arap.djlx.iDjlxModel#getBilltypesbyDjdl(java.lang.String)
	 */
	public BillTypeVO[] getBilltypesbyDjdl(String djdl){
		Enumeration em= getDjlxCache().elements();
		ArrayList<BillTypeVO> al = new ArrayList<BillTypeVO>();
		BillTypeVO vo = null;
		DjLXVO djlx = null;
		while(em.hasMoreElements()){
			vo = (BillTypeVO)em.nextElement();
			djlx =(DjLXVO) vo.getParentVO();
			if(djdl.equals(djlx.getDjdl()) && !djlx.getDjlxoid().equals(djdl)){
				//��Ϊ�����а������鵥����������Ҫ���˵�				
				al.add(vo);
			}
		}
		if(al.size()>0){
			BillTypeVO[] types = new BillTypeVO[al.size()];
			types = (BillTypeVO[])al.toArray(types);
			return types;
		}
		return null;
	}

	/* ���� Javadoc��
	 * @see nc.ui.arap.djlx.iDjlxModel#getDjdlName()
	 */
	public String[] getDjdlName() {
		return getDjdlinfo()[1];
	}
	/* ���� Javadoc��
	 * @see nc.ui.arap.djlx.iDjlxModel#getDjdlCode()
	 */
	public String[] getDjdlCode() {
		return getDjdlinfo()[0];
	}


	public AggregatedValueObject[] getSelectedDatas() {
		// TODO �Զ����ɷ������
		return new BillTypeVO[]{(BillTypeVO)getCurrData()};
	}

	public AggregatedValueObject getCurrData() {
		// TODO �Զ����ɷ������
		return m_currvo;
	}

	public void initData() throws BusinessException{
		// TODO �Զ����ɷ������
		BillTypeVO[] vos = ProxyDjlx.getIArapBillTypePrivate().queryBillType(BXUiUtil.getBXDefaultOrgUnit());
		setDatas(vos);

	}

	public void setData(AggregatedValueObject vo) {
		// TODO �Զ����ɷ������
		m_currvo = (BillTypeVO)vo;
		if(m_currvo!=null){
			if(getDjlxCache().get(m_currvo.getDjlxoid().trim())!=null){
				getDjlxCache().put(((DjLXVO)m_currvo.getParentVO()).getDjlxoid(),m_currvo);
				rebuildvos();
				fireValueChanged(new ValueChangedEvent("updatevo",vo));
			}else{
				getBmmachine().BMDirty(m_currvo.getDjlxbm());
				getDjlxCache().put(((DjLXVO)m_currvo.getParentVO()).getDjlxoid(),m_currvo);
				rebuildvos();
				fireValueChanged(new ValueChangedEvent("addnewvo",vo));
			}
		}
		

	}

	public void setDatas(AggregatedValueObject[] vos) throws BusinessException {
		// TODO �Զ����ɷ������
		m_vos = (BillTypeVO[])vos;
		initCache(m_vos);
		
		fireValueChanged(new ValueChangedEvent("allvaluechanged",vos));
	}

	public AggregatedValueObject[] getAllData() {
		// TODO �Զ����ɷ������
		return m_vos;
	}

	private EventListenerList getDataChangeListenerList() {
		if(m_DataChangeListenerList==null){
			m_DataChangeListenerList = new EventListenerList();
		}
		return m_DataChangeListenerList;
	}
	
	private void fireValueChanged(ValueChangedEvent e){
		Object[] listeners = getDataChangeListenerList().getListenerList();
		for (int i = 1; i <listeners.length; i++) {
			if (listeners[i] instanceof ValueChangedListener) {
				((ValueChangedListener) listeners[i]).valueChanged(e);
			}
		}
	}
	public AggregatedValueObject getData(Object key) throws BusinessException {
		// TODO �Զ����ɷ������
		if(key==null){
			return null;
		}
		return (BillTypeVO)getDjlxCache().get(key);
	}
	public boolean removeData(AggregatedValueObject vo) throws BusinessException {
		// TODO �Զ����ɷ������
		if(vo!=null && getDjlxCache().get(((BillTypeVO)vo).getDjlxoid())!=null){
			getBmmachine().insertBM(((BillTypeVO)vo).getDjlxbm());
			getDjlxCache().remove(((DjLXVO)vo.getParentVO()).getDjlxoid());
			rebuildvos();
			fireValueChanged(new ValueChangedEvent("delvalue",vo));
			return true;
		}
		return false;
	}
	public String getbm() throws BusinessException {
		// TODO �Զ����ɷ������
		return getBmmachine().getBM();
	}
	public void insertbm(String bm) {
		// TODO �Զ����ɷ������
		getBmmachine().insertBM(bm);
		
	}

	private void rebuildvos(){
		
		Enumeration enumeration =getDjlxCache().elements();
		ArrayList<BillTypeVO> al = new ArrayList<BillTypeVO>();
		BillTypeVO vo = null;
		while(enumeration.hasMoreElements()){
			vo=(BillTypeVO)enumeration.nextElement();
			if(vo.getDjlxoid().trim().length()<=2){
				continue;
			}
			al.add(vo);
		}
		m_vos = new BillTypeVO[al.size()];
		m_vos=(BillTypeVO[])al.toArray(m_vos);
	}

}
