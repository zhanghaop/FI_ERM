package nc.bs.erm.accruedexpense.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;

public class ErmAccruedBillConst {
	
	public static final String AccruedBill_MDID = "61a2bcdb-e040-44d7-949a-54aa632591f5";
	
	public static final String Accrued_Lock_Key = "Erm_accruedexpense";
	
	/**
	 * Ԫ���ݱ��루���壩
	 */
	public static final String Accrued_MDCODE_DETAIL = "accrued_detail";
	public static final String Accrued_MDCODE_VERIFY = "accrued_verify";
	
	/**
	 * ����־-���
	 */
	public static final int REDFLAG_RED = 1;
	/**
	 * ����־-�����
	 */
	public static final int REDFLAG_REDED = 2;
	/**
	 * ����־-һ�㵥��
	 */
	public static final int REDFLAG_NO = 0;

	
	public static final int EFFECTSTATUS_NO = 0; // ��Ч��־����δ��Ч
	public static final int EFFECTSTATUS_VALID = 1; // ��Ч��־������Ч
	public static final int EFFECTSTATUS_Temp = 2; // ��Ч��־�����ݴ�
	
	public static final int BILLSTATUS_TEMPSAVED = 0;  //����״̬�����ݴ�
	public static final int BILLSTATUS_SAVED = 1;	//����״̬��������
	public static final int BILLSTATUS_APPROVED = 3;	//����״̬-������
	public static final String BILLSTATUS_TEMPSAVED_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
			"201212_0", "0201212-0079")/* @res "�ݴ�" */;// ����״̬�����ݴ�
	public static final String BILLSTATUS_SAVED_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
			"201212_0", "0201212-0080")/* @res "����" */; // ����״̬��������
	public static final String BILLSTATUS_APPROVED_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
			"201212_0", "0201212-0082")/* @res "������" */; // ������
	
	public static final String DJDL = "ac";
	public static final String AccruedBill_Billtype = "262X";
	public static final String AccruedBill_Tradetype_Travel = "2621";
	
	/**
	 * Ԥ�ᵥ��ѯ�ڵ�
	 */
	public static final String ACC_NODECODE_QRY = "20110ACCQRY";
	
	/**
	 * Ԥ�ᵥ����ڵ�
	 */
	public static final String ACC_NODECODE_MN = "20110ACCMN";
	
	/**
	 * Ԥ�ᵥ¼��ڵ�
	 */
	public static final String ACC_NODECODE_TRAVEL = "201102621";
	
	/**
	 * Ԥ�ᵥ��������
	 */
	public static final String 	ACCRUED_MD_INSERT_OPER = "318db344-681f-49ea-a9ef-1fd879bc77b6";
	/**
	 * Ԥ�ᵥ�޸Ĳ���
	 */
	public static final String ACCRUED_MD_UPDATE_OPER = "36107e64-2312-459e-b89f-8a4193ed3548";
	/**
	 * Ԥ�ᵥɾ������
	 */
	public static final String ACCRUED_MD_DELETE_OPER = "82bbe3fc-8177-429a-bb07-7360a1203bf0";
	/**
	 * Ԥ�ᵥ�ύ����
	 */
	public static final String ACCRUED_MD_COMMIT_OPER = "7ce92bde-0d9e-42dd-96cd-57c1999df9f5";
	/**
	 * Ԥ�ᵥ�ջز���
	 */
	public static final String ACCRUED_MD_RECALL_OPER = "ad03341d-98da-4e7e-a3ea-d4ce55bf385b";
	/**
	 * Ԥ�ᵥ��������
	 */
	public static final String ACCRUED_MD_APPROVE_OPER = "d57a1d73-83ab-42ff-8b2e-290f2435f987";
	/**
	 * Ԥ�ᵥȡ����������
	 */
	public static final String ACCRUED_MD_UNAPPROVE_OPER = "f5c282e8-cd8d-4860-aab4-af56eef2df05";
	
	
	public static final Set<String> excelInputHeadItems = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
			AccruedVO.PK_TRADETYPE,AccruedVO.PK_TRADETYPEID, AccruedVO.BILLDATE, AccruedVO.PK_ORG, AccruedVO.PK_CURRTYPE,
			AccruedVO.AMOUNT, AccruedVO.OPERATOR, AccruedVO.OPERATOR_DEPT, AccruedVO.OPERATOR_ORG,
			AccruedVO.BILLNO)));
	
	public static final Set<String> excelInputBodyItems = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
			AccruedDetailVO.ASSUME_ORG, AccruedDetailVO.ASSUME_DEPT, AccruedDetailVO.AMOUNT)));
}
