package nc.impl.erm.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.arap.bx.ErContrastUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.mw.sqltrans.TempTable;
import nc.bs.sm.accountmanage.AbstractUpdateAccount;
import nc.erm.pub.conversion.ErmBillCostConver;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.itf.erm.service.IErmGroupPredataService;
import nc.itf.fip.initdata.IFipInitDataService;
import nc.itf.org.IGroupQryService;
import nc.itf.tb.control.IRuleUpgradeToV63;
import nc.itf.uap.bbd.func.IFuncRegisterQueryService;
import nc.itf.uap.pf.IWorkflowUpgrade;
import nc.itf.uap.template.ISystemTemplateAssignService;
import nc.jdbc.framework.ConnectionFactory;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountManageService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.erm.expenseaccount.ExpenseBalVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.GroupVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pftemplate.SystemplateBaseVO;
import nc.vo.pub.pftemplate.SystemplateVO;
import nc.vo.sm.funcreg.FuncRegisterVO;
import nc.vo.sm.funcreg.MenuItemVO;
import nc.vo.sm.funcreg.ParamRegVO;
import nc.vo.trade.summarize.Hashlize;
import nc.vo.trade.summarize.IHashKey;

/**
 * ���ù����Ʒ63�汾������ʱ�Ĵ������ *
 */
public class ErmNewInstallAdjust extends AbstractUpdateAccount {
	private BaseDAO dao = null;

	private BaseDAO getBaseDAO() {
		if (dao == null) {
			dao = new BaseDAO();
		}
		return dao;
	}

