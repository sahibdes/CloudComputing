import io.undertow.Undertow;
import io.undertow.server.*;
import io.undertow.util.Headers;

public class Phase1 {
	public static void main(final String[] args) {
		Undertow server = Undertow.builder().addHttpListener(8713, "localhost")
				.setHandler(new HttpHandler() {
					@Override
					public void handleRequest(final HttpServerExchange exchange)
							throws Exception {
						exchange.getResponseHeaders().put(Headers.CONTENT_TYPE,
								"text/plain");
						exchange.getResponseSender().send("Hello World");
					}
				}).build();
		server.start();
	}
}