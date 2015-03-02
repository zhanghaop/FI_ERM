package nc.vo.ep.bx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.pub.ErCorpUtil;
import nc.vo.er.pub.IFYControl;
import nc.vo.er.util.StringUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BeanHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;

/**
 * �����൥�ݱ���VO
 * 
 * @author ROCKING
 * @author twei
 * 
 *         nc.vo.ep.bx.BXHeaderVO
 */
public abstract class JKBXHeaderVO extends SuperVO implements IFYControl {

	private static final long serialVersionUID = -936531187472578799L;
	
	/**
	 * �Ƿ�����˳��õ���
	 */
	private boolean isLoadInitBill = false;
	
	/**
	 * ���л�д�������뵥ʹ�ã���¼��д���뵥����ϸ��pk
	 */
	private String pk_mtapp_detail = null;

	/**
	 * ��д�������뵥ʹ�ã���¼ҵ����pk
	 */
	private String pk_busitem = null;
	
	/**
	 * ��д�������뵥-������ϸ���ݰ�װʹ�ã��������еĽ���ϸpk
	 */
	private String jk_busitemPK;
	/**
	 * ��д�������뵥-������ϸ���ݰ�װʹ�ã��������еı�������ϸpk
	 */
	private String bx_busitemPK;
	
	/**
	 * ֧����֯
	 */
	public static String PK_PAYORG = "pk_payorg";
	public static String PK_PAYORG_V = "pk_payorg_v";
	/**
	 * �ɱ�����
	 */
	public static String PK_RESACOSTCENTER = "pk_resacostcenter";

	/**
	 * �ֽ��ʻ�
	 */
	public static String PK_CASHACCOUNT = "pk_cashaccount";

	/**
	 * �ֽ��ʻ�v6.1�����ֶΣ��͵�λ�����ʺŶ�����һ������
	 */
	protected String pk_cashaccount;

	/**
	 * �ɱ�����v6.1�����ֶΣ�Ԥ�����γ��
	 */
	protected String pk_resacostcenter;

	/**
	 * ������Ϣ��ͷǰ׺
	 */
	public static String SETTLE_HEAD_PREFIX = "zb.";

	/**
	 * �����ͷVO
	 */
	protected SuperVO settleHeadVO;

	/**
	 * ���������ʾ���ڣ�����ʾʱ��
	 */
	public static final String SHRQ_SHOW = "shrq_show";

	/**
	 * ���������ʾ���ڣ�����ʾʱ��
	 */
	public UFDate shrq_show;

