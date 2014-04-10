package nc.impl.erm.report;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.sql.ErmBaseSqlCreator;
import nc.bs.erm.sql.LoanBalanceSQLCreator;
import nc.bs.logging.Logger;
import nc.itf.erm.pub.ILoanBalanceBO;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.jdbc.framework.util.DBConsts;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.smartprovider.LoanBalanceDataProvider;
import nc.utils.fipub.FipubSqlExecuter;
import nc.utils.fipub.ReportMultiVersionSetter;
import nc.utils.fipub.SmartProcessor;
import nc.vo.erm.pub.ErmCommonReportMethod;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.rs.MemoryResultSet;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *   ���������ʱ��ѯ�������ϸ�˲�ѯ�ӿ�ʵ���ࡣ
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li> <br>
 * <br>
 *
 * @see
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-12-7 ����02:34:59
 */
public class LoanBalanceBOImpl extends FipubSqlExecuter implements ILoanBalanceBO {

	public DataSet queryLoanBalance(ReportQueryCondVO queryVO, SmartContext context) throws SmartException {
		setContext(context);
		DataSet resultDataSet = new DataSet();
		/***************************************************************/
		/********************** ��������ģ�Ͷ���ʱ��У�� ********************/

		if (queryVO == null) {
			// ��������ģ��ʱ����ִ�е��������У�飬��queryVOΪ��
			try {
				resultDataSet.setMetaData(new LoanBalanceDataProvider()
						.provideMetaData(null));
			} catch (SmartException e) {
				// ������Զ�����׳��쳣���ʳԵ��쳣Ҳ�������Ӱ��
			}
			return resultDataSet;
		}
		/****************************************************************/

		try {
			ErmBaseSqlCreator sqlCreator = new LoanBalanceSQLCreator();

			// ���ò�ѯ����VO������
			sqlCreator.setParams(queryVO);

			String[] arrangeSqls = sqlCreator.getArrangeSqls();
			String resultSql = sqlCreator.getResultSql();
			String[] dropTableSqls = sqlCreator.getDropTableSqls();

			// ������ʱ������
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}

			// ����ʱ��ȡ�ý��
			MemoryResultSet result = (MemoryResultSet) executeQuery(resultSql, getResultProcessor());

			// ɾ����ʱ��
			for (String sql : dropTableSqls) {
				executeUpdate(sql);
			}

			// ���롾���֡�����
			PubCommonReportMethod.insertNameColumn(result, IPubReportConstants.CURRTYPE, "pk_currtype", "currtype");

			String sign = "+";
			ErmCommonReportMethod.computeEndPeriodBalance(result, sign);

			// ���÷��ؽ��Ԫ����
			if(getBaseDAO().getDBType()==DBConsts.DB2){
				// ���÷��ؽ��Ԫ����
				resultDataSet.setMetaData(SmartProcessor.getMetaDataForDB2(result));
			}else {
				resultDataSet.setMetaData(SmartProcessor.getMetaData(result));
			}

			Object[][] datas = getDatasForBalance(result, queryVO);
			datas = new ReportMultiVersionSetter(result.getMetaData0(), queryVO).setOrg(datas, null);
			datas = getDatasForBalance(result, queryVO);
            PubCommonReportMethod.setVSeq(datas, resultDataSet.getMetaData().getIndex(IPubReportConstants.ORDER_MANAGE_VSEQ));
			// ���÷��ؽ�����ݼ�
			resultDataSet.setDatas(datas);
		} catch (Exception e) {
			String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0054"); /*@res "��������ѯ����"*/
			Logger.error(errMsg, e);
			throw new SmartException(errMsg, e);
		}

