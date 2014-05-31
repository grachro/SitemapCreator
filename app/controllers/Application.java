package controllers;

import static play.data.Form.form;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.grachro.sitemap.LoadedUrl;
import com.grachro.sitemap.SiteLoader;

public class Application extends Controller {

	public static class InputForm {
		public String url;
	}

	public static List<LoadedUrl> EMPTY_LIST = new ArrayList<LoadedUrl>();

	public static Result index() {
		return ok(index.render("Your new application is ready.", new Form(InputForm.class), EMPTY_LIST));
	}

	public static Result send() {
		Form<InputForm> f = form(InputForm.class).bindFromRequest();
		if (!f.hasErrors()) {

			InputForm data = f.get();
			String msg = "target URL is  " + data.url;

			SiteLoader loader = new SiteLoader();
			loader.load(1, data.url);
			save(loader);
			List<LoadedUrl> resultUrls = loader.getResult();

			return ok(index.render(msg, f, resultUrls));
		} else {
			return badRequest(index.render("ERROR", form(InputForm.class), EMPTY_LIST));
		}
	}

	private static void save(SiteLoader loader) {
		File tempDir = new File("temp");
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}

		File saveFile = new File(tempDir, "save.tsv");
		loader.save(saveFile);
	}
}
