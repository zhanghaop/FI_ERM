package nc.bs.arap.bx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import nc.bs.businessevent.EventDispatcher;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.callouter.FipCallFacade;
import nc.bs.er.settle.ErForCmpBO;
import nc.bs.er.util.BXDataPermissionChkUtil;
import nc.bs.er.util.FipUtil;
import nc.bs.er.util.SqlUtils;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.impl.arap.bx.ArapBXBillPrivateImp;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.ISqdlrKeyword;
import nc.itf.fi.pub.Currency;
import nc.itf.fipub.summary.ISummaryQueryService;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.jdbc.framework.ConnectionFactory;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.crossdb.CrossDBConnection;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.util.SQLHelper;
import nc.pubitf.arap.payable.IArapPayableBillPubQueryService;
import nc.pubitf.arap.payable.IArapPayableBillPubService;
import nc.pubitf.arap.receivable.IArapReceivableBillPubQueryService;
import nc.pubitf.arap.receivable.IArapReceivableBillPubService;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.billstatus.ARAPBillStatus;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.arap.payable.AggPayableBillVO;
import nc.vo.arap.receivable.AggReceivableBillVO;
import nc.vo.cmp.BusiStatus;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JsConstrasVO;
import nc.vo.ep.bx.MtappfUtil;
import nc.vo.ep.bx.SqdlrVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.er.check.VOChecker;
import nc.vo.er.check.VOStatusChecker;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.settle.SettleUtil;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.fipub.billcode.FinanceBillCodeInfo;
import nc.vo.fipub.billcode.FinanceBillCodeUtils;
import nc.vo.fipub.summary.SummaryVO;
import nc.vo.fipub.utils.KeyLock;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFTime;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.rbac.constant.INCSystemUserConst;
import nc.vo.uap.rbac.role.RoleVO;
import nc.vo.util.AuditInfoUtil;

/**
 * @author twei
 * 
 *         nc.bs.ep.bx.BXZbBO
 * 
 *         �����൥�ݱ�ͷҵ����
 */
public class BXZbBO {
	private JKBXDAO jkbxDAO;

	/**
	 * ������ɾ��
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] delete(JKBXVO[] vos) throws BusinessException {

		MessageVO[] msgVos = new MessageVO[vos.length];

		compareTs(vos);// tsУ�飨��������������

		List<JKBXHeaderVO> deleteHeaders = new ArrayList<JKBXHeaderVO>();
		try {
			for (int i = 0; i < vos.length; i++) {
				msgVos[i] = new MessageVO(vos[i], ActionUtils.DELETE);
				JKBXHeaderVO parentVO = vos[i].getParentVO();

				JKBXVO vo = retriveItems(vos[i]);// ������Ҫ���䣬����ĿԤ��һЩҵ��������Ҫ
				fillUpMapf(vo);

				checkDelete(vo);// У��ɾ��

				beforeActInf(vo, MESSAGE_DELETE);
				// ɾ��ҵ����Ϣ
				getBxBusitemBO(parentVO.getDjlxbm(), parentVO.getDjdl()).deleteByBXVOs(new JKBXVO[] { vo });

				// ɾ�����������Ϣ
				new ContrastBO().deleteByPK_bxd(new String[] { parentVO.getPk_jkbx() });
				// ɾ���������� Ԥ����ϸ
				new BxVerifyAccruedBillBO().deleteByBxdPks(parentVO.getPk_jkbx());
				
				//����ɾ����ͷ
				deleteHeaders.add(parentVO);

				// �ݴ浥��û�д����㣬ɾ���ݴ�ĵ���ҲӦ�ò��߽���
				boolean isTempSave = BXStatusConst.DJZT_TempSaved == parentVO.getDjzt().intValue() ? true : false;

				// �ж�CMP��Ʒ�Ƿ�����
				boolean isCmpInstalled = isCmpInstall(vo.getParentVO());

				// �Ƿ� �����տ�Ҳ�޸���
				boolean notExistsPayOrRecv = (vo.getParentVO().getZfybje() == null || vo.getParentVO().getZfybje()
						.equals(new UFDouble(0)))
						&& (vo.getParentVO().getHkybje() == null || vo.getParentVO().getHkybje()
								.equals(new UFDouble(0)));

				if (!isTempSave && !notExistsPayOrRecv && isCmpInstalled) {
					new ErForCmpBO().invokeCmp(vo, parentVO.getDjrq(), BusiStatus.Deleted);
				}

				afterActInf(vo, MESSAGE_DELETE);

				// �ǳ��õ��ݻ��˵��ݺţ����õ��ݲ����ɵ��ݺ�
				if (!vo.getParentVO().isInit()) {
					returnBillCode(vo);
					// ɾ��������
					NCLocator.getInstance().lookup(IWorkflowMachine.class).deleteCheckFlow(vo.getParentVO().getDjlxbm(), vo.getParentVO().getPrimaryKey(), vo, InvocationInfoProxy.getInstance().getUserId());
				}

			}

			// ɾ����ͷ
			getJKBXDAO().delete(deleteHeaders.toArray(new JKBXHeaderVO[] {}));
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

		return msgVos;
	}

	public IBXBusItemBO getBxBusitemBO(String djlxbm, String djdl) throws BusinessException {
		try {
			BusiTypeVO busTypeVO = BXUtil.getBusTypeVO(djlxbm, djdl);
			String busiClass = busTypeVO.getInterfaces().get(BusiTypeVO.IBXBusItemBO);
			return (IBXBusItemBO) Class.forName(busiClass).newInstance();
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	/**
	 * ���⴦��ժҪ
	 * 
	 * @param vo
	 */
	private void dealZyName(JKBXVO vo) throws BusinessException {
		String value = vo.getParentVO().getZy();
		if (value != null && value.length() == 20) {
			ISummaryQueryService service = NCLocator.getInstance().lookup(
					nc.itf.fipub.summary.ISummaryQueryService.class);
			SummaryVO[] summaryVOs = service.querySummaryVOByCondition(SummaryVO.PK_SUMMARY + "='" + value + "'");
			if (summaryVOs != null && summaryVOs.length > 0) {
				vo.getParentVO().setZy(summaryVOs[0].getSummaryname());
			}
		}
	}

	/**
	 * ��ʽ���棬����ҵ��У�飬��������Ҫ�Ľӿ�
	 * 
	 * @param vos
	 *            Ҫ���浥�ݵľۺ�VO����
	 */
	public JKBXVO[] save(JKBXVO[] vos) throws BusinessException {
		VOChecker checker = new VOChecker();
		for (JKBXVO vo : vos) {
			// ���⴦��ժҪ
			dealZyName(vo);
			//ֻ¼��ͷ����¼�������������ɱ�����
			BXUtil.generateJKBXRow(vo);
			
			VOChecker.prepare(vo);
			// ����У��
			checker.checkkSaveBackground(vo);

			// ��鵥���Ƿ����
			VOChecker.checkErmIsCloseAcc(vo);

			if (vo.getParentVO().getQcbz() != null && vo.getParentVO().getQcbz().booleanValue()) {
				// �ڳ����ݱ�����Զ����ͨ��
				vo.getParentVO().setSpzt(IPfRetCheckInfo.PASSING);
				vo.getParentVO().setShrq(new UFDateTime(vo.getParentVO().getDjrq(), new UFTime("00:00:00")));

				vo.getParentVO().setJsrq(vo.getParentVO().getDjrq());
				vo.getParentVO().setApprover(InvocationInfoProxy.getInstance().getUserId());
				vo.getParentVO().setJsr(InvocationInfoProxy.getInstance().getUserId());

			} 

			// ȥ�������¼���Ϊ����ʱ��
			if(vo.getParentVO().getCreator() == null){
				AuditInfoUtil.addData(vo.getParentVO());
			}
			
			fillUpMapf(vo);
		}

		beforeActInf(vos, MESSAGE_SAVE);

		// ���浥��
		JKBXVO[] bxvos;
		try {
			bxvos = getJKBXDAO().save(vos);
		} catch (Exception e) {
			for (JKBXVO bxvo : vos) {
				returnBillCode(bxvo);
			}
			throw ExceptionHandler.handleException(e);
		}

		try {
			// �����ֽ���ƽ̨����
			ErForCmpBO erBO = new ErForCmpBO();
			for (JKBXVO vo : vos) {
				BusiStatus billStatus = SettleUtil.getBillStatus(vo.getParentVO(), false);
				// �Ƿ�װ����
				boolean isInstallCmp = BXUtil.isProductInstalled(vo.getParentVO().getPk_group(),
						BXConstans.TM_CMP_FUNCODE);

				// �Ƿ� �����տ�Ҳ�޸���
				boolean notExistsPayOrRecv = (vo.getParentVO().getZfybje() == null || vo.getParentVO().getZfybje()
						.equals(new UFDouble(0)))
						&& (vo.getParentVO().getHkybje() == null || vo.getParentVO().getHkybje()
								.equals(new UFDouble(0)));

				if (isInstallCmp && !notExistsPayOrRecv) {
					erBO.invokeCmp(vo, vo.getParentVO().getDjrq(), billStatus);
				}
			}

			afterActInf(vos, MESSAGE_SAVE);

		} catch (Exception e) {
			for (JKBXVO bxvo : vos) {
				returnBillCode(bxvo);
			}
			throw ExceptionHandler.handleException(e);
		}

		return bxvos;
	}