	private int batchUpdate(String sql, List<SQLParameter> parameterList) throws DAOException {
		PersistenceManager manager = null;
		int value;
		try {
			manager = PersistenceManager.getInstance();
			manager.setMaxRows(100000);
			manager.setAddTimeStamp(true);
			JdbcSession session = manager.getJdbcSession();

			session.addBatch(sql, parameterList.toArray(new SQLParameter[parameterList.size()]));
			value = session.executeBatch();
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return value;
	}

	private IBXBillPrivate getQueryService() {
		return NCLocator.getInstance().lookup(IBXBillPrivate.class);
	}

	/**
	 * �°�װ���������ݿ�ṹ����֮�󣬽�����������֮ǰ���� ����63����61�����ݱ����������ֶ�Ĭ��ֵ����
	 */
	private void doBeforeUpdateData() throws DAOException, BusinessException, Exception {
		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.1������6.3��ʼ������Ϣ��ʼ" + getClass().getName() + "**********");
		Logger.debug("*************************************");

		// һ��ɾ������Ĳ������� ��FICOMMON02 �����б�ÿҳ��ʾ��¼����
		getBaseDAO().executeUpdate("delete from pub_sysinit where initcode = 'FICOMMON02'");
		getBaseDAO().executeUpdate("delete from pub_sysinittemp where initcode = 'FICOMMON02'");

		// ����ɾ��61ϵͳԤ�ò˵�������61��ϵͳԤ�ò˵���
		do61MenuDelete();

		// ����ɾ��61ԭ�е�Ӧ���ʲ�
		getBaseDAO().executeUpdate("delete from aam_appasset where pk_module = '2011' and assetlayer = 0");
		
		//�ġ�ɾ�����Ԥ���е�ģ��
		getBaseDAO().executeUpdate("delete from pub_print_cell where ctemplateid in " +
				"(select ctemplateid from pub_print_template where pub_print_template.vtemplatecode = 'ermprealarm' ) ");
		
		//�ġ�ɾ��Ԥ����Ʋ���
		getBaseDAO().executeUpdate("delete from ntb_id_ctrltactics  where sys_code = 'erm' ");

		Logger.debug("*************************************");
		Logger.debug("******** ��������ģ������6.1������6.3��ʼ������Ϣ����" + getClass().getName() + "**********");
		Logger.debug("*************************************");
	}

	/**
	 * ���½������б�ͷ������������ֶ�Ĭ��ֵ���ã�������Ŀ����Ŀ���񣬺���Ҫ�صȣ�
	 * 
	 * @param jkList
	 * @param bxList
	 * @throws Exception
	 */
	private void updateJkBxInfo(Collection<JKBXVO> jkList, Collection<JKBXVO> bxList) throws Exception {
		String updateBxPayOrgSql = "update er_bxzb set pk_payorg = isnull(pk_payorg,pk_org), pk_payorg_v = isnull(pk_payorg_v,pk_org_v)";
		getBaseDAO().executeUpdate(updateBxPayOrgSql);
		Logger.debug(updateBxPayOrgSql);

		String updateJkPayOrgSql = "update er_jkzb set pk_payorg = isnull(pk_payorg,pk_org), pk_payorg_v = isnull(pk_payorg_v,pk_org_v)";
		getBaseDAO().executeUpdate(updateJkPayOrgSql);
		Logger.debug(updateJkPayOrgSql);

		StringBuffer updateBusitemSql = new StringBuffer();
		updateBusitemSql.append("update er_busitem set pk_resacostcenter = ?, pk_checkele = ?,").append(
				"pk_pcorg = ? , pk_pcorg_v = ?,jobid = ? ,projecttask = ? where pk_jkbx = ?");

		List<JKBXHeaderVO> jkbxList = new ArrayList<JKBXHeaderVO>();
		if (jkList != null) {
			for (JKBXVO jkVo : jkList) {
				jkbxList.add(jkVo.getParentVO());
			}
		}

		if (bxList != null) {
			for (JKBXVO bxVo : bxList) {
				jkbxList.add(bxVo.getParentVO());
			}
		}

		List<SQLParameter> parameterList = new ArrayList<SQLParameter>();
		for (JKBXHeaderVO head : jkbxList) {
			SQLParameter param = new SQLParameter();
			param.addParam(head.getPk_resacostcenter());
			param.addParam(head.getPk_checkele());
			param.addParam(head.getPk_payorg());
			param.addParam(head.getPk_payorg_v());

			param.addParam(head.getJobid());
			param.addParam(head.getProjecttask());
			param.addParam(head.getPk_jkbx());
			parameterList.add(param);
		}
		batchUpdate(updateBusitemSql.toString(), parameterList);
		Logger.debug(updateBusitemSql.toString());
	}

	/**
	 * ���³�����Ϣ��63�г���ս�ҵ���н��г�������61������������ͬ��
	 * 
	 * @param jkVoCollection
	 *            ���VO����
	 * @param bxVoCollection
	 *            ����VO����
	 * @param contrastVoList
	 *            ����VO����
	 * @throws Exception
	 */
	private void updateContrastInfo(Collection<JKBXVO> jkVoCollection, Collection<JKBXVO> bxVoCollection,
			List<BxcontrastVO> contrastVoList) throws Exception {
		if (jkVoCollection == null || jkVoCollection.size() == 0 || contrastVoList == null
				|| contrastVoList.size() == 0) {
			return;
		}

		// <��pk�������м���>
		Map<String, List<BxcontrastVO>> contrastMap = new HashMap<String, List<BxcontrastVO>>();

		List<BXBusItemVO> resultBusItemList = new ArrayList<BXBusItemVO>();
		List<BxcontrastVO> resultContrastList = new ArrayList<BxcontrastVO>();

		for (BxcontrastVO bxcontrastVO : contrastVoList) {
			List<BxcontrastVO> list = contrastMap.get(bxcontrastVO.getPk_jkd());
			if (list == null) {
				list = new ArrayList<BxcontrastVO>();
				contrastMap.put(bxcontrastVO.getPk_jkd(), list);
			}
			list.add(bxcontrastVO);
		}

		for (JKBXVO jkVo : jkVoCollection) {
			JKVO jkVoClone = (JKVO) jkVo.clone();

			JKBXHeaderVO headVo = jkVoClone.getParentVO();
			if (contrastMap.get(headVo.getPk_jkbx()) == null) {// �޳���������Ҫ����
				continue;
			}

			BXBusItemVO[] busItems = jkVoClone.getChildrenVO();
			List<BxcontrastVO> contrastList = contrastMap.get(headVo.getPk_jkbx());

			int contrastIndex = 0;// ����������
			for (BXBusItemVO busItem : busItems) {
				busItem.setYjye(busItem.getYbje());
				busItem.setYbye(busItem.getYbje());
				busItem.setBbye(busItem.getBbje());
				busItem.setGroupbbye(busItem.getGroupbbje());
				busItem.setGlobalbbye(busItem.getGlobalbbje());

				resultBusItemList.add(busItem);

				if (contrastIndex >= contrastList.size()) {
					continue;
				}
				// ���������
				contrastIndex = dealContrastVos(busItem, contrastList, resultContrastList, headVo.getPk_payorg(),
						contrastIndex);
			}
		}

		// ���÷���ԭ�ҽ��
		setFyJeAndBbJe(resultContrastList, bxVoCollection);

		getBaseDAO().updateVOArray(
				resultBusItemList.toArray(new BXBusItemVO[] {}),
				new String[] { BXBusItemVO.YBYE, BXBusItemVO.YJYE, BXBusItemVO.BBYE, BXBusItemVO.GROUPBBYE,
						BXBusItemVO.GLOBALBBYE });

		// ɾ�����г�����
		getBaseDAO().deleteVOArray(contrastVoList.toArray(new BxcontrastVO[] {}));
		getBaseDAO().insertVOArray(resultContrastList.toArray(new BxcontrastVO[] {}));
	}

	private void setFyJeAndBbJe(List<BxcontrastVO> resultContrastList, Collection<JKBXVO> bxVos) {
		Map<String, List<BxcontrastVO>> bxContrastMap = new HashMap<String, List<BxcontrastVO>>();
		for (BxcontrastVO contrastVo : resultContrastList) {
			List<BxcontrastVO> list = bxContrastMap.get(contrastVo.getPk_jkd());
			if (list == null) {
				list = new ArrayList<BxcontrastVO>();
				bxContrastMap.put(contrastVo.getPk_bxd(), list);
			}
			list.add(contrastVo);
		}

		for (JKBXVO bxvo : bxVos) {
			JKBXHeaderVO parentVO = bxvo.getParentVO();
			UFDouble hkybJe = parentVO.getHkybje();
			List<BxcontrastVO> contrasVos = bxContrastMap.get(parentVO.getPk_jkbx());

			if (contrasVos == null || contrasVos.size() == 0) {
				continue;
			}
			for (BxcontrastVO contrastVo : contrasVos) {
				if (hkybJe == null || hkybJe.compareTo(UFDouble.ZERO_DBL) == 0) {
					contrastVo.setFyybje(contrastVo.getYbje());
				} else {
					UFDouble fyybje = UFDouble.ZERO_DBL;

					if (contrastVo.getYbje().compareTo(hkybJe) >= 0) {
						fyybje = contrastVo.getYbje().sub(hkybJe);
						hkybJe = UFDouble.ZERO_DBL;
					} else {
						fyybje = UFDouble.ZERO_DBL;
						hkybJe = hkybJe.sub(contrastVo.getYbje());
					}
					contrastVo.setFybbje(fyybje);
				}
			}

			// ���ñ��ҽ��
			ErContrastUtil.resetCjkJeToBB(parentVO.getBbhl(), parentVO.getGlobalbbhl(), parentVO.getGroupbbhl(),
					parentVO.getBzbm(), parentVO.getDjrq(), parentVO.getPk_org(), parentVO.getPk_group(), contrasVos);
		}
	}

	private int dealContrastVos(BXBusItemVO busItem, List<BxcontrastVO> contrastList,
			List<BxcontrastVO> resultContrastList, String payOrg, int contrastIndex) {
		int result = 0;
		for (int i = contrastIndex; i < contrastList.size(); i++) {
			BxcontrastVO bxcontrastVO = contrastList.get(i);

			if (busItem.getYjye().compareTo(bxcontrastVO.getYbje()) >= 0) {// ҵ���н����ڳ����н��
				busItem.setYjye(busItem.getYjye().sub(bxcontrastVO.getYbje()));// Ԥ���������
				if (bxcontrastVO.getSxbz() != null && bxcontrastVO.getSxbz() == BXStatusConst.SXBZ_VALID) {
					busItem.setYbye(busItem.getYjye());// ԭ�����
					busItem.setBbye(busItem.getBbye().sub(bxcontrastVO.getBbje()));
					UFDouble groupbbye = busItem.getGroupbbye() == null ? UFDouble.ZERO_DBL : busItem.getGroupbbye();
					UFDouble globalbbye = busItem.getGlobalbbye() == null ? UFDouble.ZERO_DBL : busItem.getGlobalbbye();
					busItem.setGroupbbye(groupbbye.sub(bxcontrastVO.getGroupbbje()));
					busItem.setGlobalbbye(globalbbye.sub(bxcontrastVO.getGlobalbbje()));
				}

				// TODO ��һЩ�ֶ�δ����
				BxcontrastVO newContrastVo = (BxcontrastVO) bxcontrastVO.clone();
				newContrastVo.setPk_busitem(busItem.getPk_busitem());// �����
				newContrastVo.setPk_payorg(payOrg);// ֧����λ
				newContrastVo.setPk_bxcontrast(null);
				resultContrastList.add(newContrastVo);
				result = i + 1;// �����е�����
			} else {
				BxcontrastVO newContrastVo = (BxcontrastVO) bxcontrastVO.clone();
				busItem.setYjye(UFDouble.ZERO_DBL);
				if (bxcontrastVO.getSxbz() != null && bxcontrastVO.getSxbz() == BXStatusConst.SXBZ_VALID) {
					busItem.setYbye(UFDouble.ZERO_DBL);// ԭ�����
					busItem.setBbye(UFDouble.ZERO_DBL);
					busItem.setGroupbbye(UFDouble.ZERO_DBL);
					busItem.setGlobalbbye(UFDouble.ZERO_DBL);
				}
				// ���ó����еĽ��
				bxcontrastVO.setYbje(bxcontrastVO.getYbje().sub(busItem.getYbje()));

				newContrastVo.setYbje(busItem.getYbje());// ���
				newContrastVo.setCjkybje(busItem.getYbje());// ������

				// TODO ��һЩ�ֶ�δ����
				newContrastVo.setPk_busitem(busItem.getPk_busitem());// �����
				newContrastVo.setPk_payorg(payOrg);// ֧����λ
				newContrastVo.setPk_bxcontrast(null);
				resultContrastList.add(newContrastVo);
				result = i;// �����е�����
			}

			if (busItem.getYjye().equals(UFDouble.ZERO_DBL)) {
				break;
			}
		}
		return result;
	}

	@Override
	public void doBeforeUpdateData(String oldVersion, String newVersion) throws Exception {

		if (is61UpdateTo63(oldVersion, newVersion)) {
			// 61������63ʱ������������
			doBeforeUpdateData();
		}
	}

	private boolean is61UpdateTo63(String oldVersion, String newVersion) {
		return oldVersion != null && oldVersion.startsWith("6.1") && newVersion != null && newVersion.startsWith("6.3");
	}

	@Override
	public void doBeforeUpdateDB(String oldVersion, String newVersion) throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * 63�Ա�61�����Ĺ��ܽڵ�
	 * 
	 * ����������ƹ������� 20110MCS ��̯��������-��֯ 20110SR ��̯��������-���� 20110SRG ��̯��ת���ݶ�Ӧ����-����
	 * 20110BCL ��̯��ת���ݶ�Ӧ����-��֯ 20110ABC �������뵥 201102611 �������뵥���� 20110MTAMN ���ý�ת
	 * 201105CSMG ��̯����̯�� 201105EXPMG ���� 20110EOCA ���ŷ��û��ܱ� 2011050402 �ɱ����Ļ��ܱ�
	 * 2011050403 ���õ�λ���ܱ� 2011050404 ���ŷ�����ϸ�� 2011050502 �ɱ����ķ�����ϸ�� 2011050503
	 * ��λ������ϸ�� 2011050504 ���ŷ�������ִ�������ѯ 2011050701 �����˷�������ִ�������ѯ 2011050702
	 * �ɱ����ķ�����ִ�������ѯ 2011050703
	 */

	@Override
	public void doAfterUpdateData(String oldVersion, String newVersion) throws Exception {
		if (!is61UpdateTo63(oldVersion, newVersion)) {
			// ��61������63������
			return;
		}

		// һ���������Ľڵ�Ĭ��ģ����䣬���䵽����
		doAssignTemplate();

		// �����˵����
		doUpdateMenu();

		// ����ɾ����ʱ��
		doDropTempTable();

		// �ġ������û��Զ��彻�����͹��ܽڵ�
		dealUserDefFun();

		// �塢���ƽ̨����Ԥ�ýű�����
		updateFipInfo();

		// �������ò�Ʒ����ҵ���ʼ��Ԥ������
		String[] pkgroups = null;
		IGroupQryService ip = NCLocator.getInstance().lookup(IGroupQryService.class);
		GroupVO[] groupVOs = ip.queryAllGroupVOs();
		if (groupVOs != null) {
			pkgroups = new String[groupVOs.length];
			for (int i = 0; i < pkgroups.length; i++) {
				pkgroups[i] = groupVOs[i].getPk_group();
			}
		}

		if (pkgroups != null) {// ����Ԥ�����ݵ�����
			initDataToGroup(pkgroups);
		}

		// �ߡ����õ��ݡ�֧����λĬ��ֵ���ã�Ĭ���������λ��ͬ��
		String updateInitSql = "update er_jkbx_init set pk_payorg = pk_org, pk_payorg_v = pk_org_v";
		getBaseDAO().executeUpdate(updateInitSql);
		Logger.debug(updateInitSql);

		// �ˡ���������֧����λĬ��ֵ���ã�Ĭ���������λ��ͬ��ҵ��������ά��Ĭ��ֵ���ã�Ĭ���������Ӧ�ֶ�ֵ��ͬ
		List<JKBXVO> jkVoCollection = getQueryService().queryVOsByWhereSql(" where dr = 0 ", BXConstans.JK_DJDL);
		List<JKBXVO> bxVoCollection = getQueryService().queryVOsByWhereSql(" where dr = 0 ", BXConstans.BX_DJDL);

		updateJkBxInfo(jkVoCollection, bxVoCollection);

		// �š������С�ԭ��Խ������ĳ�������Ϊ���ս���ҵ���г���������ҵ����pk˳�������
		if (jkVoCollection != null && bxVoCollection != null) {
			List<BxcontrastVO> contrastVoList = new ArrayList<BxcontrastVO>();
			for (JKBXVO bxVo : bxVoCollection) {// ��ȡ���г�����
				BxcontrastVO[] contrasts = bxVo.getContrastVO();
				if (contrasts != null && contrasts.length > 0) {
					for (BxcontrastVO bxcontrastVO : contrasts) {
						contrastVoList.add(bxcontrastVO);
					}
				}
			}

			// ���³�����Ϣ
			updateContrastInfo(jkVoCollection, bxVoCollection, contrastVoList);
		}

		// ʮ�������������н�ͨ�����ֶνű�����
		updateVehicleField();

		// ʮһ������֮ǰ�ķ�����ͬ���������˱�
		insertVOsExpAccVO();

		// ʮ����Ԥ��ű�����
		updateBudgetData();

		// ʮ������������
		updateInitParm();
		
		// ʮ�ġ�����ɾ��
		deleteInitParm();
		
		// ʮ�塢ҵ����ɾ��
        deleteBusinessPlugin();
        
        // ʮ����"ί�а���"��������
        updateBillAction();
	}
	
	@SuppressWarnings("unchecked")
	public void updateBillAction() throws BusinessException {
		// v63��v631ֻ����ί�а���������ɾ��������ɾ��ʱ��Ӧ��ɾ���������ݡ�
		String[] delActionType = new String[]{"TRANSFERFTS"};
		String[] addActionType = new String[]{"TRANSFERFTS"};
		String sql = "select distinct pk_billtypecode from bd_billtype where systemcode='erm' and ncbrcode='bx' and istransaction='Y'";
		List<String> result = (List<String>) new BaseDAO().executeQuery(sql.toString(), new BaseProcessor() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object processResultSet(ResultSet rs) throws SQLException {
				List<String> lstResult = new ArrayList<String>();
				while (rs.next()) {
					lstResult.add(rs.getString(1));
				}
				return lstResult;
			}

		});
		if(result == null || result.size() < 0){
			return;
		}
		IWorkflowUpgrade workflowUpgrade = NCLocator.getInstance().lookup(IWorkflowUpgrade.class);
		for (String billorTranstype : result) {
			workflowUpgrade.updateBillactionByGlobal(billorTranstype, delActionType, addActionType);
		}
	}

