package nc.vo.erm.control;

/**
 * 此处插入类型描述。
 * 创建日期：(2004-3-12 18:39:09)
 * @author：钟悦
 */
public class TokenTools {
    private String str;
    private String delimiters;
    private boolean retTokens;
    /**
     * TokenTools 构造子注解。
     */
    public TokenTools() {
        super();
    }
    public TokenTools(String str) {
        this.str = str;
        this.delimiters = " \t\n\r\f";
        this.retTokens = false;
    }

    public TokenTools(String str, String delim) {
        this.str = str;
        this.delimiters = delim;
        this.retTokens = false;
    }
    /**
    得到指定分割符号分割指定字符串后的字符串数组   
    
    
    */
    public String[] getStringArray() {
        java.util.StringTokenizer token = getToken();
        int count = token.countTokens();
        String[] strs = new String[count];
        int i = 0;
        while (token.hasMoreTokens()) {
            strs[i] = token.nextToken();
            i++;
        }
        return strs;

    }
    public java.util.StringTokenizer getToken() {
        return new java.util.StringTokenizer(getStr(), getDelimis(), getRetTokens());
    }
    public void setStr(String s) {
        this.str = s;
    }
    public void setDelimis(String delimis) {
        this.delimiters = delimis;
    }
    public void setRetTokens(boolean b) {
        this.retTokens = b;
    }
    public String getStr() {
        return this.str;
    }
    public String getDelimis() {
        return this.delimiters;
    }
    public boolean getRetTokens() {
        return this.retTokens;
    }

    public TokenTools(String str, String delim, boolean returnTokens) {
        this.str = str;
        this.delimiters = delim;
        this.retTokens = returnTokens;
    }

public int countTokens() {
    return getToken().countTokens();
}

public boolean isOneToken() {
    return countTokens() == 1;
}
}