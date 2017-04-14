import net.protium.api.events.Request;
import net.protium.api.events.Response;
import net.protium.api.module.Module;


public class module1 implements Module {
	@Override
	public void onEnable( ) {

	}

	@Override
	public Response onRequest(Request request) {

		System.out.println("================");
		System.out.println("MODULE1 TEST OUTPUT");
		System.out.println("================");

		System.out.println(request.getAction());
		System.out.println(request.getURL());
		System.out.println(request.getHeaders());
		System.out.println(request.getRawData());
		System.out.println(request.getRawQueryString());
		System.out.println(request.getOptions());

		System.out.println("================");
		System.out.println("MODULE1 TEST OUTPUT END");
		System.out.println("================");

		return new MyResponse();
	}

	@Override
	public void onDisable( ) {

	}
}