	private void deleteBusinessPlugin() throws DAOException {
	    String sqlER1 = "delete from PUB_EVENTLISTENER where IMPLCLASSNAME = 'nc.vo.erm.closeacc.ErmGLCloseAccListener' and owner = '2011'";

	    Logger.debug("����-���˹���У��ҵ����ɾ����" + sqlER1);
	    getBaseDAO().executeUpdate(sqlER1);
	}

	private void updateInitParm() throws DAOException {
		String sqlER1 = "update pub_sysinit set value =  "
				+ "(case when value = '������' then '2'  when value='¼����' then '1' else value end)  " /*-=notranslate=-*/
				+ " where INITCODE = 'ER1'";

		Logger.debug("���ò���������" + sqlER1);
		getBaseDAO().executeUpdate(sqlER1);

		String sqlER8 = "update pub_sysinit set value = (case when value = '�����˵�λ' then '1'  " /*-=notranslate=-*/
				+ "when value='���óе���λ' then '2' when value='������λ' then '3' else value end) " /*-=notranslate=-*/
				+ " where INITCODE = 'ER8'";
		Logger.debug("���ò���������" + sqlER8);
		getBaseDAO().executeUpdate(sqlER8);

		String sqlERY = "update pub_sysinit set value = (case when value = '���óе���λ' then '1'  " /*-=notranslate=-*/
				+ "when value='������λ' then '2' when value='�����˵�λ' then '3' when value='֧����λ' then '4' " /*-=notranslate=-*/
				+ "else value end)  where INITCODE = 'ERY'";
		Logger.debug("���ò���������" + sqlERY);
		getBaseDAO().executeUpdate(sqlERY);
		
		String sqlER9 = "update pub_sysinit set value = (case when value = '�����' then '1'  " /*-=notranslate=-*/
			+ "when value='��鵫������' then '2' when value='��鲢�ҿ���' then '3' else value end) " /*-=notranslate=-*/
			+ " where INITCODE = 'ER9'";
		Logger.debug("���ò���������" + sqlER9);
		getBaseDAO().executeUpdate(sqlER9);
		
	}
	