		return resultDataSet;
	}

	/**
	 * ���ܣ����ڴ�����ת��Ϊ��ά��<br>
	 * 
	 * @param mrs �ڴ�����<br>
	 * @return �ڴ�������Ӧ�Ķ�ά��<br>
	 * @throws SQLException
	 * <br>
	 */
	@SuppressWarnings("unchecked")
	private Object[][] getDatasForBalance(MemoryResultSet mrs, ReportQueryCondVO queryVO) throws SQLException {

		ArrayList<List<Object>> dataRowList = mrs.getResultArrayList();

		if (dataRowList == null || dataRowList.size() == 0) {
			return new Object[0][0];
		}

		if (!IPubReportConstants.ORG_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
			String[] targetFields = new String[] { "init_loc", "jk_loc", "hk_loc", "bal_loc" };
			String[] formulas = null;
			if (IPubReportConstants.GROUP_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// ���ű���
				formulas = new String[] { "init_loc->gr_init_loc", "jk_loc->gr_jk_loc", "hk_loc->gr_hk_loc", "bal_loc->gr_bal_loc" };
			} else if (IPubReportConstants.GLOBLE_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
				// ȫ�ֱ���
				formulas = new String[] { "init_loc->gl_init_loc", "jk_loc->gl_jk_loc", "hk_loc->gl_hk_loc", "bal_loc->gl_bal_loc" };
			}
			mrs.setColumnByFormulate_type(targetFields, formulas);
		}
		Object datas[][] = new Object[dataRowList.size()][mrs.getMetaData().getColumnCount()];

		//////////////////////////////
		List<Integer> qryObjIndex = new ArrayList<Integer>();
        List<Integer> qryObjNameIndex = new ArrayList<Integer>();
        List<Integer> totalCol = new ArrayList<Integer>(); // С����
        List<Integer> totalCol2 = new ArrayList<Integer>(); // С����
        int orgIndex = mrs.getColumnIndex("org");
        int currtypeIndex = mrs.getColumnIndex("currtype");
        for (int i = 0; i < queryVO.getQryObjs().size(); i++) {
            qryObjIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i));
            qryObjNameIndex.add(mrs.getColumnIndex(IPubReportConstants.QRY_OBJ_PREFIX + i + "code"));
            totalCol.add(qryObjIndex.get(i));
            totalCol2.add(qryObjNameIndex.get(i));
        }
        totalCol.add(currtypeIndex);
        totalCol2.add(orgIndex);

        boolean isCurrtype = false;
        String[] amount_ori = new String[] {"init_ori", "jk_ori", "hk_ori", "bal_ori"};
        int[] currtypeCount = new int[amount_ori.length];
        int[] orgCurrtypeCount = new int[amount_ori.length];
        int[] amount_ori_pos = new int[amount_ori.length];
        for (int nPos = 0; nPos < amount_ori.length; nPos++) {
            amount_ori_pos[nPos] = mrs.getColumnIndex(amount_ori[nPos]);
        }
        
        Map<String, Map<String, String>> countMap = new HashMap<String, Map<String, String>>();
        
        Object[] dataRow = null;
        int rn = -1;
        boolean isObj = false;
        boolean isMultiOrg = queryVO.getPk_orgs().length > 1;
		
		////////////////////////////////////////////////////////////////////////
		int rnIndex = mrs.getColumnIndex("rn");
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
//                if (rn == SmartProcessor.MAX_ROW) {
                    // rn == SmartProcessor.MAX_ROW������С��
//                    dataRow[tallydateIndex - 1] = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0055")/*@res "����С��"*/;
                    // ��С����ĩ���=��һ����ĩ���
