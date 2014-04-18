package nc.bs.erm.util;

public class UniqueSequenceCreator {

	private static int seq = 0;

	public static synchronized int getTempTableSeq() {
		return seq++;
	}

}



