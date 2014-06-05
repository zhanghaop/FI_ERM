package nc.bs.erm.event;

/**
 * �¼����ͳ���
 * 
 * @author lvhj
 *
 */
public class ErmEventType {

	 public static final String TYPE_INSERT_BEFORE = "ERM1001"; //����ǰ
	 public static final String TYPE_INSERT_AFTER  = "ERM1002"; //������
	 
	 public static final String TYPE_UPDATE_BEFORE = "ERM1003"; //�޸�ǰ
	 public static final String TYPE_UPDATE_AFTER  = "ERM1004"; //�޸ĺ�
	 
	 public static final String TYPE_DELETE_BEFORE = "ERM1005"; //ɾ��ǰ
	 public static final String TYPE_DELETE_AFTER  = "ERM1006"; //ɾ����
	 
	 public static final String TYPE_USED_BEFORE = "ERM1007"; //����ǰ
	 public static final String TYPE_USED_AFTER = "ERM1008"; //���ú�
	 
	 public static final String TYPE_UNUSED_BEFORE = "ERM1009"; //������ǰ
	 public static final String TYPE_UNUSED_AFTER = "ERM1010"; //�����ú�
	 
	 public static final String TYPE_APPROVE_BEFORE = "ERM1011"; //����ǰ
	 public static final String TYPE_APPROVE_AFTER = "ERM1012"; //������
	 
	 public static final String TYPE_UNAPPROVE_BEFORE = "ERM1013"; //ȡ������ǰ
	 public static final String TYPE_UNAPPROVE_AFTER = "ERM1014"; //ȡ��������
	 
	 public static final String TYPE_COMMIT_BEFORE = "ERM1015"; //�ύǰ
	 public static final String TYPE_COMMIT_AFTER = "ERM1016"; //�ύ��
	 
	 public static final String TYPE_RECALL_BEFORE = "ERM1017"; //�ջ�ǰ
	 public static final String TYPE_RECALL_AFTER = "ERM1018"; //�ջغ�
	 
	 public static final String TYPE_CLOSE_BEFORE = "ERM1019"; //�ر�ǰ
	 public static final String TYPE_CLOSE_AFTER = "ERM1020"; //�رպ�
	 
	 public static final String TYPE_UNCLOSE_BEFORE = "ERM1021"; //ȡ���ر�ǰ
	 public static final String TYPE_UNCLOSE_AFTER = "ERM1022"; //ȡ���رպ�
	 
	 
	 public static final String TYPE_SIGN_BEFORE = "ERM1023"; //��Чǰ
	 public static final String TYPE_SIGN_AFTER = "ERM1024"; //��Ч��
	 
	 public static final String TYPE_UNSIGN_BEFORE = "ERM1025"; //ȡ����Чǰ
	 public static final String TYPE_UNSIGN_AFTER = "ERM1026"; //ȡ����Ч��
	 
	 public static final String TYPE_WRITEOFF_BEFORE = "ERM1027"; //����ǰ
	 public static final String TYPE_WRITEOFF_AFTER = "ERM1028"; //������
	 
	 public static final String TYPE_UNWRITEOFF_BEFORE = "ERM1029"; //ȡ������ǰ
	 public static final String TYPE_UNWRITEOFF_AFTER = "ERM1030"; //ȡ��������
	 
	 public static final String TYPE_TEMPSAVE_BEFORE = "ERM1031"; //�ݴ�ǰ
	 public static final String TYPE_TEMPSAVE_AFTER = "ERM1032"; //�ݴ��
	 
	 public static final String TYPE_AMORTIZE_BEFORE = "ERM1033"; //̯��ǰ
	 public static final String TYPE_AMORTIZE_AFTER = "ERM1034"; //̯����
	 
	 public static final String TYPE_CLOSEACC_BEFORE = "ERM1035"; //����ǰ
	 public static final String TYPE_CLOSEACC_AFTER = "ERM1036"; //���˺�
	 
	 public static final String TYPE_UNCLOSEACC_BEFORE = "ERM1037"; //ȡ������ǰ
	 public static final String TYPE_UNCLOSEACC_AFTER = "ERM1038"; //ȡ������ǰ
	 
	 public static final String TYPE_TEMPUPDATE_BEFORE = "ERM1039"; //�ݴ��޸�ǰ
	 public static final String TYPE_TEMPUPDATE_AFTER = "ERM1040"; //�ݴ��޸ĺ�
	 
	 public static final String TYPE_MTAPPWB_AFTER = "ERM1041";//��������뵥��Ч(write back)
	 
	 //ehp3
	 public static final String TYPE_INVALID_BEFORE = "ERM1042"; //����ǰ
	 public static final String TYPE_INVALID_AFTER = "ERM1043"; //���Ϻ�
	 
	 
	 public static final String TYPE_REDBACK_BEFORE = "ERM2001"; //���ǰ
	 public static final String TYPE_REDBACK_AFTER = "ERM2002"; //����
	 
	 public static final String TYPE_UNREDBACK_BEFORE = "ERM2003"; //ɾ�����ǰ
	 public static final String TYPE_UNREDBACK_AFTER = "ERM2004"; //ɾ������
	

}