//                    dataRow[oriIndex - 1] = datas[i - 1][oriIndex - 1];
//                    dataRow[locIndex - 1] = datas[i - 1][locIndex - 1];
//                    dataRow[grlocIndex - 1] = datas[i - 1][grlocIndex - 1];
//                    dataRow[gllocIndex - 1] = datas[i - 1][gllocIndex - 1];
//                    int j = qryObjIndex.size() - 1;
//                    for (; j >= 0; j--) {
//                        dataRow[qryObjIndex.get(j) - 1] = "";
//                        dataRow[qryObjNameIndex.get(j) - 1] = "";
//                    }
//                    dataRow[currtypeIndex - 1] = "";
//                    dataRow[orgIndex - 1] = "";
//                } else {
                    int k = totalCol.size() - 1;
                    int curRowQryObjIndex = 0;
                    for (; k >= 0; k--) {
                        if (dataRow[totalCol.get(k) - 1] != null && !"".equals(dataRow[totalCol.get(k) - 1])) {
                            if (k == totalCol.size() - 1) {
                                // ����С��
                                dataRow[currtypeIndex - 1] = dataRow[currtypeIndex - 1] + IErmReportConstants.getConst_Sub_Total(); // С��
                                
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
                                dataRow[qryObjIndex.get(k) - 1] = dataRow[qryObjIndex.get(k) - 1] + IErmReportConstants.getConst_Sub_Total(); // С��
//                              dataRow[qryObjNameIndex.get(k) - 1] = dataRow[qryObjNameIndex.get(k) - 1] + IErmReportConstants.getConst_Sub_Total(); // С��
                                curRowQryObjIndex = qryObjNameIndex.get(k);
                                isCurrtype = false;
                            }
                            isObj = true;
                            break;
                        }
                    }

                    for (k--; k >= 0; k--) {
                        dataRow[qryObjIndex.get(k) - 1] = "";
                        dataRow[qryObjNameIndex.get(k) - 1] = "";
                    }

                    if (!isObj&& isMultiOrg && (dataRow[orgIndex - 1] == null || dataRow[orgIndex - 1].toString().length() == 0)) {
//                        dataRow[briefIndex - 1] = IErmReportConstants.CONST_ALL_TOTAL; // �ܼ�
                        dataRow[orgIndex - 1] = "";
                        // ����֯���������ս���ֶ���Ϣ
//                        if (!isCurrtype) {
//                            // �������ս���ֶ���Ϣ
//                            for (int nPos = 0; nPos < amount_ori.length; nPos++) {
//                                if (currtypeCount[nPos] > 1) {
//                                    dataRow[amount_ori_pos[nPos] - 1] = BigDecimal.ZERO;
//                                }
//                            }
//                        }

                        String org = (String)dataRow[orgIndex - 1];
                        if (!isCurrtype) {
                            // �������ս���ֶ���Ϣ
                            Map<String, String> currtypeMap = countMap.get(org); 
                            for (int nPos = 0; nPos < amount_ori.length; nPos++) {
                                if (currtypeMap != null && currtypeMap.keySet().size() > 1) {
                                    dataRow[amount_ori_pos[nPos] - 1] = BigDecimal.ZERO;
                                }
                                orgCurrtypeCount[nPos] = 0;
                            }
                        }
                    } else if (!isObj) {
                        String org = (String)dataRow[orgIndex - 1];
                        dataRow[orgIndex - 1] = dataRow[orgIndex - 1] + IErmReportConstants.getCONST_AGG_TOTAL(); // �ϼ�
//                        dataRow[tallydateIndex - 1] = "";
                        if (!isCurrtype) {
                            // �������ս���ֶ���Ϣ
                            Map<String, String> currtypeMap = countMap.get(org); 
                            for (int nPos = 0; nPos < amount_ori.length; nPos++) {
                                if (currtypeMap != null && currtypeMap.keySet().size() > 1) {
                                    dataRow[amount_ori_pos[nPos] - 1] = BigDecimal.ZERO;
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
                            if (currtypeMap != null && currtypeMap.keySet().size() > 1) {
                                dataRow[amount_ori_pos[nPos] - 1] = BigDecimal.ZERO;
                            }
                        }
                    }
//                }
            }
            datas[i] = dataRow;
		}

		return datas;
	}

	@Override
	public Object queryLoanBalanceResultSet(ReportQueryCondVO queryVO,
			SmartContext context, ResultSetProcessor processor)
			throws BusinessException {
		try {
			setContext(context);
			ErmBaseSqlCreator sqlCreator = new LoanBalanceSQLCreator();
			// ���ò�ѯ����VO������
			sqlCreator.setParams(queryVO);

			String[] arrangeSqls = sqlCreator.getArrangeSqls();
			String resultSql = sqlCreator.getResultSql();
			String[] dropTableSqls = sqlCreator.getDropTableSqls();

			// ������ʱ������
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}

			// ����ʱ��ȡ�ý��
			Object result = executeQuery(resultSql, processor);

			// ɾ����ʱ��
			for (String sql : dropTableSqls) {
				executeUpdate(sql);
			}

			return result;
		} catch (SQLException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

}

// /:~
