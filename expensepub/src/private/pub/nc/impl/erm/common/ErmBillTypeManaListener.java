package nc.impl.erm.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.bs.arap.util.SqlUtils;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.event.IArapBSEventType;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.fip.billitem.BillItemVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.sm.createcorp.CreateOrgInfo;
import nc.vo.sm.createcorp.CreatecorpVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 
 * @author liansg
 * @see ������Ҫ������¹��ܣ��������ţ������Ӽ���ʱ�����ü��ŵ�������Ԥ������
 * @see �������ţ������Ӽ���ʱ�����ñ����������͵�Ԥ������
 * @param
 */
public class ErmBillTypeManaListener implements IBusinessListener {
	public static final String CMP = "3607";

	public void doAction(IBusinessEvent event) throws BusinessException {

		//��֯��ʼ��ǰ�¼�
		if (IArapBSEventType.TYPE_ORGIINITIALIZE_BEFORE.equals(event.getEventType())) {
			nc.bs.businessevent.BusinessEvent evt = (nc.bs.businessevent.BusinessEvent) event;
			CreateOrgInfo info = (CreateOrgInfo) evt.getObject();
			//�Ƿ񿽱�������Ϣ������Ŀ (װ�����װ��������ʱ�����Կ���)
			boolean copyCmpBillItem = false;
			if (info != null && !ArrayUtils.isEmpty(info.getCreatecorpVOs())) {
				for (CreatecorpVO vo : info.getCreatecorpVOs()) {
					if (String.valueOf(BXConstans.ERM_MODULEID).equals(vo.getFunccode())) {
						//������������
						insertDjlx(vo.getPk_org());
						
						//װ��������ʱ����
						copyCmpBillItem = true;
					} else if (String.valueOf(CMP).equals(vo.getFunccode())) {
						//װ����ʱҲ����
						copyCmpBillItem = true;
					}
				}
			}
			if (copyCmpBillItem) {
				copyCmpSettleBillItem();
			}
		}
	}

	/**
	 * ����������Ϣ�ĵ�����Ŀ������������
	 */
	@SuppressWarnings("unchecked")
	private void copyCmpSettleBillItem() {
		List<String> billtypes = new ArrayList<String>();
		billtypes.add("263X");
		billtypes.add("264X");
		List<?> existsBillType = null;
		BaseDAO baseDAO = new BaseDAO();
		try {
			final String inSql = SqlUtils.getInStr("pk_billtype",billtypes.toArray(new String[0]));
			//��ѯ�Ƿ��Ѿ����ƹ��������͵Ľ�����Ϣ
			StringBuffer sql = new StringBuffer(" select distinct pk_billtype from fip_billitem where attrcode ='zb.settlenum' ");
			if(inSql!=null && inSql.length()>0){
				sql.append(" and ");
				sql.append(inSql);
			}
			existsBillType = (List<?>) baseDAO.executeQuery(sql.toString(),new ColumnListProcessor());
		} catch (DAOException e) {
			ExceptionHandler.consume(e);
		}
		//�����Ƶĵ�������
		List<String> preCopyBillType = new ArrayList<String>();
		for (String billtype : billtypes) {
			if (existsBillType == null) {
				existsBillType = new ArrayList<String>();
			}
			if (existsBillType.size() == 0) {
				preCopyBillType.addAll(billtypes);
				break;
			}
			if (!existsBillType.contains(billtype)) {
				preCopyBillType.add(billtype);
			}
		}
		if (preCopyBillType.size() > 0) {
			IMDPersistenceQueryService qryservice = MDPersistenceService.lookupPersistenceQueryService();
			boolean isLazy = false;
			try {
				//��ѯ������Ϣ�ĵ�����Ŀ
				final Collection<BillItemVO> billItemVOList = qryservice.queryBillOfVOByCond(BillItemVO.class," pk_billtype = '2201'", isLazy);
				for (String billtype : preCopyBillType) {
					List<BillItemVO> preCopyItemVOList = new ArrayList<BillItemVO>();
					for (BillItemVO billItemVO : billItemVOList) {
						//��¡
						BillItemVO preCopyItemVO = (BillItemVO) billItemVO.clone();
						if (preCopyItemVO.getPosition() == 1) {
							// ��ͷ
							preCopyItemVO.setAttrcode("zb."+ preCopyItemVO.getAttrcode());
						} else if (preCopyItemVO.getPosition() == 2) {
							// ����
							preCopyItemVO.setAttrcode("fb."+ billItemVO.getAttrcode());
						}
						preCopyItemVO.setPk_billtype(billtype);
						preCopyItemVO.setPk_billitem(null);
						//����
						preCopyItemVO.setStatus(VOStatus.NEW);
						preCopyItemVOList.add(preCopyItemVO);
					}
					//��������
					baseDAO.insertVOArray(preCopyItemVOList.toArray(new BillItemVO[0]));
				}
			} catch (MetaDataException e) {
				ExceptionHandler.consume(e);
			} catch (DAOException e) {
				ExceptionHandler.consume(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void insertDjlx(String pk_group) throws DAOException {

		BaseDAO baseDAO = new BaseDAO();
		Collection<DjLXVO> sourcevos = baseDAO
				.retrieveByClause(
						DjLXVO.class,
						" pk_group='@@@@' and djlxbm in ('2631','2632','2641','2642','2643','2644','2645','2646','2647')");
		Collection<ExpenseTypeVO> expensevos = baseDAO.retrieveByClause(
				ExpenseTypeVO.class, " pk_group='global00000000000000'");
		// ���Ӷ�Ӧ���ŵĽ�������
		for (DjLXVO sysvo : sourcevos) {
			sysvo.setPk_group(pk_group);
			baseDAO.insertVO(sysvo);
		}
		// ���Ӷ�Ӧ���ŵı�����������
		for (ExpenseTypeVO vos : expensevos) {
			vos.setPk_group(pk_group);
			baseDAO.insertVO(vos);
		}
	}
	
	
}
