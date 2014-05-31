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

		public boolean isLoading() {
			System.out.println("##########################");
			System.out.println("##########################");
			System.out.println("isLoading():" + this.loading);
			System.out.println("##########################");
			System.out.println("##########################");
			return this.loading;
		}
	}

	public static class InputForm {
		public String url;
		public int deep;
	}

	public static List<LoadedSite> EMPTY_LIST = new ArrayList<LoadedSite>();

	public static Result index() {
		return ok(index.render("Your new application is ready.", new Form(InputForm.class), EMPTY_LIST));
	}

	public static Result load() {
		Form<InputForm> f = form(InputForm.class).bindFromRequest();
		if (!f.hasErrors()) {
			if (appLoader.isLoading()) {
				return pleasWaitResutl();
			}

			final InputForm data = f.get();
			String msg = "target URL is  " + data.url;

			Runnable r = new Runnable() {
				@Override
				public void run() {
					appLoader.load(data.deep, data.url);
				};
			};
			Thread t = new Thread(r);
			t.start();

			return ok(index.render(msg, f, EMPTY_LIST));
		} else {
			return badRequest(index.render("ERROR", form(InputForm.class), EMPTY_LIST));
		}
	}

	public static Result downloadTsv() {
		if (appLoader.isLoading()) {
			return pleasWaitResutl();
		}
		return ok(appLoader.getSavefile());

	}

	public static Result downloadSitemap() {
		if (appLoader.isLoading()) {
			return pleasWaitResutl();
		}
		return ok(appLoader.getSavefile());

	}

	private static Result pleasWaitResutl() {
		String msg = "loading now. please wait.";
		Form<InputForm> f = form(InputForm.class).bindFromRequest();
		return ok(index.render(msg, f, EMPTY_LIST));
	}

}
