package la.aquare.spinver.lang.dict;

public class RegexLib {
	public static final String wordRegex =
			"[\\p{L}|\\d]+[-[\\p{L}|\\d]]*";
	public static final String specialChars = "\\+";
	public static final String inAsksings =
			"("
			+ "(a(e|ê|i|í)+)|"
			+ "(a(qu|k)i)|"
			+ "((p|pa?r?a)\\s+vend(ê|er))"
			+ ")";
	public static final String notMatchForActions = //XXX: obsoleto
			".*\\b("
			+ "(pa?r?a(\\s+a?)?\\s+troc(a|ar|á))|"
			+ "troc(a|ar|á)"
			+ ")\\b.*";
}