	private void deleteInitParm() throws DAOException {
	    String sqlEr10 = "DELETE FROM PUB_SYSINITTEMP WHERE PK_SYSINITTEMP = '0001Z362002560632514'"; 
	    Logger.debug("���ò���ɾ����" + sqlEr10);
	    getBaseDAO().executeUpdate(sqlEr10);
	}

	private void updateBudgetData() throws BusinessException {
		boolean isInstallTBB = ErUtil.isProductTbbInstalled(BXConstans.TBB_FUNCODE);
		if (isInstallTBB) {
			Map<String, String> ermAttToNew = new HashMap<String, String>();
			ermAttToNew.put("zb.pk_pcorg", "pk_pcorg");
			ermAttToNew.put("zb.pk_resacostcenter", "pk_resacostcenter");
			ermAttToNew.put("zb.deptid", "operator_dept");
			ermAttToNew.put("zb.fydeptid", "assume_dept");
			ermAttToNew.put("zb.fydwbm", "assume_org");
			ermAttToNew.put("zb.dwbm", "operator_org");
			ermAttToNew.put("zb.hbbm", "pk_supplier");
			ermAttToNew.put("zb.jobid", "pk_project");
			ermAttToNew.put("zb.customer", "pk_customer");
			ermAttToNew.put("fb.szxmid", "pk_iobsclass");
			ermAttToNew.put("zb.bzbm", "pk_currtype");
			ermAttToNew.put("zb.pk_org", "pk_org");
			ermAttToNew.put("zb.cashproj", "cashproj");
			IRuleUpgradeToV63 upgrader = NCLocator.getInstance().lookup(IRuleUpgradeToV63.class);
			upgrader.upgradeRulesForAttChanged(ermAttToNew, "erm");
		}
	}

