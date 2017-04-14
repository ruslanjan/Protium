import net.protium.api.events.Request;
import net.protium.api.events.Response;
import net.protium.api.module.Module;


public class module1 implements Module {
	@Override
	public void onEnable( ) {

	}

	@Override
	public Response onRequest(Request request) {

		String answer = request.getAction() + "\n<br>" +
			request.getURL() + "\n<br>" +
			request.getHeaders() + "\n<br>" +
			request.getRawData() + "\n<br>" +
			request.getRawQueryString() + "\n<br>" +
			request.getOptions() + "\n<br>";


		MyResponse resp = new MyResponse();

		resp.setStatus();
		resp.setContentType();

		return resp;
	}

	@Override
	public void onDisable( ) {

	}
}
