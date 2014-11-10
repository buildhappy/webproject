package com.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class ApkDownUtils {
	public static void downApk(String name, String url, String fileStorePath) {
		System.out.println("downlaoding...");
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			String filePath = fileStorePath;
			File f = new File(filePath);
			if (!f.exists()) {
				f.mkdirs();
			}

			HttpGet httpget = new HttpGet(url);
			HttpResponse reponse = client.execute(httpget);
			HttpEntity entity = reponse.getEntity();
			InputStream in = entity.getContent();

			FileOutputStream out = new FileOutputStream(new File(filePath
					+ name + ".apk"));
			BufferedOutputStream buffOut = new BufferedOutputStream(out);
			
			BufferedInputStream buffIn = new BufferedInputStream(in);

			byte[] buf = new byte[1024];
			int len = 0;
			int source = 0;
			while ((len = buffIn.read(buf)) != -1) {
				buffOut.write(buf, 0, len);
				//source += len;
			}
			//System.out.println("source:" + source);
			buffOut.flush();
			buffOut.close();
			buffIn.close();

		} catch (Exception e) {
			e.getStackTrace();
		}
	}
}