	private void insertVOsExpAccVO() throws MetaDataException, BusinessException {
		// ��ɾ�������
		getBaseDAO().executeUpdate("delete from " + ExpenseAccountVO.getDefaultTableName());
		getBaseDAO().executeUpdate("delete from " + ExpenseBalVO.getDefaultTableName());

		@SuppressWarnings("unchecked")
		List<JKBXVO> vos = (List<JKBXVO>) MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(
				BXVO.class, "1=1", false);
		ExpenseAccountVO[] expaccvo = ErmBillCostConver.getExpAccVOS(vos.toArray(new JKBXVO[0]));
		NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).insertVOs(expaccvo);
	}

	/**
	 * ���������彻ͨ�����ֶνű�����
	 * 
	 * @throws DAOException
	 */
	private void updateVehicleField() throws DAOException {
		// ���½�������defitem4�ֶΣ����÷ѽ���
		String jksql = "update er_busitem  set defitem4 = case defitem4 " + "when '����' then '0' " /*-=notranslate=-*/
				+ "when '����\\����' then '1' " + "when '����' then '2' " + "when '������' then '3' " + "when '���⳵' then '4' " /*-=notranslate=-*/
				+ "when '��;����' then '5' " + "when '��' then '6' " + "when '�ɻ�' then '7'" + "else defitem4 " + "end " /*-=notranslate=-*/
				+ "where pk_jkbx in ( select pk_jkbx from er_jkzb where djlxbm in ('2631') )"; /*-=notranslate=-*/
		getBaseDAO().executeUpdate(jksql);
		// ���±�����������defitem5�ֶΣ����÷ѱ���������ͨ�ѱ�������
		String bxsql = "update er_busitem  set defitem5 = case defitem5 " + "when '����' then '0' " /*-=notranslate=-*/
				+ "when '����\\����' then '1' " + "when '����' then '2' " + "when '������' then '3' " + "when '���⳵' then '4' " /*-=notranslate=-*/
				+ "when '��;����' then '5' " + "when '��' then '6' " + "when '�ɻ�' then '7'" + "else defitem5 " + "end " /*-=notranslate=-*/
				+ "where pk_jkbx in ( select pk_jkbx from er_bxzb where djlxbm in ('2641','2642') )"; /*-=notranslate=-*/
		getBaseDAO().executeUpdate(bxsql);
	}

	@SuppressWarnings("unchecked")
	private void dealUserDefFun() throws Exception {
		// 1.�Ȳ�������Զ��幦�ܽڵ�
		Collection<ParamRegVO> c = getBaseDAO()
				.retrieveByClause(
						ParamRegVO.class,
						"PARENTID IN ( SELECT cfunid FROM SM_FUNCREGISTER WHERE OWN_MODULE = '2011') and PARAMNAME = 'transtype' and PARAMVALUE not in ('2611','2631','2632','2641','2642','2643','2644','2645','2646','2647')",
						"PARENTID");

		if (c == null || c.size() == 0) {
			return;
		}

		// 2.��������ע��
		List<ParamRegVO> newParamRegVOList = new ArrayList<ParamRegVO>();
		List<String> pk_funRegister = new ArrayList<String>();
		for (ParamRegVO ParamRegVO : c) {
			pk_funRegister.add(ParamRegVO.getParentid());
			ParamRegVO newParamRegVO = new ParamRegVO();
			newParamRegVO.setParentid(ParamRegVO.getParentid());
			if (ParamRegVO.getParamvalue().startsWith("264X")) {
				newParamRegVO.setParamname("BeanConfigFilePath");
				newParamRegVO.setParamvalue("nc/ui/erm/billinput/billinput_bxconfig.xml");
			} else if (ParamRegVO.getParamvalue().startsWith("263X")) {
				newParamRegVO.setParamname("BeanConfigFilePath");
				newParamRegVO.setParamvalue("nc/ui/erm/billinput/billinput_jkconfig.xml");
			}
			newParamRegVOList.add(newParamRegVO);
		}

		// ��ɾ���
		getBaseDAO().deleteByClause(ParamRegVO.class,
				SqlUtils.getInStr("PARENTID", pk_funRegister, true) + " and PARAMNAME = 'BeanConfigFilePath'");
		getBaseDAO().insertVOArray(newParamRegVOList.toArray(new ParamRegVO[0]));
		// 3.���¹��ܽڵ�ע�������
		String condition = SqlUtils.getInStr(FuncRegisterVO.CFUNID, pk_funRegister.toArray(new String[0]), false);
		Collection<FuncRegisterVO> f = getBaseDAO().retrieveByClause(FuncRegisterVO.class, condition);
		List<FuncRegisterVO> newFuncRegisterList = new ArrayList<FuncRegisterVO>();
		for (FuncRegisterVO funRegisterVO : f) {
			newFuncRegisterList.add(funRegisterVO);
			funRegisterVO.setClass_name("nc.ui.erm.view.ErmToftPanel");
		}
		getBaseDAO().updateVOArray(newFuncRegisterList.toArray(new FuncRegisterVO[newFuncRegisterList.size()]),
				new String[] { FuncRegisterVO.CLASS_NAME });

		// 4.���¹��ܽڵ�ģ�����
		// String sql1 =
		// "update  pub_systemplate_base set nodekey='bill' where funnode in " +
		// "(select funcode from SM_FUNCREGISTER where "+condition+") and tempstyle='0' "
		// +
		// "and nodekey is not null";
		//
		// getBaseDAO().executeUpdate(sql1);

		// 5.����ģ���ҵ��ҳǩԪ����·��
		String sql2 = "update PUB_BILLTEMPLET_T set Metadataclass='erm.er_busitem', Metadatapath='er_busitem'   where  PK_BILLTEMPLET  in ( select PK_BILLTEMPLET from PUB_BILLTEMPLET where METADATACLASS='erm.bxzb') and Metadataclass='erm.busitem'";
		getBaseDAO().executeUpdate(sql2);
		
		// 6.����ģ��ĳ���ҳǩԪ����·��
		String sql3 = "update PUB_BILLTEMPLET_T set Metadataclass='erm.contrast', Metadatapath='er_bxcontrast'   where  PK_BILLTEMPLET  in ( select PK_BILLTEMPLET from PUB_BILLTEMPLET where METADATACLASS='erm.bxzb') and Metadataclass='erm.contrast'";
		getBaseDAO().executeUpdate(sql3);
		
		// 7.��ģ���ѡ������Ϊ��N��
		String sql4 = "update pub_billtemplet_b set listshowflag='N' where  PK_BILLTEMPLET  in ( select PK_BILLTEMPLET from PUB_BILLTEMPLET where METADATACLASS in ('erm.bxzb','erm.jkzb') and PK_BILLTYPECODE<>'CJK63') and itemkey='selected'";
		getBaseDAO().executeUpdate(sql4);
		
		// 8.����ģ��ϼ���Ϊ��1��
		String sql5= "update pub_billtemplet_b set totalflag='1' where  PK_BILLTEMPLET  in ( select PK_BILLTEMPLET from PUB_BILLTEMPLET where METADATACLASS in ('erm.bxzb','erm.jkzb') and PK_BILLTYPECODE<>'CJK63') and  itemkey in ('ybje', 'bbje', 'groupbbje', 'groupzfbbje', 'globalbbje','globalzfbbje')  and pos = '1'";
		getBaseDAO().executeUpdate(sql5);
	}

	private void doDropTempTable() {
		dropTables(new String[] { "TMP_ERM_EXPBALANCE1", "TMP_ERM_EXPDETAIL1" });
	}

	private void dropTables(String[] tempTables) {
		if (tempTables != null && tempTables.length > 0) {
			Connection con = null;
			try {
				con = ConnectionFactory.getConnection();
				TempTable tt = new TempTable();
				for (String tablename : tempTables) {
					try {
						tt.dropTempTable(con, tablename);
					} catch (SQLException e) {
						Logger.error("", e);
					}
				}
			} catch (SQLException e) {
				Logger.error("", e);
			} finally {
				try {
					if (con != null)
						con.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * ɾ��61ϵͳԤ�ò˵�������61��ϵͳԤ�ò˵��� �����ָ�ԭ61���ݵ�sql���ع�sql��------ update sm_menuitemreg
	 * set menuitemcode =
	 * substring(sm_menuitemreg.menuitemcode,6,len(sm_menuitemreg.menuitemcode)
	 * -5), pk_menuitem =replace(pk_menuitem,'@@','Z3')where (menuitemcode like
	 * '@@@@_2011%') and pk_menu in (select pk_menu from sm_menuregister where
	 * isdefault='Y')
	 * 
	 * @throws DAOException
	 */
	private void do61MenuDelete() throws DAOException {
		@SuppressWarnings("rawtypes")
		List c = (List) getBaseDAO().executeQuery(
				"select menuitemcode from sm_menuitemreg " + "where (menuitemcode = '201111') "
						+ "and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y')",
				new ColumnListProcessor());
		if (c.isEmpty()) {
			// ��ֹ�������̳���ԭ���µĲ��ع�����61�Ĳ˵���201111�ж��Ƿ�˵��Ѿ�������
			return;
		}
		// ���·��ò˵���ı���Ϊ @@@@_�˵���Ŀ����,PK����Ϊԭ�˵���Ŀ�������63��������ʱpk��ͻ�����ݱ���
		String updatesql = "update sm_menuitemreg set menuitemcode = '@@@@_'||menuitemcode ,pk_menuitem = replace(pk_menuitem,'Z3','@@') "
				+ "where (menuitemcode like '2011%') and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y')";
		getBaseDAO().executeUpdate(updatesql);

	}

	/**
	 * 
	 * 61Ԥ�õ�ϵͳ���ڵ�
	 * 
	 * 2011 �������� 201100 ��ʼ���� 20110002 ���õ�������-���� 20110004 ���õ�������-��֯ 20110006
	 * ����������-���� 20110008 ����������-��֯ 20110010 ��Ȩ�������� 20110012 ������Ȩ����-���� 20110014
	 * ������������ 20110016 ������������ 20110018 ������׼���� 20110024 �˱��ʼ�� 201101 �ڳ����� 201102
	 * ���ݴ��� 20110202 ���ݹ��� 20110204 ���ݲ�ѯ 201103 ����¼�� 20110301 ���÷ѽ� 20110302
	 * ����ѽ� 20110303 ���÷ѱ����� 20110304 ��ͨ�ѱ����� 20110305 ͨѶ�ѱ����� 20110306 ��Ʒ�ѱ�����
	 * 20110307 �д��ѱ����� 20110308 ����ѱ����� 20110309 ��� 201106 �ۺϴ��� 20110602 ��ĩ����
	 * 2011060202 ���� 2011060206 �������� 201109 ��ѯ 20110903 �����ϸ��ѯ 2011090301
	 * ����˽����ϸ�� 20110904 �������ѯ 2011090401 ����˽������ 20110905 ������ϸ��ѯ 2011090501
	 * �����˷�����ϸ�� 20110906 ���û��ܲ�ѯ 2011090601 �����˷��û��ܱ� 20110907 ��������ѯ 2011090701
	 * ����˽��������� 2011090701 ����˽�������ѯ 201111 ���� 20111101 ������������ 2011110101
	 * ����˽���������
	 * 
	 * 63�˵���Ŀ���� �� 61�ı����Ӧ��ϵ
	 * 
	 * 2011 ���ù��� 201100 ��ʼ���� 201100 ��ʼ���� 201101 �������� 20110102 �������뵥¼�� 201102
	 * �������� 2011 �������� 20110202 ����¼�� 201103 ����¼�� 201103 ���ú��� 201104 ��ĩ���� 20110602
	 * ��ĩ���� 201105 ��ѯ 201109 ��ѯ 20110501 �������ѯ 20110904 �������ѯ 20110502 �����ϸ��ѯ
	 * 20110903 �����ϸ��ѯ 20110503 ������������ѯ 20110907 ��������ѯ 20110504 ���û��ܲ�ѯ
	 * 20110906 ���û��ܲ�ѯ 20110505 ������ϸ��ѯ 20110905 ������ϸ��ѯ 20110506 ��������ͼ���� 20110507
	 * �������뵥ִ�������ѯ 201106 ���� 201111 ���� 20110601 ������������ 20111101 ������������ 201102
	 * ���ݴ��� 201106 �ۺϴ���
	 * 
	 * @throws DAOException
	 * 
	 * 
	 */

	private void doUpdateMenu() throws DAOException {
		@SuppressWarnings("rawtypes")
		List templist = (List) getBaseDAO().executeQuery(
				"select menuitemcode from sm_menuitemreg " + "where (menuitemcode = '@@@@_201111') "
						+ "and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y')",
				new ColumnListProcessor());
		if (templist.isEmpty()) {
			// ��ֹ�������̳���ԭ���µĲ��ع�����61�Ĳ˵���201111�ж��Ƿ�˵��Ѿ�������
			return;
		}
		// 61ԭϵͳ���˵���
		String[] menuItems61 = new String[] { "2011", "201100", "20110002", "20110004", "20110006", "20110008",
				"20110010", "20110012", "20110014", "20110016", "20110018", "20110024", "201101", "201102", "20110202",
				"20110204", "201103", "20110301", "20110302", "20110303", "20110304", "20110305", "20110306",
				"20110307", "20110308", "20110309", "201106", "20110602", "2011060202", "2011060206", "201109",
				"20110903", "2011090301", "20110904", "2011090401", "20110905", "2011090501", "20110906", "2011090601",
				"20110907", "2011090701", "2011090701", "201111", "20111101", "2011110101" };
		List<String> menuItem61List = Arrays.asList(menuItems61);
		// ���61�˵���Ŀ�����Ӧ63��
		Map<String, String> codemap = new HashMap<String, String>();
		codemap.put("201100", "201100");
		codemap.put("2011", "2011"); // ��������-���ù���
		codemap.put("201103", "20110202");
		codemap.put("20110602", "201104");
		codemap.put("201109", "201105");
		codemap.put("20110904", "20110501");
		codemap.put("20110903", "20110502");
		codemap.put("20110907", "20110503");
		codemap.put("20110906", "20110504");
		codemap.put("20110905", "20110505");
		codemap.put("201111", "201106");
		codemap.put("20111101", "20110601");
		codemap.put("201102", "201102");// ���ݴ���-��������
		codemap.put("201106", "201102");// �ۺϴ���-��������

		// 1����ѯ61�Ĳ˵�,���˵���Ŀ�������
		@SuppressWarnings("unchecked")
		Collection<MenuItemVO> c = getBaseDAO()
				.retrieveByClause(
						MenuItemVO.class,
						"(menuitemcode like '@@@@_2011%') and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y')",
						"menuitemcode");

		// 2������61�˵�������Զ���˵��������±���
		Map<String, Integer> newchildCount = new HashMap<String, Integer>();// ���ڵ���º��Ӹ���
		List<String> errCodes = new ArrayList<String>(); // ����ɾ���Ĵ���˵���
		List<MenuItemVO> newMenuItems = new ArrayList<MenuItemVO>();// �������Ĳ˵���
		for (MenuItemVO menuItemVO : c) {
			String code = menuItemVO.getMenuitemcode().split("_")[1];
			if (menuItem61List.contains(code)) {
				// ϵͳԤ�õĲ˵����룬����
				continue;
			}
			// ����Զ���˵���Ŀ�ĸ����룬��ȥ����λ
			String parentcode = code.substring(0, code.length() - 2);
			String parent63 = codemap.get(parentcode);
			if (parent63 == null) {
				// ��Ǳ��ݲ˵���Ŀ��ɾ��
				errCodes.add(menuItemVO.getMenuitemcode());
				ExceptionHandler.error("61������63���˵��������󣬲˵�����--------" /*-=notranslate=-*/ + code);
				continue;
			}

			// �����µı��,��a0��ʼ
			Integer count = newchildCount.get(parent63);
			if (count == null) {
				count = 1;
				menuItemVO.setMenuitemcode(parent63 + "a0");
			} else {
				menuItemVO.setMenuitemcode(parent63 + Integer.toHexString((Integer.parseInt("a0", 16) + count)));
				count = count + 1;
			}
			newchildCount.put(parent63, count);

			// ��Ǵ������Ĳ˵���ĿVO
			menuItemVO.setPk_menuitem(null);
			menuItemVO.setResid("D" + menuItemVO.getMenuitemcode());
			newMenuItems.add(menuItemVO);

			// ����˵���Ŀ�ǲ˵����࣬��Ҳ���뵽codemap�д���
			if (menuItemVO.getIsmenutype().booleanValue()) {
				codemap.put(code, menuItemVO.getMenuitemcode());
			}
		}
		// 3��ɾ�����ݵ�61�˵����ݣ�����Ĳ˵���Ŀ���ݲ�ɾ��
		String delSql = "(menuitemcode like '@@@@_2011%') and pk_menu in(select pk_menu from sm_menuregister where  isdefault='Y')";
		if (!errCodes.isEmpty()) {
			delSql += " and menuitemcode not in (" + StringUtil.toString(errCodes, ",", "'", "'") + ")";
		}
		getBaseDAO().deleteByClause(MenuItemVO.class, delSql);
		// 4�������µĲ˵���Ŀ
		if (!newMenuItems.isEmpty()) {
			getBaseDAO().insertVOArray(newMenuItems.toArray(new MenuItemVO[0]));
		}
	}

	private void doAssignTemplate() throws BusinessException, DAOException {
		// ��ѯ���ù����µ�ȫ�����ܽڵ�
		IFuncRegisterQueryService funcService = NCLocator.getInstance().lookup(IFuncRegisterQueryService.class);
		FuncRegisterVO[] vos = funcService.queryFunctionWhere(" own_module =  '2011' and isenable = 'Y' ");
		Map<String, FuncRegisterVO> funcMap = new HashMap<String, FuncRegisterVO>();
		for (int i = 0; i < vos.length; i++) {
			funcMap.put(vos[i].getFuncode(), vos[i]);
		}
		// ��ѯ�ڵ��µ�Ĭ��ģ�壬�Ұ��չ��ܽڵ���з���
		String funcodeInSql = SqlUtils.getInStr("funnode", funcMap.keySet().toArray(new String[0]), false);
		@SuppressWarnings("unchecked")
		Collection<SystemplateBaseVO> c = getBaseDAO().retrieveByClause(SystemplateBaseVO.class, funcodeInSql);
		@SuppressWarnings("unchecked")
		HashMap<String, List<SystemplateBaseVO>> tempMap = Hashlize.hashlizeObjects(
				c.toArray(new SystemplateBaseVO[0]), new IHashKey() {

					@Override
					public String getKey(Object arg0) {
						return ((SystemplateBaseVO) arg0).getFunnode();
					}
				});
		// ���·�ϵͳԤ�õ�ģ������templateflag���λ����ֹ��������ʱ��ʧ
		
		String selectSql = " " + funcodeInSql + " and  templateflag = 'Y' "
				+ " and pk_systemplate not in (select pub_systemplate.pk_systemplate "
				+ " from pub_systemplate ,pub_systemplate_base where "
				+ " pub_systemplate.funnode = pub_systemplate_base.funnode and "
				+ " isnull(pub_systemplate.nodekey,'~') = isnull(pub_systemplate_base.nodekey,'~') "
				+ " and pub_systemplate.templateid = pub_systemplate_base.templateid "
				+ " and pub_systemplate.moduleid = '2011')";

		Collection<SystemplateVO> templateVos = getBaseDAO().retrieveByClause(SystemplateVO.class, selectSql);

		if (templateVos != null) {
			for (SystemplateVO vo : templateVos) {
				vo.setTemplateflag(UFBoolean.FALSE);
			}

			getBaseDAO().updateVOArray(templateVos.toArray(new SystemplateVO[] {}));
		}

		// �������ڵ��ģ����䵽����������
		for (Entry<String, FuncRegisterVO> funcs : funcMap.entrySet()) {
			String funcode = funcs.getKey();
			FuncRegisterVO frvo = funcs.getValue();
			List<SystemplateBaseVO> templist = tempMap.get(funcode);
			if (frvo == null || templist == null || templist.isEmpty()) {
				continue;
			}
			ISystemTemplateAssignService systemService = NCLocator.getInstance().lookup(
					ISystemTemplateAssignService.class);
			systemService.doAssign4Group(frvo, templist.toArray(new SystemplateBaseVO[0]));
		}

		if (templateVos != null) {
			for (SystemplateVO vo : templateVos) {
				vo.setTemplateflag(UFBoolean.TRUE);
			}

			getBaseDAO().updateVOArray(templateVos.toArray(new SystemplateVO[] {}));
		}
	}

	/**
	 * ���ƽ̨����Ԥ�ýű�����
	 * 
	 * @throws BusinessException
	 */
	private void updateFipInfo() throws BusinessException {
		NCLocator.getInstance().lookup(IFipInitDataService.class).initData(BXConstans.ERM_PRODUCT_CODE);
	}

	private void insertDjlx(String[] pk_groups) throws BusinessException {
		// ��ɾ��������룬��ֹ�������̳����ظ�����

		String delSql = "delete from er_djlx where djdl in ('at','ma','cs')  and "
				+ SqlUtils.getInStr("pk_group", pk_groups, true);
		getBaseDAO().executeUpdate(delSql);

		DjLXVO[] sourcevos = NCLocator.getInstance().lookup(IArapBillTypePublic.class)
				.queryByWhereStr(" pk_group='@@@@'" + " and djdl in ('at','ma','cs') ");

		List<DjLXVO> result = new ArrayList<DjLXVO>();
		// ���Ӷ�Ӧ���ŵĽ�������
		for (DjLXVO sysvo : sourcevos) {
			for (int i = 0; i < pk_groups.length; i++) {
				DjLXVO djlx = (DjLXVO) sysvo.clone();
				djlx.setPk_group(pk_groups[i]);
				djlx.setPrimaryKey(null);
				result.add(djlx);
			}
		}

		getBaseDAO().insertVOArray(result.toArray(new DjLXVO[] {}));
	}

	private void initDataToGroup(String[] pk_groups) throws BusinessException {
		insertDjlx(pk_groups);// ��������
		// ά�ȶ��ա����ݶ���
		NCLocator.getInstance().lookup(IErmGroupPredataService.class).initGroupData(pk_groups);
		
		// ��������Ŀ�ɱ����ġ���Ŀ������
		String updateFipCenterSql = "update FIP_BILLFACTOR set ENTITY_ATTR = '$er_busitem.pk_resacostcenter'  where pk_systypecode = 'erm' "
				+ " and (PK_BILLTYPE like '264X%' or PK_BILLTYPE like '263X%') and pk_group != 'GLOBLE00000000000000' and ENTITY_ATTR = '@pk_resacostcenter'";
		getBaseDAO().executeUpdate(updateFipCenterSql);

		String updateFipJobSql = "update FIP_BILLFACTOR set ENTITY_ATTR = '$er_busitem.jobid'  where pk_systypecode = 'erm' "
				+ " and (PK_BILLTYPE like '264X%' or PK_BILLTYPE like '263X%') and pk_group != 'GLOBLE00000000000000' and ENTITY_ATTR = '@jobid'";
		getBaseDAO().executeUpdate(updateFipJobSql);

		String updateFipSzxmSql = "update FIP_BILLFACTOR set ENTITY_ATTR = '$er_busitem.szxmid'  where pk_systypecode = 'erm' "
				+ " and (PK_BILLTYPE like '264X%' or PK_BILLTYPE like '263X%') and pk_group != 'GLOBLE00000000000000' and ENTITY_ATTR = '#szxmid'";
		getBaseDAO().executeUpdate(updateFipSzxmSql);
	}
}
