package nc.pub.smart.smartprovider;

import java.sql.Types;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.accountageana.ILoanAccountAgeAnalyzeBO;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.pub.smart.metadata.Field;
import nc.pub.smart.metadata.MetaData;
import nc.smart.fipub.AbsReportDataProvider;
import nc.utils.fipub.SmartProcessor;
import nc.vo.fipub.report.ReportQueryCondVO;

/**
 * ���������������ṩ��<br>
 *
 * @author liansg<br>
 * @since V60 2010-12-14<br>
 */
public class LoanAgeAnalyzeDataProvider extends AbsReportDataProvider {
	private static final long serialVersionUID = 1L;

	/** �ֶ����� */
	private static final String[] CAPTIONS = {

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0070")/*@res "����PK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0071")/*@res "ҵ��ԪPK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0072")/*@res "ҵ��ԪCODE"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000396")/*@res "ҵ��Ԫ"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0073")/*@res "����PK"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001755")/*@res "����"*/,

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001819")/*@res "���к�"*/,

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0080")/*@res "�������ԭ��"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0081")/*@res "������䱾��"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0082")/*@res "������伯�ű���"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0083")/*@res "�������ȫ�ֱ���"*/, // �������

			nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0084")/*@res "�������ID"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0085")/*@res "�������"*/ // �����������
	};

	/** �ֶα��� */
	private static final String[] FLDNAMES = {
			"pk_group", // ��������
			"pk_org", // ҵ��Ԫ����
			"code_org", // ҵ��Ԫ����
			"org", // ҵ��Ԫ
			"pk_currtype", // ��������
			"currtype", // ����

			"vseq", // ���к�

			"accage_ori", // �������ԭ��
			"accage_loc", // ������䱾��
			"gr_accage_loc", // ������伯�ű���
			"gl_accage_loc", // �������ȫ�ֱ���

			"accageid", // �������ID
			"accage" // �������
	};

	/** �ֶ����� */
	private static final int[] DBCOLUMNTYPES = {

			Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,

			Types.INTEGER,

			Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL,

			Types.INTEGER, Types.VARCHAR
	};

	/** �ֶγ��� */
	private static final int[] PRECISIONS = {
		20, 20, 50, 50, 20, 50,

		10,

		30, 30, 30, 30,

		10, 10 };

	/** �ֶξ��� */
	private static final int[] SCALES = {
			0, 0, 0, 0, 0, 0,

			0,

			8, 8, 8, 8,

			0, 0
	};

	protected DataSet execute(Object obj, SmartContext context) throws SmartException {
		ReportQueryCondVO queryVO = obj == null ? null : (ReportQueryCondVO) obj;
		if (queryVO != null) {
			queryVO.setQueryDetail(false);
		}
		return NCLocator.getInstance().lookup(ILoanAccountAgeAnalyzeBO.class).accountAgeQuery(queryVO, context);
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

