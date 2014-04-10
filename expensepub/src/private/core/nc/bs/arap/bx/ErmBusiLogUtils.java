package nc.bs.arap.bx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.arap.util.ArapBillVOUtils;
import nc.bs.busilog.util.BusinessLogServiceUtil;
import nc.bs.busilog.vo.BusinessLogContext;
import nc.bs.logging.Logger;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IBean;
import nc.md.model.MetaDataException;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;

/**
 * v6.1 ҵ����־������
 *
 */
public class ErmBusiLogUtils {
	
	private static Map<String,IBean> map = new HashMap<String, IBean>();

	public static IBean getMetaDataIDByVoClassName(String voClassName)
			throws MetaDataException {
		if (!map.containsKey(voClassName)) {
			map.put(voClassName, MDBaseQueryFacade.getInstance().getBeanByFullClassName(voClassName));
		}
		return map.get(voClassName);
	}
	
	private static enum ErmBusiLogOperationType {
		/**
		 * �޸�
		 */
		EDIT("66efc3ac-092f-419e-8d48-464d8003b156", "�޸�"),/*-=�����޸�=-*//*-=notranslate=-*/

		/**
		 * ɾ��
		 */
		DELETE("78af39d8-92a3-4acc-92ea-3b3eee0dcbba", "ɾ��"),/*-=����ɾ��=-*//*-=notranslate=-*/
		
		/**
		 * ɾ��
		 */
		JKEDIT("4e9bc7ae-a9fa-4f29-9846-a4afcf95ea31", "�޸�"),/*-=����޸�=-*//*-=notranslate=-*/
		
		/**
		 * ɾ��
		 */
		JKDELETE("69159c6e-4c8d-4fe0-91c5-b5b700dd0d4f", "ɾ��");/*-=���ɾ��=-*//*-=notranslate=-*/

		String name;

		String pk_operation;

		ErmBusiLogOperationType(String pk_operation, String name) {
			this.pk_operation = pk_operation;
			this.name = name;
		}

		public String getPk_operation() {
			return pk_operation;
		}

		public String getName() {
			return name;
		}
	}
	
	public static void insertSmartBusiLogs(boolean isUpdate, JKBXVO vo) {
		try {
			List<BusinessLogContext> listvo = new ArrayList<BusinessLogContext>();
			BusinessLogContext ctx = new BusinessLogContext();
			IBean bean = getMetaDataIDByVoClassName(vo.getParentVO().getClass().getName());
			ctx.setBusiobjcode(bean.getName());
			ctx.setBusiobjname(bean.getDisplayName());
			ErmBusiLogOperationType delType = null;//ɾ��
			ErmBusiLogOperationType editType = null;//����
			
			if(vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)){//����ʱ�����Ĳ���
				delType = ErmBusiLogOperationType.DELETE;
				editType = ErmBusiLogOperationType.EDIT;
			}else{//���ʱ�����Ĳ���
				delType = ErmBusiLogOperationType.JKDELETE;
				editType = ErmBusiLogOperationType.JKEDIT;
			}
			
			ctx.setBusiobjvo(vo);
			ctx.setOldbusiobjvo(vo.getBxoldvo());
			
			ctx.setOrgpk_busiobj(vo.getParentVO().getPk_org());
			ctx.setTypepk_busiobj(bean.getID());
			ctx.setPk_busiobj(bean.getID());
			ctx.setPk_operation(isUpdate ? editType.getPk_operation() :  delType.getPk_operation());
			
			listvo.add(ctx);
			BusinessLogServiceUtil.insertBatchBusiLogAsynch(listvo);
		} catch (Exception e) {
			Logger.error("~~~~~~~~~~~~~~~~~���ɱ���ҵ����־����!!!~~~~~~~~~~~~~~~~~");
		}
	}

}