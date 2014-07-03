package nc.ui.erm.billquery.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.settle.SettleUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
/**
**
* ֻ��Խ����������ƾ֤�����ǽ���ɹ�
* ��������Чĩ����ĵ���
 * @author wangled
 *
 */
public class DealVoucherAction extends NCAction {
	private static final long serialVersionUID = 1L;

	private BillManageModel model;

	public DealVoucherAction() {
		setCode("DealVoucher");
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0164")/*@res "����ƾ֤"*/);
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object[] vos = (Object[]) getModel().getSelectedOperaDatas();
		
		boolean existSuccess = true;
		
		boolean exist = false;
        
		if (vos==null || vos.length < 1) {
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0165")/* @res "û�п�����ƾ֤�ĵ���,����ʧ��"*/);
		}
		
		JKBXVO[] jkbxVos = Arrays.asList(vos).toArray(new JKBXVO[0]);
		StringBuffer msg = new StringBuffer();
		List<JKBXVO> dealVo = new ArrayList<JKBXVO>();
		
		//TODO : ����ǰҪ�滻����
		//existSuccess = dealBillBaseVersion(existSuccess, jkbxVos, msg, dealVo);
		
		existSuccess = dealBillForCWZX(existSuccess, jkbxVos, msg, dealVo);
		
		
		if (dealVo != null && dealVo.size() != 0) {
			try {
				// �����ƽ̨
				List<JKBXVO> returnVo = NCLocator.getInstance().lookup(IBXBillPrivate.class).effectToFip(dealVo);
				
				exist = true;//�гɹ���
				//
				getModel().directlyUpdate(returnVo.toArray(new AggregatedValueObject[returnVo.size()]));

			} catch (BusinessException e1) {
				existSuccess = false;
				msg.append(e1.getMessage());
			}
		}else if((dealVo==null ||dealVo.size()==0) && existSuccess){
			existSuccess = false;
			msg.append(nc.ui.ml.NCLangRes
					.getInstance().getStrByID("201107_0", "0201107-0170"));//������������ֻ��������ƾ֤�����ǽ���ɹ��ĵ���
		}
		
		if (existSuccess) {
			ShowStatusBarMsgUtil.showStatusBarMsg(nc.ui.ml.NCLangRes
					.getInstance().getStrByID("201107_0", "0201107-0169"),
					getModel().getContext());
		} else if(!existSuccess && exist){//�гɹ�Ҳ��ʧ��
			ShowStatusBarMsgUtil.showErrorMsg(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0", "0201107-0172"), msg.toString(),
					getModel().getContext());
		} else if(!existSuccess && !exist){
			ShowStatusBarMsgUtil.showErrorMsg(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0", "0201107-0173"), msg.toString(),
					getModel().getContext());
		}
	}
	/**
	 * ehp2 : �����жϵ����Ƿ�ɾ��
	 * @param jkbxVos
	 * @throws BusinessException
	 */
	private Map<String,JKBXHeaderVO> checkBillExist(JKBXVO[] jkbxVos) throws BusinessException {
		Map<String,JKBXHeaderVO> map = new LinkedHashMap<String,JKBXHeaderVO>();
		//����У�飺������ݱ�ɾ���ˣ���ʾ��Ϣ
		String[] pk_jkbx = nc.vo.fipub.utils.VOUtil.getAttributeValues(jkbxVos, JKBXHeaderVO.PK_JKBX);
		List<JKBXHeaderVO> queryHeaders = NCLocator.getInstance().
		lookup(IBXBillPrivate.class).queryHeadersByPrimaryKeys(pk_jkbx, null);
		if(queryHeaders!=null){
			for (JKBXHeaderVO jkbxHeaderVO : queryHeaders) {
				map.put(jkbxHeaderVO.getPk_jkbx(), jkbxHeaderVO);
			}
		}
		return map;
	}

