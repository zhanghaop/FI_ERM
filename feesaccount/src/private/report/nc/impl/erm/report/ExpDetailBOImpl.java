package nc.impl.erm.report;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.common.ErmBillConst;
import nc.bs.erm.pub.ErmReportUtil;
import nc.bs.erm.sql.ExpDetailSQLCreator;
import nc.bs.erm.util.ErUtil;
import nc.bs.logging.Logger;
import nc.itf.erm.pub.IExpDetailBO;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.script.statement.select.PlainSelect;
import nc.pub.smart.smartprovider.ExpDetailDataProvider;
import nc.utils.fipub.FipubSqlExecuter;
import nc.utils.fipub.ReportMultiVersionSetter;
import nc.utils.fipub.SmartProcessor;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.rs.MemoryResultSet;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * ���������ʱ��ѯ��������ϸ�˲�ѯ�ӿ�ʵ���ࡣ
 * </p>
 * 
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-12-7 ����02:34:59
 */
public class ExpDetailBOImpl extends FipubSqlExecuter implements IExpDetailBO {

	@Override
    public DataSet queryExpenseDetail(ReportQueryCondVO queryVO,
			SmartContext context) throws SmartException {
		setContext(context);
		DataSet resultDataSet = new DataSet();

		/***************************************************************/
		/********************** ��������ģ�Ͷ���ʱ��У�� ********************/
		if (queryVO == null) {
			// ��������ģ��ʱ����ִ�е��������У�飬��queryVOΪ��
			try {
				resultDataSet.setMetaData(new ExpDetailDataProvider()
						.provideMetaData(null));
			} catch (SmartException e) {
				// ������Զ�����׳��쳣���ʳԵ��쳣Ҳ�������Ӱ��
			}
			return resultDataSet;
		}

		/****************************************************************/

		try {
            PlainSelect select = (PlainSelect)context.getAttribute("key_current_plain_select");
            select.setWhere(null);
			ExpDetailSQLCreator sqlCreator = new ExpDetailSQLCreator();
			// ���ò�ѯ����VO������
			sqlCreator.setParams(queryVO);

			String[] arrangeSqls = sqlCreator.getArrangeSqls();
			String resultSql = sqlCreator.getResultSql();

			// ������ʱ������
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}

			// ����ʱ��ȡ�ý��
			MemoryResultSet resultSet = (MemoryResultSet) executeQuery(resultSql, getResultProcessor());

            //ת������
            ErUtil.convertCurrtype(resultSet, queryVO);
			// ���롾���֡�����
			PubCommonReportMethod.insertNameColumn(resultSet, IPubReportConstants.CURRTYPE, "pk_currtype", "currtype");
			// ���롾�������͡�����
            PubCommonReportMethod.insertNameColumn(resultSet,
                    IPubReportConstants.BILLTYPE, "pk_billtype", "billtype",
                    200);

			// ���÷��ؽ��Ԫ����
			resultDataSet.setMetaData(SmartProcessor.getMetaData(resultSet));

			Object[][] datas = getDatas(resultSet, queryVO);
			queryVO.setQueryDetail(true);
			datas = new ReportMultiVersionSetter(resultSet.getMetaData0(), queryVO).setOrg(datas, ExpenseAccountVO.BILLDATE );
			int dateIndex = resultSet.getMetaData0().getNameIndex(ExpenseAccountVO.BILLDATE );
			PubCommonReportMethod.convert2ClientTime(datas, context, dateIndex);
			PubCommonReportMethod.setVSeq(datas, resultDataSet.getMetaData().getIndex(IPubReportConstants.ORDER_MANAGE_VSEQ));

			// ���÷��ؽ�����ݼ�
			resultDataSet.setDatas(datas);
			ErmReportUtil.processDataSet(context, resultDataSet);
		} catch (Exception e) {
			String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0053")/*@res "��ϸ�˲�ѯ����"*/;
			Logger.error(errMsg, e);
			throw new SmartException(errMsg, e);
		}

