package org.fordes.subview.util.TransUtil;

import java.util.concurrent.Callable;



public class TranThread implements Callable
{
	private String query,from,to;
	private String APP_ID,SECURITY_KEY;
	private TranslationUtil tu=new TranslationUtil();
	public TranThread(String query, String from, String to,String id,String key) {
		this.query=query;
		this.from=from;
		this.to=to;
		APP_ID=id;
		SECURITY_KEY=key;

	}


	@Override
	public Object call() throws Exception {
		String res=new BaiduTransUtil(APP_ID,SECURITY_KEY).getTransResult(query,from,to);
		return tu.AnalyticalBaiduTransResults(res);
	}
}