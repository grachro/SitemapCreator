package controllers;

import static play.data.Form.form;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.grachro.sitemap.LoadedSite;
import com.grachro.sitemap.SiteLoader;
import com.grachro.sitemap.SitemapXml;

public class Application extends Controller {

	private static ApplicationSiteLoader appLoader = new ApplicationSiteLoader();

	public static class ApplicationSiteLoader {
		private boolean loading = false;
		private SiteLoader loader = new SiteLoader();

		public synchronized void load(int deep, String url) {
			loading = true;
			this.loader = new SiteLoader();
			this.loader.load(deep, url);

			File saveFile = getSavefile();
			this.loader.save(saveFile);
			loading = false;
		}

		public synchronized File getSavefile() {
			File tempDir = new File("temp");
			if (!tempDir.exists()) {
				tempDir.mkdirs();
			}

			return new File(tempDir, "save.tsv");
		}

		public synchronized File getSitemapFile() {
			File tempDir = new File("temp");
			if (!tempDir.exists()) {
				tempDir.mkdirs();
			}

			return new File(tempDir, "sitemap.xml");
		}

		public boolean isLoading() {
			System.out.println("##########################");
			System.out.println("##########################");
			System.out.println("isLoading():" + this.loading);
			System.out.println("##########################");
			System.out.println("##########################");
			return this.loading;
		}

		public SiteLoader getSiteLoader() {
			return this.loader;
		}
	}

	public static class InputForm {
		public String submitBtn;
		public String url;
		public int deep;
	}

	public static Result index() {
		return ok(index.render("Your new application is ready.", new Form(InputForm.class), appLoader.getSiteLoader(),appLoader.isLoading()));
	}

	public static Result load() {
		Form<InputForm> f = form(InputForm.class).bindFromRequest();
		System.out.println("submitBtn::" + f.get().submitBtn);
		if (f.hasErrors()) {
			return badRequest(index.render("ERROR", form(InputForm.class), appLoader.getSiteLoader(),appLoader.isLoading()));
		}

		if (appLoader.isLoading()) {
			return pleasWaitResutl();
		}

		final InputForm data = f.get();

		if ("load".equals(data.submitBtn)) {
			String msg = "target URL is  " + data.url;

			Runnable r = new Runnable() {
				@Override
				public void run() {
					appLoader.load(data.deep, data.url);
				};
			};
			Thread t = new Thread(r);
			t.start();
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ok(index.render(msg, f, appLoader.getSiteLoader(),appLoader.isLoading()));
		} else if ("download TSV".equals(data.submitBtn)) {
			return ok(appLoader.getSavefile());
		} else if ("download sitemap.xml".equals(data.submitBtn)) {
			File sitemapFile = appLoader.getSitemapFile();
			new SitemapXml().create(appLoader.getSiteLoader().getResult(), sitemapFile.getAbsolutePath());
			return ok(sitemapFile);
		} else {
			String msg = "";
			return ok(index.render(msg, f, appLoader.getSiteLoader(),appLoader.isLoading()));
		}

	}

	private static Result pleasWaitResutl() {
		String msg = "loading now. please wait.";
		Form<InputForm> f = form(InputForm.class).bindFromRequest();
		return ok(index.render(msg, f, appLoader.getSiteLoader(),appLoader.isLoading()));
	}

}
