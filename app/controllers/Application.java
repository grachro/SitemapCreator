package controllers;

import play.*;
import play.data.*;
import static play.data.Form.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static class InputForm {
        public String message;
    }
    
    
    public static Result index() {
        return ok(index.render("Your new application is ready.",new Form(InputForm.class)));
    	//return ok(index.render("Your new application is ready." ));
    }

    public static Result send() {
    	Form<InputForm> f = form(InputForm.class).bindFromRequest();
    	if(!f.hasErrors()){
    		
    		InputForm data = f.get();
    		String msg = "you typed: " + data.message;
    		return ok(index.render(msg,f));
    		//return ok(index.render(msg ));
    	}else{
    		return badRequest(index.render("ERROR",form(InputForm.class)));
    		//return badRequest(index.render("ERROR" ));
    	}
    }
}