	private void checkForInitBill(JKBXVO[] vos) throws BusinessException {
		for (JKBXVO vo : vos) {
			if (vo.getParentVO().isInit()) {
				JKBXHeaderVO header = vo.getParentVO();
				String djlxbm = header.getDjlxbm();
				String pk_org = header.getPk_org();
				String pk_group = header.getPk_group();
				String pk = "ER_JKBXINIT" + djlxbm + pk_org;
				String user = KeyLock.dynamicLock(pk);

				if (user != null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0060")/*
											 * @res
											 * "�����쳣�����泣�õ���ʧ�ܣ��õ������͵ĳ��õ����ڵ�ǰ��λ�Ѿ����ڣ�"
											 */);
				}

				DjCondVO condVO = new DjCondVO();
				UFBoolean isGroup = vo.getParentVO().getIsinitgroup();
				if (isGroup.booleanValue()) {
					condVO.defWhereSQL = " zb.djlxbm='" + djlxbm + "' and zb.dr=0 and zb.isinitgroup='" + isGroup + "'";
					condVO.pk_group = new String[] { pk_group };
				} else {
					condVO.defWhereSQL = " zb.djlxbm='" + djlxbm + "' and zb.dr=0 and zb.isinitgroup='" + isGroup + "'";
					condVO.pk_org = new String[] { pk_org };
				}
				condVO.isInit = true;
				condVO.isCHz = false;
				List<JKBXVO> svos = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryVOs(0, 1, condVO);

				if (svos != null && svos.size() > 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0060")/*
											 * @res
											 * "�����쳣�����泣�õ���ʧ�ܣ��õ������͵ĳ��õ����ڵ�ǰ��λ�Ѿ����ڣ�"
											 */);
				}
			}
		}
	}

	/**
	 * �ݴ�ҵ�����������κ�ҵ��У�飬����������ģ��ӿ�
	 * 
	 * @param vos
	 *            Ҫ�ݴ浥�ݵľۺ�VO����
	 */
	public JKBXVO[] tempSave(JKBXVO[] vos) throws BusinessException {
		// ���õ��ݵĲ���У��
		checkForInitBill(vos);

		for (JKBXVO vo : vos) {
			BXUtil.generateJKBXRow(vo);
			VOChecker.prepare(vo);
			
			// ������Զ��ύ
			vo.getParentVO().setSpzt(IBillStatus.FREE);
			vo.getParentVO().setDjzt(BXStatusConst.DJZT_TempSaved);

			new VOChecker().checkkSaveBackground(vo);
			// ���⴦��ժҪ
			dealZyName(vo);

			// ���������Ϣ
			if (vo.getParentVO().getPrimaryKey() != null) {
				AuditInfoUtil.updateData(vo.getParentVO());
			} else {
				AuditInfoUtil.addData(vo.getParentVO());
			}
		}

		beforeActInf(vos, MESSAGE_TEMP_SAVE);

		JKBXVO[] bxvos;
		try {
			bxvos = getJKBXDAO().save(vos);
		} catch (SQLException e) {
			throw ExceptionHandler.handleException(e);
		}

		afterActInf(vos, MESSAGE_TEMP_SAVE);

		return bxvos;
	}

	public JKBXVO[] update(JKBXVO[] vos) throws BusinessException {
		// У��ʱ���
		compareTs(vos);
		for (JKBXVO vo : vos) {
			// ���⴦��ժҪ
			dealZyName(vo);
			
			// ��ѯ�޸�ǰ��vo
			if (vo.getBxoldvo() == null) {
				List<JKBXVO> oldvo = null;
				oldvo = NCLocator.getInstance().lookup(IBXBillPrivate.class)
						.queryVOsByPrimaryKeysForNewNode(new String[] { vo.getParentVO().getPrimaryKey() },
								vo.getParentVO().getDjdl(), vo.getParentVO().isInit(), null);
				vo.setBxoldvo(oldvo.get(0));
				// ����children��Ϣ����ǰ̨�������ĵ�ֻ�Ǹı��children��
				fillUpChildren(vo, oldvo.get(0));
				// ͬ������CShareDetailVO��Ϣ
				fillUpCShareDetail(vo, oldvo.get(0));

			}
			//ֻ¼��ͷ����¼�������������ɱ�����
			BXUtil.generateJKBXRow(vo);
			
			VOChecker voChecker = new VOChecker();
			VOChecker.prepare(vo);
			//��̨У��
			voChecker.checkUpdateSave(vo);
			
			// �������뵥�����¼(Ԥ��������õ�)
			fillUpMapf(vo);
			fillUpMapf(vo.getBxoldvo());

			// ���õ��ݵ���,�ݴ�̬���ݲ�У�����
			boolean isChkCloseAcc = true;
			JKBXHeaderVO parentVO = vo.getParentVO();
			if (parentVO != null && (parentVO.isInit() || BXStatusConst.DJZT_TempSaved == parentVO.getDjzt())) {
				isChkCloseAcc = false;
			}
			if (isChkCloseAcc) {
				VOChecker.checkErmIsCloseAcc(vo);
			}
			if (vo.getParentVO().getDjzt() != BXStatusConst.DJZT_TempSaved) {
				if (vo.getParentVO().getQcbz() != null && vo.getParentVO().getQcbz().booleanValue()) {
					vo.getParentVO().setSpzt(IPfRetCheckInfo.PASSING);
					vo.getParentVO().setShrq(new UFDateTime(vo.getParentVO().getDjrq(), new UFTime("00:00:00")));
					vo.getParentVO().setJsrq(vo.getParentVO().getDjrq());
					vo.getParentVO().setApprover(InvocationInfoProxy.getInstance().getUserId());
					vo.getParentVO().setJsr(InvocationInfoProxy.getInstance().getUserId());
				}
			} 

			// ȡ�������¼���Ϊ�޸�ʱ��
			AuditInfoUtil.updateData(vo.getParentVO());
		}

		beforeActInf(vos, MESSAGE_UPDATE);

		JKBXVO[] bxvos = null;

		try {
			bxvos = getJKBXDAO().update(vos);
		} catch (SQLException e) {
			for (JKBXVO bxvo : vos) {
				returnBillCode(bxvo);
			}
			ExceptionHandler.handleException(e);
		}
		
		try {
			// �����ֽ���ƽ̨����
			ErForCmpBO erBO = new ErForCmpBO();

			for (JKBXVO vo : vos) {

				// ��ǰ������Ϣ״̬
				BusiStatus billStatus = SettleUtil.getBillStatus(vo.getParentVO(), false);

				// ��ǰ������Ϣ״̬
				BusiStatus oldBillStatus = SettleUtil.getBillStatus(vo.getBxoldvo().getParentVO(), false);

				// CMP��Ʒ�Ƿ�����
				boolean isInstallCmp = BXUtil.isProductInstalled(vo.getParentVO().getPk_group(),
						BXConstans.TM_CMP_FUNCODE);

				// �Ƿ��ݴ浥���޸�
				boolean isTmpSave = BXStatusConst.DJZT_TempSaved == vo.getParentVO().getDjzt();

				// �Ƿ񲻴����ո���
				boolean notExistsPayOrRecv = (vo.getParentVO().getZfybje() == null || vo.getParentVO().getZfybje()
						.equals(new UFDouble(0)))
						&& (vo.getParentVO().getHkybje() == null || vo.getParentVO().getHkybje()
								.equals(new UFDouble(0)));

				boolean isToSettle = false;
				if (oldBillStatus == BusiStatus.Save && billStatus == BusiStatus.Deleted) {
					isToSettle = true;
				} else {
					isToSettle = !notExistsPayOrRecv;
				}

				if (isInstallCmp && !isTmpSave && isToSettle) {
					erBO.invokeCmp(vo, vo.getParentVO().getDjrq(), billStatus);
				}
			}

			afterActInf(vos, MESSAGE_UPDATE);
			
			//��ղ��������
			for(JKBXVO vo : vos){
				vo.setBxoldvo(null);
			}
		} catch (Exception e) {
			for (JKBXVO bxvo : vos) {
				returnBillCode(bxvo);
			}
			throw ExceptionHandler.handleException(e);
		}

		return bxvos;
	}

	/**
	 * ���������¼
	 * 
	 * @param bxVo
	 * @throws BusinessException
	 */
	private void fillUpMapf(JKBXVO bxVo) throws BusinessException {
		// ���뵥����
		MtapppfVO[] pfVos = MtappfUtil.getMaPfVosByJKBXVo(new JKBXVO[] { bxVo });
		bxVo.setMaPfVos(pfVos);

		if (bxVo instanceof BXVO) {
			MtapppfVO[] contrastPfs = MtappfUtil.getContrastMaPfVos(new JKBXVO[] { bxVo });
			bxVo.setContrastMaPfVos(contrastPfs);
		}
	}

	private void fillUpCShareDetail(JKBXVO vo, JKBXVO oldvo) {
		List<CShareDetailVO> result = new ArrayList<CShareDetailVO>();
		List<String> pkList = new ArrayList<String>();
		CShareDetailVO[] changedCShareVO = vo.getcShareDetailVo();
		CShareDetailVO[] oldCShareVO = oldvo.getcShareDetailVo();
		if (changedCShareVO == null || oldCShareVO == null) {
			return;
		}
		// �����������޸ĺ�ɾ�����У�ֻ�� �������޸ĵķŵ������
		for (int i = 0; i < changedCShareVO.length; i++) {
			if (changedCShareVO[i].getStatus() != VOStatus.DELETED) {
				result.add(changedCShareVO[i]);
			}
			pkList.add(changedCShareVO[i].getPrimaryKey());
		}
		for (int i = 0; i < oldCShareVO.length; i++) {
			if (!pkList.contains(oldCShareVO[i].getPrimaryKey())) {
				oldCShareVO[i].setStatus(VOStatus.UNCHANGED);
				result.add(oldCShareVO[i]);
			}
		}
		vo.setcShareDetailVo(result.toArray(new CShareDetailVO[] {}));
	}

	/**
	 * ����ҵ��ҳǩ
	 * 
	 * @param vo
	 * @param oldvo
	 */
	private void fillUpChildren(JKBXVO vo, JKBXVO oldvo) {
		List<BXBusItemVO> result = new ArrayList<BXBusItemVO>();
		oldvo = (JKBXVO)oldvo.clone();
		List<String> pkList = new ArrayList<String>();
		BXBusItemVO[] changedChildren = vo.getChildrenVO();
		BXBusItemVO[] oldChildren = oldvo.getChildrenVO();

		// �����������޸ĺ�ɾ�����У�ֻ�� �������޸ĵķŵ������
		for (int i = 0; i < changedChildren.length; i++) {
			if (changedChildren[i].getStatus() != VOStatus.DELETED) {
				result.add(changedChildren[i]);
			}
			pkList.add(changedChildren[i].getPrimaryKey());
		}
		for (int i = 0; i < oldChildren.length; i++) {
			if (!pkList.contains(oldChildren[i].getPrimaryKey())) {
				oldChildren[i].setStatus(VOStatus.UNCHANGED);
				result.add(oldChildren[i]);
			}

		}
		
		Collections.sort(result, new Comparator<BXBusItemVO>(){
			@Override
			public int compare(BXBusItemVO item1, BXBusItemVO item2) {
				if(item1.getRowno() == null && item2.getRowno() == null){
					return 0;
				}else if(item1.getRowno() != null && item2.getRowno() == null){
					return -1;
				}else if(item1.getRowno() == null && item2.getRowno() != null){
					return 1;
				}
				return item1.getRowno().compareTo(item2.getRowno());
			}
		});
		
		vo.setChildrenVO(result.toArray(new BXBusItemVO[] {}));
	}

	public MessageVO[] unAudit(JKBXVO[] vos) throws BusinessException {

		if (vos == null || vos.length < 1)
			return null;

		MessageVO[] msgs = new MessageVO[vos.length];

		compareTs(vos);

		try {
			for (int i = 0; i < vos.length; i++) {

				try {
					unAuditBack(vos[i]);

					msgs[i] = new MessageVO(vos[i], ActionUtils.UNAUDIT);
				} catch (BusinessException e) {
					msgs[i] = new MessageVO(vos[i], ActionUtils.UNAUDIT, false, e.getMessage());
					ExceptionHandler.handleException(e);
				}
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return msgs;

	}

	private void unAuditBack(JKBXVO bxvo) throws BusinessException {
		// �������Ϣ
		bxvo = retriveItems(bxvo);
		JKBXHeaderVO headerVO = bxvo.getParentVO();
		VOStatusChecker.checkUnAuditStatus(headerVO);
		new VOChecker().checkUnAudit(bxvo);
		

		// �ж�CMP��Ʒ�Ƿ�����
		boolean isCmpInstalled = isCmpInstall(headerVO);
		BusiStatus billStatus = SettleUtil.getBillStatus(headerVO, false, BusiStatus.Save);
		
		if (billStatus.equals(BusiStatus.Deleted)) {
			// û�н�����Ϣ�ĵ���ֱ�ӷ���Ч
			unSettle(new JKBXVO[] { bxvo });
			// ɾ��ƾ֤
			effectToFip(bxvo, MESSAGE_UNSETTLE);
		} else {
			UFDate shrq = headerVO.getShrq() == null ? null : headerVO.getShrq().getDate();
			if (!isCmpInstalled) {
				unSettle(new JKBXVO[] { bxvo });
				// ɾ��ƾ֤
				effectToFip(bxvo, MESSAGE_UNSETTLE);
			} else {
				// ��װ�˽���ķ����
				new ErForCmpBO().invokeCmp(bxvo, shrq, billStatus);
			}
		}

		headerVO.setSxbz(BXStatusConst.SXBZ_NO);
		headerVO.setDjzt(Integer.valueOf(BXStatusConst.DJZT_Saved));

		// begin--��ս����ˣ���������
		headerVO.setJsr(null);
		headerVO.setJsrq(null);
		headerVO.shrq_show = null;
		headerVO.setPayflag(BXStatusConst.PAYFLAG_None);
		headerVO.setPayman(null);
		headerVO.setPaydate(null);
		headerVO.setVouchertag(null);
		
		//����������������ʱ�������
		if (headerVO.getApprover() == null 
				|| headerVO.getApprover().equals(INCSystemUserConst.NC_USER_PK)) {
			headerVO.setApprover(null);
			headerVO.setShrq(null);
		}
		
		try {
			beforeActInf(bxvo, MESSAGE_UNAUDIT);

			getJKBXDAO().update(new JKBXHeaderVO[] { headerVO },
					new String[] { JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ, JKBXHeaderVO.SPZT,JKBXHeaderVO.VOUCHERTAG 
					,JKBXHeaderVO.APPROVER, JKBXHeaderVO.SHRQ});

			// ���¼��س����б��壨������������Ч���ڣ�
			if (bxvo.getContrastVO() != null && bxvo.getContrastVO().length > 0) {
				Collection<BxcontrastVO> contrasts = queryContrasts(bxvo.getParentVO());
				bxvo.setContrastVO(contrasts.toArray(new BxcontrastVO[] {}));
			}
			// ���¼��غ���Ԥ����ϸ
			if (bxvo.getAccruedVerifyVO() != null && bxvo.getAccruedVerifyVO().length > 0) {
				Collection<AccruedVerifyVO> accruedVerifyVOs = queryAccruedVerifyVOS(bxvo.getParentVO());
				bxvo.setAccruedVerifyVO(accruedVerifyVOs.toArray(new AccruedVerifyVO[] {}));
			}

			afterActInf(bxvo, MESSAGE_UNAUDIT);

		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private JKBXVO retriveItems(JKBXVO bxvo) throws BusinessException {

		if ((bxvo.getChildrenVO() == null || bxvo.getChildrenVO().length == 0)) {
			JKBXVO resultBxvo = new ArapBXBillPrivateImp().retriveItems(bxvo.getParentVO());
			bxvo.setChildrenVO(resultBxvo.getChildrenVO());
			bxvo.setcShareDetailVo(resultBxvo.getcShareDetailVo());
			bxvo.setContrastVO(resultBxvo.getContrastVO());
			bxvo.setAccruedVerifyVO(resultBxvo.getAccruedVerifyVO());
		}

		return bxvo;
	}

	public MessageVO[] audit(JKBXVO[] vos) throws BusinessException {

		if (vos == null || vos.length < 1)
			return null;

		MessageVO[] msgs = new MessageVO[vos.length];

		compareTs(vos);

		try {
			for (int i = 0; i < vos.length; i++) {

				auditBack(vos[i]);

				msgs[i] = new MessageVO(vos[i], ActionUtils.AUDIT);
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

		return msgs;
	}

	/**
	 * ��ˣ�����״̬תΪ����˻���ǩ��״̬
	 * 
	 * @param bxvo
	 * @throws BusinessException
	 * @throws SQLException
	 */
	private void auditBack(JKBXVO bxvo) throws BusinessException, SQLException {

		bxvo = retriveItems(bxvo);

		JKBXHeaderVO headerVO = bxvo.getParentVO();

		// ���������ȫ�����������������֧��״̬Ӧ������Ϊ��ȫ������
		if (!headerVO.isAdjustBxd()&&headerVO.zfybje.doubleValue() == 0 && headerVO.hkybje.doubleValue() == 0) {
			headerVO.setPayflag(BXStatusConst.ALL_CONTRAST);
		}
		
		if(headerVO.getApprover() == null){//�����Ϊ��ʱ������Ĭ��ֵ
			headerVO.setApprover(INCSystemUserConst.NC_USER_PK);
			headerVO.setShrq(AuditInfoUtil.getCurrentTime());
		}
		
		VOStatusChecker.checkAuditStatus(headerVO, headerVO.getShrq());
		
		headerVO.setSpzt(IPfRetCheckInfo.PASSING);
		headerVO.setDjzt(Integer.valueOf(BXStatusConst.DJZT_Verified));
		
		beforeActInf(bxvo, MESSAGE_AUDIT);
		// ������ֶ�
		String[] updateFields = new String[] { JKBXHeaderVO.SPZT, JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ,
				JKBXHeaderVO.APPROVER, JKBXHeaderVO.SHRQ ,JKBXHeaderVO.VOUCHERTAG};
		
		dealToFip(bxvo, headerVO, updateFields);
		
		try {
			// ����Զ����㴦��
			if (SettleUtil.isAutoJS(headerVO)) {
				// �Ƿ�װ�ֽ�
				boolean iscmpused = BXUtil.isProductInstalled(headerVO.getPk_group(), BXConstans.TM_CMP_FUNCODE);
				if (iscmpused) {
					List<JKBXHeaderVO> headerVOs = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryHeadersByPrimaryKeys(new String[] { headerVO.getPk_jkbx() }, headerVO.getDjdl());
					headerVO.setVouchertag(headerVOs.get(0).getVouchertag());// ���õ�ԭ���������У�����ʱ���õ�������
					headerVO.setPayflag(headerVOs.get(0).getPayflag());
					headerVO.setPaydate(headerVOs.get(0).getPaydate());
					headerVO.setPayman(headerVOs.get(0).getPayman());

					// ֧���ɹ�����ʾ�Ѿ��Զ����㣬�Զ������Ϊ�ֹ����㣬��֧�����
					if (headerVO.getPayflag() != null && headerVO.getPayflag().intValue() == BXStatusConst.PAYFLAG_PayFinish) {
						updateFields = new String[] { JKBXHeaderVO.SPZT, JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ, JKBXHeaderVO.APPROVER, JKBXHeaderVO.SHRQ };
					}
				} else {
					// û�а�װ�ֽ��Զ����㣬��֧��״̬����Ϊ֧�����
					headerVO.setPayflag(BXStatusConst.PAYFLAG_PayFinish);
					headerVO.setPaydate(new UFDate(InvocationInfoProxy.getInstance().getBizDateTime()));
					headerVO.setPayman(InvocationInfoProxy.getInstance().getUserId());
				}
			}
			
			getJKBXDAO().update(new JKBXHeaderVO[] { headerVO }, updateFields);
			
			// ���¼��س����б��壨������������Ч���ڣ�
			if (bxvo.getContrastVO() != null && bxvo.getContrastVO().length > 0) {
				Collection<BxcontrastVO> contrasts = queryContrasts(bxvo.getParentVO());
				bxvo.setContrastVO(contrasts.toArray(new BxcontrastVO[] {}));
			}
			// ���¼��غ���Ԥ����ϸ
			if (bxvo.getAccruedVerifyVO() != null && bxvo.getAccruedVerifyVO().length > 0) {
				Collection<AccruedVerifyVO> accruedVerifyVOs = queryAccruedVerifyVOS(bxvo.getParentVO());
				bxvo.setAccruedVerifyVO(accruedVerifyVOs.toArray(new AccruedVerifyVO[] {}));
			}
			afterActInf(bxvo, MESSAGE_AUDIT);

		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	/**
	 * �������ƽ̨����Ҫ�����ֽ����Ĳ���
	 * 
	 * @param bxvo
	 * @param headerVO
	 * @param isAutoSign
	 * @param isCmpInstalled
	 * @param updateFields
	 * @throws DAOException
	 * @throws SQLException
	 * @throws BusinessException
	 */
	private void dealToFip(JKBXVO bxvo, JKBXHeaderVO headerVO, String[] updateFields) throws DAOException, SQLException, BusinessException {

		// �����Ƿ��Զ�ǩ��(�ܲ�������)
		boolean isAutoSign = SettleUtil.isAutoSign(headerVO);

		// �ж�CMP��Ʒ�Ƿ�����
		boolean isCmpInstalled = isCmpInstall(headerVO);

		// �Ƿ���㴫���ƽ̨
		boolean isJsToFip = SettleUtil.isJsToFip(headerVO);

		// ȫ�����������͵��������⴦��ƾ֤��־�ֶ�
		if ((headerVO.getVouchertag() == null && (headerVO.getPayflag() != null && headerVO.getPayflag() == BXStatusConst.ALL_CONTRAST)) || headerVO.isAdjustBxd()) {
			headerVO.setVouchertag(BXStatusConst.SXFlag);
		}

		// �Ƿ��н�����Ϣ(����֧�����������ж�)
		BusiStatus billStatus = SettleUtil.getBillStatus(headerVO, false, BusiStatus.Audit);

		if (isAutoSign) {
			// 1.״̬��Ϊ����ˣ����㵥��
			headerVO.setJsr(headerVO.getApprover());
			headerVO.setJsrq(headerVO.getShrq().getDate());
			// �Զ�ǩ��
			headerVO.setDjzt(Integer.valueOf(BXStatusConst.DJZT_Sign));
			
			if (!isJsToFip && headerVO.getVouchertag() == null) {
				headerVO.setVouchertag(BXStatusConst.SXFlag);// �Զ�ǩ��ʱ���ø��ֶ�
			}

			getJKBXDAO().update(new JKBXHeaderVO[] { headerVO }, updateFields);

			// û�н�����Ϣ�ĵ���ֱ��ǩ����Ч
			if (billStatus.equals(BusiStatus.Deleted)) {
				autoSignDeal(bxvo, headerVO);
			} else {
				if (isCmpInstalled) {
					new ErForCmpBO().invokeCmp(bxvo, headerVO.getShrq().getDate(), billStatus);
				} else {
					autoSignDeal(bxvo, headerVO);
				}
			}
			
			// ��Ч��־
			headerVO.setSxbz(Integer.valueOf((BXStatusConst.SXBZ_VALID)));
		} else {
			// û�н�����Ϣ�ĵ���ֱ��ǩ����Ч
			if (billStatus.equals(BusiStatus.Deleted)) {
				notAutoSignDeal(bxvo, headerVO, isJsToFip);
			} else {
				if (isCmpInstalled) {
					new ErForCmpBO().invokeCmp(bxvo, headerVO.getShrq().getDate(), billStatus);
				} else {
					notAutoSignDeal(bxvo, headerVO, isJsToFip);
				}
			}
		}
	}
	/**
	 * �����Զ�ǩ��ʱ��û�н�����Ϣ��û�а�װ����ʱ�Ĵ���
	 * @param bxvo
	 * @param headerVO
	 * @param param
	 * @throws BusinessException
	 */
	private void notAutoSignDeal(JKBXVO bxvo, JKBXHeaderVO headerVO,
			boolean isJsToFip) throws BusinessException {
		settle(headerVO.getApprover(), headerVO.getShrq().getDate(), bxvo);
		// �����ƽ̨
		if((headerVO.getVouchertag()==null || headerVO.getVouchertag()==BXStatusConst.SXFlag || headerVO.getVouchertag()==BXStatusConst.ZGDeal) && (
				!isJsToFip || 
				(headerVO.getPayflag()!=null && headerVO.getPayflag() == BXStatusConst.ALL_CONTRAST)
				||headerVO.isAdjustBxd())){
			bxvo.getParentVO().setVouchertag(BXStatusConst.SXFlag);
			effectToFip(bxvo, MESSAGE_SETTLE);
		}
	}
	/**
	 * �Զ�ǩ��ʱ��û�н�����Ϣ��û�а�װ����ʱ�Ĵ���
	 * @param bxvo
	 * @param headerVO
	 * @param param
	 * @throws BusinessException
	 */
	private void autoSignDeal(JKBXVO bxvo, JKBXHeaderVO headerVO)
			throws BusinessException {
		settle(headerVO.getApprover(), headerVO.getShrq().getDate(), bxvo);
		// �����ƽ̨
		effectToFip(bxvo, MESSAGE_SETTLE);
	}
	
	/**
	 * ����Ч + ��ǩ��
	 * @param vos ����VOs
	 * @param isCmpUnEffectiveCall
	 *            �Ƿ���㷴��Ч����
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO[] unSettle(JKBXVO[] vos, boolean isCmpUnEffectiveCall) throws BusinessException {
		vos = unSignVos(vos);
		vos = unEffectVos(vos);
		return vos;
	}
	
	/**
	 * ��ǩ��
	 * 
	 * @param vos
	 *            ����VOs
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO[] unSignVos(JKBXVO[] vos) throws BusinessException {
		compareTs(vos);
		try {
			JKBXHeaderVO[] headers = new JKBXHeaderVO[vos.length];
			for (int i = 0; i < vos.length; i++) {
				JKBXHeaderVO parentVO = vos[i].getParentVO();
				headers[i] = parentVO;
				headers[i].setJsrq(null);
				headers[i].setJsr(null);
				headers[i].setDjzt(BXStatusConst.DJZT_Verified);
			}

			updateHeaders(headers, new String[] { JKBXHeaderVO.JSR, JKBXHeaderVO.JSRQ ,JKBXHeaderVO.DJZT});
			return vos;

		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		}
		return null;
	}
	
	/**
	 * ����Ч
	 * @param vos ����VOS
	 * @param isCmpUnEffectiveCall
	 *            �Ƿ���㷴��Ч����
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO[] unEffectVos(JKBXVO[] vos) throws BusinessException {
		compareTs(vos);

		try {
			String[] keys = new String[vos.length];
			JKBXHeaderVO[] headers = new JKBXHeaderVO[vos.length];

			for (int i = 0; i < vos.length; i++) {
				JKBXHeaderVO parentVO = vos[i].getParentVO();
				keys[i] = parentVO.getPk_jkbx();

				vos[i].setBxoldvo((JKBXVO) vos[i].clone());
				headers[i] = parentVO;
				// ������Ϣ
				addBxExtralInfo(vos[i]);
				fillUpMapf(vos[i]);
			}

			beforeActInf(vos, MESSAGE_UNSETTLE);
			
			for (int i = 0; i < vos.length; i++) {
				headers[i].setSxbz(BXStatusConst.SXBZ_NO);
			}

			String inStr = SqlUtils.getInStr("er_jsconstras.pk_bxd", keys);

			String sqlJS = " er_jsconstras  where " + inStr;

			// У�鷴ǩ����Ϣ
			checkUnSettle(keys, headers);

			Collection<JsConstrasVO> jsContrasVOs = getJKBXDAO().queryJsContrastByWhereSql(sqlJS);

			if (jsContrasVOs != null) {

				// �������ɵ��ո��������ݣ���Ӧ��Ӧ����������0��ʾӦ�� 1��ʾӦ��
				List<String> vouchid1 = new ArrayList<String>();
				List<String> vouchid0 = new ArrayList<String>();
				JsConstrasVO jsconvo = null;
				for (Iterator<JsConstrasVO> iter = jsContrasVOs.iterator(); iter.hasNext();) {
					jsconvo = iter.next();

					String vouchid = jsconvo.getPk_jsd();
					Integer billflag = jsconvo.getBillflag();
					if (billflag.intValue() == 1) {
						vouchid1.add(vouchid);
					} else {
						vouchid0.add(vouchid);
					}
				}

				if (vouchid1 != null && vouchid1.size() != 0) {
					// ͨ����Ӧ��Ӧ������pk�ҵ���Ӧ�ľۺ�VO��֮��ɾ����Ӧ��Ӧ����
					IArapPayableBillPubService apBillService = (IArapPayableBillPubService) NCLocator.getInstance()
							.lookup(IArapPayableBillPubService.class.getName());
					IArapPayableBillPubQueryService apQryService = (IArapPayableBillPubQueryService) NCLocator
							.getInstance().lookup(IArapPayableBillPubQueryService.class.getName());
					AggPayableBillVO[] payablevo = apQryService.findBillByPrimaryKey(vouchid1.toArray(new String[] {}));
					if (payablevo != null) {
						for (AggPayableBillVO ss : payablevo) {
							BaseBillVO svo = (BaseBillVO) ss.getParentVO();
							if (svo.getBillstatus().intValue() != ARAPBillStatus.TEMPSAVE.VALUE.intValue()
									&& svo.getBillstatus().intValue() != ARAPBillStatus.SAVE.VALUE.intValue()) {
								throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
										.getStrByID("expensepub_0", "02011002-0061")/*
																					 * @
																					 * res
																					 * "�������������Ѿ������ݴ�򱣴�̬�� ���ܽ��з���Ч����"
																					 */);
							}
						}
						apBillService.delete(payablevo);
					}
				}
				if (vouchid0 != null && vouchid0.size() != 0) {
					// ͨ����Ӧ��Ӧ�յ���pk�ҵ���Ӧ�ľۺ�VO��֮��ɾ����Ӧ��Ӧ�յ�
					IArapReceivableBillPubService billBo0 = (IArapReceivableBillPubService) NCLocator.getInstance()
							.lookup(IArapReceivableBillPubService.class.getName());
					IArapReceivableBillPubQueryService billquery0 = (IArapReceivableBillPubQueryService) NCLocator
							.getInstance().lookup(IArapReceivableBillPubQueryService.class.getName());
					AggReceivableBillVO[] recvo = billquery0.findBillByPrimaryKey(vouchid0.toArray(new String[] {}));
					if (recvo != null) {
						for (AggReceivableBillVO ss : recvo) {
							BaseBillVO svo = (BaseBillVO) ss.getParentVO();
							if (svo.getBillstatus().intValue() != ARAPBillStatus.TEMPSAVE.VALUE.intValue()
									&& svo.getBillstatus().intValue() != ARAPBillStatus.SAVE.VALUE.intValue()) {
								throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
										.getStrByID("expensepub_0", "02011002-0061")/*
																					 * @
																					 * res
																					 * "�������������Ѿ������ݴ�򱣴�̬�� ���ܽ��з���Ч����"
																					 */);
							}
						}
						billBo0.delete(recvo);
					}
				}

				getJKBXDAO().delete(jsContrasVOs.toArray(new JsConstrasVO[] {}));
			}

			updateHeaders(headers, new String[] {JKBXHeaderVO.SXBZ });
			
			// ����Ԥ����ϸȡ����Ч����
			new BxVerifyAccruedBillBO().uneffectAccruedVerifyVOs(vos);

			afterActInf(vos, MESSAGE_UNSETTLE);

			return vos;

		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}
		return null;
	}
	
	/**
	 * @param head
	 * @throws BusinessException
	 * 
	 *             ��̨���з��������
	 */
	public void unSettleBack(JKBXHeaderVO head) throws BusinessException {

		VOStatusChecker.checkUnSettleStatus(head);

		JKBXVO bxvo = VOFactory.createVO(head);

		head.setDjzt(BXStatusConst.DJZT_Verified);
		head.setSxbz(BXStatusConst.SXBZ_NO);
		unSettle(new JKBXVO[] { bxvo });

	}

	/**
	 * ǩ��+��Ч
	 * �˷���������Ч�������ƣ�Ԥ����ƣ����ո�
	 * 
	 * @param jsr
	 * @param jsrq
	 * @param vo
	 * @throws BusinessException
	 * 
	 */
	public void settle(String jsr, UFDate jsrq, JKBXVO vo) throws BusinessException {
		//ί�и���ʱ��ǩ��ʱ��ֻ��дǩ����Ϣ�����ݲ�����Ч
		signVo(jsr, jsrq,vo);//ǩ��
		effectVo(vo);//��Ч
	}
	
	/**
	 * ����ǩ��
	 * 
	 * @param jsr ǩ����
	 * @param jsrq ǩ������
	 * @param vo
	 * @throws BusinessException
	 * 
	 */
	public JKBXVO signVo(String jsr, UFDate jsrq, JKBXVO vo) throws BusinessException {
		// У��ts
		compareTs(new JKBXVO[] { vo });

		JKBXHeaderVO head = vo.getParentVO();
		VOStatusChecker.checkSettleStatus(head, jsrq);

		head.setJsr(jsr);
		head.setJsrq(jsrq);
		head.setDjzt(BXStatusConst.DJZT_Sign);

		// ����vo��Ϣ
		updateHeaders(new JKBXHeaderVO[] { head }, new String[] { JKBXHeaderVO.DJZT, JKBXHeaderVO.JSR, JKBXHeaderVO.JSRQ });
		return vo;
	}
	
	/**
	 * ������Ч
	 * 
	 * @param vo
	 *            ����VO
	 * @throws BusinessException
	 * 
	 */
	public JKBXVO effectVo(JKBXVO vo) throws BusinessException {
		// У��ts
		compareTs(new JKBXVO[] { vo });

		JKBXHeaderVO head = vo.getParentVO();

		// ������Ϣ
		addBxExtralInfo(vo);
		fillUpMapf(vo);

		head.setDjzt(BXStatusConst.DJZT_Sign);
		head.setSxbz(BXStatusConst.SXBZ_VALID);

		// ����ǰ�������(Ԥ�����)
		beforeActInf(new JKBXVO[] { vo }, MESSAGE_SETTLE);

		// ����vo��Ϣ
		updateHeaders(new JKBXHeaderVO[] { head }, new String[] { JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ });

		// ����Ԥ����ϸ��Ч����
		new BxVerifyAccruedBillBO().effectAccruedVerifyVOs(vo);

		// �����������
		afterActInf(new JKBXVO[] { vo }, MESSAGE_SETTLE);

		// ���ո�
		transferArap(vo);

		return vo;
	}
	
	/**
	 * �ύ
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO commitVO(JKBXVO vo) throws BusinessException {
		if (vo == null) {
			return null;
		}

		// ����,�汾У��
		compareTs(new JKBXVO[] { vo });

		// У��
		VOStatusChecker.checkCommitStatus(vo.getParentVO());
		
		// ��������״̬
		vo.getParentVO().setSpzt(IBillStatus.COMMIT);

		// ����vo��Ϣ
		updateHeaders(new JKBXHeaderVO[] { vo.getParentVO() }, new String[] { JKBXHeaderVO.SPZT });

		// ����
		return vo;
	}
	
	/**
	 * �ջ�
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO recallVO(JKBXVO vo) throws BusinessException {
		if (vo == null) {
			return null;
		}

		// ����,�汾У��
		compareTs(new JKBXVO[] { vo });

		//У��
		VOStatusChecker.checkRecallStatus(vo.getParentVO());
		
		// ��������״̬
		vo.getParentVO().setSpzt(IBillStatus.FREE);

		// ����vo��Ϣ
		updateHeaders(new JKBXHeaderVO[] { vo.getParentVO() }, new String[] { JKBXHeaderVO.SPZT });

		// ����
		return vo;
	}
	
	/**
	 * ��������
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO invalidBill(JKBXVO vo) throws BusinessException {
		if (vo == null) {
			return null;
		}
		
		// ����,�汾У��
		compareTs(new JKBXVO[] { vo });
		
		//У�飬������ĵ��ݿ��Խ��з���
		new VOChecker().checkInvalid(vo);
		// ����ǰ�������
		beforeActInf(new JKBXVO[] { vo }, MESSAGE_INVALID);
		
		//����״̬
		vo.getParentVO().setDjzt(BXStatusConst.DJZT_Invalid);
		// ȡ�������¼���Ϊ�޸�ʱ��
		AuditInfoUtil.updateData(vo.getParentVO());
		
		//���뵥��Ϣ���
		vo.getParentVO().setPk_item(null);
		
		//����
		updateHeaders(new JKBXHeaderVO[] { vo.getParentVO() }, new String[] { JKBXHeaderVO.DJZT, JKBXHeaderVO.MODIFIER, JKBXHeaderVO.MODIFIEDTIME,JKBXHeaderVO.PK_ITEM});
		
		//���崦��
		if(vo.getChildrenVO() != null && vo.getChildrenVO().length > 0){
			BXBusItemVO[] childrenVos = vo.getChildrenVO();
			for(BXBusItemVO item :childrenVos){
				//���뵥����һ��Ҫ�ŵ�ʱ��ǰ�����¼��д���
				item.setPk_item(null);//���뵥��Ϣ���
				item.setPk_mtapp_detail(null);
			}
			new BXBusItemBO().update(childrenVos);
		}
		
		// �ж�CMP��Ʒ�Ƿ�����
		boolean isCmpInstalled = BXZbBO.isCmpInstall(vo.getParentVO());

		if (isCmpInstalled) {
			//ɾ�����ݵĽ�����Ϣ
			new ErForCmpBO().invokeCmp(vo, vo.getParentVO().getDjrq(), BusiStatus.Deleted);
			vo.setContrastVO(null);
		}

		// ɾ�����������Ϣ
		try {
			new ContrastBO().deleteByPK_bxd(new String[] { vo.getParentVO().getPk_jkbx() });
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}

		// ɾ���������� Ԥ����ϸ
		new BxVerifyAccruedBillBO().deleteByBxdPks(vo.getParentVO().getPk_jkbx());
		vo.setAccruedVerifyVO(null);
		
		//�������
		retriveItems(vo);
		
		// ����ǰ�������
		afterActInf(new JKBXVO[] { vo }, MESSAGE_INVALID);
		
		// ɾ��������
		NCLocator.getInstance().lookup(IWorkflowMachine.class).deleteCheckFlow(vo.getParentVO().getDjlxbm(), vo.getParentVO().getPrimaryKey(), vo, InvocationInfoProxy.getInstance().getUserId());
		return vo;
	}

	/**
	 * 
	 * ���ո� ��������
	 */
	private void transferArap(JKBXVO vo) throws BusinessException {
		JKBXHeaderVO headVo = vo.getParentVO();
		String pk_group = headVo.getPk_group();
		if(headVo.isAdjustBxd()){
			// ��������Ϊ���õ����ĵ��ݣ�����������
			return ;
		}
		// �ж��Ƿ�װӦ��Ӧ����Ʒ�����򲻽���ת��������
		boolean isARused = BXUtil.isProductInstalled(pk_group, BXConstans.FI_AR_FUNCODE);
		boolean isAPused = BXUtil.isProductInstalled(pk_group, BXConstans.FI_AP_FUNCODE);

		// ����ת�ո�����
		if (isARused && isAPused) {
			new ErPFUtil().doTransferArap(vo);
		}
	}

	/**
	 * @param vo
	 * @throws BusinessException
	 * 
	 *             ���ӳ������Ϣ�Ͷ�Ӧ�Ľ�����Ϣ
	 */
	private void addBxExtralInfo(JKBXVO vo) throws BusinessException {
		if (vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			BxcontrastVO[] contrast = vo.getContrastVO();
			if (vo.getContrastVO() == null || vo.getContrastVO().length == 0) {
				// ���������Ϣ
				Collection<BxcontrastVO> contrasts = queryContrasts(vo.getParentVO());
				contrast = contrasts.toArray(new BxcontrastVO[] {});
				vo.setContrastVO(contrast);
			}

			if (vo.getcShareDetailVo() == null || vo.getcShareDetailVo().length == 0) {
				// �����̯��ϸ
				Collection<CShareDetailVO> cShares = queryCSharesVOS(new JKBXHeaderVO[] { vo.getParentVO() });
				vo.setcShareDetailVo(cShares.toArray(new CShareDetailVO[] {}));
			}
			if (vo.getAccruedVerifyVO() == null || vo.getAccruedVerifyVO().length == 0) {
				// �������Ԥ����ϸ
				Collection<AccruedVerifyVO> accvvos =queryAccruedVerifyVOS( vo.getParentVO() );
				vo.setAccruedVerifyVO(accvvos.toArray(new AccruedVerifyVO[] {}));
			}

			// �����Ӧ�Ľ���Ϣ
			List<String> jkdKeys = new ArrayList<String>();
			if (contrast != null && contrast.length != 0) {
				for (BxcontrastVO contr : contrast) {
					jkdKeys.add(contr.getPk_jkd());
				}
			}

			if (jkdKeys.size() > 0) {
				List<JKBXHeaderVO> jkds = new BXZbBO().queryHeadersByPrimaryKeys(jkdKeys.toArray(new String[] {}),
						BXConstans.JK_DJDL);
				Map<String, JKBXHeaderVO> jkdMap = new HashMap<String, JKBXHeaderVO>();
				if (jkds != null) {
					for (JKBXHeaderVO jkd : jkds) {
						jkdMap.put(jkd.getPrimaryKey(), jkd);
					}
				}
				vo.setJkdMap(jkdMap);
			}
		}
	}

	/**
	 * ���˵��ݺ�
	 * 
	 * @param bxvo
	 * @throws BusinessException
	 */

	public void returnBillCode(JKBXVO bxvo) throws BusinessException {
		FinanceBillCodeUtils utils = new FinanceBillCodeUtils(new FinanceBillCodeInfo(JKBXHeaderVO.DJDL,
				JKBXHeaderVO.DJBH, JKBXHeaderVO.PK_GROUP, JKBXHeaderVO.PK_ORG, bxvo.getParentVO().getTableName()));
		utils.returnBillCode(new AggregatedValueObject[] { bxvo });

	}

	/**
	 * @param headers
	 * @throws DAOException
	 * @throws SQLException
	 * 
	 *             ����DAO����ͬʱ���½��ͱ�������������Ҫ�ֿ�����.
	 */
	public void updateHeaders(JKBXHeaderVO[] headers, String[] fields) throws BusinessException {
		Map<String, List<JKBXHeaderVO>> voMap = splitJkbx(headers);
		Collection<List<JKBXHeaderVO>> values = voMap.values();
		List<JKBXHeaderVO> list = new LinkedList<JKBXHeaderVO>();
		for (Iterator<List<JKBXHeaderVO>> iter = values.iterator(); iter.hasNext();) {
			List<JKBXHeaderVO> lvos = iter.next();
			list.addAll(lvos);
		}
		try {
			getJKBXDAO().update(list.toArray(new JKBXHeaderVO[list.size()]), fields);
		} catch (SQLException e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}

	/**
	 * @param headers
	 * @return �����ݴ������VO����
	 */
	private Map<String, List<JKBXHeaderVO>> splitJkbx(JKBXHeaderVO[] headers) {
		Map<String, List<JKBXHeaderVO>> map = new HashMap<String, List<JKBXHeaderVO>>();
		for (int i = 0; i < headers.length; i++) {
			JKBXHeaderVO headerVO = headers[i];
			String djdl = headerVO.getDjdl();
			if (map.containsKey(djdl)) {
				map.get(djdl).add(headerVO);
			} else {
				List<JKBXHeaderVO> list = new ArrayList<JKBXHeaderVO>();
				list.add(headerVO);
				map.put(djdl, list);
			}
		}
		return map;
	}

	/**
	 * @param keys
	 *            //���е�������
	 * @param jkdKeys
	 *            //��������
	 * @param headers
	 *            //���ݱ�ͷ
	 * @throws SQLException
	 * @throws DAOException
	 * @throws BusinessException
	 */
	private void checkUnSettle(String[] keys, JKBXHeaderVO[] headers) throws BusinessException {

		try {
			if (keys != null) {// У����Ƿ��Ѿ������˳���

				String sqlJK = SqlUtils.getInStr(BxcontrastVO.PK_JKD, keys);

				Collection<BxcontrastVO> jkContrasVOs = getJKBXDAO().retrieveContrastByClause(sqlJK);

				if (jkContrasVOs != null && jkContrasVOs.size() > 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
							"UPP2011-000384")/*
											 * @res "���ѽ����˳�������,�޷����з�ǩ��!"
											 */);
				}

				for (int i = 0; i < headers.length; i++) {
					JKBXHeaderVO bxHeaderVO = headers[i];
					if (BXConstans.BX_DJDL.equals(bxHeaderVO.getDjdl())) {
						CostShareVO shareVo = NCLocator.getInstance().lookup(IErmCostShareBillQuery.class)
								.queryCShareVOByBxVoHead(bxHeaderVO, UFBoolean.FALSE);
						if (shareVo != null) {
							throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
									"upp2012v575_0", "0upp2012V575-0063")/*
																		 * @res
																		 * "�������ѽ�ת���޷����з�ǩ��!"
																		 */);
						}
					}
				}

			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public static String MESSAGE_TEMP_SAVE = "bx-temp-save";

	public static String MESSAGE_SAVE = "bx-save";

	public static String MESSAGE_UPDATE = "bx-update";

	public static String MESSAGE_AUDIT = "bx-audit";

	public static String MESSAGE_UNAUDIT = "bx-unaudit";

	public static String MESSAGE_DELETE = "bx-delete";

	public static String MESSAGE_SETTLE = "bx-settle";

	public static String MESSAGE_UNSETTLE = "bx-unsettle";
	
	public static String MESSAGE_INVALID = "bx-invalid";

	public static int MESSAGE_NOTSEND = -1;

	/**
	 * @ ���ո��Լ����ƽ̨���� @
	 * */

	private void sendMessage(JKBXVO vo, int message) throws BusinessException {
		try {
			JKBXHeaderVO headVO = VOFactory.createHeadVO(vo.getParentVO().getDjdl());
			headVO = vo.getParentVO();

			AggregatedValueObject object = createJkbxToFIPVO(vo, message);

			sendMessageToFip(headVO, vo, object, message);
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		}
	}

	public static AggregatedValueObject createJkbxToFIPVO(JKBXVO vo, int message) throws BusinessException {
		AggregatedValueObject object = null;

		JKBXHeaderVO headVo = vo.getParentVO();
		if(headVo.isAdjustBxd()){
			// ��������Ϊ���õ����ĵ��ݣ����ձ�������+��̯��ϸ������ƾ֤
			object=(JKBXVO) vo.clone();
		}else{
			JKBXVO bxvo = ErVOUtils.prepareBxvoHeaderToItemClone(vo);
			
			if (message != FipMessageVO.MESSAGETYPE_DEL) {
				object = new FipUtil().addOtherInfo(bxvo);
			}
		}
		
		return object;
	}

	/**
	 * ������ƽ̨�����ݣ� ���ţ���֯����Դϵͳ��ҵ�����ڣ�����PK���������� �Զ�������������ڻ��ƽ̨��Ҫչʾ����Ŀ
	 * 
	 * @param message
	 * */
	private void sendMessageToFip(JKBXHeaderVO headVO, JKBXVO bxvo, Object object, int message)
			throws BusinessException {

		FipRelationInfoVO reVO = new FipRelationInfoVO();
		// ����������ϢӦ�û��ƽ̨
		reVO.setPk_group(headVO.getPk_group());

		// 63�󴫻��ƾ֤��֧����λ�����д���
		reVO.setPk_org(headVO.getPk_org());
		//reVO.setRelationID(headVO.getPk());
		reVO.setRelationID(headVO.getPk()+"_"+headVO.getVouchertag());
		reVO.setPk_system(BXConstans.ERM_PRODUCT_CODE_Lower);
		
		reVO.setBusidate(headVO.getJsrq() == null ? new UFDate(InvocationInfoProxy.getInstance().getBizDateTime()) : headVO.getJsrq());
		reVO.setPk_billtype(headVO.getDjlxbm());
		reVO.setPk_operator(headVO.getOperator());
		reVO.setFreedef1(headVO.getDjbh());
		reVO.setFreedef2(headVO.getZy());
		UFDouble total = headVO.getYbje();

		// added by chendya ���ý���ֶεľ���
		total = total.setScale(Currency.getCurrDigit(headVO.getBzbm()), UFDouble.ROUND_HALF_UP);
		reVO.setFreedef3(String.valueOf(total));

		FipMessageVO messageVO = new FipMessageVO();
		messageVO.setBillVO(object);
		messageVO.setMessagetype(message);
		messageVO.setMessageinfo(reVO);
		try {
			new FipCallFacade().sendMessage(messageVO);
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);

		}
	}

	void beforeActInf(JKBXVO djzb, String message) throws BusinessException {
		beforeActInf(new JKBXVO[] { djzb }, message);
	}

	void afterActInf(JKBXVO djzb, String message) throws BusinessException {
		afterActInf(new JKBXVO[] { djzb }, message);
	}

	/**
	 * �������¼�����
	 * 
	 * @param vos
	 * @param message
	 * @throws BusinessException
	 */
	private void afterActInf(JKBXVO[] vos, String message) throws BusinessException {
		List<JKBXVO> listVOs = new ArrayList<JKBXVO>();
		for (JKBXVO vo : vos) {
			if (!vo.getParentVO().isNoOtherEffectItf()) {
				listVOs.add(vo);
			}
		}

		String eventType = null;
		if (message.equals(MESSAGE_SAVE)) {
			eventType = ErmEventType.TYPE_INSERT_AFTER;
		} else if (message.equals(MESSAGE_UPDATE)) {
			if (vos[0].getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved) {
				eventType = ErmEventType.TYPE_TEMPUPDATE_AFTER;
			} else {
				eventType = ErmEventType.TYPE_UPDATE_AFTER;
			}
		} else if (message.equals(MESSAGE_AUDIT)) {
			eventType = ErmEventType.TYPE_APPROVE_AFTER;
		} else if (message.equals(MESSAGE_UNAUDIT)) {
			eventType = ErmEventType.TYPE_UNAPPROVE_AFTER;
		} else if (message.equals(MESSAGE_DELETE)) {
			eventType = ErmEventType.TYPE_DELETE_AFTER;
		} else if (message.equals(MESSAGE_SETTLE)) {
			eventType = ErmEventType.TYPE_SIGN_AFTER;
		} else if (message.equals(MESSAGE_UNSETTLE)) {
			eventType = ErmEventType.TYPE_UNSIGN_AFTER;
		} else if (message.equals(MESSAGE_TEMP_SAVE)) {
			eventType = ErmEventType.TYPE_TEMPSAVE_AFTER;
		} else if (message.equals(MESSAGE_INVALID)) {
			eventType = ErmEventType.TYPE_INVALID_AFTER;
		} else {
			return;
		}
		// �ǳ��õ��ݷ����¼�
		if (!vos[0].getParentVO().isInit()) {
			EventDispatcher.fireEvent(new ErmBusinessEvent(BXConstans.ERM_MDID_BX, eventType, vos));
		}

		// ���ɱ���ҵ����־
		for (JKBXVO bxvo : vos) {
			if (message.equals(MESSAGE_UPDATE) || message.equals(MESSAGE_DELETE)) {
				BxBusiLogUtils.insertSmartBusiLogs(message.equals(MESSAGE_UPDATE) ? true : false, bxvo);
			}
		}
	}

	/**
	 * ������Ч�����ƽ̨
	 * 
	 * @throws BusinessException
	 */
	private void effectToFip(JKBXVO bxvo, String message) throws BusinessException {
		if (getDapMessage(bxvo.getParentVO(), message) != MESSAGE_NOTSEND) {
			sendMessage(bxvo, getDapMessage(bxvo.getParentVO(), message));
		}
	}

	/**
	 * ������Ч�����ƽ̨
	 * 
	 * @throws BusinessException
	 */
	public void effectToFip(List<JKBXVO> listVOs, String message) throws BusinessException {
		// ���ͻ��ƽ̨
		for (Iterator<JKBXVO> iter = listVOs.iterator(); iter.hasNext();) {
			effectToFip(iter.next(), message);
		}
	}

	/**
	 * Action����֮ǰ�Ķ���
	 * 
	 * @param vos
	 * @param message
	 * @throws BusinessException
	 */
	private void beforeActInf(JKBXVO[] vos, String message) throws BusinessException {
		List<JKBXVO> listVOs = new ArrayList<JKBXVO>();
		for (JKBXVO vo : vos) {
			if (!vo.getParentVO().isNoOtherEffectItf()) {
				listVOs.add(vo);
			}
		}

		// �¼�����
		String eventType = null;
		if (message.equals(MESSAGE_SAVE)) {
			eventType = ErmEventType.TYPE_INSERT_BEFORE;
		} else if (message.equals(MESSAGE_UPDATE)) {
			if (vos[0].getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved) {
				eventType = ErmEventType.TYPE_TEMPUPDATE_BEFORE;
			} else {
				eventType = ErmEventType.TYPE_UPDATE_BEFORE;
			}
		} else if (message.equals(MESSAGE_AUDIT)) {
			eventType = ErmEventType.TYPE_APPROVE_BEFORE;
		} else if (message.equals(MESSAGE_UNAUDIT)) {
			eventType = ErmEventType.TYPE_UNAPPROVE_BEFORE;
		} else if (message.equals(MESSAGE_DELETE)) {
			eventType = ErmEventType.TYPE_DELETE_BEFORE;
		} else if (message.equals(MESSAGE_SETTLE)) {
			eventType = ErmEventType.TYPE_SIGN_BEFORE;
		} else if (message.equals(MESSAGE_UNSETTLE)) {
			eventType = ErmEventType.TYPE_UNSIGN_BEFORE;
		} else if (message.equals(MESSAGE_TEMP_SAVE)) {
			eventType = ErmEventType.TYPE_TEMPSAVE_BEFORE;
		} else if (message.equals(MESSAGE_INVALID)) {
			eventType = ErmEventType.TYPE_INVALID_BEFORE;
		} else {
			return;
		}

		// �ǳ��õ��ݷ����¼�
		if (!vos[0].getParentVO().isInit()) {
			EventDispatcher.fireEvent(new ErmBusinessEvent(BXConstans.ERM_MDID_BX, eventType, vos));
		}
	}

	// �ж�ǩ��ʱ�򴫻��ƽ̨
	private int getDapMessage(JKBXHeaderVO head, String message) {
		int msg = MESSAGE_NOTSEND;

		if (message.equals(MESSAGE_SETTLE)) {
			msg = FipMessageVO.MESSAGETYPE_ADD;
		} else if (message.equals(MESSAGE_UNSETTLE)) {
			msg = FipMessageVO.MESSAGETYPE_DEL;
		} else if (head.getQcbz().equals(UFBoolean.TRUE) && message.equals(MESSAGE_SAVE)) {
			msg = FipMessageVO.MESSAGETYPE_ADD;
		} else if (head.getQcbz().equals(UFBoolean.TRUE) && message.equals(MESSAGE_DELETE)) {
			msg = FipMessageVO.MESSAGETYPE_DEL;
		}
		return msg;
	}

	public List<JKBXHeaderVO> queryHeaders(Integer start, Integer count, DjCondVO condVO) throws BusinessException {
		try {
			return getJKBXDAO().queryHeaders(start, count, condVO);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public List<JKBXHeaderVO> queryHeadersByWhereSql(String sql, String djdl) throws BusinessException {
		try {
			return getJKBXDAO().queryHeadersByWhereSql(sql, djdl);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private static void lockDJ(String pk) throws BusinessException {
		String lock = KeyLock.dynamicLock(pk);
		if (lock != null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000359"));
		}
	}

	public void compareTs(JKBXHeaderVO[] vos) throws BusinessException {
		Hashtable<String, String> ts = new Hashtable<String, String>();
		Hashtable<String, String> ts2 = new Hashtable<String, String>();
		Hashtable<String, String> ts3 = new Hashtable<String, String>();
		for (JKBXHeaderVO vo : vos) {
			if (vo.isInit()) {
				ts3.put(vo.getPk_jkbx(), vo.getTs().toString());
			} else if (vo.getDjdl().equals(BXConstans.BX_DJDL)) {
				ts.put(vo.getPk_jkbx(), vo.getTs().toString());
			} else {
				ts2.put(vo.getPk_jkbx(), vo.getTs().toString());
			}
		}
		if (ts.size() != 0)
			compareTS(ts, "er_bxzb", JKBXHeaderVO.PK_JKBX);
		if (ts2.size() != 0)
			compareTS(ts2, "er_jkzb", JKBXHeaderVO.PK_JKBX);
		if (ts3.size() != 0)
			compareTS(ts3, "er_jkbx_init", JKBXHeaderVO.PK_JKBX);
	}

	public void compareTs(JKBXVO[] vos) throws BusinessException {
		Hashtable<String, String> ts = new Hashtable<String, String>();
		Hashtable<String, String> ts2 = new Hashtable<String, String>();
		Hashtable<String, String> ts3 = new Hashtable<String, String>();

		for (JKBXVO vo : vos) {
			UFDateTime ts4 = vo.getParentVO().getTs();
			String pk_jkbx = vo.getParentVO().getPk_jkbx();

			if (ts4 == null || pk_jkbx == null)
				continue;

			if (vo.getParentVO().isInit()) {
				ts3.put(pk_jkbx, ts4.toString());
			} else if (vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
				ts.put(pk_jkbx, ts4.toString());
			} else {
				ts2.put(pk_jkbx, ts4.toString());
			}
		}

		if (ts.size() != 0)
			compareTS(ts, "er_bxzb", JKBXHeaderVO.PK_JKBX);
		if (ts2.size() != 0)
			compareTS(ts2, "er_jkzb", JKBXHeaderVO.PK_JKBX);
		if (ts3.size() != 0)
			compareTS(ts3, "er_jkbx_init", JKBXHeaderVO.PK_JKBX);
	}

	public static void compareTS(Hashtable<String, String> ts, String tableName, String pkField)
			throws BusinessException {
		String tname = null;
		Connection con = null;
		PreparedStatement stat = null;
		ResultSet rs = null;

		try {
			con = ConnectionFactory.getConnection();

			nc.bs.mw.sqltrans.TempTable tmptab = new nc.bs.mw.sqltrans.TempTable();

			tname = tmptab.createTempTable(con, "ErmTsTemp", "pk char(20),ts1 char(19)", "pk");

			String sql = "insert into " + tname + " (pk,ts1) values(?,?)";
			((CrossDBConnection) con).setAddTimeStamp(false);
			stat = con.prepareStatement(sql);

			for (Map.Entry<String, String> entry : ts.entrySet()) {
				stat.setString(1, entry.getKey());
				stat.setString(2, entry.getValue());
				stat.addBatch();
				lockDJ(entry.getKey());
			}

			stat.executeBatch();
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		} finally {
			closeDBCon(con, stat, rs);
		}

		try {
			final String sql_n = "select count(temp.pk) from " + tname + " temp inner join " + tableName
					+ " tab on temp.pk=tab." + pkField + " where temp.ts1=tab.ts";
			con = ConnectionFactory.getConnection();
			stat = con.prepareStatement(sql_n);
			rs = stat.executeQuery();
			int result = 0;
			if (rs.next())
				result = rs.getInt(1);
			
			if (result != ts.size())
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000359")/*
										 * @res "�����쳣�������Ѿ����£������²�ѯ���ݺ����"
										 */);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		} finally {
			closeDBCon(con, stat, rs);
		}
	}

	private static void closeDBCon(Connection con, PreparedStatement stat, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				ExceptionHandler.consume(e);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				ExceptionHandler.consume(e);
			}
		}
		if (stat != null) {
			try {
				stat.close();
			} catch (SQLException e) {
				ExceptionHandler.consume(e);
			}
		}
	}

	public Collection<JsConstrasVO> queryJsContrasts(JKBXHeaderVO header) throws BusinessException {

		String pk_jkbx = header.getPk_jkbx();

		Collection<JsConstrasVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveJsContrastByClause(JsConstrasVO.PK_BXD + "='" + pk_jkbx + "'");
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		return vos;
	}

	public Collection<BxcontrastVO> queryContrasts(JKBXHeaderVO parentVO) throws BusinessException {

		String pk_jkbx = parentVO.getPk_jkbx();
		String key = null;

		if (parentVO.getDjdl().equals(BXConstans.BX_DJDL)) {
			key = BxcontrastVO.PK_BXD;
		} else {
			key = BxcontrastVO.PK_JKD;
		}

		Collection<BxcontrastVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveContrastByClause(key + "='" + pk_jkbx + "'");
			fillUpHkJe(vos);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;

	}

	public Collection<BxcontrastVO> queryContrasts(JKBXHeaderVO[] parentVO, String djdl) throws BusinessException {
		String key = null;

		if (djdl.equals(BXConstans.BX_DJDL)) {
			key = BxcontrastVO.PK_BXD;
		} else {
			key = BxcontrastVO.PK_JKD;
		}

		Collection<BxcontrastVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveContrastByClause(SqlUtils.getInStr(key, parentVO, JKBXHeaderVO.PK_JKBX));
			fillUpHkJe(vos);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;
	}

	private void fillUpHkJe(Collection<BxcontrastVO> vos) {
		if (vos != null && vos.size() > 0) {// ������Ϣ�Ļ��������ڼ������ԣ�������Ҫ�����ﲹ��
			for (BxcontrastVO contrastVo : vos) {
				contrastVo.setHkybje(contrastVo.getCjkybje().sub(
						contrastVo.getFyybje() == null ? UFDouble.ZERO_DBL : contrastVo.getFyybje()));
				
				UFDouble cjkbbje = contrastVo.getCjkbbje() == null ? UFDouble.ZERO_DBL : contrastVo.getCjkbbje();
				UFDouble fybbje = contrastVo.getFybbje() == null ? UFDouble.ZERO_DBL : contrastVo.getFybbje();
				
				contrastVo.setHkbbje(cjkbbje.sub(fybbje));
			}
		}
	}

	public List<JKBXHeaderVO> queryHeadersByPrimaryKeys(String[] keys, String djdl) throws BusinessException {
		List<JKBXHeaderVO> headVos = null;
		try {

			String inStr = SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, keys);
			inStr += " order by djrq desc, djbh desc ";
			headVos = getJKBXDAO().queryHeadersByWhereSql(" where zb.dr=0 and " + inStr, djdl);

		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		return headVos;
	}

	public int querySize(DjCondVO condVO) throws BusinessException {
		int querySize = -99;
		try {
			querySize = getJKBXDAO().querySize(condVO);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		return querySize;
	}

	public void updateQzzt(JKBXVO[] vos) throws BusinessException {
		JKBXHeaderVO[] heads = new JKBXHeaderVO[vos.length];
		for (int i = 0; i < heads.length; i++) {
			heads[i] = vos[i].getParentVO();
		}
		try {
			getJKBXDAO().update(heads, new String[] { JKBXHeaderVO.QZZT });
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public Collection<BxcontrastVO> queryJkContrast(JKBXVO[] selBxvos, boolean isBatch) throws BusinessException {
		Collection<BxcontrastVO> jkContrasVOs = null;
		try {
			String[] bxdKeys = new String[selBxvos.length];
			for (int i = 0; i < bxdKeys.length; i++) {
				bxdKeys[i] = selBxvos[i].getParentVO().getPk_jkbx();
			}
			String sqlJK = SqlUtils.getInStr(BxcontrastVO.PK_BXD, bxdKeys);
			jkContrasVOs = getJKBXDAO().retrieveContrastByClause(sqlJK);

			if (isBatch) {
				List<String> cjk_pc = new ArrayList<String>();
				for (BxcontrastVO vo : jkContrasVOs) {
					if (!StringUtils.isNullWithTrim(vo.getPk_pc())) {
						cjk_pc.add(vo.getPk_pc());
					}
				}

				Collection<BxcontrastVO> batchVos = getJKBXDAO().retrieveContrastByClause(
						SqlUtils.getInStr(BxcontrastVO.PK_PC, cjk_pc.toArray(new String[] {})));

				jkContrasVOs.addAll(batchVos);
			}

		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		
		fillUpHkJe(jkContrasVOs);
		return jkContrasVOs;

	}

	/**
	 * ��ѯ��̯��Ϣ�����壩
	 * 
	 * @param parentVO
	 * @return
	 * @throws BusinessException
	 */
	public Collection<CShareDetailVO> queryCSharesVOS(JKBXHeaderVO[] parentVO) throws BusinessException {

		String key = CShareDetailVO.SRC_ID;

		Collection<CShareDetailVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveCShareVoByClause(
					SqlUtils.getInStr(key, parentVO, JKBXHeaderVO.PK_JKBX) + " and " + CShareDetailVO.SRC_TYPE + "="
							+ IErmCostShareConst.CostShare_Bill_SCRTYPE_BX + " and dr=0 ");
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;
	}

	public void saveSqdlrs(List<String> roles, SqdlrVO[] sqdlrVOs) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		List<SqdlrVO> insertVos = new ArrayList<SqdlrVO>();
		List<SqdlrVO> updateVos = new ArrayList<SqdlrVO>();
		for (SqdlrVO sqdlrVO : sqdlrVOs) {
			for (String role : roles) {
				if (sqdlrVO.getPk_authorize() != null) {
					if (!role.equals(sqdlrVO.getPk_roler())) {
						addNewSqdlrVO(insertVos, sqdlrVO, role);
					} else {
						updateVos.add(sqdlrVO);
					}
				} else {
					addNewSqdlrVO(insertVos, sqdlrVO, role);
				}

			}
		}
		try {
			dao.insertVOArray(insertVos.toArray(new SqdlrVO[insertVos.size()]));
			dao.updateVOArray(updateVos.toArray(new SqdlrVO[0]));
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private void addNewSqdlrVO(List<SqdlrVO> insertVos, SqdlrVO sqdlrVO, String role) throws BusinessException {
		if (!isAsigned(role, sqdlrVO.getPk_user(), (sqdlrVO.getKeyword() != null ? sqdlrVO.getKeyword()
				: ISqdlrKeyword.KEYWORD_BUSIUSER))) {
			SqdlrVO o = new SqdlrVO();
			o.setPk_roler(role);
			o.setPk_org(sqdlrVO.getPk_org());
			o.setPk_user(sqdlrVO.getPk_user());
			o.setKeyword(sqdlrVO.getKeyword() != null ? sqdlrVO.getKeyword() : ISqdlrKeyword.KEYWORD_BUSIUSER);
			o.setType(0);
			insertVos.add(o);
		}
	}

	public boolean isAsigned(String pk_role, String user, String keyword) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String condition = " pk_roler = '" + pk_role + "' and pk_user = '" + user + "' and keyword = '" + keyword + "'";

		@SuppressWarnings("rawtypes")
		Collection c = null;
		try {
			c = dao.retrieveByClause(SqdlrVO.class, condition);
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
		if (c != null && c.size() > 0)
			return true;
		return false;
	}

	public boolean isAsigned(String pk_role, String user) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String condition = " pk_roler = '" + pk_role + "' and pk_user = '" + user + "'";

		@SuppressWarnings("rawtypes")
		Collection c = null;
		try {
			c = dao.retrieveByClause(SqdlrVO.class, condition);
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
		if (c != null && c.size() > 0)
			return true;
		return false;
	}

	public Map<String, List<SqdlrVO>> querySqdlr(String[] pk_roles, String... ywy_corps) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		StringBuffer condition = null;
		try {
			condition = new StringBuffer(SqlUtils.getInStr("pk_roler", pk_roles));
			if (ywy_corps != null && ywy_corps.length > 0) {
				condition.append(" and ");
				condition.append(SqlUtils.getInStr("pk_corp", ywy_corps));
			}
			condition.append(" and type=0 ");

			@SuppressWarnings("rawtypes")
			Collection c = dao.retrieveByClause(SqdlrVO.class, condition.toString());
			Map<String, List<SqdlrVO>> vos = new HashMap<String, List<SqdlrVO>>();
			if (c != null) {
				for (Object o : c) {
					if (vos.get(((SqdlrVO) o).getPk_roler()) == null) {
						vos.put(((SqdlrVO) o).getPk_roler(), new ArrayList<SqdlrVO>());
					}
					vos.get(((SqdlrVO) o).getPk_roler()).add((SqdlrVO) o);
				}
			}
			return vos;
		} catch (Exception e1) {
			throw ExceptionHandler.handleException(e1);
		}

	}

	public void delSqdlrs(List<String> roles, SqdlrVO[] sqdlrVOs) throws BusinessException {
		try {
			batchDelete(roles, sqdlrVOs);
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private int batchDelete(List<String> roles, SqdlrVO[] sqdlrVOs) throws DAOException {
		PersistenceManager manager = null;
		int result = 0;
		try {
			manager = PersistenceManager.getInstance();
			manager.setMaxRows(100000);
			manager.setAddTimeStamp(true);
			JdbcSession session = manager.getJdbcSession();
			List<SQLParameter> listPara = new LinkedList<SQLParameter>();
			String sql = SQLHelper.getDeleteSQL(sqdlrVOs[0].getTableName(), new String[] { "pk_roler", "pk_user" });
			for (String role : roles) {
				for (SqdlrVO sqdlrVO : sqdlrVOs) {
					SQLParameter para = new SQLParameter();
					para.addParam(role);
					para.addParam(sqdlrVO.getPk_user());
					listPara.add(para);
				}
			}
			session.addBatch(sql, listPara.toArray(new SQLParameter[listPara.size()]));
			result = session.executeBatch();
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return result;
	}

	public Collection<JsConstrasVO> queryJsContrastsByJsd(String pk_jsd) throws BusinessException {

		Collection<JsConstrasVO> vos;
		try {
			vos = getJKBXDAO().retrieveJsContrastByClause(JsConstrasVO.PK_JSD + "='" + pk_jsd + "'");
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;
	}

	public void unSettleFromArap(JKBXVO[] vos) throws BusinessException {
		unSettle(vos, false);
	}

	public JKBXVO[] unSettle(JKBXVO[] vos) throws BusinessException {
		return unSettle(vos, true);
	}

	public List<SqdlrVO> querySqdlr(String pk_user, String user_corp, String ywy_corp) throws BusinessException {
		nc.pubitf.rbac.IRolePubService service = NCLocator.getInstance().lookup(nc.pubitf.rbac.IRolePubService.class);
		try {
			RoleVO[] roleVOs = service.queryRoleByUserID(pk_user, user_corp);
			List<String> roles = new ArrayList<String>();
			for (RoleVO roleVO : roleVOs) {
				roles.add(roleVO.getPk_role());
			}
			Map<String, List<SqdlrVO>> sqdlrMap = querySqdlr(roles.toArray(new String[roles.size()]), ywy_corp);
			List<SqdlrVO> result = new ArrayList<SqdlrVO>();

			for (Map.Entry<String, List<SqdlrVO>> entry : sqdlrMap.entrySet()) {
				result.addAll(entry.getValue());
			}
			return result;
		} catch (BusinessException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public void savedefSqdlrs(List<String> roles, Map<String, String[]> defMap) throws BusinessException {
		List<SqdlrVO> vos = new ArrayList<SqdlrVO>();

		for (Map.Entry<String, String[]> entry : defMap.entrySet()) {
			if (entry.getKey() != null) {
				deldefSqdlrs(roles, entry.getKey(), entry.getValue());
				for (String def : entry.getValue()) {
					SqdlrVO vo = new SqdlrVO();
					vo.setKeyword(entry.getKey());
					vo.setPk_user(def);
					vo.setType(0);
					vos.add(vo);
				}
			} else {
				deldefSqdlrs(roles, entry.getKey(), new String[] {});
			}
		}

		saveSqdlrs(roles, vos.toArray(new SqdlrVO[vos.size()]));

	}

	private void deldefSqdlrs(List<String> roles, String key, String[] pk_defs) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		List<String> curDefls = new ArrayList<String>();
		for (String pk_def : pk_defs) {
			curDefls.add(pk_def);
		}
		List<SqdlrVO> list = new ArrayList<SqdlrVO>();
		for (String role : roles) {
			SqdlrVO[] sqdlrVOs = getAssigned(role, key);
			if (sqdlrVOs == null)
				break;
			for (SqdlrVO o : sqdlrVOs) {
				if (!curDefls.contains(o.getPk_user())) {
					list.add(o);
				}
			}
		}
		dao.deleteVOList(list);
	}

	private SqdlrVO[] getAssigned(String pk_role, String keyword) {
		BaseDAO dao = new BaseDAO();
		String condition = " pk_roler = '" + pk_role + "' and keyword = '" + keyword + "'";
		Collection<?> c = null;
		try {
			c = dao.retrieveByClause(SqdlrVO.class, condition);
		} catch (DAOException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
		}
		if (c != null && c.size() > 0)
			return c.toArray(new SqdlrVO[c.size()]);
		return null;
	}

	public JKBXHeaderVO updateHeader(JKBXHeaderVO header, String[] fields) throws BusinessException {
		compareTs(new JKBXHeaderVO[] { header });
		updateHeaders(new JKBXHeaderVO[] { header }, fields);
		return header;
	}

	public JKBXDAO getJKBXDAO() throws SQLException {
		if (null == jkbxDAO) {
			try {
				jkbxDAO = new JKBXDAO();
			} catch (NamingException e) {
				Log.getInstance(this.getClass()).error(e.getMessage(), e);
				throw new SQLException(e.getMessage());
			}
		}
		return jkbxDAO;
	}

	public static boolean isCmpInstall(JKBXHeaderVO parentVO) throws BusinessException {
		boolean flag = BXUtil.isProductInstalled(parentVO.getPk_group(), BXConstans.TM_CMP_FUNCODE);
		if (!flag) {
			return false;
		}
		// �ֽ��ڱ���ʱ�����У��
//		String periord = NCLocator.getInstance().lookup(IOrgUnitPubService.class)
//				.getOrgModulePeriodByOrgIDAndModuleID(parentVO.getPk_org(), BXConstans.TM_CMP_FUNCODE);
//		if (periord == null) {
//			Logger.debug("��ǰ����������֯��������λ)δ�����ֽ����");
//			flag = false;
//		}
		return flag;
	}

	private static String getBSLoginUser() {
		return InvocationInfoProxy.getInstance().getUserId();
	}

	public Map<String, String> getTsByPrimaryKey(String[] key, String tableName, String pkfield)
			throws BusinessException {
		try {
			return getJKBXDAO().getTsByPrimaryKeys(key, tableName, pkfield);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(this.getClass(), e);

		}
	}

	private void checkDelete(JKBXVO vo) throws DataValidateException, BusinessException {
		// У�鵥��״̬
		VOStatusChecker.checkDeleteStatus(vo.getParentVO());

		// �ǳ��õ��ݵ���У���Ƿ����
		if (!vo.getParentVO().isInit() && vo.getParentVO().getDjzt() != BXStatusConst.DJZT_TempSaved) {
			VOChecker.checkErmIsCloseAcc(vo);
		}
	}

	@SuppressWarnings("unused")
	private void checkDataPermission(JKBXVO[] vos) throws BusinessException {
		List<JKBXVO> bxList = new ArrayList<JKBXVO>();
		List<JKBXVO> jkList = new ArrayList<JKBXVO>();
		boolean isNCClient=false;
		for (JKBXVO vo : vos) {
			// �Ƿ��ڳ�����
			boolean isQc = vo.getParentVO().getQcbz().booleanValue();
			// �Ƿ��õ���
			boolean isInit = vo.getParentVO().isInit();
			
			//�Ƿ��NC�ͻ���
			isNCClient=vo.isNCClient();
			// �ڳ����õ��ݲ�У������Ȩ��
			if (isQc || isInit) {
				continue;
			} else {
				if (vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
					bxList.add(vo);
				} else if (vo.getParentVO().getDjdl().equals(BXConstans.JK_DJDL)) {
					jkList.add(vo);
				}
			}
		}
		// ��֤����Ȩ��
		if(isNCClient){
			BXDataPermissionChkUtil.process(bxList.toArray(new JKBXVO[0]), BXConstans.ERMEXPRESOURCECODE,
					BXConstans.EXPDELOPTCODE, getBSLoginUser());
			
			// ��֤���Ȩ��
			BXDataPermissionChkUtil.process(jkList.toArray(new JKBXVO[0]), BXConstans.ERMLOANRESOURCECODE,
					BXConstans.LOANDELOPTCODE, getBSLoginUser());
		}
	}
	
	/**
	 * ��ѯ����Ԥ����ϸ
	 * 
	 * @param parentVO
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AccruedVerifyVO> queryAccruedVerifyVOS(JKBXHeaderVO... parentVO) throws BusinessException {

		Collection<AccruedVerifyVO> vos = null;
		BaseDAO dao = new BaseDAO();
		try {
			vos = dao.retrieveByClause(AccruedVerifyVO.class,SqlUtils.getInStr(AccruedVerifyVO.PK_BXD, 
					parentVO, JKBXHeaderVO.PK_JKBX) );
			// ��ѯԭԤ�ᵥ����Ԥ����ϸ��ts
			if (vos != null && vos.size() > 0) {
				List<String> accruedBillPks = new ArrayList<String>();
				for (AccruedVerifyVO vo : vos) {
					accruedBillPks.add(vo.getPk_accrued_bill());
				}
				Collection<AccruedVO> accvos = dao.retrieveByClause(AccruedVO.class, SqlUtils.getInStr(
						AccruedVO.PK_ACCRUED_BILL, accruedBillPks.toArray(new String[accruedBillPks.size()])));
				Map<String, UFDateTime> accTsMap = new HashMap<String, UFDateTime>();
				if (accvos != null && accvos.size() > 0) {
					for (AccruedVO vo : accvos) {
						accTsMap.put(vo.getPk_accrued_bill(), vo.getTs());
					}
				}
				if (accTsMap.size() > 0) {
					for (AccruedVerifyVO vo : vos) {
						vo.setTs(accTsMap.get(vo.getPk_accrued_bill()));
					}
				}
			}
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}

		return vos;
	}
}