	/**
	 * �����г�ʼ�����ֶ�, ���ڿ��Ƴ��õ��ݵļ���
	 * 
	 * @return
	 */
	public static String[] getFieldNotInit() {
		return new String[] { JKBXR, DJLXBM, PK_GROUP, OPERATOR, MODIFIER, MODIFIEDTIME, CREATOR, CREATIONTIME,
				PK_JKBX, DJBH, DJRQ, TS, DJZT, KJND, KJQJ, PAYMAN, PAYDATE, PAYFLAG, PK_ORG_V, FYDWBM_V, DWBM_V,
				PK_PCORG_V, DEPTID_V, FYDEPTID_V, DWBM, DEPTID, BBHL, SKYHZH, JSR, APPROVER, START_PERIOD,
				RECEIVER};
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public void setGroupbbye(UFDouble groupbbye) {
		this.groupbbye = groupbbye;
	}

	/**
	 * �����п������ֶ�,���ڿ��Ƶ��ݵĸ��ƹ���
	 * 
	 * @return
	 */
	public static String[] getFieldNotCopy() {
		return new String[] { ZHRQ, SXBZ, JSH, DJZT, SPZT, TS, DR, MODIFIER, OPERATOR, APPROVER, MODIFIEDTIME,VOUCHER,
				PK_JKBX, DJBH, DJRQ, KJND, KJQJ, JSRQ, SHRQ, JSR, CONTRASTENDDATE, PAYMAN, PAYDATE, PAYFLAG ,BBHL,GROUPBBHL,GLOBALBBHL,
				START_PERIOD,SHRQ_SHOW,VOUCHERTAG, RED_STATUS, REDBILLPK};
	}

	/**
	 * ����ֶ�, ���ڽ��е��ݵĺϲ�
	 * 
	 * @return
	 */
	public static String[] getJeField() {
		return new String[] { CJKYBJE, CJKBBJE, ZFYBJE, ZFBBJE, HKYBJE, HKBBJE, YBJE, BBJE, TOTAL, YBYE, BBYE,
				GLOBALCJKBBJE, GLOBALZFBBJE, GLOBALHKBBJE, GLOBALBBJE, GLOBALBBYE, GROUPCJKBBJE, GROUPZFBBJE,
				GROUPHKBBJE, GROUPBBJE, GROUPBBYE };
	}

	/**
	 * ����ԭ�ҽ���ֶ�
	 * 
	 * @return
	 */
	public static String[] getYbjeField() {
		return new String[] { CJKYBJE, ZFYBJE, HKYBJE, YBJE, TOTAL, YBYE, YJYE };
	}

	/**
	 * ���ر��ҽ���ֶ�
	 * 
	 * @return
	 */
	public static String[] getBbjeField() {
		return new String[] { CJKBBJE, ZFBBJE, HKBBJE, BBJE, BBYE, GLOBALCJKBBJE, GLOBALZFBBJE, GLOBALHKBBJE,
				GLOBALBBJE, GLOBALBBYE, GROUPCJKBBJE, GROUPZFBBJE, GROUPHKBBJE, GROUPBBJE, GROUPBBYE };
	}

	/**
	 * ������֯���ҽ���ֶ�
	 * 
	 * @return
	 */
	public static String[] getOrgBbjeField() {
		return new String[] { CJKBBJE, ZFBBJE, HKBBJE, BBJE, BBYE };
	}

	/**
	 * ���ر�ͷ���ű��ҽ���ֶ�
	 * 
	 * @author chendya
	 * @return
	 */
	public static String[] getHeadGroupBbjeField() {
		return new String[] { GROUPCJKBBJE, GROUPZFBBJE, GROUPHKBBJE, GROUPBBJE, GROUPBBYE };
	}

	/**
	 * 
	 * ���ر�ͷȫ�ֱ��ҽ���ֶ�
	 * 
	 * @author chendya
	 * @return
	 */
	public static String[] getHeadGlobalBbjeField() {
		return new String[] { GLOBALCJKBBJE, GLOBALZFBBJE, GLOBALHKBBJE, GLOBALBBJE, GLOBALBBYE };
	}

	/**
	 * <p>
	 * ȡ�ñ�����.
	 * <p>
	 * ��������:2007-6-13
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getPKFieldName() {
		return PK_JKBX;
	}

	/**
	 * <p>
	 * ���ر�����.
	 * <p>
	 * ��������:2007-6-13
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getTableName() {

		if (isInit)
			return "er_jkbx_init";

		String tableName = "er_bxzb";

		if (djdl != null) {
			if (djdl.equals(BXConstans.JK_DJDL))
				tableName = "er_jkzb";
		}
		return tableName;

	}

	/**
	 * ������ֵ�������ʾ����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return java.lang.String ������ֵ�������ʾ����.
	 */
	@Override
	public String getEntityName() {

		return "er_bxzb";

	}

	/**
	 * ��֤���������֮��������߼���ȷ��.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @exception nc.vo.pub.ValidationException
	 *                �����֤ʧ��,�׳� ValidationException,�Դ�����н���.
	 */
	@Override
	public void validate() throws ValidationException {

		validateNullField();
		validateNotNullField2();
	}

	protected void validateNotNullField2() {

		Map<String, String> map = new HashMap<String, String>();
		map.put(JKBXHeaderVO.YBJE, JKBXHeaderVO.ZPXE);

		StringBuffer message = new StringBuffer();
		message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000285")/*
																									 * @
																									 * res
																									 * "��ͷ�����ֶβ���ͬʱΪ��:"
																									 */);

		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (getFieldName(key) == null && null == getFieldName(value)) {
				message.append("\n");
				message.append(key + "-" + value);
			}
		}
	}

	protected String djlxmc;

	public String getDjlxmc() {
		return djlxmc;
	}

	public void setDjlxmc(String djlxmc) {
		this.djlxmc = djlxmc;
	}

	protected void validateNullField() throws NullFieldException {
		ArrayList<String> errFields = new ArrayList<String>(); // errFields
																// record those
																// null
		List<String> notNullFields = null; // errFields record those null
		// FIXME ��ʱע����
		String[] str = { JKBXHeaderVO.DJRQ, JKBXHeaderVO.DWBM, JKBXHeaderVO.JKBXR, JKBXHeaderVO.BZBM,
				JKBXHeaderVO.BBHL, JKBXHeaderVO.YBJE, JKBXHeaderVO.BBJE, JKBXHeaderVO.YBYE, JKBXHeaderVO.BBYE,
				JKBXHeaderVO.FYDWBM, JKBXHeaderVO.PK_ORG, JKBXHeaderVO.OPERATOR, JKBXHeaderVO.PK_GROUP, };
		notNullFields = Arrays.asList(str);

		for (String field : notNullFields) {
			if (getAttributeValue(field) == null)
				errFields.add(getFieldName(field));
		}

		StringBuffer message = new StringBuffer();
		message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000286")/*
																									 * @
																									 * res
																									 * "��ͷ�����ֶβ���Ϊ��:\n"
																									 */);
		if (errFields.size() > 0) {
			String[] temp = errFields.toArray(new String[0]);
			message.append(temp[0]);
			for (int i = 1; i < temp.length; i++) {
				message.append(",");
				message.append(temp[i]);
			}
			throw new NullFieldException(message.toString());
		}
	}

	public String getFieldName(String field) {
		// ע��������֯�ͼ��żӶ���
		if (field.equals(PK_ORG))
			if (djdl.equals(BXConstans.BX_DJDL))
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0131")/*
																											 * @
																											 * res
																											 * "������λ"
																											 */;
			else
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0132")/*
																											 * @
																											 * res
																											 * "��λ"
																											 */;
		else if (field.equals(PK_GROUP))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC001-0000072")/*
																									 * @
																									 * res
																									 * "����"
																									 */;
		// public static final String CUSTACCOUNT = "custaccount";
		// public static final String FREECUST = "freecust";
		else if (field.equals(CUSTACCOUNT))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0133")/*
																										 * @
																										 * res
																										 * "���������˺�"
																										 */;
		else if (field.equals(FREECUST))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0002272")/*
																									 * @
																									 * res
																									 * "ɢ��"
																									 */;
		else if (field.equals(FYDEPTID))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000223")/*
																								 * @
																								 * res
																								 * "���óе�����"
																								 */;
		else if (field.equals(FYDWBM))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000287")/*
																								 * @
																								 * res
																								 * "���óе���˾"
																								 */;
		else if (field.equals(DWBM))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000288")/*
																								 * @
																								 * res
																								 * "�����˹�˾"
																								 */;
		else if (field.equals(YBJE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000280")/*
																								 * @
																								 * res
																								 * "ԭ�ҽ��"
																								 */;
		else if (field.equals(BBJE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000245")/*
																								 * @
																								 * res
																								 * "���ҽ��"
																								 */;
		else if (field.equals(ISCHECK))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000289")/*
																								 * @
																								 * res
																								 * "֧Ʊ���"
																								 */;
		else if (field.equals(ISINITGROUP))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0134")/*
																										 * @
																										 * res
																										 * "���ų��õ���"
																										 */;
		else if (field.equals(ZPXE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000290")/*
																								 * @
																								 * res
																								 * "֧Ʊ�޶�"
																								 */;
		else if (field.equals(BZBM))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000291")/*
																								 * @
																								 * res
																								 * "���ֱ���"
																								 */;
		else if (field.equals(JKBXR))
			if (djdl.equals(BXConstans.BX_DJDL))
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000292")/*
																									 * @
																									 * res
																									 * "������"
																									 */;
			else
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000249")/*
																									 * @
																									 * res
																									 * "�����"
																									 */;
		else if (field.equals(PJH))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000293")/*
																								 * @
																								 * res
																								 * "Ʊ�ݺ�"
																								 */;
		else if (field.equals(CHECKTYPE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0003020")/*
																									 * @
																									 * res
																									 * "Ʊ������"
																									 */;
		else if (field.equals(BBHL))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000294")/*
																								 * @
																								 * res
																								 * "���һ���"
																								 */;
		else if (field.equals(SKYHZH))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000295")/*
																								 * @
																								 * res
																								 * "�տ������˺�"
																								 */;
		else if (field.equals(FKYHZH))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000296")/*
																								 * @
																								 * res
																								 * "���������˺�"
																								 */;
		else if (field.equals(JSH))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000297")/*
																								 * @
																								 * res
																								 * "�����"
																								 */;
		else if (field.equals(JSFS))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000047")/*
																								 * @
																								 * res
																								 * "���㷽ʽ"
																								 */;
		else if (field.equals(TOTAL))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000298")/*
																								 * @
																								 * res
																								 * "�ϼƽ��"
																								 */;
		else if (field.equals(DJRQ))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000248")/*
																								 * @
																								 * res
																								 * "��������"
																								 */;
		else if (field.equals(KJND))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPT2011-000709")/*
																								 * @
																								 * res
																								 * "������"
																								 */;
		else if (field.equals(KJQJ))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPT2011-000715")/*
																								 * @
																								 * res
																								 * "����ڼ�"
																								 */;

		else if (field.equals(SZXMID))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000224")/*
																								 * @
																								 * res
																								 * "��֧��Ŀ"
																								 */;
		else if (field.equals(JOBID))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000221")/*
																								 * @
																								 * res
																								 * "��Ŀ"
																								 */;
		else if (field.equals(PROJECTTASK))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UCMD1-000312")/*
																								 * @
																								 * res
																								 * "��Ŀ����"
																								 */;
		else
			return field;
	}

	public String pk_payorg;// ֧����֯
	public String pk_payorg_v;// ֧����֯

	public Integer payflag; // ֧����־
	public String cashproj; // �ʽ�ƻ���Ŀ
	public String busitype; // ҵ������
	public String payman; // ֧����
	public UFDate paydate; // ֧������
	public String receiver; // �տ���
	public String reimrule; // ��������

	public String dwbm;

	public String zyx30;

	public UFDateTime shrq;

	public String zyx4;

	public String zyx20;

	public UFDouble hkbbje;

	public String zyx14;

	public UFBoolean ischeck;

	public UFBoolean isinitgroup; // ���õ����Ƿ��ż�

	public UFDouble bbhl;

	public Integer fjzs;

	public String zyx21;

	public String zyx3;

	public String zyx15;

	public String zy;

	public String zyx16;

	public String zyx5;

	public String skyhzh;

	public String zyx25;

	public String zyx18;

	public String zyx9;

	public String zyx13;

	public String zyx24;

	public String jsh;

	public String zyx17;

	public String zyx8;

	public String cashitem;

	public Integer sxbz;

	public String bzbm;

	public UFDouble hkybje;

	public String fydwbm;

	public String zyx6;

	public String zyx11;

	public String fydeptid;

	public UFDouble zpxe;

	public String jobid;

	public String projecttask;

	public String jsfs;

	public String approver;

	public String zyx26;

	public String szxmid;

	public String zyx12;

	public String pk_item;

	public String modifier;

	public String zyx29;

	public String djlxbm;

	public String fkyhzh;

	public UFDouble cjkybje;

	public UFDate jsrq;

	public String zyx23;

	public String operator;

	public String zyx7;

	public String jkbxr;

	public String zyx2;

	public UFDouble zfbbje;

	public String zyx27;

	public String zyx22;

	public String pk_jkbx;

	public String djdl;

	public String zyx10;

	public String pjh;

	public String checktype;

	public String zyx19;

	public String hbbm; // ��Ӧ��

	public String customer; // �ͻ�

	public UFDate djrq;

	public String deptid;

	public String zyx28;

	public String djbh;

	public Integer djzt;

	public String zyx1;

	public UFDouble cjkbbje;

	public UFDouble zfybje;

	public UFBoolean qcbz;

	public Integer spzt;

	public UFDateTime ts;

	public Integer dr;

	public UFDate zhrq;

	public UFDouble ybye;

	public UFDouble bbye;

	public UFDate contrastenddate;

	public Integer qzzt;

	public String kjnd;

	public String kjqj;

	public String jsr;

	public UFDate officialprintdate;

	public String officialprintuser;

	public String auditman;

	public UFDouble yjye; // ��Ԥ�����

	public UFDouble jsybye; // ����ԭ�����

	public String mngaccid;

	public UFDouble ybje;
	public UFDouble bbje;

	public UFDouble total;
	public Integer loantype;

	// v6����
	public String pk_checkele; // ����Ҫ��
	public String pk_pcorg; // ��������
	public String pk_group; // ����
	public String pk_org; // ҵ��Ԫ
	public String pk_fiorg; // ������֯
	public String pk_org_v; // ҵ��Ԫ�汾

	// begin-- added by chendya@ufida.com.cn ��֯�Ͳ��������汾����Ϣ
	
	/**
	 * �������İ汾��
	 */
	public static String PK_PCORG_V = "pk_pcorg_v";
	/**
	 * (���/����)���Ű汾��
	 */
	public static String DEPTID_V = "deptid_v";
	/**
	 * (���/����)���óе����Ű汾��
	 */
	public static String FYDEPTID_V = "fydeptid_v";
	/**
	 * (���/����)��λ�汾��
	 */
	public static String DWBM_V = "dwbm_v";
	/**
	 * (���/����)���óе���λ�汾��
	 */
	public static String FYDWBM_V = "fydwbm_v";

	/**
	 * �������İ汾��
	 */
	public String pk_pcorg_v;

	/**
	 * (���/����)���Ű汾��
	 */
	public String deptid_v;

	/**
	 * (���/����)���óе����Ű汾��
	 */
	public String fydeptid_v;

	/**
	 * (���/����)��λ�汾��
	 */
	public String dwbm_v;

	/**
	 * (���/����)���óе���λ�汾��
	 */
	public String fydwbm_v;
	// --end

	// v6����
	public UFDouble globalcjkbbje; // ȫ�ֳ���ҽ��
	public UFDouble globalhkbbje; // ȫ�ֻ���ҽ��
	public UFDouble globalzfbbje; // ȫ��֧�����ҽ��
	public UFDouble globalbbje; // ȫ�ֽ��/�������ҽ��
	public UFDouble globalbbye; // ȫ�ֱ�������
	public UFDouble groupbbye; // ���ű�������
	public UFDouble groupcjkbbje; // ���ų���ҽ��
	public UFDouble grouphkbbje; // ���Ż���ҽ��
	public UFDouble groupzfbbje; // ����֧�����ҽ��
	public UFDouble groupbbje; // ���Ž��/�������ҽ��
	public UFDouble globalbbhl; // ȫ�ֱ��һ���
	public UFDouble groupbbhl; // ���ű��һ���

	public String creator; // ������
	public UFDateTime creationtime; // ����ʱ��
	public UFDateTime modifiedtime; // �޸�ʱ��
	public String custaccount; // ���������˺�
	public String freecust; // ɢ��
	public String setorg;

	public UFBoolean iscostshare = UFBoolean.FALSE;// ��̯��־
	public UFBoolean isexpamt = UFBoolean.FALSE;// ̯����־
	public String start_period;// ��ʼ̯���ڼ�
	public java.lang.Integer total_period;// ��̯����

	public UFBoolean flexible_flag = UFBoolean.FALSE;// ��ĿԤ��-�Ƿ����Կ���
	
	public UFBoolean iscusupplier = UFBoolean.FALSE;//�Թ�֧��
	public static final String ISCUSUPPLIER = "iscusupplier";
	
	//v631����
	public String pk_proline;//��Ʒ��
	public String pk_brand;//Ʒ��
	
	// ehp2����
	public Integer paytarget; // �տ����
	public Integer vouchertag; // ƾ֤��־
	public UFDate  tbb_period ;//Ԥ��ռ���ڼ�
	
	// ehp3����
	public Integer red_status;// ����־
	public String redbillpk;// ��嵥������
	// v633���� CRM
	public String pk_matters;// Ӫ������
	public String pk_campaign;// Ӫ���

	//add 2014-07-07 V635
	public String imag_status;// Ӱ��״̬
	public UFBoolean isneedimag;// ��ҪӰ��ɨ��
	private java.lang.String pk_billtype;//��������
	public UFBoolean isexpedited;//����
	
	public static final String PK_PROLINE = "pk_proline";//��Ʒ��
	public static final String PK_BRAND = "pk_brand";//Ʒ��
	public static final String RED_STATUS = "red_status";//���״̬
	public static final String REDBILLPK = "redbillpk";//���pk
	public static final String PK_MATTERS = "pk_matters";// Ӫ������
	public static final String PK_CAMPAIGN = "pk_campaign";// Ӫ���
	
	public static final String PAYTARGET = "paytarget";
	public static final String TBB_PERIOD = "tbb_period";
	public static final String VOUCHERTAG = "vouchertag";
	
	public static final String IMAG_STATUS = "imag_status";
	public static final String ISNEEDIMAG = "isneedimag";
	public static String PK_BILLTYPE = "pk_billtype";//��������
	public static String ISEXPEDITED = "isexpedited";//����
	

	// v6����
	public static final String GLOBALCJKBBJE = "globalcjkbbje";
	public static final String GLOBALHKBBJE = "globalhkbbje";
	public static final String GLOBALZFBBJE = "globalzfbbje";
	public static final String GLOBALBBJE = "globalbbje";
	public static final String GLOBALBBYE = "globalbbye";
	public static final String GROUPBBYE = "groupbbye";
	public static final String GROUPCJKBBJE = "groupcjkbbje";
	public static final String GROUPHKBBJE = "grouphkbbje";
	public static final String GROUPZFBBJE = "groupzfbbje";
	public static final String GROUPBBJE = "groupbbje";
	public static final String GLOBALBBHL = "globalbbhl";
	public static final String GROUPBBHL = "groupbbhl";
	public static final String CUSTOMER = "customer";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String CUSTACCOUNT = "custaccount";
	public static final String FREECUST = "freecust";

	public static final String PK_CHECKELE = "pk_checkele";
	public static final String PK_PCORG = "pk_pcorg";
	public static final String PK_FIORG = "pk_fiorg";
	public static final String PK_ORG_V = "pk_org_v";

	public static final String PK_GROUP = "pk_group";

	public static final String RECEIVER = "receiver";

	public static final String AMOUNT = "amount";

	public static final String TOTAL = "total";

	public static final String OFFICIALPRINTDATE = "officialprintdate";

	public static final String OFFICIALPRINTUSER = "officialprintuser";

	public static final String KJND = "kjnd";

	public static final String KJQJ = "kjqj";

	public static final String QZZT = "qzzt";

	public static final String CONTRASTENDDATE = "contrastenddate";

	public static final String YBYE = "ybye";

	public static final String BBYE = "bbye";

	public static final String ZHRQ = "zhrq";

	public static final String SPZT = "spzt";

	public static final String TS = "ts";

	public static final String DR = "dr";

	public static final String QCBZ = "qcbz";

	public static final String DWBM = "dwbm";

	public static final String BUSITYPE = "busitype";

	public static final String ZYX30 = "zyx30";

	public static final String SHRQ = "shrq";

	public static final String ZYX4 = "zyx4";

	public static final String ZYX20 = "zyx20";

	public static final String HKBBJE = "hkbbje";

	public static final String ZYX14 = "zyx14";

	public static final String BBHL = "bbhl";

	public static final String FJZS = "fjzs";

	public static final String ZYX21 = "zyx21";

	public static final String ZYX3 = "zyx3";

	public static final String ZYX15 = "zyx15";

	public static final String ZY = "zy";

	public static final String ZYX16 = "zyx16";

	public static final String ZYX5 = "zyx5";

	public static final String SKYHZH = "skyhzh";

	public static final String ZYX25 = "zyx25";

	public static final String ZYX18 = "zyx18";

	public static final String ZYX9 = "zyx9";

	public static final String ZYX13 = "zyx13";

	public static final String YBJE = "ybje";

	public static final String ZYX24 = "zyx24";

	public static final String JSH = "jsh";

	public static final String ZYX17 = "zyx17";

	public static final String ZYX8 = "zyx8";

	public static final String CASHITEM = "cashitem";

	public static final String SXBZ = "sxbz";

	public static final String BZBM = "bzbm";

	public static final String HKYBJE = "hkybje";

	public static final String FYYBJE = "fyybje";

	public static final String FYDWBM = "fydwbm";

	public static final String PK_ORG = "pk_org";

	public static final String ZYX6 = "zyx6";

	public static final String ZYX11 = "zyx11";

	public static final String FYDEPTID = "fydeptid";

	public static final String ZPXE = "zpxe";

	public static final String JOBID = "jobid";

	public static final String PROJECTTASK = "projecttask";

	public static final String JSFS = "jsfs";

	public static final String APPROVER = "approver";

	public static final String ZYX26 = "zyx26";

	public static final String SZXMID = "szxmid";

	public static final String ZYX12 = "zyx12";

	public static final String PK_ITEM = "pk_item";
	
	/**
	 * �������뵥���
	 */
	public static final String PK_ITEM_BILLNO = "pk_item.billno";

	public static final String MODIFIER = "modifier";

	public static final String ZYX29 = "zyx29";

	public static final String DJLXBM = "djlxbm";
	public static final String DJLXMC = "djlxmc";

	public static final String FKYHZH = "fkyhzh";

	public static final String CJKYBJE = "cjkybje";

	public static final String JSRQ = "jsrq";

	public static final String ZYX23 = "zyx23";

	public static final String OPERATOR = "operator";

	public static final String ZYX7 = "zyx7";

	public static final String JKBXR = "jkbxr";

	public static final String ZYX2 = "zyx2";

	public static final String ZFBBJE = "zfbbje";

	public static final String BBJE = "bbje";

	public static final String ZYX27 = "zyx27";

	public static final String ZYX22 = "zyx22";

	public static final String PK_JKBX = "pk_jkbx";

	public static final String DJDL = "djdl";

	public static final String ZYX10 = "zyx10";

	public static final String PJH = "pjh";

	public static final String CHECKTYPE = "checktype";

	public static final String ZYX19 = "zyx19";

	public static final String HBBM = "hbbm";

	public static final String DJRQ = "djrq";

	public static final String DEPTID = "deptid";

	public static final String ZYX28 = "zyx28";

	public static final String DJBH = "djbh";

	public static final String DJZT = "djzt";

	public static final String ZYX1 = "zyx1";

	public static final String CJKBBJE = "cjkbbje";

	public static final String ZFYBJE = "zfybje";

	public static final String JSR = "jsr";

	public static final String ISCHECK = "ischeck";

	public static final String ISINITGROUP = "isinitgroup"; // ���õ����Ƿ��ż�

	public static final String CASHPROJ = "cashproj";

	public static final String JK = "jk";

	public static final String ZPJE = "zpje";

	public static final String PAYFLAG = "payflag";
	public static final String PAYDATE = "paydate";
	public static final String PAYMAN = "payman";

	public static final String MNGACCID_MC = "mngaccid_mc";
	public static final String YJYE = "yjye";
	public static final String MNGACCID = "mngaccid";
	public static final String SETORG = "setorg";

	public static final String ISCOSTSHARE = "iscostshare";
	public static final String ISEXPAMT = "isexpamt";
	public static final String START_PERIOD = "start_period";
	public static final String TOTAL_PERIOD = "total_period";
	public static final String FLEXIBLE_FLAG = "flexible_flag";
	public static final String CENTER_DEPT = "center_dept";
	
	/**
	 * ��Դ��������
	 */
	public static final String SRCBILLTYPE = "srcbilltype";
	/**
	 * ��Դ���ͣ�Ĭ��Ϊ�������뵥
	 */
	public static final String SRCTYPE = "srctype";
	/**
	 * �Ƿ����������뵥��̯���ӷ������뵥��������
	 */
	public static final String ISMASHARE = "ismashare";

	/**
	 * �����������
	 */
	public static final String AUDITMAN = "auditman";
	
	/**
	 * ��ڹ�����
	 */
	private java.lang.String center_dept;
	
	/**
	 * ��Դ��������
	 */
	private String srcbilltype;
	private String srctype;
	
	/**
	 * �Ƿ����뵥��̯����
	 */
	private UFBoolean ismashare;

	public UFDate getOfficialprintdate() {
		return officialprintdate;
	}

	public void setOfficialprintdate(UFDate officialprintdate) {
		this.officialprintdate = officialprintdate;
	}

	public String getOfficialprintuser() {
		return officialprintuser;
	}

	public void setOfficialprintuser(String officialprintuser) {
		this.officialprintuser = officialprintuser;
	}

	public String getJsr() {
		return jsr;
	}

	public void setJsr(String jsr) {
		this.jsr = jsr;
	}

	/**
	 * ����pk_corp��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getDwbm() {
		return dwbm;
	}

	/**
	 * ����pk_corp��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newPk_corp
	 *            String
	 */
	public void setDwbm(String dwbm) {

		this.dwbm = dwbm;
	}

	/**
	 * ����zyx30��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx30() {
		return zyx30;
	}

	/**
	 * ����zyx30��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx30
	 *            String
	 */
	public void setZyx30(String newZyx30) {

		zyx30 = newZyx30;
	}

	/**
	 * ����shrq��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return UFDateTime
	 */
	public UFDateTime getShrq() {
		return shrq;
	}

	/**
	 * ����shrq��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newShrq
	 *            UFDate
	 */
	public void setShrq(UFDateTime newShrq) {

		shrq = newShrq;
	}

	/**
	 * ����zyx4��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx4() {
		return zyx4;
	}

	/**
	 * ����zyx4��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx4
	 *            String
	 */
	public void setZyx4(String newZyx4) {

		zyx4 = newZyx4;
	}

	/**
	 * ����zyx20��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx20() {
		return zyx20;
	}

	/**
	 * ����zyx20��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx20
	 *            String
	 */
	public void setZyx20(String newZyx20) {

		zyx20 = newZyx20;
	}

	/**
	 * ����hkbbje��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getHkbbje() {
		if (hkbbje == null)
			return UFDouble.ZERO_DBL;
		return hkbbje;
	}

	/**
	 * ����hkbbje��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newHkbbje
	 *            UFDouble
	 */
	public void setHkbbje(UFDouble newHkbbje) {

		hkbbje = newHkbbje;
	}

	/**
	 * ����zyx14��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx14() {
		return zyx14;
	}

	/**
	 * ����zyx14��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx14
	 *            String
	 */
	public void setZyx14(String newZyx14) {

		zyx14 = newZyx14;
	}

	public UFBoolean getIscheck() {
		if (ischeck == null)
			return UFBoolean.FALSE;
		return ischeck;
	}

	public void setIscheck(UFBoolean newisCheck) {

		ischeck = newisCheck;
	}

	public UFBoolean getIsinitgroup() {
		if (isinitgroup == null)
			isinitgroup = UFBoolean.FALSE;
		return isinitgroup;
	}

	public void setIsinitgroup(UFBoolean isinitgroup) {
		this.isinitgroup = isinitgroup;
	}

	/**
	 * ����bbhl��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getBbhl() {
		return bbhl;
	}

	/**
	 * ����bbhl��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newBbhl
	 *            UFDouble
	 */
	public void setBbhl(UFDouble newBbhl) {

		bbhl = newBbhl;
	}

	/**
	 * ����fjzs��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return Integer
	 */
	public Integer getFjzs() {
		return fjzs;
	}

	/**
	 * ����fjzs��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newFjzs
	 *            Integer
	 */
	public void setFjzs(Integer newFjzs) {

		fjzs = newFjzs;
	}

	/**
	 * ����zyx21��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx21() {
		return zyx21;
	}

	/**
	 * ����zyx21��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx21
	 *            String
	 */
	public void setZyx21(String newZyx21) {

		zyx21 = newZyx21;
	}

	/**
	 * ����zyx3��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx3() {
		return zyx3;
	}

	/**
	 * ����zyx3��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx3
	 *            String
	 */
	public void setZyx3(String newZyx3) {

		zyx3 = newZyx3;
	}

	/**
	 * ����zyx15��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx15() {
		return zyx15;
	}

	/**
	 * ����zyx15��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx15
	 *            String
	 */
	public void setZyx15(String newZyx15) {

		zyx15 = newZyx15;
	}

	/**
	 * ����zy��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZy() {
		return zy;
	}

	/**
	 * ����zy��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZy
	 *            String
	 */
	public void setZy(String newZy) {

		zy = newZy;
	}

	/**
	 * ����zyx16��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx16() {
		return zyx16;
	}

	/**
	 * ����zyx16��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx16
	 *            String
	 */
	public void setZyx16(String newZyx16) {

		zyx16 = newZyx16;
	}

	/**
	 * ����zyx5��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx5() {
		return zyx5;
	}

	/**
	 * ����zyx5��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx5
	 *            String
	 */
	public void setZyx5(String newZyx5) {

		zyx5 = newZyx5;
	}

	/**
	 * ����skyhzh��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getSkyhzh() {
		return skyhzh;
	}

	/**
	 * ����skyhzh��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newSkyhzh
	 *            String
	 */
	public void setSkyhzh(String newSkyhzh) {

		skyhzh = newSkyhzh;
	}

	/**
	 * ����zyx25��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx25() {
		return zyx25;
	}

	/**
	 * ����zyx25��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx25
	 *            String
	 */
	public void setZyx25(String newZyx25) {

		zyx25 = newZyx25;
	}

	/**
	 * ����zyx18��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx18() {
		return zyx18;
	}

	/**
	 * ����zyx18��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx18
	 *            String
	 */
	public void setZyx18(String newZyx18) {

		zyx18 = newZyx18;
	}

	/**
	 * ����zyx9��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx9() {
		return zyx9;
	}

	/**
	 * ����zyx9��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx9
	 *            String
	 */
	public void setZyx9(String newZyx9) {

		zyx9 = newZyx9;
	}

	/**
	 * ����zyx13��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx13() {
		return zyx13;
	}

	/**
	 * ����zyx13��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx13
	 *            String
	 */
	public void setZyx13(String newZyx13) {

		zyx13 = newZyx13;
	}

	/**
	 * ����zyx24��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx24() {
		return zyx24;
	}

	/**
	 * ����zyx24��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx24
	 *            String
	 */
	public void setZyx24(String newZyx24) {

		zyx24 = newZyx24;
	}

	/**
	 * ����jsh��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getJsh() {
		return jsh;
	}

	/**
	 * ����jsh��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newJsh
	 *            String
	 */
	public void setJsh(String newJsh) {

		jsh = newJsh;
	}

	/**
	 * ����zyx17��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx17() {
		return zyx17;
	}

	/**
	 * ����zyx17��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx17
	 *            String
	 */
	public void setZyx17(String newZyx17) {

		zyx17 = newZyx17;
	}

	/**
	 * ����zyx8��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx8() {
		return zyx8;
	}

	/**
	 * ����zyx8��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx8
	 *            String
	 */
	public void setZyx8(String newZyx8) {

		zyx8 = newZyx8;
	}

	/**
	 * ����cashitem��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getCashitem() {
		return cashitem;
	}

	/**
	 * ����cashitem��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newCashitem
	 *            String
	 */
	public void setCashitem(String newCashitem) {

		cashitem = newCashitem;
	}

	/**
	 * ����sxbz��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return Integer
	 */
	public Integer getSxbz() {
		return sxbz;
	}

	/**
	 * ����sxbz��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newSxbz
	 *            Integer
	 */
	public void setSxbz(Integer newSxbz) {

		sxbz = newSxbz;
	}

	/**
	 * ����bzbm��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getBzbm() {
		return bzbm;
	}

	/**
	 * ����bzbm��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newBzbm
	 *            String
	 */
	public void setBzbm(String newBzbm) {

		bzbm = newBzbm;
	}

	/**
	 * ����hkybje��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getHkybje() {
		if (hkybje == null)
			return UFDouble.ZERO_DBL;
		return hkybje;
	}

	/**
	 * ����hkybje��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newHkybje
	 *            UFDouble
	 */
	public void setHkybje(UFDouble newHkybje) {

		hkybje = newHkybje;
	}

	/**
	 * ����fydwbm��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getFydwbm() {
		return fydwbm;
	}

	/**
	 * ����fydwbm��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newFydwbm
	 *            String
	 */
	public void setFydwbm(String newFydwbm) {

		fydwbm = newFydwbm;
	}

	/**
	 * ����zyx6��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx6() {
		return zyx6;
	}

	/**
	 * ����zyx6��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx6
	 *            String
	 */
	public void setZyx6(String newZyx6) {

		zyx6 = newZyx6;
	}

	/**
	 * ����zyx11��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx11() {
		return zyx11;
	}

	/**
	 * ����zyx11��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx11
	 *            String
	 */
	public void setZyx11(String newZyx11) {

		zyx11 = newZyx11;
	}

	/**
	 * ����fydeptid��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getFydeptid() {
		return fydeptid;
	}

	/**
	 * ����fydeptid��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newFydeptid
	 *            String
	 */
	public void setFydeptid(String newFydeptid) {

		fydeptid = newFydeptid;
	}

	/**
	 * ����zpxe��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getZpxe() {
		return zpxe;
	}

	/**
	 * ����zpxe��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZpxe
	 *            UFDouble
	 */
	public void setZpxe(UFDouble newZpxe) {

		zpxe = newZpxe;
	}

	/**
	 * ����jobid��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getJobid() {
		return jobid;
	}

	/**
	 * ����jobid��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newJobid
	 *            String
	 */
	public void setJobid(String newJobid) {

		jobid = newJobid;
	}

	public String getProjecttask() {
		return projecttask;
	}

	public void setProjecttask(String projecttask) {
		this.projecttask = projecttask;
	}

	/**
	 * ����jsfs��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getJsfs() {
		return jsfs;
	}

	/**
	 * ����jsfs��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newJsfs
	 *            String
	 */
	public void setJsfs(String newJsfs) {

		jsfs = newJsfs;
	}

	/**
	 * ����zyx26��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx26() {
		return zyx26;
	}

	/**
	 * ����zyx26��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx26
	 *            String
	 */
	public void setZyx26(String newZyx26) {

		zyx26 = newZyx26;
	}

	/**
	 * ����szxmid��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getSzxmid() {
		return szxmid;
	}

	/**
	 * ����szxmid��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newSzxmid
	 *            String
	 */
	public void setSzxmid(String newSzxmid) {

		szxmid = newSzxmid;
	}

	/**
	 * ����zyx12��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx12() {
		return zyx12;
	}

	/**
	 * ����zyx12��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx12
	 *            String
	 */
	public void setZyx12(String newZyx12) {

		zyx12 = newZyx12;
	}

	/**
	 * ����pk_item��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getPk_item() {
		return pk_item;
	}

	/**
	 * ����pk_item��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newPk_item
	 *            String
	 */
	public void setPk_item(String newPk_item) {

		pk_item = newPk_item;
	}

	/**
	 * ����modifier��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getModifier() {
		return modifier;
	}

	/**
	 * ����modifier��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newModifier
	 *            String
	 */
	public void setModifier(String newModifier) {

		modifier = newModifier;
	}

	/**
	 * ����zyx29��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx29() {
		return zyx29;
	}

	/**
	 * ����zyx29��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx29
	 *            String
	 */
	public void setZyx29(String newZyx29) {

		zyx29 = newZyx29;
	}

	/**
	 * ����djlxbm��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getDjlxbm() {
		return djlxbm;
	}

	/**
	 * ����djlxbm��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newDjlxbm
	 *            String
	 */
	public void setDjlxbm(String newDjlxbm) {

		djlxbm = newDjlxbm;
	}

	/**
	 * ����fkyhzh��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getFkyhzh() {
		return fkyhzh;
	}

	/**
	 * ����fkyhzh��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newFkyhzh
	 *            String
	 */
	public void setFkyhzh(String newFkyhzh) {

		fkyhzh = newFkyhzh;
	}

	/**
	 * ����cjkybje��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getCjkybje() {
		if (cjkybje == null)
			return UFDouble.ZERO_DBL;
		return cjkybje;
	}

	/**
	 * ����cjkybje��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newCjkybje
	 *            UFDouble
	 */
	public void setCjkybje(UFDouble newCjkybje) {

		cjkybje = newCjkybje;
	}

	/**
	 * ����jsrq��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return UFDate
	 */
	public UFDate getJsrq() {
		return jsrq;
	}

	/**
	 * ����jsrq��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param jsrq
	 *            UFDate
	 */
	public void setJsrq(UFDate jsrq) {

		this.jsrq = jsrq;
	}

	/**
	 * ����zyx23��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx23() {
		return zyx23;
	}

	/**
	 * ����zyx23��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx23
	 *            String
	 */
	public void setZyx23(String newZyx23) {

		zyx23 = newZyx23;
	}

	/**
	 * ¼���� ����operator��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * ����operator��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newOperator
	 *            String
	 */
	public void setOperator(String newOperator) {

		operator = newOperator;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * ����zyx7��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx7() {
		return zyx7;
	}

	/**
	 * ����zyx7��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx7
	 *            String
	 */
	public void setZyx7(String newZyx7) {

		zyx7 = newZyx7;
	}

	/**
	 * ����jkbxr��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getJkbxr() {
		return jkbxr;
	}

	/**
	 * ����jkbxr��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newJkbxr
	 *            String
	 */
	public void setJkbxr(String newJkbxr) {

		jkbxr = newJkbxr;
	}

	/**
	 * ����zyx2��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx2() {
		return zyx2;
	}

	/**
	 * ����zyx2��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx2
	 *            String
	 */
	public void setZyx2(String newZyx2) {

		zyx2 = newZyx2;
	}

	/**
	 * ����zfbbje��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getZfbbje() {
		if (zfbbje == null)
			return UFDouble.ZERO_DBL;
		return zfbbje;
	}

	/**
	 * ����zfbbje��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZfbbje
	 *            UFDouble
	 */
	public void setZfbbje(UFDouble newZfbbje) {

		zfbbje = newZfbbje;
	}

	/**
	 * ����zyx27��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx27() {
		return zyx27;
	}

	/**
	 * ����zyx27��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx27
	 *            String
	 */
	public void setZyx27(String newZyx27) {

		zyx27 = newZyx27;
	}

	/**
	 * ����zyx22��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx22() {
		return zyx22;
	}

	/**
	 * ����zyx22��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx22
	 *            String
	 */
	public void setZyx22(String newZyx22) {

		zyx22 = newZyx22;
	}

	/**
	 * ����pk_jkbx��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getPk_jkbx() {
		return pk_jkbx;
	}

	/**
	 * ����pk_jkbx��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newPk_jkbx
	 *            String
	 */
	public void setPk_jkbx(String newPk_jkbx) {

		pk_jkbx = newPk_jkbx;
	}

	/**
	 * ����djdl��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getDjdl() {
		return djdl;
	}

	/**
	 * ����djdl��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newDjdl
	 *            String
	 */
	public void setDjdl(String newDjdl) {
		djdl = newDjdl;
	}

	/**
	 * ����zyx10��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx10() {
		return zyx10;
	}

	/**
	 * ����zyx10��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx10
	 *            String
	 */
	public void setZyx10(String newZyx10) {

		zyx10 = newZyx10;
	}

	/**
	 * ����pjh��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getPjh() {
		return pjh;
	}

	/**
	 * ����pjh��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newPjh
	 *            String
	 */
	public void setPjh(String newPjh) {

		pjh = newPjh;
	}

	/**
	 * ����checktype��Getter����.
	 * 
	 * ��������:2011-05-24
	 * 
	 * @return String
	 */
	public String getChecktype() {
		return checktype;
	}

	/**
	 * ����checktype��Setter����.
	 * 
	 * ��������:2011-05-24
	 * 
	 * @param newChecktype
	 *            String
	 */
	public void setChecktype(String checktype) {
		this.checktype = checktype;
	}

	/**
	 * ����zyx19��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx19() {
		return zyx19;
	}

	/**
	 * ����zyx19��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx19
	 *            String
	 */
	public void setZyx19(String newZyx19) {

		zyx19 = newZyx19;
	}

	/**
	 * ����hbbm��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getHbbm() {
		return hbbm;
	}

	/**
	 * ����hbbm��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newHbbm
	 *            String
	 */
	public void setHbbm(String newHbbm) {

		hbbm = newHbbm;
	}

	/**
	 * ����djrq��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return UFDate
	 */
	public UFDate getDjrq() {
		return djrq;
	}

	/**
	 * ����djrq��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newDjrq
	 *            UFDate
	 */
	public void setDjrq(UFDate newDjrq) {

		djrq = newDjrq;
	}

	/**
	 * ����deptid��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getDeptid() {
		return deptid;
	}

	/**
	 * ����deptid��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newDeptid
	 *            String
	 */
	public void setDeptid(String newDeptid) {

		deptid = newDeptid;
	}

	/**
	 * ����zyx28��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx28() {
		return zyx28;
	}

	/**
	 * ����zyx28��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx28
	 *            String
	 */
	public void setZyx28(String newZyx28) {

		zyx28 = newZyx28;
	}

	/**
	 * ����djbh��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getDjbh() {
		return djbh;
	}

	/**
	 * ����djbh��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newDjbh
	 *            String
	 */
	public void setDjbh(String newDjbh) {

		djbh = newDjbh;
	}

	/**
	 * ����djzt��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return Integer
	 */
	public Integer getDjzt() {
		return djzt;
	}

	/**
	 * ����djzt��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newDjzt
	 *            Integer
	 */
	public void setDjzt(Integer newDjzt) {

		djzt = newDjzt;
	}

	/**
	 * ����zyx1��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx1() {
		return zyx1;
	}

	/**
	 * ����zyx1��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZyx1
	 *            String
	 */
	public void setZyx1(String newZyx1) {

		zyx1 = newZyx1;
	}

	/**
	 * ����cjkbbje��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getCjkbbje() {
		if (cjkbbje == null)
			return UFDouble.ZERO_DBL;
		return cjkbbje;
	}

	/**
	 * ����cjkbbje��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newCjkbbje
	 *            UFDouble
	 */
	public void setCjkbbje(UFDouble newCjkbbje) {

		cjkbbje = newCjkbbje;
	}

	/**
	 * ����zfybje��Getter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getZfybje() {
		if (zfybje == null)
			return UFDouble.ZERO_DBL;
		return zfybje;
	}

	/**
	 * ����zfybje��Setter����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newZfybje
	 *            UFDouble
	 */
	public void setZfybje(UFDouble newZfybje) {

		zfybje = newZfybje;
	}

	/**
	 * <p>
	 * ȡ�ø�VO�����ֶ�.
	 * <p>
	 * ��������:2007-6-13
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getParentPKFieldName() {

		return null;

	}

	/**
	 * ����Ĭ�Ϸ�ʽ����������.
	 * 
	 * ��������:2007-6-13
	 */
	public JKBXHeaderVO() {

		super();
	}

	/**
	 * ʹ���������г�ʼ���Ĺ�����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newPk_jkbx
	 *            ����ֵ
	 */
	public JKBXHeaderVO(String newPk_jkbx) {

		// Ϊ�����ֶθ�ֵ:
		pk_jkbx = newPk_jkbx;

	}

	/**
	 * ���ض����ʶ,����Ψһ��λ����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @return String
	 */
	@Override
	public String getPrimaryKey() {

		return pk_jkbx;

	}

	/**
	 * ���ö����ʶ,����Ψһ��λ����.
	 * 
	 * ��������:2007-6-13
	 * 
	 * @param newPk_jkbx
	 *            String
	 */
	@Override
	public void setPrimaryKey(String newPk_jkbx) {

		pk_jkbx = newPk_jkbx;

	}

	public UFBoolean getQcbz() {
		if (qcbz == null)
			return UFBoolean.FALSE;
		return qcbz;
	}

	public void setQcbz(UFBoolean qcbz) {
		this.qcbz = qcbz;
	}

	public Integer getSpzt() {
		if(spzt == null){
			spzt = IPfRetCheckInfo.NOSTATE;
		}
		return spzt;
	}

	public void setSpzt(Integer spzt) {
		this.spzt = spzt;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public UFDate getZhrq() {
		return zhrq;
	}

	public void setZhrq(UFDate zhrq) {
		this.zhrq = zhrq;
	}

	public UFDouble getBbye() {
		return bbye;
	}

	public void setBbye(UFDouble bbye) {
		this.bbye = bbye;
	}

	public UFDouble getYbye() {
		return ybye;
	}

	public void setYbye(UFDouble ybye) {
		this.ybye = ybye;
	}

	/**
	 * VO�����ֶ�, ������չʾ��, ���̻������ݿ�. VO�����ֶ�, ������չʾ��, ���̻������ݿ�.
	 */
	public static final String SELECTED = "selected";
	protected UFBoolean selected = UFBoolean.FALSE;

	public UFBoolean getSelected() {
		return selected;
	}

	public void setSelected(UFBoolean selected) {
		this.selected = selected;
	}

	public static final String VOUCHER = "voucher";
	protected String voucher;

	public String getVoucher() {
		return voucher;
	}

	public void setVoucher(String voucher) {
		this.voucher = voucher;
	}

	/**
	 * ҵ������, ���߼�����ʹ��, ���̻������ݿ�. ҵ������, ���߼�����ʹ��, ���̻������ݿ�.
	 */

	protected boolean isInit; // �Ƿ��õ���

	public boolean isInit() {
		return isInit;
	}

	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}

	/**
	 * ��ʾ����, ������չʾ��, ��ʹ����ҵ���߼�. ��ʾ����, ������չʾ��, ��ʹ����ҵ���߼�.
	 */
	protected String jkr, bxr;

	public void setBxr(String bxr) {
		this.bxr = bxr;
	}

	public void setJkr(String jkr) {
		this.jkr = jkr;
	}

	public String getJkr() {
		if (jkr != null)
			return jkr;
		if (djdl != null && djdl.equals(BXConstans.JK_DJDL))
			return getJkbxr();
		else
			return null;
	}

	public String getBxr() {
		if (bxr != null)
			return bxr;
		if (djdl != null && djdl.equals(BXConstans.BX_DJDL))
			return getJkbxr();
		else
			return null;
	}

	/**
	 * �߼�����, ���߼�������, �����������޸�. �߼�����, ���߼�������, �����������޸�.
	 */
	public boolean isRepayBill() { // �Ƿ񻹿��
		return getHkybje() != null && getHkybje().doubleValue() > 0;
	}

	public boolean isXeBill() { // �Ƿ��޶�֧Ʊ�͵���
		return getIscheck().booleanValue();
	}

	protected String hyflag;

	protected boolean isFySaveControl = false; // ���ÿ������ʱ����

	public boolean isFySaveControl() {
		return isFySaveControl;
	}

	public void setFySaveControl(boolean isFySaveControl) {
		this.isFySaveControl = isFySaveControl;
	}

	protected boolean isunAudit;

	public boolean isIsunAudit() {
		return isunAudit;
	}

	public void setIsunAudit(boolean isunAudit) {
		this.isunAudit = isunAudit;
	}

	/**
	 * @return �Ƿ���Ҫ��Ҫ������Ӱ��ӿ�
	 */
	public boolean isNoOtherEffectItf() {

		boolean status = false;

		if (isInit())
			status = true;
		// if (getDjzt().equals(BXStatusConst.DJZT_TempSaved))
		// status=true;

		return status;
	}

	public UFDate getContrastenddate() {
		return contrastenddate;
	}

	public void setContrastenddate(UFDate contrastEndDate) {
		this.contrastenddate = contrastEndDate;
	}

	public Integer getQzzt() {
		return qzzt;
	}

	public void setQzzt(Integer qzzt) {
		this.qzzt = qzzt;
	}

	public String getKjnd() {
		return kjnd;
	}

	public void setKjnd(String kjnd) {
		this.kjnd = kjnd;
	}

	public String getKjqj() {
		return kjqj;
	}

	public void setKjqj(String kjqj) {
		this.kjqj = kjqj;
	}

	// /////////////////�����������ƽӿ���Ҫʵ�ֵķ���/////////////////

	public boolean isSaveControl() {
		return isFySaveControl();
	}

	public UFDate getOperationDate() {
		if (getDjzt() == null)
			return null;
		if (getDjzt().intValue() == BXStatusConst.DJZT_Saved) {
			return getDjrq();
			// FIXME �������ע��
			// }else if(getDjzt().intValue()==BXStatusConst.DJZT_Verified){
			// return getShrq();
		} else if (getDjzt().intValue() == BXStatusConst.DJZT_Sign) {
			return getJsrq();
		}
		return null;
	}

	public String getOperationUser() {
		if (getDjzt() == null)
			return null;
		if (getDjzt().intValue() == BXStatusConst.DJZT_Saved) {
			return getOperator();
		} else if (getDjzt().intValue() == BXStatusConst.DJZT_Verified) {
			return getApprover();
		} else if (getDjzt().intValue() == BXStatusConst.DJZT_Sign) {
			return getJsr();
		}
		return null;
	}

	public boolean isSSControlAble() {

		boolean status = true;

		if (getQcbz() != null && getQcbz().booleanValue())
			status = false;
		if (StringUtils.isNullWithTrim(getPk_item()))
			status = false;
		if (getDjzt().equals(BXStatusConst.DJZT_TempSaved))
			status = false;

		return status;
	}

	public boolean isYSControlAble() {

		boolean status = true;

		if (getQcbz() != null && getQcbz().booleanValue())
			status = false;
		if (getDjzt().equals(BXStatusConst.DJZT_TempSaved))
			status = false;

		return status;
	}

	public boolean isJKControlAble() {

		boolean status = true;

		if (getDjdl() == null || !getDjdl().equals(BXConstans.JK_DJDL))
			status = false;
		if (getQcbz() != null && getQcbz().booleanValue())
			status = false;
		if (getDjzt().equals(BXStatusConst.DJZT_TempSaved))
			status = false;

		return status;
	}

	public UFDouble[] getItemHl() {
		return new UFDouble[] { getGlobalbbhl(), getGroupbbhl(), getBbhl() };
	}

	public UFDouble[] getItemJe() {
		return new UFDouble[] { getGlobalbbje(), getGroupbbje(), getBbje(), getYbje() };
	}
	
	private UFDouble[] preItemJe;

	@Override
	public UFDouble[] getPreItemJe() {
		return preItemJe;
	}

	public void setPreItemJe(UFDouble[] preItemJe) {
		this.preItemJe = preItemJe;
	}

	public Object getItemValue(String key) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("zrdeptid", "fydeptid");
		map.put("xmbm2", "jobid");
		map.put("ywybm", "jkbxr");
		return getAttributeValue(map.get(key) == null ? key : map.get(key));
	}

	public String getDdlx() {
		return null;
	}

	public Integer getFx() {
		return isRepayBill() ? Integer.valueOf(-1) : Integer.valueOf(1);
	}

	// /////////////////�����������ƽӿ���Ҫʵ�ֵķ���/////////////////

	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}

	/**
	 * 
	 * ���������������
	 */
	public String getAuditman() throws BusinessException {
		String result = "";
		result = ErCorpUtil.getBxCtlMan(this);
		return result;
	}

	/* ��д�˷�����Ŀ����Ϊ��clone��ʱ�򣬲���¡auditman����ΪgetAuditman�ж��Զ�̵��� */
	@Override
	public String[] getAttributeNames() {
		List<String> retValues = new ArrayList<String>();
		final String[] names = super.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (AUDITMAN.equals(names[i])) {
				continue;
			}
			retValues.add(names[i]);
		}
		return retValues.toArray(new String[0]);
	}

	public void setAuditman(String man) {

	}

	public UFDouble getYjye() {
		return yjye;
	}

	public void setYjye(UFDouble yjye) {
		this.yjye = yjye;
	}

	public UFDouble getJsybye() {
		return jsybye;
	}

	public void setJsybye(UFDouble jsybye) {
		this.jsybye = jsybye;
	}

	public UFDouble getBbje() {
		return bbje;
	}

	public void setBbje(UFDouble bbje) {
		this.bbje = bbje;
	}

	public UFDouble getYbje() {
		return ybje;
	}

	public void setYbje(UFDouble ybje) {
		this.ybje = ybje;
	}

	public String getCashproj() {
		return cashproj;
	}

	public void setCashproj(String cashproj) {
		this.cashproj = cashproj;
	}

	public Integer getPayflag() {
		return payflag;
	}

	public void setPayflag(Integer payflag) {
		this.payflag = payflag;
	}

	public UFDouble getTotal() {
		return total;
	}

	public void setTotal(UFDouble total) {
		this.total = total;
	}

	public UFDate getPaydate() {
		return paydate;
	}

	public void setPaydate(UFDate paydate) {
		this.paydate = paydate;
	}

	public String getPayman() {
		return payman;
	}

	public void setPayman(String payman) {
		this.payman = payman;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Integer getLoantype() {
		return loantype;
	}

	public void setLoantype(Integer loantype) {
		this.loantype = loantype;
	}

	@Override
	public Object getAttributeValue(String key) {
		// �������������Ϣ�ֶΣ��ӽ���vo��ȡֵ
		if (key.startsWith(SETTLE_HEAD_PREFIX)) {
			if (getSettleHeadVO() != null) {
				String attribute = key.substring(key.indexOf(SETTLE_HEAD_PREFIX) + SETTLE_HEAD_PREFIX.length());
				return getSettleHeadVO().getAttributeValue(attribute);
			}
		}
		
		String name = null;
		Object result = null;
		String[] tokens = StringUtil.split(key, ".");
		if (tokens.length == 1) {
			name = key;
		}else{
			name = tokens[1];
		}
		if(BeanHelper.getMethod(this, name) != null){
			result = BeanHelper.getProperty(this, name);//��������������ֶβ�һ������
		}else{
			result = super.getAttributeValue(name);
		}

		return result;
	}
	
	@Override
	public void setAttributeValue(String name, Object value) {
		if (BeanHelper.getMethod(this, name) != null) {
			BeanHelper.setProperty(this, name, value);
		} else {
			super.setAttributeValue(name, value);
		}
	}

	//shiwla �ֻ��˽���voת����
	public void setJsonAttributeValue(String name, Object value) {
		super.setAttributeValue(name, value);
	}
	public String getPk_org_v() {
		return pk_org_v;
	}

	public void setPk_org_v(String pkOrgV) {
		pk_org_v = pkOrgV;
	}

	public String getHyflag() {
		return hyflag;
	}

	public void setHyflag(String hyflag) {
		this.hyflag = hyflag;
	}

	public String getMngaccid() {
		return mngaccid;
	}

	public void setMngaccid(String mngaccid) {
		this.mngaccid = mngaccid;
	}

	public String getReimrule() {
		return reimrule;
	}

	public void setReimrule(String reimrule) {
		this.reimrule = reimrule;
	}

	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getPk_checkele() {
		return pk_checkele;
	}

	public void setPk_checkele(String pk_checkele) {
		this.pk_checkele = pk_checkele;
	}

	public String getPk_pcorg() {
		return pk_pcorg;
	}

	public void setPk_pcorg(String pk_pcorg) {
		this.pk_pcorg = pk_pcorg;
	}

	public String getPk_fiorg() {
		return pk_fiorg;
	}

	public void setPk_fiorg(String pk_fiorg) {
		this.pk_fiorg = pk_fiorg;
	}

	/**
	 * ����globalcjkbbje��Getter����. ��������:2010-01-18 09:32:58
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getGlobalcjkbbje() {
		return globalcjkbbje;
	}

	/**
	 * ����globalcjkbbje��Setter����. ��������:2010-01-18 09:32:58
	 * 
	 * @param newGlobalcjkbbje
	 *            UFDouble
	 */
	public void setGlobalcjkbbje(UFDouble newGlobalcjkbbje) {
		this.globalcjkbbje = newGlobalcjkbbje;
	}

	/**
	 * ����globalhkbbje��Getter����. ��������:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalhkbbje() {
		return globalhkbbje;
	}

	/**
	 * ����globalhkbbje��Setter����. ��������:2010-01-18 09:32:58
	 * 
	 * @param newGlobalhkbbje
	 *            UFDouble
	 */
	public void setGlobalhkbbje(UFDouble newGlobalhkbbje) {
		this.globalhkbbje = newGlobalhkbbje;
	}

	/**
	 * ����globalzfbbje��Getter����. ��������:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalzfbbje() {
		return globalzfbbje;
	}

	/**
	 * ����globalzfbbje��Setter����. ��������:2010-01-18 09:32:58
	 * 
	 * @param newGlobalzfbbje
	 *            UFDouble
	 */
	public void setGlobalzfbbje(UFDouble newGlobalzfbbje) {
		this.globalzfbbje = newGlobalzfbbje;
	}

	/**
	 * ����globalbbje��Getter����. ��������:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalbbje() {
		return globalbbje;
	}

	/**
	 * ����globalbbje��Setter����. ��������:2010-01-18 09:32:58
	 * 
	 * @param newGlobalbbje
	 *            UFDouble
	 */
	public void setGlobalbbje(UFDouble newGlobalbbje) {
		this.globalbbje = newGlobalbbje;
	}

	/**
	 * ����globalbbye��Getter����. ��������:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalbbye() {
		return globalbbye;
	}

	/**
	 * ����globalbbye��Setter����. ��������:2010-01-18 09:32:58
	 * 
	 * @param newGlobalbbye
	 *            UFDouble
	 */
	public void setGlobalbbye(UFDouble newGlobalbbye) {
		this.globalbbye = newGlobalbbye;
	}

	/**
	 * ����groupbbye��Getter����. ��������:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupbbye() {
		return groupbbye;
	}

	/**
	 * ����groupbbye��Setter����. ��������:2010-01-18 09:32:58
	 * 
	 * @param newGroupbbye
	 *            UFDouble
	 */
	public void setgroupbbye(UFDouble newGroupbbye) {
		this.groupbbye = newGroupbbye;
	}

	/**
	 * ����groupcjkbbje��Getter����. ��������:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupcjkbbje() {
		return groupcjkbbje;
	}

	/**
	 * ����groupcjkbbje��Setter����. ��������:2010-01-18 09:32:58
	 * 
	 * @param newGroupcjkbbje
	 *            UFDouble
	 */
	public void setGroupcjkbbje(UFDouble newGroupcjkbbje) {
		this.groupcjkbbje = newGroupcjkbbje;
	}

	/**
	 * ����grouphkbbje��Getter����. ��������:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGrouphkbbje() {
		return grouphkbbje;
	}

	/**
	 * ����grouphkbbje��Setter����. ��������:2010-01-18 09:32:58
	 * 
	 * @param newGrouphkbbje
	 *            UFDouble
	 */
	public void setGrouphkbbje(UFDouble newGrouphkbbje) {
		this.grouphkbbje = newGrouphkbbje;
	}

	/**
	 * ����groupzfbbje��Getter����. ��������:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupzfbbje() {
		return groupzfbbje;
	}

	/**
	 * ����groupzfbbje��Setter����. ��������:2010-01-18 09:32:58
	 * 
	 * @param newGroupzfbbje
	 *            UFDouble
	 */
	public void setGroupzfbbje(UFDouble newGroupzfbbje) {
		this.groupzfbbje = newGroupzfbbje;
	}

	/**
	 * ����groupbbje��Getter����. ��������:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupbbje() {
		return groupbbje;
	}

	/**
	 * ����groupbbje��Setter����. ��������:2010-01-18 09:32:58
	 * 
	 * @param newGroupbbje
	 *            UFDouble
	 */
	public void setGroupbbje(UFDouble newGroupbbje) {
		this.groupbbje = newGroupbbje;
	}

	/**
	 * ����globalbbhl��Getter����. ��������:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalbbhl() {
		return globalbbhl;
	}

	/**
	 * ����globalbbhl��Setter����. ��������:2010-01-18 09:32:58
	 * 
	 * @param newGlobalbbhl
	 *            UFDouble
	 */
	public void setGlobalbbhl(UFDouble newGlobalbbhl) {
		this.globalbbhl = newGlobalbbhl;
	}

	/**
	 * ����groupbbhl��Getter����. ��������:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupbbhl() {
		return groupbbhl;
	}

	/**
	 * ����groupbbhl��Setter����. ��������:2010-01-18 09:32:58
	 * 
	 * @param newGroupbbhl
	 *            UFDouble
	 */
	public void setGroupbbhl(UFDouble newGroupbbhl) {
		this.groupbbhl = newGroupbbhl;
	}

	/**
	 * ����customer��Getter����. ��������:2010-01-18 09:32:58
	 * 
	 * @return java.lang.String
	 */
	public String getCustomer() {
		return customer;
	}

	/**
	 * ����customer��Setter����. ��������:2010-01-18 09:32:58
	 * 
	 * @param newCustomer
	 *            java.lang.String
	 */
	public void setCustomer(String newCustomer) {
		this.customer = newCustomer;
	}

	public UFDateTime getCreationtime() {
		return creationtime;
	}

	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	public UFDateTime getModifiedtime() {
		return modifiedtime;
	}

	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	public String getCustaccount() {
		return custaccount;
	}

	public void setCustaccount(String custaccount) {
		this.custaccount = custaccount;
	}

	public String getFreecust() {
		return freecust;
	}

	public void setFreecust(String freecust) {
		this.freecust = freecust;
	}

	/**
	 * ����setorg��Setter����. ��������:2010-01-18 09:32:58 @ ʵ���б���漯�ţ�ҵ��Ԫ��ʾ
	 */
	public String getSetorg() {
		return setorg;
	}

	public void setSetorg(String setorg) {
		this.setorg = setorg;
	}

	/**
	 * ������汾�ֶζ��ձ�
	 * 
	 * @return
	 */
	public static Map<String, String> getOrgMultiVersionFieldMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(JKBXHeaderVO.PK_ORG, JKBXHeaderVO.PK_ORG_V);
		map.put(JKBXHeaderVO.FYDWBM, JKBXHeaderVO.FYDWBM_V);
		map.put(JKBXHeaderVO.DWBM, JKBXHeaderVO.DWBM_V);
		map.put(JKBXHeaderVO.PK_PCORG, JKBXHeaderVO.PK_PCORG_V);
		map.put(JKBXHeaderVO.FYDEPTID, JKBXHeaderVO.FYDEPTID_V);
		map.put(JKBXHeaderVO.DEPTID, JKBXHeaderVO.DEPTID_V);
		map.put(JKBXHeaderVO.PK_PAYORG, JKBXHeaderVO.PK_PAYORG_V);// ֧����λ
		return map;
	}

	public static String getDeptFieldByVField(String vField) {
		Map<String, String> map = getDeptMultiVersionFieldMap();
		Set<Entry<String, String>> entrySet = map.entrySet();
		for (Iterator<Entry<String, String>> iterator = entrySet.iterator(); iterator.hasNext();) {
			Entry<String, String> entry = (Entry<String, String>) iterator.next();
			if (vField.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static String getDeptVFieldByField(String field) {
		return getDeptMultiVersionFieldMap().get(field);
	}

	public static String getOrgFieldByVField(String vField) {
		Map<String, String> map = getOrgMultiVersionFieldMap();
		Set<Entry<String, String>> entrySet = map.entrySet();
		for (Iterator<Entry<String, String>> iterator = entrySet.iterator(); iterator.hasNext();) {
			Entry<String, String> entry = (Entry<String, String>) iterator.next();
			if (vField.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static String getOrgVFieldByField(String field) {
		return getOrgMultiVersionFieldMap().get(field);
	}

	/**
	 * ������汾�ֶ�����
	 * 
	 * @return
	 */
	public static String[] getOrgMultiVersionFieldArray() {
		return (String[]) getOrgMultiVersionFieldMap().values().toArray(new String[0]);
	}

	/**
	 * ������汾�ֶ��б�
	 * 
	 * @return
	 */
	public static List<String> getOrgMultiVersionFieldList() {
		return Arrays.asList(getOrgMultiVersionFieldArray());
	}

	/**
	 * ������汾�ֶζ��ձ�
	 * 
	 * @return
	 */
	public static Map<String, String> getDeptMultiVersionFieldMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(JKBXHeaderVO.DEPTID, JKBXHeaderVO.DEPTID_V);
		map.put(JKBXHeaderVO.FYDEPTID, JKBXHeaderVO.FYDEPTID_V);
		return map;
	}

	/**
	 * ������汾�ֶ�����
	 * 
	 * @return
	 */
	public static String[] getDeptMultiVersionFieldArray() {
		return (String[]) getDeptMultiVersionFieldMap().values().toArray(new String[0]);
	}

	/**
	 * ������汾�ֶ��б�
	 * 
	 * @return
	 */
	public static List<String> getDeptMultiVersionFieldList() {
		return Arrays.asList(getDeptMultiVersionFieldArray());
	}

	@Override
	public String getParentBillType() {
		if (BXConstans.BX_DJDL.equals(this.getDjdl())) {
			return BXConstans.BX_DJLXBM;
		} else if (BXConstans.JK_DJDL.equals(this.getDjdl())) {
			return BXConstans.JK_DJLXBM;
		}
		return null;
	}

	public UFBoolean getIscostshare() {
		if (iscostshare == null) {
			return UFBoolean.FALSE;
		}
		return iscostshare;
	}

	public void setIscostshare(UFBoolean iscostshare) {
		this.iscostshare = iscostshare;
	}

	public UFBoolean getIsexpamt() {
		if (isexpamt == null) {
			return UFBoolean.FALSE;
		}
		return isexpamt;
	}

	public void setIsexpamt(UFBoolean isexpamt) {
		this.isexpamt = isexpamt;
	}
	
	public UFBoolean getIscusupplier() {
		if (iscusupplier == null){
			return UFBoolean.FALSE;
		}
		return iscusupplier;
	}

	public void setIscusupplier(UFBoolean iscusupplier) {
		this.iscusupplier = iscusupplier;
	}

	public String getStart_period() {
		return start_period;
	}

	public void setStart_period(String startPeriod) {
		start_period = startPeriod;
	}

	public java.lang.Integer getTotal_period() {
		return total_period;
	}

	public void setTotal_period(java.lang.Integer totalPeriod) {
		total_period = totalPeriod;
	}

	public String getPk_payorg() {
		return pk_payorg;
	}

	public void setPk_payorg(String pk_payorg) {
		this.pk_payorg = pk_payorg;
	}

	public String getPk_payorg_v() {
		return pk_payorg_v;
	}

	public void setPk_payorg_v(String pk_payorg_v) {
		this.pk_payorg_v = pk_payorg_v;
	}

	public UFBoolean getFlexible_flag() {
		return flexible_flag;
	}

	public void setFlexible_flag(UFBoolean flexible_flag) {
		this.flexible_flag = flexible_flag;
	}
	
	public void setPk_cashaccount(String pkCashaccount) {
		pk_cashaccount = pkCashaccount;
	}

	public String getPk_cashaccount() {
		return pk_cashaccount;
	}

	public void setPk_resacostcenter(String pkResacostcenter) {
		pk_resacostcenter = pkResacostcenter;
	}

	public String getPk_resacostcenter() {
		return pk_resacostcenter;
	}

	public void setSettleHeadVO(SuperVO settleHeadVO) {
		this.settleHeadVO = settleHeadVO;
	}

	public SuperVO getSettleHeadVO() {
		return settleHeadVO;
	}

	/**
	 * �˷���������
	 * 
	 * @param shrqShow
	 */
	public void setShrq_show(UFDate shrqShow) {
		shrq_show = shrqShow;
	}

	public UFDate getShrq_show() {
		if(getShrq() != null){
			shrq_show = getShrq().getDate();
		}
		return shrq_show;
	}

	public String getPk_pcorg_v() {
		return pk_pcorg_v;
	}

	public void setPk_pcorg_v(String pkPcorgV) {
		pk_pcorg_v = pkPcorgV;
	}

	public String getDeptid_v() {
		return deptid_v;
	}

	public void setDeptid_v(String deptidV) {
		deptid_v = deptidV;
	}

	public String getFydeptid_v() {
		return fydeptid_v;
	}

	public void setFydeptid_v(String fydeptidV) {
		fydeptid_v = fydeptidV;
	}

	public String getDwbm_v() {
		return dwbm_v;
	}

	public void setDwbm_v(String dwbmV) {
		dwbm_v = dwbmV;
	}

	public String getFydwbm_v() {
		return fydwbm_v;
	}

	public void setFydwbm_v(String fydwbmV) {
		fydwbm_v = fydwbmV;
	}

	public String getPk_busitem() {
		return pk_busitem;
	}

	public void setPk_busitem(String pk_busitem) {
		this.pk_busitem = pk_busitem;
	}

	@Override
	public String getPk() {
		return getPk_jkbx();
	}

	public String getJk_busitemPK() {
		return jk_busitemPK;
	}

	public void setJk_busitemPK(String jk_busitemPK) {
		this.jk_busitemPK = jk_busitemPK;
	}

	public String getBx_busitemPK() {
		return bx_busitemPK;
	}

	public void setBx_busitemPK(String bx_busitemPK) {
		this.bx_busitemPK = bx_busitemPK;
	}

	public java.lang.String getCenter_dept() {
		return center_dept;
	}

	public void setCenter_dept(java.lang.String center_dept) {
		this.center_dept = center_dept;
	}

	public String getSrcbilltype() {
		return srcbilltype;
	}

	public void setSrcbilltype(String srcbilltype) {
		this.srcbilltype = srcbilltype;
	}

	public String getSrctype() {
		return srctype;
	}

	public void setSrctype(String srctype) {
		this.srctype = srctype;
	}

	public String getPk_mtapp_detail() {
		return pk_mtapp_detail;
	}

	public void setPk_mtapp_detail(String pk_mtapp_detail) {
		this.pk_mtapp_detail = pk_mtapp_detail;
	}

	public UFBoolean getIsmashare() {
		return ismashare;
	}

	public void setIsmashare(UFBoolean ismashare) {
		this.ismashare = ismashare;
	}

	public String getPk_proline() {
		return pk_proline;
	}

	public void setPk_proline(String pkProline) {
		pk_proline = pkProline;
	}

	public String getPk_brand() {
		return pk_brand;
	}

	public void setPk_brand(String pkBrand) {
		pk_brand = pkBrand;
	}
	
	public java.lang.String getPk_billtype() {
		return pk_billtype;
	}

	public void setPk_billtype(java.lang.String pk_billtype) {
		this.pk_billtype = pk_billtype;
	}

	@Override
	public String getWorkFlowBillPk() {
		return getPk();
	}

	@Override
	public String getWorkFolwBillType() {
		return getDjlxbm();
	}
	
	public Integer getVouchertag() {
		return vouchertag;
	}

	public void setVouchertag(Integer vouchertag) {
		this.vouchertag = vouchertag;
	}
	
	public String getImag_status() {
		return imag_status;
	}

	public void setImag_status(String imag_status) {
		this.imag_status = imag_status;
	}

	public UFBoolean getIsneedimag() {
		return isneedimag;
	}

	public void setIsneedimag(UFBoolean isneedimag) {
		this.isneedimag = isneedimag;
	}

	public Integer getRed_status() {
		return red_status;
	}

	public void setRed_status(Integer red_status) {
		this.red_status = red_status;
	}

	public String getRedbillpk() {
		return redbillpk;
	}

	public void setRedbillpk(String redbillpk) {
		this.redbillpk = redbillpk;
	}

	public UFDate getTbb_period() {
		return tbb_period;
	}

	public void setTbb_period(UFDate tbbPeriod) {
		tbb_period = tbbPeriod;
	}

	public Integer getPaytarget() {
		return paytarget;
	}
	
	public void setPaytarget(Integer paytarget) {
		this.paytarget = paytarget;
	}
	
	public UFBoolean getIsexpedited() {
		return isexpedited;
	}

	public void setIsexpedited(UFBoolean isexpedited) {
		this.isexpedited = isexpedited;
	}

	public String getPk_matters() {
		return pk_matters;
	}

	public void setPk_matters(String pk_matters) {
		this.pk_matters = pk_matters;
	}

	public String getPk_campaign() {
		return pk_campaign;
	}

	public void setPk_campaign(String pk_campaign) {
		this.pk_campaign = pk_campaign;
	}

	public void combineVO(JKBXHeaderVO vo){
		if(this.pk_item == null && this.isLoadInitBill == false){
			// �������Ҳ����س��õ�����������ݵ���ģ������ó��vo�е�Ĭ��ֵ
				if(!this.getIscostshare().booleanValue()){
					setIscostshare(vo.getIscostshare());
				}
				if(!this.getIsexpamt().booleanValue()){
					setIsexpamt(vo.getIsexpamt());
				}
				if(!this.getIscusupplier().booleanValue()){
					setIscusupplier(vo.getIscusupplier());
				}
		}
		String[] attributeNames = this.getAttributeNames();
		for (String attribute : attributeNames) {
			// ��ʼֵ��δ���õ�ֵ�����ݵ���ģ��Ĭ��ֵ����
			if (this.getAttributeValue(attribute) == null) {
				Object newValue = vo.getAttributeValue(attribute);
				if (newValue != null) {
					setAttributeValue(attribute,newValue);
				}
			}
		}
	}

	public boolean isLoadInitBill() {
		return isLoadInitBill;
	}

	public void setLoadInitBill(boolean isLoadInitBill) {
		this.isLoadInitBill = isLoadInitBill;
	}

	/**
	 * ��ǰ�������Ƿ��Ƿ��õ�������
	 * 
	 * @return
	 */
	public boolean isAdjustBxd() {
		boolean isAdjust = false;
		try {
			isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(getPk_group(), getDjlxbm(),ErmDjlxConst.BXTYPE_ADJUST);
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
		return isAdjust;
	}

}