	/**
	 * ehp2 : ��άר����������ʹ�ã����Զ���Ч�ĵ���������ĩƾ֤����δ��Ч�ĵ��������ݹ�ƾ֤��
	 * @param existSuccess
	 * @param jkbxVos
	 * @param msg
	 * @param dealVo
	 * @return
	 * @throws BusinessException
	 */
	private boolean dealBillForCWZX(boolean existSuccess, JKBXVO[] jkbxVos,
			StringBuffer msg, List<JKBXVO> dealVo) throws BusinessException {
		
		Map<String, JKBXHeaderVO> ExistBill = checkBillExist(jkbxVos);
		
		for (int i = 0; i < jkbxVos.length; i++) {
			if(SettleUtil.isJsToFip(jkbxVos[i].getParentVO())){
				//���ڵ��ݵĲ���У��
				if(!ExistBill.containsKey(jkbxVos[i].getParentVO().getPk_jkbx())){
					existSuccess = false;
					msg.append(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0166") + jkbxVos[i].getParentVO().getDjbh() + " "+nc.ui.ml.NCLangRes.getInstance().getStrByID("2011",
					"UPP2011-000954")).append("\r\n");
					continue;
				}
				//�����ݴ�ĵ��ݲ�������ƾ֤
				if(jkbxVos[i].getParentVO().getSxbz()!=null && BXStatusConst.DJZT_TempSaved == jkbxVos[i].getParentVO().getDjzt()){
					existSuccess = false;
					msg.append(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0166") + jkbxVos[i].getParentVO().getDjbh() + " "+nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0174")).append("\r\n");
					continue;
				}
		
				//�������ϵĵ��ݲ�����ĩ����:ehp3
				if(jkbxVos[i].getParentVO().getDjzt()!=null && BXStatusConst.DJZT_Invalid == jkbxVos[i].getParentVO().getDjzt()){
					existSuccess = false;
					msg.append(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0166") + jkbxVos[i].getParentVO().getDjbh() + " "+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000955")).append("\r\n");
					continue;
				}
				//������Ч�ĵ��ݣ�����ƾ֤��־�����ݹ�ʱ����ʾ��ʾ����ƾ֤
//				if(((Integer.valueOf(BXStatusConst.SXBZ_VALID).equals(jkbxVos[i].getParentVO().getSxbz()) 
//					&& jkbxVos[i].getParentVO().getVouchertag()!=null && jkbxVos[i].getParentVO().getVouchertag()!=BXStatusConst.ZGDeal )
//					)
//					|| (!Integer.valueOf(BXStatusConst.SXBZ_VALID).equals(jkbxVos[i].getParentVO().getSxbz())
//					&& jkbxVos[i].getParentVO().getVouchertag()!=null && jkbxVos[i].getParentVO().getVouchertag()==BXStatusConst.ZGDeal)){
//					existSuccess = false;
//					msg.append(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
//					"0201107-0166") + jkbxVos[i].getParentVO().getDjbh() + " "+nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
//					"0201107-0168")).append("\r\n");
//					continue;
//				}
				
				dealVo.add(jkbxVos[i]);//��Ҫ�����ƽ̨�ĵ���
			}else{
				continue;
			}
		}
		return existSuccess;
	}
	
	/**
	 * TODO
	 * ehp2 : ͨ������ݣ��ڷ���ǰ��Ҫ�滻���� 2014-03-12 (��Ҫɾ��)
	 * @param existSuccess
	 * @param jkbxVos
	 * @param msg
	 * @param dealVo
	 * @return
	 * @throws BusinessException
	 */
	private boolean dealBillBaseVersion(boolean existSuccess, JKBXVO[] jkbxVos,
			StringBuffer msg, List<JKBXVO> dealVo) throws BusinessException {
		
		Map<String, JKBXHeaderVO> ExistBill = checkBillExist(jkbxVos);
		for (int i = 0; i < jkbxVos.length; i++) {
			//String param = SysinitAccessor.getInstance().getParaString(jkbxVos[i].getParentVO().getPk_org(), "CMP37");
			if(SettleUtil.isJsToFip(jkbxVos[i].getParentVO())){
				//���ڵ��ݵĲ���У��
				if(!ExistBill.containsKey(jkbxVos[i].getParentVO().getPk_jkbx())){
					existSuccess = false;
					msg.append(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0166") + jkbxVos[i].getParentVO().getDjbh() + " "+nc.ui.ml.NCLangRes.getInstance().getStrByID("2011",
					"UPP2011-000954")).append("\r\n");
					continue;
				}
				
				if(!Integer.valueOf(BXStatusConst.SXBZ_VALID).equals(jkbxVos[i].getParentVO().getSxbz())){//δ��Ч�ĵ���
					existSuccess = false;
					msg.append(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0166") + jkbxVos[i].getParentVO().getDjbh() + " "+nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0167")).append("\r\n");
					continue;
				
				}
				if(Integer.valueOf(BXStatusConst.SXBZ_VALID).equals(jkbxVos[i].getParentVO().getSxbz()) 
						&& (jkbxVos[i].getParentVO().getVouchertag()!=null) 
						){
					existSuccess = false;
					msg.append(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0166") + jkbxVos[i].getParentVO().getDjbh() + " "+nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0168")).append("\r\n");
					continue;
				
				}
				dealVo.add(jkbxVos[i]);//��Ҫ�����ƽ̨�ĵ���
			}else{
				continue;
			}
		}
		return existSuccess;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	@Override
	protected boolean isActionEnable() {
		boolean flag = BXUtil.isProductInstalled(getModel().getContext().getPk_group(), BXConstans.TM_CMP_FUNCODE);
		if(BXConstans.MONTHEND_DEAL.equals(getModel().getContext().getNodeCode())){
			if(!flag){
				ShowStatusBarMsgUtil.showErrorMsg(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("201107_0",
						"0201107-0035"), nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("201107_0",
						"0201107-0171"),//δ��ȫ�ֽ����ģ�飬�ð�ť���ڵ㲻����
						getModel().getContext());
			}
		}
		return flag;
	}
	
	
}
