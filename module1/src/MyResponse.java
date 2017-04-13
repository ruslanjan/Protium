import net.protium.api.events.Response;

/**
 * From: protium
 * Pkg: PACKAGE_NAME
 * At: 13.04.2017
 */

class MyResponse implements Response {

	@Override
	public String getContentType( ) {
		return "text/html; charset=utf-8";
	}

	@Override
	public String getResponse( ) {
		return "<h1>Hello, World!</h1>";
	}

	@Override
	public Integer getStatus( ) {
		return 200;
	}
}
