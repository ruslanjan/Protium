import net.protium.api.events.Response;

/**
 * From: protium
 * Pkg: PACKAGE_NAME
 * At: 13.04.2017
 */

class MyResponse implements Response {

	private String response;
	private int status;
	private String contentType;

	@Override
	public String getResponse( ) {
		return response;
	}

	void setResponse(String response) {
		this.response = response;
	}

	@Override
	public Integer getStatus( ) {
		return status;
	}

	void setStatus( ) {
		this.status = 200;
	}

	@Override
	public String getContentType( ) {
		return contentType;
	}

	void setContentType( ) {
		this.contentType = "text/html; charset=utf-8";
	}
}
