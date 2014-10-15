package nc.pub.smart.smartprovider;

import java.sql.Types;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.pub.ILoanBalanceBO;
import nc.itf.fipub.report.IPubReportConstants;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.metadata.Field;
import nc.pub.smart.metadata.MetaData;
import nc.smart.fipub.AbsReportDataProvider;
import nc.utils.fipub.SmartProcessor;
import nc.vo.fipub.report.ReportQueryCondVO;


/**
 * <p>
 * TODO �����������������������ṩ�ߡ�
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br><br>
 *
 * @see
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-11-22 ����09:42:13
 */
public class LoanBalanceDataProvider extends AbsReportDataProvider{

	private static final long serialVersionUID = 1L;

	/** �ֶ����� */
	private static final String[] CAPTIONS = {

	 nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0070")/*@res "����PK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0071")/*@res "ҵ��ԪPK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0072")/*@res "ҵ��ԪCODE"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000396")/*@res "ҵ��Ԫ"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0073")/*@res "����PK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001755")/*@res "����"*/,

	 nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0086")/*@res "�ڳ�ԭ�����"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0087")/*@res "�ڳ��������"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0088")/*@res "�ڳ����ű������"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0089")/*@res "�ڳ�ȫ�ֱ������"*/,

	 nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0090")/*@res "���ڽ��ԭ��"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0091")/*@res "���ڽ���"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0092")/*@res "���ڽ��ű���"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0093")/*@res "���ڽ��ȫ�ֱ���"*/,

	 nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0094")/*@res "���ڻ���ԭ��"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0095")/*@res "���ڻ����"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0096")/*@res "���ڻ���ű���"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0097")/*@res "���ڻ���ȫ�ֱ���"*/,

	 nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0098")/*@res "������"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0099")/*@res "�������"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0100")/*@res "���ű������"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0101")/*@res "���ȫ�ֱ������"*/,
	 
	 nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0112")/*@res "���"*/

	};

	/** �ֶα��� */
	private static final String[] FLDNAMES = {
			"pk_group", // ��������
			"pk_org", // ҵ��Ԫ����
			"code_org", // ҵ��Ԫ����
			"org", // ҵ��Ԫ
			"pk_currtype", // ��������
			"currtype", // ����

			"init_ori", // �ڳ�ԭ��
			"init_loc", // �ڳ�����
			"gr_init_loc", // �ڳ����ű���
			"gl_init_loc", // �ڳ����ű���

			"jk_ori", // ���ڽ��ԭ��
			"jk_loc", // ���ڽ���
			"gr_jk_loc", // ���ڽ��ű���
			"gl_jk_loc",// ���ڽ��ȫ�ֱ���

			"hk_ori", // ���ڻ���ԭ��
			"hk_loc", // ���ڻ����
			"gr_hk_loc", // ���ڻ���ű���"
			"gl_hk_loc", // ���ڻ���ȫ�ֱ���"

			"bal_ori", // ������ԭ��
			"bal_loc", // �������
			"gr_bal_loc", // ���ű������
			"gl_bal_loc", // ���ȫ�ֱ������
			IPubReportConstants.ORDER_MANAGE_VSEQ // ���

	};

	/** �ֶ����� */
	private static final int[] DBCOLUMNTYPES = {
			Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,

			Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL,

			Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL,

			Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL,

			Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL,
			
			Types.INTEGER

	};

	/** �ֶγ��� */
	private static final int[] PRECISIONS = {

	20, 20, 50, 50, 20, 50,

	30, 30, 30, 30,

	30, 30, 30, 30,

	30, 30, 30, 30,

	30, 30, 30, 30,
	
	20

	};

	/** �ֶξ��� */
	private static final int[] SCALES = {

	0, 0, 0, 0, 0, 0,

	8, 8, 8, 8,

	8, 8, 8, 8,

	8, 8, 8, 8,

	8, 8, 8, 8,
	
	0

	};



	protected DataSet execute(Object obj, SmartContext context) throws SmartException {
		ReportQueryCondVO queryVO = obj == null ? null : (ReportQueryCondVO) obj;
		return NCLocator.getInstance().lookup(ILoanBalanceBO.class).queryLoanBalance(queryVO, context);
	}

	public MetaData provideMetaData(SmartContext context) throws SmartException {
		MetaData metaData = new MetaData();

		// ���ö�̬��ѯ����
		metaData.addField(SmartProcessor.getSmartQueryObj().toArray(new Field[0]));

		// ����������ѯ�ֶ�
		Field fld = null;
		for (int i = 0; i < FLDNAMES.length; i++) {
			fld = new Field();
			fld.setCaption(CAPTIONS[i]);
			fld.setFldname(FLDNAMES[i]);
			fld.setDbColumnType(DBCOLUMNTYPES[i]);
			fld.setPrecision(PRECISIONS[i]);
			fld.setScale(SCALES[i]);
			metaData.addField(fld);
		}
		return metaData;
	}

}