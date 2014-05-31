package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.grachro.sitemap.LoadUrls;
import com.grachro.sitemap.LoadedUrl;

import play.*;
import play.data.*;
import static play.data.Form.*;
import play.mvc.*;

import views.html.*;

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

			List<LoadedUrl> resultUrls = new LoadUrls().load(1, data.url);

			return ok(index.render(msg, f, resultUrls));
		} else {
			return badRequest(index.render("ERROR", form(InputForm.class), EMPTY_LIST));
		}
	}
}