		return resultDataSet;

	}

	@SuppressWarnings("unchecked")
	private Object[][] getDatas(MemoryResultSet mrs, ReportQueryCondVO queryVO) throws SQLException {
		boolean isMultiOrg = queryVO.getPk_orgs().length > 1;

		ArrayList<List<Object>> dataRowList = mrs.getResultArrayList();

		if (dataRowList == null || dataRowList.size() == 0) {
			return new Object[0][0];
		}

		if (!IPubReportConstants.ORG_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
			String[] targetFields = new String[] { "ORG_AMOUNT_LOC" };
			String[] formulas = null;
			if (IPubReportConstants.GROUP_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// ���ű���
				formulas = new String[] { "ORG_AMOUNT_LOC->GR_GROUP_AMOUNT_LOC" };
			} else if (IPubReportConstants.GLOBLE_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// ȫ�ֱ���
				formulas = new String[] { "ORG_AMOUNT_LOC->GL_GLOBAL_AMOUNT_LOC" };
			}
			mrs.setColumnByFormulate_type(targetFields, formulas);
		}

		Object datas[][] = new Object[dataRowList.size()][mrs.getMetaData().getColumnCount()];

		int rnIndex = mrs.getColumnIndex("rn");
		int orgIndex = mrs.getColumnIndex("org");
//		int currtypeIndex = mrs.getColumnIndex(ExpenseAccountVO.PK_CURRTYPE);

        int currtypeIndex = mrs.getColumnIndex("currtype");
		
		int tallydateIndex = mrs.getColumnIndex(ExpenseAccountVO.BILLDATE);
		int accperiod = mrs.getColumnIndex(ExpenseAccountVO.ACCPERIOD);
//		int briefIndex = mrs.getColumnIndex(ExpenseAccountVO.REASON);
		//���ݱ��
		int src_billnoI = mrs.getColumnIndex(ExpenseAccountVO.SRC_BILLNO);
		int src_billtypeI = mrs.getColumnIndex("pk_billtype");
		List<Integer> qryObjIndex = new ArrayList<Integer>();
		List<Integer> qryObjNameIndex = new ArrayList<Integer>();
		List<Integer> totalCol = new ArrayList<Integer>(); // С����
        List<Integer> totalCol2 = new ArrayList<Integer>(); // С����
		for (int i = 0; i < queryVO.getQryObjs().size(); i++) {
			qryObjIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i));
			qryObjNameIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i + "code"));
            totalCol2.add(qryObjNameIndex.get(i));
			totalCol.add(qryObjIndex.get(i));
		}
		totalCol.add(currtypeIndex);
        totalCol2.add(orgIndex);

        Map<String, Map<String, String>> countMap = new HashMap<String, Map<String, String>>();
        
		Object[] dataRow = null;
		int rn = -1;
		boolean isObj = false;
		
		boolean isCurrtype = false;
		String[] amount_ori = new String[] {"assume_amount_ori"};
        int[] currtypeCount = new int[amount_ori.length];
        int[] orgCurrtypeCount = new int[amount_ori.length];
		int[] amount_ori_pos = new int[amount_ori.length];
		for (int nPos = 0; nPos < amount_ori.length; nPos++) {
		    amount_ori_pos[nPos] = mrs.getColumnIndex(amount_ori[nPos]);
		}
		for (int i = 0; i < dataRowList.size(); i++) {
			dataRow = dataRowList.get(i).toArray();
			rn = Integer.parseInt(dataRow[rnIndex - 1].toString());
			isObj = false;
			
			for (int nPos = 0; nPos < totalCol2.size(); nPos++) {
                String key = (String)dataRow[totalCol2.get(nPos) - 1];
                String currtype = (String)dataRow[currtypeIndex - 1];
                if (StringUtils.isEmpty(key)) {
                    continue;
                }
                Map<String, String> currtypeMap = countMap.get(key); 
                if (currtypeMap == null) {
                    currtypeMap = new HashMap<String, String>();
                    countMap.put(key, currtypeMap);
                }
                if (StringUtils.isNotEmpty(currtype)) {
                    currtypeMap.put(currtype, null);
                }
            }
			
			if (rn >= SmartProcessor.MAX_ROW) {
				// ����ϼ���
				if (rn == SmartProcessor.MAX_ROW) {
				} else {
					int k = totalCol.size() - 1;
                    int curRowQryObjIndex = 0;
					for (; k >= 0; k--) {
						if (dataRow[totalCol.get(k) - 1] != null && !"".equals(dataRow[totalCol.get(k) - 1])) {
							if (k == totalCol.size() - 1) {
								// ����С��
								dataRow[currtypeIndex - 1] = dataRow[currtypeIndex - 1]
										+ IErmReportConstants.getConst_Sub_Total(); // С��
								if (!isCurrtype) {
                                    for (int nPos = 0; nPos < orgCurrtypeCount.length; nPos++) {
                                        currtypeCount[nPos] = 0;
                                    }
                                }
                                for (int nPos = 0; nPos < orgCurrtypeCount.length; nPos++) {
                                    if (dataRow[amount_ori_pos[nPos] - 1] != null && 
                                            BigDecimal.ZERO.compareTo((BigDecimal)dataRow[amount_ori_pos[nPos] - 1]) != 0) {
                                        currtypeCount[nPos]++;
                                        orgCurrtypeCount[nPos]++;
                                    }
                                }
                                isCurrtype = true;
							} else {
								// ��ѯ����С��
								dataRow[qryObjIndex.get(k) - 1] = dataRow[qryObjIndex.get(k) - 1]
										+ IErmReportConstants.getConst_Sub_Total(); // С��
//								dataRow[qryObjNameIndex.get(k) - 1] = dataRow[qryObjNameIndex.get(k) - 1]
//										+ IErmReportConstants.getConst_Sub_Total(); // С��
								isCurrtype = false;
								curRowQryObjIndex = qryObjNameIndex.get(k);
							}
							isObj = true;
							break;
						}
					}
					
					for (k--; k >= 0; k--) {
						dataRow[qryObjIndex.get(k) - 1] = "";
						dataRow[qryObjNameIndex.get(k) - 1] = "";
					}

					if (!isObj && isMultiOrg && (dataRow[orgIndex - 1] == null
							|| dataRow[orgIndex - 1].toString().length() == 0)) {
//						dataRow[briefIndex - 1] = IErmReportConstants.getCONST_ALL_TOTAL(); // �ܼ�
						dataRow[orgIndex - 1] = IErmReportConstants.getCONST_ALL_TOTAL(); // �ܼ�
						// ����֯���������ս���ֶ���Ϣ
						if (!isCurrtype) {
                            // �������ս���ֶ���Ϣ
                            for (int nPos = 0; nPos < amount_ori.length; nPos++) {
                                if (currtypeCount[nPos] > 1) {
                                    dataRow[amount_ori_pos[nPos] - 1] = null;
                                }
                            }
                        }
					} else if (!isObj) {
                        String org = (String)dataRow[orgIndex - 1];
						dataRow[orgIndex - 1] = dataRow[orgIndex - 1] + IErmReportConstants.getCONST_AGG_TOTAL(); // �ϼ�
						dataRow[tallydateIndex - 1] = "";
						if (!isCurrtype) {
                            // �������ս���ֶ���Ϣ
                            Map<String, String> currtypeMap = countMap.get(org); 
                            for (int nPos = 0; nPos < amount_ori.length; nPos++) {
                                if (orgCurrtypeCount[nPos] > 1 && currtypeMap.keySet().size() > 1) {
                                    dataRow[amount_ori_pos[nPos] - 1] = null;
                                }
                                orgCurrtypeCount[nPos] = 0;
                            }
                        }
					} else {
						dataRow[orgIndex - 1] = "";
					}
                    if (!isCurrtype && curRowQryObjIndex > 0) {
                        String curRowQryObj = (String)dataRow[curRowQryObjIndex - 1];
                        Map<String, String> currtypeMap = countMap.get(curRowQryObj); 
                        // �������ս���ֶ���Ϣ
                        for (int nPos = 0; nPos < amount_ori.length; nPos++) {
                            if (currtypeCount[nPos] > 1 && currtypeMap != null && currtypeMap.keySet().size() > 1) {
                                dataRow[amount_ori_pos[nPos] - 1] = null;
                            }
                        }
                    }
				}
			}
			//�Է�̯�����ݱ���Ը�ʽΪ:��������+����
			datas[i] = dataRow;
			if(datas[i][src_billtypeI-1] != null && ErmBillConst.Expamoritize_BILLTYPE.equals(datas[i][src_billtypeI-1])){
					String accperiodval = (String) datas[i][accperiod-1];
					datas[i][src_billnoI-1] = ErmBillConst.Expamoritize_BILLTYPE+accperiodval;
			}
		}

		return datas;
	}

	@Override
	public Object queryExpenseDetailResultSet(ReportQueryCondVO queryVO,
			SmartContext context, ResultSetProcessor processor)
			throws BusinessException {
		                             
		return null;
	}

}

// /:~
